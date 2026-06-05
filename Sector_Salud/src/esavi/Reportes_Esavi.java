/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package esavi;

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
public class Reportes_Esavi extends JPanel {
    
    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    
    private JComboBox<String> cbTipoReporte;
    private JComboBox<String> cbFiltro;
    private JButton btnGenerar, btnExportar;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalRegistros;
    private CardLayout cardLayout;
    private JPanel contenedor;
    
    // Mapas para almacenar datos de centros y doctores
    private Map<Integer, String> mapaCentros;
    private Map<String, String> mapaDoctores;
    
    // Arreglo con todos los estados de México
    private final String[] ESTADOS_MEXICO = {
        "Todos", "Aguascalientes", "Baja California", "Baja California Sur", "Campeche", 
        "Chiapas", "Chihuahua", "Ciudad de México", "Coahuila", "Colima", 
        "Durango", "Estado de México", "Guanajuato", "Guerrero", "Hidalgo", 
        "Jalisco", "Michoacán", "Morelos", "Nayarit", "Nuevo León", "Oaxaca", 
        "Puebla", "Querétaro", "Quintana Roo", "San Luis Potosí", "Sinaloa", 
        "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala", "Veracruz", "Yucatán", "Zacatecas"
    };
    
    public Reportes_Esavi(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.mapaCentros = new HashMap<>();
        this.mapaDoctores = new HashMap<>();
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Reportes de ESAVI");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);
        
        // Panel central
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(FONDO);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelFiltros.setBackground(FONDO);
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Seleccionar Reporte"));
        
        JLabel lblTipoReporte = new JLabel("Tipo de Reporte:");
        lblTipoReporte.setFont(new Font("Arial", Font.BOLD, 14));
        
        cbTipoReporte = new JComboBox<>(new String[]{
            "Reporte por CURP de ciudadano",
            "Reporte por Cédula Profesional",
            "Reporte por Centro de Salud",
            "Reporte por Estado del Centro de Salud"
        });
        cbTipoReporte.setFont(new Font("Arial", Font.PLAIN, 14));
        cbTipoReporte.setPreferredSize(new Dimension(300, 30));
        cbTipoReporte.addActionListener(e -> actualizarFiltro());
        
        JLabel lblFiltro = new JLabel("Filtrar:");
        lblFiltro.setFont(new Font("Arial", Font.BOLD, 14));
        
        cbFiltro = new JComboBox<>();
        cbFiltro.setFont(new Font("Arial", Font.PLAIN, 14));
        cbFiltro.setPreferredSize(new Dimension(250, 30));
        cbFiltro.setEditable(true);
        
        btnGenerar = new JButton("Generar Reporte");
        btnGenerar.setBackground(GUINDO);
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFocusPainted(false);
        btnGenerar.setFont(new Font("Arial", Font.BOLD, 12));
        btnGenerar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerar.addActionListener(e -> generarReporte());
        
        panelFiltros.add(lblTipoReporte);
        panelFiltros.add(cbTipoReporte);
        panelFiltros.add(lblFiltro);
        panelFiltros.add(cbFiltro);
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
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Resultados"));
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
        btnExportar.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo5"));
        
        panelBotones.add(btnExportar);
        panelBotones.add(btnRegresar);
        
        panelInferior.add(lblTotalRegistros, BorderLayout.WEST);
        panelInferior.add(panelBotones, BorderLayout.EAST);
        
        centro.add(panelInferior, BorderLayout.SOUTH);
        
        add(centro, BorderLayout.CENTER);
        
        // Cargar datos iniciales para los combos
        cargarCentrosSalud();
        cargarDoctores();
        
        // Configurar filtro inicial
        actualizarFiltro();
    }
    
