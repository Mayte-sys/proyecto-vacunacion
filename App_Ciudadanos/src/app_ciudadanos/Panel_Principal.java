    package app_ciudadanos;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Panel_Principal extends JFrame {

    private static final Color GUINDO       = new Color(109, 27, 46);
    private static final Color ORO          = new Color(193, 154, 80);
    private static final Color FONDO        = new Color(245, 245, 248);
    private static final Color PANEL_BG     = new Color(250, 250, 252);
    private static final Color BORDE_PANEL  = new Color(200, 200, 210);
    private static final Color COLOR_BROTE  = new Color(180, 30, 30);
    private static final Color COLOR_ALERTA = new Color(200, 120, 0);
    private static final Color COLOR_INFO   = new Color(30, 100, 170);

    private final String correoUsuario;
    private int idCiudadano = -1;
    private String nombreCiudadano;
    private String curpCiudadano;
    private String estadoCiudadano;
    private CardLayout cardLayout;
    private JPanel contenedor;
    private BotonModulo botonModulo2;
    
    // Componentes de notificaciones
    private JLabel badgeCampana;
    private JPanel panelLateralUsuario;
    private JPanel panelLateralNotificaciones;
    private JPanel panelNotificacionesLista;

    public Panel_Principal(String correo) {
        this.correoUsuario = correo;
        this.idCiudadano   = obtenerIdCiudadano(correo);
        cargarDatosCiudadano();
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

        // Panel central con contenido
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(FONDO);
        panelCentral.add(crearAreaContenido(), BorderLayout.CENTER);

        // Paneles laterales
        panelLateralUsuario = crearPanelUsuario();
        panelLateralNotificaciones = crearPanelNotificaciones();
        panelLateralUsuario.setVisible(false);
        panelLateralNotificaciones.setVisible(false);

        // Contenedor principal con BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(FONDO);
        mainContainer.add(panelCentral, BorderLayout.CENTER);

        // Usar un panel contenedor para los laterales (se posicionan sobre el contenido)
        JPanel lateralContainer = new JPanel(null); // layout null para posicionamiento absoluto
        lateralContainer.setOpaque(false);
        lateralContainer.add(panelLateralUsuario);
        lateralContainer.add(panelLateralNotificaciones);

        // Usar GlassPane para los paneles laterales (más fácil)
        setGlassPane(lateralContainer);
        getGlassPane().setVisible(true);

        add(mainContainer, BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);

        // Ajustar tamaños
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ajustarPaneles();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                ajustarPaneles();
            }
        });
    }

    private void ajustarPaneles() {
        int width = getWidth();
        int height = getHeight() - 85 - 36;
        int panelWidth = 320;
        int panelX = width - panelWidth;
        int panelY = 85;

        panelLateralUsuario.setBounds(panelX, panelY, panelWidth, height);
        panelLateralNotificaciones.setBounds(panelX, panelY, panelWidth, height);

        getGlassPane().revalidate();
        getGlassPane().repaint();
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

    // ══════════════════════════════════════════════════
    //  BARRA SUPERIOR
    // ══════════════════════════════════════════════════
    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(GUINDO);
        barra.setBorder(new EmptyBorder(8, 15, 8, 15));

        // Parte izquierda
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 5));
        izquierda.setBackground(GUINDO);

        JLabel lblIcono = new JLabel();
        try {
            ImageIcon icono = new ImageIcon(
                getClass().getClassLoader().getResource("Recursos/logo_gob_mx.png")
            );
            int altoDeseado = 55;
            int anchoEscalado = (int)((double) icono.getIconWidth() / icono.getIconHeight() * altoDeseado);
            lblIcono.setIcon(new ImageIcon(
                icono.getImage().getScaledInstance(anchoEscalado, altoDeseado, Image.SCALE_SMOOTH)
            ));
        } catch (Exception e) {
            lblIcono.setText("◉");
            lblIcono.setFont(new Font("Arial", Font.BOLD, 28));
            lblIcono.setForeground(ORO);
        }

        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 44));
        sep.setForeground(ORO);

        JLabel lblSistema = new JLabel("  Sistema Nacional de Vacunación — Ciudadanos");
        lblSistema.setForeground(new Color(230, 210, 170));
        lblSistema.setFont(new Font("Arial", Font.PLAIN, 13));

        izquierda.add(lblIcono);
        izquierda.add(sep);
        izquierda.add(lblSistema);

        // Parte derecha: botones
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        derecha.setBackground(GUINDO);

        // Botón usuario
        JButton btnUsuario = crearBotonIcono("👤", "Mi perfil");
        btnUsuario.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        btnUsuario.addActionListener(e -> togglePanel(panelLateralUsuario, panelLateralNotificaciones));
        
        // Botón notificaciones con badge
        JLayeredPane lpCampana = new JLayeredPane();
        lpCampana.setPreferredSize(new Dimension(52, 44));
        lpCampana.setOpaque(false);
        
        JButton btnCampana = crearBotonCampana();
        badgeCampana = crearBadge();
        
        btnCampana.addActionListener(e -> {
            togglePanel(panelLateralNotificaciones, panelLateralUsuario);
            cargarNotificacionesEnPanel();
            marcarTodasNotificacionesLeidas();
            actualizarBadges();
        });
        
        lpCampana.add(btnCampana, JLayeredPane.DEFAULT_LAYER);
        lpCampana.add(badgeCampana, JLayeredPane.PALETTE_LAYER);

        derecha.add(btnUsuario);
        derecha.add(lpCampana);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);

        return barra;
    }
    
    private JButton crearBotonIcono(String texto, String tooltip) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        btn.setForeground(new Color(230, 210, 170));
        btn.setBackground(GUINDO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(tooltip);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { btn.setForeground(new Color(230, 210, 170)); }
        });
        return btn;
    }
    
    private JButton crearBotonCampana() {
        JButton btn = new JButton() {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color col = hover ? Color.WHITE : new Color(230, 210, 170);
                g2.setColor(col);
                g2.setStroke(new BasicStroke(hover ? 2.6f : 2.2f));
                
                int cx = getWidth()/2, cy = getHeight()/2 - 1;
                int bw = 20, bh = 18;
                
                g2.drawArc(cx - bw/2, cy - bh, bw, bh*2, 0, 180);
                g2.drawLine(cx - bw/2, cy, cx - bw/2 - 3, cy + 7);
                g2.drawLine(cx + bw/2, cy, cx + bw/2 + 3, cy + 7);
                g2.drawLine(cx - bw/2 - 3, cy + 7, cx + bw/2 + 3, cy + 7);
                g2.fillOval(cx - 3, cy + 7, 7, 7);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawArc(cx - 4, cy - bh - 5, 8, 7, 0, -180);
                
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Notificaciones");
        btn.setBounds(4, 2, 40, 40);
        return btn;
    }
    
    private JLabel crearBadge() {
        JLabel badge = new JLabel("0") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(210, 0, 0));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Arial", Font.BOLD, 9));
        badge.setForeground(Color.WHITE);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setVerticalAlignment(SwingConstants.CENTER);
        badge.setOpaque(false);
        badge.setBounds(27, 0, 19, 15);
        badge.setVisible(false);
        return badge;
    }
    
    // ══════════════════════════════════════════════════
    //  PANEL LATERAL - USUARIO
    // ══════════════════════════════════════════════════
    private JPanel crearPanelUsuario() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, BORDE_PANEL),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Cabecera
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        
        JLabel lblTitulo = new JLabel("Mi Perfil");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(GUINDO);
        
        JButton btnCerrar = new JButton("X");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.setForeground(Color.GRAY);
        btnCerrar.setBackground(PANEL_BG);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> ocultarPanel(panelLateralUsuario));
        
        header.add(lblTitulo, BorderLayout.WEST);
        header.add(btnCerrar, BorderLayout.EAST);
        
        // Contenido
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(PANEL_BG);
        contenido.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel lblNombre = new JLabel(nombreCiudadano != null ? nombreCiudadano : "Usuario");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblCURP = new JLabel("CURP: " + (curpCiudadano != null ? curpCiudadano : "No disponible"));
        lblCURP.setFont(new Font("Arial", Font.PLAIN, 11));
        lblCURP.setForeground(Color.GRAY);
        lblCURP.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblCorreo = new JLabel(correoUsuario);
        lblCorreo.setFont(new Font("Arial", Font.PLAIN, 11));
        lblCorreo.setForeground(Color.GRAY);
        lblCorreo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JSeparator separador = new JSeparator();
        separador.setMaximumSize(new Dimension(280, 2));
        
        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        btnCerrarSesion.setBackground(GUINDO);
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFont(new Font("Arial", Font.BOLD, 12));
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.setMaximumSize(new Dimension(200, 35));
        btnCerrarSesion.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCerrarSesion.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnCerrarSesion.setBackground(new Color(140, 40, 62)); }
            public void mouseExited(MouseEvent e) { btnCerrarSesion.setBackground(GUINDO); }
        });
        btnCerrarSesion.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro que deseas cerrar sesión?",
                "Cerrar sesión", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                new Ventana_login().setVisible(true);
                dispose();
            }
        });
        
        contenido.add(lblNombre);
        contenido.add(Box.createVerticalStrut(5));
        contenido.add(lblCURP);
        contenido.add(Box.createVerticalStrut(2));
        contenido.add(Box.createVerticalStrut(2));
        contenido.add(lblCorreo);
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(separador);
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(btnCerrarSesion);
        contenido.add(Box.createVerticalGlue());
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(contenido), BorderLayout.CENTER);
        
        return panel;
    }
    
    // ══════════════════════════════════════════════════
    //  PANEL LATERAL - NOTIFICACIONES
    // ══════════════════════════════════════════════════
    private JPanel crearPanelNotificaciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, BORDE_PANEL),
            new EmptyBorder(20, 0, 20, 0)
        ));
        
        // Cabecera
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        
        JLabel lblTitulo = new JLabel("Notificaciones");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(GUINDO);
        
        JButton btnCerrar = new JButton("X");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.setForeground(Color.GRAY);
        btnCerrar.setBackground(PANEL_BG);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> ocultarPanel(panelLateralNotificaciones));
        
        header.add(lblTitulo, BorderLayout.WEST);
        header.add(btnCerrar, BorderLayout.EAST);
        
        // Lista de notificaciones
        panelNotificacionesLista = new JPanel();
        panelNotificacionesLista.setLayout(new BoxLayout(panelNotificacionesLista, BoxLayout.Y_AXIS));
        panelNotificacionesLista.setBackground(PANEL_BG);
        
        JScrollPane scroll = new JScrollPane(panelNotificacionesLista);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void cargarNotificacionesEnPanel() {
        if (panelNotificacionesLista == null) return;

        panelNotificacionesLista.removeAll();
        List<Object[]> notificaciones = obtenerNotificacionesDesdeBD();

        if (notificaciones.isEmpty()) {
            JLabel lblVacio = new JLabel("No tienes notificaciones");
            lblVacio.setFont(new Font("Arial", Font.ITALIC, 12));
            lblVacio.setForeground(Color.GRAY);
            lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelNotificacionesLista.add(lblVacio);
        } else {
            for (Object[] n : notificaciones) {
                JPanel tarjeta = crearTarjetaNotificacion(
                    (String) n[0],   // titulo
                    (String) n[1],   // mensaje COMPLETO (sin cortar)
                    (String) n[2],   // fecha
                    (String) n[3]    // tipo
                );
                panelNotificacionesLista.add(tarjeta);
                panelNotificacionesLista.add(Box.createVerticalStrut(10));
            }
        }

        panelNotificacionesLista.revalidate();
        panelNotificacionesLista.repaint();
    }
    
    private JPanel crearTarjetaNotificacion(String titulo, String mensaje, String fecha, String tipo) {
        Color colorTipo;
        String tipoTexto;

        switch (tipo != null ? tipo : "") {
            case "Brote":
                colorTipo = COLOR_BROTE;
                tipoTexto = "BROTE";
                break;
            case "Alerta":
                colorTipo = COLOR_ALERTA;
                tipoTexto = "ALERTA";
                break;
            default:
                colorTipo = COLOR_INFO;
                tipoTexto = "INFO";
                break;
        }

        // Panel principal con BoxLayout para altura dinámica
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 1, 1, colorTipo),
            new EmptyBorder(12, 12, 12, 12)
        ));
        tarjeta.setMaximumSize(new Dimension(420, 100));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fila superior: Tipo + Título
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(Color.WHITE);
        header.setOpaque(true);
        header.setMaximumSize(new Dimension(290, 25));

        JLabel lblTipo = new JLabel(tipoTexto);
        lblTipo.setFont(new Font("Arial", Font.BOLD, 10));
        lblTipo.setForeground(colorTipo);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitulo.setForeground(new Color(40, 40, 40));

        header.add(lblTipo, BorderLayout.WEST);
        header.add(lblTitulo, BorderLayout.CENTER);

        // Mensaje COMPLETO - sin cortar, usando HTML para ajuste automático
        JLabel lblMensaje = new JLabel();
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 11));
        lblMensaje.setForeground(new Color(80, 80, 80));
        lblMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Usar HTML para que el texto se ajuste automáticamente
        String mensajeCompleto = "<html><body style='width: 240px; text-align: left;'>" 
                               + mensaje 
                               + "</body></html>";
        lblMensaje.setText(mensajeCompleto);

        // Fecha
        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setFont(new Font("Arial", Font.PLAIN, 9));
        lblFecha.setForeground(new Color(150, 150, 150));
        lblFecha.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(header);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(lblMensaje);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(lblFecha);

        return tarjeta;
    }
    // ══════════════════════════════════════════════════
    //  MÉTODOS DE NOTIFICACIONES (BD)
    // ══════════════════════════════════════════════════
    
    private int contarNotificacionesNoLeidas() {
        String sql = "SELECT COUNT(*) FROM notificaciones n " +
                     "WHERE n.fecha_publicacion <= NOW() " +
                     "AND (FIND_IN_SET(?, n.estados) > 0 OR n.estados = 'Todos' " +
                     "     OR n.estados = '' OR n.estados IS NULL) " +
                     "AND n.id_notificacion NOT IN (" +
                     "  SELECT id_notificacion FROM notificaciones_leidas " +
                     "  WHERE id_ciudadano = ?)";
        try (Connection con = Conexion_DB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estadoCiudadano != null ? estadoCiudadano : "");
            ps.setInt(2, idCiudadano);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return 0;
    }
    
    private List<Object[]> obtenerNotificacionesDesdeBD() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT n.titulo, n.mensaje, " +
                     "DATE_FORMAT(n.fecha_publicacion, '%d/%m/%Y') AS fecha, " +
                     "n.tipo " +
                     "FROM notificaciones n " +
                     "WHERE n.fecha_publicacion <= NOW() " +
                     "AND (FIND_IN_SET(?, n.estados) > 0 OR n.estados = 'Todos' " +
                     "     OR n.estados = '' OR n.estados IS NULL) " +
                     "ORDER BY n.fecha_publicacion DESC";
        
        try (Connection con = Conexion_DB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estadoCiudadano != null ? estadoCiudadano : "");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("titulo"),
                    rs.getString("mensaje"),
                    rs.getString("fecha"),
                    rs.getString("tipo")
                });
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return lista;
    }
    
    private void marcarTodasNotificacionesLeidas() {
        String sql = "INSERT IGNORE INTO notificaciones_leidas (id_ciudadano, id_notificacion) " +
                     "SELECT ?, id_notificacion FROM notificaciones n " +
                     "WHERE (FIND_IN_SET(?, n.estados) > 0 OR n.estados = 'Todos' " +
                     "     OR n.estados = '' OR n.estados IS NULL) " +
                     "AND id_notificacion NOT IN (" +
                     "  SELECT id_notificacion FROM notificaciones_leidas " +
                     "  WHERE id_ciudadano = ?)";
        try (Connection con = Conexion_DB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiudadano);
            ps.setString(2, estadoCiudadano != null ? estadoCiudadano : "");
            ps.setInt(3, idCiudadano);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    private void actualizarBadges() {
        int noLeidas = contarNotificacionesNoLeidas();
        
        // Actualizar badge de la campana
        if (badgeCampana != null) {
            if (noLeidas > 0) {
                badgeCampana.setText(noLeidas > 99 ? "99+" : String.valueOf(noLeidas));
                badgeCampana.setVisible(true);
            } else {
                badgeCampana.setVisible(false);
            }
        }
    }
    
    // ══════════════════════════════════════════════════
    //  CONTROL DE PANELES LATERALES
    // ══════════════════════════════════════════════════
    
    private void togglePanel(JPanel panelMostrar, JPanel panelOcultar) {
        if (panelMostrar.isVisible()) {
            ocultarPanel(panelMostrar);
        } else {
            if (panelOcultar.isVisible()) {
                ocultarPanel(panelOcultar);
            }
            mostrarPanel(panelMostrar);
        }
    }
    
    private void mostrarPanel(JPanel panel) {
        panel.setVisible(true);
        ajustarPaneles();
        panel.revalidate();
        panel.repaint();
    }
    
    private void ocultarPanel(JPanel panel) {
        panel.setVisible(false);
    }

    // ══════════════════════════════════════════════════
    //  ÁREA DE CONTENIDO PRINCIPAL
    // ══════════════════════════════════════════════════
    
    private JPanel crearAreaContenido() {
        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);
        contenedor.setBackground(FONDO);

        Modulo2 modulo2 = new Modulo2(cardLayout, contenedor, idCiudadano, this);
        JPanel menu = crearMenuPrincipal();

        contenedor.add(menu, "menu");
        contenedor.add(new Modulo1(cardLayout, contenedor, idCiudadano), "modulo1");
        contenedor.add(modulo2, "modulo2");
        contenedor.add(new Modulo3(cardLayout, contenedor, idCiudadano), "modulo3");
        contenedor.add(new Modulo4(cardLayout, contenedor), "modulo4");

        cardLayout.show(contenedor, "menu");
        actualizarBadges();

        return contenedor;
    }

    private JPanel crearMenuPrincipal() {
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setBackground(FONDO);
        envoltorio.add(crearFranjaLateral(), BorderLayout.WEST);
        envoltorio.add(crearFranjaLateral(), BorderLayout.EAST);

        JPanel centrado = new JPanel(new GridBagLayout());
        centrado.setBackground(FONDO);

        JPanel grid = new JPanel(new GridLayout(2, 2, 28, 28));
        grid.setBackground(FONDO);

        grid.add(new BotonModulo("Historial Vacunación",
            () -> cardLayout.show(contenedor, "modulo1")));

        botonModulo2 = new BotonModulo("Cartilla de Vacunación",
            () -> cardLayout.show(contenedor, "modulo2"));
        grid.add(botonModulo2);

        grid.add(new BotonModulo("Mi Bienestar Certificado",
            () -> cardLayout.show(contenedor, "modulo3")));
        
        grid.add(new BotonModulo("Mapa de Vacunación",
            () -> cardLayout.show(contenedor, "modulo4")));

        centrado.add(grid, new GridBagConstraints());
        envoltorio.add(centrado, BorderLayout.CENTER);
        return envoltorio;
    }

    // ══════════════════════════════════════════════════
    //  MÉTODOS DE APOYO
    // ══════════════════════════════════════════════════
    
    private void cargarDatosCiudadano() {
        String sql = "SELECT c.nombre, c.apellido, c.curp, c.estado " +
                     "FROM ciudadanos c " +
                     "JOIN usuarios_ciudadanos u ON c.id_ciudadano = u.id_ciudadano " +
                     "WHERE u.correo = ?";
        try (Connection con = Conexion_DB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correoUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nombreCiudadano = rs.getString("nombre") + " " + rs.getString("apellido");
                curpCiudadano = rs.getString("curp");
                estadoCiudadano = rs.getString("estado");
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            nombreCiudadano = correoUsuario;
            curpCiudadano = "No disponible";
            estadoCiudadano = null;
        }
    }

    private int obtenerIdCiudadano(String correo) {
        String sql = "SELECT id_ciudadano FROM usuarios_ciudadanos WHERE correo = ?";
        try (Connection con = Conexion_DB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return -1;
    }

    private JPanel crearFranjaLateral() {
        JPanel franja = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2;
                g2.setColor(new Color(109, 27, 46, 60));
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(cx, 40, cx, getHeight() - 40);
                g2.setColor(new Color(109, 27, 46, 90));
                g2.fillOval(cx - 5, 32, 10, 10);
                g2.fillOval(cx - 5, getHeight() - 42, 10, 10);
                g2.dispose();
            }
        };
        franja.setBackground(FONDO);
        franja.setPreferredSize(new Dimension(80, 0));
        return franja;
    }

    private JPanel crearBarraInferior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        barra.setBackground(new Color(228, 228, 228));
        barra.setPreferredSize(new Dimension(getWidth(), 36));
        JLabel pie = new JLabel("© 2026 Gobierno de Mexico  |  Sistema Nacional de Vacunacion");
        pie.setFont(new Font("Arial", Font.PLAIN, 10));
        pie.setForeground(new Color(130, 130, 130));
        barra.add(pie);
        return barra;
    }
}