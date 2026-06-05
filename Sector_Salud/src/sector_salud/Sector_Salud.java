package sector_salud;

import javax.swing.SwingUtilities;

public class Sector_Salud 
{
    public static void main(String[] args) {
        // SwingUtilities.invokeLater garantiza que la interfaz
        // se cree en el hilo correcto (buena práctica en Java Swing)
        SwingUtilities.invokeLater(() -> 
        {
            new Ventana_login().setVisible(true);
        });
    }
}
