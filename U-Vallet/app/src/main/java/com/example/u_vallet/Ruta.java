package com.example.u_vallet;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Ruta {
    List<LatLng> route;
    LatLng originLocation;
    LatLng destinationLocation;
    String origen;
    String destino;
    String key;
    String uidConductor;
    String status;
    ArrayList<String> uidsPasajeros;

    public Ruta(List<LatLng> route, LatLng originLocation, LatLng destinationLocation, String key, String uidConductor, ArrayList<String> uidsPasajeros) {
        this.route = route;
        this.originLocation = originLocation;
        this.destinationLocation = destinationLocation;
        this.key = key;
        this.uidConductor = uidConductor;
        this.uidsPasajeros = uidsPasajeros;
    }

    public Ruta(List<LatLng> route, LatLng originLocation, LatLng destinationLocation, String key, String uidConductor, String origen, String destino, String status){
        this.route = route;
        this.originLocation = originLocation;
        this.destinationLocation = destinationLocation;
        this.key = key;
        this.uidConductor = uidConductor;
        this.origen = origen;
        this.destino = destino;
        this.status = status;
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

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
