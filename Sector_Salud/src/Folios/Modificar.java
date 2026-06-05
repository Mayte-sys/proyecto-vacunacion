/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Folios;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.List;
import sector_salud.Conexion_DB;

public class Modificar extends JPanel {
    
    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    
    // Campos de búsqueda
    private JComboBox<String> cbBuscarPor;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    
    // Campos del formulario (serán habilitados después de buscar)
    private JTextField txtLoteVacunacion, txtEmpresa, txtNombreComercial, txtCantidad;
    private JComboBox<String> cbPais;
    private JSpinner spFechaCaducidad;
    private JLabel lblIdLote, lblFolio;
    
    // Componentes para enfermedades
    private JList<String> listaEnfermedades;
    private DefaultListModel<String> modeloEnfermedades;
    private JTextArea txtEnfermedadesSeleccionadas;
    
    private CardLayout cardLayout;
    private JPanel contenedor;
    private int loteActualId = -1;
    
    public Modificar(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Modificar Lote");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);
        
        // Panel central
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(FONDO);
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBusqueda.setBackground(FONDO);
        panelBusqueda.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblBuscarPor = new JLabel("Buscar lote:");
        lblBuscarPor.setFont(new Font("Arial", Font.BOLD, 14));
        
        cbBuscarPor = new JComboBox<>(new String[]{"ID de Lote", "Nombre de Lote"});
        cbBuscarPor.setFont(new Font("Arial", Font.PLAIN, 14));
        
        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(GUINDO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.addActionListener(e -> buscarLote());
        
        panelBusqueda.add(lblBuscarPor);
        panelBusqueda.add(cbBuscarPor);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        
        centro.add(panelBusqueda, BorderLayout.NORTH);
        
        // Panel del formulario
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(FONDO);
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del Lote"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // ID Lote (solo lectura)
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblId = new JLabel("ID Lote:");
        lblId.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblId, gbc);
        gbc.gridx = 1;
        lblIdLote = new JLabel("---");
        lblIdLote.setFont(new Font("Arial", Font.PLAIN, 16));
        formulario.add(lblIdLote, gbc);
        
        // Folio (solo lectura)
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblFolioLabel = new JLabel("Folio:");
        lblFolioLabel.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblFolioLabel, gbc);
        gbc.gridx = 1;
        lblFolio = new JLabel("---");
        lblFolio.setFont(new Font("Arial", Font.PLAIN, 16));
        formulario.add(lblFolio, gbc);
        
