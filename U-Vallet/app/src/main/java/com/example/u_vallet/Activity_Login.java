package com.example.u_vallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Activity_Login extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText mUser;
    private  EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button botonRegistrarse = (Button) findViewById(R.id.registrar);
        Button botonIniciarSesion = (Button) findViewById(R.id.iniciar_sesion);

        mUser = (EditText)findViewById(R.id.nombreUsuarioText);
        mPassword = (EditText)findViewById(R.id.contraseñaText);

        botonRegistrarse.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegistrar = new Intent(v.getContext(), Activity_Registrarse.class);
                startActivity(intentRegistrar);
            }
        }));

        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInUser(mUser.getText().toString(), mPassword.getText().toString());
                //Intent intentIniciarSesion = new Intent (v.getContext(), Activity_Roles.class);
                //startActivity(intentIniciarSesion);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            Intent intent = new Intent(getBaseContext(), Activity_Roles.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            mUser.setText("");
            mPassword.setText("");
        }
    }

    private boolean validateForm(){
        boolean valid = true;
        String email = mUser.getText().toString();
        if(TextUtils.isEmpty(email)){
            mUser.setError("Required.");
            valid = false;
        } else if(!isEmailValid(email)){
            mUser.setError("Ingrese un correo válido");
            valid = false;
        }else {
            mUser.setError(null);
        }
        String password = mPassword.getText().toString();
        if(TextUtils.isEmpty(password)){
            mPassword.setError("Required.");
            valid = false;
        }else{
            mPassword.setError(null);
        }
        return valid;
    }

    private boolean isEmailValid(String email){
        if(!email.contains("@") || !email.contains(".") || email.length() < 5)
            return false;
        return true;
    }

    private void logInUser(String email, String password){
        if(validateForm()){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d("AUTH", "logInWithEmail:success"+ task.isSuccessful());
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }
                            if(!task.isSuccessful()) {
                                Log.w("AUTH", "logInWithEmail:failed", task.getException());
                                Toast.makeText(Activity_Login.this, "Inicio de sesión fallido.",
                                        Toast.LENGTH_SHORT);
                                updateUI(null);
                            }
                        }
                    });
        }
    }
}