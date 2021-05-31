package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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


public class Activity_MisCarros extends AppCompatActivity {

    private ArrayList<Carro> MisCarros = new ArrayList<>();
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_carros);
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        getCarrosFromDB();
        // Create the custom adapter for the trips
        //CarrosCustomAdapter carrosAdapter = new CarrosCustomAdapter();
        // Create and bind list view with TripsCustomAdapter
        //ListView carrosListView = (ListView) findViewById(R.id.Cars_ListView);
        //carrosListView.setAdapter(carrosAdapter);

        //----- Get the button to add a new car and add event listener -------
        Button btnAgregarCarro = (Button) findViewById(R.id.btn_MisCarros_AgregarCarro);
        btnAgregarCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and launch the new activity with an Intent
                Intent agregarCarro = new Intent(v.getContext(), Activity_AgregarCarro.class);
                startActivity(agregarCarro);
            }
        });

        //
        Button botonMiViaje = (Button) findViewById(R.id.buttonMiViajeMV2);
        botonMiViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMiViaje = new Intent(v.getContext(), Activity_Mi_Viaje_Conductor.class);
                startActivity(intentMiViaje);
            }
        });

        //
        Button botonCrearViaje = (Button) findViewById(R.id.buttonCrearViajeMC4);
        botonCrearViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCrearViaje = new Intent(v.getContext(), Activity_CrearViaje_Maps.class);
                startActivity(intentCrearViaje);
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
        }else if(itemClicked == R.id.menuLogOut){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent( this, Activity_Login.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //funciones para listview------------------------------------

    public void getCarrosFromDB() {

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
                CarrosCustomAdapter carrosAdapter = new CarrosCustomAdapter();
                ListView carrosListView = (ListView) findViewById(R.id.Cars_ListView);
                carrosListView.setAdapter(carrosAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("LOADUSER", "Error en la consulta", databaseError.toException());
            }
        });

    }

    //----------------------------------------------
    //--------   Custom adapter for cars  ----------
    //----------------------------------------------
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
            Log.i("perro", String.valueOf(MisCarros.size()));
            convertView = getLayoutInflater().inflate(R.layout.listview_carros, null);
            // Get information fields of the view
            ImageView fotocarro = (ImageView) convertView.findViewById(R.id.fotocarro);
            TextView placa = (TextView) convertView.findViewById(R.id.campoPlaca);
            TextView marca = (TextView) convertView.findViewById(R.id.campoMarca);
            TextView modelo = (TextView) convertView.findViewById(R.id.campoModelo);
            TextView capacidad = (TextView) convertView.findViewById(R.id.campoCapacidad);


            //Log.i("Marca",marca.getText().toString());
            //Log.i("Modelo",modelo.getText().toString());
            // Set information to the view
            placa.setText(MisCarros.get(position).placa);
            marca.setText(MisCarros.get(position).marcaCarro);
            modelo.setText(MisCarros.get(position).modelo);
            Log.i("Placa",placa.getText().toString());
            capacidad.setText(String.valueOf(MisCarros.get(position).capacidad));
            StorageReference profileRef = mStorageRef.child("cars/"+mAuth.getCurrentUser().getUid()+"/"+placa.getText().toString()+"/car.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(fotocarro);
                }
            });

            //------ Get button to edit a car and add event listener --------
            Button btnEditarCarro = (Button) convertView.findViewById(R.id.botonEditarCarro);
            btnEditarCarro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editarCarro = new Intent(v.getContext(), Activity_EditarCarro.class);
                    editarCarro.putExtra("placa" , placa.getText().toString());
                    editarCarro.putExtra("marca" , marca.getText().toString());
                    editarCarro.putExtra("modelo" , modelo.getText().toString());
                    editarCarro.putExtra("capacidad" , capacidad.getText().toString());
                    startActivity(editarCarro);
                }
            });

            // Return view
            return convertView;
        }
    }
}