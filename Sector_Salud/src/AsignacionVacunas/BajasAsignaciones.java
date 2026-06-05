/*checar despues porque no funciona correctamente
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

public class BajasAsignaciones extends JPanel {

    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    private static final Color ROJO = new Color(180, 50, 50);

    private JComboBox<String> cbCentroSalud;
    private JButton btnBuscar, btnDarBaja, btnLimpiar;
    private JTable tablaAsignaciones;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalRegistros;

    private CardLayout cardLayout;
    private JPanel contenedor;
    private Map<String, Integer> mapaCentros;
    private int idCentroSeleccionado = -1;

    public BajasAsignaciones(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.mapaCentros = new HashMap<>();

        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Bajas de Asignaciones de Vacunas");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(FONDO);

        // Panel búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBusqueda.setBackground(FONDO);
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar Asignaciones por Centro"));

        JLabel lblCentro = new JLabel("Centro de Salud:");
        lblCentro.setFont(new Font("Arial", Font.BOLD, 14));

        cbCentroSalud = new JComboBox<>();
        cbCentroSalud.setFont(new Font("Arial", Font.PLAIN, 14));
        cbCentroSalud.setPreferredSize(new Dimension(350, 30));
        cargarCentros();

        btnBuscar = new JButton("Buscar Asignaciones");
        btnBuscar.setBackground(GUINDO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.addActionListener(e -> buscarAsignaciones());

        panelBusqueda.add(lblCentro);
        panelBusqueda.add(cbCentroSalud);
        panelBusqueda.add(btnBuscar);
        centro.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla
        modeloTabla = new DefaultTableModel();
        tablaAsignaciones = new JTable(modeloTabla);
        tablaAsignaciones.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaAsignaciones.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaAsignaciones.getTableHeader().setBackground(GUINDO);
        tablaAsignaciones.getTableHeader().setForeground(Color.WHITE);
        tablaAsignaciones.setRowHeight(25);
        JScrollPane scrollTabla = new JScrollPane(tablaAsignaciones);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Asignaciones Activas del Centro"));
        centro.add(scrollTabla, BorderLayout.CENTER);

        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(FONDO);
        lblTotalRegistros = new JLabel("Total de registros: 0");
        lblTotalRegistros.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalRegistros.setForeground(GUINDO);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(FONDO);

        btnDarBaja = new JButton("Dar de Baja Asignación Seleccionada");
        btnDarBaja.setBackground(ROJO);
        btnDarBaja.setForeground(Color.WHITE);
        btnDarBaja.setFocusPainted(false);
        btnDarBaja.setEnabled(false);
        btnDarBaja.addActionListener(e -> darBajaAsignacion());

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(Color.GRAY);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.addActionListener(e -> limpiarTabla());

        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.setBackground(GUINDO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegresar.setPreferredSize(new Dimension(120, 35));
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo6"));

        panelBotones.add(btnDarBaja);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRegresar);

        panelInferior.add(lblTotalRegistros, BorderLayout.WEST);
        panelInferior.add(panelBotones, BorderLayout.EAST);
        centro.add(panelInferior, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
    }

    private void cargarCentros() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_centro, nombre FROM centros_salud WHERE activo = true ORDER BY nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            cbCentroSalud.removeAllItems();
            mapaCentros.clear();
            cbCentroSalud.addItem("-- Seleccione un centro --");
            while (rs.next()) {
                int id = rs.getInt("id_centro");
                String nombre = rs.getString("nombre");
                mapaCentros.put(nombre, id);
                cbCentroSalud.addItem(nombre);
            }
            rs.close();
            stmt.close();
            cbCentroSalud.setSelectedIndex(0);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar centros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarAsignaciones() {
        String centro = (String) cbCentroSalud.getSelectedItem();
        if (centro == null || centro.equals("-- Seleccione un centro --")) {
            JOptionPane.showMessageDialog(this, "Seleccione un centro de salud válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        idCentroSeleccionado = mapaCentros.get(centro);

        String[] columnas = {"ID Asignación", "Lote", "Folios Asignados", "Cantidad", "Fecha Asignación", "Observaciones"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);

        String sql = "SELECT a.id_inventario, l.lote_vacunacion, a.folio, a.cantidad_asignar, " +
                     "a.fecha_asignacion, a.observaciones " +
                     "FROM asignacion_vacunas_centro a " +
                     "JOIN lotes l ON a.id_lote = l.id_lote " +
                     "WHERE a.id_centro = ? AND a.activo = true " +
                     "ORDER BY a.fecha_asignacion DESC";

        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCentroSeleccionado);
            ResultSet rs = pstmt.executeQuery();
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_inventario"),
                    rs.getString("lote_vacunacion"),
                    rs.getString("folio"),
                    rs.getInt("cantidad_asignar"),
                    rs.getTimestamp("fecha_asignacion"),
                    rs.getString("observaciones")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            lblTotalRegistros.setText("Total de asignaciones activas: " + total);
            btnDarBaja.setEnabled(total > 0);
            if (total == 0) {
                JOptionPane.showMessageDialog(this, "No hay asignaciones activas para este centro", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void darBajaAsignacion() {
        int fila = tablaAsignaciones.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una asignación de la tabla", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idAsignacion = (int) modeloTabla.getValueAt(fila, 0);
        String lote = (String) modeloTabla.getValueAt(fila, 1);
        String foliosAsignados = (String) modeloTabla.getValueAt(fila, 2);
        int cantidad = (int) modeloTabla.getValueAt(fila, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Dar de baja la siguiente asignación?\n\n" +
            "Lote: " + lote + "\n" +
            "Folios asignados: " + foliosAsignados + "\n" +
            "Cantidad: " + cantidad + "\n\n" +
            "Se devolverán " + cantidad + " dosis al lote.\n" +
            "El folio del lote se reestablecerá al rango original.\n" +
            "Esta acción no se puede deshacer.",
            "Confirmar baja",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                realizarBaja(idAsignacion, cantidad, lote, foliosAsignados);
                JOptionPane.showMessageDialog(this, "Asignación dada de baja correctamente.\nSe han devuelto " + cantidad + " dosis al lote y el folio se ha restaurado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                buscarAsignaciones(); // Refrescar tabla
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al dar de baja: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void realizarBaja(int idAsignacion, int cantidadADevolver, String loteNombre, String foliosAsignados) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtUpdateAsignacion = null;
        PreparedStatement pstmtGetLote = null;
        PreparedStatement pstmtUpdateLote = null;

        try {
            conn = Conexion_DB.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Obtener información actual del lote
            String sqlGetLote = "SELECT id_lote, cantidad, folio FROM lotes WHERE lote_vacunacion = ?";
            pstmtGetLote = conn.prepareStatement(sqlGetLote);
            pstmtGetLote.setString(1, loteNombre);
            ResultSet rs = pstmtGetLote.executeQuery();
            if (!rs.next()) {
                throw new SQLException("No se encontró el lote: " + loteNombre);
            }
            int idLote = rs.getInt("id_lote");
            int cantidadActual = rs.getInt("cantidad");
            String folioActualLote = rs.getString("folio");
            rs.close();
            pstmtGetLote.close();

            // 2. Parsear folios para restaurar el rango completo
            // foliosAsignados ej: "1-20", folioActualLote ej: "21-1000"
            String[] asignadoParts = foliosAsignados.split("-");
            String[] actualParts = folioActualLote.split("-");
            if (asignadoParts.length != 2 || actualParts.length != 2) {
                throw new SQLException("Formato de folio inválido. Asignado: " + foliosAsignados + ", Actual: " + folioActualLote);
            }
            int inicioAsignado = Integer.parseInt(asignadoParts[0]);
            int finActual = Integer.parseInt(actualParts[1]); // el fin del rango actual es el fin original del lote
            String nuevoFolio = inicioAsignado + "-" + finActual;

            // 3. Calcular nueva cantidad
            int nuevaCantidad = cantidadActual + cantidadADevolver;

            // 4. Actualizar asignación: activo = false
            String sqlUpdateAsig = "UPDATE asignacion_vacunas_centro SET activo = false WHERE id_inventario = ?";
            pstmtUpdateAsignacion = conn.prepareStatement(sqlUpdateAsig);
            pstmtUpdateAsignacion.setInt(1, idAsignacion);
            pstmtUpdateAsignacion.executeUpdate();

            // 5. Actualizar lote: cantidad y folio
            String sqlUpdateLote = "UPDATE lotes SET cantidad = ?, folio = ? WHERE id_lote = ?";
            pstmtUpdateLote = conn.prepareStatement(sqlUpdateLote);
            pstmtUpdateLote.setInt(1, nuevaCantidad);
            pstmtUpdateLote.setString(2, nuevoFolio);
            pstmtUpdateLote.setInt(3, idLote);
            int filasLote = pstmtUpdateLote.executeUpdate();

            if (filasLote == 0) {
                throw new SQLException("No se pudo actualizar el lote. ID lote: " + idLote);
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            if (pstmtUpdateAsignacion != null) pstmtUpdateAsignacion.close();
            if (pstmtUpdateLote != null) pstmtUpdateLote.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private void limpiarTabla() {
        modeloTabla.setRowCount(0);
        lblTotalRegistros.setText("Total de registros: 0");
        btnDarBaja.setEnabled(false);
        cbCentroSalud.setSelectedIndex(0);
        idCentroSeleccionado = -1;
    }
}