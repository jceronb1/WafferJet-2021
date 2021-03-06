package com.example.u_vallet;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectionsJSONParser {
    /**
     * Receives a JSONObject and returns a list of lists containing latitude and
     * longitude
     */
    public directionsReturn parse(JSONObject jObject){

        directionsReturn retorno = new directionsReturn();

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        List<List<List<HashMap<String,String>>>> routes1 = new ArrayList<List<List<HashMap<String,String>>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONObject jLegsDuration = null;
        JSONObject jDuration = null;
        try {

            jRoutes = jObject.getJSONArray("routes");
            HashMap<String,String> hmd = new HashMap<String, String>();
            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();
                List path1 = new ArrayList<ArrayList<HashMap<String,String>>>();
                String key = null;
                // Log.d("legs",jLegs.toString());
                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jLegsDuration = (jLegs.getJSONObject(j));
                    String duration = jLegsDuration.getJSONObject("duration").getString("text");
                    Log.d("JSON DURATION", duration);
                    retorno.getDurations().add(duration);
                    hmd.put("dur", duration);
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    // Log.d("steps",jSteps.toString());
                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){

                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);
                        //  Log.d("polyline",polyline.toString());
                        /** Traversing all points */

                        for(int l=0;l<list.size();l++){
                            HashMap<String, String>    hm = new HashMap<String, String>();

                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );

                            path.add(hm);
                            //  Log.d("lat", Double.toString(((LatLng)list.get(l)).latitude));
                            //  Log.d("lng", Double.toString(((LatLng)list.get(l)).longitude));
                        }

                    }
                    path1.add(hmd);
                    path1.add(path);
                    //Log.d("JSON PATH",String.valueOf(path1.size()));
                    //path1.add(hmd);
                }
                routes1.add(path1);
            }
            /*Log.d("JSON ROUTE",String.valueOf(routes1.size()));
            ArrayList<HashMap<String,String>> directions = new ArrayList<HashMap<String, String>>();
            directions.add(hmd);
            List directions1 = new ArrayList<ArrayList<HashMap<String, String>>>();
            directions1.add(directions);
            routes1.add(directions1);*/
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        retorno.setRoutes1(routes1);
        return retorno;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}