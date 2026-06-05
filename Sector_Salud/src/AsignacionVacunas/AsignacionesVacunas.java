/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AsignacionVacunas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import sector_salud.Conexion_DB;

/**
 *
 * @author marga
 */
public class AsignacionesVacunas extends JPanel {
    
    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    
    private JComboBox<String> cbCentroSalud;
    private JComboBox<String> cbLoteVacuna;
    private JTextField txtCantidadAsignar, txtFolioActual, txtFolioNuevo, txtObservaciones;
    private JLabel lblCantidadDisponible, lblLoteInfo;
    private JButton btnAsignar, btnLimpiar;
    
    private CardLayout cardLayout;
    private JPanel contenedor;
    
    // Mapas para almacenar IDs
    private Map<String, Integer> mapaCentros;
    private Map<String, LoteInfo> mapaLotes;
    
    // Clase interna para almacenar información del lote
    private class LoteInfo {
        int idLote;
        String loteVacunacion;
        String folioActual;
        int cantidadDisponible;
        
        LoteInfo(int idLote, String loteVacunacion, String folioActual, int cantidadDisponible) {
            this.idLote = idLote;
            this.loteVacunacion = loteVacunacion;
            this.folioActual = folioActual;
            this.cantidadDisponible = cantidadDisponible;
        }
        
        // Obtener el inicio del rango del folio (ej. "1-1000" -> 1)
        int getInicioFolio() {
            if (folioActual == null || !folioActual.contains("-")) return 1;
            try {
                return Integer.parseInt(folioActual.split("-")[0]);
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        
        // Obtener el fin del rango del folio (ej. "1-1000" -> 1000)
        int getFinFolio() {
            if (folioActual == null || !folioActual.contains("-")) return cantidadDisponible;
            try {
                return Integer.parseInt(folioActual.split("-")[1]);
            } catch (NumberFormatException e) {
                return cantidadDisponible;
            }
        }
        
        // Generar nuevo folio después de asignar
        String generarNuevoFolio(int cantidadAsignar) {
            int inicioActual = getInicioFolio();
            int nuevoInicio = inicioActual + cantidadAsignar;
            int finActual = getFinFolio();
            return nuevoInicio + "-" + finActual;
        }
        
        // Obtener el rango de folios asignados (ej. "1-20")
        String getFoliosAsignados(int cantidadAsignar) {
            int inicioActual = getInicioFolio();
            int finAsignado = inicioActual + cantidadAsignar - 1;
            return inicioActual + "-" + finAsignado;
        }
    }
    
    public AsignacionesVacunas(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.mapaCentros = new HashMap<>();
        this.mapaLotes = new HashMap<>();
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Asignación de Vacunas a Centros de Salud");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);
        
        // Panel central con formulario
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Centro de Salud
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblCentro = new JLabel("Centro de Salud:");
        lblCentro.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblCentro, gbc);
        gbc.gridx = 1;
        cbCentroSalud = new JComboBox<>();
        cbCentroSalud.setFont(new Font("Arial", Font.PLAIN, 14));
        cbCentroSalud.setPreferredSize(new Dimension(300, 30));
        formulario.add(cbCentroSalud, gbc);
        
        // Lote de Vacuna
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblLote = new JLabel("Lote de Vacuna:");
        lblLote.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblLote, gbc);
        gbc.gridx = 1;
        cbLoteVacuna = new JComboBox<>();
        cbLoteVacuna.setFont(new Font("Arial", Font.PLAIN, 14));
        cbLoteVacuna.setPreferredSize(new Dimension(300, 30));
        cbLoteVacuna.addActionListener(e -> cargarInfoLote());
        formulario.add(cbLoteVacuna, gbc);
        
        // Información del Lote
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblInfo = new JLabel("Información del Lote:");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblInfo, gbc);
        gbc.gridx = 1;
        lblLoteInfo = new JLabel("Seleccione un lote");
        lblLoteInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblLoteInfo.setForeground(GUINDO);
        formulario.add(lblLoteInfo, gbc);
        
