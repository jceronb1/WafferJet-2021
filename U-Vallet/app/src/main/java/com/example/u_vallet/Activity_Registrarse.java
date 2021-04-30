package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Activity_Registrarse extends AppCompatActivity {
    private FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();

        Button buttonRegistrarse = (Button) findViewById(R.id.buttoRegistrar);
        //Button buttonGaleria =(Button) findViewById(R.id.buttonSeleccionarFoto);

        mUser = (EditText)findViewById(R.id.nombreUsuarioText);
        mUserName = (EditText)findViewById(R.id.nombreCompletoText);
        mPassword = (EditText)findViewById(R.id.contrasenaText);
        mConfirmPassword = (EditText)findViewById(R.id.confirmarContrasenaText);
        mTelefono = (EditText)findViewById(R.id.telefonoText);
        mDireccion = (EditText)findViewById(R.id.direccionText);

        buttonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(mUser.getText().toString(), mPassword.getText().toString());
            }
        });
    }

    private void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            Intent intent = new Intent(getBaseContext(), Activity_Roles.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        }else{
            mUser.setText("");
            mPassword.setText("");
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
                            }
                        }
                    }
                });
    }
}