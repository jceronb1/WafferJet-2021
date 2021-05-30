package com.example.u_vallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Activity_CrearViaje_Maps extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SearchView searchViewOrigin;
    SearchView searchViewDestination;
    private LatLng mOrigin;
    private LatLng mDestination;
    private Polyline mPolyline;
    ArrayList<LatLng> mMarkerPoints;
    List<LatLng> routeSelected;
    private Marker markerOrigin;
    private Marker markerDestination;
    ArrayList<Polyline> polylines;

    LatLng originLatLng;
    LatLng destinationLatLng;
    String originLocation;
    String destinationLocation;

    // Location
    private static final int REQUEST_LOCATION = 410;
    private FusedLocationProviderClient fusedLocationProviderClient;
    String[] location_permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private double userLastKnownLocationLat;
    private double userLastKnownLocationLong;
    public String durationA;
    public String durationB;
    public String durationC;
    private TextView viewDuration;



    // Description of the method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__crear_viaje__maps);
        mAuth = FirebaseAuth.getInstance();

        viewDuration = (TextView)findViewById(R.id.viewDuration);
        polylines = new ArrayList<>();
        markerOrigin = null;
        markerDestination = null;
        searchViewOrigin = findViewById(R.id.sv_origin);

        searchViewDestination = findViewById(R.id.sv_destination);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        int originCloseButtonId = searchViewOrigin.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        int destinationButtonID = searchViewDestination.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView originCloseButton = (ImageView) searchViewOrigin.findViewById(originCloseButtonId);
        ImageView destinationCloseButton = (ImageView) searchViewDestination.findViewById(destinationButtonID);

        originCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchViewOrigin.setQuery("", false);
                if (markerOrigin != null) {
                    mMarkerPoints.remove(markerOrigin.getPosition());
                    markerOrigin.remove();
                    markerOrigin = null;
                }
                if (polylines != null) {
                    for (int i = 0; i < polylines.size(); i++) {
                        polylines.get(i).remove();
                    }
                }
            }
        });

        destinationCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchViewDestination.setQuery("", false);
                if (markerDestination != null) {
                    mMarkerPoints.remove(markerDestination.getPosition());
                    markerDestination.remove();
                    markerDestination = null;
                }
                if (polylines != null) {
                    for (int i = 0; i < polylines.size(); i++) {
                        polylines.get(i).remove();
                    }
                }
            }
        });
        searchViewOrigin.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (markerOrigin != null) {
                    markerOrigin.remove();
                }
                if (mMarkerPoints.size() > 1) {
                    mMarkerPoints.clear();
                }
                originLocation = searchViewOrigin.getQuery().toString();
                if (!originLocation.contains(",")) {
                    originLocation = originLocation + ", Bogotá";
                }
                List<Address> addressListOrigin = null;

                if (originLocation != null || !originLocation.equals("")) {
                    Geocoder geocoder = new Geocoder(Activity_CrearViaje_Maps.this);
                    try {
                        addressListOrigin = geocoder.getFromLocationName(originLocation, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address addressOrigin = addressListOrigin.get(0);
                    originLatLng = new LatLng(addressOrigin.getLatitude(), addressOrigin.getLongitude());

                    if (null != markerOrigin) {
                        markerOrigin.remove();
                    }
                    markerOrigin = mMap.addMarker(new MarkerOptions().position(originLatLng).title(originLocation));
                    Log.d("LatLng", String.valueOf(addressOrigin.getLatitude()) + "," + String.valueOf(addressOrigin.getLongitude()));
                    /*markerOrigin.position(originLatLng).title(originLocation);
                    mMap.addMarker(markerOrigin);*/
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLatLng, 17));
                    mMarkerPoints.add(originLatLng);

                    if (mMarkerPoints.size() >= 2) {
                        mOrigin = originLatLng;
                        drawRoute();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        searchViewDestination.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (markerDestination != null) {
                    markerDestination.remove();
                }
                if (mMarkerPoints.size() > 1) {
                    mMarkerPoints.clear();
                }

                destinationLocation = searchViewDestination.getQuery().toString();
                if (!destinationLocation.contains(",")) {
                    destinationLocation = destinationLocation + ", Bogotá";
                }
                List<Address> addressListDestination = null;

                if (destinationLocation != null || !destinationLocation.equals("")) {
                    Geocoder geocoder = new Geocoder(Activity_CrearViaje_Maps.this);
                    try {

                        addressListDestination = geocoder.getFromLocationName(destinationLocation, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address addressDestination = addressListDestination.get(0);
                    destinationLatLng = new LatLng(addressDestination.getLatitude(), addressDestination.getLongitude());
                    markerDestination = mMap.addMarker(new MarkerOptions().position(destinationLatLng).title(destinationLocation));
                    Log.d("LatLng", String.valueOf(addressDestination.getLatitude()) + "," + String.valueOf(addressDestination.getLongitude()));
                    /*mMap.addMarker(markerDestination);*/
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 17));
                    mMarkerPoints.add(destinationLatLng);

                    if (mMarkerPoints.size() >= 2) {
                        mOrigin = originLatLng;
                        mDestination = destinationLatLng;
                        drawRoute();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        mapFragment.getMapAsync(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Activity_Seleccionar_Carro.class);

                for (int i = 0; i < polylines.size(); i++) {
                    if(polylines.get(i).getColor() == -10052106) {
                        routeSelected = polylines.get(i).getPoints();
                    }
                }
                Ruta ruta = new Ruta();
                ruta.setRoute(routeSelected);
                ruta.setOriginLocation(mOrigin);
                ruta.setDestinationLocation(mDestination);
                ruta.setUidConductor(mAuth.getUid());
                mDatabase = FirebaseDatabase.getInstance().getReference("routes");
                String key = mDatabase.push().getKey();
                ruta.setKey(key);
                mDatabase = FirebaseDatabase.getInstance().getReference("routes/"+key);
                mDatabase.setValue(ruta);
                Intent intent2 = new Intent(v.getContext(), Activity_ExplorarViajes.class);
                intent.putExtra("Route", key);
                intent.putExtra("direccionO",searchViewOrigin.getQuery().toString());
                intent.putExtra("direccionD", searchViewDestination.getQuery().toString());
                startActivity(intent);
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        mMarkerPoints = new ArrayList<>();



        //--------  Get last known location from the user  --------------   TODO : Get current location, not the last known
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    // Description of the method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng mapaInicial = new LatLng(4.6584796, -74.0934579);
        mMap.moveCamera(CameraUpdateFactory.newLatLng( mapaInicial));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());

        //--------   Move the camera to the location of the user  ------------
        // Check if Location permissions are already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get last know user's location
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        if (markerOrigin != null)
                            markerOrigin.remove();

                        mMarkerPoints.clear();
                        // Get Longitude and Latitude
                        userLastKnownLocationLat = location.getLatitude();
                        userLastKnownLocationLong = location.getLongitude();

                        LatLng mapaInicial = new LatLng(userLastKnownLocationLat, userLastKnownLocationLong);
                        mMarkerPoints.add(mapaInicial);
                        MarkerOptions options = new MarkerOptions();
                        options.position(mapaInicial);
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                        List<Address> addresses = null;
                        String completeAdd = null;
                        String finalAdd = null;
                        try {
                            addresses = geocoder.getFromLocation(userLastKnownLocationLat, userLastKnownLocationLong, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        completeAdd = addresses.get(0).getAddressLine(0);
                        String[] addName = completeAdd.split(",");
                        finalAdd = addName[0];

                        searchViewOrigin.setQuery(finalAdd, false);
                        originLatLng = mapaInicial;
                        markerOrigin = mMap.addMarker(options.title(finalAdd));
                        // Center the map camera into current user location
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(mapaInicial));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                    } else {
                        LatLng mapaInicial = new LatLng(4.6584796, -74.0934579);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng( mapaInicial));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                        Log.i("Location", "Location is null");
                    }
                }
            });
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Toast.makeText(this, "Location permissions are required", Toast.LENGTH_SHORT).show();
        } else {
            // If permissions have not been granted, request required permissions
            ActivityCompat.requestPermissions(this, location_permissions, REQUEST_LOCATION);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // Already two locations
                if (mMarkerPoints.size() > 1) {
                    mMarkerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                mMarkerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (mMarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (mMarkerPoints.size() == 2 && markerOrigin == null) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (mMarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
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

                if (searchViewOrigin.getQuery().length() > 0 && searchViewDestination.getQuery().length() > 0) {
                    searchViewOrigin.setQuery(finalAdd, false);
                    searchViewDestination.setQuery("", false);
                } else if (searchViewOrigin.getQuery().length() == 0) {
                    searchViewOrigin.setQuery(finalAdd, false);
                } else {
                    searchViewDestination.setQuery(finalAdd, false);
                }
                if (mMarkerPoints.size() == 1) {
                    originLatLng = point;
                    markerOrigin = mMap.addMarker(options.title(finalAdd));
                } else if (mMarkerPoints.size() == 2 && markerOrigin == null) {
                    originLatLng = point;
                    markerOrigin = mMap.addMarker(options.title(finalAdd));
                } else if (mMarkerPoints.size() == 2) {
                    destinationLatLng = point;
                    markerDestination = mMap.addMarker(options.title(finalAdd));
                }

                // Checks, whether start and end locations are captured
                if (mMarkerPoints.size() >= 2) {
                    mOrigin = originLatLng;
                    mDestination = destinationLatLng;
                    drawRoute();
                }

            }
        });
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Log.e("Polyline position", " -- " + polyline.getTag());
                Log.e("Polyline color", String.valueOf(polyline.getColor()));
                Log.e("Polyline colors", String.valueOf(R.color.grey));


                if(polyline.getTag().equals("lineA")){
                    //Toast.makeText(getBaseContext(), "durationA", Toast.LENGTH_SHORT).show();
                    viewDuration.setText("Tiempo: "+durationA);
                }else if(polyline.getTag().equals("lineB")){
                    //Toast.makeText(getBaseContext(), "durationB", Toast.LENGTH_SHORT).show();
                    viewDuration.setText("Tiempo: "+durationB);
                }else{
                    //Toast.makeText(getBaseContext(), "durationC", Toast.LENGTH_SHORT).show();
                    viewDuration.setText("Tiempo: "+durationC);
                }

                //polyline.getId();
                Log.e("Polyline id", polyline.getId());
                //ArrayList<Polyline> polylinesAux = new ArrayList<>();
                //polylinesAux = polylines;
                for (int i = 0; i < polylines.size(); i++) {
                    //polylines.get(i).getId();
                    if (polylines.get(i).getId().equals(polyline.getId())) {
                        polyline.setColor(-10052106); //ligthblue
                        //polyline.remove();
                    } else {
                        Log.e("Polyline id FOR", polylines.get(i).getId());
                        polylines.get(i).setColor(-4473409); //grey
                    }

                }
                //onButtonShowPopupWindowClick("  " + polyline.getTag());
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Line to erase after
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if permissions were granted
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location permissions granted", Toast.LENGTH_SHORT).show();
                    // Get last know user's location
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                if (markerOrigin != null)
                                    markerOrigin.remove();

                                mMarkerPoints.clear();
                                // Get Longitude and Latitude
                                userLastKnownLocationLat = location.getLatitude();
                                userLastKnownLocationLong = location.getLongitude();

                                LatLng mapaInicial = new LatLng(userLastKnownLocationLat, userLastKnownLocationLong);
                                mMarkerPoints.add(mapaInicial);
                                Log.d("DEBCREARV_123", String.valueOf(mMarkerPoints.size()));
                                MarkerOptions options = new MarkerOptions();
                                options.position(mapaInicial);
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                                List<Address> addresses = null;
                                String completeAdd = null;
                                String finalAdd = null;
                                Geocoder geocoder;
                                geocoder = new Geocoder(Activity_CrearViaje_Maps.this, Locale.getDefault());
                                try {
                                    addresses = geocoder.getFromLocation(userLastKnownLocationLat, userLastKnownLocationLong, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                completeAdd = addresses.get(0).getAddressLine(0);
                                String[] addName = completeAdd.split(",");
                                finalAdd = addName[0];

                                searchViewOrigin.setQuery(finalAdd, false);
                                originLatLng = mapaInicial;
                                markerOrigin = mMap.addMarker(options.title(finalAdd));
                                // Center the map camera into current user location
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(mapaInicial));
                                mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                            } else {
                                Log.i("Location", "Location is null");
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Location services were denied by the user", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    // Description of the method
    private void drawRoute() {

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(mOrigin, mDestination);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);


    }

    // Description of the Method
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Key
        String key = "key=" + getString(R.string.google_maps_key);
        Log.i("API KEY"," -> "+ key);

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&alternatives=true&region=co&"+key;
        Log.i("API KEY","Parameters -> "+parameters);
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask1 parserTask = new ParserTask1();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask1 extends AsyncTask<String, Integer, List<List<List<HashMap<String, String>>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<List<HashMap<String, String>>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<List<HashMap<String, String>>>> routes = null;
            List<String> durations = new ArrayList<>();
            List<List<HashMap<String, String>>> routesAux = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject).getRoutes1();
                durations = parser.parse(jObject).getDurations();
                int indice = 0;
                for(String duration : durations){
                    Log.i("Tiempo: ",duration);
                    if(indice==0){
                        durationA = duration;
                    }else if(indice==1){
                        durationB = duration;
                    }else{
                        durationC = duration;
                    }
                    indice++;
                }
                /*int tam = routes.size()-1;
                routesAux = routes.get(tam);
                routes.remove(tam);
                Log.d("JSON", routesAux.toString());*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<List<HashMap<String, String>>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = new PolylineOptions();

            PolylineOptions lineOptions1 = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            Integer size1 = 0;
            Integer size2 = 0;
            Integer size3 = 0;
//            Log.d(SHETTY, "onPostExecute: result set "+result.size());


            List<LatLng> aline1 = new ArrayList<LatLng>();
            List<LatLng> aline2 = new ArrayList<LatLng>();
            List<LatLng> aline3 = new ArrayList<LatLng>();


            if (result != null) {
                int i = 0;

                while (i < result.size()) {

                    //  for(int i=0;i<result.size();i++){
                    //result.size()
                    //int g= i-1;
                    points = new ArrayList<LatLng>();
                    // lineOptions = new PolylineOptions();
                    // if(i==1){

                    // }else{
                    List<List<HashMap<String, String>>> path1 = result.get(i);

                    HashMap<String, String>pathAux = (HashMap<String, String>) path1.get(0);
                    Log.i("JSON AUX",String.valueOf(pathAux));
                    path1.remove(0);
                    duration = pathAux.get("dur");


                    //path1.remove(0);


                    for (int s = 0; s < path1.size(); s++) {
                        Log.d("pathsize1", path1.size() + "");

                        // Fetching i-th route
                        List<HashMap<String, String>> path = path1.get(s);
                        Log.d("pathsize", path.size() + "");
                        //Log.d("JSON B", point.get("dur"));
                        // Fetching all the points in i-th route
                        String duracion = "duro";
                        for (int j = 0; j < path.size(); j++) {
                            lineOptions1 = new PolylineOptions();
                            HashMap<String, String> point = path.get(j);
                           /*String pathAux = point.get("dur");
                            point.remove("dur");
                            Log.d("JSON AUX",pathAux);*/
                            /*points = new ArrayList<LatLng>();
                            if(j==0){    // Get distance from the list
                                distance = (String)point.get("distance");
                                continue;
                            }else if(j==1){ // Get duration from the list
                                duration = (String)point.get("duration");
                                continue;
                            }*/

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));

                            LatLng position = new LatLng(lat, lng);
                            Log.d("latlng", position.toString());
                            points.add(position);


                        }
                        Log.i("JSON DURATION", duracion);
                        //                lineOptions.addAll(points);
                        //                lineOptions.width(5);
                        //                lineOptions.color(Color.BLUE);
                        //                map.addPolyline(lineOptions);

                    }
                    // }
                    if (i == 0) {


//                        line1.addAll(points);
//                        mMap.addPolyline(line1);

                        size1 = points.size();

                        aline1.addAll(points);
                    } else if (i == 1) {


//                        line2.addAll(points);
//                        mMap.addPolyline(line2);

                        aline2.addAll(points);
                        size2 = points.size();
                    } else if (i == 2) {


//                        line3.addAll(points);
//                        mMap.addPolyline(line3);

                        aline3.addAll(points);
                        size3 = points.size();
                    }
                    // Adding all the points in the route to LineOptions
                    i++;


                }
                // Drawing polyline in the Google Map for the i-th route
                // map.addPolyline(lineOptions);
            }
            if (size3 != 0)
            {

                if ((size1 > size2 && size1 > size3)) {
                    if (size2 > size3) {
                        PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);
                        Polyline lineA = mMap.addPolyline(line1);
                        Polyline lineB = mMap.addPolyline(line2);
                        Polyline lineC = mMap.addPolyline(line3);
                        lineA.setClickable(true);
                        lineA.setTag("lineA");
                        lineB.setClickable(true);
                        lineB.setTag("lineB");
                        lineC.setClickable(true);
                        lineC.setTag("lineC");
                        Log.d("Polyline", String.valueOf(lineA.getPoints()+ String.valueOf(lineA.getWidth())));
                        polylines.add(lineA);
                        polylines.add(lineB);
                        polylines.add(lineC);
                    } else {

                        PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);

                        Polyline lineA = mMap.addPolyline(line1);
                        Polyline lineC =mMap.addPolyline(line3);

                        Polyline lineB  = mMap.addPolyline(line2);
                        lineA.setClickable(true);
                        lineA.setTag("lineA");
                        lineB.setClickable(true);
                        lineB.setTag("lineB");
                        lineC.setClickable(true);
                        lineC.setTag("lineC");

                        polylines.add(lineA);
                        polylines.add(lineB);
                        polylines.add(lineC);
                    }
                } else if ((size2 > size1 && size2 > size3)) {
                    if (size1 > size3) {
                        PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);

                        Polyline lineA = mMap.addPolyline(line1);
                        Polyline lineB = mMap.addPolyline(line2);
                        Polyline lineC = mMap.addPolyline(line3);
                        lineA.setClickable(true);
                        lineA.setTag("lineA");
                        lineB.setClickable(true);
                        lineB.setTag("lineB");
                        lineC.setClickable(true);
                        lineC.setTag("lineC");

                        polylines.add(lineA);
                        polylines.add(lineB);
                        polylines.add(lineC);

                    } else {

                        PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);


                        Polyline lineB = mMap.addPolyline(line2);
                        Polyline lineC = mMap.addPolyline(line3);

                        Polyline lineA =mMap.addPolyline(line1);
                        lineA.setClickable(true);
                        lineA.setTag("lineA");
                        lineB.setClickable(true);
                        lineB.setTag("lineB");
                        lineC.setClickable(true);
                        lineC.setTag("lineC");
                        polylines.add(lineA);
                        polylines.add(lineB);
                        polylines.add(lineC);

                    }
                } else if ((size3 > size1 && size3 > size2)) {
                    if (size1 > size2) {
                        PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);


                        Polyline lineC = mMap.addPolyline(line3);
                        Polyline lineA = mMap.addPolyline(line1);
                        Polyline lineB = mMap.addPolyline(line2);

                        lineA.setClickable(true);
                        lineA.setTag("lineA");
                        lineB.setClickable(true);
                        lineB.setTag("lineB");
                        lineC.setClickable(true);
                        lineC.setTag("lineC");

                        polylines.add(lineA);
                        polylines.add(lineB);
                        polylines.add(lineC);

                    } else {
                        PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);

                        Polyline lineC = mMap.addPolyline(line3);
                        Polyline lineB = mMap.addPolyline(line2);
                        Polyline lineA = mMap.addPolyline(line1);

                        lineA.setClickable(true);
                        lineA.setTag("lineA");
                        lineB.setClickable(true);
                        lineB.setTag("lineB");
                        lineC.setClickable(true);
                        lineC.setTag("lineC");

                        polylines.add(lineA);
                        polylines.add(lineB);
                        polylines.add(lineC);

                    }
                } else {
                    System.out.println("ERROR!");
                }

            }else if(size2!=0)
            {
                if(size1>size2){

                    PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                    PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));


                    line1.addAll(aline1);
                    line2.addAll(aline2);

                    Polyline lineA = mMap.addPolyline(line1);
                    Polyline lineB = mMap.addPolyline(line2);

                    lineA.setClickable(true);
                    lineA.setTag("lineA");
                    lineB.setClickable(true);
                    lineB.setTag("lineB");
                    polylines.add(lineA);
                    polylines.add(lineB);

                }else
                {
                    PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                    PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));

                    line1.addAll(aline1);
                    line2.addAll(aline2);

                    Polyline lineB = mMap.addPolyline(line2);
                    Polyline lineA = mMap.addPolyline(line1);

                    lineA.setClickable(true);
                    lineA.setTag("lineA");
                    lineB.setClickable(true);
                    lineB.setTag("lineB");
                    polylines.add(lineA);
                    polylines.add(lineB);
                }



            }
            else if(size1!=0){
                PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                line1.addAll(aline1);
                Polyline lineA = mMap.addPolyline(line1);

                lineA.setClickable(true);
                lineA.setTag("lineA");
                polylines.add(lineA);
            }

            viewDuration.setText("Tiempo: "+durationA);



        }


    }

}