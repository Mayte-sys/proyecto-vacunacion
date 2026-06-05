package sector_salud;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Modulo3 extends JPanel {

    private static final Color GUINDO       = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO        = new Color(245, 245, 248);

    public Modulo3(CardLayout cardLayout, JPanel contenedor) {
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Título
        JLabel titulo = new JLabel("Centros de Salud");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(new Color(109, 27, 46));
        add(titulo, BorderLayout.NORTH);

        // Panel envoltorio con franjas laterales (como el menú principal)
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setBackground(FONDO);

        // Franja decorativa izquierda
        envoltorio.add(crearFranjaLateral(), BorderLayout.WEST);
        // Franja decorativa derecha
        envoltorio.add(crearFranjaLateral(), BorderLayout.EAST);

        // Panel central con cuadrícula
        JPanel centrado = new JPanel(new GridBagLayout());
        centrado.setBackground(FONDO);

        JPanel grid = new JPanel(new GridLayout(2, 2, 28, 28));
        grid.setBackground(FONDO);
        
        ImageIcon iconoAltas = cargarImagen("/Recursos/logo_gob_mx.png", 64, 64);
        ImageIcon iconoBajas = cargarImagen("/Recursos/alta_folios.png", 64, 64);
        ImageIcon iconoModificaciones = cargarImagen("/Recursos/alta_folios.png", 64, 64);
        ImageIcon iconoReportes = cargarImagen("/Recursos/alta_folios.png", 64, 64);
        
        // Nombres y claves para los 4 botones
        String[] nombres = {"Altas", "Bajas", "Modificaciones", "Reportes"};
        ImageIcon[] iconos = {iconoAltas, iconoBajas, iconoModificaciones, iconoReportes};
        String[] claves  = {"Altas_Salud", "Bajas_Salud", "Modificacion_Salud", "Reportes_Salud"};

         for (int i = 0; i < 4; i++) {
            final String clave = claves[i];
            BotonModulo boton = new BotonModulo(nombres[i], iconos[i], () -> cardLayout.show(contenedor, clave));
            grid.add(boton);
        }

        centrado.add(grid, new GridBagConstraints());
        envoltorio.add(centrado, BorderLayout.CENTER);

        add(envoltorio, BorderLayout.CENTER);

        // Botón regresar
        JButton btnRegresar = new JButton("← Regresar al menú");
        btnRegresar.setBackground(new Color(109, 27, 46));
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "menu"));
        btnRegresar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                btnRegresar.setBackground(new Color(140, 40, 62)); 
            }
            public void mouseExited(java.awt.event.MouseEvent e) { 
                btnRegresar.setBackground(new Color(109, 27, 46)); 
            }
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.setBackground(FONDO);
        sur.add(btnRegresar);
        add(sur, BorderLayout.SOUTH);
    }

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
    private ImageIcon cargarImagen(String ruta, int ancho, int alto) {
    try {
        java.net.URL url = getClass().getResource(ruta);
        if (url != null) {
            ImageIcon original = new ImageIcon(url);
            // Escalar la imagen al tamaño deseado
            Image imagenEscalada = original.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            return new ImageIcon(imagenEscalada);
        } else {
            System.err.println("No se encontró la imagen: " + ruta);
        }
    } catch (Exception e) {
        System.err.println("Error al cargar la imagen: " + ruta + " - " + e.getMessage());
    }
    return null; // Retorna null si no se pudo cargar
    }
}