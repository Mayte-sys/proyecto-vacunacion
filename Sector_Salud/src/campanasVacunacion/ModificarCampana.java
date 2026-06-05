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
import java.util.HashMap;
import java.util.Map;
import sector_salud.Conexion_DB;

public class ModificarCampana extends JPanel {

    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);

    private JTable tablaCampanas;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JComboBox<String> cbFiltroCentro;
    private JComboBox<String> cbTipo;
    private JSpinner spFechaFin;
    private JButton btnCargar, btnGuardar, btnRefrescar, btnRegresar;

    private Map<String, Integer> mapaCentros; // nombre centro -> id_centro
    private CardLayout cardLayout;
    private JPanel contenedor;

    // Almacena la campaña seleccionada actualmente
    private Integer currentIdCampana = null;

    public ModificarCampana(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.mapaCentros = new HashMap<>();

        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Modificar Campaña de Vacunación");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);

        // Panel central dividido en dos secciones: búsqueda+tabla y edición
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setBackground(FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        // --- Panel superior: Filtros y tabla ---
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setBackground(FONDO);
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Seleccionar campaña"));

        // Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelFiltros.setBackground(FONDO);
        panelFiltros.add(new JLabel("Nombre:"));
        txtBuscar = new JTextField(20);
        panelFiltros.add(txtBuscar);
        panelFiltros.add(new JLabel("Centro:"));
        cbFiltroCentro = new JComboBox<>();
        cbFiltroCentro.addItem("Todos");
        cargarCentrosFiltro();
        panelFiltros.add(cbFiltroCentro);
        btnRefrescar = new JButton("Buscar");
        btnRefrescar.addActionListener(e -> cargarCampanasActivas());
        panelFiltros.add(btnRefrescar);
        panelSuperior.add(panelFiltros, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Tipo actual", "Fecha inicio", "Fecha fin actual", "Centro"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCampanas = new JTable(modeloTabla);
        tablaCampanas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaCampanas);
        panelSuperior.add(scroll, BorderLayout.CENTER);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weighty = 0.6;
        centro.add(panelSuperior, gbc);

        // --- Panel inferior: Formulario de modificación ---
        JPanel panelEdicion = new JPanel(new GridBagLayout());
        panelEdicion.setBackground(FONDO);
        panelEdicion.setBorder(BorderFactory.createTitledBorder("Datos a modificar"));
        GridBagConstraints gbcEdit = new GridBagConstraints();
        gbcEdit.insets = new Insets(8, 10, 8, 10);
        gbcEdit.fill = GridBagConstraints.HORIZONTAL;

        // Tipo (ComboBox)
        gbcEdit.gridx = 0; gbcEdit.gridy = 0;
        panelEdicion.add(new JLabel("Tipo de campaña:"), gbcEdit);
        gbcEdit.gridx = 1;
        cbTipo = new JComboBox<>(new String[]{"Permanente", "Temporal"});
        panelEdicion.add(cbTipo, gbcEdit);

        // Fecha fin (Spinner)
        gbcEdit.gridx = 0; gbcEdit.gridy = 1;
        panelEdicion.add(new JLabel("Fecha de fin:"), gbcEdit);
        gbcEdit.gridx = 1;
        spFechaFin = new JSpinner(new SpinnerDateModel());
        spFechaFin.setEditor(new JSpinner.DateEditor(spFechaFin, "dd/MM/yyyy"));
        panelEdicion.add(spFechaFin, gbcEdit);

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnCargar = new JButton("Cargar datos de campaña seleccionada");
        btnCargar.addActionListener(e -> cargarDatosSeleccionados());
        btnGuardar = new JButton("Guardar cambios");
        btnGuardar.setBackground(GUINDO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarCambios());
        btnRegresar = new JButton("← Regresar");
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo1"));
        panelBotones.add(btnCargar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnRegresar);

        gbcEdit.gridx = 0; gbcEdit.gridy = 2;
        gbcEdit.gridwidth = 2;
        panelEdicion.add(panelBotones, gbcEdit);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weighty = 0.4;
        centro.add(panelEdicion, gbc);

        add(centro, BorderLayout.CENTER);

        // Cargar tabla al inicio
        cargarCampanasActivas();
    }

    private void cargarCentrosFiltro() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_centro, nombre FROM centros_salud WHERE activo = true ORDER BY nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id_centro");
                String nombre = rs.getString("nombre");
                mapaCentros.put(nombre, id);
                cbFiltroCentro.addItem(nombre);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar centros: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarCampanasActivas() {
        modeloTabla.setRowCount(0);
        String nombreFiltro = txtBuscar.getText().trim();
        String centroSeleccionado = (String) cbFiltroCentro.getSelectedItem();
        Integer idCentroFiltro = null;
        if (centroSeleccionado != null && !centroSeleccionado.equals("Todos")) {
            idCentroFiltro = mapaCentros.get(centroSeleccionado);
        }

        String sql = "SELECT c.id_campana, c.nombre, c.tipo, c.fecha_inicio, c.fecha_fin, cs.nombre AS centro " +
                     "FROM campana_vacunacion c " +
                     "JOIN centros_salud cs ON c.id_centro = cs.id_centro " +
                     "WHERE c.activo = true ";
        if (!nombreFiltro.isEmpty()) {
            sql += "AND c.nombre LIKE ? ";
        }
        if (idCentroFiltro != null) {
            sql += "AND c.id_centro = ? ";
        }
        sql += "ORDER BY c.fecha_inicio DESC";

        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (!nombreFiltro.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + nombreFiltro + "%");
            }
            if (idCentroFiltro != null) {
                pstmt.setInt(paramIndex, idCentroFiltro);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_campana"),
                    rs.getString("nombre"),
                    rs.getString("tipo"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getString("centro")
                };
                modeloTabla.addRow(fila);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar campañas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosSeleccionados() {
        int fila = tablaCampanas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una campaña de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentIdCampana = (Integer) modeloTabla.getValueAt(fila, 0);
        String tipoActual = (String) modeloTabla.getValueAt(fila, 2);
        Date fechaFinActual = (Date) modeloTabla.getValueAt(fila, 4);

        // Cargar en el formulario
        cbTipo.setSelectedItem(tipoActual);
        spFechaFin.setValue(fechaFinActual);

        JOptionPane.showMessageDialog(this, "Datos cargados. Puede modificar Tipo y Fecha de fin.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void guardarCambios() {
        if (currentIdCampana == null) {
            JOptionPane.showMessageDialog(this, "Primero seleccione y cargue una campaña", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nuevoTipo = (String) cbTipo.getSelectedItem();
        java.util.Date nuevaFechaFin = (java.util.Date) spFechaFin.getValue();

        // Validación adicional: la fecha fin no puede ser anterior a fecha inicio de la campaña
        // Obtenemos la fecha inicio actual desde la tabla o desde BD
        Date fechaInicioActual = null;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if ((Integer) modeloTabla.getValueAt(i, 0) == currentIdCampana) {
                fechaInicioActual = (Date) modeloTabla.getValueAt(i, 3);
                break;
            }
        }
        if (fechaInicioActual != null && nuevaFechaFin.before(fechaInicioActual)) {
            JOptionPane.showMessageDialog(this, "La fecha de fin no puede ser anterior a la fecha de inicio (" + fechaInicioActual + ")",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "UPDATE campana_vacunacion SET tipo = ?, fecha_fin = ? WHERE id_campana = ? AND activo = true";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nuevoTipo);
            pstmt.setDate(2, new java.sql.Date(nuevaFechaFin.getTime()));
            pstmt.setInt(3, currentIdCampana);
            int afectadas = pstmt.executeUpdate();
            pstmt.close();

            if (afectadas > 0) {
                JOptionPane.showMessageDialog(this, "Campaña modificada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Refrescar la tabla
                cargarCampanasActivas();
                // Limpiar selección
                currentIdCampana = null;
                cbTipo.setSelectedIndex(0);
                spFechaFin.setValue(new java.util.Date());
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo modificar (¿la campaña ya no está activa?)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}