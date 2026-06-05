package app_ciudadanos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ventana_Registro extends JFrame {

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCURP;
    private JDateChooser fechaNacimiento;
    private JComboBox<String> cbGenero;
    private JTextField txtTelefono;
    private JTextField txtCalle;
    private JTextField txtNumeroExterior;
    private JTextField txtNumeroInterior;
    private JTextField txtColonia;
    private JTextField txtMunicipio;
    private JTextField txtEstado;
    private JTextField txtCodigoPostal;
    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmarPassword;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JLabel lblMensaje;
    private JScrollPane scrollPane;

    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    private static final Color GRIS_BORDE = new Color(200, 200, 200);

    public Ventana_Registro() {
        configurarVentana();
        construirUI();
        setIconoVentana();
    }
    private void configurarVentana() {
        setTitle("Registro de Ciudadano — Ciudadanos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(FONDO);
        setLayout(new BorderLayout());
    }
    private void construirUI() {
        setLayout(new BorderLayout());
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        barra.setBackground(GUINDO);
        barra.setPreferredSize(new Dimension(getWidth(), 70));

        JLabel lblTitulo = new JLabel("Registro de Nuevo Ciudadano");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));

        barra.add(lblTitulo);
        return barra;
    }
    private void setIconoVentana() {
        URL url = getClass().getResource("/recursos/logo.png");
        if (url != null) {
            ImageIcon icono = new ImageIcon(url);
            setIconImage(icono.getImage());
        } else {
            try {
                Image icono = Toolkit.getDefaultToolkit().getImage("logo.png");
                setIconImage(icono);
            } catch (Exception e) {
                System.err.println("No se pudo cargar el ícono");
            }
        }
    }

    private JPanel crearPanelFormulario() {
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(FONDO);

        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 215), 1),
            new EmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(8, 10, 8, 10);

        int row = 0;

        // Título Datos Personales
        JLabel lblTituloPersonal = new JLabel("DATOS PERSONALES");
        lblTituloPersonal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTituloPersonal.setForeground(GUINDO);
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 15, 0);
        tarjeta.add(lblTituloPersonal, c);
        c.gridwidth = 1;
        c.insets = new Insets(8, 10, 8, 10);

        // Nombre
        txtNombre = new JTextField();
        addCampo(tarjeta, c, "Nombre completo:*", txtNombre, row++);
        
        // Apellido
        txtApellido = new JTextField();
        addCampo(tarjeta, c, "Apellido:*", txtApellido, row++);
        
        // CURP
        txtCURP = new JTextField();
        txtCURP.setDocument(new JTextFieldLimit(18));
        addCampo(tarjeta, c, "CURP:*", txtCURP, row++);
        
        // Fecha de Nacimiento con JCalendar
        JLabel lblFecha = new JLabel("Fecha de Nacimiento:*");
        lblFecha.setFont(new Font("Arial", Font.BOLD, 12));
        c.gridx = 0;
        c.gridy = row;
        tarjeta.add(lblFecha, c);
        
        fechaNacimiento = new JDateChooser();
        fechaNacimiento.setPreferredSize(new Dimension(200, 35));
        fechaNacimiento.setDateFormatString("yyyy-MM-dd");
        fechaNacimiento.getCalendarButton().setBackground(GUINDO);
        fechaNacimiento.getCalendarButton().setForeground(Color.WHITE);
        
        JPanel fechaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fechaPanel.setBackground(Color.WHITE);
        fechaPanel.add(fechaNacimiento);
        
        c.gridx = 1;
        c.gridy = row++;
        tarjeta.add(fechaPanel, c);
        
        // Género
        cbGenero = new JComboBox<>(new String[]{"Selecciona un género", "Masculino", "Femenino"});
        addCampo(tarjeta, c, "Género:*", cbGenero, row++);
        
        // Teléfono
        txtTelefono = new JTextField();
        txtTelefono.setDocument(new JTextFieldLimit(15));
        addCampo(tarjeta, c, "Teléfono:*", txtTelefono, row++);
        
        // Separador
        addSeparador(tarjeta, c, row++);
        
        // Título Dirección
        JLabel lblDireccion = new JLabel("DIRECCIÓN");
        lblDireccion.setFont(new Font("Arial", Font.BOLD, 16));
        lblDireccion.setForeground(GUINDO);
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.insets = new Insets(10, 0, 15, 0);
        tarjeta.add(lblDireccion, c);
        c.gridwidth = 1;
        c.insets = new Insets(8, 10, 8, 10);
        
        // Calle
        txtCalle = new JTextField();
        addCampo(tarjeta, c, "Calle:*", txtCalle, row++);
        
        // Número Exterior
        txtNumeroExterior = new JTextField();
        addCampo(tarjeta, c, "Número Exterior:*", txtNumeroExterior, row++);
        
        // Número Interior
        txtNumeroInterior = new JTextField();
        addCampo(tarjeta, c, "Número Interior:", txtNumeroInterior, row++);
        
        // Colonia
        txtColonia = new JTextField();
        addCampo(tarjeta, c, "Colonia:*", txtColonia, row++);
        
        // Municipio
        txtMunicipio = new JTextField();
        addCampo(tarjeta, c, "Municipio:*", txtMunicipio, row++);
        
        // Estado
        txtEstado = new JTextField();
        addCampo(tarjeta, c, "Estado:*", txtEstado, row++);
        
        // Código Postal
        txtCodigoPostal = new JTextField();
        txtCodigoPostal.setDocument(new JTextFieldLimit(5));
        addCampo(tarjeta, c, "Código Postal:*", txtCodigoPostal, row++);
        
        // Separador
        addSeparador(tarjeta, c, row++);
        
        // Título Cuenta
        JLabel lblCuenta = new JLabel("DATOS DE ACCESO");
        lblCuenta.setFont(new Font("Arial", Font.BOLD, 16));
        lblCuenta.setForeground(GUINDO);
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.insets = new Insets(10, 0, 15, 0);
        tarjeta.add(lblCuenta, c);
        c.gridwidth = 1;
        c.insets = new Insets(8, 10, 8, 10);
        
        // Correo
        txtCorreo = new JTextField();
        addCampo(tarjeta, c, "Correo Electrónico:*", txtCorreo, row++);
        
        // Contraseña
        txtPassword = new JPasswordField();
        addCampo(tarjeta, c, "Contraseña:*", txtPassword, row++);
        
        // Confirmar Contraseña
        txtConfirmarPassword = new JPasswordField();
        addCampo(tarjeta, c, "Confirmar Contraseña:*", txtConfirmarPassword, row++);
        
        // Mensaje de error
        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setFont(new Font("Arial", Font.ITALIC, 11));
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.insets = new Insets(10, 0, 10, 0);
        tarjeta.add(lblMensaje, c);
        c.gridwidth = 1;
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setBackground(Color.WHITE);
        
        btnRegistrar = new JButton("REGISTRARSE");
        btnRegistrar.setBackground(GUINDO);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegistrar.setPreferredSize(new Dimension(180, 45));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(e -> registrarCiudadano());
        btnRegistrar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnRegistrar.setBackground(new Color(140, 40, 62)); }
            public void mouseExited(MouseEvent e) { btnRegistrar.setBackground(GUINDO); }
        });
        
        btnCancelar = new JButton("CANCELAR");
        btnCancelar.setBackground(Color.LIGHT_GRAY);
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancelar.setPreferredSize(new Dimension(180, 45));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> cancelarRegistro());
        btnCancelar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnCancelar.setBackground(Color.GRAY); btnCancelar.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { btnCancelar.setBackground(Color.LIGHT_GRAY); btnCancelar.setForeground(Color.BLACK); }
        });
        
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        c.insets = new Insets(20, 0, 0, 0);
        tarjeta.add(panelBotones, c);
        
        // Scroll pane para el formulario
        scrollPane = new JScrollPane(tarjeta);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        contenedor.add(scrollPane, gbc);
        return contenedor;
    }
    
    private void addCampo(JPanel panel, GridBagConstraints c, String label, JComponent campo, int row) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(Color.DARK_GRAY);
        c.gridx = 0;
        c.gridy = row;
        c.insets = new Insets(8, 10, 8, 10);
        panel.add(lbl, c);
        
        if (campo instanceof JTextField || campo instanceof JPasswordField) {
            campo.setPreferredSize(new Dimension(300, 38));
            campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            campo.setFont(new Font("Arial", Font.PLAIN, 13));
        } else if (campo instanceof JComboBox) {
            campo.setPreferredSize(new Dimension(300, 38));
            ((JComboBox<?>) campo).setBackground(Color.WHITE);
            ((JComboBox<?>) campo).setBorder(BorderFactory.createLineBorder(GRIS_BORDE, 1));
            ((JComboBox<?>) campo).setFont(new Font("Arial", Font.PLAIN, 13));
        }
        
        c.gridx = 1;
        c.gridy = row;
        panel.add(campo, c);
    }
    
    private void addSeparador(JPanel panel, GridBagConstraints c, int row) {
        JSeparator separator = new JSeparator();
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        c.insets = new Insets(15, 0, 15, 0);
        panel.add(separator, c);
        c.gridwidth = 1;
        c.insets = new Insets(8, 10, 8, 10);
    }
    
    private void registrarCiudadano() {
        if (!validarCampos()) {
            return;
        }
        
        Connection con = null;
        try {
            con = Conexion_DB.obtenerConexion();
            con.setAutoCommit(false);
            
            // 1. Insertar en ciudadanos
            String sqlCiudadano = "INSERT INTO ciudadanos (nombre, apellido, curp, fecha_nacimiento, genero, "
                                + "telefono, calle, numero_exterior, numero_interior, colonia, municipio, estado, codigo_postal) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement psCiudadano = con.prepareStatement(sqlCiudadano, Statement.RETURN_GENERATED_KEYS);
            psCiudadano.setString(1, txtNombre.getText().trim());
            psCiudadano.setString(2, txtApellido.getText().trim());
            psCiudadano.setString(3, txtCURP.getText().trim().toUpperCase());
            
            java.util.Date fechaUtil = fechaNacimiento.getDate();
            java.sql.Date fechaSQL = new java.sql.Date(fechaUtil.getTime());
            psCiudadano.setDate(4, fechaSQL);
            
            psCiudadano.setString(5, (String) cbGenero.getSelectedItem());
            psCiudadano.setString(6, txtTelefono.getText().trim());
            psCiudadano.setString(7, txtCalle.getText().trim());
            psCiudadano.setString(8, txtNumeroExterior.getText().trim());
            
            String numInterior = txtNumeroInterior.getText().trim();
            psCiudadano.setString(9, numInterior.isEmpty() ? null : numInterior);
            
            psCiudadano.setString(10, txtColonia.getText().trim());
            psCiudadano.setString(11, txtMunicipio.getText().trim());
            psCiudadano.setString(12, txtEstado.getText().trim());
            psCiudadano.setString(13, txtCodigoPostal.getText().trim());
            
            int affectedRows = psCiudadano.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se pudo insertar el ciudadano");
            }
            
            ResultSet generatedKeys = psCiudadano.getGeneratedKeys();
            int idCiudadano;
            if (generatedKeys.next()) {
                idCiudadano = generatedKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID del ciudadano");
            }
            
            // 2. Insertar en usuarios_ciudadanos
            String sqlUsuario = "INSERT INTO usuarios_ciudadanos (correo, password_, id_ciudadano) VALUES (?, ?, ?)";
            PreparedStatement psUsuario = con.prepareStatement(sqlUsuario);
            psUsuario.setString(1, txtCorreo.getText().trim());
            psUsuario.setString(2, new String(txtPassword.getPassword()));
            psUsuario.setInt(3, idCiudadano);
            
            psUsuario.executeUpdate();
            
            con.commit();
            
            JOptionPane.showMessageDialog(this,
                "✅ ¡REGISTRO EXITOSO!\n\nYa puedes iniciar sesión con tu correo y contraseña.",
                "Registro Completo",
                JOptionPane.INFORMATION_MESSAGE);
            
            new Ventana_login().setVisible(true);
            this.dispose();
            
        } catch (SQLException ex) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            if (ex.getMessage().contains("Duplicate entry") && ex.getMessage().contains("curp")) {
                lblMensaje.setText("Error: La CURP ya está registrada");
            } else if (ex.getMessage().contains("Duplicate entry") && ex.getMessage().contains("correo")) {
                lblMensaje.setText(" Error: El correo electrónico ya está registrado");
            } else {
                lblMensaje.setText("Error al registrar: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    private boolean validarCampos() {
        // Datos personales
        if (txtNombre.getText().trim().isEmpty()) {
            lblMensaje.setText("El nombre es obligatorio");
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtApellido.getText().trim().isEmpty()) {
            lblMensaje.setText("El apellido es obligatorio");
            txtApellido.requestFocus();
            return false;
        }
        
        if (txtCURP.getText().trim().isEmpty()) {
            lblMensaje.setText("La CURP es obligatoria");
            txtCURP.requestFocus();
            return false;
        }
        
        if (txtCURP.getText().trim().length() != 18) {
            lblMensaje.setText("La CURP debe tener 18 caracteres");
            txtCURP.requestFocus();
            return false;
        }
        
        if (fechaNacimiento.getDate() == null) {
            lblMensaje.setText("Debe seleccionar una fecha de nacimiento");
            fechaNacimiento.requestFocus();
            return false;
        }
        
        if (txtTelefono.getText().trim().isEmpty()) {
            lblMensaje.setText("El teléfono es obligatorio");
            txtTelefono.requestFocus();
            return false;
        }
        
        // Dirección
        if (txtCalle.getText().trim().isEmpty()) {
            lblMensaje.setText("La calle es obligatoria");
            txtCalle.requestFocus();
            return false;
        }
        
        if (txtNumeroExterior.getText().trim().isEmpty()) {
            lblMensaje.setText("El número exterior es obligatorio");
            txtNumeroExterior.requestFocus();
            return false;
        }
        
        if (txtColonia.getText().trim().isEmpty()) {
            lblMensaje.setText("La colonia es obligatoria");
            txtColonia.requestFocus();
            return false;
        }
        
        if (txtMunicipio.getText().trim().isEmpty()) {
            lblMensaje.setText("El municipio es obligatorio");
            txtMunicipio.requestFocus();
            return false;
        }
        
        if (txtEstado.getText().trim().isEmpty()) {
            lblMensaje.setText("El estado es obligatorio");
            txtEstado.requestFocus();
            return false;
        }
        
        if (txtCodigoPostal.getText().trim().isEmpty()) {
            lblMensaje.setText("El código postal es obligatorio");
            txtCodigoPostal.requestFocus();
            return false;
        }
        
        if (txtCodigoPostal.getText().trim().length() != 5) {
            lblMensaje.setText("El código postal debe tener 5 dígitos");
            txtCodigoPostal.requestFocus();
            return false;
        }
        
        // Datos de cuenta
        String correo = txtCorreo.getText().trim();
        if (correo.isEmpty()) {
            lblMensaje.setText("El correo electrónico es obligatorio");
            txtCorreo.requestFocus();
            return false;
        }
        
        if (!correo.contains("@") || !correo.contains(".")) {
            lblMensaje.setText("Ingrese un correo electrónico válido");
            txtCorreo.requestFocus();
            return false;
        }
        
        String password = new String(txtPassword.getPassword());
        String confirmar = new String(txtConfirmarPassword.getPassword());
        
        if (password.isEmpty()) {
            lblMensaje.setText("La contraseña es obligatoria");
            txtPassword.requestFocus();
            return false;
        }
        
        if (password.length() < 6) {
            lblMensaje.setText("La contraseña debe tener al menos 6 caracteres");
            txtPassword.requestFocus();
            return false;
        }
        
        if (!password.equals(confirmar)) {
            lblMensaje.setText("Las contraseñas no coinciden");
            txtConfirmarPassword.requestFocus();
            return false;
        }
        
        lblMensaje.setText(" ");
        return true;
    }
    
    private void cancelarRegistro() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea cancelar el registro?\nLos datos no serán guardados.",
            "Cancelar Registro",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            new Ventana_login().setVisible(true);
            this.dispose();
        }
    }
    
    private JPanel crearBarraInferior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        barra.setBackground(new Color(228, 228, 228));
        barra.setPreferredSize(new Dimension(getWidth(), 36));
        JLabel pie = new JLabel("© 2026 Gobierno de México  |  Sistema Nacional de Vacunación");
        pie.setFont(new Font("Arial", Font.PLAIN, 10));
        pie.setForeground(new Color(130, 130, 130));
        barra.add(pie);
        return barra;
    }
    
    // Clase para limitar caracteres
    class JTextFieldLimit extends javax.swing.text.PlainDocument {
        private int limit;
        
        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }
        
        public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
            if (str == null) return;
            
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }
}