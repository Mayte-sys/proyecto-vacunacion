/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package centros_salud;

/**
 *
 * @author marga
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import sector_salud.Conexion_DB;

public class Reportes_Salud extends JPanel {
    
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
    
    // Arreglo con todos los estados de México
    private final String[] ESTADOS_MEXICO = {
        "Aguascalientes", "Baja California", "Baja California Sur", "Campeche", 
        "Chiapas", "Chihuahua", "Ciudad de México", "Coahuila", "Colima", 
        "Durango", "Estado de México", "Guanajuato", "Guerrero", "Hidalgo", 
        "Jalisco", "Michoacán", "Morelos", "Nayarit", "Nuevo León", "Oaxaca", 
        "Puebla", "Querétaro", "Quintana Roo", "San Luis Potosí", "Sinaloa", 
        "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala", "Veracruz", "Yucatán", "Zacatecas"
    };
    
    public Reportes_Salud(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Reportes de Centros de Salud");
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
            "Centros activos",
            "Centros inactivos",
            "Todos los centros",
            "Centros por estado"
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
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo3"));
        
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
        
        if (tipo.equals("Centros por estado")) {
            cbFiltro.addItem("Todos los estados");
            for (String estado : ESTADOS_MEXICO) {
                cbFiltro.addItem(estado);
            }
            cbFiltro.setEnabled(true);
        } else {
            cbFiltro.addItem("---");
            cbFiltro.setEnabled(false);
        }
    }
    
    private void generarReporte() {
        String tipo = (String) cbTipoReporte.getSelectedItem();
        
        try {
            if (tipo.equals("Centros activos")) {
                reportePorEstadoActivo(true);
            } else if (tipo.equals("Centros inactivos")) {
                reportePorEstadoActivo(false);
            } else if (tipo.equals("Todos los centros")) {
                reporteTodosLosCentros();
            } else if (tipo.equals("Centros por estado")) {
                String estado = (String) cbFiltro.getSelectedItem();
                reportePorEstado(estado);
            }
            btnExportar.setEnabled(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void reportePorEstadoActivo(boolean activo) throws SQLException {
        String estadoTexto = activo ? "ACTIVOS" : "INACTIVOS";
        String[] columnas = {"ID", "Nombre", "Calle", "Núm. Ext", "Núm. Int", "Colonia", 
                            "Municipio", "Estado", "C.P.", "Teléfono", "Horario Apertura", "Horario Cierre"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql = "SELECT id_centro, nombre, calle, numero_exterior, numero_interior, " +
                    "colonia, municipio, estado, codigo_postal, telefono, " +
                    "horario_apertura, horario_cierre " +
                    "FROM centros_salud WHERE activo = ? ORDER BY nombre";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, activo);
            ResultSet rs = pstmt.executeQuery();
            
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_centro"),
                    rs.getString("nombre"),
                    rs.getString("calle"),
                    rs.getString("numero_exterior"),
                    rs.getString("numero_interior") != null ? rs.getString("numero_interior") : "",
                    rs.getString("colonia"),
                    rs.getString("municipio"),
                    rs.getString("estado"),
                    rs.getString("codigo_postal"),
                    rs.getString("telefono"),
                    rs.getTime("horario_apertura"),
                    rs.getTime("horario_cierre")
                };
                modeloTabla.addRow(fila);
                total++;
            }
            rs.close();
            lblTotalRegistros.setText("Total de registros: " + total + " - Centros " + estadoTexto);
            
            if (total == 0) {
                JOptionPane.showMessageDialog(this, "No hay centros " + estadoTexto.toLowerCase(), "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void reporteTodosLosCentros() throws SQLException {
        String[] columnas = {"ID", "Nombre", "Calle", "Núm. Ext", "Núm. Int", "Colonia", 
                            "Municipio", "Estado", "C.P.", "Teléfono", "Horario Apertura", 
                            "Horario Cierre", "Estado Activo"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql = "SELECT id_centro, nombre, calle, numero_exterior, numero_interior, " +
                    "colonia, municipio, estado, codigo_postal, telefono, " +
                    "horario_apertura, horario_cierre, activo " +
                    "FROM centros_salud ORDER BY nombre";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int total = 0;
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_centro"),
                    rs.getString("nombre"),
                    rs.getString("calle"),
                    rs.getString("numero_exterior"),
                    rs.getString("numero_interior") != null ? rs.getString("numero_interior") : "",
                    rs.getString("colonia"),
                    rs.getString("municipio"),
                    rs.getString("estado"),
                    rs.getString("codigo_postal"),
                    rs.getString("telefono"),
                    rs.getTime("horario_apertura"),
                    rs.getTime("horario_cierre"),
                    rs.getBoolean("activo") ? "ACTIVO" : "INACTIVO"
                };
                modeloTabla.addRow(fila);
                total++;
            }
            lblTotalRegistros.setText("Total de registros: " + total);
        }
    }
    
    private void reportePorEstado(String estado) throws SQLException {
        String[] columnas = {"ID", "Nombre", "Calle", "Núm. Ext", "Núm. Int", "Colonia", 
                            "Municipio", "C.P.", "Teléfono", "Horario Apertura", "Horario Cierre", "Estado Activo"};
        modeloTabla.setColumnIdentifiers(columnas);
        modeloTabla.setRowCount(0);
        
        String sql;
        PreparedStatement pstmt;
        
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            if (estado.equals("Todos los estados")) {
                sql = "SELECT id_centro, nombre, calle, numero_exterior, numero_interior, " +
                      "colonia, municipio, estado, codigo_postal, telefono, " +
                      "horario_apertura, horario_cierre, activo " +
                      "FROM centros_salud ORDER BY estado, nombre";
                pstmt = conn.prepareStatement(sql);
            } else {
                sql = "SELECT id_centro, nombre, calle, numero_exterior, numero_interior, " +
                      "colonia, municipio, estado, codigo_postal, telefono, " +
                      "horario_apertura, horario_cierre, activo " +
                      "FROM centros_salud WHERE estado = ? ORDER BY nombre";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, estado);
            }
            
            ResultSet rs = pstmt.executeQuery();
            int total = 0;
            
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_centro"),
                    rs.getString("nombre"),
                    rs.getString("calle"),
                    rs.getString("numero_exterior"),
                    rs.getString("numero_interior") != null ? rs.getString("numero_interior") : "",
                    rs.getString("colonia"),
                    rs.getString("municipio"),
                    rs.getString("codigo_postal"),
                    rs.getString("telefono"),
                    rs.getTime("horario_apertura"),
                    rs.getTime("horario_cierre"),
                    rs.getBoolean("activo") ? "ACTIVO" : "INACTIVO"
                };
                modeloTabla.addRow(fila);
                total++;
            }
            rs.close();
            pstmt.close();
            
            lblTotalRegistros.setText("Total de registros: " + total + " - Estado: " + estado);
            
            if (total == 0 && !estado.equals("Todos los estados")) {
                JOptionPane.showMessageDialog(this, "No hay centros de salud en " + estado, "Información", JOptionPane.INFORMATION_MESSAGE);
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
            fileChooser.setSelectedFile(new java.io.File("reporte_centros_salud_" + 
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