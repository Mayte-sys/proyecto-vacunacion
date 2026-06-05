
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
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import sector_salud.Conexion_DB;

public class Altas_Salud extends JPanel {
    
    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    
    private JTextField txtNombre, txtCalle, txtNumeroExterior, txtNumeroInterior;
    private JTextField txtColonia, txtMunicipio, txtCodigoPostal, txtTelefono;
    private JComboBox<String> cbEstado, cbHorarioApertura, cbHorarioCierre;
    
    private CardLayout cardLayout;
    private JPanel contenedor;
    
    // Arreglo con todos los estados de México
    private final String[] ESTADOS_MEXICO = { "Selecciona un estado",
        "Aguascalientes", "Baja California", "Baja California Sur", "Campeche", 
        "Chiapas", "Chihuahua", "Ciudad de México", "Coahuila", "Colima", 
        "Durango", "Estado de México", "Guanajuato", "Guerrero", "Hidalgo", 
        "Jalisco", "Michoacán", "Morelos", "Nayarit", "Nuevo León", "Oaxaca", 
        "Puebla", "Querétaro", "Quintana Roo", "San Luis Potosí", "Sinaloa", 
        "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala", "Veracruz", "Yucatán", "Zacatecas"
    };
    
    public Altas_Salud(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Alta de Centros de Salud");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);
        
        // Panel central con formulario
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblNombre, gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(35);
        formulario.add(txtNombre, gbc);
        
        // Calle
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblCalle = new JLabel("Calle:");
        lblCalle.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblCalle, gbc);
        gbc.gridx = 1;
        txtCalle = new JTextField(35);
        formulario.add(txtCalle, gbc);
        