        // Cantidad Disponible
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblDisponible = new JLabel("Cantidad Disponible:");
        lblDisponible.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblDisponible, gbc);
        gbc.gridx = 1;
        lblCantidadDisponible = new JLabel("0");
        lblCantidadDisponible.setFont(new Font("Arial", Font.BOLD, 16));
        lblCantidadDisponible.setForeground(GUINDO);
        formulario.add(lblCantidadDisponible, gbc);
        
        // Folio Actual
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblFolioActual = new JLabel("Folio Actual:");
        lblFolioActual.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblFolioActual, gbc);
        gbc.gridx = 1;
        txtFolioActual = new JTextField(30);
        txtFolioActual.setEditable(false);
        txtFolioActual.setBackground(new Color(240, 240, 240));
        formulario.add(txtFolioActual, gbc);
        
        // Cantidad a Asignar
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblCantidad = new JLabel("Cantidad a Asignar:");
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblCantidad, gbc);
        gbc.gridx = 1;
        txtCantidadAsignar = new JTextField(15);
        txtCantidadAsignar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPreviaFolio(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPreviaFolio(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPreviaFolio(); }
        });
        formulario.add(txtCantidadAsignar, gbc);
        
        // Folio Nuevo (vista previa)
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblFolioNuevo = new JLabel("Nuevo Folio (vista previa):");
        lblFolioNuevo.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblFolioNuevo, gbc);
        gbc.gridx = 1;
        txtFolioNuevo = new JTextField(30);
        txtFolioNuevo.setEditable(false);
        txtFolioNuevo.setBackground(new Color(240, 240, 240));
        formulario.add(txtFolioNuevo, gbc);
        
        // Observaciones
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel lblObservaciones = new JLabel("Observaciones:");
        lblObservaciones.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblObservaciones, gbc);
        gbc.gridx = 1;
        txtObservaciones = new JTextField(40);
        formulario.add(txtObservaciones, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBotones.setBackground(FONDO);
        
        btnAsignar = new JButton("Asignar Vacunas");
        btnAsignar.setBackground(GUINDO);
        btnAsignar.setForeground(Color.WHITE);
        btnAsignar.setFocusPainted(false);
        btnAsignar.setBorderPainted(false);
        btnAsignar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAsignar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAsignar.setPreferredSize(new Dimension(180, 40));
        btnAsignar.addActionListener(e -> asignarVacunas());
        
        btnAsignar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                btnAsignar.setBackground(GUINDO_HOVER); 
            }
            public void mouseExited(java.awt.event.MouseEvent e) { 
                btnAsignar.setBackground(GUINDO); 
            }
        });
        
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(Color.GRAY);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setFont(new Font("Arial", Font.BOLD, 14));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.setPreferredSize(new Dimension(120, 40));
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.setBackground(GUINDO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.setPreferredSize(new Dimension(120, 40));
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo6"));
        
        panelBotones.add(btnAsignar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRegresar);
        
        // Agregar componentes al panel principal
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FONDO);
        centro.add(formulario, BorderLayout.CENTER);
        centro.add(panelBotones, BorderLayout.SOUTH);
        
        add(centro, BorderLayout.CENTER);
        
        // Cargar datos
        cargarCentrosSalud();
        cargarLotesVacuna();
    }
    
    private void cargarCentrosSalud() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_centro, nombre FROM centros_salud WHERE activo = true ORDER BY nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            cbCentroSalud.removeAllItems();
            mapaCentros.clear();
            cbCentroSalud.addItem("-- Seleccione un centro de salud --");
            
            while (rs.next()) {
                int id = rs.getInt("id_centro");
                String nombre = rs.getString("nombre");
                mapaCentros.put(nombre, id);
                cbCentroSalud.addItem(nombre);
            }
            rs.close();
            stmt.close();
            
            if (cbCentroSalud.getItemCount() == 0) {
                cbCentroSalud.addItem("No hay centros activos");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar centros: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarLotesVacuna() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            // Solo lotes con cantidad > 0
            String sql = "SELECT id_lote, lote_vacunacion, folio, cantidad FROM lotes WHERE cantidad > 0 ORDER BY lote_vacunacion";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            cbLoteVacuna.removeAllItems();
            mapaLotes.clear();
            cbLoteVacuna.addItem("-- Seleccione un lote --");
            
            while (rs.next()) {
                int idLote = rs.getInt("id_lote");
                String loteVacunacion = rs.getString("lote_vacunacion");
                String folio = rs.getString("folio");
                int cantidad = rs.getInt("cantidad");
                
                LoteInfo info = new LoteInfo(idLote, loteVacunacion, folio, cantidad);
                mapaLotes.put(loteVacunacion, info);
                cbLoteVacuna.addItem(loteVacunacion);
            }
            rs.close();
            stmt.close();
            
            if (cbLoteVacuna.getItemCount() == 0) {
                cbLoteVacuna.addItem("No hay lotes disponibles");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar lotes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarInfoLote() {
        String loteSeleccionado = (String) cbLoteVacuna.getSelectedItem();
        if (loteSeleccionado != null && mapaLotes.containsKey(loteSeleccionado)) {
            LoteInfo info = mapaLotes.get(loteSeleccionado);
            lblCantidadDisponible.setText(String.valueOf(info.cantidadDisponible));
            txtFolioActual.setText(info.folioActual);
            lblLoteInfo.setText("Lote: " + info.loteVacunacion);
            actualizarVistaPreviaFolio();
        } else {
            lblCantidadDisponible.setText("0");
            txtFolioActual.setText("");
            lblLoteInfo.setText("Seleccione un lote");
        }
    }
    
    private void actualizarVistaPreviaFolio() {
        String loteSeleccionado = (String) cbLoteVacuna.getSelectedItem();
        if (loteSeleccionado == null || !mapaLotes.containsKey(loteSeleccionado)) {
            txtFolioNuevo.setText("");
            return;
        }
        
        LoteInfo info = mapaLotes.get(loteSeleccionado);
        String cantidadTexto = txtCantidadAsignar.getText().trim();
        
        if (cantidadTexto.isEmpty()) {
            txtFolioNuevo.setText(info.folioActual);
            return;
        }
        
        try {
            int cantidadAsignar = Integer.parseInt(cantidadTexto);
            if (cantidadAsignar > 0 && cantidadAsignar <= info.cantidadDisponible) {
                String nuevoFolio = info.generarNuevoFolio(cantidadAsignar);
                txtFolioNuevo.setText(nuevoFolio);
                txtFolioNuevo.setForeground(new Color(0, 150, 0));
            } else if (cantidadAsignar > info.cantidadDisponible) {
                txtFolioNuevo.setText("Cantidad excede el stock disponible");
                txtFolioNuevo.setForeground(Color.RED);
            } else {
                txtFolioNuevo.setText(info.folioActual);
                txtFolioNuevo.setForeground(Color.BLACK);
            }
        } catch (NumberFormatException e) {
            txtFolioNuevo.setText("Cantidad inválida");
            txtFolioNuevo.setForeground(Color.RED);
        }
    }
    
    private void asignarVacunas() {
        try {
            // Validaciones
            if (cbCentroSalud.getSelectedIndex() == -1 || cbCentroSalud.getItemCount() == 0 ||
                cbCentroSalud.getSelectedItem().equals("No hay centros activos")) {
                JOptionPane.showMessageDialog(this, "Seleccione un centro de salud válido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (cbLoteVacuna.getSelectedIndex() == -1 || cbLoteVacuna.getItemCount() == 0 ||
                cbLoteVacuna.getSelectedItem().equals("No hay lotes disponibles")) {
                JOptionPane.showMessageDialog(this, "Seleccione un lote de vacuna válido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String loteSeleccionado = (String) cbLoteVacuna.getSelectedItem();
            LoteInfo info = mapaLotes.get(loteSeleccionado);
            
            if (info == null) {
                JOptionPane.showMessageDialog(this, "Error al obtener información del lote", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String cantidadTexto = txtCantidadAsignar.getText().trim();
            if (cantidadTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese la cantidad a asignar", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int cantidadAsignar = Integer.parseInt(cantidadTexto);
            
            if (cantidadAsignar <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (cantidadAsignar > info.cantidadDisponible) {
                JOptionPane.showMessageDialog(this, "Cantidad insuficiente. Disponible: " + info.cantidadDisponible, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Confirmar asignación?\n\n" +
                "Centro: " + cbCentroSalud.getSelectedItem() + "\n" +
                "Lote: " + info.loteVacunacion + "\n" +
                "Cantidad a asignar: " + cantidadAsignar + "\n" +
                "Folios asignados: " + info.getFoliosAsignados(cantidadAsignar) + "\n" +
                "Nuevo folio del lote: " + info.generarNuevoFolio(cantidadAsignar) + "\n" +
                "Stock restante: " + (info.cantidadDisponible - cantidadAsignar),
                "Confirmar asignación",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                String nombreCentro = (String) cbCentroSalud.getSelectedItem();
                int idCentro = mapaCentros.get(nombreCentro);
                String foliosAsignados = info.getFoliosAsignados(cantidadAsignar);
                String nuevoFolio = info.generarNuevoFolio(cantidadAsignar);
                int nuevaCantidad = info.cantidadDisponible - cantidadAsignar;
                String observaciones = txtObservaciones.getText().trim();
                
                // Realizar la transacción
                realizarAsignacion(info.idLote, idCentro, cantidadAsignar, foliosAsignados, 
                                  nuevoFolio, nuevaCantidad, observaciones);
                
                JOptionPane.showMessageDialog(this,
                    "Asignación realizada exitosamente!\n\n" +
                    "Centro: " + nombreCentro + "\n" +
                    "Lote: " + info.loteVacunacion + "\n" +
                    "Cantidad asignada: " + cantidadAsignar + "\n" +
                    "Folios asignados: " + foliosAsignados,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                limpiarFormulario();
                cargarLotesVacuna();// Recargar lotes actualizados
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al asignar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void realizarAsignacion(int idLote, int idCentro, int cantidadAsignar, 
                                     String foliosAsignados, String nuevoFolio, 
                                     int nuevaCantidad, String observaciones) throws SQLException {
        
        Connection conn = null;
        PreparedStatement pstmtAsignacion = null;
        PreparedStatement pstmtUpdateLote = null;
        
        try {
            conn = Conexion_DB.obtenerConexion();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // 1. Insertar en asignacion_vacunas_centro
            String sqlAsignacion = "INSERT INTO asignacion_vacunas_centro " +
                                   "(id_centro, id_lote, folio, cantidad_asignar, observaciones) " +
                                   "VALUES (?, ?, ?, ?, ?)";
            pstmtAsignacion = conn.prepareStatement(sqlAsignacion);
            pstmtAsignacion.setInt(1, idCentro);
            pstmtAsignacion.setInt(2, idLote);
            pstmtAsignacion.setString(3, foliosAsignados);
            pstmtAsignacion.setInt(4, cantidadAsignar);
            pstmtAsignacion.setString(5, observaciones);
            pstmtAsignacion.executeUpdate();
            
            // 2. Actualizar la tabla lotes (cantidad y folio)
            String sqlUpdateLote = "UPDATE lotes SET cantidad = ?, folio = ? WHERE id_lote = ?";
            pstmtUpdateLote = conn.prepareStatement(sqlUpdateLote);
            pstmtUpdateLote.setInt(1, nuevaCantidad);
            pstmtUpdateLote.setString(2, nuevoFolio);
            pstmtUpdateLote.setInt(3, idLote);
            pstmtUpdateLote.executeUpdate();
            
            conn.commit(); // Confirmar transacción
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir cambios en caso de error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (pstmtAsignacion != null) pstmtAsignacion.close();
            if (pstmtUpdateLote != null) pstmtUpdateLote.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    private void limpiarFormulario() {
        if (cbCentroSalud.getItemCount() > 0 && !cbCentroSalud.getSelectedItem().equals("No hay centros activos")) {
            cbCentroSalud.setSelectedIndex(0);
        }
        if (cbLoteVacuna.getItemCount() > 0 && !cbLoteVacuna.getSelectedItem().equals("No hay lotes disponibles")) {
            cbLoteVacuna.setSelectedIndex(0);
        }
        txtCantidadAsignar.setText("");
        txtObservaciones.setText("");
        cargarInfoLote();
    }
}