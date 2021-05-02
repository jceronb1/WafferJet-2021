package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_Mi_Viaje_Pasajero extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__mi__viaje__pasajero);

        Button ExplorarViaje = (Button) findViewById(R.id.buttonMiViajeMV4);

        ExplorarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCrearViaje = new Intent(v.getContext(), Activity_ExplorarViajes.class);
                startActivity(intentCrearViaje);
            }
        });
    }
}