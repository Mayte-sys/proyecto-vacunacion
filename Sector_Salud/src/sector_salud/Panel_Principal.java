package sector_salud;

import AsignacionVacunas.*;
import Folios.*;
import aplicadorVacunas.*;
import campanasVacunacion.*;
import centros_salud.*;
import esavi.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import notificaciones.altaNotificacion;

public class Panel_Principal extends JFrame {

    private static final Color GUINDO       = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color ORO          = new Color(193, 154, 80);
    private static final Color FONDO        = new Color(245, 245, 248);

    private final String correoEmpleado;
    private String cedula;

    // CardLayout para navegar entre menú y módulos
    private CardLayout cardLayout;
    private JPanel contenedor;

    public Panel_Principal(String correo) {
        this.correoEmpleado = correo;
        this.cedula = obtenerCedula();
        configurarVentana();
        construirUI();
        setIconoVentana(); 
        
    }

    private void configurarVentana() {
        setTitle("Sistema Nacional de Vacunación — Sector Salud");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(FONDO);
        setLayout(new BorderLayout());
    }

    private void construirUI() {
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearAreaContenido(), BorderLayout.CENTER);
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
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        barra.setBackground(GUINDO);
        // Logo y título (izquierda)
        JLabel lblIcono = new JLabel();
        try {
            ImageIcon icono = new ImageIcon(
                getClass().getClassLoader().getResource("Recursos/logo_gob_mx.png")
            );
            int altoDeseado = 65;
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

        JLabel lblSistema = new JLabel("  Sistema Nacional de Vacunación");
        lblSistema.setForeground(new Color(230, 210, 170));
        lblSistema.setFont(new Font("Arial", Font.PLAIN, 13));

        barra.add(lblIcono);
        barra.add(sep);
        barra.add(lblSistema);

        // Panel derecho (usuario + notificaciones)
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 22));
        derecha.setBackground(GUINDO);

        // Botón de usuario con menú desplegable
        JButton btnUsuario = new JButton(correoEmpleado + " ▼");
        btnUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        btnUsuario.setBackground(GUINDO);
        btnUsuario.setForeground(new Color(220, 200, 160));
        btnUsuario.setFocusPainted(false);
        btnUsuario.setBorderPainted(false);
        btnUsuario.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Crear menú emergente
        JPopupMenu menuUsuario = new JPopupMenu();

        // Obtener datos del empleado
        String nombreEmpleado = obtenerNombreEmpleado();
        String cedulaEmpleado = obtenerCedula();

        JMenuItem itemNombre = new JMenuItem("👤 " + (nombreEmpleado != null ? nombreEmpleado : correoEmpleado));
        itemNombre.setEnabled(false);
        itemNombre.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        JMenuItem itemCedula = new JMenuItem("📄 Cédula: " + (cedulaEmpleado != null ? cedulaEmpleado : "No registrada"));
        itemCedula.setEnabled(false);
        itemCedula.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        JSeparator separadorMenu = new JSeparator();

        JMenuItem itemCerrarSesion = new JMenuItem("🚪 Cerrar sesión");
        itemCerrarSesion.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        itemCerrarSesion.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro que deseas cerrar sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (respuesta == JOptionPane.YES_OPTION) {
                new Ventana_login().setVisible(true);
                dispose();
            }
        });

        menuUsuario.add(itemNombre);
        menuUsuario.add(itemCedula);
        menuUsuario.add(separadorMenu);
        menuUsuario.add(itemCerrarSesion);

        btnUsuario.addActionListener(e -> {
            menuUsuario.show(btnUsuario, 0, btnUsuario.getHeight());
        });

        // Botón de notificaciones (campana)
        JButton btnNotificaciones = new JButton("️📢");
        btnNotificaciones.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        btnNotificaciones.setBackground(GUINDO);
        btnNotificaciones.setForeground(new Color(220, 200, 160));
        btnNotificaciones.setFocusPainted(false);
        btnNotificaciones.setBorderPainted(false);
        btnNotificaciones.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNotificaciones.setToolTipText("Notificaciones");

        btnNotificaciones.addActionListener(e -> {
            cardLayout.show(contenedor, "notificacion_menu");
        });

        // Agregar primero el usuario, luego notificaciones (ambos a la derecha)
        derecha.add(btnUsuario);
        derecha.add(btnNotificaciones);

        JPanel contenedorBarra = new JPanel(new BorderLayout());
        contenedorBarra.setBackground(GUINDO);
        contenedorBarra.setPreferredSize(new Dimension(getWidth(), 85));
        contenedorBarra.add(barra, BorderLayout.WEST);
        contenedorBarra.add(derecha, BorderLayout.EAST);

        return contenedorBarra;
    }
    


    // Método auxiliar para obtener nombre del empleado
    private String obtenerNombreEmpleado() {
    String sql = "SELECT e.nombre, e.apellido FROM empleados e " +
                 "JOIN usuarios u ON e.id_empleado = u.id_empleado " +
                 "WHERE u.correo = ?";

    try (java.sql.Connection con = Conexion_DB.obtenerConexion();
         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, correoEmpleado);
        java.sql.ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String nombre = rs.getString("nombre");
            String apellido = rs.getString("apellido");
            
            String nombreCompleto = nombre;
            if (apellido != null && !apellido.isEmpty()) {
                nombreCompleto += " " + apellido;
            }
            
            return nombreCompleto;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
    //  ÁREA DE CONTENIDO — no modificar la estructura,
    private JPanel crearAreaContenido() {
        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);
        contenedor.setBackground(FONDO);

        // Menú principal
        contenedor.add(crearMenuPrincipal(), "menu");

        // Módulos — se desarrolla el contenido en cada clase
        contenedor.add(new Modulo1(cardLayout, contenedor), "modulo1");
        contenedor.add(new Modulo2(cardLayout, contenedor), "modulo2");
        contenedor.add(new Modulo3(cardLayout, contenedor), "modulo3");
        contenedor.add(new Modulo4(cardLayout, contenedor), "modulo4");
        contenedor.add(new Modulo5(cardLayout, contenedor), "modulo5");
        contenedor.add(new Modulo6(cardLayout, contenedor), "modulo6");
        contenedor.add(new Notificaciones(cardLayout, contenedor), "notificacion_menu");
        
        contenedor.add(new Altas(cardLayout, contenedor), "Altas");//folios
        contenedor.add(new Bajas(cardLayout, contenedor), "Bajas");//folios
        contenedor.add(new Modificar (cardLayout, contenedor), "Modificar");//folios
        contenedor.add(new Reportes (cardLayout, contenedor), "Reportes");//folios
        
        contenedor.add(new Altas_Salud(cardLayout, contenedor), "Altas_Salud");//centro salud
        contenedor.add(new Bajas_Salud(cardLayout, contenedor), "Bajas_Salud");//centro salud
        contenedor.add(new Modificacion_Salud(cardLayout, contenedor), "Modificacion_Salud");//centro salud
        contenedor.add(new Reportes_Salud(cardLayout, contenedor), "Reportes_Salud");//centro salud
        
        contenedor.add(new Altas_Esavi(cardLayout, contenedor, cedula), "Altas_Esavi");//esavi
        contenedor.add(new Reportes_Esavi(cardLayout, contenedor), "Reportes_Esavi");//esavi
        
        contenedor.add(new AltasCampana(cardLayout, contenedor), "Altas_Campana");//campañas de vacunacion
        contenedor.add(new BajasCampana(cardLayout, contenedor), "Bajas_Campana");//campañas de vacunacion
        contenedor.add(new ModificarCampana(cardLayout, contenedor), "Modificar_Campana");//campañas de vacunacion
        contenedor.add(new ReportesCampana(cardLayout, contenedor), "Reportes_Campana");//campañas de vacunacion
        
        contenedor.add(new AsignacionesVacunas(cardLayout, contenedor), "Altas_Asignaciones");// asignaciones de vacunas
        contenedor.add(new BajasAsignaciones(cardLayout, contenedor), "Bajas_Asignaciones");// asignaciones de vacunas
        contenedor.add(new ModificarAsignaciones(cardLayout, contenedor), "Modificar_Asignaciones");// asignaciones de vacunas
        contenedor.add(new ReportesAsignaciones(cardLayout, contenedor), "Reportes_Asignaciones");// asignaciones de vacunas
        
        contenedor.add(new AltaVacunacion(cardLayout, contenedor,correoEmpleado), "Altas_Vacunacion");//vacunacion
        contenedor.add(new ReportesVacunacion(cardLayout, contenedor), "Reportes_Vacunacion");//vacunacion
        
        contenedor.add(new altaNotificacion(cardLayout, contenedor), "altaNotificacion");
        cardLayout.show(contenedor, "menu");
        return contenedor;
    }

    //  MENÚ PRINCIPAL 6 cuadros
    private JPanel crearMenuPrincipal() {
       JPanel envoltorio = new JPanel(new BorderLayout());
       envoltorio.setBackground(FONDO);

       // — Franja decorativa izquierda —
       envoltorio.add(crearFranjaLateral(), BorderLayout.WEST);
       // — Franja decorativa derecha —
       envoltorio.add(crearFranjaLateral(), BorderLayout.EAST);

       // — Cuadrícula central de botones —
       JPanel centrado = new JPanel(new GridBagLayout());
       centrado.setBackground(FONDO);

       JPanel grid = new JPanel(new GridLayout(2, 3, 28, 28));
       grid.setBackground(FONDO);

       String[] nombres = {"Campaña de Vacunación", "Lotes", "Centro de Salud",
                           "Aplicador de Vacunas", "Generador de ESAVI", "Asignación Vacunas"}; //AQUI SE CAMBIAN LOS NOMBRES DE LOS MODULOS EN PANTALLA
       String[] claves  = {"modulo1", "modulo2", "modulo3",
                           "modulo4", "modulo5", "modulo6"};

       for (int i = 0; i < 6; i++) {
           final String clave = claves[i];
           grid.add(new BotonModulo(nombres[i], () -> cardLayout.show(contenedor, clave)));
       }

       centrado.add(grid, new GridBagConstraints());
       envoltorio.add(centrado, BorderLayout.CENTER);

       return envoltorio;
   }

   /** Franja lateral decorativa con línea guindo y texto vertical. */
   private JPanel crearFranjaLateral() {
       JPanel franja = new JPanel() {
           @Override
           protected void paintComponent(Graphics g) {
               super.paintComponent(g);
               Graphics2D g2 = (Graphics2D) g.create();
               g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

               int cx = getWidth() / 2;

               // Línea guindo vertical
               g2.setColor(new Color(109, 27, 46, 60));
               g2.setStroke(new BasicStroke(2));
               g2.drawLine(cx, 40, cx, getHeight() - 40);

               // Círculos decorativos en los extremos
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

    
    
        /**
     * Consulta la foto del empleado en la BD usando su correo.
     * Devuelve null si no tiene foto guardada o si ocurre algún error.
     */
    private ImageIcon obtenerFotoEmpleado() {
        String sql = "SELECT e.foto FROM empleados e " +
                     "JOIN usuarios u ON e.id_empleado = u.id_empleado " +
                     "WHERE u.correo = ?";
        try (java.sql.Connection con = Conexion_DB.obtenerConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correoEmpleado);
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                byte[] bytes = rs.getBytes("foto");
                if (bytes != null && bytes.length > 0) {
                    ImageIcon fotoOriginal = new ImageIcon(bytes);
                    // Recorte circular 40×40px
                    Image imgEscalada = fotoOriginal.getImage()
                        .getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                    return new ImageIcon(imgEscalada);
                }
            }
        } catch (Exception ex) {
            // Si falla silenciosamente, se usará el ícono por defecto
        }
        return null; // sin foto → usará el emoji 👤
    }
    
    private String obtenerCedula() {
    String sql = "SELECT e.cedula_profesional FROM empleados e " +
                 "JOIN usuarios u ON e.id_empleado = u.id_empleado " +
                 "WHERE u.correo = ?";

    try (java.sql.Connection con = Conexion_DB.obtenerConexion();
         java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, correoEmpleado);
        java.sql.ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("cedula_profesional");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
    }
    
    //  BARRA INFERIOR  no modificar
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
}