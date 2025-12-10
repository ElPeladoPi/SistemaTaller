package com.taller.dao;

import com.taller.conexion.Conexion;
import com.taller.modelo.Repuesto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepuestoDAO {

    // GUARDAR
    public boolean guardar(Repuesto r) {
        String sql = "INSERT INTO repuestos (codigo, descripcion, marca, precio, stock) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getCodigo());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getMarca());
            ps.setDouble(4, r.getPrecio());
            ps.setInt(5, r.getStock());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // BUSQUEDA GENERAL (Por texto)
    public List<Repuesto> buscar(String busqueda) {
        List<Repuesto> lista = new ArrayList<>();
        String sql = "SELECT * FROM repuestos WHERE codigo LIKE ? OR descripcion LIKE ? OR marca LIKE ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + busqueda + "%");
            ps.setString(2, "%" + busqueda + "%");
            ps.setString(3, "%" + busqueda + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Repuesto(rs.getInt("id"), rs.getString("codigo"), rs.getString("descripcion"), rs.getString("marca"), rs.getDouble("precio"), rs.getInt("stock")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    // --- NUEVO MÉTODO: BUSCAR POR ID EXACTO ---
    public Repuesto buscarPorId(int id) {
        String sql = "SELECT * FROM repuestos WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Repuesto(rs.getInt("id"), rs.getString("codigo"), rs.getString("descripcion"), rs.getString("marca"), rs.getDouble("precio"), rs.getInt("stock"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null; // No encontrado
    }

    // ACTUALIZAR
    public boolean actualizar(Repuesto r) {
        String sql = "UPDATE repuestos SET codigo=?, descripcion=?, marca=?, precio=?, stock=? WHERE id=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getCodigo());
            ps.setString(2, r.getDescripcion());
            ps.setString(3, r.getMarca());
            ps.setDouble(4, r.getPrecio());
            ps.setInt(5, r.getStock());
            ps.setInt(6, r.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    // ELIMINAR
    public boolean eliminar(int id) {
        String sql = "DELETE FROM repuestos WHERE id=?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }
}