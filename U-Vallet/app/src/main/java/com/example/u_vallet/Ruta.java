package com.example.u_vallet;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Ruta {
    List<LatLng> route;
    LatLng originLocation;
    LatLng destinationLocation;
    String key;
    String uidConductor;
    ArrayList<String> uidsPasajeros;

    public Ruta(List<LatLng> route, LatLng originLocation, LatLng destinationLocation, String key, String uidConductor, ArrayList<String> uidsPasajeros) {
        this.route = route;
        this.originLocation = originLocation;
        this.destinationLocation = destinationLocation;
        this.key = key;
        this.uidConductor = uidConductor;
        this.uidsPasajeros = uidsPasajeros;
    }

    public Ruta(List<LatLng> route, LatLng originLocation, LatLng destinationLocation, String key, String uidConductor){
        this.route = route;
        this.originLocation = originLocation;
        this.destinationLocation = destinationLocation;
        this.key = key;
        this.uidConductor = uidConductor;
    }

    public Ruta(){

    }
    public List<com.google.android.gms.maps.model.LatLng> getRoute() {
        return route;
    }

    public void setRoute(List<LatLng> ruta) {
        this.route = ruta;
    }

    public LatLng getOriginLocation() {
        return originLocation;
    }

    public void setOriginLocation(LatLng originLocation) {
        this.originLocation = originLocation;
    }

    public LatLng getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(LatLng destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUidConductor() {
        return uidConductor;
    }

    public void setUidConductor(String uidConductor) {
        this.uidConductor = uidConductor;
    }

    public ArrayList<String> getUidsPasajeros() {
        return uidsPasajeros;
    }

    public void setUidsPasajeros(ArrayList<String> uidsPasajeros) {
        this.uidsPasajeros = uidsPasajeros;
    }

}
