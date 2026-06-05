/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AsignacionVacunas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import sector_salud.Conexion_DB;

/**
 *
 * @author marga
 */
public class ModificarAsignaciones extends JPanel {

    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    private static final Color NARANJA = new Color(255, 165, 0);

    private JComboBox<String> cbCentroSalud;
    private JComboBox<String> cbLoteVacuna;
    private JTextField txtCantidadAsignar, txtFolioActual, txtFolioNuevo, txtObservaciones;
    private JLabel lblCantidadDisponible, lblLoteInfo, lblIdAsignacion, lblLoteOriginal, lblCantidadOriginal;
    private JButton btnBuscar, btnModificar, btnLimpiar;
    private JTable tablaAsignaciones;
    private DefaultTableModel modeloTabla;

    private CardLayout cardLayout;
    private JPanel contenedor;

    private Map<String, Integer> mapaCentros;
    private Map<String, LoteInfo> mapaLotes;
    private int idAsignacionSeleccionada = -1;
    private int idLoteOriginal = -1;
    private int cantidadOriginal = -1;
    private String folioOriginalAsignado = "";
    private int idCentroOriginal = -1;

    private class LoteInfo {
        int idLote;
        String loteVacunacion;
        String folioActual;
        int cantidadDisponible;

        LoteInfo(int idLote, String loteVacunacion, String folioActual, int cantidadDisponible) {
            this.idLote = idLote;
            this.loteVacunacion = loteVacunacion;
            this.folioActual = folioActual;
            this.cantidadDisponible = cantidadDisponible;
        }

        int getInicioFolio() {
            if (folioActual == null || !folioActual.contains("-")) return 1;
            try { return Integer.parseInt(folioActual.split("-")[0]); } catch (Exception e) { return 1; }
        }

        int getFinFolio() {
            if (folioActual == null || !folioActual.contains("-")) return cantidadDisponible;
            try { return Integer.parseInt(folioActual.split("-")[1]); } catch (Exception e) { return cantidadDisponible; }
        }

        String generarNuevoFolio(int cantidadAsignar) {
            int inicioActual = getInicioFolio();
            int nuevoInicio = inicioActual + cantidadAsignar;
            return nuevoInicio + "-" + getFinFolio();
        }

        String getFoliosAsignados(int cantidadAsignar) {
            int inicioActual = getInicioFolio();
            int finAsignado = inicioActual + cantidadAsignar - 1;
            return inicioActual + "-" + finAsignado;
        }
    }

