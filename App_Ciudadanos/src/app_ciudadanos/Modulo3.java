package app_ciudadanos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;

public class Modulo3 extends JPanel {

    private static final Color GUINDO       = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO        = new Color(245, 245, 248);
    private static final Color ORO          = new Color(193, 154, 80);

    private final int idCiudadano;
    private final CardLayout cardLayout;
    private final JPanel contenedor;

    private JTable tablaVacunas;
    private DefaultTableModel modelo;
    private final List<DatosVacuna> listaVacunas = new ArrayList<>();

    public Modulo3(CardLayout cardLayout, JPanel contenedor, int idCiudadano) {
        this.cardLayout  = cardLayout;
        this.contenedor  = contenedor;
        this.idCiudadano = idCiudadano;

        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // ── NORTE ──
        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(FONDO);
        JLabel titulo = new JLabel("Mi Bienestar Certificado");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        norte.add(titulo, BorderLayout.NORTH);
        JLabel sub = new JLabel(
            "Selecciona una vacuna y presiona «Generar PDF» — solo deberás elegir dónde guardar el archivo.");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(Color.GRAY);
        sub.setBorder(new EmptyBorder(6, 0, 0, 0));
        norte.add(sub, BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        // ── CENTRO ──
        String[] cols = {"#", "Nombre Comercial", "Marca / Empresa", "Lote", "Fecha de Aplicación"};        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaVacunas = new JTable(modelo);
        tablaVacunas.setRowHeight(28);
        tablaVacunas.getTableHeader().setBackground(GUINDO);
        tablaVacunas.getTableHeader().setForeground(Color.WHITE);
        tablaVacunas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablaVacunas.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaVacunas.setSelectionBackground(new Color(193, 154, 80, 100));
        tablaVacunas.setGridColor(new Color(220, 220, 220));
        tablaVacunas.setFillsViewportHeight(true);
        tablaVacunas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaVacunas.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scroll = new JScrollPane(tablaVacunas);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setBackground(FONDO);
        centro.setBorder(new EmptyBorder(16, 0, 0, 0));
        centro.add(scroll, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        // ── SUR ──
        JButton btnGenerar = new JButton("  Generar Certificado PDF");
        btnGenerar.setBackground(ORO);
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFocusPainted(false);
        btnGenerar.setBorderPainted(false);
        btnGenerar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGenerar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerar.setPreferredSize(new Dimension(270, 40));
        btnGenerar.addActionListener(e -> generarCertificado());
        btnGenerar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnGenerar.setBackground(new Color(165, 125, 55)); }
            public void mouseExited (java.awt.event.MouseEvent e) { btnGenerar.setBackground(ORO); }
        });

