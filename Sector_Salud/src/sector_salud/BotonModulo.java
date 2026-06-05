package sector_salud;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Botón personalizado con bordes curvos para el menú principal.
 */
public class BotonModulo extends JPanel {

    private static final Color GUINDO       = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color GUINDO_CLICK = new Color(85, 20, 35);

    private Color colorActual = GUINDO;
    private String texto;
    private Runnable accion;
    private ImageIcon imagen;  // Nueva variable para la imagen
    private boolean tieneImagen = false;

    // Constructor original (solo texto)
    public BotonModulo(String texto, Runnable accion) {
        this(texto, null, accion);
    }

    // Nuevo constructor (texto + imagen)
    public BotonModulo(String texto, ImageIcon imagen, Runnable accion) {
        this.texto  = texto;
        this.accion = accion;
        this.imagen = imagen;
        this.tieneImagen = (imagen != null);

        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(300, 185));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                colorActual = GUINDO_HOVER;
                repaint();
            }
            public void mouseExited(MouseEvent e) {
                colorActual = GUINDO;
                repaint();
            }
            public void mousePressed(MouseEvent e) {
                colorActual = GUINDO_CLICK;
                repaint();
            }
            public void mouseReleased(MouseEvent e) {
                colorActual = GUINDO_HOVER;
                repaint();
                if (accion != null) accion.run();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Suavizado de bordes
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (tieneImagen) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }

        int w = getWidth();
        int h = getHeight();
        int arco = 40;

        // Sombra suave
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fill(new RoundRectangle2D.Double(4, 6, w - 6, h - 6, arco, arco));

        // Fondo del botón
        g2.setColor(colorActual);
        g2.fill(new RoundRectangle2D.Double(0, 0, w - 4, h - 4, arco, arco));

        if (tieneImagen) {
            // Dibujar imagen arriba (centrada)
            int anchoImagen = 80;
            int altoImagen = 80;
            Image img = imagen.getImage();
            
            // Escalar imagen manteniendo calidad
            Image imagenEscalada = img.getScaledInstance(anchoImagen, altoImagen, Image.SCALE_SMOOTH);
            
            int xImagen = ((w - 4) - anchoImagen) / 2;
            int yImagen = 30; // Margen superior
            
            g2.drawImage(imagenEscalada, xImagen, yImagen, anchoImagen, altoImagen, null);
            
            // Dibujar texto debajo de la imagen
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();
            int xTexto = ((w - 4) - fm.stringWidth(texto)) / 2;
            int yTexto = yImagen + altoImagen + 30; // Debajo de la imagen
            
            g2.drawString(texto, xTexto, yTexto);
        } else {
            // Modo solo texto (centrado como antes)
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 25));
            FontMetrics fm = g2.getFontMetrics();
            int xTexto = ((w - 4) - fm.stringWidth(texto)) / 2;
            int yTexto = ((h - 4) - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(texto, xTexto, yTexto);
        }

        g2.dispose();
    }
}