        // Lote de Vacunación
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblLote = new JLabel("Lote de Vacunación:");
        lblLote.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblLote, gbc);
        gbc.gridx = 1;
        txtLoteVacunacion = new JTextField(35);
        txtLoteVacunacion.setEnabled(false);
        formulario.add(txtLoteVacunacion, gbc);
        
        // País
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblPais = new JLabel("País:");
        lblPais.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblPais, gbc);
        gbc.gridx = 1;
        cbPais = new JComboBox<>();
        cbPais.setFont(new Font("Arial", Font.PLAIN, 16));
        cbPais.setEnabled(false);
        cargarPaises();
        formulario.add(cbPais, gbc);
        
        // Empresa
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblEmpresa = new JLabel("Empresa:");
        lblEmpresa.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblEmpresa, gbc);
        gbc.gridx = 1;
        txtEmpresa = new JTextField(35);
        txtEmpresa.setEnabled(false);
        formulario.add(txtEmpresa, gbc);
        
        // Nombre Comercial
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblNombre = new JLabel("Nombre Comercial:");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblNombre, gbc);
        gbc.gridx = 1;
        txtNombreComercial = new JTextField(35);
        txtNombreComercial.setEnabled(false);
        formulario.add(txtNombreComercial, gbc);
        
        // Fecha de Caducidad
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblFecha = new JLabel("Fecha de Caducidad:");
        lblFecha.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblFecha, gbc);
        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spFechaCaducidad = new JSpinner(dateModel);
        spFechaCaducidad.setEditor(new JSpinner.DateEditor(spFechaCaducidad, "dd/MM/yyyy"));
        spFechaCaducidad.setPreferredSize(new Dimension(200, 30));
        spFechaCaducidad.setEnabled(false);
        formulario.add(spFechaCaducidad, gbc);
        
        // Cantidad
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblCantidad, gbc);
        gbc.gridx = 1;
        txtCantidad = new JTextField(35);
        txtCantidad.setEnabled(false);
        formulario.add(txtCantidad, gbc);
        
        // Enfermedades
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel lblEnfermedades = new JLabel("Enfermedades:");
        lblEnfermedades.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblEnfermedades, gbc);
        gbc.gridx = 1;
        
        JPanel panelEnfermedades = new JPanel(new BorderLayout(10, 10));
        panelEnfermedades.setBackground(FONDO);
        
        modeloEnfermedades = new DefaultListModel<>();
        cargarEnfermedades();
        
        listaEnfermedades = new JList<>(modeloEnfermedades);
        listaEnfermedades.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaEnfermedades.setFont(new Font("Arial", Font.PLAIN, 14));
        listaEnfermedades.setEnabled(false);
        JScrollPane scrollLista = new JScrollPane(listaEnfermedades);
        scrollLista.setPreferredSize(new Dimension(250, 150));
        scrollLista.setBorder(BorderFactory.createTitledBorder("Enfermedades disponibles"));
        panelEnfermedades.add(scrollLista, BorderLayout.WEST);
        
        JPanel panelBotonesEnf = new JPanel(new GridLayout(3, 1, 5, 5));
        panelBotonesEnf.setBackground(FONDO);
        panelBotonesEnf.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        JButton btnAgregar = new JButton(" Agregar >");
        btnAgregar.setBackground(GUINDO);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setEnabled(false);
        btnAgregar.addActionListener(e -> agregarEnfermedadesSeleccionadas());
        
        JButton btnAgregarTodas = new JButton(" Agregar todas >>");
        btnAgregarTodas.setBackground(GUINDO);
        btnAgregarTodas.setForeground(Color.WHITE);
        btnAgregarTodas.setFocusPainted(false);
        btnAgregarTodas.setEnabled(false);
        btnAgregarTodas.addActionListener(e -> agregarTodasEnfermedades());
        
        JButton btnQuitar = new JButton(" < Quitar ");
        btnQuitar.setBackground(Color.GRAY);
        btnQuitar.setForeground(Color.WHITE);
        btnQuitar.setFocusPainted(false);
        btnQuitar.setEnabled(false);
        btnQuitar.addActionListener(e -> quitarEnfermedadSeleccionada());
        
        panelBotonesEnf.add(btnAgregar);
        panelBotonesEnf.add(btnAgregarTodas);
        panelBotonesEnf.add(btnQuitar);
        panelEnfermedades.add(panelBotonesEnf, BorderLayout.CENTER);
        
        txtEnfermedadesSeleccionadas = new JTextArea(6, 30);
        txtEnfermedadesSeleccionadas.setEditable(false);
        txtEnfermedadesSeleccionadas.setFont(new Font("Arial", Font.PLAIN, 14));
        txtEnfermedadesSeleccionadas.setLineWrap(true);
        txtEnfermedadesSeleccionadas.setWrapStyleWord(true);
        JScrollPane scrollSeleccion = new JScrollPane(txtEnfermedadesSeleccionadas);
        scrollSeleccion.setPreferredSize(new Dimension(300, 150));
        scrollSeleccion.setBorder(BorderFactory.createTitledBorder("Enfermedades seleccionadas"));
        panelEnfermedades.add(scrollSeleccion, BorderLayout.EAST);
        
        formulario.add(panelEnfermedades, gbc);
        
        centro.add(formulario, BorderLayout.CENTER);
        
        // Panel de botones principales
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBotones.setBackground(FONDO);
        
        JButton btnHabilitarEdicion = new JButton("Habilitar Edición");
        btnHabilitarEdicion.setBackground(new Color(255, 165, 0));
        btnHabilitarEdicion.setForeground(Color.WHITE);
        btnHabilitarEdicion.setFocusPainted(false);
        btnHabilitarEdicion.setFont(new Font("Arial", Font.BOLD, 14));
        btnHabilitarEdicion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHabilitarEdicion.setPreferredSize(new Dimension(150, 40));
        btnHabilitarEdicion.addActionListener(e -> habilitarEdicion(true));
        
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(GUINDO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(150, 40));
        btnGuardar.addActionListener(e -> guardarCambios());
        
        btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                btnGuardar.setBackground(GUINDO_HOVER); 
            }
            public void mouseExited(java.awt.event.MouseEvent e) { 
                btnGuardar.setBackground(GUINDO); 
            }
        });
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.GRAY);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(120, 40));
        btnCancelar.addActionListener(e -> {
            habilitarEdicion(false);
            cargarDatosLote(loteActualId);
        });
        
        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.setBackground(GUINDO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.setPreferredSize(new Dimension(120, 40));
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo2"));
        
        panelBotones.add(btnHabilitarEdicion);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnRegresar);
        
        centro.add(panelBotones, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
    }
    
    private void cargarPaises() {
        try {
            cbPais.addItem("Seleccione un país...");
            String sql = "SELECT id, nombre FROM paises ORDER BY nombre";
            try (Connection conn = Conexion_DB.obtenerConexion();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    cbPais.addItem(rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar países: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarEnfermedades() {
        try {
            String sql = "SELECT nombre FROM enfermedades ORDER BY nombre";
            try (Connection conn = Conexion_DB.obtenerConexion();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    modeloEnfermedades.addElement(rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar enfermedades: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarLote() {
        String criterio = txtBuscar.getText().trim();
        
        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un valor para buscar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql;
            PreparedStatement pstmt;
            
            if (cbBuscarPor.getSelectedIndex() == 0) {
                sql = "SELECT id_lote FROM lotes WHERE id_lote = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(criterio));
            } else {
                sql = "SELECT id_lote FROM lotes WHERE lote_vacunacion LIKE ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "%" + criterio + "%");
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                loteActualId = rs.getInt("id_lote");
                cargarDatosLote(loteActualId);
                JOptionPane.showMessageDialog(this, "Lote encontrado. Puede habilitar la edición.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún lote con ese criterio.", "Error", JOptionPane.ERROR_MESSAGE);
                loteActualId = -1;
            }
            
            rs.close();
            pstmt.close();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido para ID", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cargarDatosLote(int idLote) {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT l.id_lote, l.lote_vacunacion, l.folio, l.id_pais, " +
                         "l.empresa, l.nombre_comercial, l.fecha_caducidad, l.cantidad, l.enfermedades, " +
                         "p.nombre AS pais_nombre " +
                         "FROM lotes l " +
                         "JOIN paises p ON l.id_pais = p.id " +
                         "WHERE l.id_lote = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idLote);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                lblIdLote.setText(String.valueOf(rs.getInt("id_lote")));
                lblFolio.setText(rs.getString("folio"));
                txtLoteVacunacion.setText(rs.getString("lote_vacunacion"));
                txtEmpresa.setText(rs.getString("empresa"));
                txtNombreComercial.setText(rs.getString("nombre_comercial"));
                txtCantidad.setText(String.valueOf(rs.getInt("cantidad")));
                spFechaCaducidad.setValue(rs.getDate("fecha_caducidad"));
                txtEnfermedadesSeleccionadas.setText(rs.getString("enfermedades"));
                
                // Seleccionar país en combo
                String paisNombre = rs.getString("pais_nombre");
                for (int i = 0; i < cbPais.getItemCount(); i++) {
                    if (cbPais.getItemAt(i).equals(paisNombre)) {
                        cbPais.setSelectedIndex(i);
                        break;
                    }
                }
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void habilitarEdicion(boolean habilitar) {
        txtLoteVacunacion.setEnabled(habilitar);
        cbPais.setEnabled(habilitar);
        txtEmpresa.setEnabled(habilitar);
        txtNombreComercial.setEnabled(habilitar);
        spFechaCaducidad.setEnabled(habilitar);
        txtCantidad.setEnabled(habilitar);
        listaEnfermedades.setEnabled(habilitar);
        
        // Habilitar botones de enfermedades
        Component parent = listaEnfermedades.getParent();
        while (!(parent instanceof JPanel) && parent != null) {
            parent = parent.getParent();
        }
        if (parent instanceof JPanel) {
            for (Component comp : ((JPanel)parent).getComponents()) {
                if (comp instanceof JPanel) {
                    for (Component btn : ((JPanel)comp).getComponents()) {
                        if (btn instanceof JButton) {
                            btn.setEnabled(habilitar);
                        }
                    }
                }
            }
        }
    }
    
    private void agregarEnfermedadesSeleccionadas() {
        List<String> seleccionadas = listaEnfermedades.getSelectedValuesList();
        StringBuilder textoActual = new StringBuilder(txtEnfermedadesSeleccionadas.getText());
        
        for (String enfermedad : seleccionadas) {
            if (!txtEnfermedadesSeleccionadas.getText().contains(enfermedad)) {
                if (textoActual.length() > 0) {
                    textoActual.append(", ");
                }
                textoActual.append(enfermedad);
            }
        }
        txtEnfermedadesSeleccionadas.setText(textoActual.toString());
    }
    
    private void agregarTodasEnfermedades() {
        StringBuilder textoActual = new StringBuilder();
        for (int i = 0; i < modeloEnfermedades.getSize(); i++) {
            String enfermedad = modeloEnfermedades.getElementAt(i);
            if (textoActual.length() > 0) {
                textoActual.append(", ");
            }
            textoActual.append(enfermedad);
        }
        txtEnfermedadesSeleccionadas.setText(textoActual.toString());
    }
    
    private void quitarEnfermedadSeleccionada() {
        String textoSeleccionado = txtEnfermedadesSeleccionadas.getSelectedText();
        if (textoSeleccionado != null) {
            String textoActual = txtEnfermedadesSeleccionadas.getText();
            String nuevoTexto = textoActual.replace(textoSeleccionado, "");
            nuevoTexto = nuevoTexto.replace(",,", ",").replace(" ,", ",");
            if (nuevoTexto.startsWith(", ")) {
                nuevoTexto = nuevoTexto.substring(2);
            }
            if (nuevoTexto.endsWith(", ")) {
                nuevoTexto = nuevoTexto.substring(0, nuevoTexto.length() - 2);
            }
            txtEnfermedadesSeleccionadas.setText(nuevoTexto);
        }
    }
    
    private int obtenerIdPais(String nombrePais) throws SQLException {
        String sql = "SELECT id FROM paises WHERE nombre = ?";
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombrePais);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }
    
    private void guardarCambios() {
        if (loteActualId == -1) {
            JOptionPane.showMessageDialog(this, "No hay ningún lote cargado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar campos
        if (txtLoteVacunacion.getText().trim().isEmpty() ||
            cbPais.getSelectedIndex() == 0 ||
            txtEmpresa.getText().trim().isEmpty() ||
            txtNombreComercial.getText().trim().isEmpty() ||
            txtCantidad.getText().trim().isEmpty() ||
            txtEnfermedadesSeleccionadas.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Por favor complete todos los campos", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de guardar los cambios?\n" +
            "ID Lote: " + loteActualId, 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String loteVacunacion = txtLoteVacunacion.getText().trim();
                String nombrePais = cbPais.getSelectedItem().toString();
                int idPais = obtenerIdPais(nombrePais);
                String empresa = txtEmpresa.getText().trim();
                String nombreComercial = txtNombreComercial.getText().trim();
                java.util.Date fechaUtil = (java.util.Date) spFechaCaducidad.getValue();
                java.sql.Date fechaCaducidad = new java.sql.Date(fechaUtil.getTime());
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                String enfermedades = txtEnfermedadesSeleccionadas.getText().trim();
                
                actualizarEnBD(loteActualId, loteVacunacion, idPais, empresa, 
                              nombreComercial, fechaCaducidad, cantidad, enfermedades);
                
                JOptionPane.showMessageDialog(this, 
                    "Lote modificado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                habilitarEdicion(false);
                cargarDatosLote(loteActualId);
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void actualizarEnBD(int idLote, String loteVacunacion, int idPais, 
                                 String empresa, String nombreComercial, 
                                 java.sql.Date fechaCaducidad, int cantidad, 
                                 String enfermedades) throws SQLException {
        String sql = "UPDATE lotes SET lote_vacunacion = ?, id_pais = ?, empresa = ?, " +
                    "nombre_comercial = ?, fecha_caducidad = ?, cantidad = ?, enfermedades = ? " +
                    "WHERE id_lote = ?";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loteVacunacion);
            pstmt.setInt(2, idPais);
            pstmt.setString(3, empresa);
            pstmt.setString(4, nombreComercial);
            pstmt.setDate(5, fechaCaducidad);
            pstmt.setInt(6, cantidad);
            pstmt.setString(7, enfermedades);
            pstmt.setInt(8, idLote);
            
            pstmt.executeUpdate();
        }
    }
}