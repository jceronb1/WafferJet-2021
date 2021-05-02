package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Activity_MisCarros extends AppCompatActivity {

    private ArrayList<Carro> MisCarros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_carros);

        MisCarros = getCarrosFromDB();
        // Create the custom adapter for the trips
        CarrosCustomAdapter carrosAdapter = new CarrosCustomAdapter();
        // Create and bind list view with TripsCustomAdapter
        ListView carrosListView = (ListView) findViewById(R.id.Cars_ListView);
        carrosListView.setAdapter(carrosAdapter);


        /*
        Button botonMiViaje = (Button) findViewById(R.id.buttonMiViajeMV2);
        Button botonCrearViaje = (Button) findViewById(R.id.buttonCrearViajeMC4);

        botonMiViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMiViaje = new Intent(v.getContext(), Activity_Mi_Viaje_Conductor.class);
                startActivity(intentMiViaje);
            }
        });

        botonCrearViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCrearViaje = new Intent(v.getContext(), Activity_CrearViaje_Maps.class);
                startActivity(intentCrearViaje);
            }
        });*/
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

    //funciones para listview------------------------------------

    public ArrayList<Carro> getCarrosFromDB() {
        ArrayList<Carro> testData = new ArrayList<Carro>();
        testData.add(new Carro("Gabriel Gomez","Mazda","JNL 373","CX5",5,123));
        testData.add(new Carro("Joaquin Perez","Renault","HLK 819","Koleos",5,456));
        testData.add(new Carro("Pablo Manrique","Chevrolet","FVL 652","TrailBlazer",7,789));
        return testData;
    }


    class CarrosCustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return MisCarros.size();
        }

        @Override
        public Object getItem(int position) {
            return MisCarros.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //
            convertView = getLayoutInflater().inflate(R.layout.listview_carros, null);
            // Get information fields of the view
            TextView placa = (TextView) convertView.findViewById(R.id.campoPlaca);
            TextView marca = (TextView) convertView.findViewById(R.id.campoMarca);
            TextView modelo = (TextView) convertView.findViewById(R.id.campoModelo);
            TextView capacidad = (TextView) convertView.findViewById(R.id.campoCapacidad);
            Log.i("Placa",placa.getText().toString());
            Log.i("Marca",marca.getText().toString());
            Log.i("Modelo",modelo.getText().toString());
            // Set information to the view
            placa.setText(MisCarros.get(position).placa);
            marca.setText(MisCarros.get(position).marcaCarro);
            modelo.setText(MisCarros.get(position).modelo);
            capacidad.setText(String.valueOf(MisCarros.get(position).capacidad));

            // Set event listeners to teh buttons
            Button seleccionarCarro = (Button) convertView.findViewById(R.id.botonSeleccionarCarro);
            seleccionarCarro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("TAG","Carro seleccionado");
                }
            });

            // Return view
            return convertView;
        }
    }
}