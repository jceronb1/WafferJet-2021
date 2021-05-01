package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Activity_EditarPerfil extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    public static final String PATH_USERS = "users/";

    private EditText mNombreUsuario;
    private EditText mNombreCompleto;
    private EditText mContraseñaAntigua;
    private TextView mFechaNacimeiento;
    private EditText mTelefono;
    private EditText mDireccion;
    private ImageView mImageView;

    int IMAGE_PICKER_REQUEST = 1;
    int REQUEST_IMAGE_CAPTURE = 2;

    private int STORAGE_PERMISSION_CODE = 1;
    private int CAMERA_PERMISSION_CODE = 2;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mNombreUsuario = (EditText) findViewById(R.id.nombreUsuarioText);
        mNombreCompleto = (EditText) findViewById(R.id.nombreCompletoText);
        mContraseñaAntigua = (EditText) findViewById(R.id.contrasenaAntiguaText);
        mFechaNacimeiento = (TextView) findViewById(R.id.fechaNacimientoPick);
        mTelefono = (EditText) findViewById(R.id.telefonoText);
        mDireccion = (EditText) findViewById(R.id.direccionText);
        mImageView = (ImageView) findViewById(R.id.imagenFotoPerfil);

        StorageReference profileRef = mStorageRef.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mImageView);
            }
        });

        loadUserInfo();
        Button botonCambiarImagen = (Button) findViewById(R.id.botonCambiarImagen);
        botonCambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission( Activity_EditarPerfil.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Activity_EditarPerfil.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
                }else{
                    requestStoragePermission();
                }

            }
        });

        Button botonTomarFoto = (Button) findViewById(R.id.botonTomarFoto);
        botonTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Activity_EditarPerfil.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Activity_EditarPerfil.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
                    takeImage();
                }else{
                    requestCamaraPermission();
                }
            }
        });
        mDisplayDate = (TextView) findViewById(R.id.fechaNacimientoPick);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        Activity_EditarPerfil.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                String sDay;
                String sMonth;
                month = month + 1;
                Log.d("DATE_PICK", "onDateSet: date: "+dayOfMonth+"/"+month+"/"+year);
                if(dayOfMonth < 10)
                    sDay = "0"+ dayOfMonth;
                else
                    sDay = String.valueOf(dayOfMonth);

                if(month < 10)
                    sMonth = "0" + month;
                else
                    sMonth = String.valueOf(month);
                String date = sDay + "/" + sMonth + "/"+ year;
                mDisplayDate.setText(date);
            }
        };
        Button Guardar = (Button)findViewById(R.id.botonGuardar);
        Button Cancelar = (Button)findViewById(R.id.cancelar);
        Guardar.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRoles = new Intent(v.getContext(), Activity_Roles.class);
                startActivity(intentRoles);
            }
        }));
        Cancelar.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRoles = new Intent(v.getContext(), Activity_Roles.class);
                startActivity(intentRoles);
            }
        }));

    }

    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Este permiso es requerido para el acceso a su galeria")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Activity_EditarPerfil.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }
    private void requestCamaraPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                .setTitle("Permiso requerido")
                .setTitle("Este permiso es requerido para el acceso a su camarar")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(Activity_EditarPerfil.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
        } else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    public void loadUserInfo(){
        myRef = database.getReference(PATH_USERS);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Usuario usuario = singleSnapshot.getValue(Usuario.class);
                    String email = usuario.getUsername();
                    String nombreC = usuario.getName();
                    String contraseñaA = usuario.getContraseña();
                    //String fecha = usuario.getFechaNacimiento().toString();
                    String telefono = String.valueOf(usuario.getTelefono());
                    String direccion = usuario.getDireccion();

                    Log.d("LOADUSER", email +"/" + mAuth.getCurrentUser().getEmail());
                    if(email.equals(mAuth.getCurrentUser().getEmail())) {

                        mNombreUsuario.setText(email);
                        mNombreCompleto.setText(nombreC);
                        mContraseñaAntigua.setText(contraseñaA);
                        //mFechaNacimeiento.setText(fecha);
                        mTelefono.setText(telefono);
                        mDireccion.setText(direccion);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("LOADUSER", "Error en la consulta", databaseError.toException());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso Concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void takeImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView image = (ImageView) findViewById(R.id.imagenFotoPerfil);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        image.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            case 2: {
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    image.setImageBitmap(imageBitmap);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity__navegation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemClicked = item.getItemId();
        if(itemClicked == R.id.menuCambiarRol){
            Intent intent = new Intent(this, Activity_Roles.class);
            startActivity(intent);
        }else if (itemClicked == R.id.menuEditarPerfil){
            Intent intent = new Intent( this, Activity_EditarPerfil.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}