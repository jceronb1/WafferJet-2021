package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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