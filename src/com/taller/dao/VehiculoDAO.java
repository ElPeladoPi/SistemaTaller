package com.taller.dao;

import com.taller.conexion.Conexion;
import com.taller.modelo.Historial;
import com.taller.modelo.Vehiculo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    public boolean guardar(Vehiculo v) {
        String sql = "INSERT INTO vehiculos (patente, descripcion, propietario) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPatente().toUpperCase());
            ps.setString(2, v.getDescripcion());
            ps.setString(3, v.getPropietario());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public Vehiculo buscarPorPatente(String patente) {
        String sql = "SELECT * FROM vehiculos WHERE patente = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, patente.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Vehiculo(rs.getInt("id"), rs.getString("patente"), rs.getString("descripcion"), rs.getString("propietario"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // --- NUEVO: LISTAR TODOS LOS VEHICULOS ---
    public List<Vehiculo> listarTodos() {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos ORDER BY patente ASC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Vehiculo(rs.getInt("id"), rs.getString("patente"), rs.getString("descripcion"), rs.getString("propietario")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public boolean agregarHistorial(Historial h) {
        String sql = "INSERT INTO historial_vehiculos (id_vehiculo, fecha, kilometraje, detalle_servicio) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, h.idVehiculo);
            ps.setDate(2, h.getFecha());
            ps.setInt(3, h.getKilometraje());
            ps.setString(4, h.getDetalle());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public List<Historial> listarHistorial(int idVehiculo) {
        List<Historial> lista = new ArrayList<>();
        String sql = "SELECT * FROM historial_vehiculos WHERE id_vehiculo = ? ORDER BY fecha DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVehiculo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Historial(rs.getInt("id"), rs.getInt("id_vehiculo"), rs.getDate("fecha"), rs.getInt("kilometraje"), rs.getString("detalle_servicio")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }
}