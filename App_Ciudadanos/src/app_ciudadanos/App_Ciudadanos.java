package app_ciudadanos;

import javax.swing.SwingUtilities;

public class App_Ciudadanos {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Ventana_login().setVisible(true);
        });
    }
}
