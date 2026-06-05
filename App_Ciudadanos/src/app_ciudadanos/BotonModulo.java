package app_ciudadanos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class BotonModulo extends JPanel {

    private static final Color GUINDO       = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color GUINDO_CLICK = new Color(85, 20, 35);

    private Color colorActual = GUINDO;
    private final String texto;
    private final Runnable accion;
    private int badge = 0;
    private ImageIcon icono;   // <-- nueva variable para la imagen

    // Constructor sin imagen (mantiene compatibilidad)
    public BotonModulo(String texto, Runnable accion) {
        this(texto, null, accion);
    }

    // Constructor con imagen (nuevo)
    public BotonModulo(String texto, ImageIcon icono, Runnable accion) {
        this.texto  = texto;
        this.icono  = icono;
        this.accion = accion;
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(300, 185));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e)  { colorActual = GUINDO_HOVER; repaint(); }
            public void mouseExited(MouseEvent e)   { colorActual = GUINDO;       repaint(); }
            public void mousePressed(MouseEvent e)  { colorActual = GUINDO_CLICK; repaint(); }
            public void mouseReleased(MouseEvent e) {
                colorActual = GUINDO_HOVER; repaint();
                if (accion != null) accion.run();
            }
        });
    }

    public void setBadge(int cantidad) {
        this.badge = cantidad;
        repaint();
    }

    public void limpiarBadge() {
        this.badge = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth(), h = getHeight(), arco = 40;

        // Sombra
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fill(new RoundRectangle2D.Double(4, 6, w - 6, h - 6, arco, arco));

        // Fondo del botón
        g2.setColor(colorActual);
        g2.fill(new RoundRectangle2D.Double(0, 0, w - 4, h - 4, arco, arco));

        // --- DIBUJAR IMAGEN (si existe) ---
        int textY; // posición Y donde comenzará el texto
        if (icono != null) {
            // Tamaño deseado para la imagen (ejemplo 80x80, ajustable)
            int imgAncho = 80;
            int imgAlto  = 80;
            Image img = icono.getImage();
            // Escalar manteniendo proporción (opcional: usar getScaledInstance)
            Image imgEscalada = img.getScaledInstance(imgAncho, imgAlto, Image.SCALE_SMOOTH);
            int imgX = (w - imgAncho) / 2;
            int imgY = 25; // margen superior
            g2.drawImage(imgEscalada, imgX, imgY, imgAncho, imgAlto, null);
            textY = imgY + imgAlto + 20; // debajo de la imagen
        } else {
            // Sin imagen: texto centrado verticalmente
            textY = (h - 4) / 2;
        }

        // --- DIBUJAR TEXTO ---
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 17));
        FontMetrics fm = g2.getFontMetrics();
        int textoAncho = fm.stringWidth(texto);
        int textoX = ((w - 4) - textoAncho) / 2;
        int textoY;
        if (icono != null) {
            textoY = textY + fm.getAscent() - 4;
        } else {
            textoY = textY + fm.getHeight() / 2 - 2;
        }
        g2.drawString(texto, textoX, textoY);

        // --- BADGE (si existe) ---
        if (badge > 0) {
            String badgeTexto = badge > 99 ? "99+" : String.valueOf(badge);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fmB = g2.getFontMetrics();
            int badgeW = Math.max(22, fmB.stringWidth(badgeTexto) + 16);
            int badgeH = 25;
            int badgeX = w - badgeW - 8;
            int badgeY = 8;

            g2.setColor(new Color(220, 30, 30));
            g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, badgeH, badgeH);

            g2.setColor(Color.WHITE);
            g2.drawString(badgeTexto,
                badgeX + (badgeW - fmB.stringWidth(badgeTexto)) / 2,
                badgeY + badgeH / 2 + fmB.getAscent() / 2 - 1);
        }

        g2.dispose();
    }
}