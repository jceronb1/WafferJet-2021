package com.example.u_vallet;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Activity_CrearViaje_Maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SearchView searchViewOrigin;
    SearchView searchViewDestination;
    private LatLng mOrigin;
    private LatLng mDestination;
    private Polyline mPolyline;
    ArrayList<LatLng> mMarkerPoints;
    private Marker markerOrigin;
    private Marker markerDestination;

    LatLng originLatLng;
    LatLng destinationLatLng;
    String originLocation;
    String destinationLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__crear_viaje__maps);

        markerOrigin = null;
        markerDestination = null;
        searchViewOrigin = findViewById(R.id.sv_origin);
        searchViewDestination = findViewById(R.id.sv_destination);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        searchViewOrigin.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(mMarkerPoints.size()>1){
                    mMarkerPoints.clear();
                }
                originLocation = searchViewOrigin.getQuery().toString();
                List<Address> addressListOrigin = null;

                if(originLocation != null || !originLocation.equals("")) {
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
                    Log.d("LatLng", String.valueOf(addressOrigin.getLatitude())+","+String.valueOf(addressOrigin.getLongitude()));
                    /*markerOrigin.position(originLatLng).title(originLocation);
                    mMap.addMarker(markerOrigin);*/
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLatLng, 17));
                    mMarkerPoints.add(originLatLng);
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
                if(mMarkerPoints.size()>1){
                    mMarkerPoints.clear();
                }
                destinationLocation = searchViewDestination.getQuery().toString();
                List<Address> addressListDestination = null;

                if(destinationLocation != null || !destinationLocation.equals("")){
                    Geocoder geocoder = new Geocoder(Activity_CrearViaje_Maps.this);
                    try{
                        addressListDestination = geocoder.getFromLocationName(destinationLocation, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address addressDestination = addressListDestination.get(0);
                    destinationLatLng = new LatLng(addressDestination.getLatitude(), addressDestination.getLongitude());
                    markerDestination = mMap.addMarker(new MarkerOptions().position(destinationLatLng).title(destinationLocation));
                    Log.d("LatLng", String.valueOf(addressDestination.getLatitude())+","+String.valueOf(addressDestination.getLongitude()));
                    /*mMap.addMarker(markerDestination);*/
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 17));
                    mMarkerPoints.add(destinationLatLng);

                    if(mMarkerPoints.size() >= 2){
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
                Intent intent = new Intent(v.getContext(), Activity_CrearViaje.class);
                intent.putExtra("Origen", originLocation);
                intent.putExtra("Destino", destinationLocation);
                startActivity(intent);
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        mMarkerPoints = new ArrayList<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double latitudeInicial = 4.6584796;
        double longitudeInicial = -74.0934579;
        LatLng mapaInicial = new LatLng(latitudeInicial, longitudeInicial);
        mMap.moveCamera(CameraUpdateFactory.newLatLng( mapaInicial));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // Already two locations
                if(mMarkerPoints.size()>1){
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
                if(mMarkerPoints.size()==1){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else if(mMarkerPoints.size()==2){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if(mMarkerPoints.size() >= 2){
                    mOrigin = mMarkerPoints.get(0);
                    mDestination = mMarkerPoints.get(1);
                    drawRoute();
                }

            }
        });
    }

    private void drawRoute(){

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(mOrigin, mDestination);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


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

    /** A class to parse the Google Directions in JSON format */
    /*private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            }else
                Toast.makeText(getApplicationContext(),"No route is found", Toast.LENGTH_LONG).show();
        }
    }*/
    private class ParserTask1 extends AsyncTask<String, Integer, List<List<List<HashMap<String, String>>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<List<HashMap<String, String>>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<List<HashMap<String, String>>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
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

                    for (int s = 0; s < path1.size(); s++) {
                        Log.d("pathsize1", path1.size() + "");

                        // Fetching i-th route
                        List<HashMap<String, String>> path = path1.get(s);
                        Log.d("pathsize", path.size() + "");
                        //Log.d("JSON B", point.get("dur"));
                        // Fetching all the points in i-th route

                        for (int j = 0; j < path.size(); j++) {
                            lineOptions1 = new PolylineOptions();
                            HashMap<String, String> point = path.get(j);
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
                            /*String durationB = point.get("dur");
                            Log.d("JSON DURATION", durationB);*/
                            LatLng position = new LatLng(lat, lng);
                            Log.d("latlng", position.toString());
                            points.add(position);


                        }
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
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getResources().getColor(R.color.grey));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getResources().getColor(R.color.lightblue));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);

                        mMap.addPolyline(line1);
                        mMap.addPolyline(line2);
                        mMap.addPolyline(line3);
                    } else {

                        PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);

                        mMap.addPolyline(line1);
                        mMap.addPolyline(line3);

                        mMap.addPolyline(line2);

                    }
                } else if ((size2 > size1 && size2 > size3)) {
                    if (size1 > size3) {
                        PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);

                        mMap.addPolyline(line1);
                        mMap.addPolyline(line2);

                        mMap.addPolyline(line3);


                    } else {

                        PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);


                        mMap.addPolyline(line2);
                        mMap.addPolyline(line3);

                        mMap.addPolyline(line1);

                    }
                } else if ((size3 > size1 && size3 > size2)) {
                    if (size1 > size2) {
                        PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);


                        mMap.addPolyline(line3);
                        mMap.addPolyline(line1);
                        mMap.addPolyline(line2);

                    } else {
                        PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                        PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));
                        PolylineOptions line3 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));

                        line1.addAll(aline1);
                        line2.addAll(aline2);
                        line3.addAll(aline3);

                        mMap.addPolyline(line3);
                        mMap.addPolyline(line2);

                        mMap.addPolyline(line1);

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

                    mMap.addPolyline(line1);
                    mMap.addPolyline(line2);

                }else
                {
                    PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                    PolylineOptions line2 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.grey));

                    line1.addAll(aline1);
                    line2.addAll(aline2);

                    mMap.addPolyline(line2);
                    mMap.addPolyline(line1);
                }



            }
            else if(size1!=0){
                PolylineOptions line1 = new PolylineOptions().width(8).color(mapFragment.getActivity().getResources().getColor(R.color.lightblue));
                line1.addAll(aline1);
                mMap.addPolyline(line1);
            }

        }




    }

}