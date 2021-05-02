package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_AgregarCarro extends AppCompatActivity {

    //-----------------------------------------------
    //---------------  Attributes  ------------------
    //-----------------------------------------------
    private DatabaseReference firebaseDB;

    //-----------------------------------------------
    //---------------  On create  -------------------
    //-----------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_carro);

        // Create Firebase Data Base instance
        firebaseDB = FirebaseDatabase.getInstance().getReference();

        // Get "Agregar nuevo carro" and add event listener
        Button agregarCarro = (Button) findViewById(R.id.btn_AgregarCarro_AgregarCarro);
        agregarCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input fields
                EditText placa = (EditText) v.findViewById(R.id.AgregarCarro_Placa);
                EditText marca = (EditText) v.findViewById(R.id.AgregarCarro_Marca);
                EditText modelo = (EditText) v.findViewById(R.id.AgregarCarro_Modelo);
                EditText capacidad = (EditText) v.findViewById(R.id.AgregarCarro_Capacidad);
                // Convert values in input
                String marcaCarro = marca.getText().toString();
                String placaCarro = placa.getText().toString();
                String modeloCarro = modelo.getText().toString();
                int capacidadCarro = Integer.parseInt(capacidad.getText().toString());

                // Get user info
                String nombreConductor = "Pedro Perez";
                int idConductor = 123;

                // Write new car
                // writeNewCar(nombreConductor, marcaCarro, placaCarro, modeloCarro, capacidadCarro, idConductor);
                Log.i("Carro", "New car added");
            }
        });
    }

    //-----------------------------------------------
    //----------------  Methods  --------------------
    //-----------------------------------------------
    public void writeNewCar(String nombreConductor, String marcaCarro, String placa,String modelo,int capacidad ,int idConductor) {
        // Create instance of Car
        Carro carro = new Carro(nombreConductor, marcaCarro, placa, modelo, capacidad, idConductor);
        //
        firebaseDB.child("Carros").child(String.valueOf(idConductor)).setValue(carro);
    }

}