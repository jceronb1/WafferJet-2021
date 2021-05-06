package com.example.u_vallet;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Usuario {
    String uid;
    String username;
    String name;
    String contraseñaAntigua;
    String contraseñaNueva;
    Date fechaNacimiento;
    long telefono;
    String direccion;
    String contraseña;
    String confimarContraseña;

    public Usuario (String uid, String username, String name, String contraseñaAntigua, String contraseñaNueva, Date fechaNacimiento, long telefono, String direccion) {
        this.uid = uid;
        this.username = username;
        this.name = name;
        this.contraseñaAntigua = contraseñaAntigua;
        this.contraseñaNueva = contraseñaNueva;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    public Usuario (String uid, String username, String name, String contraseña, Date fechaNacimiento, long telefono, String direccion) {
        this.uid = uid;
        this.username = username;
        this.name = name;
        this.contraseña = contraseña;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    public Usuario (String username, String name, String contraseñaAntigua, String contraseñaNueva,  long telefono, String direccion) {
        this.username = username;
        this.name = name;
        this.contraseñaAntigua = contraseñaAntigua;
        this.contraseñaNueva = contraseñaNueva;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    public Usuario(){
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public long getTelefono() {
        return telefono;
    }

    public void setTelefono(long telefono) {
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
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("username",username);
        result.put("name",name);
        result.put("contraseña",contraseña);
        result.put("fechaNacimiento",fechaNacimiento);
        result.put("telefono",telefono);
        result.put("direccion",direccion);
        return result;
    }
}
