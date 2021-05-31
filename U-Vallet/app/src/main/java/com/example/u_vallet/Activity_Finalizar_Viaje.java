package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_Finalizar_Viaje extends AppCompatActivity {

    private Button finalizarViaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar__viaje);

        finalizarViaje = (Button)findViewById(R.id.buttonFinalizarViaje);

        finalizarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(v.getContext(), Activity_Roles.class);
                startActivity(home);
            }
        });

    }
}