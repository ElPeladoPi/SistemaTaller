package com.taller.modelo;

public class Vehiculo {
    private int id;
    private String patente;
    private String descripcion;
    private String propietario;

    public Vehiculo(int id, String patente, String descripcion, String propietario) {
        this.id = id;
        this.patente = patente;
        this.descripcion = descripcion;
        this.propietario = propietario;
    }

    public Vehiculo(String patente, String descripcion, String propietario) {
        this.patente = patente;
        this.descripcion = descripcion;
        this.propietario = propietario;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPatente() { return patente; }
    public String getDescripcion() { return descripcion; }
    public String getPropietario() { return propietario; }

    @Override
    public String toString() { return patente + " - " + descripcion; }
}