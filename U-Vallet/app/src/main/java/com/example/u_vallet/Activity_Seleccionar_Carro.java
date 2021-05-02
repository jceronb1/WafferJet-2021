package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_Seleccionar_Carro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__seleccionar__carro);

        Button botonContinuar = (Button)findViewById(R.id.botonContinuarCrearViaje);
        botonContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Activity_CrearViaje.class);
                startActivity(intent);
            }
        });
    }
}