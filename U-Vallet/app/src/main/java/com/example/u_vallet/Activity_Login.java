package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class Activity_Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

<<<<<<< HEAD
=======
        Button botonRegistrarse = (Button) findViewById(R.id.registrar);
        botonRegistrarse.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegistrar = new Intent(v.getContext(), Activity_Registrarse.class);
                startActivity(intentRegistrar);
            }
        }));
>>>>>>> 0f2e2d4dce71f08a363824df55ee7e791f43b078

    }
}