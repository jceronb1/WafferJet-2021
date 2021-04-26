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

public class Activity_Roles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roles);

        Button botonConductor = (Button) findViewById(R.id.Roles_BotonConductor);
        Button botonPasajero = (Button) findViewById(R.id.Roles_BotonPasajero);

        botonConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentConductor = new Intent (v.getContext(), Activity_MisCarros.class);
                startActivity(intentConductor);
            }
        });

        botonPasajero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPasajero = new Intent (v.getContext(), Activity_ExplorarViajes.class);
                startActivity(intentPasajero);
            }
        });
    }
}