package sector_salud;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;

/*
 * Ventana principal de inicio de sesión para trabajadores del Sector Salud.*/
public class Ventana_login extends JFrame {

    // ── Componentes de la interfaz ──
    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private JButton btnOjo;          // botón para mostrar/ocultar contraseña
    private JButton btnLogin;
    private JLabel lblMensaje;       // para mostrar errores en pantalla
    private boolean passwordVisible = false;

    //Paleta de colores del Gobierno de México
    private static final Color GUINDO       = new Color(109, 27, 46);   // barra superior
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);   // botón al pasar el mouse
    private static final Color ORO          = new Color(193, 154, 80);  // detalle dorado
    private static final Color FONDO        = new Color(245, 245, 248); // fondo general
    private static final Color GRIS_BORDE   = new Color(200, 200, 200); // bordes de campos

    public Ventana_login() {
        configurarVentana();
        construirUI();
        setIconoVentana(); 
    }

    /*Configura propiedades básicas de la ventana*/
    private void configurarVentana() {
        setTitle("Sistema Nacional de Vacunación — Sector Salud");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa maximizada
        setMinimumSize(new Dimension(600, 500)); // tamaño mínimo si el usuario la reduce
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(FONDO);
        setLayout(new BorderLayout());
    }

    /* Construye y ensambla todos los paneles. */
    private void construirUI() {
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearPanelFormulario(), BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);
    }
    private void setIconoVentana() {
        // Método 1: Desde carpeta recursos
        URL url = getClass().getResource("/recursos/logo_sector.png");
        if (url != null) {
            ImageIcon icono = new ImageIcon(url);
            setIconImage(icono.getImage());
        } else {
            // Método 2: Desde ruta absoluta como fallback
            try {
                Image icono = Toolkit.getDefaultToolkit().getImage("logo_sector.png");
                setIconImage(icono);
            } catch (Exception e) {
                System.err.println("No se pudo cargar el ícono");
            }
        }
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 8));
        barra.setBackground(GUINDO);
        barra.setPreferredSize(new Dimension(getWidth(), 85)); //IMPORTANTE PARA EL TAMAÑO DE LA BARRA

        // Imagen del águila sin distorsionar, tamaño natural ajustado por alto
        JLabel lblIcono = new JLabel();
        try {
            ImageIcon icono = new ImageIcon(
                getClass().getClassLoader().getResource("recursos/logo_gob_mx.png")
            );
            // Escala proporcional: fijamos el alto en 52px y calculamos el ancho
            Image imgOriginal = icono.getImage();
            int altoDeseado = 65; //IMPORTANTE PARA EL TAMAÑO DE LA IMAGEN DE LA BARRA
            int anchoOriginal = icono.getIconWidth();
            int altoOriginal  = icono.getIconHeight();
            int anchoEscalado = (int)((double) anchoOriginal / altoOriginal * altoDeseado);
            Image imgEscalada = imgOriginal.getScaledInstance(anchoEscalado, altoDeseado, Image.SCALE_SMOOTH);
            lblIcono.setIcon(new ImageIcon(imgEscalada));
        } catch (Exception e) {
            lblIcono.setText("◉");
            lblIcono.setFont(new Font("Arial", Font.BOLD, 28));
            lblIcono.setForeground(ORO);
        }

        // Separador vertical dorado
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 44));
        sep.setForeground(ORO);

        // Texto del sistema (sin "Gobierno de México" porque la imagen ya lo tiene)
        JLabel lblSistema = new JLabel("  Sistema Nacional de Vacunación");
        lblSistema.setForeground(new Color(230, 210, 170));
        lblSistema.setFont(new Font("Arial", Font.PLAIN, 13));

        barra.add(lblIcono);
        barra.add(sep);
        barra.add(lblSistema);

        return barra;
    }

    private JPanel crearPanelFormulario() {
        // Panel externo: centra la "tarjeta" horizontal y verticalmente
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(FONDO);

        // Tarjeta blanca con sombra simulada
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 215), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));
        // Ancho fijo de 400px — los campos ya no se estiran a pantalla completa
        tarjeta.setPreferredSize(new Dimension(400, 370));
        tarjeta.setMaximumSize(new Dimension(400, 370));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridwidth = 1;

        JLabel lblTitulo = new JLabel("Iniciar Sesión");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(GUINDO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0; c.gridy = 0;
        c.insets = new Insets(0, 0, 4, 0);
        tarjeta.add(lblTitulo, c);

        JLabel lblSub = new JLabel("Acceso exclusivo para personal autorizado");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 11));
        lblSub.setForeground(Color.GRAY);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 1;
        c.insets = new Insets(0, 0, 22, 0);
        tarjeta.add(lblSub, c);

        //Etiqueta correo
        JLabel lblCorreo = new JLabel("Correo institucional");
        lblCorreo.setFont(new Font("Arial", Font.BOLD, 12));
        c.gridy = 2;
        c.insets = new Insets(0, 0, 4, 0);
        tarjeta.add(lblCorreo, c);

        //Campo correo
        txtCorreo = new JTextField();
        estilizarCampo(txtCorreo);
        c.gridy = 3;
        c.insets = new Insets(0, 0, 14, 0);
        tarjeta.add(txtCorreo, c);

        //Etiqueta contraseña
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Arial", Font.BOLD, 12));
        c.gridy = 4;
        c.insets = new Insets(0, 0, 4, 0);
        tarjeta.add(lblPass, c);

        //Panel contraseña (campo + ojo)
        tarjeta.add(crearPanelPassword(), colocarEn(c, 5, new Insets(0, 0, 8, 0)));

        //Etiqueta de mensajes de error
        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(new Color(180, 20, 20));
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 11));
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        tarjeta.add(lblMensaje, colocarEn(c, 6, new Insets(0, 0, 4, 0)));

        //Botón Iniciar Sesión
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
        tarjeta.add(btnLogin, colocarEn(c, 7, new Insets(4, 0, 0, 0)));

        // Enter en contraseña dispara el login
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) validarLogin();
            }
        });

        // Centrar la tarjeta en el contenedor
        contenedor.add(tarjeta, new GridBagConstraints());
        return contenedor;
    }

        /*Crea el panel que contiene el campo de contraseña y el botón ojo. */
        private JPanel crearPanelPassword() {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setBackground(Color.WHITE);

        txtPassword = new JPasswordField();
        estilizarCampo(txtPassword);

        // Cargar imágenes de ojo
        ImageIcon iconoAbierto  = cargarIconoOjo("recursos/ojo_abierto.png");
        ImageIcon iconoCerrado  = cargarIconoOjo("recursos/ojo_cerrado.png");

        // Botón ojo con imagen
        btnOjo = new JButton(iconoCerrado); // empieza con ojo cerrado (contraseña oculta)
        btnOjo.setPreferredSize(new Dimension(48, 42));
        btnOjo.setBackground(new Color(245, 245, 245));
        btnOjo.setFocusPainted(false);
        btnOjo.setBorder(BorderFactory.createLineBorder(GRIS_BORDE));
        btnOjo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOjo.setToolTipText("Mostrar contraseña");

        btnOjo.addActionListener(e -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                txtPassword.setEchoChar((char) 0);
                btnOjo.setIcon(iconoAbierto);
                btnOjo.setToolTipText("Ocultar contraseña");
            } else {
                txtPassword.setEchoChar('●'); //Simbolo para ocultar la contraseña
                btnOjo.setIcon(iconoCerrado);
                btnOjo.setToolTipText("Mostrar contraseña");
            }
            txtPassword.requestFocus();
        });

        panel.add(txtPassword, BorderLayout.CENTER);
        panel.add(btnOjo, BorderLayout.EAST);
        return panel;
    }

    /* Carga y escala un ícono de ojo desde recursos */
    private ImageIcon cargarIconoOjo(String ruta) {
        try {
            ImageIcon icono = new ImageIcon(getClass().getClassLoader().getResource(ruta));
            Image img = icono.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            // Si no carga la imagen, usa texto de respaldo
            return null;
        }
    }

    //  BARRA INFERIOR
    private JPanel crearBarraInferior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        barra.setBackground(new Color(228, 228, 228));
        barra.setPreferredSize(new Dimension(480, 36));

        JLabel pie = new JLabel("© 2026 Gobierno de México  |  Sistema Nacional de Vacunación");
        pie.setFont(new Font("Arial", Font.PLAIN, 10));
        pie.setForeground(new Color(130, 130, 130));
        barra.add(pie);
        return barra;
    }
    
    
    private void validarLogin() {
        String correo   = txtCorreo.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // 1. Campos vacíos
        if (correo.isEmpty()) {
            mostrarError("Por favor ingrese su correo institucional.");
            txtCorreo.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            mostrarError("Por favor ingrese su contraseña.");
            txtPassword.requestFocus();
            return;
        }

        // 2. Formato básico de correo (@gob.mx)
        if (!correo.toLowerCase().endsWith("@gob.mx")) {
            mostrarError("El correo debe ser institucional (terminar en @gob.mx).");
            txtCorreo.requestFocus();
            return;
        }

        // 3. Consulta a la base de datos
        // ✅ Ahora
        if (autenticar(correo, password)) 
        {
            new Panel_Principal(correo).setVisible(true);
            this.dispose();
        }
        else 
        {
            mostrarError("Correo o contraseña incorrectos.");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    
    /* Consulta la tabla 'usuarios' */
    private boolean autenticar(String correo, String password) {
        String sql = "SELECT correo FROM usuarios WHERE correo = ? AND password_ = ?";
        try (Connection con = Conexion_DB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true si encontró al menos 1 registro

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo conectar a la base de datos.\n" +
                "Verifica que MySQL esté activo y revisa ConexionDB.java\n\n" +
                "Detalle: " + ex.getMessage(),
                "Error de conexión",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
    }

    /*Aplica el estilo visual uniforme a los campos de texto */
    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setPreferredSize(new Dimension(0, 42));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_BORDE),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        campo.setBackground(Color.WHITE);
    }

    /*Método auxiliar para posicionar componentes en GridBagLayout de forma concisa */
    private GridBagConstraints colocarEn(GridBagConstraints c, int fila, Insets insets) {
        c.gridy = fila;
        c.insets = insets;
        return c;
    }

}