    public ModificarAsignaciones(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.mapaCentros = new HashMap<>();
        this.mapaLotes = new HashMap<>();

        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Modificar Asignación de Vacunas");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(FONDO);

        // Panel superior: seleccionar asignación
        JPanel panelSeleccion = new JPanel(new BorderLayout());
        panelSeleccion.setBackground(FONDO);
        panelSeleccion.setBorder(BorderFactory.createTitledBorder("Seleccionar Asignación a Modificar"));

        // Tabla para mostrar asignaciones activas del centro seleccionado
        modeloTabla = new DefaultTableModel();
        tablaAsignaciones = new JTable(modeloTabla);
        tablaAsignaciones.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaAsignaciones.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaAsignaciones.getTableHeader().setBackground(GUINDO);
        tablaAsignaciones.getTableHeader().setForeground(Color.WHITE);
        tablaAsignaciones.setRowHeight(25);
        tablaAsignaciones.getSelectionModel().addListSelectionListener(e -> cargarDatosSeleccion());
        JScrollPane scrollTabla = new JScrollPane(tablaAsignaciones);
        scrollTabla.setPreferredSize(new Dimension(800, 150));
        panelSeleccion.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelFiltroCentro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltroCentro.setBackground(FONDO);
        JLabel lblCentroFiltro = new JLabel("Centro: ");
        cbCentroSalud = new JComboBox<>();
        cbCentroSalud.setPreferredSize(new Dimension(300, 25));
        btnBuscar = new JButton("Buscar Asignaciones");
        btnBuscar.addActionListener(e -> cargarAsignacionesPorCentro());
        panelFiltroCentro.add(lblCentroFiltro);
        panelFiltroCentro.add(cbCentroSalud);
        panelFiltroCentro.add(btnBuscar);
        panelSeleccion.add(panelFiltroCentro, BorderLayout.NORTH);

        centro.add(panelSeleccion, BorderLayout.NORTH);

        // Panel de formulario de modificación
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(FONDO);
        formulario.setBorder(BorderFactory.createTitledBorder("Datos de la Asignación"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID Asignación
        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("ID Asignación:"), gbc);
        gbc.gridx = 1;
        lblIdAsignacion = new JLabel("---");
        formulario.add(lblIdAsignacion, gbc);

        // Centro de Salud
        gbc.gridx = 0; gbc.gridy = 1;
        formulario.add(new JLabel("Centro de Salud:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cbCentroMod = new JComboBox<>();
        cbCentroMod.setPreferredSize(new Dimension(300, 25));
        cargarCentrosEnCombo(cbCentroMod);
        formulario.add(cbCentroMod, gbc);
        cbCentroSalud = cbCentroMod; // reutilizar referencia

        // Lote actual (solo lectura)
        gbc.gridx = 0; gbc.gridy = 2;
        formulario.add(new JLabel("Lote original:"), gbc);
        gbc.gridx = 1;
        lblLoteOriginal = new JLabel("---");
        formulario.add(lblLoteOriginal, gbc);

        // Cantidad original
        gbc.gridx = 0; gbc.gridy = 3;
        formulario.add(new JLabel("Cantidad original:"), gbc);
        gbc.gridx = 1;
        lblCantidadOriginal = new JLabel("---");
        formulario.add(lblCantidadOriginal, gbc);

        // Nuevo Lote
        gbc.gridx = 0; gbc.gridy = 4;
        formulario.add(new JLabel("Nuevo Lote de Vacuna:"), gbc);
        gbc.gridx = 1;
        cbLoteVacuna = new JComboBox<>();
        cbLoteVacuna.addActionListener(e -> cargarInfoLote());
        formulario.add(cbLoteVacuna, gbc);

        // Información del nuevo lote
        gbc.gridx = 0; gbc.gridy = 5;
        formulario.add(new JLabel("Información lote:"), gbc);
        gbc.gridx = 1;
        lblLoteInfo = new JLabel("Seleccione un lote");
        formulario.add(lblLoteInfo, gbc);

        // Cantidad disponible nuevo lote
        gbc.gridx = 0; gbc.gridy = 6;
        formulario.add(new JLabel("Cantidad disponible:"), gbc);
        gbc.gridx = 1;
        lblCantidadDisponible = new JLabel("0");
        formulario.add(lblCantidadDisponible, gbc);

        // Folio actual nuevo lote
        gbc.gridx = 0; gbc.gridy = 7;
        formulario.add(new JLabel("Folio actual del lote:"), gbc);
        gbc.gridx = 1;
        txtFolioActual = new JTextField(30);
        txtFolioActual.setEditable(false);
        formulario.add(txtFolioActual, gbc);

        // Nueva cantidad a asignar
        gbc.gridx = 0; gbc.gridy = 8;
        formulario.add(new JLabel("Nueva cantidad a asignar:"), gbc);
        gbc.gridx = 1;
        txtCantidadAsignar = new JTextField(15);
        txtCantidadAsignar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPreviaFolio(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPreviaFolio(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPreviaFolio(); }
        });
        formulario.add(txtCantidadAsignar, gbc);

        // Nuevo folio a asignar (vista previa)
        gbc.gridx = 0; gbc.gridy = 9;
        formulario.add(new JLabel("Nuevo folio a asignar:"), gbc);
        gbc.gridx = 1;
        txtFolioNuevo = new JTextField(30);
        txtFolioNuevo.setEditable(false);
        formulario.add(txtFolioNuevo, gbc);

        // Observaciones
        gbc.gridx = 0; gbc.gridy = 10;
        formulario.add(new JLabel("Observaciones:"), gbc);
        gbc.gridx = 1;
        txtObservaciones = new JTextField(40);
        formulario.add(txtObservaciones, gbc);

        centro.add(formulario, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBotones.setBackground(FONDO);
        btnModificar = new JButton("Guardar Cambios");
        btnModificar.setBackground(NARANJA);
        btnModificar.setForeground(Color.WHITE);
        btnModificar.setEnabled(false);
        btnModificar.addActionListener(e -> modificarAsignacion());

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo6"));

        panelBotones.add(btnModificar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRegresar);
        centro.add(panelBotones, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);

        // Cargar listas iniciales
        cargarCentrosEnCombo(cbCentroSalud);
        cargarLotesEnCombo();
    }

    private void cargarCentrosEnCombo(JComboBox<String> combo) {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_centro, nombre FROM centros_salud WHERE activo = true ORDER BY nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            combo.removeAllItems();
            mapaCentros.clear();
            combo.addItem("-- Seleccione un centro --");
            while (rs.next()) {
                int id = rs.getInt("id_centro");
                String nombre = rs.getString("nombre");
                mapaCentros.put(nombre, id);
                combo.addItem(nombre);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarLotesEnCombo() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_lote, lote_vacunacion, folio, cantidad FROM lotes WHERE cantidad > 0 ORDER BY lote_vacunacion";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            cbLoteVacuna.removeAllItems();
            mapaLotes.clear();
            cbLoteVacuna.addItem("-- Seleccione un lote --");
            while (rs.next()) {
                int idLote = rs.getInt("id_lote");
                String lote = rs.getString("lote_vacunacion");
                String folio = rs.getString("folio");
                int cantidad = rs.getInt("cantidad");
                mapaLotes.put(lote, new LoteInfo(idLote, lote, folio, cantidad));
                cbLoteVacuna.addItem(lote);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarAsignacionesPorCentro() {
        String centro = (String) cbCentroSalud.getSelectedItem();
        if (centro == null || centro.equals("-- Seleccione un centro --")) {
            JOptionPane.showMessageDialog(this, "Seleccione un centro de salud", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idCentro = mapaCentros.get(centro);
        String[] columnas = {"ID Asignación", "Lote", "Cantidad", "Folio Asignado", "Fecha", "Observaciones"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);

        String sql = "SELECT a.id_inventario, l.lote_vacunacion, a.cantidad_asignar, a.folio, " +
                     "a.fecha_asignacion, a.observaciones, a.id_lote, a.id_centro " +
                     "FROM asignacion_vacunas_centro a " +
                     "JOIN lotes l ON a.id_lote = l.id_lote " +
                     "WHERE a.id_centro = ? AND a.activo = true ORDER BY a.fecha_asignacion DESC";
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCentro);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_inventario"),
                    rs.getString("lote_vacunacion"),
                    rs.getInt("cantidad_asignar"),
                    rs.getString("folio"),
                    rs.getTimestamp("fecha_asignacion"),
                    rs.getString("observaciones")
                };
                modeloTabla.addRow(fila);
            }
            rs.close();
            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No hay asignaciones activas para este centro", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar asignaciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosSeleccion() {
        int fila = tablaAsignaciones.getSelectedRow();
        if (fila == -1) {
            btnModificar.setEnabled(false);
            return;
        }
        idAsignacionSeleccionada = (int) modeloTabla.getValueAt(fila, 0);
        String loteNombre = (String) modeloTabla.getValueAt(fila, 1);
        cantidadOriginal = (int) modeloTabla.getValueAt(fila, 2);
        folioOriginalAsignado = (String) modeloTabla.getValueAt(fila, 3);
        String observaciones = (String) modeloTabla.getValueAt(fila, 5);

        // Obtener id_lote original y id_centro
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_lote, id_centro FROM asignacion_vacunas_centro WHERE id_inventario = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idAsignacionSeleccionada);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                idLoteOriginal = rs.getInt("id_lote");
                idCentroOriginal = rs.getInt("id_centro");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lblIdAsignacion.setText(String.valueOf(idAsignacionSeleccionada));
        lblLoteOriginal.setText(loteNombre);
        lblCantidadOriginal.setText(String.valueOf(cantidadOriginal));
        txtObservaciones.setText(observaciones);

        // Seleccionar el centro original en el combo
        for (int i = 0; i < cbCentroSalud.getItemCount(); i++) {
            String item = cbCentroSalud.getItemAt(i);
            if (mapaCentros.get(item) != null && mapaCentros.get(item) == idCentroOriginal) {
                cbCentroSalud.setSelectedIndex(i);
                break;
            }
        }
        btnModificar.setEnabled(true);
    }

    private void cargarInfoLote() {
        String loteSeleccionado = (String) cbLoteVacuna.getSelectedItem();
        if (loteSeleccionado != null && mapaLotes.containsKey(loteSeleccionado)) {
            LoteInfo info = mapaLotes.get(loteSeleccionado);
            lblCantidadDisponible.setText(String.valueOf(info.cantidadDisponible));
            txtFolioActual.setText(info.folioActual);
            lblLoteInfo.setText("Lote: " + info.loteVacunacion);
            actualizarVistaPreviaFolio();
        } else {
            lblCantidadDisponible.setText("0");
            txtFolioActual.setText("");
            lblLoteInfo.setText("Seleccione un lote");
        }
    }

    private void actualizarVistaPreviaFolio() {
        String loteSeleccionado = (String) cbLoteVacuna.getSelectedItem();
        if (loteSeleccionado == null || !mapaLotes.containsKey(loteSeleccionado)) {
            txtFolioNuevo.setText("");
            return;
        }
        LoteInfo info = mapaLotes.get(loteSeleccionado);
        String cantidadTexto = txtCantidadAsignar.getText().trim();
        if (cantidadTexto.isEmpty()) {
            txtFolioNuevo.setText("");
            return;
        }
        try {
            int cantidad = Integer.parseInt(cantidadTexto);
            if (cantidad > 0 && cantidad <= info.cantidadDisponible) {
                txtFolioNuevo.setText(info.getFoliosAsignados(cantidad));
                txtFolioNuevo.setForeground(new Color(0,150,0));
            } else if (cantidad > info.cantidadDisponible) {
                txtFolioNuevo.setText("Excede stock disponible");
                txtFolioNuevo.setForeground(Color.RED);
            } else {
                txtFolioNuevo.setText("Cantidad inválida");
                txtFolioNuevo.setForeground(Color.RED);
            }
        } catch (NumberFormatException e) {
            txtFolioNuevo.setText("Cantidad inválida");
            txtFolioNuevo.setForeground(Color.RED);
        }
    }

    private void modificarAsignacion() {
        if (idAsignacionSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una asignación primero", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nuevoCentroNombre = (String) cbCentroSalud.getSelectedItem();
        if (nuevoCentroNombre == null || nuevoCentroNombre.equals("-- Seleccione un centro --")) {
            JOptionPane.showMessageDialog(this, "Seleccione un centro de salud válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int nuevoIdCentro = mapaCentros.get(nuevoCentroNombre);

        String nuevoLoteNombre = (String) cbLoteVacuna.getSelectedItem();
        if (nuevoLoteNombre == null || nuevoLoteNombre.equals("-- Seleccione un lote --")) {
            JOptionPane.showMessageDialog(this, "Seleccione un lote de vacuna", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LoteInfo nuevoLoteInfo = mapaLotes.get(nuevoLoteNombre);
        String cantidadTexto = txtCantidadAsignar.getText().trim();
        if (cantidadTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la nueva cantidad", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int nuevaCantidad = Integer.parseInt(cantidadTexto);
        if (nuevaCantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nuevaCantidad > nuevoLoteInfo.cantidadDisponible) {
            JOptionPane.showMessageDialog(this, "Cantidad insuficiente en el lote seleccionado. Disponible: " + nuevoLoteInfo.cantidadDisponible, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Confirmar modificación?\n\n" +
            "ID Asignación: " + idAsignacionSeleccionada + "\n" +
            "Centro actual: " + cbCentroSalud.getSelectedItem() + "\n" +
            "Lote original: " + lblLoteOriginal.getText() + " (Cantidad original: " + cantidadOriginal + ")\n" +
            "Nuevo lote: " + nuevoLoteNombre + "\n" +
            "Nueva cantidad: " + nuevaCantidad + "\n" +
            "Nuevos folios a asignar: " + nuevoLoteInfo.getFoliosAsignados(nuevaCantidad),
            "Confirmar modificación",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            conn = Conexion_DB.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Revertir la asignación original (devolver cantidad al lote original)
            revertirAsignacionOriginal(conn, idLoteOriginal, cantidadOriginal, folioOriginalAsignado);

            // 2. Actualizar la asignación con los nuevos datos (id_lote, cantidad, folio, id_centro)
            String nuevoFolio = nuevoLoteInfo.getFoliosAsignados(nuevaCantidad);
            String sqlUpdate = "UPDATE asignacion_vacunas_centro SET id_centro = ?, id_lote = ?, cantidad_asignar = ?, folio = ?, observaciones = ? WHERE id_inventario = ?";
            PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate);
            pstmtUpdate.setInt(1, nuevoIdCentro);
            pstmtUpdate.setInt(2, nuevoLoteInfo.idLote);
            pstmtUpdate.setInt(3, nuevaCantidad);
            pstmtUpdate.setString(4, nuevoFolio);
            pstmtUpdate.setString(5, txtObservaciones.getText().trim());
            pstmtUpdate.setInt(6, idAsignacionSeleccionada);
            pstmtUpdate.executeUpdate();
            pstmtUpdate.close();

            // 3. Restar la nueva cantidad del nuevo lote y actualizar su folio
            int nuevaCantidadLote = nuevoLoteInfo.cantidadDisponible - nuevaCantidad;
            String nuevoFolioLote = nuevoLoteInfo.generarNuevoFolio(nuevaCantidad);
            String sqlUpdateLote = "UPDATE lotes SET cantidad = ?, folio = ? WHERE id_lote = ?";
            PreparedStatement pstmtLote = conn.prepareStatement(sqlUpdateLote);
            pstmtLote.setInt(1, nuevaCantidadLote);
            pstmtLote.setString(2, nuevoFolioLote);
            pstmtLote.setInt(3, nuevoLoteInfo.idLote);
            pstmtLote.executeUpdate();
            pstmtLote.close();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Asignación modificada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarAsignacionesPorCentro(); // refrescar tabla

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void revertirAsignacionOriginal(Connection conn, int idLoteOriginal, int cantidadOriginal, String folioAsignado) throws SQLException {
        // Obtener estado actual del lote original
        String sqlSelect = "SELECT cantidad, folio FROM lotes WHERE id_lote = ? FOR UPDATE";
        PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect);
        pstmtSelect.setInt(1, idLoteOriginal);
        ResultSet rs = pstmtSelect.executeQuery();
        if (!rs.next()) {
            throw new SQLException("No se encontró el lote original con id " + idLoteOriginal);
        }
        int cantidadActual = rs.getInt("cantidad");
        String folioActual = rs.getString("folio");
        rs.close();
        pstmtSelect.close();

        // Calcular nuevo folio: se debe "expandir" el rango para incluir el folio asignado
        // Asumimos que la asignación original fue la última realizada sobre ese lote
        // Ejemplo: folioActual = "21-1000", folioAsignado = "1-20" -> nuevo folio = "1-1000"
        String[] asignadoParts = folioAsignado.split("-");
        String[] actualParts = folioActual.split("-");
        if (asignadoParts.length != 2 || actualParts.length != 2) {
            throw new SQLException("Formato de folio inválido para revertir: " + folioAsignado + " / " + folioActual);
        }
        int inicioAsignado = Integer.parseInt(asignadoParts[0]);
        int finActual = Integer.parseInt(actualParts[1]);
        String nuevoFolio = inicioAsignado + "-" + finActual;
        int nuevaCantidad = cantidadActual + cantidadOriginal;

        // Actualizar lote original
        String sqlUpdate = "UPDATE lotes SET cantidad = ?, folio = ? WHERE id_lote = ?";
        PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate);
        pstmtUpdate.setInt(1, nuevaCantidad);
        pstmtUpdate.setString(2, nuevoFolio);
        pstmtUpdate.setInt(3, idLoteOriginal);
        int affected = pstmtUpdate.executeUpdate();
        pstmtUpdate.close();

        if (affected == 0) {
            throw new SQLException("No se pudo revertir el lote original");
        }
    }

    private void limpiarFormulario() {
        idAsignacionSeleccionada = -1;
        idLoteOriginal = -1;
        cantidadOriginal = -1;
        folioOriginalAsignado = "";
        lblIdAsignacion.setText("---");
        lblLoteOriginal.setText("---");
        lblCantidadOriginal.setText("---");
        txtObservaciones.setText("");
        cbLoteVacuna.setSelectedIndex(0);
        txtCantidadAsignar.setText("");
        txtFolioNuevo.setText("");
        txtFolioActual.setText("");
        lblCantidadDisponible.setText("0");
        lblLoteInfo.setText("Seleccione un lote");
        btnModificar.setEnabled(false);
        modeloTabla.setRowCount(0);
        cbCentroSalud.setSelectedIndex(0);
    }
}