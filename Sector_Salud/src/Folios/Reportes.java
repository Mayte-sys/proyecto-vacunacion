/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Folios;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import sector_salud.Conexion_DB;

public class Reportes extends JPanel {
    
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
    
    public Reportes(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Reportes de Lotes");
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
            "Lotes próximos a caducar (7 días)",
            "Lotes por País",
            "Lotes por Empresa",
            "Todos los lotes activos"
        });
        cbTipoReporte.setFont(new Font("Arial", Font.PLAIN, 14));
        cbTipoReporte.setPreferredSize(new Dimension(300, 30));
        cbTipoReporte.addActionListener(e -> actualizarFiltro());
        
        JLabel lblFiltro = new JLabel("Filtrar:");
        lblFiltro.setFont(new Font("Arial", Font.BOLD, 14));
        
        cbFiltro = new JComboBox<>();
        cbFiltro.setFont(new Font("Arial", Font.PLAIN, 14));
        cbFiltro.setPreferredSize(new Dimension(250, 30));
        
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
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo2"));
        
        panelBotones.add(btnExportar);
        panelBotones.add(btnRegresar);
        
        panelInferior.add(lblTotalRegistros, BorderLayout.WEST);
        panelInferior.add(panelBotones, BorderLayout.EAST);
        
        centro.add(panelInferior, BorderLayout.SOUTH);
        
        add(centro, BorderLayout.CENTER);
        
        // Cargar filtros iniciales
        actualizarFiltro();
    }
    
    private void actualizarFiltro() {
        cbFiltro.removeAllItems();
        String tipo = (String) cbTipoReporte.getSelectedItem();
        
        if (tipo.equals("Lotes por País")) {
            cbFiltro.addItem("Todos los países");
            cargarPaisesEnFiltro();
        } else if (tipo.equals("Lotes por Empresa")) {
            cbFiltro.addItem("Todas las empresas");
            cargarEmpresasEnFiltro();
        } else {
            cbFiltro.addItem("---");
            cbFiltro.setEnabled(false);
            return;
        }
        cbFiltro.setEnabled(true);
    }
    
    private void cargarPaisesEnFiltro() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT nombre FROM paises ORDER BY nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                cbFiltro.addItem(rs.getString("nombre"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void cargarEmpresasEnFiltro() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT DISTINCT empresa FROM lotes ORDER BY empresa";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                cbFiltro.addItem(rs.getString("empresa"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    private void generarReporte() {
        String tipo = (String) cbTipoReporte.getSelectedItem();
        String filtro = (String) cbFiltro.getSelectedItem();
        
        try {
            if (tipo.equals("Lotes próximos a caducar (7 días)")) {
                reporteProximosACaducar();
            } else if (tipo.equals("Lotes por País")) {
                reportePorPais(filtro);
            } else if (tipo.equals("Lotes por Empresa")) {
                reportePorEmpresa(filtro);
            } else if (tipo.equals("Todos los lotes activos")) {
                reporteTodosLosLotes();
            }
            btnExportar.setEnabled(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void reporteProximosACaducar() throws SQLException {
        // Configurar columnas
        String[] columnas = {"ID", "Lote Vacunación", "Folio", "País", "Empresa", 
                            "Nombre Comercial", "Fecha Caducidad", "Días Restantes", "Cantidad", "Enfermedades"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        // Calcular fecha dentro de 7 días
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        java.sql.Date fechaLimite = new java.sql.Date(cal.getTimeInMillis());
        java.sql.Date hoy = new java.sql.Date(System.currentTimeMillis());
        
        String sql = "SELECT l.id_lote, l.lote_vacunacion, l.folio, p.nombre AS pais, " +
                    "l.empresa, l.nombre_comercial, l.fecha_caducidad, l.cantidad, l.enfermedades " +
                    "FROM lotes l " +
                    "JOIN paises p ON l.id_pais = p.id " +
                    "WHERE l.fecha_caducidad BETWEEN ? AND ? " +
                    "ORDER BY l.fecha_caducidad ASC";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, hoy);
            pstmt.setDate(2, fechaLimite);
            ResultSet rs = pstmt.executeQuery();
            
            int total = 0;
            while (rs.next()) {
                java.sql.Date fechaCad = rs.getDate("fecha_caducidad");
                long diasRestantes = (fechaCad.getTime() - hoy.getTime()) / (1000 * 60 * 60 * 24);
                
                Object[] fila = {
                    rs.getInt("id_lote"),
                    rs.getString("lote_vacunacion"),
                    rs.getString("folio"),
                    rs.getString("pais"),
                    rs.getString("empresa"),
                    rs.getString("nombre_comercial"),
                    fechaCad,
                    diasRestantes,
                    rs.getInt("cantidad"),
                    rs.getString("enfermedades")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            rs.close();
            lblTotalRegistros.setText("Total de registros: " + total);
            
            if (total == 0) {
                JOptionPane.showMessageDialog(this, "No hay lotes próximos a caducar en los próximos 7 días", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void reportePorPais(String pais) throws SQLException {
        String[] columnas = {"ID", "Lote Vacunación", "Folio", "Empresa", "Nombre Comercial", 
                            "Fecha Caducidad", "Cantidad", "Enfermedades"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql;
        if (pais.equals("Todos los países")) {
            sql = "SELECT l.id_lote, l.lote_vacunacion, l.folio, p.nombre AS pais, " +
                  "l.empresa, l.nombre_comercial, l.fecha_caducidad, l.cantidad, l.enfermedades " +
                  "FROM lotes l JOIN paises p ON l.id_pais = p.id " +
                  "ORDER BY p.nombre, l.fecha_caducidad";
        } else {
            sql = "SELECT l.id_lote, l.lote_vacunacion, l.folio, p.nombre AS pais, " +
                  "l.empresa, l.nombre_comercial, l.fecha_caducidad, l.cantidad, l.enfermedades " +
                  "FROM lotes l JOIN paises p ON l.id_pais = p.id " +
                  "WHERE p.nombre = ? ORDER BY l.fecha_caducidad";
        }
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (!pais.equals("Todos los países")) {
                pstmt.setString(1, pais);
            }
            
            ResultSet rs = pstmt.executeQuery();
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_lote"),
                    rs.getString("lote_vacunacion"),
                    rs.getString("folio"),
                    rs.getString("empresa"),
                    rs.getString("nombre_comercial"),
                    rs.getDate("fecha_caducidad"),
                    rs.getInt("cantidad"),
                    rs.getString("enfermedades")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            rs.close();
            lblTotalRegistros.setText("Total de registros: " + total + " - País: " + pais);
        }
    }
    
    private void reportePorEmpresa(String empresa) throws SQLException {
        String[] columnas = {"ID", "Lote Vacunación", "Folio", "País", "Nombre Comercial", 
                            "Fecha Caducidad", "Cantidad", "Enfermedades"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql;
        if (empresa.equals("Todas las empresas")) {
            sql = "SELECT l.id_lote, l.lote_vacunacion, l.folio, p.nombre AS pais, " +
                  "l.empresa, l.nombre_comercial, l.fecha_caducidad, l.cantidad, l.enfermedades " +
                  "FROM lotes l JOIN paises p ON l.id_pais = p.id " +
                  "ORDER BY l.empresa, l.fecha_caducidad";
        } else {
            sql = "SELECT l.id_lote, l.lote_vacunacion, l.folio, p.nombre AS pais, " +
                  "l.empresa, l.nombre_comercial, l.fecha_caducidad, l.cantidad, l.enfermedades " +
                  "FROM lotes l JOIN paises p ON l.id_pais = p.id " +
                  "WHERE l.empresa = ? ORDER BY l.fecha_caducidad";
        }
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (!empresa.equals("Todas las empresas")) {
                pstmt.setString(1, empresa);
            }
            
            ResultSet rs = pstmt.executeQuery();
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_lote"),
                    rs.getString("lote_vacunacion"),
                    rs.getString("folio"),
                    rs.getString("pais"),
                    rs.getString("nombre_comercial"),
                    rs.getDate("fecha_caducidad"),
                    rs.getInt("cantidad"),
                    rs.getString("enfermedades")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            rs.close();
            lblTotalRegistros.setText("Total de registros: " + total + " - Empresa: " + empresa);
        }
    }
    
    private void reportePorEnfermedad(String enfermedad) throws SQLException {
        String[] columnas = {"ID", "Lote Vacunación", "Folio", "País", "Empresa", 
                            "Nombre Comercial", "Fecha Caducidad", "Cantidad"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql;
        if (enfermedad.equals("Todas las enfermedades")) {
            sql = "SELECT l.id_lote, l.lote_vacunacion, l.folio, p.nombre AS pais, " +
                  "l.empresa, l.nombre_comercial, l.fecha_caducidad, l.cantidad, l.enfermedades " +
                  "FROM lotes l JOIN paises p ON l.id_pais = p.id " +
                  "ORDER BY l.fecha_caducidad";
        } else {
            sql = "SELECT l.id_lote, l.lote_vacunacion, l.folio, p.nombre AS pais, " +
                  "l.empresa, l.nombre_comercial, l.fecha_caducidad, l.cantidad, l.enfermedades " +
                  "FROM lotes l JOIN paises p ON l.id_pais = p.id " +
                  "WHERE l.enfermedades LIKE ? ORDER BY l.fecha_caducidad";
        }
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (!enfermedad.equals("Todas las enfermedades")) {
                pstmt.setString(1, "%" + enfermedad + "%");
            }
            
            ResultSet rs = pstmt.executeQuery();
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_lote"),
                    rs.getString("lote_vacunacion"),
                    rs.getString("folio"),
                    rs.getString("pais"),
                    rs.getString("empresa"),
                    rs.getString("nombre_comercial"),
                    rs.getDate("fecha_caducidad"),
                    rs.getInt("candidato")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            rs.close();
            lblTotalRegistros.setText("Total de registros: " + total + " - Enfermedad: " + enfermedad);
        }
    }
    
    private void reporteTodosLosLotes() throws SQLException {
        String[] columnas = {"ID", "Lote Vacunación", "Folio", "País", "Empresa", 
                            "Nombre Comercial", "Fecha Caducidad", "Cantidad", "Enfermedades", "Fecha Registro"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql = "SELECT l.id_lote, l.lote_vacunacion, l.folio, p.nombre AS pais, " +
                    "l.empresa, l.nombre_comercial, l.fecha_caducidad, l.cantidad, l.enfermedades, l.fecha " +
                    "FROM lotes l JOIN paises p ON l.id_pais = p.id " +
                    "ORDER BY l.fecha DESC";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_lote"),
                    rs.getString("lote_vacunacion"),
                    rs.getString("folio"),
                    rs.getString("pais"),
                    rs.getString("empresa"),
                    rs.getString("nombre_comercial"),
                    rs.getDate("fecha_caducidad"),
                    rs.getInt("cantidad"),
                    rs.getString("enfermedades"),
                    rs.getTimestamp("fecha")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            lblTotalRegistros.setText("Total de registros: " + total);
        }
    }
    
    private void exportarCSV() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("reporte_lotes_" + 
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