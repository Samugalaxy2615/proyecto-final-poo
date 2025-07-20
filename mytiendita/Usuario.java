package com.example.mytiendita;

public class Usuario {
    private String nombre;
    private String correo;
    private double saldo;

    public Usuario() {} // Necesario para Firebase

    public Usuario(String nombre, String correo, double saldo) {
        this.nombre = nombre;
        this.correo = correo;
        this.saldo = saldo;
    }

    // Getters y setters
}