package com.taller.modelo;
import java.sql.Date;

public class Historial {
    private int id;
    public int idVehiculo; // Ojo: public para acceder facil desde el DAO
    private Date fecha;
    private int kilometraje;
    private String detalle;

    public Historial(int idVehiculo, Date fecha, int kilometraje, String detalle) {
        this.idVehiculo = idVehiculo;
        this.fecha = fecha;
        this.kilometraje = kilometraje;
        this.detalle = detalle;
    }

    public Historial(int id, int idVehiculo, Date fecha, int kilometraje, String detalle) {
        this.id = id;
        this.idVehiculo = idVehiculo;
        this.fecha = fecha;
        this.kilometraje = kilometraje;
        this.detalle = detalle;
    }

    // Getters
    public Date getFecha() { return fecha; }
    public int getKilometraje() { return kilometraje; }
    public String getDetalle() { return detalle; }
}