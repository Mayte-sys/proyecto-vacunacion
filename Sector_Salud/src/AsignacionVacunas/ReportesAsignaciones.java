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
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import sector_salud.Conexion_DB;

/**
 *
 * @author marga
 */
public class ReportesAsignaciones extends JPanel {

    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    
    private JComboBox<String> cbCentroSalud;
    private JButton btnGenerar, btnExportar;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalRegistros;
    private CardLayout cardLayout;
    private JPanel contenedor;
    private Map<String, Integer> mapaCentros;
    
    public ReportesAsignaciones(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.mapaCentros = new HashMap<>();
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Reporte de Inventario por Centro de Salud");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);
        
        // Panel central
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(FONDO);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelFiltros.setBackground(FONDO);
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtrar por Centro de Salud"));
        
        JLabel lblCentro = new JLabel("Centro de Salud:");
        lblCentro.setFont(new Font("Arial", Font.BOLD, 14));
        
        cbCentroSalud = new JComboBox<>();
        cbCentroSalud.setFont(new Font("Arial", Font.PLAIN, 14));
        cbCentroSalud.setPreferredSize(new Dimension(350, 30));
        cargarCentros();
        
        btnGenerar = new JButton("Generar Reporte");
        btnGenerar.setBackground(GUINDO);
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFocusPainted(false);
        btnGenerar.setFont(new Font("Arial", Font.BOLD, 12));
        btnGenerar.addActionListener(e -> generarReporte());
        
        panelFiltros.add(lblCentro);
        panelFiltros.add(cbCentroSalud);
        panelFiltros.add(btnGenerar);
        
        centro.add(panelFiltros, BorderLayout.NORTH);
        
        // Tabla de resultados
        modeloTabla = new DefaultTableModel();
        tablaResultados = new JTable(modeloTabla);
        tablaResultados.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaResultados.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaResultados.getTableHeader().setBackground(GUINDO);
        tablaResultados.getTableHeader().setForeground(Color.WHITE);
        tablaResultados.setRowHeight(25);
        tablaResultados.setSelectionBackground(new Color(200, 200, 200));
        
        JScrollPane scrollTabla = new JScrollPane(tablaResultados);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Inventario Actual"));
        centro.add(scrollTabla, BorderLayout.CENTER);
        
        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(FONDO);
        
        lblTotalRegistros = new JLabel("Total de registros: 0");
        lblTotalRegistros.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalRegistros.setForeground(GUINDO);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(FONDO);
        
        btnExportar = new JButton("Exportar a CSV");
        btnExportar.setBackground(new Color(50, 150, 50));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFocusPainted(false);
        btnExportar.setFont(new Font("Arial", Font.BOLD, 12));
        btnExportar.setEnabled(false);
        btnExportar.addActionListener(e -> exportarCSV());
        
        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.setBackground(GUINDO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.setPreferredSize(new Dimension(120, 35));
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo6"));
        
        panelBotones.add(btnExportar);
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
            cbCentroSalud.addItem("-- Todos los centros --");
            while (rs.next()) {
                int id = rs.getInt("id_centro");
                String nombre = rs.getString("nombre");
                mapaCentros.put(nombre, id);
                cbCentroSalud.addItem(nombre);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar centros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generarReporte() {
        String centroSeleccionado = (String) cbCentroSalud.getSelectedItem();
        String[] columnas = {"Centro de Salud", "Lote de Vacuna", "Cantidad Asignada", "Folios Asignados", 
                             "Fecha Asignación", "Observaciones"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql;
        PreparedStatement pstmt = null;
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            if (centroSeleccionado.equals("-- Todos los centros --")) {
                sql = "SELECT c.nombre AS centro, l.lote_vacunacion, a.cantidad_asignar, a.folio, " +
                      "a.fecha_asignacion, a.observaciones " +
                      "FROM asignacion_vacunas_centro a " +
                      "JOIN centros_salud c ON a.id_centro = c.id_centro " +
                      "JOIN lotes l ON a.id_lote = l.id_lote " +
                      "WHERE a.activo = true " +
                      "ORDER BY c.nombre, a.fecha_asignacion DESC";
                pstmt = conn.prepareStatement(sql);
            } else {
                int idCentro = mapaCentros.get(centroSeleccionado);
                sql = "SELECT c.nombre AS centro, l.lote_vacunacion, a.cantidad_asignar, a.folio, " +
                      "a.fecha_asignacion, a.observaciones " +
                      "FROM asignacion_vacunas_centro a " +
                      "JOIN centros_salud c ON a.id_centro = c.id_centro " +
                      "JOIN lotes l ON a.id_lote = l.id_lote " +
                      "WHERE a.id_centro = ? AND a.activo = true " +
                      "ORDER BY a.fecha_asignacion DESC";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, idCentro);
            }
            
            ResultSet rs = pstmt.executeQuery();
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getString("centro"),
                    rs.getString("lote_vacunacion"),
                    rs.getInt("cantidad_asignar"),
                    rs.getString("folio"),
                    rs.getTimestamp("fecha_asignacion"),
                    rs.getString("observaciones")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            rs.close();
            pstmt.close();
            
            lblTotalRegistros.setText("Total de registros: " + total);
            btnExportar.setEnabled(total > 0);
            
            if (total == 0) {
                JOptionPane.showMessageDialog(this, "No hay inventario activo para el criterio seleccionado", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void exportarCSV() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("reporte_inventario_centros_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".csv"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String ruta = fileChooser.getSelectedFile().getAbsolutePath();
                if (!ruta.endsWith(".csv")) {
                    ruta += ".csv";
                }
                
                try (java.io.FileWriter writer = new java.io.FileWriter(ruta)) {
                    // Escribir encabezados
                    for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
                        writer.write("\"" + modeloTabla.getColumnName(i) + "\"");
                        if (i < modeloTabla.getColumnCount() - 1) writer.write(",");
                    }
                    writer.write("\n");
                    
                    // Escribir datos
                    for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                        for (int j = 0; j < modeloTabla.getColumnCount(); j++) {
                            Object valor = modeloTabla.getValueAt(i, j);
                            writer.write("\"" + (valor != null ? valor.toString() : "") + "\"");
                            if (j < modeloTabla.getColumnCount() - 1) writer.write(",");
                        }
                        writer.write("\n");
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Reporte exportado exitosamente a:\n" + ruta, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}