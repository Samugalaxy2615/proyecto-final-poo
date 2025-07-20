package com.example.mytiendita;


// Dentro de Database.java o como clase separada com.example.mytiendita.Producto
 public class Producto {
     private String nombre;
     private double precio;
     private int stock;

     public Producto() {
         // Constructor vacío requerido por Firebase
     }

     public Producto(String nombre, double precio, int stock) {
         this.nombre = nombre;
         this.precio = precio;
         this.stock = stock;
     }

     public String getNombre() {
         return nombre;
     }
     public double getPrecio() {
         return precio;
     }
     public int getStock() {
         return stock;
     }
     public void setNombre(String nombre) {
         this.nombre = nombre;
     }
     public void setPrecio(double precio) {
         this.precio = precio;
     }
     public void setStock(int stock) {
         this.stock = stock;
     }
 }