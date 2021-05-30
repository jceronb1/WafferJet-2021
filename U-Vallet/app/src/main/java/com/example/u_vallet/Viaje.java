package com.example.u_vallet;

public class Viaje {
    //----------------------------------------------
    //----------------  Attributes  ----------------
    //----------------------------------------------
    String NombreDelConductor;
    String idConductor;
    String origen;
    String destino;
    String marca;
    String placa;
    int capacidad;
    int valorCupo;

    //----------------------------------------------
    //---------------- Constructors ----------------
    //----------------------------------------------
    public Viaje(){

    }
    public Viaje(String nombreDelConductor, String idConductor, String origen, String destino,int capacidad, int valorCupo,String marca,String placa) {
        NombreDelConductor = nombreDelConductor;
        this.idConductor = idConductor;
        this.origen = origen;
        this.destino = destino;
        this.capacidad = capacidad;
        this.valorCupo = valorCupo;
        this.marca = marca;
        this.placa = placa;
    }

    public String getNombreDelConductor() {
        return NombreDelConductor;
    }

    public void setNombreDelConductor(String nombreDelConductor) {
        NombreDelConductor = nombreDelConductor;
    }

    public String getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(String idConductor) {
        this.idConductor = idConductor;
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

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public int getValorCupo() {
        return valorCupo;
    }

    public void setValorCupo(int valorCupo) {
        this.valorCupo = valorCupo;
    }
}
