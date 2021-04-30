package com.example.u_vallet;

public class Carro {
    //----------------------------------------------
    //----------------  Attributes  ----------------
    //----------------------------------------------
    String nombreConductor;
    String marcaCarro;
    String placa;
    int idConductor;

    //----------------------------------------------
    //---------------  Constructor  ----------------
    //----------------------------------------------
    public Carro(String nombreConductor, String marcaCarro, String placa, int idConductor) {
        this.nombreConductor = nombreConductor;
        this.marcaCarro = marcaCarro;
        this.placa = placa;
        this.idConductor = idConductor;
    }
}
