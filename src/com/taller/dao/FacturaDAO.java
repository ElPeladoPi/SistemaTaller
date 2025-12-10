package com.taller.dao;

import com.taller.conexion.Conexion;
import com.taller.modelo.ItemFactura;
import java.sql.*;
import java.util.List;

public class FacturaDAO {

    public boolean registrarVenta(String cliente, List<ItemFactura> items, double total) {
        Connection con = null;
        try {
            con = Conexion.getConexion();
            con.setAutoCommit(false); // Inicio transacción

            // 1. Cabecera Factura
            String sqlFactura = "INSERT INTO facturas (cliente, total) VALUES (?, ?)";
            PreparedStatement psFact = con.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS);
            psFact.setString(1, cliente);
            psFact.setDouble(2, total);
            psFact.executeUpdate();

            int idFactura = 0;
            ResultSet rs = psFact.getGeneratedKeys();
            if (rs.next()) idFactura = rs.getInt(1);

            // 2. Detalles y Stock (CON SEGURIDAD)
            String sqlDetalle = "INSERT INTO detalles_factura (id_factura, tipo, descripcion, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?, ?)";

            // TRUCO DE SEGURIDAD: "AND stock >= ?"
            // Esto hace que si no hay suficiente stock, la base de datos no actualice nada (devuelve 0 filas)
            String sqlUpdateStock = "UPDATE repuestos SET stock = stock - ? WHERE id = ? AND stock >= ?";

            PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
            PreparedStatement psStock = con.prepareStatement(sqlUpdateStock);

            for (ItemFactura item : items) {
                // Guardar detalle
                psDetalle.setInt(1, idFactura);
                psDetalle.setString(2, item.getTipo());
                psDetalle.setString(3, item.getDescripcion());
                psDetalle.setInt(4, item.getCantidad());
                psDetalle.setDouble(5, item.getPrecioUnitario());
                psDetalle.setDouble(6, item.getSubtotal());
                psDetalle.executeUpdate();

                // Descontar Stock (Solo si es repuesto)
                if (item.getTipo().equals("REPUESTO")) {
                    psStock.setInt(1, item.getCantidad()); // Cantidad a restar
                    psStock.setInt(2, item.getIdReferencia()); // ID
                    psStock.setInt(3, item.getCantidad()); // CONDICION: Stock debe ser mayor o igual a lo que resto

                    int filasAfectadas = psStock.executeUpdate();

                    if (filasAfectadas == 0) {
                        // Si entra acá es porque NO ALCANZÓ EL STOCK en la base de datos
                        throw new SQLException("Stock insuficiente en BD para el producto ID: " + item.getIdReferencia());
                    }
                }
            }

            con.commit(); // Confirmar cambios
            return true;

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (Exception e){}
        }
    }
}