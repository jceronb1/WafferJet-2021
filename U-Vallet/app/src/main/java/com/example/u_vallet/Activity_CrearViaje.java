package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class Activity_CrearViaje extends AppCompatActivity implements View.OnClickListener {

    Button botonHora;
    EditText campoHora;
    private int dia,mes,anio,hora,minutos;
    private TimePickerDialog.OnTimeSetListener mTimeListener;
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

        //obtener hora partida
        botonHora = (Button)findViewById(R.id.botonHora);
        campoHora = (EditText)findViewById(R.id.editHoraPartida);
        botonHora.setOnClickListener(this);

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

    //boton para la hora partida
    @Override
    public void onClick(View v) {
        if(v == botonHora){
            final Calendar calendar = Calendar.getInstance();
            hora = calendar.get(Calendar.HOUR_OF_DAY);
            minutos = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    campoHora.setText(hourOfDay+" : "+minute);
                }
            },hora,minutos,false);
            timePickerDialog.show();
        }
    }
}