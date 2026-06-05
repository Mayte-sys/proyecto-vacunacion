package app_ciudadanos;

import app_ciudadanos.Conexion_DB;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Modulo4 extends JPanel {
    private CardLayout cardLayout;
    private JPanel contenedor;
    private JTable tablaCampanas;
    private DefaultTableModel modeloTabla;
    private Connection conexionBD;
    private Image imagenMapa;
    private Map<String, Rectangle> areaEstados;

    private static final Color GRIS_FONDO = new Color(245, 245, 248);
    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color ORO = new Color(193, 154, 80);

    public Modulo4(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;

        // Configurar modelo de tabla (columnas)
        modeloTabla = new DefaultTableModel(
            new String[]{"Campaña", "Descripción", "Centro de Salud", "Horario", "Fecha Inicio", "Fecha Fin"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCampanas = new JTable(modeloTabla);
        tablaCampanas.setRowHeight(25);
        tablaCampanas.setFont(new Font("Arial", Font.PLAIN, 12));

        // Personalizar cabecera
        JTableHeader header = tablaCampanas.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(GUINDO);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 30));

        tablaCampanas.setGridColor(Color.LIGHT_GRAY);
        tablaCampanas.setShowGrid(true);

        // Ajustar anchos de columna (opcional)
        tablaCampanas.getColumnModel().getColumn(0).setPreferredWidth(150); // Campaña
        tablaCampanas.getColumnModel().getColumn(1).setPreferredWidth(200); // Descripción
        tablaCampanas.getColumnModel().getColumn(2).setPreferredWidth(180); // Centro
        tablaCampanas.getColumnModel().getColumn(3).setPreferredWidth(100); // Horario
        tablaCampanas.getColumnModel().getColumn(4).setPreferredWidth(100); // Fecha inicio
        tablaCampanas.getColumnModel().getColumn(5).setPreferredWidth(100); // Fecha fin

        // Cargar imagen del mapa (debe estar en src/recursos/mapa_mexico.png)
        try {
            imagenMapa = new ImageIcon(getClass().getResource("/recursos/mapa_mexico.png")).getImage();
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen del mapa.");
        }

        // Conectar a BD
        try {
            conexionBD = Conexion_DB.obtenerConexion();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error de conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }

        definirAreasDeEstados();
        configurarUI();
    }

    /**
     * Define las áreas (rectángulos) de cada estado sobre la imagen del mapa.
     * IMPORTANTE: ajusta los valores (x, y, ancho, alto) según las coordenadas reales
     * de tu imagen mapa_mexico.png (ancho x alto original).
     */
    private void definirAreasDeEstados() {
        areaEstados = new HashMap<>();
        // Ejemplos (debes modificarlos según tu imagen)
        areaEstados.put("Nuevo León", new Rectangle(420, 200, 60, 50));
        areaEstados.put("Jalisco",      new Rectangle(280, 280, 70, 60));
        areaEstados.put("Ciudad de México", new Rectangle(340, 320, 40, 30));
        areaEstados.put("Yucatán",      new Rectangle(520, 340, 70, 50));
        areaEstados.put("Chihuahua",    new Rectangle(300, 100, 100, 70));
        areaEstados.put("Sonora",       new Rectangle(180, 120, 80, 80));
        areaEstados.put("Veracruz",     new Rectangle(420, 320, 70, 60));
        // Agrega aquí todos los estados que necesites
    }

    private void configurarUI() {
        setLayout(new BorderLayout());
        setBackground(GRIS_FONDO);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Encabezado
        JPanel panelTitulo = new JPanel(new GridLayout(2, 1));
        panelTitulo.setBackground(GUINDO);
        panelTitulo.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titulo = new JLabel("Campañas Activas de Vacunación por Estado", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        JLabel subtitulo = new JLabel("Haz clic en cualquier estado del mapa para ver sus campañas", JLabel.CENTER);
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitulo.setForeground(ORO);
        panelTitulo.add(titulo);
        panelTitulo.add(subtitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel central: mapa + reporte
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCentral.setBackground(GRIS_FONDO);
        panelCentral.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Panel izquierdo: mapa con detección de clics
        JPanel panelMapa = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                if (imagenMapa != null) {
                    int pW = getWidth(), pH = getHeight();
                    int iW = imagenMapa.getWidth(null), iH = imagenMapa.getHeight(null);
                    double propImagen = (double) iW / iH;
                    double propPanel = (double) pW / pH;
                    int dW, dH, dX, dY;
                    if (propPanel > propImagen) {
                        dW = (int) (pH * propImagen);
                        dH = pH;
                        dX = (pW - dW) / 2;
                        dY = 0;
                    } else {
                        dW = pW;
                        dH = (int) (pW / propImagen);
                        dX = 0;
                        dY = (pH - dH) / 2;
                    }
                    g2d.drawImage(imagenMapa, dX, dY, dW, dH, this);
                } else {
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("Mapa no disponible", getWidth() / 2 - 50, getHeight() / 2);
                }
                g2d.dispose();
            }
        };
        panelMapa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelMapa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String estado = obtenerEstadoPorClic(e, panelMapa);
                if (estado != null) {
                    actualizarReporteCampanas(estado);
                } else {
                    modeloTabla.setRowCount(0);
                    modeloTabla.addRow(new Object[]{"", "Haz clic en un estado del mapa", "", "", "", ""});
                }
            }
        });
        panelCentral.add(panelMapa);

        // Panel derecho: tabla con formato de reporte
        JPanel panelReporte = new JPanel(new BorderLayout());
        panelReporte.setBackground(GRIS_FONDO);
        panelReporte.setBorder(new TitledBorder(BorderFactory.createLineBorder(ORO, 2),
                " REPORTE DE CAMPAÑAS ACTIVAS ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), GUINDO));

        JScrollPane scrollTabla = new JScrollPane(tablaCampanas);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder());
        panelReporte.add(scrollTabla, BorderLayout.CENTER);
        panelCentral.add(panelReporte);
        add(panelCentral, BorderLayout.CENTER);

        // Botón regresar (ajusta "modulo4" al nombre de tu panel menú)
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setBackground(GRIS_FONDO);
        JButton btnRegresar = new JButton("Regresar al Menú");
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRegresar.setBackground(GUINDO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "menu"));
        panelInferior.add(btnRegresar);
        add(panelInferior, BorderLayout.SOUTH);
    }

    /**
     * Determina en qué estado se hizo clic según las coordenadas del mouse
     * y las áreas predefinidas.
     */
    private String obtenerEstadoPorClic(MouseEvent e, JPanel panelMapa) {
        if (imagenMapa == null) return null;
        int panelW = panelMapa.getWidth();
        int panelH = panelMapa.getHeight();
        int iW = imagenMapa.getWidth(null);
        int iH = imagenMapa.getHeight(null);
        double propImagen = (double) iW / iH;
        double propPanel = (double) panelW / panelH;
        int dW, dH, dX, dY;
        if (propPanel > propImagen) {
            dW = (int) (panelH * propImagen);
            dH = panelH;
            dX = (panelW - dW) / 2;
            dY = 0;
        } else {
            dW = panelW;
            dH = (int) (panelW / propImagen);
            dX = 0;
            dY = (panelH - dH) / 2;
        }
        int clickX = e.getX() - dX;
        int clickY = e.getY() - dY;
        if (clickX < 0 || clickY < 0 || clickX > dW || clickY > dH) return null;
        double escalaX = (double) iW / dW;
        double escalaY = (double) iH / dH;
        int imgX = (int) (clickX * escalaX);
        int imgY = (int) (clickY * escalaY);
        for (Map.Entry<String, Rectangle> entry : areaEstados.entrySet()) {
            if (entry.getValue().contains(imgX, imgY)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Consulta la base de datos y actualiza la tabla con todas las campañas activas
     * del estado seleccionado, incluyendo el centro de salud y su horario.
     */
    private void actualizarReporteCampanas(String estado) {
        modeloTabla.setRowCount(0); // limpiar tabla
        String sql = """
            SELECT c.nombre AS campana_nombre,
                   c.descripcion,
                   c.fecha_inicio,
                   c.fecha_fin,
                   cs.nombre AS centro_nombre,
                   cs.horario_apertura,
                   cs.horario_cierre
            FROM campana_vacunacion c
            INNER JOIN centros_salud cs ON c.id_centro = cs.id_centro
            WHERE cs.estado = ? AND c.activo = TRUE
            ORDER BY c.fecha_inicio DESC
        """;
        try (PreparedStatement pst = conexionBD.prepareStatement(sql)) {
            pst.setString(1, estado);
            ResultSet rs = pst.executeQuery();
            boolean hayDatos = false;
            while (rs.next()) {
                hayDatos = true;
                String horario = String.format("%s – %s",
                        rs.getTime("horario_apertura").toString(),
                        rs.getTime("horario_cierre").toString());
                Object[] fila = {
                    rs.getString("campana_nombre"),
                    rs.getString("descripcion") != null ? rs.getString("descripcion") : "Sin descripción",
                    rs.getString("centro_nombre"),
                    horario,
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin")
                };
                modeloTabla.addRow(fila);
            }
            if (!hayDatos) {
                modeloTabla.addRow(new Object[]{"No hay campañas activas en " + estado, "", "", "", "", ""});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            modeloTabla.addRow(new Object[]{"Error de BD: " + e.getMessage(), "", "", "", "", ""});
        }
    }
}