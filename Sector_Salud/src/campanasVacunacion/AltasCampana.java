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

public class AltasCampana extends JPanel {

    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);

    private JTextField txtNombre, txtDescripcion;
    private JComboBox<String> cbTipo, cbCentro;
    private JSpinner spFechaInicio, spFechaFin;
    private JTable tablaLotes;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregarLote, btnQuitarLote, btnGuardar, btnCancelar;

    // Datos auxiliares
    private Map<String, Integer> mapaCentros;
    private Map<String, InventarioItem> mapaLotesInventario;
    private java.util.List<DetalleCampana> listaDetalle;

    private CardLayout cardLayout;
    private JPanel contenedor;

    private class InventarioItem {
        int idInventario;
        String lote;
        String folio;
        int disponible;
        InventarioItem(int id, String lote, String folio, int disp) {
            this.idInventario = id;
            this.lote = lote;
            this.folio = folio;
            this.disponible = disp;
        }
    }

    private class DetalleCampana {
        int idInventario;
        String lote;
        String folio;
        int cantidad;
        DetalleCampana(int idInventario, String lote, String folio, int cantidad) {
            this.idInventario = idInventario;
            this.lote = lote;
            this.folio = folio;
            this.cantidad = cantidad;
        }
    }

    public AltasCampana(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.mapaCentros = new HashMap<>();
        this.mapaLotesInventario = new HashMap<>();
        this.listaDetalle = new java.util.ArrayList<>();

        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Registro de Campaña de Vacunación");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(FONDO);

        // Formulario de la campaña
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("Nombre de la campaña:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(30);
        formulario.add(txtNombre, gbc);

        // Tipo
        gbc.gridx = 0; gbc.gridy = 1;
        formulario.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        cbTipo = new JComboBox<>(new String[]{"Selecciona un tipo", "Permanente", "Temporal"});
        formulario.add(cbTipo, gbc);

        // Centro de salud
        gbc.gridx = 0; gbc.gridy = 2;
        formulario.add(new JLabel("Centro de salud:"), gbc);
        gbc.gridx = 1;
        cbCentro = new JComboBox<>();
        cbCentro.addActionListener(e -> cargarInventarioCentro());
        formulario.add(cbCentro, gbc);

        // Fechas
        gbc.gridx = 0; gbc.gridy = 3;
        formulario.add(new JLabel("Fecha inicio:"), gbc);
        gbc.gridx = 1;
        spFechaInicio = new JSpinner(new SpinnerDateModel());
        spFechaInicio.setEditor(new JSpinner.DateEditor(spFechaInicio, "dd/MM/yyyy"));
        formulario.add(spFechaInicio, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formulario.add(new JLabel("Fecha fin:"), gbc);
        gbc.gridx = 1;
        spFechaFin = new JSpinner(new SpinnerDateModel());
        spFechaFin.setEditor(new JSpinner.DateEditor(spFechaFin, "dd/MM/yyyy"));
        formulario.add(spFechaFin, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 5;
        formulario.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        txtDescripcion = new JTextField(40);
        formulario.add(txtDescripcion, gbc);

        centro.add(formulario, BorderLayout.NORTH);

        // Panel de lotes asignados
        JPanel panelLotes = new JPanel(new BorderLayout());
        panelLotes.setBorder(BorderFactory.createTitledBorder("Lotes a utilizar en la campaña"));
        modeloTabla = new DefaultTableModel(new String[]{"Lote", "Folio", "Cantidad disponible", "Cantidad a usar"}, 0);
        tablaLotes = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaLotes);
        panelLotes.add(scroll, BorderLayout.CENTER);

        JPanel botonesLotes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAgregarLote = new JButton("Agregar lote del inventario");
        btnAgregarLote.addActionListener(e -> agregarLoteDialog());
        btnQuitarLote = new JButton("Quitar lote seleccionado");
        btnQuitarLote.addActionListener(e -> quitarLote());
        botonesLotes.add(btnAgregarLote);
        botonesLotes.add(btnQuitarLote);
        panelLotes.add(botonesLotes, BorderLayout.NORTH);

        centro.add(panelLotes, BorderLayout.CENTER);

        // Botones principales
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnGuardar = new JButton("Guardar Campaña");
        btnGuardar.setBackground(GUINDO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarCampana());
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> limpiarFormulario());
        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo1"));

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnRegresar);
        centro.add(panelBotones, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);

        // Cargar centros
        cargarCentros();
    }

    private void cargarCentros() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_centro, nombre FROM centros_salud WHERE activo = true ORDER BY nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            cbCentro.removeAllItems();
            mapaCentros.clear();
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
        }
    }

    private void cargarInventarioCentro() {
        String centro = (String) cbCentro.getSelectedItem();
        if (centro == null) return;
        int idCentro = mapaCentros.get(centro);
        mapaLotesInventario.clear();
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT a.id_inventario, l.lote_vacunacion, a.folio, a.cantidad_asignar " +
                         "FROM asignacion_vacunas_centro a " +
                         "JOIN lotes l ON a.id_lote = l.id_lote " +
                         "WHERE a.id_centro = ? AND a.activo = true AND a.cantidad_asignar > 0";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idCentro);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_inventario");
                String lote = rs.getString("lote_vacunacion");
                String folio = rs.getString("folio");
                int disp = rs.getInt("cantidad_asignar");
                mapaLotesInventario.put(lote + " - " + folio, new InventarioItem(id, lote, folio, disp));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void agregarLoteDialog() {
        if (mapaLotesInventario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay lotes disponibles en el inventario de este centro", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String[] opciones = mapaLotesInventario.keySet().toArray(new String[0]);
        String seleccion = (String) JOptionPane.showInputDialog(this, "Seleccione un lote:", "Agregar lote",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (seleccion == null) return;
        InventarioItem item = mapaLotesInventario.get(seleccion);
        if (item == null) return;

        String cantidadStr = JOptionPane.showInputDialog(this, "Cantidad a usar (máx " + item.disponible + "):");
        if (cantidadStr == null) return;
        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0 || cantidad > item.disponible) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad no numérica", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar si ya se agregó ese lote en el detalle actual (sumar cantidades)
        for (DetalleCampana det : listaDetalle) {
            if (det.idInventario == item.idInventario) {
                int nuevaCant = det.cantidad + cantidad;
                if (nuevaCant > item.disponible) {
                    JOptionPane.showMessageDialog(this, "Suma excede la cantidad disponible del lote", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                det.cantidad = nuevaCant;
                actualizarTabla();
                return;
            }
        }
        listaDetalle.add(new DetalleCampana(item.idInventario, item.lote, item.folio, cantidad));
        actualizarTabla();
    }

    private void quitarLote() {
        int fila = tablaLotes.getSelectedRow();
        if (fila == -1) return;
        listaDetalle.remove(fila);
        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        for (DetalleCampana det : listaDetalle) {
            InventarioItem item = null;
            for (InventarioItem i : mapaLotesInventario.values()) {
                if (i.idInventario == det.idInventario) {
                    item = i;
                    break;
                }
            }
            if (item != null) {
                modeloTabla.addRow(new Object[]{item.lote, item.folio, item.disponible, det.cantidad});
            }
        }
    }

    private void guardarCampana() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese nombre de la campaña", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (cbCentro.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un centro", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (listaDetalle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un lote a la campaña", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.util.Date inicio = (java.util.Date) spFechaInicio.getValue();
        java.util.Date fin = (java.util.Date) spFechaFin.getValue();
        if (inicio.after(fin)) {
            JOptionPane.showMessageDialog(this, "La fecha inicio no puede ser mayor a fecha fin", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = Conexion_DB.obtenerConexion();
            conn.setAutoCommit(false);

            // Insertar campaña
            String sqlCampana = "INSERT INTO campana_vacunacion (nombre, tipo, descripcion, fecha_inicio, fecha_fin, id_centro) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmtCamp = conn.prepareStatement(sqlCampana, Statement.RETURN_GENERATED_KEYS);
            pstmtCamp.setString(1, txtNombre.getText().trim());
            pstmtCamp.setString(2, (String) cbTipo.getSelectedItem());
            pstmtCamp.setString(3, txtDescripcion.getText().trim());
            pstmtCamp.setDate(4, new java.sql.Date(inicio.getTime()));
            pstmtCamp.setDate(5, new java.sql.Date(fin.getTime()));
            pstmtCamp.setInt(6, mapaCentros.get((String) cbCentro.getSelectedItem()));
            pstmtCamp.executeUpdate();
            ResultSet rs = pstmtCamp.getGeneratedKeys();
            rs.next();
            int idCampana = rs.getInt(1);
            rs.close();
            pstmtCamp.close();

            // Insertar detalles
            String sqlDetalle = "INSERT INTO campana_detalle_lote (id_campana, id_inventario, cantidad_planificada) VALUES (?, ?, ?)";
            PreparedStatement pstmtDet = conn.prepareStatement(sqlDetalle);
            for (DetalleCampana det : listaDetalle) {
                pstmtDet.setInt(1, idCampana);
                pstmtDet.setInt(2, det.idInventario);
                pstmtDet.setInt(3, det.cantidad);
                pstmtDet.addBatch();
            }
            pstmtDet.executeBatch();
            pstmtDet.close();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Campaña registrada exitosamente con ID " + idCampana, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cardLayout.show(contenedor, "modulo6");
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        cbTipo.setSelectedIndex(0);
        cbCentro.setSelectedIndex(0);
        spFechaInicio.setValue(new java.util.Date());
        spFechaFin.setValue(new java.util.Date());
        txtDescripcion.setText("");
        listaDetalle.clear();
        actualizarTabla();
        mapaLotesInventario.clear();
    }
}