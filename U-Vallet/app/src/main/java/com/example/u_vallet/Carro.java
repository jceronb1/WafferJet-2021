package com.example.u_vallet;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
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

    public Carro(String marcaCarro, String placa, String modelo, int capacidad) {
        this.marcaCarro = marcaCarro;
        this.placa = placa;
        this.modelo = modelo;
        this.capacidad = capacidad;
    }

    public Carro() {
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public String getMarcaCarro() {
        return marcaCarro;
    }

    public void setMarcaCarro(String marcaCarro) {
        this.marcaCarro = marcaCarro;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public int getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(int idConductor) {
        this.idConductor = idConductor;
    }
}
