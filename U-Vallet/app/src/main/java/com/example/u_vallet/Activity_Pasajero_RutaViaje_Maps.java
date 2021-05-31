package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_Pasajero_RutaViaje_Maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mRefList;

    public static final String PATH_ROUTES = "routes/";

    private LatLng mOrigin;
    private LatLng mDestination;
    private Button botonIniciarViaje;
    private TextView pasajeroOrigin;
    private TextView pasajeroDestination;
    private PolylineOptions line;

    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__pasajero__ruta_viaje__maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        pasajeroOrigin = (TextView)findViewById(R.id.tVPasajeroOrigen);
        pasajeroDestination = (TextView)findViewById(R.id.tVPasajeroDestino);
        String key = getIntent().getExtras().getString("PasajeroKey");
        setRoute(key);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void setRoute(String key){
        mRef = mDatabase.getReference(PATH_ROUTES).child(key);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Double originLat = snapshot.child("originLocation").child("latitude").getValue(Double.class);
                Double originLong = snapshot.child("originLocation").child("longitude").getValue(Double.class);
                Double destinationLat = snapshot.child("destinationLocation").child("latitude").getValue(Double.class);
                Double destinationLong = snapshot.child("destinationLocation").child("longitude").getValue(Double.class);
                String originDriection = snapshot.child("originDirection").getValue(String.class);
                String destinationDirection = snapshot.child("destinationDirection").getValue(String.class);
                //String uidConductor = ruta.getUidConductor();
                String keyF = snapshot.child("key").getValue(String.class);;
                mOrigin = new LatLng(originLat,originLong);
                mDestination = new LatLng(destinationLat, destinationLong);

                pasajeroOrigin.setText(originDriection);
                pasajeroDestination.setText(destinationDirection);
                Log.d("USPRUEBA", keyF+ "/"+key );

                mMap.addMarker(new MarkerOptions().position(mOrigin).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions().position(mDestination));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mOrigin));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRefList = mDatabase.getReference(PATH_ROUTES).child(key).child("route");
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