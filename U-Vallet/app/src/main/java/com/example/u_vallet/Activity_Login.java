package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class Activity_Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button botonRegistrarse = (Button) findViewById(R.id.registrar);
        Button botonIniciarSesion = (Button) findViewById(R.id.iniciar_sesion);

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
                Intent intentIniciarSesion = new Intent (v.getContext(), Activity_Navegation.class);
                startActivity(intentIniciarSesion);
            }
        });

    }


}