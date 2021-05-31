package com.example.u_vallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Activity_Seleccionar_Carro extends AppCompatActivity {

    DatabaseReference mRef;

    private ArrayList<Carro> MisCarros = new ArrayList<>();
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity__seleccionar__carro);
        key = getIntent().getExtras().getString("Route");

        getCarrosFromDB();
        // Create the custom adapter for the trips

            CarrosCustomAdapter carrosAdapter = new CarrosCustomAdapter();
            // Create and bind list view with TripsCustomAdapter
            ListView carrosListView = (ListView) findViewById(R.id.Cars_ListView);
            carrosListView.setAdapter(carrosAdapter);

    }
    //funciones para listview------------------------------------

    public void alertas(View v){
        Toast.makeText(v.getContext(), "Seleccione un carro para continuar", Toast.LENGTH_SHORT).show();
    }

    public void getCarrosFromDB() {
        MisCarros.clear();
        myRef = database.getReference("cars/"+mAuth.getCurrentUser().getUid()+"/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String marca = singleSnapshot.child("marca").getValue(String.class);
                    Integer capacidad = singleSnapshot.child("capacidad").getValue(Integer.class);
                    String modelo = singleSnapshot.child("modelo").getValue(String.class);
                    String placa = singleSnapshot.child("placa").getValue(String.class);
                    Carro carro = new Carro();
                    carro.setMarcaCarro(marca);
                    carro.setCapacidad(capacidad);
                    carro.setModelo(modelo);
                    carro.setPlaca(placa);

                    MisCarros.add(carro);
                    //testData.add(carro);

                }
                Activity_Seleccionar_Carro.CarrosCustomAdapter carrosAdapter = new Activity_Seleccionar_Carro.CarrosCustomAdapter();
                ListView carrosListView = (ListView) findViewById(R.id.Cars_ListView);
                carrosListView.setAdapter(carrosAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("LOADUSER", "Error en la consulta", databaseError.toException());
            }
        });
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
            convertView = getLayoutInflater().inflate(R.layout.listview_carros_seleccionar, null);
            // Get information fields of the view
            ImageView fotocarro = (ImageView) convertView.findViewById(R.id.fotocarro);
            TextView placa = (TextView) convertView.findViewById(R.id.campoPlaca);
            TextView marca = (TextView) convertView.findViewById(R.id.campoMarca);
            TextView modelo = (TextView) convertView.findViewById(R.id.campoModelo);
            TextView capacidad = (TextView) convertView.findViewById(R.id.campoCapacidad);
            // Set information to the view
            placa.setText(MisCarros.get(position).placa);
            marca.setText(MisCarros.get(position).marcaCarro);
            modelo.setText(MisCarros.get(position).modelo);
            capacidad.setText(String.valueOf(MisCarros.get(position).capacidad));
            StorageReference profileRef = mStorageRef.child("cars/"+mAuth.getCurrentUser().getUid()+"/"+placa.getText().toString()+"/car.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(fotocarro);
                }
            });

            // Set event listeners to teh buttons
            Button seleccionarCarro = (Button) convertView.findViewById(R.id.botonSeleccionarCarro);
            seleccionarCarro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("TAG","Carro seleccionado");
                    Intent intentContinuarViaje = new Intent (v.getContext(), Activity_CrearViaje.class);
                    String direccionO = getIntent().getExtras().getString("direccionO");
                    String direccionD = getIntent().getExtras().getString("direccionD");
                    intentContinuarViaje.putExtra("Route_2", key);
                    intentContinuarViaje.putExtra("direccionO_2", direccionO);
                    intentContinuarViaje.putExtra("direccionD_2", direccionD);
                    mRef = FirebaseDatabase.getInstance().getReference("routes/").child(key);
                    mRef.child("carro").child("marca").setValue(marca.getText().toString());
                    mRef.child("carro").child("placa").setValue(placa.getText().toString());
                    intentContinuarViaje.putExtra("placa", marca.getText().toString() +" : "+ placa.getText().toString());
                    startActivity(intentContinuarViaje);
                }
            });
            // Return view
            return convertView;
        }
    }
}