        JButton btnRegresar = new JButton("← Regresar al menú");
        btnRegresar.setBackground(GUINDO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "menu"));
        btnRegresar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnRegresar.setBackground(GUINDO_HOVER); }
            public void mouseExited (java.awt.event.MouseEvent e) { btnRegresar.setBackground(GUINDO); }
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        sur.setBackground(FONDO);
        sur.add(btnRegresar);
        sur.add(btnGenerar);
        add(sur, BorderLayout.SOUTH);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentShown(java.awt.event.ComponentEvent e) { cargarHistorial(); }
        });
    }

    //  CARGA DE DATOS
    //  Tablas involucradas:
    //    · aplicador_vacunas   id_application, fecha (cuándo se aplicó)
    //    · ciudadanos          nombre, apellido, curp (JOIN por curp_paciente = curp)
    //    · campana_vacunacion  nombre de la campaña   (JOIN por id_campana)
    //    · campana_detalle_lote     
    //    · asignacion_vacunas_centro subqueries para llegar al lote
    //    · lotes               lote_vacunacion, empresa, nombre_comercial
    // ════════════════════════════════════════════════════════════
    private void cargarHistorial() {
        modelo.setRowCount(0);
        listaVacunas.clear();

        String sql =
            "SELECT av.id_aplicacion, av.fecha, " +
            "       c.nombre, c.apellido, c.curp, " +
            "       cv.nombre AS campana, " +
            /* lote_vacunacion: aplicador_vacunas → campana → campana_detalle_lote → asignacion → lotes */
            "       (SELECT l.lote_vacunacion " +
            "          FROM campana_detalle_lote cdl " +
            "          JOIN asignacion_vacunas_centro avc ON avc.id_inventario = cdl.id_inventario " +
            "          JOIN lotes l ON l.id_lote = avc.id_lote " +
            "         WHERE cdl.id_campana = av.id_campana LIMIT 1) AS lote_vacunacion, " +
            "       (SELECT l.empresa " +
            "          FROM campana_detalle_lote cdl " +
            "          JOIN asignacion_vacunas_centro avc ON avc.id_inventario = cdl.id_inventario " +
            "          JOIN lotes l ON l.id_lote = avc.id_lote " +
            "         WHERE cdl.id_campana = av.id_campana LIMIT 1) AS empresa, " +
            "       (SELECT l.nombre_comercial " +
            "          FROM campana_detalle_lote cdl " +
            "          JOIN asignacion_vacunas_centro avc ON avc.id_inventario = cdl.id_inventario " +
            "          JOIN lotes l ON l.id_lote = avc.id_lote " +
            "         WHERE cdl.id_campana = av.id_campana LIMIT 1) AS nombre_comercial " +
            "FROM aplicador_vacunas av " +
            "JOIN ciudadanos c ON av.curp_paciente = c.curp " +
            "JOIN campana_vacunacion cv ON av.id_campana = cv.id_campana " +
            "WHERE c.id_ciudadano = ? " +
            "ORDER BY av.fecha DESC";

        try (Connection con = Conexion_DB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiudadano);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DatosVacuna d = new DatosVacuna();
                d.idApplication = rs.getInt("id_aplicacion");
                d.nombre          = rs.getString("nombre") + " " + rs.getString("apellido");
                d.curp            = rs.getString("curp");
                d.campana         = rs.getString("campana");
                d.empresa         = nvl(rs.getString("empresa"),         "N/D");
                d.lote            = nvl(rs.getString("lote_vacunacion"),  "N/D");
                d.nombreComercial = nvl(rs.getString("nombre_comercial"), d.campana);
                d.fechaAplicacion = rs.getTimestamp("fecha") != null
                                    ? rs.getTimestamp("fecha").toString().substring(0, 16)
                                    : "N/D";
                listaVacunas.add(d);
                modelo.addRow(new Object[]{
                    d.idApplication, d.nombreComercial, d.empresa, d.lote, d.fechaAplicacion
                });
            }
            if (listaVacunas.isEmpty())
                modelo.addRow(new Object[]{"—", "No se encontraron vacunas registradas.", "", "", ""});
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar historial: " + ex.getMessage(),
                "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    //  GENERAR PDF — un solo clic: elige dónde guardar y listo
    private void generarCertificado() {
        int fila = tablaVacunas.getSelectedRow();
        if (fila < 0 || listaVacunas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Selecciona una vacuna de la tabla primero.",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DatosVacuna datos = listaVacunas.get(fila);
        datos.fechaExpedicion = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
                    new java.util.Locale("es", "MX")));

        // Imágenes (opcionales — agrégalas en src/Recursos/)
        BufferedImage escudo = cargarImg("/Recursos/escudo.png");
        BufferedImage firma   = cargarImg("/Recursos/firma.png");

        // ── Selector de archivo (ÚNICA interacción del usuario) ──
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar Certificado de Vacunación");
        fc.setSelectedFile(new java.io.File("Certificado_Vacunacion_" + datos.curp + ".pdf"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File archivo = fc.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf"))
            archivo = new java.io.File(archivo.getAbsolutePath() + ".pdf");

        // ── Configurar página (carta, márgenes mínimos para bordes al filo) ──
        PrinterJob job = PrinterJob.getPrinterJob();

        // Buscar impresora PDF automáticamente (Microsoft Print to PDF, Adobe, etc.)
        for (PrintService svc : PrintServiceLookup.lookupPrintServices(null, null)) {
            String n = svc.getName().toLowerCase();
            if (n.contains("pdf") || n.contains("adobe pdf") || n.contains("foxit")) {
                try { job.setPrintService(svc); break; } catch (Exception ignored) {}
            }
        }

        PageFormat pf = job.defaultPage();
        Paper papel = new Paper();
        double pw = 8.5 * 72, ph = 11.0 * 72;   // 612 × 792 pt  (carta)
        papel.setSize(pw, ph);
        // Márgenes de solo 12 pt → los bordes decorativos quedan muy cerca del filo
        papel.setImageableArea(12, 12, pw - 24, ph - 24);
        pf.setPaper(papel);
        pf.setOrientation(PageFormat.PORTRAIT);

        final DatosVacuna d = datos;
        final BufferedImage esc = escudo, fir = firma;

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,        RenderingHints.VALUE_RENDER_QUALITY);
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            dibujarCertificado(g2,
                (int) pageFormat.getImageableWidth(),
                (int) pageFormat.getImageableHeight(),
                d, esc, fir);
            return Printable.PAGE_EXISTS;
        }, pf);

        job.setJobName("Certificado_" + datos.curp);

        // Imprimir directamente al archivo — SIN diálogo de impresión
        try {
            PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
            attrs.add(new Destination(archivo.toURI()));
            job.print(attrs);
            JOptionPane.showMessageDialog(this,
                "¡Certificado guardado correctamente!\n" + archivo.getAbsolutePath(),
                "PDF Generado", JOptionPane.INFORMATION_MESSAGE);
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo guardar directamente.\n"
                + "Se abrirá el diálogo de impresión — selecciona «Microsoft Print to PDF».",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            // Fallback: abrir diálogo normal
            if (job.printDialog()) {
                try { job.print(); }
                catch (PrinterException ex2) {
                    JOptionPane.showMessageDialog(this,
                        "Error: " + ex2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    //  DIBUJO DEL CERTIFICADO
    private void dibujarCertificado(Graphics2D g, int W, int H,
                                    DatosVacuna d,
                                    BufferedImage escudo, BufferedImage firma) {

        final Color VERDE     = new Color(0,  100, 50);
        final Color VERDE_OSC = new Color(0,   70, 30);
        final Color DORADO    = new Color(193, 154, 80);
        final Color GUINDO_C  = new Color(109,  27, 46);
        final Color GRIS_CAMP = new Color(245, 245, 245);
        final Color GRIS_TXT  = new Color(55,   55, 55);

        // Fondo blanco
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        // ══ BORDES TIPO ACTA (muy cerca del filo) ══
        // 1) Marco exterior verde oscuro — 7 pt
        g.setColor(VERDE_OSC);
        g.setStroke(new BasicStroke(7f));
        g.drawRect(4, 4, W - 8, H - 8);

        // 2) Filo dorado
        g.setColor(DORADO);
        g.setStroke(new BasicStroke(2.5f));
        g.drawRect(10, 10, W - 20, H - 20);

        // 3) Línea verde interior delgada
        g.setColor(VERDE);
        g.setStroke(new BasicStroke(1f));
        g.drawRect(14, 14, W - 28, H - 28);

        // 4) Patrón de segmentos en los 4 lados
        dibujarPatronBorde(g, W, H, VERDE, DORADO);

        // 5) Ornamentos en esquinas
        dibujarEsquinas(g, W, H, VERDE_OSC, DORADO);

        //  ENCABEZADO 
        int y = 30;

        // Escudo centrado
        if (escudo != null) {
            int eH = 80;
            int eW = (int)((double) escudo.getWidth() / escudo.getHeight() * eH);
            g.drawImage(escudo, (W - eW) / 2, y, eW, eH, null);
            y += eH + 8;
        } else {
            // Placeholder (quitar cuando pongas el PNG real)
            g.setColor(new Color(210, 210, 210));
            g.fillOval(W / 2 - 36, y, 72, 72);
            g.setColor(GRIS_TXT);
            g.setFont(new Font("Arial", Font.PLAIN, 8));
            drawCenter(g, "[ Escudo ]", W, y + 40);
            y += 82;
        }

        // País
        g.setColor(GUINDO_C);
        g.setFont(new Font("Times New Roman", Font.BOLD, 15));
        drawCenter(g, "ESTADOS UNIDOS MEXICANOS", W, y);
        y += 17;

        g.setColor(VERDE_OSC);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 10));
        drawCenter(g, "Secretaría de Salud  ·  Sistema Nacional de Vacunación", W, y);
        y += 13;

        // Línea dorada
        g.setColor(DORADO);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(20, y, W - 20, y);
        y += 5;

        // Título
        g.setColor(GUINDO_C);
        g.setFont(new Font("Times New Roman", Font.BOLD, 25));
        drawCenter(g, "CERTIFICADO DE VACUNACIÓN", W, y + 22);
        y += 30;

        g.setColor(DORADO);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(20, y, W - 20, y);
        y += 9;

        // Folio y fecha — banda gris
        g.setColor(new Color(240, 240, 240));
        g.fillRoundRect(20, y, W - 40, 18, 4, 4);
        g.setColor(GRIS_TXT);
        g.setFont(new Font("Arial", Font.PLAIN, 8));
        drawCenter(g,
            "Folio: VX-" + String.format("%06d", d.idApplication)
            + "        Fecha de expedición: " + d.fechaExpedicion,
            W, y + 12);
        y += 23;

        // ══ SECCIÓN PACIENTE ══
        y = dibujarBandaSeccion(g, W, y, "  DATOS DEL PACIENTE", VERDE, VERDE_OSC);
        y = dibujarCampo(g, 20, y, W - 40, "Nombre completo:", d.nombre.toUpperCase(), VERDE_OSC, GRIS_CAMP, GRIS_TXT);
        y = dibujarCampo(g, 20, y, W - 40, "CURP:", d.curp, VERDE_OSC, GRIS_CAMP, GRIS_TXT);

        // ══ SECCIÓN VACUNACIÓN ══
        y = dibujarBandaSeccion(g, W, y, "  DATOS DE VACUNACIÓN", VERDE, VERDE_OSC);

        // Fila 1: Campaña | Marca   (dos columnas)
        int mitad = W / 2 - 2, xDer = W / 2 + 2, cW = mitad - 20;
        int yFila = y;
        dibujarCampo(g, 20,   yFila, cW, "Nombre Comercial de la Vacuna:", d.nombreComercial, VERDE_OSC, GRIS_CAMP, GRIS_TXT);
        y = dibujarCampo(g, xDer, yFila, cW, "Marca:",                     d.empresa,         VERDE_OSC, GRIS_CAMP, GRIS_TXT);

        // Fila 2: Lote | Fecha aplicación
        yFila = y;
        dibujarCampo(g, 20,   yFila, cW, "Número de Lote:",          d.lote,              VERDE_OSC, GRIS_CAMP, GRIS_TXT);
        y = dibujarCampo(g, xDer, yFila, cW, "Fecha de Aplicación:", d.fechaAplicacion,   VERDE_OSC, GRIS_CAMP, GRIS_TXT);

        

        //  TEXTO LEGAL 
        g.setColor(new Color(90, 90, 90));
        g.setFont(new Font("Times New Roman", Font.ITALIC, 8));
        String legal =
            "El presente documento certifica que la persona identificada ha recibido la vacuna indicada, " +
            "conforme a los registros del Sistema Nacional de Vacunación de los Estados Unidos Mexicanos. " +
            "Este documento tiene validez oficial en todo el territorio nacional y podrá ser requerido como " +
            "comprobante de inmunización ante autoridades públicas y privadas.";
        y = dibujarTextoJustificado(g, legal, 20, y, W - 40, 11);
        y += 10;

        // Línea dorada
        g.setColor(DORADO);
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(20, y, W - 20, y);
        y += 14;

        //  FIRMA 
        int fW = 180, fH = 65, fX = (W - fW) / 2;
        if (firma != null) {
            g.drawImage(firma, fX, y, fW, fH, null);
        }
        int yPost = y + fH + 4;
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(0.8f));
        g.drawLine(fX, yPost, fX + fW, yPost);
        yPost += 12;

        g.setColor(GUINDO_C);
        g.setFont(new Font("Times New Roman", Font.BOLD, 10));
        drawCenter(g, "Director General de Epidemiología", W, yPost);
        yPost += 13;

        g.setColor(VERDE_OSC);
        g.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        drawCenter(g, "Secretaría de Salud — Gobierno de México", W, yPost);
        yPost += 16;

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 7));
        drawCenter(g,
            "Verifique la autenticidad en: www.salud.gob.mx/certificados  |  Folio: VX-"
            + String.format("%06d", d.idApplication),
            W, yPost);

        // ══ BANDA INFERIOR GUINDO ══
        // Posicionada dejando ~20 pt desde el borde decorativo inferior
        int bandaY = H - 32;
        g.setColor(GUINDO_C);
        g.fillRect(20, bandaY, W - 40, 14);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 6));
        drawCenter(g,
            "SISTEMA NACIONAL DE VACUNACIÓN  ·  SECRETARÍA DE SALUD  ·  GOBIERNO DE MÉXICO  ·  2026",
            W, bandaY + 10);
    }

    //  HELPERS DE DIBUJO
    private int dibujarBandaSeccion(Graphics2D g, int W, int y,
                                    String texto, Color fondo, Color fuente) {
        g.setColor(new Color(fondo.getRed(), fondo.getGreen(), fondo.getBlue(), 35));
        g.fillRoundRect(20, y, W - 40, 18, 4, 4);
        g.setColor(fuente);
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(20, y, W - 40, 18, 4, 4);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString(texto, 26, y + 13);
        return y + 24;
    }

    private int dibujarCampo(Graphics2D g, int x, int y, int ancho,
                              String etiqueta, String valor,
                              Color colorEtiq, Color colorFondo, Color colorValor) {
        int alto = 30;
        g.setColor(colorFondo);
        g.fillRoundRect(x, y, ancho, alto, 4, 4);
        g.setColor(new Color(200, 200, 200));
        g.setStroke(new BasicStroke(0.5f));
        g.drawRoundRect(x, y, ancho, alto, 4, 4);
        g.setColor(colorEtiq);
        g.setFont(new Font("Arial", Font.BOLD, 8));
        g.drawString(etiqueta, x + 6, y + 10);
        g.setColor(colorValor);
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        g.drawString(truncar(g, valor, ancho - 10), x + 6, y + 24);
        return y + alto + 5;
    }

    private void dibujarPatronBorde(Graphics2D g, int W, int H, Color c1, Color c2) {
        int paso = 10;
        g.setStroke(new BasicStroke(1.5f));
        for (int x = 18; x < W - 18; x += paso) {
            g.setColor((x / paso) % 2 == 0 ? c1 : c2);
            int fin = Math.min(x + paso - 2, W - 18);
            g.drawLine(x, 8,     fin, 8);
            g.drawLine(x, H - 8, fin, H - 8);
        }
        for (int yy = 18; yy < H - 18; yy += paso) {
            g.setColor((yy / paso) % 2 == 0 ? c1 : c2);
            int fin = Math.min(yy + paso - 2, H - 18);
            g.drawLine(8,     yy, 8,     fin);
            g.drawLine(W - 8, yy, W - 8, fin);
        }
    }

    private void dibujarEsquinas(Graphics2D g, int W, int H, Color relleno, Color borde) {
        int r = 18;
        int[][] pos = {
            {4, 4}, {W - 4 - r * 2, 4},
            {4, H - 4 - r * 2}, {W - 4 - r * 2, H - 4 - r * 2}
        };
        for (int[] p : pos) {
            g.setColor(borde);
            g.setStroke(new BasicStroke(2f));
            g.drawOval(p[0], p[1], r * 2, r * 2);
            g.setColor(relleno);
            g.fillOval(p[0] + 4, p[1] + 4, r * 2 - 8,  r * 2 - 8);
            g.setColor(borde);
            g.fillOval(p[0] + 8, p[1] + 8, r * 2 - 16, r * 2 - 16);
        }
    }

    private void drawCenter(Graphics2D g, String s, int W, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, (W - fm.stringWidth(s)) / 2, y);
    }

    private int dibujarTextoJustificado(Graphics2D g, String texto,
                                        int x, int y, int ancho, int lineH) {
        String[] palabras = texto.split(" ");
        StringBuilder linea = new StringBuilder();
        FontMetrics fm = g.getFontMetrics();
        for (String p : palabras) {
            if (fm.stringWidth(linea + p + " ") > ancho) {
                g.drawString(linea.toString().trim(), x, y);
                y += lineH;
                linea = new StringBuilder();
            }
            linea.append(p).append(" ");
        }
        if (linea.length() > 0) { g.drawString(linea.toString().trim(), x, y); y += lineH; }
        return y;
    }

    private String truncar(Graphics2D g, String s, int max) {
        if (s == null) return "N/D";
        FontMetrics fm = g.getFontMetrics();
        if (fm.stringWidth(s) <= max) return s;
        while (s.length() > 0 && fm.stringWidth(s + "…") > max) s = s.substring(0, s.length() - 1);
        return s + "…";
    }

    private BufferedImage cargarImg(String ruta) {
        try {
            java.net.URL url = getClass().getResource(ruta);
            if (url == null) return null;
            return ImageIO.read(url);
        } catch (Exception e) { return null; }
    }

    private static String nvl(String v, String def) {
        return (v == null || v.isBlank()) ? def : v;
    }

    //  CLASE INTERNA DE DATOS
    static class DatosVacuna {
        int    idApplication;
        String nombre, curp, campana;
        String empresa, lote, nombreComercial;
        String fechaAplicacion, fechaExpedicion;
    }
}