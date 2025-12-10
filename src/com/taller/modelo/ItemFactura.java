package com.taller.modelo;

public class ItemFactura {
    private int idReferencia; // El ID del repuesto o servicio original
    private String tipo;      // "REPUESTO" o "SERVICIO"
    private String descripcion;
    private double precioUnitario;
    private int cantidad;
    private double subtotal;

    public ItemFactura(int idReferencia, String tipo, String descripcion, double precioUnitario, int cantidad) {
        this.idReferencia = idReferencia;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.subtotal = precioUnitario * cantidad;
    }

    // Getters
    public int getIdReferencia() { return idReferencia; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public double getPrecioUnitario() { return precioUnitario; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }
}