    private void actualizarFiltro() {
        cbFiltro.removeAllItems();
        String tipo = (String) cbTipoReporte.getSelectedItem();
        
        if (tipo.equals("Reporte por CURP de ciudadano")) {
            cbFiltro.addItem("Ingrese CURP...");
            cbFiltro.setEditable(true);
        } else if (tipo.equals("Reporte por Cédula Profesional")) {
            for (Map.Entry<String, String> entry : mapaDoctores.entrySet()) {
                cbFiltro.addItem(entry.getKey() + " - " + entry.getValue());
            }
            cbFiltro.setEditable(true);
        } else if (tipo.equals("Reporte por Centro de Salud")) {
            cbFiltro.addItem("Todos los centros");
            for (Map.Entry<Integer, String> entry : mapaCentros.entrySet()) {
                cbFiltro.addItem(entry.getValue());
            }
            cbFiltro.setEditable(true);
        } else if (tipo.equals("Reporte por Estado del Centro de Salud")) {
            for (String estado : ESTADOS_MEXICO) {
                cbFiltro.addItem(estado);
            }
            cbFiltro.setEditable(false);
        }
    }
    
    private void cargarCentrosSalud() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_centro, nombre FROM centros_salud ORDER BY nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            mapaCentros.clear();
            while (rs.next()) {
                mapaCentros.put(rs.getInt("id_centro"), rs.getString("nombre"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void cargarDoctores() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            // Usando solo el campo 'apellido' según tu tabla empleados
            String sql = "SELECT cedula_profesional, CONCAT(nombre, ' ', apellido) AS nombre_completo " +
                        "FROM empleados WHERE id_rol = 1 ORDER BY nombre_completo";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            mapaDoctores.clear();
            while (rs.next()) {
                mapaDoctores.put(rs.getString("cedula_profesional"), rs.getString("nombre_completo"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void generarReporte() {
        String tipo = (String) cbTipoReporte.getSelectedItem();
        String filtro = cbFiltro.getSelectedItem() != null ? cbFiltro.getSelectedItem().toString() : "";
        
        if (filtro.isEmpty() || filtro.equals("Ingrese CURP...")) {
            JOptionPane.showMessageDialog(this, "Seleccione o escriba un valor para filtrar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (tipo.equals("Reporte por CURP de ciudadano")) {
                reportePorCURP(filtro);
            } else if (tipo.equals("Reporte por Cédula Profesional")) {
                String cedula = filtro.split(" - ")[0];
                reportePorCedula(cedula);
            } else if (tipo.equals("Reporte por Centro de Salud")) {
                if (filtro.equals("Todos los centros")) {
                    reporteTodosLosCentros();
                } else {
                    reportePorCentroSalud(filtro);
                }
            } else if (tipo.equals("Reporte por Estado del Centro de Salud")) {
                reportePorEstadoCentro(filtro);
            }
            btnExportar.setEnabled(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void reportePorCURP(String curp) throws SQLException {
        String[] columnas = {"ID Reporte", "CURP", "Nombre Ciudadano", "Cédula Doctor", "Doctor", 
                            "Lote Vacuna", "Centro de Salud", "Estado", "Descripción", "Fecha"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        // Corregido: usando solo 'apellido' en lugar de 'apellido_paterno' y 'apellido_materno'
        String sql = "SELECT e.id_reporte, e.curp_cuidadano, e.nombre_cuidadano, e.cedula_doctor, " +
                    "CONCAT(emp.nombre, ' ', emp.apellido) AS doctor_nombre, " +
                    "l.lote_vacunacion, c.nombre AS centro_nombre, c.estado, e.descripcion, e.fecha " +
                    "FROM esavi e " +
                    "JOIN empleados emp ON e.cedula_doctor = emp.cedula_profesional " +
                    "JOIN lotes l ON e.id_lote = l.id_lote " +
                    "JOIN centros_salud c ON e.id_centro = c.id_centro " +
                    "WHERE e.curp_cuidadano LIKE ? " +
                    "ORDER BY e.fecha DESC";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + curp + "%");
            ResultSet rs = pstmt.executeQuery();
            
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_reporte"),
                    rs.getString("curp_cuidadano"),
                    rs.getString("nombre_cuidadano"),
                    rs.getString("cedula_doctor"),
                    rs.getString("doctor_nombre"),
                    rs.getString("lote_vacunacion"),
                    rs.getString("centro_nombre"),
                    rs.getString("estado"),
                    rs.getString("descripcion"),
                    rs.getTimestamp("fecha")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            rs.close();
            lblTotalRegistros.setText("Total de registros: " + total + " - CURP: " + curp);
            
            if (total == 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron reportes para la CURP: " + curp, "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void reportePorCedula(String cedula) throws SQLException {
        String[] columnas = {"ID Reporte", "CURP", "Nombre Ciudadano", "Cédula Doctor", "Doctor", 
                            "Lote Vacuna", "Centro de Salud", "Estado", "Descripción", "Fecha"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql = "SELECT e.id_reporte, e.curp_cuidadano, e.nombre_cuidadano, e.cedula_doctor, " +
                    "CONCAT(emp.nombre, ' ', emp.apellido) AS doctor_nombre, " +
                    "l.lote_vacunacion, c.nombre AS centro_nombre, c.estado, e.descripcion, e.fecha " +
                    "FROM esavi e " +
                    "JOIN empleados emp ON e.cedula_doctor = emp.cedula_profesional " +
                    "JOIN lotes l ON e.id_lote = l.id_lote " +
                    "JOIN centros_salud c ON e.id_centro = c.id_centro " +
                    "WHERE e.cedula_doctor = ? " +
                    "ORDER BY e.fecha DESC";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            ResultSet rs = pstmt.executeQuery();
            
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_reporte"),
                    rs.getString("curp_cuidadano"),
                    rs.getString("nombre_cuidadano"),
                    rs.getString("cedula_doctor"),
                    rs.getString("doctor_nombre"),
                    rs.getString("lote_vacunacion"),
                    rs.getString("centro_nombre"),
                    rs.getString("estado"),
                    rs.getString("descripcion"),
                    rs.getTimestamp("fecha")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            rs.close();
            lblTotalRegistros.setText("Total de registros: " + total + " - Cédula: " + cedula);
            
            if (total == 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron reportes para la cédula: " + cedula, "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void reportePorCentroSalud(String nombreCentro) throws SQLException {
        String[] columnas = {"ID Reporte", "CURP", "Nombre Ciudadano", "Cédula Doctor", "Doctor", 
                            "Lote Vacuna", "Centro de Salud", "Estado", "Descripción", "Fecha"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql = "SELECT e.id_reporte, e.curp_cuidadano, e.nombre_cuidadano, e.cedula_doctor, " +
                    "CONCAT(emp.nombre, ' ', emp.apellido) AS doctor_nombre, " +
                    "l.lote_vacunacion, c.nombre AS centro_nombre, c.estado, e.descripcion, e.fecha " +
                    "FROM esavi e " +
                    "JOIN empleados emp ON e.cedula_doctor = emp.cedula_profesional " +
                    "JOIN lotes l ON e.id_lote = l.id_lote " +
                    "JOIN centros_salud c ON e.id_centro = c.id_centro " +
                    "WHERE c.nombre LIKE ? " +
                    "ORDER BY e.fecha DESC";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nombreCentro + "%");
            ResultSet rs = pstmt.executeQuery();
            
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_reporte"),
                    rs.getString("curp_cuidadano"),
                    rs.getString("nombre_cuidadano"),
                    rs.getString("cedula_doctor"),
                    rs.getString("doctor_nombre"),
                    rs.getString("lote_vacunacion"),
                    rs.getString("centro_nombre"),
                    rs.getString("estado"),
                    rs.getString("descripcion"),
                    rs.getTimestamp("fecha")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            rs.close();
            lblTotalRegistros.setText("Total de registros: " + total + " - Centro: " + nombreCentro);
            
            if (total == 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron reportes para el centro: " + nombreCentro, "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void reporteTodosLosCentros() throws SQLException {
        String[] columnas = {"ID Reporte", "CURP", "Nombre Ciudadano", "Cédula Doctor", "Doctor", 
                            "Lote Vacuna", "Centro de Salud", "Estado", "Descripción", "Fecha"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql = "SELECT e.id_reporte, e.curp_cuidadano, e.nombre_cuidadano, e.cedula_doctor, " +
                    "CONCAT(emp.nombre, ' ', emp.apellido) AS doctor_nombre, " +
                    "l.lote_vacunacion, c.nombre AS centro_nombre, c.estado, e.descripcion, e.fecha " +
                    "FROM esavi e " +
                    "JOIN empleados emp ON e.cedula_doctor = emp.cedula_profesional " +
                    "JOIN lotes l ON e.id_lote = l.id_lote " +
                    "JOIN centros_salud c ON e.id_centro = c.id_centro " +
                    "ORDER BY c.nombre, e.fecha DESC";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_reporte"),
                    rs.getString("curp_cuidadano"),
                    rs.getString("nombre_cuidadano"),
                    rs.getString("cedula_doctor"),
                    rs.getString("doctor_nombre"),
                    rs.getString("lote_vacunacion"),
                    rs.getString("centro_nombre"),
                    rs.getString("estado"),
                    rs.getString("descripcion"),
                    rs.getTimestamp("fecha")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            lblTotalRegistros.setText("Total de registros: " + total + " - Todos los centros");
        }
    }
    
    private void reportePorEstadoCentro(String estado) throws SQLException {
        String[] columnas = {"ID Reporte", "CURP", "Nombre Ciudadano", "Cédula Doctor", "Doctor", 
                            "Lote Vacuna", "Centro de Salud", "Estado", "Descripción", "Fecha"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql;
        PreparedStatement pstmt;
        
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            if (estado.equals("Todos")) {
                sql = "SELECT e.id_reporte, e.curp_cuidadano, e.nombre_cuidadano, e.cedula_doctor, " +
                      "CONCAT(emp.nombre, ' ', emp.apellido) AS doctor_nombre, " +
                      "l.lote_vacunacion, c.nombre AS centro_nombre, c.estado, e.descripcion, e.fecha " +
                      "FROM esavi e " +
                      "JOIN empleados emp ON e.cedula_doctor = emp.cedula_profesional " +
                      "JOIN lotes l ON e.id_lote = l.id_lote " +
                      "JOIN centros_salud c ON e.id_centro = c.id_centro " +
                      "ORDER BY c.estado, e.fecha DESC";
                pstmt = conn.prepareStatement(sql);
            } else {
                sql = "SELECT e.id_reporte, e.curp_cuidadano, e.nombre_cuidadano, e.cedula_doctor, " +
                      "CONCAT(emp.nombre, ' ', emp.apellido) AS doctor_nombre, " +
                      "l.lote_vacunacion, c.nombre AS centro_nombre, c.estado, e.descripcion, e.fecha " +
                      "FROM esavi e " +
                      "JOIN empleados emp ON e.cedula_doctor = emp.cedula_profesional " +
                      "JOIN lotes l ON e.id_lote = l.id_lote " +
                      "JOIN centros_salud c ON e.id_centro = c.id_centro " +
                      "WHERE c.estado = ? " +
                      "ORDER BY e.fecha DESC";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, estado);
            }
            
            ResultSet rs = pstmt.executeQuery();
            int total = 0;
            
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_reporte"),
                    rs.getString("curp_cuidadano"),
                    rs.getString("nombre_cuidadano"),
                    rs.getString("cedula_doctor"),
                    rs.getString("doctor_nombre"),
                    rs.getString("lote_vacunacion"),
                    rs.getString("centro_nombre"),
                    rs.getString("estado"),
                    rs.getString("descripcion"),
                    rs.getTimestamp("fecha")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            rs.close();
            pstmt.close();
            
            lblTotalRegistros.setText("Total de registros: " + total + " - Estado: " + estado);
            
            if (total == 0 && !estado.equals("Todos")) {
                JOptionPane.showMessageDialog(this, "No se encontraron reportes para el estado: " + estado, "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void exportarCSV() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("reporte_esavi_" + 
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