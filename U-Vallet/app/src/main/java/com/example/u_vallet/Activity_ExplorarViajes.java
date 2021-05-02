package com.example.u_vallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Activity_ExplorarViajes extends AppCompatActivity {
    //----------------------------------------------
    //----------------  Attributes  ----------------
    //----------------------------------------------
    private ArrayList<Viaje> ActiveTrips;

    //----------------------------------------------
    //-----------------  On Create  ----------------
    //----------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorar_viajes);

        // Get all active trips from DB
        ActiveTrips = getActiveTripsFromDB();

        // Create the custom adapter for the trips
        TripsCustomAdapter tripsAdapter = new TripsCustomAdapter();
        // Create and bind list view with TripsCustomAdapter
        ListView tripsListView = (ListView) findViewById(R.id.Trips_ListView);
        tripsListView.setAdapter(tripsAdapter);

        Button botonMiViaje = (Button) findViewById(R.id.buttonCrearViajeMC3);

        botonMiViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMiViaje = new Intent(v.getContext(), Activity_Mi_Viaje_Pasajero.class);
                startActivity(intentMiViaje);
            }
        });

    }

    //----------------------------------------------
    //--------- Methods that involves DB -----------
    //----------------------------------------------
    private ArrayList<Viaje> getActiveTripsFromDB() {
        ArrayList<Viaje> testData = new ArrayList<Viaje>();
        testData.add(new Viaje("Juan Diego",
                123,
                "Origen X",
                "Destino Y",
                new Carro("Gabriel Gomez","Mazda","JNL 373","CX5",5,123)));
        testData.add(new Viaje("Campos Neira",
                456,
                "Origen X",
                "Destino Y",
                new Carro("Joaquin Perez","Renault","HLK 819","Koleos",5,456)));
        testData.add(new Viaje("Campos Neira",
                456,
                "Origen X",
                "Destino Y",
                new Carro("Pablo Manrique","Chevrolet","FVL 652","TrailBlazer",7,789)));
        return testData;
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
            // Set information to the view
            driverName.setText(ActiveTrips.get(position).NombreDelConductor);
            originDirection.setText(ActiveTrips.get(position).origen);
            destinyDirection.setText(ActiveTrips.get(position).destino);
            carBrand.setText(ActiveTrips.get(position).carroQueRealizaElViaje.marcaCarro);
            carPlate.setText(ActiveTrips.get(position).carroQueRealizaElViaje.placa);

            // Set event listeners to teh buttons
            Button seeRoute = (Button) convertView.findViewById(R.id.Button_See_Route);
            Button reserveTrip = (Button) convertView.findViewById(R.id.Button_Reserve_Trip);
            seeRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Route   ", v.getContext().toString());
                    Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=4.688549,-74.050789&daddr=4.6259875,-74.0631727");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
            reserveTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Reserve ", v.getContext().toString());
                }
            });

            // Return view
            return convertView;
        }
    }

}