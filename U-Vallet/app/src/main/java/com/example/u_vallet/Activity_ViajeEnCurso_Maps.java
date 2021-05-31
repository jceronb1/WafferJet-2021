package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Activity_ViajeEnCurso_Maps extends FragmentActivity implements OnMapReadyCallback {

    //--------------------------------------------------------
    //                         Attributes
    //--------------------------------------------------------
    // Map
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mRefList;
    // Firebase Path(s)
    public static final String PATH_ROUTES = "routes/";
    // Hardware sensor (Accelerometer)
    private SensorManager sensorManager;
    private Sensor sensor;
    private TriggerEventListener triggerEventListener;
    // Location
    private LatLng mOrigin;
    private LatLng mDestination;
    private FusedLocationProviderClient fusedLocationClient;
    // Button(s)
    private Button botonFinalizarViaje;
    // Routes
    private PolylineOptions line;
    String routeUid;

    //--------------------------------------------------------
    //                         On create
    //--------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__viaje_en_curso__maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //------------ Firebase ------------
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        //------------ User ------------
        routeUid = getIntent().getExtras().getString("Trip");
        setRoute();

        //------------ Location ------------
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //------------ Accelerometer sensor ------------
        sensorManager = (SensorManager) getSystemService(Activity_ViajeEnCurso_Maps.this.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        triggerEventListener = new TriggerEventListener() {
            @Override
            public void onTrigger(TriggerEvent event) {
                Log.i("SENSOR", "SE HA DETECTADO CAMBIO EN EL SENSOR");
                // Get user's last known location
                getUserLocation();
                // Show the user that a change has been detected
                Toast.makeText(Activity_ViajeEnCurso_Maps.this, "Se ha detectado un cambio en el acelerometro", Toast.LENGTH_SHORT).show();
            }
        };
        // Bind trigger event with sensor
        sensorManager.requestTriggerSensor(triggerEventListener, sensor);

        //------------ Buttons ------------
        botonFinalizarViaje = findViewById(R.id.buttonFinalizarViaje);
        botonFinalizarViaje.setOnClickListener(v -> {
            // End the courrent route
            endRoute();
            // Launch new activity with finish route info
            Intent finalizar = new Intent(v.getContext(), Activity_Finalizar_Viaje.class);
            finalizar.putExtra("KeyEnd", routeUid);
            startActivity(finalizar);
        });

    }

    //--------------------------------------------------------
    //                         Methods
    //--------------------------------------------------------
    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("PER", "No hay permisos");
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.i("Location", "Location is requested");
                        updateOriginLocationFirebase(location);
                    }
                });
    }

    private void updateOriginLocationFirebase(Location location) {
        Log.i("location", location.toString());
        // Firebase reference
        mRef = mDatabase.getReference(PATH_ROUTES).child(routeUid).child("originLocation");
        // Create HashMap to store location
        HashMap<String, Double> newLocation = new HashMap<String, Double>();
        newLocation.put("latitude", location.getLatitude());
        newLocation.put("longitude", location.getLongitude());
        // Set new location in firebase
        mRef.setValue(newLocation);
    }

    //--------------------------------------------------------
    //                          Map
    //--------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    //--------------------------------------------------------
    //                          Routes
    //--------------------------------------------------------
    private void endRoute(){
        mRef = mDatabase.getReference(PATH_ROUTES).child(routeUid);
        mRef.child("status").setValue("finished");
    }

    private void setRoute(){
        //----------------- Listener to origin location changes -----------------
        mRef = mDatabase.getReference(PATH_ROUTES).child(routeUid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Double originLat = snapshot.child("originLocation").child("latitude").getValue(Double.class);
                Double originLong = snapshot.child("originLocation").child("longitude").getValue(Double.class);
                Double destinationLat = snapshot.child("destinationLocation").child("latitude").getValue(Double.class);
                Double destinationLong = snapshot.child("destinationLocation").child("longitude").getValue(Double.class);
                //String uidConductor = ruta.getUidConductor();
                String keyF = snapshot.child("key").getValue(String.class);;
                mOrigin = new LatLng(originLat,originLong);
                mDestination = new LatLng(destinationLat, destinationLong);

                mMap.addMarker(new MarkerOptions().position(mOrigin).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions().position(mDestination));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mOrigin));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //----------------- Draw the routes line -----------------
        mRefList = mDatabase.getReference(PATH_ROUTES).child(routeUid).child("route");
        mRefList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                List<LatLng> aline = new ArrayList<LatLng>();
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    Double lat = snapshot.child(String.valueOf(count)).child("latitude").getValue(Double.class);
                    Double lng = snapshot.child(String.valueOf(count)).child("longitude").getValue(Double.class);
                    LatLng point = new LatLng(lat, lng);
                    aline.add(point);
                    line = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));

                    count++;
                }
                Log.d("USPRUEBA", "Count final: "+String.valueOf(count));
                Log.d("USPRUEBA", "Aline: "+String.valueOf(aline.size()));
                line.addAll(aline);
                mMap.addPolyline(line);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}