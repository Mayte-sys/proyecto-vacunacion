package aplicadorVacunas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import sector_salud.Conexion_DB;

public class ReportesVacunacion extends JPanel {

    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color FONDO = new Color(245, 245, 248);

    private JComboBox<String> cbCentro, cbCampana;
    private JTextField txtFechaInicio, txtFechaFin, txtCURP;
    private JButton btnFiltrar, btnLimpiar, btnRegresar;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;

    private Map<String, Integer> mapaCentros;    // nombre centro -> id_centro
    private Map<String, Integer> mapaCampanas;   // nombre campaña -> id_campana

    private CardLayout cardLayout;
    private JPanel contenedor;

    public ReportesVacunacion(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.mapaCentros = new HashMap<>();
        this.mapaCampanas = new HashMap<>();

        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Reportes de Aplicaciones de Vacunas");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(15, 15));
        centro.setBackground(FONDO);

        // Panel de filtros
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        panelFiltros.setBackground(FONDO);
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de búsqueda"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Centro
        gbc.gridx = 0; gbc.gridy = 0;
        panelFiltros.add(new JLabel("Centro de salud:"), gbc);
        gbc.gridx = 1;
        cbCentro = new JComboBox<>();
        cbCentro.addItem("Todos");
        cargarCentros();
        cbCentro.addActionListener(e -> cargarCampanasPorCentro());
        panelFiltros.add(cbCentro, gbc);

        // Campaña
        gbc.gridx = 2; gbc.gridy = 0;
        panelFiltros.add(new JLabel("Campaña:"), gbc);
        gbc.gridx = 3;
        cbCampana = new JComboBox<>();
        cbCampana.addItem("Todas");
        panelFiltros.add(cbCampana, gbc);

        // Rango de fechas
        gbc.gridx = 0; gbc.gridy = 1;
        panelFiltros.add(new JLabel("Fecha inicio (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        txtFechaInicio = new JTextField(12);
        panelFiltros.add(txtFechaInicio, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        panelFiltros.add(new JLabel("Fecha fin (dd/MM/yyyy):"), gbc);
        gbc.gridx = 3;
        txtFechaFin = new JTextField(12);
        panelFiltros.add(txtFechaFin, gbc);

        // CURP
        gbc.gridx = 0; gbc.gridy = 2;
        panelFiltros.add(new JLabel("CURP del paciente:"), gbc);
        gbc.gridx = 1;
        txtCURP = new JTextField(15);
        panelFiltros.add(txtCURP, gbc);

        // Botones
        JPanel panelBotonesFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnFiltrar = new JButton("Generar reporte");
        btnFiltrar.addActionListener(e -> generarReporte());
        btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panelBotonesFiltros.add(btnFiltrar);
        panelBotonesFiltros.add(btnLimpiar);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        panelFiltros.add(panelBotonesFiltros, gbc);

        centro.add(panelFiltros, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Paciente", "CURP", "Campaña", "Centro", "Empleado", "Fecha y hora"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaResultados = new JTable(modeloTabla);
        tablaResultados.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scroll = new JScrollPane(tablaResultados);
        scroll.setBorder(BorderFactory.createTitledBorder("Aplicaciones registradas"));
        centro.add(scroll, BorderLayout.CENTER);

        // Botón regresar
        JPanel panelRegresar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRegresar = new JButton("← Regresar al menú");
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo4"));
        panelRegresar.add(btnRegresar);
        centro.add(panelRegresar, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);
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

    private void cargarCampanasPorCentro() {
        String centroSel = (String) cbCentro.getSelectedItem();
        Integer idCentro = null;
        if (centroSel != null && !centroSel.equals("Todos")) {
            idCentro = mapaCentros.get(centroSel);
        }

        cbCampana.removeAllItems();
        cbCampana.addItem("Todas");
        mapaCampanas.clear();

        if (idCentro == null) {
            // Todas las campañas activas
            try (Connection conn = Conexion_DB.obtenerConexion()) {
                String sql = "SELECT id_campana, nombre FROM campana_vacunacion WHERE activo = true ORDER BY nombre";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    int id = rs.getInt("id_campana");
                    String nombre = rs.getString("nombre");
                    mapaCampanas.put(nombre, id);
                    cbCampana.addItem(nombre);
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try (Connection conn = Conexion_DB.obtenerConexion()) {
                String sql = "SELECT id_campana, nombre FROM campana_vacunacion WHERE id_centro = ? AND activo = true ORDER BY nombre";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, idCentro);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id_campana");
                    String nombre = rs.getString("nombre");
                    mapaCampanas.put(nombre, id);
                    cbCampana.addItem(nombre);
                }
                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void generarReporte() {
        modeloTabla.setRowCount(0);

        StringBuilder sql = new StringBuilder(
            "SELECT a.id_aplicacion, CONCAT(c.nombre, ' ', c.apellido) AS paciente, a.curp_paciente, " +
            "camp.nombre AS campana, cs.nombre AS centro, CONCAT(e.nombre, ' ', e.apellido) AS empleado, " +
            "a.fecha " +
            "FROM aplicador_vacunas a " +
            "JOIN ciudadanos c ON a.curp_paciente = c.curp " +
            "JOIN campana_vacunacion camp ON a.id_campana = camp.id_campana " +
            "JOIN centros_salud cs ON a.id_centro = cs.id_centro " +
            "JOIN empleados e ON a.id_empleado = e.id_empleado " +
            "WHERE 1=1"
        );

        java.util.List<Object> params = new java.util.ArrayList<>();

        // Filtro centro
        String centroSel = (String) cbCentro.getSelectedItem();
        if (centroSel != null && !centroSel.equals("Todos")) {
            Integer idCentro = mapaCentros.get(centroSel);
            if (idCentro != null) {
                sql.append(" AND a.id_centro = ?");
                params.add(idCentro);
            }
        }

        // Filtro campaña
        String campanaSel = (String) cbCampana.getSelectedItem();
        if (campanaSel != null && !campanaSel.equals("Todas")) {
            Integer idCampana = mapaCampanas.get(campanaSel);
            if (idCampana != null) {
                sql.append(" AND a.id_campana = ?");
                params.add(idCampana);
            }
        }

        // Filtro CURP
        String curp = txtCURP.getText().trim().toUpperCase();
        if (!curp.isEmpty()) {
            sql.append(" AND a.curp_paciente = ?");
            params.add(curp);
        }

        // Filtro fechas (columna "fecha" es TIMESTAMP)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            if (!txtFechaInicio.getText().trim().isEmpty()) {
                java.util.Date fechaInicio = sdf.parse(txtFechaInicio.getText().trim());
                sql.append(" AND DATE(a.fecha) >= ?");
                params.add(new java.sql.Date(fechaInicio.getTime()));
            }
            if (!txtFechaFin.getText().trim().isEmpty()) {
                java.util.Date fechaFin = sdf.parse(txtFechaFin.getText().trim());
                sql.append(" AND DATE(a.fecha) <= ?");
                params.add(new java.sql.Date(fechaFin.getTime()));
            }
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        sql.append(" ORDER BY a.fecha DESC");

        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat sdfHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_aplicacion"),
                    rs.getString("paciente"),
                    rs.getString("curp_paciente"),
                    rs.getString("campana"),
                    rs.getString("centro"),
                    rs.getString("empleado"),
                    sdfHora.format(rs.getTimestamp("fecha"))
                };
                modeloTabla.addRow(fila);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        JOptionPane.showMessageDialog(this, "Se encontraron " + modeloTabla.getRowCount() + " aplicaciones.",
                "Reporte generado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarFiltros() {
        cbCentro.setSelectedIndex(0);
        cbCampana.removeAllItems();
        cbCampana.addItem("Todas");
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        txtCURP.setText("");
        generarReporte();
    }
}