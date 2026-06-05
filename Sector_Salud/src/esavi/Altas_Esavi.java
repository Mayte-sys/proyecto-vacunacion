/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package esavi;

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

public class Altas_Esavi extends JPanel {
    
    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    
    private JTextField txtCURP, txtNombrePaciente, txtLoteVacuna;
    private JTextArea txtDescripcion;
    private JTextField txtCedulaDoctor;
    private JComboBox<String> cbCentroSalud;
    private Map<Integer, String> mapaCentros;
    
    private CardLayout cardLayout;
    private JPanel contenedor;
    private String cedulaDoctorActual;
    private int idCentroAsociado;
    
    public Altas_Esavi(CardLayout cardLayout, JPanel contenedor, String cedulaDoctor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.cedulaDoctorActual = cedulaDoctor;
        this.mapaCentros = new HashMap<>();
        this.idCentroAsociado = -1;
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Registro de ESAVI");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);
        
        // Panel central con formulario
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // CURP del Paciente
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblCURP = new JLabel("CURP del Paciente:");
        lblCURP.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblCURP, gbc);
        gbc.gridx = 1;
        txtCURP = new JTextField(25);
        formulario.add(txtCURP, gbc);
        
        // Botón para validar CURP
        JButton btnValidarCURP = new JButton("Validar CURP");
        btnValidarCURP.setBackground(GUINDO);
        btnValidarCURP.setForeground(Color.WHITE);
        btnValidarCURP.setFocusPainted(false);
        btnValidarCURP.setFont(new Font("Arial", Font.BOLD, 12));
        btnValidarCURP.addActionListener(e -> validarCURP());
        gbc.gridx = 2;
        formulario.add(btnValidarCURP, gbc);
        
