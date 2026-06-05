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

public class BajasCampana extends JPanel {

    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);

    private JTable tablaCampanas;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JComboBox<String> cbFiltroCentro;
    private JButton btnDarBaja, btnRefrescar, btnRegresar;

    private Map<String, Integer> mapaCentros; // nombre centro -> id_centro
    private CardLayout cardLayout;
    private JPanel contenedor;

    public BajasCampana(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.mapaCentros = new HashMap<>();

        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Baja de Campañas de Vacunación");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);

        // Panel central con filtros y tabla
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(FONDO);

        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelFiltros.setBackground(FONDO);
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de búsqueda"));

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

        centro.add(panelFiltros, BorderLayout.NORTH);

        // Tabla de campañas
        String[] columnas = {"ID", "Nombre", "Tipo", "Fecha Inicio", "Fecha Fin", "Centro", "Descripción"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // no editable
            }
        };
        tablaCampanas = new JTable(modeloTabla);
        tablaCampanas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaCampanas);
        scroll.setBorder(BorderFactory.createTitledBorder("Campañas activas"));
        centro.add(scroll, BorderLayout.CENTER);

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnDarBaja = new JButton("Dar de baja campaña seleccionada");
        btnDarBaja.setBackground(GUINDO);
        btnDarBaja.setForeground(Color.WHITE);
        btnDarBaja.addActionListener(e -> darBajaCampana());
        btnRegresar = new JButton("← Regresar");
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo1"));

        panelBotones.add(btnDarBaja);
        panelBotones.add(btnRegresar);
        centro.add(panelBotones, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);

        // Cargar datos iniciales
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

        String sql = "SELECT c.id_campana, c.nombre, c.tipo, c.fecha_inicio, c.fecha_fin, cs.nombre AS centro, c.descripcion " +
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
                    rs.getString("centro"),
                    rs.getString("descripcion")
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

    private void darBajaCampana() {
        int filaSeleccionada = tablaCampanas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una campaña para dar de baja", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCampana = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombreCampana = (String) modeloTabla.getValueAt(filaSeleccionada, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de desactivar la campaña \"" + nombreCampana + "\"?\n"
                + "Esta acción no elimina los registros asociados, solo la oculta.",
                "Confirmar baja", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "UPDATE campana_vacunacion SET activo = false WHERE id_campana = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idCampana);
            int affected = pstmt.executeUpdate();
            pstmt.close();

            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Campaña desactivada correctamente", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarCampanasActivas(); // refrescar tabla
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo desactivar la campaña", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al desactivar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}