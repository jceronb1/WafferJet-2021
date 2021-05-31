package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Activity_ReservarViaje_Maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mRefList;

    public static final String PATH_ROUTES = "routes/";

    private LatLng mOrigin;
    private LatLng mDestination;
    private PolylineOptions line;
    SearchView seleccionarUbicacion;
    private LatLng mReserva;
    private LatLng reservaLatLng;
    private String reservaLocation;
    ArrayList<LatLng> mMarkerPoints;
    private Marker markerReserva;

    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_viaje__maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        markerReserva = null;
        mMarkerPoints = new ArrayList<>();
        int disponibles = Integer.parseInt(getIntent().getExtras().getString("cuposDisponibles"));
        Log.d("USPUEBA", "Mapa: "+String.valueOf(disponibles));
        String uidconductor = getIntent().getExtras().getString("uidconductor");
        String valor = getIntent().getExtras().getString("precio");
        String key = getIntent().getExtras().getString("Viaje");
        setRoute(key);
        seleccionarUbicacion = (SearchView)findViewById(R.id.sVReserva);
        int closeButtonID = seleccionarUbicacion.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView seleccionarUbicacionButton = (ImageView)seleccionarUbicacion.findViewById(closeButtonID);

        seleccionarUbicacionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarUbicacion.setQuery("", false);
                if(markerReserva != null){
                    mMarkerPoints.remove(markerReserva.getPosition());
                    markerReserva.remove();
                    markerReserva = null;
                }
            }
        });

        seleccionarUbicacion.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(markerReserva != null){
                    markerReserva.remove();
                }
                if(mMarkerPoints.size() > 0){
                    mMarkerPoints.clear();
                }
                reservaLocation = seleccionarUbicacion.getQuery().toString();
                if(!reservaLocation.contains(",")){
                    reservaLocation = reservaLocation + ", Bogotá";
                }
                List<Address> addressListReserva = null;

                if(reservaLocation != null || reservaLocation.equals("")){
                    Geocoder geocoder = new Geocoder( Activity_ReservarViaje_Maps.this);
                    try{
                        addressListReserva = geocoder.getFromLocationName(reservaLocation, 1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    Address addressReserva = addressListReserva.get(0);
                    reservaLatLng = new LatLng(addressReserva.getLatitude(), addressReserva.getLongitude());

                    if(null != markerReserva){
                        markerReserva.remove();
                    }
                    markerReserva = mMap.addMarker(new MarkerOptions().position(reservaLatLng).title(reservaLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(reservaLatLng, 17));
                    mMarkerPoints.add(reservaLatLng);

                    if(mMarkerPoints.size() >=1){
                        mReserva = reservaLatLng;
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Button buttonGuardar = (Button)findViewById(R.id.buttonGuardarViaje);
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(v.getContext(), Activity_Reservar_Viaje.class);
                intent.putExtra("direccion", reservaLocation);
                intent.putExtra("LatLng", mReserva);
                String dispo = String.valueOf(disponibles);
                intent.putExtra("cuposDisponibles", dispo);
                intent.putExtra("uidconductor", uidconductor);
                intent.putExtra("precio", valor);
                intent.putExtra("idviaje", key);
                startActivity(intent);
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
        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if(mMarkerPoints.size() > 0){
                    mMarkerPoints.clear();
                    markerReserva.remove();
                }
                mMarkerPoints.add(point);
                MarkerOptions options = new MarkerOptions();
                options.position(point);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                List<Address> addresses = null;
                String address = String.valueOf(point);
                String[] aux = address.split("\\(");
                String[] aux2 = aux[1].split("\\)");
                String[] ltlg = aux2[0].split(",");
                double lat = Double.parseDouble(ltlg[0]);
                double lng = Double.parseDouble(ltlg[1]);
                String completeAdd = null;
                String finalAdd = null;
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                completeAdd = addresses.get(0).getAddressLine(0);

                String[] addName = completeAdd.split(",");

                finalAdd = addName[0];
                reservaLocation = finalAdd;
                seleccionarUbicacion.setQuery(finalAdd, false);
                if(mMarkerPoints.size() == 1){
                    mReserva = point;
                    markerReserva = mMap.addMarker(options.title(finalAdd));
                }
            }
        });

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