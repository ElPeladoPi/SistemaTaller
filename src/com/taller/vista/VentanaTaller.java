package com.taller.vista;

import com.taller.dao.FacturaDAO;
import com.taller.dao.RepuestoDAO;
import com.taller.dao.ServicioDAO;
import com.taller.dao.VehiculoDAO;
import com.taller.modelo.Historial;
import com.taller.modelo.ItemFactura;
import com.taller.modelo.Repuesto;
import com.taller.modelo.Servicio;
import com.taller.modelo.Vehiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentanaTaller extends JFrame {

    private JTabbedPane pestañas;

    // --- MODELOS DE TABLAS ---
    private DefaultTableModel modeloRepuestos;
    private DefaultTableModel modeloFactura;
    private DefaultTableModel modeloServicios;
    private DefaultTableModel modeloListaVehiculos;
    private DefaultTableModel modeloHistorial;

    // --- VARIABLES DE CONTROL ---
    private int idRepuestoSeleccionado = -1;
    private int idServicioSeleccionado = -1;
    private Vehiculo vehiculoActual = null;

    // --- LISTA DE FACTURACION ---
    private List<ItemFactura> itemsParaFacturar = new ArrayList<>();
    private JLabel lblTotalFactura;

    public VentanaTaller() {
        setTitle("Sistema Taller Mecánico - Gestión Integral PRO v4.0");
        setSize(1280, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        pestañas = new JTabbedPane();
        pestañas.addTab("1. Gestión de Repuestos", crearPanelRepuestos());
        pestañas.addTab("2. Facturación y Caja", crearPanelFacturacion());
        pestañas.addTab("3. Gestión de Servicios", crearPanelServicios());
        pestañas.addTab("4. Vehículos e Historial", crearPanelVehiculos());

        add(pestañas);
    }

    // ========================================================================
    // PESTAÑA 1: GESTIÓN DE REPUESTOS
    // ========================================================================
    private JPanel crearPanelRepuestos() {
        JPanel panel = new JPanel(new BorderLayout());

        // Buscador
        JPanel panelBusqueda = new JPanel();
        JTextField txtBuscador = new JTextField(25);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnMostrarTodos = new JButton("Ver Todos");
        panelBusqueda.add(new JLabel("Buscar (Código/Nombre):")); panelBusqueda.add(txtBuscador);
        panelBusqueda.add(btnBuscar); panelBusqueda.add(btnMostrarTodos);
        panel.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Código", "Descripción", "Marca", "Precio ($)", "Stock"};
        modeloRepuestos = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tabla = new JTable(modeloRepuestos);
        cargarTablaRepuestos("");

        // Formulario
        JPanel panelFormulario = new JPanel(new BorderLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Repuesto"));
        JPanel campos = new JPanel(new GridLayout(3, 4, 15, 10));

        JTextField txtCodigo = new JTextField();
        JTextField txtDesc = new JTextField();
        JTextField txtMarca = new JTextField();
        JTextField txtPrecio = new JTextField();
        JTextField txtStock = new JTextField();

        campos.add(new JLabel("Código (SKU):")); campos.add(txtCodigo);
        campos.add(new JLabel("Descripción:")); campos.add(txtDesc);
        campos.add(new JLabel("Marca:")); campos.add(txtMarca);
        campos.add(new JLabel("Precio ($):")); campos.add(txtPrecio);
        campos.add(new JLabel("Stock:")); campos.add(txtStock);
        campos.add(new JLabel("")); campos.add(new JLabel(""));

        JPanel botones = new JPanel();
        JButton btnNuevo = new JButton("Limpiar / Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        btnActualizar.setEnabled(false); btnEliminar.setEnabled(false);

        botones.add(btnNuevo); botones.add(btnGuardar); botones.add(btnActualizar); botones.add(btnEliminar);
        panelFormulario.add(campos, BorderLayout.CENTER);
        panelFormulario.add(botones, BorderLayout.SOUTH);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(panelFormulario, BorderLayout.SOUTH);

        // Eventos
        btnBuscar.addActionListener(e -> cargarTablaRepuestos(txtBuscador.getText()));
        btnMostrarTodos.addActionListener(e -> { txtBuscador.setText(""); cargarTablaRepuestos(""); });

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int f = tabla.getSelectedRow();
                if (f >= 0) {
                    idRepuestoSeleccionado = (int) modeloRepuestos.getValueAt(f, 0);
                    txtCodigo.setText(modeloRepuestos.getValueAt(f, 1).toString());
                    txtDesc.setText(modeloRepuestos.getValueAt(f, 2).toString());
                    txtMarca.setText(modeloRepuestos.getValueAt(f, 3).toString());
                    txtPrecio.setText(modeloRepuestos.getValueAt(f, 4).toString());
                    txtStock.setText(modeloRepuestos.getValueAt(f, 5).toString());
                    btnActualizar.setEnabled(true); btnEliminar.setEnabled(true); btnGuardar.setEnabled(false);
                }
            }
        });

        btnNuevo.addActionListener(e -> {
            idRepuestoSeleccionado = -1;
            txtCodigo.setText(""); txtDesc.setText(""); txtMarca.setText(""); txtPrecio.setText(""); txtStock.setText("");
            tabla.clearSelection();
            btnActualizar.setEnabled(false); btnEliminar.setEnabled(false); btnGuardar.setEnabled(true);
        });

        btnGuardar.addActionListener(e -> {
            try {
                Repuesto r = new Repuesto(txtCodigo.getText(), txtDesc.getText(), txtMarca.getText(), Double.parseDouble(limpiarPrecio(txtPrecio.getText())), Integer.parseInt(txtStock.getText()));
                if(new RepuestoDAO().guardar(r)) { cargarTablaRepuestos(""); btnNuevo.doClick(); }
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error datos"); }
        });

        btnActualizar.addActionListener(e -> {
            try {
                Repuesto r = new Repuesto(idRepuestoSeleccionado, txtCodigo.getText(), txtDesc.getText(), txtMarca.getText(), Double.parseDouble(limpiarPrecio(txtPrecio.getText())), Integer.parseInt(txtStock.getText()));
                if(new RepuestoDAO().actualizar(r)) { cargarTablaRepuestos(""); btnNuevo.doClick(); }
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error act."); }
        });

        btnEliminar.addActionListener(e -> {
            if (idRepuestoSeleccionado != -1 && JOptionPane.showConfirmDialog(this, "¿Borrar?", "Conf", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                new RepuestoDAO().eliminar(idRepuestoSeleccionado); cargarTablaRepuestos(""); btnNuevo.doClick();
            }
        });

        return panel;
    }

    // ========================================================================
    // PESTAÑA 2: FACTURACIÓN (CON CONTROL DE STOCK)
    // ========================================================================
    private JPanel crearPanelFacturacion() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtCliente = new JTextField(30);
        panelNorte.add(new JLabel("Cliente / Razón Social:")); panelNorte.add(txtCliente);
        panel.add(panelNorte, BorderLayout.NORTH);

        String[] colFactura = {"Tipo", "Descripción", "Cant.", "Precio Unit.", "Subtotal"};
        modeloFactura = new DefaultTableModel(colFactura, 0);
        panel.add(new JScrollPane(new JTable(modeloFactura)), BorderLayout.CENTER);

        JPanel panelSeleccion = new JPanel(new GridBagLayout());
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Agregar Item"));
        panelSeleccion.setPreferredSize(new Dimension(340, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 5, 8, 5); g.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"REPUESTO", "SERVICIO"});
        JTextField txtIdRef = new JTextField();
        txtIdRef.setFont(new Font("SansSerif", Font.BOLD, 20));
        txtIdRef.setPreferredSize(new Dimension(100, 40));
        txtIdRef.setHorizontalAlignment(JTextField.CENTER);

        JButton btnBuscarItem = new JButton("🔍");
        btnBuscarItem.setPreferredSize(new Dimension(50, 40));

        JTextArea txtDescItem = new JTextArea(3, 20);
        txtDescItem.setLineWrap(true); txtDescItem.setEditable(false); txtDescItem.setBackground(Color.LIGHT_GRAY);
        JTextField txtPrecioItem = new JTextField();
        JTextField txtCantItem = new JTextField("1");

        JButton btnAgregar = new JButton("AGREGAR ITEM");
        btnAgregar.setBackground(new Color(200, 220, 255));
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAgregar.setPreferredSize(new Dimension(0, 45));

        g.gridx=0; g.gridy=0; panelSeleccion.add(new JLabel("Tipo:"), g);
        g.gridx=1; g.gridy=0; panelSeleccion.add(comboTipo, g);

        g.gridx=0; g.gridy=1; panelSeleccion.add(new JLabel("Código/ID:"), g);
        JPanel pnlId = new JPanel(new BorderLayout());
        pnlId.add(txtIdRef, BorderLayout.CENTER);
        pnlId.add(btnBuscarItem, BorderLayout.EAST);
        g.gridx=1; g.gridy=1; panelSeleccion.add(pnlId, g);

        g.gridx=0; g.gridy=2; g.anchor=GridBagConstraints.NORTH; panelSeleccion.add(new JLabel("Detalle:"), g);
        g.gridx=1; g.gridy=2; panelSeleccion.add(new JScrollPane(txtDescItem), g);

        g.gridx=0; g.gridy=3; panelSeleccion.add(new JLabel("Precio $:"), g);
        g.gridx=1; g.gridy=3; panelSeleccion.add(txtPrecioItem, g);

        g.gridx=0; g.gridy=4; panelSeleccion.add(new JLabel("Cant:"), g);
        g.gridx=1; g.gridy=4; panelSeleccion.add(txtCantItem, g);

        g.gridx=0; g.gridy=5; g.gridwidth=2; panelSeleccion.add(btnAgregar, g);
        g.gridy=6; g.weighty=1.0; panelSeleccion.add(new JLabel(), g);

        panel.add(panelSeleccion, BorderLayout.EAST);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotalFactura = new JLabel("TOTAL: $ 0.00   ");
        lblTotalFactura.setFont(new Font("Arial", Font.BOLD, 22));
        JButton btnImprimir = new JButton("🖨️ IMPRIMIR / PDF");
        JButton btnFinalizar = new JButton("💰 COBRAR Y FINALIZAR");
        btnFinalizar.setBackground(Color.GREEN);

        panelSur.add(btnImprimir);
        panelSur.add(Box.createHorizontalStrut(30));
        panelSur.add(lblTotalFactura); panelSur.add(btnFinalizar);
        panel.add(panelSur, BorderLayout.SOUTH);

        // --- LOGICA FACTURACION ---
        btnBuscarItem.addActionListener(e -> {
            buscarItemParaFacturar(txtIdRef, comboTipo, txtDescItem, txtPrecioItem);
        });

        btnAgregar.addActionListener(e -> {
            try {
                // Si el precio está vacío, intentamos buscar primero
                if (txtPrecioItem.getText().isEmpty()) {
                    buscarItemParaFacturar(txtIdRef, comboTipo, txtDescItem, txtPrecioItem);
                    if (txtPrecioItem.getText().isEmpty()) return;
                }

                int idReal = Integer.parseInt(txtIdRef.getText());
                String tipo = (String) comboTipo.getSelectedItem();
                int cantSolicitada = Integer.parseInt(txtCantItem.getText());

                // VERIFICACIÓN DE STOCK VISUAL
                if (tipo.equals("REPUESTO")) {
                    Repuesto r = new RepuestoDAO().buscarPorId(idReal);
                    if (r != null) {
                        int cantidadEnLista = 0;
                        for (ItemFactura item : itemsParaFacturar) {
                            if (item.getTipo().equals("REPUESTO") && item.getIdReferencia() == idReal) {
                                cantidadEnLista += item.getCantidad();
                            }
                        }
                        if (cantidadEnLista + cantSolicitada > r.getStock()) {
                            JOptionPane.showMessageDialog(this,
                                    "¡STOCK INSUFICIENTE!\n" +
                                            "Stock Real: " + r.getStock() + "\n" +
                                            "En Factura: " + cantidadEnLista + "\n" +
                                            "Solicitado: " + cantSolicitada,
                                    "Alerta de Stock", JOptionPane.WARNING_MESSAGE);
                            return; // CANCELAMOS
                        }
                    }
                }

                ItemFactura item = new ItemFactura(
                        idReal, tipo, txtDescItem.getText(),
                        Double.parseDouble(txtPrecioItem.getText()), cantSolicitada
                );
                itemsParaFacturar.add(item);
                modeloFactura.addRow(new Object[]{item.getTipo(), item.getDescripcion(), item.getCantidad(), item.getPrecioUnitario(), item.getSubtotal()});

                double total = 0; for(ItemFactura i : itemsParaFacturar) total += i.getSubtotal();
                lblTotalFactura.setText("TOTAL: $ " + String.format("%.2f", total) + "   ");

                txtIdRef.setText(""); txtDescItem.setText(""); txtPrecioItem.setText(""); txtIdRef.requestFocus();
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar. Verifique datos.");
            }
        });

        btnImprimir.addActionListener(e -> imprimirFactura(txtCliente.getText()));

        btnFinalizar.addActionListener(e -> {
            if(itemsParaFacturar.isEmpty() || txtCliente.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Faltan datos (Cliente o Items)"); return;
            }
            if (JOptionPane.showConfirmDialog(this, "¿Imprimir antes de finalizar?", "Imprimir", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                imprimirFactura(txtCliente.getText());
            }

            double total = 0; for(ItemFactura i : itemsParaFacturar) total += i.getSubtotal();

            if(new FacturaDAO().registrarVenta(txtCliente.getText(), itemsParaFacturar, total)) {
                JOptionPane.showMessageDialog(this, "¡Venta Exitosa! Stock descontado.");
                itemsParaFacturar.clear(); modeloFactura.setRowCount(0); txtCliente.setText(""); lblTotalFactura.setText("TOTAL: $ 0.00");
                cargarTablaRepuestos("");
            } else {
                JOptionPane.showMessageDialog(this, "Error al facturar. Verifique Stock.");
            }
        });

        return panel;
    }

    // ========================================================================
    // PESTAÑA 3: GESTIÓN DE SERVICIOS
    // ========================================================================
    private JPanel crearPanelServicios() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] colServ = {"ID", "Nombre Servicio", "Costo Sugerido ($)"};
        modeloServicios = new DefaultTableModel(colServ, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tablaServ = new JTable(modeloServicios);
        cargarTablaServicios();
        panel.add(new JScrollPane(tablaServ), BorderLayout.CENTER);

        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelForm.setBorder(BorderFactory.createTitledBorder("Administrar Servicio"));

        JTextField txtNomServ = new JTextField(25);
        JTextField txtCostoServ = new JTextField(10);
        JButton btnS_Nuevo = new JButton("Nuevo");
        JButton btnS_Guardar = new JButton("Guardar");
        JButton btnS_Actualizar = new JButton("Actualizar");
        JButton btnS_Eliminar = new JButton("Eliminar");
        btnS_Actualizar.setEnabled(false); btnS_Eliminar.setEnabled(false);

        panelForm.add(new JLabel("Nombre:")); panelForm.add(txtNomServ);
        panelForm.add(new JLabel("Costo:")); panelForm.add(txtCostoServ);
        panelForm.add(btnS_Nuevo); panelForm.add(btnS_Guardar); panelForm.add(btnS_Actualizar); panelForm.add(btnS_Eliminar);

        panel.add(panelForm, BorderLayout.SOUTH);

        tablaServ.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int f = tablaServ.getSelectedRow();
                if (f >= 0) {
                    idServicioSeleccionado = (int) modeloServicios.getValueAt(f, 0);
                    txtNomServ.setText(modeloServicios.getValueAt(f, 1).toString());
                    txtCostoServ.setText(modeloServicios.getValueAt(f, 2).toString());
                    btnS_Actualizar.setEnabled(true); btnS_Eliminar.setEnabled(true); btnS_Guardar.setEnabled(false);
                }
            }
        });

        btnS_Nuevo.addActionListener(e -> {
            idServicioSeleccionado = -1; txtNomServ.setText(""); txtCostoServ.setText("");
            tablaServ.clearSelection();
            btnS_Actualizar.setEnabled(false); btnS_Eliminar.setEnabled(false); btnS_Guardar.setEnabled(true);
        });

        btnS_Guardar.addActionListener(e -> {
            try {
                if(new ServicioDAO().guardar(new Servicio(txtNomServ.getText(), Double.parseDouble(txtCostoServ.getText())))) {
                    cargarTablaServicios(); btnS_Nuevo.doClick();
                }
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error datos"); }
        });

        btnS_Actualizar.addActionListener(e -> {
            try {
                if(new ServicioDAO().actualizar(new Servicio(idServicioSeleccionado, txtNomServ.getText(), Double.parseDouble(txtCostoServ.getText())))) {
                    cargarTablaServicios(); btnS_Nuevo.doClick();
                }
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error actualización"); }
        });

        btnS_Eliminar.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "¿Borrar?", "Conf", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                new ServicioDAO().eliminar(idServicioSeleccionado); cargarTablaServicios(); btnS_Nuevo.doClick();
            }
        });

        return panel;
    }

    // ========================================================================
    // PESTAÑA 4: VEHÍCULOS E HISTORIAL (NUEVA VERSIÓN CON LISTA)
    // ========================================================================
    private JPanel crearPanelVehiculos() {
        JPanel panel = new JPanel(new BorderLayout());

        // --- SECTOR IZQUIERDO: LISTA DE AUTOS Y FORMULARIO ---
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder(" Parque Automotor "));
        panelIzquierdo.setPreferredSize(new Dimension(450, 0));

        // 1. TABLA DE VEHICULOS (LISTA ARRIBA)
        String[] colVeh = {"Patente", "Modelo", "Propietario"};
        modeloListaVehiculos = new DefaultTableModel(colVeh, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaVehiculos = new JTable(modeloListaVehiculos);
        cargarListaVehiculos();

        JScrollPane scrollLista = new JScrollPane(tablaVehiculos);
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);

        // 2. FORMULARIO REGISTRO (ABAJO DE LA LISTA)
        JPanel panelFormAuto = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtPatente = new JTextField();
        JTextField txtDescAuto = new JTextField();
        JTextField txtPropietario = new JTextField();
        JButton btnGuardarAuto = new JButton("REGISTRAR NUEVO");
        JButton btnLimpiarAuto = new JButton("Limpiar / Nuevo");

        g.gridx=0; g.gridy=0; panelFormAuto.add(new JLabel("Patente:"), g);
        g.gridx=1; g.gridy=0; panelFormAuto.add(txtPatente, g);
        g.gridx=0; g.gridy=1; panelFormAuto.add(new JLabel("Modelo:"), g);
        g.gridx=1; g.gridy=1; panelFormAuto.add(txtDescAuto, g);
        g.gridx=0; g.gridy=2; panelFormAuto.add(new JLabel("Dueño:"), g);
        g.gridx=1; g.gridy=2; panelFormAuto.add(txtPropietario, g);

        JPanel pnlBotones = new JPanel(new GridLayout(1, 2, 5, 0));
        pnlBotones.add(btnLimpiarAuto); pnlBotones.add(btnGuardarAuto);
        g.gridx=0; g.gridy=3; g.gridwidth=2; panelFormAuto.add(pnlBotones, g);

        panelIzquierdo.add(panelFormAuto, BorderLayout.SOUTH);
        panel.add(panelIzquierdo, BorderLayout.WEST);

        // --- SECTOR DERECHO: HISTORIAL DEL AUTO SELECCIONADO ---
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder(" Historial de Servicios "));

        String[] colHist = {"Fecha", "Km", "Detalle"};
        modeloHistorial = new DefaultTableModel(colHist, 0);
        JTable tablaHistorial = new JTable(modeloHistorial);
        panelDerecho.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        JPanel panelNuevoServ = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtKm = new JTextField(8);
        JTextField txtDetalleHist = new JTextField(30);
        JButton btnAgregarHist = new JButton("Guardar Evento");

        panelNuevoServ.add(new JLabel("Km:")); panelNuevoServ.add(txtKm);
        panelNuevoServ.add(new JLabel("Detalle:")); panelNuevoServ.add(txtDetalleHist);
        panelNuevoServ.add(btnAgregarHist);

        panelDerecho.add(panelNuevoServ, BorderLayout.SOUTH);
        panel.add(panelDerecho, BorderLayout.CENTER);

        // --- LÓGICA DE EVENTOS VEHÍCULOS ---

        // CLIC EN LA LISTA -> CARGA DATOS Y HISTORIAL
        tablaVehiculos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fila = tablaVehiculos.getSelectedRow();
                if (fila >= 0) {
                    String pat = (String) modeloListaVehiculos.getValueAt(fila, 0);
                    vehiculoActual = new VehiculoDAO().buscarPorPatente(pat);
                    if (vehiculoActual != null) {
                        txtPatente.setText(vehiculoActual.getPatente());
                        txtDescAuto.setText(vehiculoActual.getDescripcion());
                        txtPropietario.setText(vehiculoActual.getPropietario());

                        cargarHistorial(vehiculoActual.getId());

                        txtPatente.setEditable(false);
                        btnGuardarAuto.setEnabled(false);
                    }
                }
            }
        });

        btnLimpiarAuto.addActionListener(e -> {
            vehiculoActual = null;
            txtPatente.setText(""); txtPatente.setEditable(true);
            txtDescAuto.setText(""); txtPropietario.setText("");
            modeloHistorial.setRowCount(0);
            tablaVehiculos.clearSelection();
            btnGuardarAuto.setEnabled(true);
        });

        btnGuardarAuto.addActionListener(e -> {
            if(txtPatente.getText().isEmpty() || txtDescAuto.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Faltan datos"); return;
            }
            // Validar duplicado en la lista visual (más rápido)
            for(int i=0; i<modeloListaVehiculos.getRowCount(); i++) {
                if(modeloListaVehiculos.getValueAt(i, 0).toString().equalsIgnoreCase(txtPatente.getText())) {
                    JOptionPane.showMessageDialog(this, "Patente ya existe"); return;
                }
            }

            Vehiculo v = new Vehiculo(txtPatente.getText(), txtDescAuto.getText(), txtPropietario.getText());
            if(new VehiculoDAO().guardar(v)) {
                JOptionPane.showMessageDialog(this, "Vehículo Registrado");
                cargarListaVehiculos();
                btnLimpiarAuto.doClick();
            } else { JOptionPane.showMessageDialog(this, "Error al guardar"); }
        });

        btnAgregarHist.addActionListener(e -> {
            if (vehiculoActual == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un auto de la lista de la izquierda."); return;
            }
            try {
                int km = Integer.parseInt(txtKm.getText());
                String det = txtDetalleHist.getText();
                if(det.isEmpty()) return;

                Historial h = new Historial(vehiculoActual.getId(), Date.valueOf(LocalDate.now()), km, det);
                if (new VehiculoDAO().agregarHistorial(h)) {
                    cargarHistorial(vehiculoActual.getId());
                    txtDetalleHist.setText(""); txtKm.setText("");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Km debe ser número"); }
        });

        return panel;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    private void buscarItemParaFacturar(JTextField txtIdRef, JComboBox<String> comboTipo, JTextArea txtDescItem, JTextField txtPrecioItem) {
        String textoBusqueda = txtIdRef.getText().trim();
        if (textoBusqueda.isEmpty()) return;
        String tipo = (String) comboTipo.getSelectedItem();

        try {
            if (tipo.equals("REPUESTO")) {
                Repuesto encontrado = null;
                // 1. Buscar por ID
                try {
                    int idNum = Integer.parseInt(textoBusqueda);
                    encontrado = new RepuestoDAO().buscarPorId(idNum);
                } catch (NumberFormatException ex) { }

                // 2. Buscar por Código si no encontró ID
                if (encontrado == null) {
                    List<Repuesto> lista = new RepuestoDAO().buscar(textoBusqueda);
                    for (Repuesto r : lista) {
                        if (r.getCodigo().equalsIgnoreCase(textoBusqueda)) {
                            encontrado = r; break;
                        }
                    }
                }

                if (encontrado != null) {
                    txtDescItem.setText("[" + encontrado.getCodigo() + "] " + encontrado.getDescripcion());
                    txtPrecioItem.setText(String.valueOf(encontrado.getPrecio()));
                    txtPrecioItem.setEditable(false);
                    txtIdRef.setText(String.valueOf(encontrado.getId())); // Ponemos ID real para que Agregar no falle
                } else {
                    JOptionPane.showMessageDialog(this, "Repuesto no encontrado");
                }
            } else {
                try {
                    Servicio s = new ServicioDAO().buscarPorId(Integer.parseInt(textoBusqueda));
                    if (s != null) {
                        txtDescItem.setText(s.getNombre());
                        txtPrecioItem.setText(String.valueOf(s.getCosto()));
                        txtPrecioItem.setEditable(true);
                    } else { JOptionPane.showMessageDialog(this, "Servicio no encontrado"); }
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Servicios: Busque por ID numérico."); }
            }
        } catch(Exception ex) { ex.printStackTrace(); }
    }

    private void imprimirFactura(String cliente) {
        if (itemsParaFacturar.isEmpty()) { JOptionPane.showMessageDialog(this, "Nada para imprimir"); return; }

        StringBuilder html = new StringBuilder();
        html.append("<html><div style='font-family: monospace;'>");
        html.append("<h2 style='text-align:center;'>TALLER MECÁNICO</h2><hr>");
        html.append("<p><b>Cliente:</b> ").append(cliente).append("</p>");
        html.append("<p><b>Fecha:</b> ").append(java.time.LocalDate.now()).append("</p><br>");

        html.append("<table width='100%' style='border-collapse: collapse;'>");
        html.append("<tr style='border-bottom: 1px solid black;'><th>Desc</th><th align='right'>Cant</th><th align='right'>Unit</th><th align='right'>Total</th></tr>");

        double total = 0;
        for (ItemFactura i : itemsParaFacturar) {
            html.append("<tr>");
            html.append("<td>").append(i.getDescripcion()).append("</td>");
            html.append("<td align='right'>").append(i.getCantidad()).append("</td>");
            html.append("<td align='right'>$").append(String.format("%.2f", i.getPrecioUnitario())).append("</td>");
            html.append("<td align='right'>$").append(String.format("%.2f", i.getSubtotal())).append("</td>");
            html.append("</tr>");
            total += i.getSubtotal();
        }
        html.append("</table><hr>");
        html.append("<h3 style='text-align:right;'>TOTAL: $").append(String.format("%.2f", total)).append("</h3>");
        html.append("</div></html>");

        JTextPane impresion = new JTextPane();
        impresion.setContentType("text/html");
        impresion.setText(html.toString());

        try { impresion.print(null, null, true, null, null, true); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Error impresión: " + e.getMessage()); }
    }

    private void cargarTablaRepuestos(String busqueda) {
        modeloRepuestos.setRowCount(0);
        for (Repuesto r : new RepuestoDAO().buscar(busqueda)) {
            modeloRepuestos.addRow(new Object[]{r.getId(), r.getCodigo(), r.getDescripcion(), r.getMarca(), r.getPrecio(), r.getStock()});
        }
    }

    private void cargarTablaServicios() {
        modeloServicios.setRowCount(0);
        for (Servicio s : new ServicioDAO().listar()) {
            modeloServicios.addRow(new Object[]{s.getId(), s.getNombre(), s.getCosto()});
        }
    }

    private void cargarListaVehiculos() {
        modeloListaVehiculos.setRowCount(0);
        List<Vehiculo> lista = new VehiculoDAO().listarTodos();
        for (Vehiculo v : lista) {
            modeloListaVehiculos.addRow(new Object[]{v.getPatente(), v.getDescripcion(), v.getPropietario()});
        }
    }

    private void cargarHistorial(int idVehiculo) {
        modeloHistorial.setRowCount(0);
        List<Historial> lista = new VehiculoDAO().listarHistorial(idVehiculo);
        for (Historial h : lista) {
            modeloHistorial.addRow(new Object[]{h.getFecha(), h.getKilometraje() + " km", h.getDetalle()});
        }
    }

    private String limpiarPrecio(String p) {
        return p.trim().replace(".", "").replace(",", ".");
    }
}