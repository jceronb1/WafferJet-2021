package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Activity_CrearViaje extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_viaje);

        String origen = getIntent().getExtras().getString("Origen");
        String destino = getIntent().getExtras().getString("Destino");
        Log.d("VIAJE_CRE", origen +" -> "+ destino );
        //String origenAux[] = origen.split(",");
        Button botonMiViaje = (Button) findViewById(R.id.buttonMiViajeCV);
        Button botonMisCarros = (Button) findViewById(R.id.buttonMisCarrosCV);

        EditText origenET = (EditText) findViewById(R.id.origen);
        EditText destinoET = (EditText)findViewById(R.id.destino);

        origenET.setText("Origen: " + origen);
        destinoET.setText("Destino: "+ destino);

        botonMiViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMiViaje = new Intent(v.getContext(), Activity_Mi_Viaje_Conductor.class);
                startActivity(intentMiViaje);
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
        }
        return super.onOptionsItemSelected(item);
    }
}