        // Número Exterior
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblNumeroExterior = new JLabel("Número Exterior:");
        lblNumeroExterior.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblNumeroExterior, gbc);
        gbc.gridx = 1;
        txtNumeroExterior = new JTextField(15);
        formulario.add(txtNumeroExterior, gbc);
        
        // Número Interior
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblNumeroInterior = new JLabel("Número Interior:");
        lblNumeroInterior.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblNumeroInterior, gbc);
        gbc.gridx = 1;
        txtNumeroInterior = new JTextField(15);
        formulario.add(txtNumeroInterior, gbc);
        
        // Colonia
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblColonia = new JLabel("Colonia:");
        lblColonia.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblColonia, gbc);
        gbc.gridx = 1;
        txtColonia = new JTextField(35);
        formulario.add(txtColonia, gbc);
        
        // Municipio
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblMunicipio = new JLabel("Municipio:");
        lblMunicipio.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblMunicipio, gbc);
        gbc.gridx = 1;
        txtMunicipio = new JTextField(35);
        formulario.add(txtMunicipio, gbc);
        
        // Estado (ComboBox)
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblEstado, gbc);
        gbc.gridx = 1;
        cbEstado = new JComboBox<>(ESTADOS_MEXICO);
        cbEstado.setFont(new Font("Arial", Font.PLAIN, 14));
        cbEstado.setPreferredSize(new Dimension(200, 30));
        formulario.add(cbEstado, gbc);
        
        // Código Postal
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel lblCodigoPostal = new JLabel("Código Postal:");
        lblCodigoPostal.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblCodigoPostal, gbc);
        gbc.gridx = 1;
        txtCodigoPostal = new JTextField(10);
        formulario.add(txtCodigoPostal, gbc);
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = 9;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblTelefono, gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(20);
        formulario.add(txtTelefono, gbc);
        
        // Horario Apertura
        gbc.gridx = 0; gbc.gridy = 10;
        JLabel lblHorarioApertura = new JLabel("Horario Apertura:");
        lblHorarioApertura.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblHorarioApertura, gbc);
        gbc.gridx = 1;
        
        // Crear horarios cada 30 minutos
        String[] horarios = generarHorarios();
        cbHorarioApertura = new JComboBox<>(horarios);
        cbHorarioApertura.setFont(new Font("Arial", Font.PLAIN, 14));
        cbHorarioApertura.setPreferredSize(new Dimension(200, 30));
        cbHorarioApertura.setSelectedItem("00:00");
        formulario.add(cbHorarioApertura, gbc);
        
        // Horario Cierre
        gbc.gridx = 0; gbc.gridy = 11;
        JLabel lblHorarioCierre = new JLabel("Horario Cierre:");
        lblHorarioCierre.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblHorarioCierre, gbc);
        gbc.gridx = 1;
        
        cbHorarioCierre = new JComboBox<>(horarios);
        cbHorarioCierre.setFont(new Font("Arial", Font.PLAIN, 14));
        cbHorarioCierre.setPreferredSize(new Dimension(200, 30));
        cbHorarioCierre.setSelectedItem("20:00");
        formulario.add(cbHorarioCierre, gbc);
       
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
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo3"));
        
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
        btnGuardar.addActionListener(e -> guardarCentroSalud());
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
    
    private String[] generarHorarios() {
        String[] horarios = new String[48]; // 24 horas * 2 (cada 30 min)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (int i = 0; i < 48; i++) {
            LocalTime time = LocalTime.of(i / 2, (i % 2) * 30);
            horarios[i] = time.format(formatter);
        }
        return horarios;
    }
    
    private void guardarCentroSalud() {
        try {
            // Validar campos obligatorios
            if (txtNombre.getText().trim().isEmpty() || 
                txtCalle.getText().trim().isEmpty() ||
                txtNumeroExterior.getText().trim().isEmpty() ||
                txtColonia.getText().trim().isEmpty() ||
                txtMunicipio.getText().trim().isEmpty() ||
                txtCodigoPostal.getText().trim().isEmpty() ||
                txtTelefono.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this, 
                    "Por favor complete todos los campos obligatorios", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar que el nombre no exista ya
            if (existeNombre(txtNombre.getText().trim())) {
                JOptionPane.showMessageDialog(this, 
                    "El nombre '" + txtNombre.getText() + "' ya existe", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar código postal (5 dígitos)
            String cp = txtCodigoPostal.getText().trim();
            if (!cp.matches("\\d{5}")) {
                JOptionPane.showMessageDialog(this, 
                    "El código postal debe tener 5 dígitos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Obtener valores
            String nombre = txtNombre.getText().trim();
            String calle = txtCalle.getText().trim();
            String numeroExterior = txtNumeroExterior.getText().trim();
            String numeroInterior = txtNumeroInterior.getText().trim();
            if (numeroInterior.isEmpty()) {
                numeroInterior = null;
            }
            String colonia = txtColonia.getText().trim();
            String municipio = txtMunicipio.getText().trim();
            String estado = (String) cbEstado.getSelectedItem();
            String codigoPostal = cp;
            String telefono = txtTelefono.getText().trim();
            
            // Convertir horarios a Time
            Time horarioApertura = Time.valueOf(cbHorarioApertura.getSelectedItem().toString() + ":00");
            Time horarioCierre = Time.valueOf(cbHorarioCierre.getSelectedItem().toString() + ":00");
            
            // Validar que apertura sea menor que cierre
            if (horarioApertura.compareTo(horarioCierre) >= 0) {
                JOptionPane.showMessageDialog(this, 
                    "El horario de apertura debe ser menor que el horario de cierre", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Guardar en base de datos (el ID se genera automáticamente)
            int idGenerado = guardarEnBD(nombre, calle, numeroExterior, numeroInterior, colonia, 
                       municipio, estado, codigoPostal, telefono, 
                       horarioApertura, horarioCierre);
            
            JOptionPane.showMessageDialog(this, 
                "Centro de Salud guardado exitosamente con ID: " + idGenerado, 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cardLayout.show(contenedor, "modulo3");
            
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
    
    private boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT nombre FROM centros_salud WHERE nombre = ?";
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    private int guardarEnBD(String nombre, String calle, String numeroExterior, 
                             String numeroInterior, String colonia, String municipio, 
                             String estado, String codigoPostal, String telefono, 
                             Time horarioApertura, Time horarioCierre) throws SQLException {
        
        String sql = "INSERT INTO centros_salud (nombre, calle, numero_exterior, " +
                    "numero_interior, colonia, municipio, estado, codigo_postal, telefono, " +
                    "horario_apertura, horario_cierre, activo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, calle);
            pstmt.setString(3, numeroExterior);
            pstmt.setString(4, numeroInterior);
            pstmt.setString(5, colonia);
            pstmt.setString(6, municipio);
            pstmt.setString(7, estado);
            pstmt.setString(8, codigoPostal);
            pstmt.setString(9, telefono);
            pstmt.setTime(10, horarioApertura);
            pstmt.setTime(11, horarioCierre);
            pstmt.setBoolean(12, true); // activo = true
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se pudo insertar el registro");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado");
                }
            }
        }
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtCalle.setText("");
        txtNumeroExterior.setText("");
        txtNumeroInterior.setText("");
        txtColonia.setText("");
        txtMunicipio.setText("");
        txtCodigoPostal.setText("");
        txtTelefono.setText("");
        cbEstado.setSelectedIndex(0);
        cbHorarioApertura.setSelectedItem("00:00");
        cbHorarioCierre.setSelectedItem("20:00");
    }
}