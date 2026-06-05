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

public class Modificacion_Salud extends JPanel {
    
    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    private static final Color NARANJA = new Color(255, 165, 0);
    
    // Campos de búsqueda
    private JTextField txtBuscar;
    private JButton btnBuscar;
    
    // Campos del formulario
    private JTextField txtNombre, txtCalle, txtNumeroExterior, txtNumeroInterior;
    private JTextField txtColonia, txtMunicipio, txtCodigoPostal, txtTelefono;
    private JComboBox<String> cbEstado, cbHorarioApertura, cbHorarioCierre;
    private JLabel lblIdCentro;
    
    // Estado del formulario
    private boolean editando = false;
    private int centroActualId = -1;
    
    // Arreglo con todos los estados de México
    private final String[] ESTADOS_MEXICO = {
        "Aguascalientes", "Baja California", "Baja California Sur", "Campeche", 
        "Chiapas", "Chihuahua", "Ciudad de México", "Coahuila", "Colima", 
        "Durango", "Estado de México", "Guanajuato", "Guerrero", "Hidalgo", 
        "Jalisco", "Michoacán", "Morelos", "Nayarit", "Nuevo León", "Oaxaca", 
        "Puebla", "Querétaro", "Quintana Roo", "San Luis Potosí", "Sinaloa", 
        "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala", "Veracruz", "Yucatán", "Zacatecas"
    };
    
    private CardLayout cardLayout;
    private JPanel contenedor;
    
    public Modificacion_Salud(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Modificar Centros de Salud");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);
        
        // Panel central
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(FONDO);
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBusqueda.setBackground(FONDO);
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar Centro de Salud"));
        
        JLabel lblBuscar = new JLabel("Nombre del Centro:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 14));
        
        txtBuscar = new JTextField(30);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(GUINDO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> buscarCentro());
        
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        
        centro.add(panelBusqueda, BorderLayout.NORTH);
        
        // Panel del formulario
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(FONDO);
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del Centro de Salud"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        /* ID Centro (solo lectura)
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblId = new JLabel("ID Centro:");
        lblId.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblId, gbc);
        gbc.gridx = 1;
        lblIdCentro = new JLabel("---");
        lblIdCentro.setFont(new Font("Arial", Font.PLAIN, 16));
        formulario.add(lblIdCentro, gbc);*/
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblNombre, gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(35);
        txtNombre.setEnabled(false);
        formulario.add(txtNombre, gbc);
        
        // Calle
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblCalle = new JLabel("Calle:");
        lblCalle.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblCalle, gbc);
        gbc.gridx = 1;
        txtCalle = new JTextField(35);
        txtCalle.setEnabled(false);
        formulario.add(txtCalle, gbc);
        