        // Nombre del Paciente (se llena automáticamente)
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblNombre = new JLabel("Nombre del Paciente:");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblNombre, gbc);
        gbc.gridx = 1;
        txtNombrePaciente = new JTextField(35);
        txtNombrePaciente.setEditable(false);
        txtNombrePaciente.setBackground(new Color(240, 240, 240));
        formulario.add(txtNombrePaciente, gbc);
        
        // Cédula del Doctor (automática, solo lectura)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblCedula = new JLabel("Cédula del Doctor:");
        lblCedula.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblCedula, gbc);
        gbc.gridx = 1;
        txtCedulaDoctor = new JTextField(35);
        txtCedulaDoctor.setText(cedulaDoctorActual);
        txtCedulaDoctor.setEditable(false);
        txtCedulaDoctor.setBackground(new Color(240, 240, 240));
        formulario.add(txtCedulaDoctor, gbc);
        
        // Lote de Vacuna
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblLote = new JLabel("Lote de Vacunación:");
        lblLote.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblLote, gbc);
        gbc.gridx = 1;
        txtLoteVacuna = new JTextField(35);
        formulario.add(txtLoteVacuna, gbc);
        
        // Botón para validar Lote
        JButton btnValidarLote = new JButton("Validar Lote");
        btnValidarLote.setBackground(GUINDO);
        btnValidarLote.setForeground(Color.WHITE);
        btnValidarLote.setFocusPainted(false);
        btnValidarLote.setFont(new Font("Arial", Font.BOLD, 12));
        btnValidarLote.addActionListener(e -> validarLoteVacuna());
        gbc.gridx = 2;
        formulario.add(btnValidarLote, gbc);
        
        // Centro de Salud (automático según el doctor)
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblCentro = new JLabel("Centro de Salud:");
        lblCentro.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblCentro, gbc);
        gbc.gridx = 1;
        cbCentroSalud = new JComboBox<>();
        cbCentroSalud.setFont(new Font("Arial", Font.PLAIN, 14));
        cbCentroSalud.setPreferredSize(new Dimension(200, 30));
        cbCentroSalud.setEnabled(false);
        cbCentroSalud.setBackground(new Color(240, 240, 240));
        formulario.add(cbCentroSalud, gbc);
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblDescripcion, gbc);
        gbc.gridx = 1;
        txtDescripcion = new JTextArea(4, 35);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setPreferredSize(new Dimension(300, 100));
        formulario.add(scrollDescripcion, gbc);
        
        // Panel de botones principales
        JPanel panelBotones = new JPanel(new BorderLayout());
        panelBotones.setBackground(FONDO);
        panelBotones.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Botón Regresar a la izquierda
        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.setBackground(GUINDO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.setPreferredSize(new Dimension(120, 40));
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo5"));
        
        btnRegresar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                btnRegresar.setBackground(GUINDO_HOVER); 
            }
            public void mouseExited(java.awt.event.MouseEvent e) { 
                btnRegresar.setBackground(GUINDO); 
            }
        });
        
        panelBotones.add(btnRegresar, BorderLayout.WEST);
        
        // Panel para Guardar y Cancelar
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelDerecho.setBackground(FONDO);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(GUINDO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setPreferredSize(new Dimension(120, 40));
        btnGuardar.addActionListener(e -> guardarESAVI());
        
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
        btnCancelar.addActionListener(e -> limpiarFormulario());
        
        panelDerecho.add(btnGuardar);
        panelDerecho.add(btnCancelar);
        
        panelBotones.add(panelDerecho, BorderLayout.CENTER);
        
        // Agregar componentes al panel principal
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FONDO);
        centro.add(formulario, BorderLayout.CENTER);
        centro.add(panelBotones, BorderLayout.SOUTH);
        
        add(centro, BorderLayout.CENTER);
        
        // Cargar el centro de salud asociado al doctor
        cargarCentroSaludDoctor();
    }
    
    private void cargarCentroSaludDoctor() {
        try {
            String sql = "SELECT e.id_centro, c.nombre FROM empleados e " +
                        "JOIN centros_salud c ON e.id_centro = c.id_centro " +
                        "WHERE e.cedula_profesional = ?";
            try (Connection conn = Conexion_DB.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, cedulaDoctorActual);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    idCentroAsociado = rs.getInt("id_centro");
                    String nombreCentro = rs.getString("nombre");
                    cbCentroSalud.addItem(nombreCentro);
                    mapaCentros.put(idCentroAsociado, nombreCentro);
                } else {
                    cbCentroSalud.addItem("No hay centro asociado");
                    JOptionPane.showMessageDialog(this, 
                        "El doctor no tiene un centro de salud asociado", 
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar centro de salud: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void validarCURP() {
        String curp = txtCURP.getText().trim().toUpperCase();
        if (curp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una CURP para validar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String[] datos = obtenerDatosPorCURP(curp);
            if (datos != null) {
                txtNombrePaciente.setText(datos[0] + " " + datos[1]);
                txtCURP.setBackground(Color.WHITE);
                JOptionPane.showMessageDialog(this, "CURP válida\nPaciente: " + datos[0] + " " + datos[1], "Validación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                txtNombrePaciente.setText("");
                txtCURP.setBackground(new Color(255, 200, 200));
                JOptionPane.showMessageDialog(this, "CURP no registrada en el sistema", "Error", JOptionPane.ERROR_MESSAGE);
                txtCURP.requestFocus();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al validar CURP: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private String[] obtenerDatosPorCURP(String curp) throws SQLException {
        String sql = "SELECT nombre, apellido FROM ciudadanos WHERE curp = ?";
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, curp);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String[] datos = new String[2];
                    datos[0] = rs.getString("nombre");
                    datos[1] = rs.getString("apellido");
                    return datos;
                }
                return null;
            }
        }
    }
    
    private void validarLoteVacuna() {
        String lote = txtLoteVacuna.getText().trim();
        if (lote.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un lote para validar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (existeLoteVacuna(lote)) {
                JOptionPane.showMessageDialog(this, "Lote de vacuna válido", "Validación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                txtLoteVacuna.setBackground(Color.WHITE);
            } else {
                JOptionPane.showMessageDialog(this, "El lote de vacuna no existe", "Error", JOptionPane.ERROR_MESSAGE);
                txtLoteVacuna.setBackground(new Color(255, 200, 200));
                txtLoteVacuna.requestFocus();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al validar lote: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private boolean existeLoteVacuna(String loteVacunacion) throws SQLException {
        String sql = "SELECT id_lote FROM lotes WHERE lote_vacunacion = ?";
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loteVacunacion);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    private int obtenerIdLote(String loteVacunacion) throws SQLException {
        String sql = "SELECT id_lote FROM lotes WHERE lote_vacunacion = ?";
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loteVacunacion);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_lote");
                }
                return -1;
            }
        }
    }
    
    private void guardarESAVI() {
        try {
            // Validar campos obligatorios
            if (txtCURP.getText().trim().isEmpty() || 
                txtLoteVacuna.getText().trim().isEmpty() ||
                txtDescripcion.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this, 
                    "Por favor complete todos los campos obligatorios", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String curp = txtCURP.getText().trim().toUpperCase();
            String nombreCompleto = txtNombrePaciente.getText().trim();
            String cedulaDoctor = txtCedulaDoctor.getText().trim();
            String loteVacuna = txtLoteVacuna.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            
            // Validar que se tenga el nombre del paciente
            if (nombreCompleto.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Primero valide la CURP del paciente", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar que se haya seleccionado un centro de salud
            if (idCentroAsociado == -1) {
                JOptionPane.showMessageDialog(this, 
                    "No hay un centro de salud asociado al doctor", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar CURP
            String[] datos = obtenerDatosPorCURP(curp);
            if (datos == null) {
                JOptionPane.showMessageDialog(this, 
                    "La CURP no está registrada en el sistema", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar Lote
            if (!existeLoteVacuna(loteVacuna)) {
                JOptionPane.showMessageDialog(this, 
                    "El lote de vacuna no existe", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Obtener ID del lote
            int idLote = obtenerIdLote(loteVacuna);
            
            // Guardar en base de datos
            int idGenerado = guardarEnBD(curp, nombreCompleto, cedulaDoctor, idLote, idCentroAsociado, descripcion);
            
            JOptionPane.showMessageDialog(this, 
                "ESAVI registrado exitosamente con ID: " + idGenerado + "\n" +
                "Paciente: " + nombreCompleto, 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cardLayout.show(contenedor, "modulo5");
            
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
    
    private int guardarEnBD(String curp, String nombreCuidadano, String cedulaDoctor, 
                            int idLote, int idCentro, String descripcion) throws SQLException {
        String sql = "INSERT INTO esavi (curp_cuidadano, nombre_cuidadano, cedula_doctor, " +
                    "id_lote, id_centro, descripcion) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, curp);
            pstmt.setString(2, nombreCuidadano);
            pstmt.setString(3, cedulaDoctor);
            pstmt.setInt(4, idLote);
            pstmt.setInt(5, idCentro);
            pstmt.setString(6, descripcion);
            
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
        txtCURP.setText("");
        txtNombrePaciente.setText("");
        txtLoteVacuna.setText("");
        txtDescripcion.setText("");
        txtCURP.setBackground(Color.WHITE);
        txtLoteVacuna.setBackground(Color.WHITE);
    }
}