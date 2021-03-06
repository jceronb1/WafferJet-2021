package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.u_vallet.Activity_Mi_Viaje_Condcutor_Alternativo;
import com.example.u_vallet.Activity_Roles;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activity_Mi_Viaje_Conductor extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText idEt;
    private EditText origenEt;
    private EditText destinoEt;
    private EditText horaEt;
    private EditText puntoEt;
    private Spinner pasajeros;
    private DatabaseReference mRef2;

    ArrayAdapter<String> spinnerAdapter;
    private boolean active = false;
    private String keyAux;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_viaje_conductor);

        idEt = (EditText)findViewById(R.id.viajeIDText);
        origenEt = (EditText)findViewById(R.id.viajeOrigenText);
        destinoEt = (EditText)findViewById(R.id.miViajeDestinoText);
        horaEt = (EditText)findViewById(R.id.horaPartidaText);
        puntoEt = (EditText)findViewById(R.id.puntoEncuentroText);
        pasajeros = (Spinner)findViewById(R.id.spinnerPasajeros);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pasajeros.setAdapter(spinnerAdapter);
        Button botonCrearViaje = (Button) findViewById(R.id.buttonCrearViajeMC);
        Button botonMisCarros = (Button) findViewById(R.id.buttonMisCarrosMV);
        Button botonCancelarViaje = (Button) findViewById(R.id.botonCancelarViaje);
        Button botonVerRuta = (Button) findViewById(R.id.buttonVerRutaC);
        mAuth = FirebaseAuth.getInstance();
        loadRoute();

        botonVerRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(active) {
                    Intent intentVerRuta = new Intent(v.getContext(), Activity_RutaViaje_Maps.class);
                    intentVerRuta.putExtra("Route_4", keyAux);
                    startActivity(intentVerRuta);
                }
            }
        });

        botonCancelarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef2 = FirebaseDatabase.getInstance().getReference("users/");
                mRef2.child(mAuth.getUid()).child("viajeActivo").setValue("false");
                cancelRoute();
            }
        });

        botonCrearViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!active) {
                    Intent intentCrearViaje = new Intent(v.getContext(), Activity_CrearViaje_Maps.class);
                    startActivity(intentCrearViaje);
                }else {
                    Toast.makeText(getBaseContext(), "Tiene un viaje activo, por lo que no puede crear otro.", Toast.LENGTH_SHORT).show();
                }
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

    private void loadRoute(){
        mDatabase = FirebaseDatabase.getInstance().getReference("routes");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String uid = singleSnapshot.child("uidConductor").getValue(String.class);
                    String status = singleSnapshot.child("status").getValue(String.class);
                    if(status.equals("canceled") && uid.equals(mAuth.getUid())){
                        idEt.setText("");
                        origenEt.setText("");
                        destinoEt.setText("");
                        horaEt.setText("");
                        puntoEt.setText("");
                        active = false;
                    }else if(status.equals("active") && uid.equals(mAuth.getUid())){
                        active = true;
                        keyAux = singleSnapshot.child("key").getValue(String.class);
                        idEt.setText(keyAux);
                        origenEt.setText(singleSnapshot.child("originDirection").getValue(String.class));
                        destinoEt.setText(singleSnapshot.child("destinationDirection").getValue(String.class));
                        horaEt.setText(singleSnapshot.child("horaViaje").getValue(String.class));
                        puntoEt.setText(singleSnapshot.child("puntoEncuentro").getValue(String.class));
                        loadPasajeros(keyAux);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPasajeros(String key){
        ArrayList<String> pasajeros = new ArrayList<String>();
        mDatabase = FirebaseDatabase.getInstance().getReference("routes").child(key);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("pasajeros").exists() && key.equals(snapshot.child("key").getValue(String.class))){
                    Log.d("USPRUEBA","Llave:"+ key);
                    spinnerAdapter.clear();
                    for(DataSnapshot singleSnapshot : snapshot.child("pasajeros").getChildren()){
                        String nombre = singleSnapshot.child("nombre").getValue(String.class);
                        Integer cantidadReservas = singleSnapshot.child("cantidadReservas").getValue(Integer.class);
                        String pasajero = nombre + " - Cupos: "+String.valueOf(cantidadReservas);
                        if(!pasajeros.contains(pasajero))
                            pasajeros.add(pasajero);
                    }
                    spinnerAdapter.addAll(pasajeros);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cancelRoute(){
        mDatabase = FirebaseDatabase.getInstance().getReference("routes").child(keyAux);
        mDatabase.child("status").setValue("canceled");
        Toast.makeText(getBaseContext(), "Viajse cancelado exitosamente", Toast.LENGTH_SHORT).show();
        idEt.setText("");
        origenEt.setText("");
        destinoEt.setText("");
        horaEt.setText("");
        puntoEt.setText("");
        Intent miViaje = new Intent(getBaseContext(), Activity_Mi_Viaje_Condcutor_Alternativo.class);
        startActivity(miViaje);
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
        }else if(itemClicked == R.id.menuLogOut){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent( this, Activity_Login.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}