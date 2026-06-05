package app_ciudadanos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;

public class Ventana_login extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private JButton btnOjo;
    private JButton btnLogin;
    private JButton btnRegistrarse;  // ← Nuevo botón
    private JLabel lblMensaje;
    private boolean passwordVisible = false;

    private static final Color GUINDO       = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color ORO          = new Color(193, 154, 80);
    private static final Color FONDO        = new Color(245, 245, 248);
    private static final Color GRIS_BORDE   = new Color(200, 200, 200);

    public Ventana_login() {
        configurarVentana();
        construirUI();
        setIconoVentana();
    }

    private void configurarVentana() {
        setTitle("Sistema Nacional de Vacunación — Ciudadanos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(FONDO);
        setLayout(new BorderLayout());
    }

    private void construirUI() {
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);
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

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        barra.setBackground(GUINDO);

        JLabel lblIcono = new JLabel();
        try {
            ImageIcon icono = new ImageIcon(
                getClass().getClassLoader().getResource("Recursos/logo_gob_mx.png")
            );
            int altoDeseado   = 65;
            int anchoEscalado = (int)((double) icono.getIconWidth() / icono.getIconHeight() * altoDeseado);
            Image img = icono.getImage().getScaledInstance(anchoEscalado, altoDeseado, Image.SCALE_SMOOTH);
            lblIcono.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblIcono.setText("◉");
            lblIcono.setFont(new Font("Arial", Font.BOLD, 28));
            lblIcono.setForeground(ORO);
        }

        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 54));
        sep.setForeground(ORO);

        JLabel lblSistema = new JLabel("  Sistema Nacional de Vacunación — Ciudadanos");
        lblSistema.setForeground(new Color(230, 210, 170));
        lblSistema.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel contenedorBarra = new JPanel(new BorderLayout());
        contenedorBarra.setBackground(GUINDO);
        contenedorBarra.setPreferredSize(new Dimension(getWidth(), 85));

        barra.add(lblIcono);
        barra.add(sep);
        barra.add(lblSistema);
        contenedorBarra.add(barra, BorderLayout.WEST);

        return contenedorBarra;
    }

    private JPanel crearPanelFormulario() {
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(FONDO);

        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 215), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));
        tarjeta.setPreferredSize(new Dimension(400, 420)); // ← Aumentado para el botón de registro

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        JLabel lblTitulo = new JLabel("Iniciar Sesión");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(GUINDO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0; c.gridy = 0;
        c.insets = new Insets(0, 0, 4, 0);
        tarjeta.add(lblTitulo, c);

        JLabel lblSub = new JLabel("Acceso para ciudadanos registrados");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 11));
        lblSub.setForeground(Color.GRAY);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 1;
        c.insets = new Insets(0, 0, 22, 0);
        tarjeta.add(lblSub, c);

        JLabel lblCorreo = new JLabel("Correo electrónico");
        lblCorreo.setFont(new Font("Arial", Font.BOLD, 12));
        c.gridy = 2;
        c.insets = new Insets(0, 0, 4, 0);
        tarjeta.add(lblCorreo, c);

        txtCorreo = new JTextField();
        estilizarCampo(txtCorreo);
        c.gridy = 3;
        c.insets = new Insets(0, 0, 14, 0);
        tarjeta.add(txtCorreo, c);

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Arial", Font.BOLD, 12));
        c.gridy = 4;
        c.insets = new Insets(0, 0, 4, 0);
        tarjeta.add(lblPass, c);

        tarjeta.add(crearPanelPassword(), colocarEn(c, 5, new Insets(0, 0, 8, 0)));

        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(new Color(180, 20, 20));
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 11));
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        tarjeta.add(lblMensaje, colocarEn(c, 6, new Insets(0, 0, 4, 0)));

        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(GUINDO);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> validarLogin());
        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogin.setBackground(GUINDO_HOVER); }
            public void mouseExited(MouseEvent e)  { btnLogin.setBackground(GUINDO); }
        });
        tarjeta.add(btnLogin, colocarEn(c, 7, new Insets(4, 0, 15, 0)));

        // ← Botón de registrarse
        btnRegistrarse = new JButton("¿No tienes cuenta? Regístrate");
        btnRegistrarse.setBackground(new Color(240, 240, 240));
        btnRegistrarse.setForeground(GUINDO);
        btnRegistrarse.setFont(new Font("Arial", Font.BOLD, 12));
        btnRegistrarse.setPreferredSize(new Dimension(0, 35));
        btnRegistrarse.setFocusPainted(false);
        btnRegistrarse.setBorder(BorderFactory.createLineBorder(GRIS_BORDE));
        btnRegistrarse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrarse.addActionListener(e -> abrirVentanaRegistro());
        btnRegistrarse.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { 
                btnRegistrarse.setBackground(new Color(230, 230, 230));
            }
            public void mouseExited(MouseEvent e) { 
                btnRegistrarse.setBackground(new Color(240, 240, 240));
            }
        });
        tarjeta.add(btnRegistrarse, colocarEn(c, 8, new Insets(0, 0, 0, 0)));

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) validarLogin();
            }
        });

        contenedor.add(tarjeta, new GridBagConstraints());
        return contenedor;
    }

    // ← Método para abrir la ventana de registro
    private void abrirVentanaRegistro() {
        new Ventana_Registro().setVisible(true);
        this.dispose();
    }

    private JPanel crearPanelPassword() {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setBackground(Color.WHITE);

        txtPassword = new JPasswordField();
        estilizarCampo(txtPassword);

        ImageIcon iconoAbierto = cargarIconoOjo("Recursos/ojo_abierto.png");
        ImageIcon iconoCerrado = cargarIconoOjo("Recursos/ojo_cerrado.png");

        btnOjo = new JButton(iconoCerrado);
        btnOjo.setPreferredSize(new Dimension(48, 42));
        btnOjo.setBackground(new Color(245, 245, 245));
        btnOjo.setFocusPainted(false);
        
        btnOjo.setBorder(BorderFactory.createLineBorder(GRIS_BORDE));
        btnOjo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnOjo.addActionListener(e -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                txtPassword.setEchoChar((char) 0);
                btnOjo.setIcon(iconoAbierto);
            } else {
                txtPassword.setEchoChar('●');
                btnOjo.setIcon(iconoCerrado);
            }
            txtPassword.requestFocus();
        });

        panel.add(txtPassword, BorderLayout.CENTER);
        panel.add(btnOjo, BorderLayout.EAST);
        return panel;
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

    private void validarLogin() {
        String correo   = txtCorreo.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (correo.isEmpty()) {
            lblMensaje.setText("Por favor ingrese su correo.");
            txtCorreo.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            lblMensaje.setText("Por favor ingrese su contraseña.");
            txtPassword.requestFocus();
            return;
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            lblMensaje.setText("Ingrese un correo electrónico válido.");
            txtCorreo.requestFocus();
            return;
        }

        if (autenticar(correo, password)) {
            new Panel_Principal(correo).setVisible(true);
            this.dispose();
        } else {
            lblMensaje.setText("Correo o contraseña incorrectos.");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    private boolean autenticar(String correo, String password) {
        String sql = "SELECT correo FROM usuarios_ciudadanos WHERE correo = ? AND password_ = ?";
        try (Connection con = Conexion_DB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo conectar a la base de datos.\n" + ex.getMessage(),
                "Error de conexión", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private ImageIcon cargarIconoOjo(String ruta) {
        try {
            ImageIcon icono = new ImageIcon(getClass().getClassLoader().getResource(ruta));
            return new ImageIcon(icono.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH));
        } catch (Exception e) { return null; }
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setPreferredSize(new Dimension(0, 42));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BORDE),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        campo.setBackground(Color.WHITE);
    }

    private GridBagConstraints colocarEn(GridBagConstraints c, int fila, Insets insets) {
        c.gridy = fila;
        c.insets = insets;
        return c;
    }  
}