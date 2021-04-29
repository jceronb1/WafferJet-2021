package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Activity_Mi_Viaje_Conductor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_viaje_conductor);

        Button botonCrearViaje = (Button) findViewById(R.id.buttonCrearViajeMC);
        Button botonMisCarros = (Button) findViewById(R.id.buttonMisCarrosMV);

        botonCrearViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCrearViaje = new Intent(v.getContext(), Activity_CrearViaje.class);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity__navegation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemClicked = item.getItemId();
        if(itemClicked == R.id.menuCambiarRol){
            Intent intent = new Intent(this, Activity_Roles.class);
            startActivity(intent);
        }else if (itemClicked == R.id.menuEditarPerfil){
            Intent intent = new Intent( this, Activity_EditarPerfil.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}