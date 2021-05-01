package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Activity_Registrarse extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static final String PATH_USERS = "users/";

    private EditText mUser;
    private EditText mUserName;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mTelefono;
    private EditText mDireccion;

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

        Button buttonRegistrarse = (Button) findViewById(R.id.buttoRegistrar);
        //Button buttonGaleria =(Button) findViewById(R.id.buttonSeleccionarFoto);

        mUserName = (EditText)findViewById(R.id.nombreUsuarioText);
        mUser = (EditText)findViewById(R.id.nombreCompletoText);
        mPassword = (EditText)findViewById(R.id.contrasenaText);
        mConfirmPassword = (EditText)findViewById(R.id.confirmarContrasenaText);
        //Fecha de Nacimiento
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
                    Toast.makeText(Activity_Registrarse.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
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
                    Toast.makeText(Activity_Registrarse.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
                    takeImage();
                }else{
                    requestCamaraPermission();
                }
            }
        });

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
    private boolean validateForm(){
        boolean valid = true;
        String user = mUserName.getText().toString();
        String nombre = mUser.getText().toString();
        String contraseña = mPassword.getText().toString();
        String confirmarContraseña = mConfirmPassword.getText().toString();
        String telefono = mTelefono.getText().toString();
        String direccion = mDireccion.getText().toString();
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
    private void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            currentUser = mAuth.getCurrentUser();
            if(validateForm()) {
                Usuario usuario = new Usuario();
                usuario.setUsername(mUserName.getText().toString());
                usuario.setName(mUser.getText().toString());
                usuario.setContraseña(mPassword.getText().toString());
                //usuario.setFecha()
                usuario.setTelefono(Double.parseDouble(mTelefono.getText().toString()));
                usuario.setDireccion(mDireccion.getText().toString());

                myRef = database.getReference(PATH_USERS + currentUser.getUid());
                String key = myRef.push().getKey();
                myRef = database.getReference(PATH_USERS + key);
                myRef.setValue(usuario);
            }
            Intent intent = new Intent(getBaseContext(), Activity_Roles.class);
            intent.putExtra("user", currentUser.getEmail());
            loadUsersSuscripcion();
            startActivity(intent);
        }else{
            mUserName.setText("");
            mUser.setText("");
            mPassword.setText("");
            mConfirmPassword.setText("");
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
                                updateUI(user);
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
                    Toast.makeText(getBaseContext(), name + " /" + contraseña, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Suscripcion Usuarios", "Error en la consulta", databaseError.toException());
            }
        });
    }
}