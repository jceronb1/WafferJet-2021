package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.List;

public class Activity_RutaViaje_Maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mRefList;
    private DatabaseReference mRefPasajeros;

    public static final String PATH_ROUTES = "routes/";
    ArrayList<LatLng> mMarkerPoints;
    private LatLng mOrigin;
    private LatLng mDestination;
    private Button botonIniciarViaje;
    private PolylineOptions line;

    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__ruta_viaje__maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mMarkerPoints = new ArrayList<>();
        botonIniciarViaje = (Button)findViewById(R.id.buttonIniciarViaje);

        String key = getIntent().getExtras().getString("Route_4");
        Log.d("USPRUEBA", key + "0");
        setRoute(key);

        botonIniciarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viaje = new Intent(v.getContext(), Activity_ViajeEnCurso_Maps.class);
                startRoute(key);
                viaje.putExtra("Trip", key);
                startActivity(viaje);
            }
        });

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

    private void startRoute(String key){
        mRef = mDatabase.getReference(PATH_ROUTES).child(key);
        mRef.child("status").setValue("onCourse");

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
                //String uidConductor = ruta.getUidConductor();
                String keyF = snapshot.child("key").getValue(String.class);;
                mOrigin = new LatLng(originLat,originLong);
                mDestination = new LatLng(destinationLat, destinationLong);

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
        mRefPasajeros = mDatabase.getReference(PATH_ROUTES).child(key);
        mRefPasajeros.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("pasajeros").exists() && key.equals(snapshot.child("key").getValue(String.class))){
                    for(DataSnapshot singleSnapshot : snapshot.child("pasajeros").getChildren()){
                        Double latitude = singleSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = singleSnapshot.child("longitude").getValue(Double.class);
                        String name = singleSnapshot.child("nombre").getValue(String.class);
                        Integer cantidad = singleSnapshot.child("cantidadReservas").getValue(Integer.class);
                        String title = name + " - "+String.valueOf(cantidad);
                        LatLng user = new LatLng(latitude,longitude);

                        mMarkerPoints.add(user);
                        if(mMarkerPoints.size() == 0)
                            mMap.addMarker(new MarkerOptions().position(user).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                        else if (mMarkerPoints.size() == 1)
                           mMap.addMarker(new MarkerOptions().position(user).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        else if (mMarkerPoints.size() == 2)
                            mMap.addMarker(new MarkerOptions().position(user).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        else if (mMarkerPoints.size() == 3)
                            mMap.addMarker(new MarkerOptions().position(user).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        else if (mMarkerPoints.size() == 4)
                            mMap.addMarker(new MarkerOptions().position(user).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                        else if (mMarkerPoints.size() >= 5)
                            mMap.addMarker(new MarkerOptions().position(user).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                }
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