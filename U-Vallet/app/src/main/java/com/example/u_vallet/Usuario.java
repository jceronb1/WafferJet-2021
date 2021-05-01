package com.example.u_vallet;

import java.util.Date;

public class Usuario {
    String username;
    String name;
    String contraseñaAntigua;
    String contraseñaNueva;
    Date fechaNacimiento;
    double telefono;
    String direccion;
    String contraseña;
    String confimarContraseña;

    public Usuario (String username, String name, String contraseñaAntigua, String contraseñaNueva, Date fechaNacimiento, double telefono, String direccion) {
        this.username = username;
        this.name = name;
        this.contraseñaAntigua = contraseñaAntigua;
        this.contraseñaNueva = contraseñaNueva;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    public Usuario (String username, String name, String contraseñaAntigua, String contraseñaNueva,  double telefono, String direccion) {
        this.username = username;
        this.name = name;
        this.contraseñaAntigua = contraseñaAntigua;
        this.contraseñaNueva = contraseñaNueva;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    public Usuario(){
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContraseñaAntigua() {
        return contraseñaAntigua;
    }

    public void setContraseñaAntigua(String contraseñaAntigua) {
        this.contraseñaAntigua = contraseñaAntigua;
    }

    public String getContraseñaNueva() {
        return contraseñaNueva;
    }

    public void setContraseñaNueva(String contraseñaNueva) {
        this.contraseñaNueva = contraseñaNueva;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public double getTelefono() {
        return telefono;
    }

    public void setTelefono(double telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getConfimarContraseña() {
        return confimarContraseña;
    }

    public void setConfimarContraseña(String confimarContraseña) {
        this.confimarContraseña = confimarContraseña;
    }
}
