package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Calendar;
import java.util.Date;

public class Activity_Registrarse extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser mFUser;
    private FirebaseFirestore mFstore;
    private StorageReference mSotorageRef;

    public static final String PATH_USERS = "users/";

    private EditText mUser;
    private EditText mUserName;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mTelefono;
    private EditText mDireccion;
    private TextView mFechaNacimiento;
    private ImageView mImageView;
    private DatePickerDialog.OnDateSetListener mFechaSetListener;
    private Uri imagenUri;

    private boolean imageInclude = false;

    int IMAGE_PICKER_REQUEST = 1;
    int REQUEST_IMAGE_CAPTURE = 2;

    private int STORAGE_PERMISSION_CODE = 1;
    private int CAMERA_PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mSotorageRef = FirebaseStorage.getInstance().getReference();

        Button buttonRegistrarse = (Button) findViewById(R.id.buttoRegistrar);

        mImageView = (ImageView)findViewById(R.id.imagenFotoPerfil);
        mUserName = (EditText)findViewById(R.id.nombreUsuarioText);
        mUser = (EditText)findViewById(R.id.nombreCompletoText);
        mPassword = (EditText)findViewById(R.id.contrasenaText);
        mConfirmPassword = (EditText)findViewById(R.id.confirmarContrasenaText);
        mFechaNacimiento = (TextView)findViewById(R.id.fechaNacimientoPick);
        mTelefono = (EditText)findViewById(R.id.telefonoText);
        mDireccion = (EditText)findViewById(R.id.direccionText);

        buttonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    signInUser(mUserName.getText().toString(), mPassword.getText().toString());
                }
            }
        });

        Button botonCambiarImagen = (Button) findViewById(R.id.botonCambiarImagen2);
        botonCambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission( Activity_Registrarse.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(Activity_Registrarse.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
                    Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
                }else{
                    requestStoragePermission();
                }

            }
        });
        Button botonTomarFoto = (Button) findViewById(R.id.botonTomarFoto2);
        botonTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Activity_Registrarse.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    //Toast.makeText(Activity_Registrarse.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
                    takeImage();
                }else{
                    requestCamaraPermission();
                }
            }
        });
        mFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int año = cal.get(Calendar.YEAR);
                int mes = cal.get(Calendar.MONTH);
                int dia = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        Activity_Registrarse.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mFechaSetListener,
                        año,mes,dia);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mFechaSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int año, int mes, int diaDelMes) {
                String sDia, sMes;
                mes = mes +1;
                Log.d("DATE_PICK", "OnDateSet: fecha: "+diaDelMes+"/"+mes+"/"+año);
                if(diaDelMes < 10)
                    sDia = "0"+diaDelMes;
                else
                    sDia = String.valueOf(diaDelMes);
                if(mes < 10)
                    sMes = "0"+mes;
                else
                    sMes = String.valueOf(mes);
                String fecha = sDia + "/" + sMes + "/" + año;
                mFechaNacimiento.setText(fecha);
            }
        };
        Button Cancelar = (Button)findViewById(R.id.cancelar2);

        Cancelar.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin = new Intent(v.getContext(), Activity_Login.class);
                startActivity(intentLogin);
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
                            ActivityCompat.requestPermissions(Activity_Registrarse.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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
                            ActivityCompat.requestPermissions(Activity_Registrarse.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                        //mImageView.setImageURI(imageUri);
                        imageInclude = true;
                        imagenUri = imageUri;

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
                    imageInclude = true;
                    mImageView.setImageBitmap(imageBitmap);
                    imagenUri = imageUri;
                }
            }
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void uploadImageToFirebase(Uri imageUri){
        StorageReference fileRef = mSotorageRef.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(mImageView);
                        //Toast.makeText(Activity_Registrarse.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Activity_Registrarse.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm(){
        boolean valid = true;
        String user = mUserName.getText().toString();
        String nombre = mUser.getText().toString();
        String contraseña = mPassword.getText().toString();
        String confirmarContraseña = mConfirmPassword.getText().toString();
        String fechaNacimiento = mFechaNacimiento.getText().toString();
        String telefono = mTelefono.getText().toString();
        String direccion = mDireccion.getText().toString();
        if(imageInclude == false){
            Toast.makeText(getBaseContext(), "Debe seleccionar una foto de perfil", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(TextUtils.isEmpty(user)){
            mUserName.setError("Required");
            valid = false;
        }else{
            mUserName.setError(null);
        }
        if(TextUtils.isEmpty(nombre)){
            mUser.setError("Required");
            valid = false;
        }else{
            mUser.setError(null);
        }
        if(TextUtils.isEmpty(contraseña)){
            mPassword.setError("Required");
            valid = false;
        }else{
            mPassword.setError(null);
        }
        if(TextUtils.isEmpty(confirmarContraseña)){
            mConfirmPassword.setError("Required");
            valid = false;
        }else{
            mConfirmPassword.setError(null);
        }
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

    private void updateUI(FirebaseUser currentUser) throws ParseException {
        if(currentUser != null){
            currentUser = mAuth.getCurrentUser();

            if(validateForm()) {
                uploadImageToFirebase(imagenUri);
                Usuario usuario = new Usuario();

                usuario.setUsername(mUserName.getText().toString());
                usuario.setName(mUser.getText().toString());
                usuario.setContraseña(mPassword.getText().toString());
                Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(mFechaNacimiento.getText().toString());
                usuario.setFechaNacimiento(fecha);
                usuario.setTelefono(Long.parseLong(mTelefono.getText().toString()));
                usuario.setDireccion(mDireccion.getText().toString());

                myRef = database.getReference(PATH_USERS + currentUser.getUid());
                usuario.setUid(currentUser.getUid());
                //myRef = database.getReference(PATH_USERS + key);
                myRef.setValue(usuario);
            }
            Intent intent = new Intent(getBaseContext(), Activity_Roles.class);
            Toast.makeText(getBaseContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
            intent.putExtra("user", currentUser.getEmail());
            loadUsersSuscripcion();
            startActivity(intent);
        }else{
            mUserName.setText("");
            mUser.setText("");
            mPassword.setText("");
            mConfirmPassword.setText("");
            mFechaNacimiento.setText("");
            mTelefono.setText("");
            mDireccion.setText("");
        }
    }

    private void signInUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("AUTH", "createUserWithEmail:onComplete"+task.isSuccessful());
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null){
                                UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                                upcrb.setDisplayName(mUserName.getText().toString());
                                user.updateProfile(upcrb.build());
                                try {
                                    updateUI(user);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if(!task.isSuccessful()){
                            Toast.makeText(Activity_Registrarse.this, "Falló la autenticación" + task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e("", task.getException().getMessage());
                        }
                    }
                });
    }

    public void loadUsersSuscripcion(){
        myRef = database.getReference(PATH_USERS);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnahpshot : dataSnapshot.getChildren()) {
                    Usuario usuario = singleSnahpshot.getValue(Usuario.class);

                    Log.i("Suscripcion Usuarios", "Encontró usuario: " + usuario.getName());
                    String name = usuario.getName();
                    String contraseña = usuario.getContraseña();
                    //Toast.makeText(getBaseContext(), name + " /" + contraseña, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Suscripcion Usuarios", "Error en la consulta", databaseError.toException());
            }
        });
    }
}