package com.taller.dao;

import com.taller.conexion.Conexion;
import com.taller.modelo.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    public boolean guardar(Servicio s) {
        String sql = "INSERT INTO servicios (nombre, costo) VALUES (?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setDouble(2, s.getCosto());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public List<Servicio> listar() {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM servicios";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Servicio s = new Servicio(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("costo"));
                lista.add(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public Servicio buscarPorId(int id) {
        String sql = "SELECT * FROM servicios WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Servicio(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("costo"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean actualizar(Servicio s) {
        String sql = "UPDATE servicios SET nombre=?, costo=? WHERE id=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setDouble(2, s.getCosto());
            ps.setInt(3, s.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM servicios WHERE id=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }
}