package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Marker;
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
    private Marker markerOrigin = null;
    ArrayList<LatLng> mMarkerPoints;
    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mRefList;
    private DatabaseReference mRefPasajeros;
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
    private static final int NOTIFICATION_CODE = 200;
    private static final String NOTIFICATION_CHANNEL = "NOTIFICATION";
    private boolean initialState = true;

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
        mMarkerPoints = new ArrayList<>();

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
            createNotificationFinalizar();
            createNotificationChannelFinalizar();
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
    private void createNotificationFinalizar(){
        Intent reservaPasajero = new Intent(this, Activity_Roles.class);
        reservaPasajero.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, reservaPasajero, 0);
        String notificationMessage = " finalizo el viaje exitosamente";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL);
        notificationBuilder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        notificationBuilder.setContentTitle("NOTIFICACION DE CONDUCTOR");
        notificationBuilder.setColor(Color.BLUE);
        notificationBuilder.setContentText(notificationMessage);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(0,notificationBuilder.build());
    }
    private void createNotificationChannelFinalizar() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NOTIFICATION";
            String description = "NOTIFICATION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager)getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }
    }
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
        createNotification();
        createNotificationChannel();
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

                String keyF = snapshot.child("key").getValue(String.class);
                if (markerOrigin != null) {
                    markerOrigin.remove();
                }

                mOrigin = new LatLng(originLat,originLong);
                mDestination = new LatLng(destinationLat, destinationLong);

                markerOrigin = mMap.addMarker(new MarkerOptions().position(mOrigin).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(new MarkerOptions().position(mDestination));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mOrigin));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRefPasajeros = mDatabase.getReference(PATH_ROUTES).child(routeUid);
        mRefPasajeros.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("pasajeros").exists() && routeUid.equals(snapshot.child("key").getValue(String.class))){
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
                line.addAll(aline);
                mMap.addPolyline(line);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void createNotification(){
        Log.i("SUPERTAG","ENTRO A CREAR LA NOTIFICACION");
        String notificationMessage = " Se finalizo el viaje exitosamente";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL);
        notificationBuilder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        notificationBuilder.setContentTitle("NOTIFICACION DE CONDUCTOR");
        notificationBuilder.setColor(Color.BLUE);
        notificationBuilder.setContentText(notificationMessage);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(0,notificationBuilder.build());
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NOTIFICATION";
            String description = "NOTIFICATION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager)getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }
    }

}