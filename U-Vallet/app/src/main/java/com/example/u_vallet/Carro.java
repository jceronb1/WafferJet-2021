package com.example.u_vallet;

public class Carro {
    //----------------------------------------------
    //----------------  Attributes  ----------------
    //----------------------------------------------
    String nombreConductor;
    String marcaCarro;
    String placa;
    String modelo;
    int capacidad;
    int idConductor;

    //----------------------------------------------
    //---------------  Constructor  ----------------
    //----------------------------------------------
    public Carro(String nombreConductor, String marcaCarro, String placa,String modelo,int capacidad ,int idConductor) {
        this.nombreConductor = nombreConductor;
        this.marcaCarro = marcaCarro;
        this.placa = placa;
        this.idConductor = idConductor;
        this.modelo = modelo;
        this.capacidad = capacidad;
    }
}
