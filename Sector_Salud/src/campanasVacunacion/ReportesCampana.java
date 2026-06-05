/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package campanasVacunacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import sector_salud.Conexion_DB;

public class ReportesCampana extends JPanel {

    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color FONDO = new Color(245, 245, 248);

    private JComboBox<String> cbEstado, cbCentro, cbTipo;
    private JTextField txtFechaInicio, txtFechaFin;
    private JButton btnFiltrar, btnLimpiar, btnRegresar;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private Map<String, Integer> mapaCentros; // nombre centro -> id_centro

    private CardLayout cardLayout;
    private JPanel contenedor;

    public ReportesCampana(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.mapaCentros = new HashMap<>();

        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Reportes de Campañas de Vacunación");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);

        // Panel central con filtros y tabla
        JPanel centro = new JPanel(new BorderLayout(15, 15));
        centro.setBackground(FONDO);

        // Panel de filtros (GridBagLayout para orden)
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        panelFiltros.setBackground(FONDO);
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de búsqueda"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Estado
        gbc.gridx = 0; gbc.gridy = 0;
        panelFiltros.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        cbEstado = new JComboBox<>(new String[]{"Todas", "Activas", "Inactivas"});
        panelFiltros.add(cbEstado, gbc);

        // Centro
        gbc.gridx = 2; gbc.gridy = 0;
        panelFiltros.add(new JLabel("Centro de salud:"), gbc);
        gbc.gridx = 3;
        cbCentro = new JComboBox<>();
        cbCentro.addItem("Todos");
        cargarCentros();
        panelFiltros.add(cbCentro, gbc);

        // Tipo
        gbc.gridx = 0; gbc.gridy = 1;
        panelFiltros.add(new JLabel("Tipo campaña:"), gbc);
        gbc.gridx = 1;
        cbTipo = new JComboBox<>(new String[]{"Todos", "Permanente", "Temporal"});
        panelFiltros.add(cbTipo, gbc);

        // Rango de fechas
        gbc.gridx = 2; gbc.gridy = 1;
        panelFiltros.add(new JLabel("Fecha inicio desde:"), gbc);
        gbc.gridx = 3;
        txtFechaInicio = new JTextField(12);
        txtFechaInicio.setToolTipText("dd/MM/yyyy");
        panelFiltros.add(txtFechaInicio, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelFiltros.add(new JLabel("Fecha fin hasta:"), gbc);
        gbc.gridx = 1;
        txtFechaFin = new JTextField(12);
        txtFechaFin.setToolTipText("dd/MM/yyyy");
        panelFiltros.add(txtFechaFin, gbc);

        // Botones de acción
        JPanel panelBotonesFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnFiltrar = new JButton("Generar reporte");
        btnFiltrar.addActionListener(e -> generarReporte());
        btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panelBotonesFiltros.add(btnFiltrar);
        panelBotonesFiltros.add(btnLimpiar);
        gbc.gridx = 2; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelFiltros.add(panelBotonesFiltros, gbc);

        centro.add(panelFiltros, BorderLayout.NORTH);

        // Tabla de resultados
        String[] columnas = {"ID", "Nombre", "Tipo", "Fecha inicio", "Fecha fin", "Centro", "Estado", "Descripción"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaResultados = new JTable(modeloTabla);
        tablaResultados.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scroll = new JScrollPane(tablaResultados);
        scroll.setBorder(BorderFactory.createTitledBorder("Campañas encontradas"));
        centro.add(scroll, BorderLayout.CENTER);

        // Botón regresar
        JPanel panelRegresar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRegresar = new JButton("← Regresar al menú");
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo1"));
        panelRegresar.add(btnRegresar);
        centro.add(panelRegresar, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);

        // Generar reporte inicial (todas activas por defecto)
        generarReporte();
    }

    private void cargarCentros() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_centro, nombre FROM centros_salud WHERE activo = true ORDER BY nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id_centro");
                String nombre = rs.getString("nombre");
                mapaCentros.put(nombre, id);
                cbCentro.addItem(nombre);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar centros: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarReporte() {
        modeloTabla.setRowCount(0);

        // Construir consulta dinámica
        StringBuilder sql = new StringBuilder(
            "SELECT c.id_campana, c.nombre, c.tipo, c.fecha_inicio, c.fecha_fin, " +
            "cs.nombre AS centro, c.activo, c.descripcion " +
            "FROM campana_vacunacion c " +
            "JOIN centros_salud cs ON c.id_centro = cs.id_centro " +
            "WHERE 1=1"
        );

        java.util.List<Object> params = new java.util.ArrayList<>();

        // Filtro estado
        String estado = (String) cbEstado.getSelectedItem();
        if ("Activas".equals(estado)) {
            sql.append(" AND c.activo = true");
        } else if ("Inactivas".equals(estado)) {
            sql.append(" AND c.activo = false");
        }
        // Filtro centro
        String centroSel = (String) cbCentro.getSelectedItem();
        if (centroSel != null && !centroSel.equals("Todos")) {
            Integer idCentro = mapaCentros.get(centroSel);
            if (idCentro != null) {
                sql.append(" AND c.id_centro = ?");
                params.add(idCentro);
            }
        }
        // Filtro tipo
        String tipoSel = (String) cbTipo.getSelectedItem();
        if (tipoSel != null && !tipoSel.equals("Todos")) {
            sql.append(" AND c.tipo = ?");
            params.add(tipoSel);
        }
        // Filtro rango fechas (fecha_inicio >= ? y fecha_fin <= ?)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date fechaInicio = null, fechaFin = null;
        try {
            if (!txtFechaInicio.getText().trim().isEmpty()) {
                fechaInicio = sdf.parse(txtFechaInicio.getText().trim());
                sql.append(" AND c.fecha_inicio >= ?");
                params.add(new java.sql.Date(fechaInicio.getTime()));
            }
            if (!txtFechaFin.getText().trim().isEmpty()) {
                fechaFin = sdf.parse(txtFechaFin.getText().trim());
                sql.append(" AND c.fecha_fin <= ?");
                params.add(new java.sql.Date(fechaFin.getTime()));
            }
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        sql.append(" ORDER BY c.fecha_inicio DESC");

        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String estadoTexto = rs.getBoolean("activo") ? "Activa" : "Inactiva";
                Object[] fila = {
                    rs.getInt("id_campana"),
                    rs.getString("nombre"),
                    rs.getString("tipo"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getString("centro"),
                    estadoTexto,
                    rs.getString("descripcion")
                };
                modeloTabla.addRow(fila);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFiltros() {
        cbEstado.setSelectedIndex(0); // Todas
        cbCentro.setSelectedIndex(0); // Todos
        cbTipo.setSelectedIndex(0);   // Todos
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        generarReporte(); // refrescar con filtros limpios
    }
}