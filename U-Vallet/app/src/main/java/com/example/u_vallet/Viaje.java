package com.example.u_vallet;

public class Viaje {
    //----------------------------------------------
    //----------------  Attributes  ----------------
    //----------------------------------------------
    String NombreDelConductor;
    int idConductor;
    String origen;
    String destino;
    Carro carroQueRealizaElViaje;

    //----------------------------------------------
    //---------------- Constructors ----------------
    //----------------------------------------------
    public Viaje(String nombreDelConductor, int idConductor, String origen, String destino, Carro carroQueRealizaElViaje) {
        NombreDelConductor = nombreDelConductor;
        this.idConductor = idConductor;
        this.origen = origen;
        this.destino = destino;
        this.carroQueRealizaElViaje = carroQueRealizaElViaje;
    }
}
