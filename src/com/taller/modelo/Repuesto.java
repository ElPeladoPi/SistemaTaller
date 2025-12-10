package com.taller.modelo;

public class Repuesto {
    private int id;
    private String codigo; // NUEVO CAMPO
    private String descripcion;
    private String marca;
    private double precio;
    private int stock;

    // Constructor Completo (Para leer de DB)
    public Repuesto(int id, String codigo, String descripcion, String marca, double precio, int stock) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.marca = marca;
        this.precio = precio;
        this.stock = stock;
    }

    // Constructor Sin ID (Para crear nuevos)
    public Repuesto(String codigo, String descripcion, String marca, double precio, int stock) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.marca = marca;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters
    public int getId() { return id; }
    public String getCodigo() { return codigo; } // Nuevo Getter
    public String getDescripcion() { return descripcion; }
    public String getMarca() { return marca; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
}