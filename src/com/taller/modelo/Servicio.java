package com.taller.modelo;

public class Servicio {
    private int id;
    private String nombre;
    private double costo;

    public Servicio() {}

    public Servicio(String nombre, double costo) {
        this.nombre = nombre;
        this.costo = costo;
    }

    public Servicio(int id, String nombre, double costo) {
        this.id = id;
        this.nombre = nombre;
        this.costo = costo;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }
}