        // Número Exterior
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblNumeroExterior = new JLabel("Número Exterior:");
        lblNumeroExterior.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblNumeroExterior, gbc);
        gbc.gridx = 1;
        txtNumeroExterior = new JTextField(15);
        txtNumeroExterior.setEnabled(false);
        formulario.add(txtNumeroExterior, gbc);
        
        // Número Interior
        /*gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblNumeroInterior = new JLabel("Número Interior:");
        lblNumeroInterior.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblNumeroInterior, gbc);
        gbc.gridx = 1;
        txtNumeroInterior = new JTextField(15);
        txtNumeroInterior.setEnabled(false);
        formulario.add(txtNumeroInterior, gbc);*/
        
        // Colonia
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblColonia = new JLabel("Colonia:");
        lblColonia.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblColonia, gbc);
        gbc.gridx = 1;
        txtColonia = new JTextField(35);
        txtColonia.setEnabled(false);
        formulario.add(txtColonia, gbc);
        
        // Municipio
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblMunicipio = new JLabel("Municipio:");
        lblMunicipio.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblMunicipio, gbc);
        gbc.gridx = 1;
        txtMunicipio = new JTextField(35);
        txtMunicipio.setEnabled(false);
        formulario.add(txtMunicipio, gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblEstado, gbc);
        gbc.gridx = 1;
        cbEstado = new JComboBox<>(ESTADOS_MEXICO);
        cbEstado.setFont(new Font("Arial", Font.PLAIN, 14));
        cbEstado.setPreferredSize(new Dimension(200, 30));
        cbEstado.setEnabled(false);
        formulario.add(cbEstado, gbc);
        
        // Código Postal
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel lblCodigoPostal = new JLabel("Código Postal:");
        lblCodigoPostal.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblCodigoPostal, gbc);
        gbc.gridx = 1;
        txtCodigoPostal = new JTextField(10);
        txtCodigoPostal.setEnabled(false);
        formulario.add(txtCodigoPostal, gbc);
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = 9;
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblTelefono, gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(20);
        txtTelefono.setEnabled(false);
        formulario.add(txtTelefono, gbc);
        
        // Horario Apertura
        gbc.gridx = 0; gbc.gridy = 10;
        JLabel lblHorarioApertura = new JLabel("Horario Apertura:");
        lblHorarioApertura.setFont(new Font("Arial", Font.BOLD, 16));
        formulario.add(lblHorarioApertura, gbc);
        gbc.gridx = 1;
        
        String[] horarios = generarHorarios();
        cbHorarioApertura = new JComboBox<>(horarios);
        cbHorarioApertura.setFont(new Font("Arial", Font.PLAIN, 14));
        cbHorarioApertura.setPreferredSize(new Dimension(200, 30));
        cbHorarioApertura.setEnabled(false);
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
        cbHorarioCierre.setEnabled(false);
        formulario.add(cbHorarioCierre, gbc);
        
        centro.add(formulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBotones.setBackground(FONDO);
        
        JButton btnHabilitarEdicion = new JButton("Habilitar Edición");
        btnHabilitarEdicion.setBackground(NARANJA);
        btnHabilitarEdicion.setForeground(Color.WHITE);
        btnHabilitarEdicion.setFocusPainted(false);
        btnHabilitarEdicion.setBorderPainted(false);
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
        btnGuardar.addActionListener(e -> {
            try {
                guardarCambios();
            } catch (SQLException ex) {
                System.getLogger(Modificacion_Salud.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
        
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
            if (centroActualId != -1) {
                cargarDatosCentro(centroActualId);
            }
        });
        
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
        
        panelBotones.add(btnHabilitarEdicion);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnRegresar);
        
        centro.add(panelBotones, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
    }
    
    private String[] generarHorarios() {
        String[] horarios = new String[48];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (int i = 0; i < 48; i++) {
            LocalTime time = LocalTime.of(i / 2, (i % 2) * 30);
            horarios[i] = time.format(formatter);
        }
        return horarios;
    }
    
    private void buscarCentro() {
        String nombreBuscar = txtBuscar.getText().trim();
        
        if (nombreBuscar.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese el nombre del centro de salud para buscar", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_centro FROM centros_salud WHERE nombre LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + nombreBuscar + "%");
            ResultSet rs = pstmt.executeQuery();
            
            java.util.ArrayList<Integer> ids = new java.util.ArrayList<>();
            java.util.ArrayList<String> nombres = new java.util.ArrayList<>();
            
            while (rs.next()) {
                ids.add(rs.getInt("id_centro"));
                // Obtener nombre para mostrar
                String sqlNombre = "SELECT nombre FROM centros_salud WHERE id_centro = ?";
                PreparedStatement pstmtNombre = conn.prepareStatement(sqlNombre);
                pstmtNombre.setInt(1, rs.getInt("id_centro"));
                ResultSet rsNombre = pstmtNombre.executeQuery();
                if (rsNombre.next()) {
                    nombres.add(rsNombre.getString("nombre"));
                }
                rsNombre.close();
                pstmtNombre.close();
            }
            rs.close();
            pstmt.close();
            
            if (ids.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontró ningún centro con ese nombre", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (ids.size() == 1) {
                centroActualId = ids.get(0);
                cargarDatosCentro(centroActualId);
                JOptionPane.showMessageDialog(this, 
                    "Centro encontrado: " + nombres.get(0), 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Múltiples resultados, mostrar opciones
                String[] opciones = nombres.toArray(new String[0]);
                String seleccion = (String) JOptionPane.showInputDialog(this,
                    "Se encontraron varios centros. Seleccione uno:",
                    "Múltiples resultados",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);
                
                if (seleccion != null) {
                    int index = nombres.indexOf(seleccion);
                    centroActualId = ids.get(index);
                    cargarDatosCentro(centroActualId);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al buscar: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cargarDatosCentro(int idCentro) {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_centro, nombre, calle, numero_exterior, numero_interior, " +
                        "colonia, municipio, estado, codigo_postal, telefono, " +
                        "horario_apertura, horario_cierre " +
                        "FROM centros_salud WHERE id_centro = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idCentro);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                lblIdCentro.setText(String.valueOf(rs.getInt("id_centro")));
                txtNombre.setText(rs.getString("nombre"));
                txtCalle.setText(rs.getString("calle"));
                txtNumeroExterior.setText(rs.getString("numero_exterior"));
                
                String numInt = rs.getString("numero_interior");
                if (numInt != null) {
                    txtNumeroInterior.setText(numInt);
                } else {
                    txtNumeroInterior.setText("");
                }
                
                txtColonia.setText(rs.getString("colonia"));
                txtMunicipio.setText(rs.getString("municipio"));
                
                String estado = rs.getString("estado");
                cbEstado.setSelectedItem(estado);
                
                txtCodigoPostal.setText(rs.getString("codigo_postal"));
                txtTelefono.setText(rs.getString("telefono"));
                
                String horaApertura = rs.getTime("horario_apertura").toString().substring(0, 5);
                String horaCierre = rs.getTime("horario_cierre").toString().substring(0, 5);
                cbHorarioApertura.setSelectedItem(horaApertura);
                cbHorarioCierre.setSelectedItem(horaCierre);
            }
            rs.close();
            pstmt.close();
            
            habilitarEdicion(false);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar datos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void habilitarEdicion(boolean habilitar) {
        editando = habilitar;
        txtNombre.setEnabled(habilitar);
        txtCalle.setEnabled(habilitar);
        txtNumeroExterior.setEnabled(habilitar);
        txtNumeroInterior.setEnabled(habilitar);
        txtColonia.setEnabled(habilitar);
        txtMunicipio.setEnabled(habilitar);
        cbEstado.setEnabled(habilitar);
        txtCodigoPostal.setEnabled(habilitar);
        txtTelefono.setEnabled(habilitar);
        cbHorarioApertura.setEnabled(habilitar);
        cbHorarioCierre.setEnabled(habilitar);
        
        if (!habilitar) {
            btnBuscar.setEnabled(true);
        } else {
            btnBuscar.setEnabled(false);
            txtNombre.requestFocus();
        }
    }
    
    private void guardarCambios() throws SQLException {
        if (!editando) {
            JOptionPane.showMessageDialog(this, 
                "Primero habilite la edición", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (centroActualId == -1) {
            JOptionPane.showMessageDialog(this, 
                "No hay ningún centro cargado", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
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
        
        // Validar código postal
        String cp = txtCodigoPostal.getText().trim();
        if (!cp.matches("\\d{5}")) {
            JOptionPane.showMessageDialog(this, 
                "El código postal debe tener 5 dígitos", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar horarios
        String horaApertura = (String) cbHorarioApertura.getSelectedItem();
        String horaCierre = (String) cbHorarioCierre.getSelectedItem();
        
        if (horaApertura.compareTo(horaCierre) >= 0) {
            JOptionPane.showMessageDialog(this, 
                "El horario de apertura debe ser menor que el horario de cierre", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar si el nombre ya existe (y no es el mismo centro)
        if (!txtNombre.getText().trim().equals(obtenerNombreActual())) {
            if (existeNombre(txtNombre.getText().trim())) {
                JOptionPane.showMessageDialog(this, 
                    "El nombre '" + txtNombre.getText() + "' ya existe en otro centro", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de guardar los cambios?\n" +
            "ID Centro: " + centroActualId, 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
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
                
                Time horarioAperturaTime = Time.valueOf(horaApertura + ":00");
                Time horarioCierreTime = Time.valueOf(horaCierre + ":00");
                
                actualizarEnBD(centroActualId, nombre, calle, numeroExterior, numeroInterior,
                              colonia, municipio, estado, codigoPostal, telefono,
                              horarioAperturaTime, horarioCierreTime);
                
                JOptionPane.showMessageDialog(this, 
                    "Centro de Salud modificado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                habilitarEdicion(false);
                cargarDatosCentro(centroActualId);
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private String obtenerNombreActual() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT nombre FROM centros_salud WHERE id_centro = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, centroActualId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
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
    
    private void actualizarEnBD(int idCentro, String nombre, String calle, String numeroExterior,
                                 String numeroInterior, String colonia, String municipio,
                                 String estado, String codigoPostal, String telefono,
                                 Time horarioApertura, Time horarioCierre) throws SQLException {
        
        String sql = "UPDATE centros_salud SET nombre = ?, calle = ?, numero_exterior = ?, " +
                    "numero_interior = ?, colonia = ?, municipio = ?, estado = ?, " +
                    "codigo_postal = ?, telefono = ?, horario_apertura = ?, horario_cierre = ? " +
                    "WHERE id_centro = ?";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
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
            pstmt.setInt(12, idCentro);
            
            pstmt.executeUpdate();
        }
    }
}