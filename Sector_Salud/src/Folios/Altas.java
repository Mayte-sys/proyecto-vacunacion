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

public class Altas extends JPanel {
    
    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    
    private JTextField txtLoteVacunacion, txtEmpresa;
    private JComboBox<String> cbPais;
    private JTextField txtNombreComercial, txtCantidad;
    private JSpinner spFechaCaducidad;
    private JLabel lblFolioGenerado;
    
    // Componentes para enfermedades con selección múltiple
    private JList<String> listaEnfermedades;
    private DefaultListModel<String> modeloEnfermedades;
    private JTextArea txtEnfermedadesSeleccionadas;
    
    private CardLayout cardLayout;
    private JPanel contenedor;
    
    public Altas(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Alta de Lotes");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);
        
        // Panel central con formulario
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Lote de Vacunación
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblLote = new JLabel("Lote de Vacunación:");
        lblLote.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblLote, gbc);
        gbc.gridx = 1;
        txtLoteVacunacion = new JTextField(35);
        formulario.add(txtLoteVacunacion, gbc);
        
        // Folio (mostrará el rango automático)
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblFolio = new JLabel("Folio generado:");
        lblFolio.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblFolio, gbc);
        gbc.gridx = 1;
        lblFolioGenerado = new JLabel("Se generará automáticamente al guardar");
        lblFolioGenerado.setFont(new Font("Arial", Font.ITALIC, 14));
        lblFolioGenerado.setForeground(Color.GRAY);
        formulario.add(lblFolioGenerado, gbc);
        
        // País
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblPais = new JLabel("País:");
        lblPais.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblPais, gbc);
        gbc.gridx = 1;
        
        // Cargar países desde la base de datos
        cbPais = new JComboBox<>();
        cbPais.setFont(new Font("Arial", Font.PLAIN, 16));
        cbPais.setPreferredSize(new Dimension(200, 30));
        cargarPaises();
        formulario.add(cbPais, gbc);
        
        // Empresa
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblEmpresa = new JLabel("Empresa:");
        lblEmpresa.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblEmpresa, gbc);
        gbc.gridx = 1;
        txtEmpresa = new JTextField(35);
        formulario.add(txtEmpresa, gbc);
        
        // Nombre Comercial
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblNombre = new JLabel("Nombre Comercial:");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblNombre, gbc);
        gbc.gridx = 1;
        txtNombreComercial = new JTextField(35);
        formulario.add(txtNombreComercial, gbc);
        
        // Fecha de Caducidad
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblFecha = new JLabel("Fecha de Caducidad:");
        lblFecha.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblFecha, gbc);
        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spFechaCaducidad = new JSpinner(dateModel);
        spFechaCaducidad.setEditor(new JSpinner.DateEditor(spFechaCaducidad, "dd/MM/yyyy"));
        spFechaCaducidad.setPreferredSize(new Dimension(200, 30));
        formulario.add(spFechaCaducidad, gbc);
        
        // Cantidad
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblCantidad, gbc);
        gbc.gridx = 1;
        txtCantidad = new JTextField(35);
        txtCantidad.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPreviaFolio(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPreviaFolio(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPreviaFolio(); }
        });
        formulario.add(txtCantidad, gbc);
        
        // Enfermedades - Selección múltiple
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel lblEnfermedades = new JLabel("Enfermedades:");
        lblEnfermedades.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblEnfermedades, gbc);
        gbc.gridx = 1;
        
        // Panel principal para enfermedades
        JPanel panelEnfermedades = new JPanel(new BorderLayout(10, 10));
        panelEnfermedades.setBackground(FONDO);
        
        // Cargar enfermedades desde la base de datos
        modeloEnfermedades = new DefaultListModel<>();
        cargarEnfermedades();
        
        listaEnfermedades = new JList<>(modeloEnfermedades);
        listaEnfermedades.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaEnfermedades.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollLista = new JScrollPane(listaEnfermedades);
        scrollLista.setPreferredSize(new Dimension(250, 150));
        scrollLista.setBorder(BorderFactory.createTitledBorder("Enfermedades disponibles"));
        panelEnfermedades.add(scrollLista, BorderLayout.WEST);
        
        // Panel de botones
        JPanel panelBotonesEnf = new JPanel(new GridLayout(3, 1, 5, 5));
        panelBotonesEnf.setBackground(FONDO);
        panelBotonesEnf.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        JButton btnAgregar = new JButton(" Agregar >");
        btnAgregar.setBackground(GUINDO);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgregar.addActionListener(e -> agregarEnfermedadesSeleccionadas());
        
        JButton btnAgregarTodas = new JButton(" Agregar todas >>");
        btnAgregarTodas.setBackground(GUINDO);
        btnAgregarTodas.setForeground(Color.WHITE);
        btnAgregarTodas.setFocusPainted(false);
        btnAgregarTodas.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgregarTodas.addActionListener(e -> agregarTodasEnfermedades());
        
        JButton btnQuitar = new JButton(" < Quitar ");
        btnQuitar.setBackground(Color.GRAY);
        btnQuitar.setForeground(Color.WHITE);
        btnQuitar.setFocusPainted(false);
        btnQuitar.setFont(new Font("Arial", Font.BOLD, 12));
        btnQuitar.addActionListener(e -> quitarEnfermedadSeleccionada());
        
        panelBotonesEnf.add(btnAgregar);
        panelBotonesEnf.add(btnAgregarTodas);
        panelBotonesEnf.add(btnQuitar);
        panelEnfermedades.add(panelBotonesEnf, BorderLayout.CENTER);
        
        // Área de enfermedades seleccionadas
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
        
        // Panel de botones principales
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBotones.setBackground(FONDO);
        
        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.setBackground(GUINDO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.setPreferredSize(new Dimension(120, 40));
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo2"));
        
        btnRegresar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                btnRegresar.setBackground(GUINDO_HOVER); 
            }
            public void mouseExited(java.awt.event.MouseEvent e) { 
                btnRegresar.setBackground(GUINDO); 
            }
        });
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(GUINDO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(120, 40));
        btnGuardar.addActionListener(e -> guardarLote());
        
        // Efecto hover para btnGuardar
        btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                btnGuardar.setBackground(GUINDO_HOVER); 
            }
            public void mouseExited(java.awt.event.MouseEvent e) { 
                btnGuardar.setBackground(GUINDO); 
            }
        });
        
        JButton btnCancelar = new JButton("Limpiar");
        btnCancelar.setBackground(Color.GRAY);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setPreferredSize(new Dimension(120, 40));
        btnCancelar.addActionListener(e -> limpiarFormulario());
        
        panelBotones.add(btnRegresar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
          
        // Agregar componentes al panel principal
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FONDO);
        centro.add(formulario, BorderLayout.CENTER);
        centro.add(panelBotones, BorderLayout.SOUTH);
        
        add(centro, BorderLayout.CENTER);
    }
    
    private void actualizarVistaPreviaFolio() {
        String cantidadTexto = txtCantidad.getText().trim();
        if (!cantidadTexto.isEmpty()) {
            try {
                int cantidad = Integer.parseInt(cantidadTexto);
                if (cantidad > 0) {
                    lblFolioGenerado.setText("Se generará: 1-" + cantidad);
                    lblFolioGenerado.setForeground(GUINDO);
                    lblFolioGenerado.setFont(new Font("Arial", Font.BOLD, 14));
                } else {
                    lblFolioGenerado.setText("Cantidad debe ser mayor a 0");
                    lblFolioGenerado.setForeground(Color.RED);
                }
            } catch (NumberFormatException e) {
                lblFolioGenerado.setText("Cantidad inválida");
                lblFolioGenerado.setForeground(Color.RED);
            }
        } else {
            lblFolioGenerado.setText("Se generará automáticamente al guardar");
            lblFolioGenerado.setForeground(Color.GRAY);
        }
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
    
    private void guardarLote() {
        try {
            // Validar campos obligatorios
            if (txtLoteVacunacion.getText().trim().isEmpty() ||
                cbPais.getSelectedIndex() == 0 ||
                txtEmpresa.getText().trim().isEmpty() ||
                txtNombreComercial.getText().trim().isEmpty() ||
                txtCantidad.getText().trim().isEmpty() ||
                txtEnfermedadesSeleccionadas.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this, 
                    "Por favor complete todos los campos obligatorios", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "La cantidad debe ser mayor a 0", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Generar folio automáticamente como rango "1-cantidad"
            String folioGenerado = "1-" + cantidad;
            
            // Validar que el lote no exista ya por lote_vacunacion
            if (existeLote()) {
                JOptionPane.showMessageDialog(this, 
                    "El lote de vacunación ya existe", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String loteVacunacion = txtLoteVacunacion.getText().trim();
            String nombrePais = cbPais.getSelectedItem().toString();
            int idPais = obtenerIdPais(nombrePais);
            String empresa = txtEmpresa.getText().trim();
            String nombreComercial = txtNombreComercial.getText().trim();
            java.util.Date fechaUtil = (java.util.Date) spFechaCaducidad.getValue();
            java.sql.Date fechaCaducidad = new java.sql.Date(fechaUtil.getTime());
            String enfermedades = txtEnfermedadesSeleccionadas.getText().trim();
            
            // Guardar en base de datos
            guardarEnBD(loteVacunacion, folioGenerado, idPais, empresa, nombreComercial, 
                       fechaCaducidad, cantidad, enfermedades);
            
            JOptionPane.showMessageDialog(this, 
                "Lote guardado exitosamente!\n" +
                "Lote: " + loteVacunacion + "\n" +
                "Folio generado: " + folioGenerado + "\n" +
                "Cantidad: " + cantidad, 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cardLayout.show(contenedor, "modulo2");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Error: La cantidad debe ser un número válido", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de base de datos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private boolean existeLote() throws SQLException {
        String loteVacunacion = txtLoteVacunacion.getText().trim();
        String sql = "SELECT lote_vacunacion FROM lotes WHERE lote_vacunacion = ?";
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loteVacunacion);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    private void guardarEnBD(String loteVacunacion, String folio, int idPais, 
                              String empresa, String nombreComercial, 
                              java.sql.Date fechaCaducidad, int cantidad, 
                              String enfermedades) throws SQLException {
        String sql = "INSERT INTO lotes (lote_vacunacion, folio, id_pais, empresa, " +
                    "nombre_comercial, fecha_caducidad, cantidad, enfermedades) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loteVacunacion);
            pstmt.setString(2, folio);
            pstmt.setInt(3, idPais);
            pstmt.setString(4, empresa);
            pstmt.setString(5, nombreComercial);
            pstmt.setDate(6, fechaCaducidad);
            pstmt.setInt(7, cantidad);
            pstmt.setString(8, enfermedades);
            
            pstmt.executeUpdate();
        }
    }
    
    private void limpiarFormulario() {
        txtLoteVacunacion.setText("");
        cbPais.setSelectedIndex(0);
        txtEmpresa.setText("");
        txtNombreComercial.setText("");
        spFechaCaducidad.setValue(new java.util.Date());
        txtCantidad.setText("");
        txtEnfermedadesSeleccionadas.setText("");
        lblFolioGenerado.setText("Se generará automáticamente al guardar");
        lblFolioGenerado.setForeground(Color.GRAY);
        lblFolioGenerado.setFont(new Font("Arial", Font.ITALIC, 14));
    }
}