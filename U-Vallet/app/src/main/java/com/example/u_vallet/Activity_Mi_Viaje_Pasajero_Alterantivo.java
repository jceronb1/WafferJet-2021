package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_Mi_Viaje_Pasajero_Alterantivo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__mi__viaje__pasajero__alterantivo);
        Button botonCrearViaje = (Button) findViewById(R.id.buttonCrearViajeMC2);
        Button botonMisCarros = (Button) findViewById(R.id.buttonMisCarrosMV3);

        botonCrearViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCrearViaje = new Intent(v.getContext(), Activity_CrearViaje_Maps.class);
                startActivity(intentCrearViaje);
            }
        });

        botonMisCarros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMisCarros = new Intent(v.getContext(), Activity_MisCarros.class);
                startActivity(intentMisCarros);
            }
        });
    }
}