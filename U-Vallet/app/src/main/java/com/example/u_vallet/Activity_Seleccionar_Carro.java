package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Activity_Seleccionar_Carro extends AppCompatActivity {

    private ArrayList<Carro> MisCarros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__seleccionar__carro);

        MisCarros = getCarrosFromDB();
        // Create the custom adapter for the trips
        CarrosCustomAdapter carrosAdapter = new CarrosCustomAdapter();
        // Create and bind list view with TripsCustomAdapter
        ListView carrosListView = (ListView) findViewById(R.id.Cars_ListView);
        carrosListView.setAdapter(carrosAdapter);
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
                    Intent intentContinuarViaje = new Intent (v.getContext(), Activity_CrearViaje.class);
                    intentContinuarViaje.putExtra("placa", marca.getText().toString() +" : "+ placa.getText().toString());
                    startActivity(intentContinuarViaje);
                }
            });

            // Return view
            return convertView;
        }
    }
}