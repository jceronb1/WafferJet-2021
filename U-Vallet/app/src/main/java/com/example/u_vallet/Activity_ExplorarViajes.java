package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Activity_ExplorarViajes extends AppCompatActivity {
    //----------------------------------------------
    //----------------  Attributes  ----------------
    //----------------------------------------------
    private ArrayList<Viaje> ActiveTrips = new ArrayList<>();
    private ArrayList<String> idRutas = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;
    private DatabaseReference mRef;
    private DatabaseReference mRef2;
    public static final String PathRoute = "routes/";
    private String correoUserAutenticado;

    //----------------------------------------------
    //-----------------  On Create  ----------------
    //----------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorar_viajes);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();

        // Get all active trips from DB
        getRoutesFromDB();



        Button botonMiViaje = (Button) findViewById(R.id.buttonCrearViajeMC3);

        botonMiViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correoUserAutenticado = mAuth.getCurrentUser().getEmail();
                mRef2 = FirebaseDatabase.getInstance().getReference("users/");
                mRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snap :snapshot.getChildren() ){
                            String correo = snap.child("username").getValue(String.class);
                            if(correo.equals(correoUserAutenticado)){
                                try {
                                    String viajeactivo = snap.child("viajeActivo").getValue(String.class);
                                    if(viajeactivo.equals("true")){
                                        Intent intentMiViaje = new Intent(v.getContext(), Activity_Mi_Viaje_Pasajero.class);
                                        startActivity(intentMiViaje);
                                    }else{
                                        Intent intentMiViaje = new Intent(v.getContext(), Activity_Mi_Viaje_Pasajero_Alterantivo.class);
                                        startActivity(intentMiViaje);
                                    }
                                }catch (Exception e){
                                    Intent intentMiViaje = new Intent(v.getContext(), Activity_Mi_Viaje_Pasajero_Alterantivo.class);
                                    startActivity(intentMiViaje);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    //----------------------------------------------
    //--------- Methods that involves DB -----------
    //----------------------------------------------

    private void getRoutesFromDB(){
        mRef = mDataBase.getReference(PathRoute);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = ActiveTrips.size();
                for(DataSnapshot singlesnapshot : snapshot.getChildren() ){
                    Viaje viaje = new Viaje();
                    String status =  singlesnapshot.child("status").getValue(String.class);
                    String uidConductor =  singlesnapshot.child("uidConductor").getValue(String.class);


                    String key = singlesnapshot.child("key").getValue(String.class);;
                    if(status.equals("active") && !uidConductor.equals(mAuth.getUid()) && !idRutas.contains(key)){

                        String origen = singlesnapshot.child("originDirection").getValue(String.class);
                        String nombreConductor = singlesnapshot.child("nombreConductor").getValue(String.class);
                        String destino = singlesnapshot.child("destinationDirection").getValue(String.class);
                        String marca = singlesnapshot.child("carro").child("marca").getValue(String.class);
                        String placa = singlesnapshot.child("carro").child("placa").getValue(String.class);
                        Integer valorCupo = singlesnapshot.child("valorViaje").getValue(Integer.class);
                        Integer capacidad = singlesnapshot.child("cuposDisponibles").getValue(Integer.class);
                        String puntoEncuentro = singlesnapshot.child("puntoEncuentro").getValue(String.class);
                        String hora = singlesnapshot.child("horaViaje").getValue(String.class);
                        if(capacidad > 0) {
                            viaje.setIdConductor(uidConductor);
                            viaje.setIdViaje(key);
                            viaje.setNombreDelConductor(nombreConductor);
                            viaje.setOrigen(origen);
                            viaje.setDestino(destino);
                            viaje.setMarca(marca);
                            viaje.setPlaca(placa);
                            viaje.setValorCupo(valorCupo);
                            viaje.setCapacidad(capacidad);
                            viaje.setPuntoEncuentro(puntoEncuentro);
                            viaje.setHora(hora);
                            ActiveTrips.add(viaje);
                            idRutas.add(key);
                        }
                    }
                }

                if(size != ActiveTrips.size()){
                    // Create the custom adapter for the trips
                    TripsCustomAdapter tripsAdapter = new TripsCustomAdapter();
                    // Create and bind list view with TripsCustomAdapter
                    ListView tripsListView = (ListView) findViewById(R.id.Trips_ListView);
                    tripsListView.setAdapter(tripsAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //----------------------------------------------
    //------------ Methods for the menu ------------
    //----------------------------------------------
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


    //----------------------------------------------
    //--------   Custom adapter for trips  ---------
    //----------------------------------------------
    class TripsCustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ActiveTrips.size();
        }

        @Override
        public Object getItem(int position) {
            return ActiveTrips.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //
            convertView = getLayoutInflater().inflate(R.layout.listview_viaje, null);
            // Get information fields of the view
            TextView driverName = (TextView) convertView.findViewById(R.id.Driver_Name);
            TextView originDirection = (TextView) convertView.findViewById(R.id.Origin_Direction);
            TextView destinyDirection = (TextView) convertView.findViewById(R.id.Destiny_Direction);
            TextView carBrand = (TextView) convertView.findViewById(R.id.Car_Brand);
            TextView carPlate = (TextView) convertView.findViewById(R.id.Car_Plate);
            TextView cuposDisponibles = (TextView)   convertView.findViewById(R.id.cupos);
            TextView valorCupo = (TextView)   convertView.findViewById(R.id.valorCupo);
            TextView puntoEncuentro = (TextView)   convertView.findViewById(R.id.puntEncuentro);
            TextView hora = (TextView)   convertView.findViewById(R.id.hora);
            // Set information to the view
            driverName.setText(ActiveTrips.get(position).NombreDelConductor);
            originDirection.setText(ActiveTrips.get(position).origen);
            destinyDirection.setText(ActiveTrips.get(position).destino);
            carBrand.setText(ActiveTrips.get(position).marca);
            carPlate.setText(ActiveTrips.get(position).placa);
            cuposDisponibles.setText(String.valueOf(ActiveTrips.get(position).capacidad));
            valorCupo.setText(String.valueOf(ActiveTrips.get(position).valorCupo));
            puntoEncuentro.setText(ActiveTrips.get(position).puntoEncuentro);
            hora.setText(ActiveTrips.get(position).hora);

            // Set event listeners to the buttons
            Button seeRoute = (Button) convertView.findViewById(R.id.Button_See_Route);
            Button reserveTrip = (Button) convertView.findViewById(R.id.Button_Reserve_Trip);
            seeRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mapIntent = new Intent(getBaseContext(), Activity_Pasajero_RutaViaje_Maps.class);
                    mapIntent.putExtra("PasajeroKey", ActiveTrips.get(position).getIdViaje());
                    startActivity(mapIntent);
                }
            });
            reserveTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    correoUserAutenticado = mAuth.getCurrentUser().getEmail();
                    mRef2 = FirebaseDatabase.getInstance().getReference("users/");
                    mRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snap :snapshot.getChildren() ){
                                String correo = snap.child("username").getValue(String.class);
                                if(correo.equals(correoUserAutenticado)){
                                    try {
                                        String viajeactivo = snap.child("viajeActivo").getValue(String.class);
                                        if(viajeactivo.equals("true")){
                                            Toast.makeText(getBaseContext(), "usted ya tiene un viaje reservado", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Intent intent = new Intent( getBaseContext(), Activity_Reservar_Viaje.class);
                                            intent.putExtra("direccion", "");
                                            intent.putExtra("Lat", "");
                                            intent.putExtra("Lng", "");
                                            intent.putExtra("reserva","");
                                            intent.putExtra("precio",valorCupo.getText().toString());
                                            intent.putExtra("idviaje", ActiveTrips.get(position).getIdViaje());
                                            intent.putExtra("cuposDisponibles",cuposDisponibles.getText().toString());
                                            intent.putExtra("uidconductor",ActiveTrips.get(position).getIdConductor());
                                            startActivity(intent);
                                        }
                                    }catch (Exception e){
                                        Intent intent = new Intent( getBaseContext(), Activity_Reservar_Viaje.class);
                                        intent.putExtra("direccion", "");
                                        intent.putExtra("Lat", "");
                                        intent.putExtra("Lng", "");
                                        intent.putExtra("reserva","");
                                        intent.putExtra("precio",valorCupo.getText().toString());
                                        intent.putExtra("idviaje", ActiveTrips.get(position).getIdViaje());
                                        intent.putExtra("cuposDisponibles",cuposDisponibles.getText().toString());
                                        intent.putExtra("uidconductor",ActiveTrips.get(position).getIdConductor());
                                        startActivity(intent);
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            // Return view
            return convertView;
        }
    }

}