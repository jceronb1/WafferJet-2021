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
import android.content.Context;
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
import android.text.TextUtils;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Activity_EditarPerfil extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private FirebaseUser currentUser;
    public static final String PATH_USERS = "users/";

    private EditText mNombreUsuario;
    private EditText mNombreCompleto;
    private EditText mContraseñaAntigua;
    private EditText mContraseñaNueva;
    private TextView mFechaNacimiento;
    private EditText mTelefono;
    private EditText mDireccion;
    private String uid;
    private ImageView mImageView;
    private Button mPassword;
    private Uri imagenUri;

    private boolean imageInclude = false;

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
        currentUser = mAuth.getCurrentUser();

        mNombreUsuario = (EditText) findViewById(R.id.nombreUsuarioText);
        mNombreCompleto = (EditText) findViewById(R.id.nombreCompletoText);
        mContraseñaAntigua = (EditText) findViewById(R.id.contrasenaAntiguaText);
        mContraseñaNueva = (EditText) findViewById(R.id.contrasenaNuevaText);
        mFechaNacimiento = (TextView) findViewById(R.id.fechaNacimientoPick);
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
                try {
                    updateUserInfo(currentUser);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

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

    private void guardarcontraseña(){
        Log.d("hello","vamos");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        String nombreusuario = mNombreUsuario.getHint().toString();
        String contra =mContraseñaAntigua.getText().toString();
        String nueva = mContraseñaNueva.getText().toString();
        Log.d("hello",nombreusuario + "/"+ contra);
        AuthCredential credential = EmailAuthProvider.getCredential(nombreusuario,contra);
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(nueva).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("hello", "Password updated");
                            } else {
                                Log.d("hello", "Error password not updated");
                            }
                        }
                    });
                } else {
                    Log.d("hello", "Error auth failed");
                }
            }
        });
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
                    Date fecha = usuario.getFechaNacimiento();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String sFecha = null;
                    Log.d("FECHAUS", String.valueOf(fecha));
                    if(fecha != null) {
                        sFecha = dateFormat.format(fecha);
                        Log.d("FECHAUS", sFecha+"/"+String.valueOf(fecha));
                    }

                    String telefono = String.valueOf(usuario.getTelefono());
                    String direccion = usuario.getDireccion();
                    String key = usuario.getUid();

                    Log.d("LOADUSER", email +"/" + mAuth.getCurrentUser().getEmail());
                    if(email.equals(mAuth.getCurrentUser().getEmail())) {

                        uid = key;
                        Log.d("UID_EP", key);
                        mNombreUsuario.setHint(email);
                        mNombreCompleto.setText(nombreC);
                        mContraseñaAntigua.setText(contraseñaA);
                        mFechaNacimiento.setText(sFecha);
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
    public Uri getImageUri(Context inContext, Bitmap inImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void uploadImageToFirebase(Uri imagenUri){
        StorageReference fileRef = mStorageRef.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(mImageView);
                        Toast.makeText(Activity_EditarPerfil.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Activity_EditarPerfil.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm(){
        boolean valid = true;
        String nombre = mNombreCompleto.getText().toString();
        String contraseña = mContraseñaAntigua.getText().toString();
        String confirmarContraseña = mContraseñaNueva.getText().toString();
        String fechaNacimiento = mFechaNacimiento.getText().toString();
        String telefono = mTelefono.getText().toString();
        String direccion = mDireccion.getText().toString();
        if(imageInclude == false){
            Toast.makeText(getBaseContext(), "Debe seleccionar una foto de perfil", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(TextUtils.isEmpty(nombre)){
            mNombreCompleto.setError("Required");
            valid = false;
        }else{
            mNombreCompleto.setError(null);
        }
        if(TextUtils.isEmpty(contraseña)){
            mContraseñaAntigua.setError("Required");
            valid = false;
        }else{
            mContraseñaAntigua.setError(null);
        }
        /*if(TextUtils.isEmpty(confirmarContraseña)){
            mConfirmPassword.setError("Required");
            valid = false;
        }else{
            mConfirmPassword.setError(null);
        }*/
        if(TextUtils.isEmpty(fechaNacimiento)){
            mFechaNacimiento.setError("Required");
            valid = false;
        }else{
            mFechaNacimiento.setError(null);
        }
        if(TextUtils.isEmpty(telefono)){
            mTelefono.setError("Required");
            valid = false;
        }else{
            mTelefono.setError(null);
        }
        if(TextUtils.isEmpty(direccion)){
            mDireccion.setError("Required");
            valid = false;
        }else{
            mDireccion.setError(null);
        }
        return valid;
    }

    private void overWriteUserInfo(FirebaseUser currentUser,String uid, String userName, String name, String contraseña, Date fechaNacimiento, long telefono, String direccion){
        currentUser = mAuth.getCurrentUser();
        String key = uid;
        Log.d("UID_OW",key+"/"+uid);
        myRef = database.getReference("users/");
        Usuario usuario = new Usuario (uid, userName, name, contraseña, fechaNacimiento, telefono, direccion);
        Map<String, Object> valoresUsuario = usuario.toMap();
        Map<String, Object> actualizacionHijos = new HashMap<>();
        actualizacionHijos.put(key,valoresUsuario);
        myRef.updateChildren(actualizacionHijos);
    }

    private void updateUserInfo(FirebaseUser currentUser) throws ParseException {
        Log.d("UID_GET",uid);
        if(validateForm()){
            uploadImageToFirebase(imagenUri);
            Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(mFechaNacimiento.getText().toString());
            if(!mContraseñaNueva.getText().toString().isEmpty()){
                overWriteUserInfo(currentUser,uid,mNombreUsuario.getHint().toString(),mNombreCompleto.getText().toString(),mContraseñaNueva.getText().toString(),
                        fecha,Long.parseLong(mTelefono.getText().toString()),mDireccion.getText().toString());
                Log.d("hello", "entramos");
                guardarcontraseña();
            }else {
                overWriteUserInfo(currentUser, uid, mNombreUsuario.getHint().toString(), mNombreCompleto.getText().toString(), mContraseñaAntigua.getText().toString(),
                        fecha, Long.parseLong(mTelefono.getText().toString()), mDireccion.getText().toString());
            }
            /*myRef = database.getReference(PATH_USERS + currentUser.getUid());
            String key = myRef.push().getKey();
            myRef = database.getReference(PATH_USERS + key);
            myRef.valu
            myRef.setValue(usuario);*/
        }
        Intent intent = new Intent(getBaseContext(), Activity_Roles.class);
        intent.putExtra("user", currentUser.getEmail());
        startActivity(intent);
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
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        mImageView.setImageBitmap(selectedImage);
                        imagenUri = imageUri;
                        imageInclude = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            case 2: {
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    final Uri imageUri = getImageUri(getApplicationContext(),imageBitmap);
                    mImageView.setImageBitmap(imageBitmap);
                    imagenUri = imageUri;
                    imageInclude = true;
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
        }else if(itemClicked == R.id.menuLogOut){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent( this, Activity_Login.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}