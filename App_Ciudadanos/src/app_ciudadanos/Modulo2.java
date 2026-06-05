package app_ciudadanos;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;


public class Modulo2 extends JPanel {

    /* ── Colores ─────────────────────────────────────────────────────────── */
    private static final Color GUINDO       = new Color(109, 27,  46);
    private static final Color GUINDO_DARK  = new Color( 85, 20,  35);
    private static final Color GUINDO_HOVER = new Color(140, 40,  62);
    private static final Color VERDE_OSC    = new Color( 39, 78,  19);
    private static final Color VERDE_MED    = new Color( 56,118,  29);
    private static final Color FONDO        = new Color(245,245, 248);
    private static final Color PAGINA       = new Color(254,253, 248);
    private static final Color BORDE_TABLA  = new Color(160,200, 140);
    
    private static final Color C_SR     = new Color(255, 243, 215); 
    private static final Color C_TD     = new Color(210, 232, 248); 
    private static final Color C_TDPA   = new Color(210, 240, 215); 
    private static final Color C_FLU    = new Color(252, 224, 230); 
    private static final Color C_COV    = new Color(255, 208, 210); 
    private static final Color C_OTRAS  = new Color(242, 242, 248); 
    private static final Color C_HEADER = new Color( 39,  78,  19); 

    
    private final int             idCiudadano;
    private final Panel_Principal panelPrincipal;
    private final CardLayout      cardLayout;
    private final JPanel          contenedorPrincipal;

    private String genero;
    private String estadoCiudadano;
    private String nombreCiudadano;

    /* UI */
    private JPanel  areaLibro;

   
    public Modulo2(CardLayout cardLayout, JPanel contenedor,
                   int idCiudadano, Panel_Principal panelPrincipal) {
        this.idCiudadano         = idCiudadano;
        this.panelPrincipal      = panelPrincipal;
        this.cardLayout          = cardLayout;
        this.contenedorPrincipal = contenedor;

        cargarDatosCiudadano();

        setBackground(FONDO);
        setLayout(new BorderLayout());

        add(crearBarra(), BorderLayout.NORTH);

        areaLibro = new JPanel(new BorderLayout());
        areaLibro.setBackground(FONDO);
        add(areaLibro, BorderLayout.CENTER);

        // Construir la cartilla de inmediato
        construirLibro();

        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) {
                construirLibro();
            }
        });
    }

    /* 
       BARRA SUPERIOR
       Izquierda : [← Regresar]  "Cartilla de Vacunación"
    */
    private JPanel crearBarra() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(GUINDO);
        barra.setBorder(new EmptyBorder(10, 20, 10, 20));

        // ── Izquierda ──
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        izq.setBackground(GUINDO);

        JButton btnReg = btnGuindo("← Regresar al menú", GUINDO_DARK);
        btnReg.addActionListener(e -> cardLayout.show(contenedorPrincipal, "menu"));

        JLabel lblTit = new JLabel("Cartilla de Vacunación");
        lblTit.setFont(new Font("Arial", Font.BOLD, 18));
        lblTit.setForeground(new Color(230, 210, 170));

        izq.add(btnReg);
        izq.add(lblTit);

        barra.add(izq, BorderLayout.WEST);
        return barra;
    }

    /* 
       LIBRO ABIERTO — dos páginas como la cartilla física
    */
    private void construirLibro() {
        areaLibro.removeAll();

        // Obtener datos de vacunas aplicadas
        Map<String, AplicacionInfo> datosBD = obtenerDatosAplicacion();

        // Panel exterior con scroll
        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setBackground(FONDO);
        envoltorio.setBorder(new EmptyBorder(18, 20, 18, 20));

        // ── Encabezado del ciudadano ──────────────────────────────────────
        JPanel encCiudadano = crearEncabezadoCiudadano();
        envoltorio.add(encCiudadano, BorderLayout.NORTH);

        // ── Libro (dos páginas juntas) ────────────────────────────────────
        JPanel libro = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight(), cx = w/2;

                // Sombra del libro
                g2.setColor(new Color(0,0,0,28));
                g2.fillRoundRect(8, 8, w-8, h-4, 14, 14);

                // Tapa izquierda (página par)
                g2.setColor(PAGINA);
                g2.fillRoundRect(0, 0, cx, h, 10, 10);
                g2.setColor(new Color(200,190,175));
                g2.drawRoundRect(0, 0, cx, h-1, 10, 10);

                // Tapa derecha (página impar)
                g2.setColor(new Color(250,249,244));
                g2.fillRoundRect(cx, 0, w-cx, h, 10, 10);
                g2.setColor(new Color(200,190,175));
                g2.drawRoundRect(cx, 0, w-cx-1, h-1, 10, 10);

                // Lomo central
                GradientPaint gp = new GradientPaint(cx-8,0, new Color(180,168,150),
                                                      cx+8,0, new Color(220,210,195));
                g2.setPaint(gp);
                g2.fillRect(cx-8, 0, 16, h);
                // Línea de unión
                g2.setColor(new Color(155,140,120));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawLine(cx-1, 0, cx-1, h);
                g2.drawLine(cx+1, 0, cx+1, h);

                g2.dispose();
            }
        };
        libro.setLayout(new GridLayout(1, 2, 0, 0));
        libro.setOpaque(false);
        libro.setBorder(new EmptyBorder(0,0,0,0));

        // Página izquierda
        libro.add(crearPaginaIzquierda(datosBD));
        // Página derecha
        libro.add(crearPaginaDerecha(datosBD));

        envoltorio.add(libro, BorderLayout.CENTER);

        // Notas al pie
        JPanel notas = crearNotasPie();
        envoltorio.add(notas, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(envoltorio);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        areaLibro.add(scroll, BorderLayout.CENTER);
        areaLibro.revalidate();
        areaLibro.repaint();
    }

    /* ── Encabezado con nombre y género del ciudadano ── */
    private JPanel crearEncabezadoCiudadano() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0, VERDE_OSC, getWidth(),0, VERDE_MED);
                g2.setPaint(gp);
                g2.fillRoundRect(0,0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        p.setLayout(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(12, 20, 12, 20));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Icono género + nombre
        String iconoGen = "Masculino".equals(genero) ? "♂" : "♀";
        String tipo     = "Masculino".equals(genero) ? "Hombre" : "Mujer";
        Calendar cal = Calendar.getInstance();
        String fecha = String.format("%02d/%02d/%04d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR));

        JLabel lblNom = new JLabel(iconoGen + "  Cartilla de Vacunación — " + tipo + " (20 a 59 años)   |   " + nombre());
        lblNom.setFont(new Font("Arial", Font.BOLD, 15));
        lblNom.setForeground(Color.WHITE);

        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setFont(new Font("Arial", Font.PLAIN, 12));
        lblFecha.setForeground(new Color(200,230,190));

        p.add(lblNom,   BorderLayout.WEST);
        p.add(lblFecha, BorderLayout.EAST);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(FONDO);
        wrap.setBorder(new EmptyBorder(0,0,12,0));
        wrap.add(p, BorderLayout.CENTER);
        return wrap;
    }

    /* 
       PÁGINA IZQUIERDA — SR, Td (y Tdpa para mujer)
    */
    private JPanel crearPaginaIzquierda(Map<String, AplicacionInfo> bd) {
        JPanel pag = new JPanel();
        pag.setLayout(new BoxLayout(pag, BoxLayout.Y_AXIS));
        pag.setOpaque(false);
        pag.setBorder(new EmptyBorder(16, 16, 16, 10));

        // Tabla SR
        pag.add(crearCabeceraBanda("SR", C_SR,
            "Hasta los 39 años de edad que no han sido vacunados o tienen esquema incompleto",
            "Sarampión y Rubéola"));
        pag.add(crearTablaVacuna(filasSR(), C_SR, bd, "SR"));
        pag.add(Box.createVerticalStrut(10));

        // Tabla Td
        pag.add(crearCabeceraBanda("Td", C_TD, "", "Tétanos y Difteria"));
        pag.add(crearTablaVacuna(filasTd(), C_TD, bd, "Td"));

        // Tdpa (solo mujer)
        if ("Femenino".equals(genero)) {
            pag.add(Box.createVerticalStrut(10));
            pag.add(crearCabeceraBanda("Tdpa", C_TDPA, "", "Tétanos, Difteria y Tosferina"));
            pag.add(crearTablaVacuna(filasTdpa(), C_TDPA, bd, "Tdpa"));
        }

        return pag;
    }

    /* 
       PÁGINA DERECHA — Influenza, COVID-19, Otras
     */
    private JPanel crearPaginaDerecha(Map<String, AplicacionInfo> bd) {
        JPanel pag = new JPanel();
        pag.setLayout(new BoxLayout(pag, BoxLayout.Y_AXIS));
        pag.setOpaque(false);
        pag.setBorder(new EmptyBorder(16, 10, 16, 16));

        // Influenza
        pag.add(crearCabeceraBanda("Influenza Estacional", C_FLU, "",
            "Neumonía por virus de la Influenza A y B"));
        pag.add(crearTablaVacuna(filasInfluenza(), C_FLU, bd, "Influenza Estacional"));
        pag.add(Box.createVerticalStrut(10));

        // COVID-19
        pag.add(crearCabeceraBanda("COVID-19 **", C_COV, "",
            "Formas graves de la COVID-19"));
        pag.add(crearTablaVacuna(filesCovid(), C_COV, bd, "COVID-19"));
        pag.add(Box.createVerticalStrut(10));

        // Otras vacunas
        pag.add(crearCabeceraBanda("Otras vacunas", C_OTRAS, "", ""));
        pag.add(crearTablaVacuna(filasOtras(), C_OTRAS, bd, "Otras"));

        return pag;
    }

    /* ── Cabecera verde de grupo vacunal ── */
    private JPanel crearCabeceraBanda(String nombreVacuna, Color colorFila,
                                       String descripcion, String enfermedades) {
        // Fila de cabecera con degradado verde
        JPanel cab = new JPanel(new GridLayout(1, 5, 0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_HEADER);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        cab.setOpaque(false);
        cab.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cab.setBorder(BorderFactory.createMatteBorder(1,1,0,1, BORDE_TABLA));

        // Celda 1: nombre de la vacuna (con color propio)
        JPanel celdaVac = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(colorFila.darker());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        celdaVac.setOpaque(false);
        JLabel lblVac = new JLabel("<html><b>" + nombreVacuna + "</b></html>");
        lblVac.setFont(new Font("Arial", Font.BOLD, 11));
        lblVac.setForeground(Color.WHITE);
        lblVac.setBorder(new EmptyBorder(4,6,4,4));
        celdaVac.add(lblVac, BorderLayout.CENTER);

        cab.add(celdaVac);
        cab.add(celdaHeader("Enfermedades\nque previene"));
        cab.add(celdaHeader("Dosis"));
        cab.add(celdaHeader("Edad de vacunación oportuna\ny grupo de intervención"));

        // Últimas 2 columnas: subdivididas
        JPanel ultimas = new JPanel(new GridLayout(1,2,0,0));
        ultimas.setOpaque(false);
        ultimas.add(celdaHeader("Fecha de\naplicación"));
        ultimas.add(celdaHeader("Lote de\nla vacuna"));
        cab.add(ultimas);

        if (!descripcion.isEmpty()) {
            // Sub-cabecera con descripción de la vacuna
            JPanel sub = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(colorFila.darker());
                    g.fillRect(0,0,getWidth(),getHeight());
                }
            };
            sub.setOpaque(false);
            sub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            sub.setBorder(BorderFactory.createMatteBorder(0,1,0,1, BORDE_TABLA));
            JLabel lblDesc = new JLabel("<html><i>&nbsp;" + descripcion + "</i></html>");
            lblDesc.setFont(new Font("Arial", Font.ITALIC, 10));
            lblDesc.setForeground(new Color(240,240,240));
            sub.add(lblDesc, BorderLayout.CENTER);

            JPanel stack = new JPanel();
            stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
            stack.setOpaque(false);
            stack.add(cab);
            stack.add(sub);
            stack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
            return stack;
        }

        return cab;
    }

    private JPanel celdaHeader(String texto) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(0,1,0,0, new Color(80,120,60)));
        JLabel lbl = new JLabel("<html><center>" + texto.replace("\n","<br>") + "</center></html>");
        lbl.setFont(new Font("Arial", Font.BOLD, 10));
        lbl.setForeground(Color.WHITE);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(new EmptyBorder(3,4,3,4));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    /* ── Tabla de filas de una vacuna ── */
    private JPanel crearTablaVacuna(Object[][] filas, Color colorBase,
                                     Map<String, AplicacionInfo> bd, String nombreVacuna) {
        JPanel tabla = new JPanel(new GridLayout(filas.length, 1, 0, 0));
        tabla.setOpaque(false);
        tabla.setBorder(BorderFactory.createMatteBorder(0,1,1,1, BORDE_TABLA));
        tabla.setMaximumSize(new Dimension(Integer.MAX_VALUE, filas.length * 26));

        // Obtener la información de aplicación para esta vacuna
        AplicacionInfo appInfo = bd.get(nombreVacuna);
        
        // Determinar en qué fila debe ir la aplicación (según la dosis)
        int filaAplicacion = -1;
        if (appInfo != null && !appInfo.dosis.isEmpty()) {
            for (int i = 0; i < filas.length; i++) {
                String dosisFila = (String) filas[i][2];
                if (dosisFila != null && dosisFila.equals(appInfo.dosis)) {
                    filaAplicacion = i;
                    break;
                }
            }
            // Si no encuentra coincidencia exacta, buscar por tipo de esquema
            if (filaAplicacion == -1) {
                for (int i = 0; i < filas.length; i++) {
                    String dosisFila = (String) filas[i][2];
                    if (dosisFila != null && (dosisFila.contains(appInfo.dosis) || appInfo.dosis.contains(dosisFila))) {
                        filaAplicacion = i;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < filas.length; i++) {
            String vacuna     = (String) filas[i][0];
            String enfermedad = (String) filas[i][1];
            String dosis      = (String) filas[i][2];
            String edad       = (String) filas[i][3];
            
            boolean esVacia = vacuna.isEmpty() && dosis.isEmpty();
            Color bgFila = esVacia
                ? (i%2==0 ? colorBase : new Color(colorBase.getRed(),colorBase.getGreen(),
                                                   colorBase.getBlue(), 160))
                : colorBase;

            String fecha = "";
            String lote = "";
            
            // Si esta es la fila que tiene la aplicación, mostrar los datos
            if (appInfo != null && (i == filaAplicacion || filaAplicacion == -1)) {
                fecha = appInfo.fecha;
                lote = appInfo.lote;
            }

            tabla.add(crearFila(bgFila, vacuna, enfermedad, dosis, edad, fecha, lote));
        }
        return tabla;
    }

    /* ── Fila individual de la cartilla ── */
    private JPanel crearFila(Color bg, String vacuna, String enfermedad, String dosis,
                              String edad, String fecha, String lote) {
        JPanel fila = new JPanel(new GridLayout(1, 5, 0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(bg);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createMatteBorder(1,0,0,0,
            new Color(BORDE_TABLA.getRed(), BORDE_TABLA.getGreen(),
                      BORDE_TABLA.getBlue(), 120)));
        fila.setPreferredSize(new Dimension(0, 60));

        // Col 0: vacuna
        fila.add(celdaTexto(vacuna, Font.ITALIC, true));
        // Col 1: enfermedades
        fila.add(celdaTexto(enfermedad, Font.PLAIN, false));
        // Col 2: dosis
        fila.add(celdaTexto(dosis, Font.PLAIN, false));
        // Col 3: edad/grupo
        fila.add(celdaTexto(edad, Font.PLAIN, false));

        // Col 4: fecha + lote (dos celdas)
        JPanel ultimas = new JPanel(new GridLayout(1,2,0,0));
        ultimas.setOpaque(false);

        JPanel celdaFecha = celdaTextoConEstado(fecha, !fecha.isEmpty());
        ultimas.add(celdaFecha);

        JPanel celdaLote = celdaTextoConEstado(lote, !lote.isEmpty());
        ultimas.add(celdaLote);

        fila.add(ultimas);
        return fila;
    }

    private JPanel celdaTexto(String texto, int estilo, boolean borde) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        if (borde) p.setBorder(BorderFactory.createMatteBorder(0,0,0,1,
            new Color(BORDE_TABLA.getRed(), BORDE_TABLA.getGreen(),
                      BORDE_TABLA.getBlue(), 90)));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", estilo, 10));
        lbl.setForeground(new Color(40,40,40));
        lbl.setBorder(new EmptyBorder(0,4,0,4));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    /* Celda con color verde si ya tiene dato (vacuna aplicada) */
    private JPanel celdaTextoConEstado(String texto, boolean aplicada) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (aplicada) {
                    g.setColor(new Color(210, 240, 210));
                    g.fillRect(0,0,getWidth(),getHeight());
                }
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(0,1,0,0,
            new Color(BORDE_TABLA.getRed(), BORDE_TABLA.getGreen(),
                      BORDE_TABLA.getBlue(), 80)));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", aplicada ? Font.BOLD : Font.PLAIN, 10));
        lbl.setForeground(aplicada ? new Color(0,100,0) : new Color(40,40,40));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(new EmptyBorder(0,3,0,3));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    /* 
       DEFINICIÓN DE FILAS POR VACUNA
    */

    private Object[][] filasSR() {
        return new Object[][]{
            {"SR", "Sarampión y Rubéola", "Sin antecedente vacunal", "En el primer contacto"},
            {"", "", "Sin antecedente vacunal", "<html>4 semanas después<br>de la primera dosis</html>"},
            {"", "", "Con esquema incompleto", "En el primer contacto"}
        };
    }

    private Object[][] filasTd() {
        return new Object[][]{
            {"Td", "Tétanos y Difteria", "Con esq. completo", "Cada 10 años"},
            {"", "", "Con esq. incompleto", "Dosis inicial"},
            {"", "", "Con esq. incompleto", "<html>1 mes después<br>de la primera dosis</html>"},
            {"", "", "Con esq. incompleto", "<html>12 meses posteriores<br>a la primera dosis</html>"}
        };
    }

    private Object[][] filasTdpa() {
        return new Object[][]{
            {"Tdpa", "Tétanos, Difteria y Tosferina", "Única", "<html>A partir de la semana 20<br>del embarazo</html>"},
            {"", "", "", ""},
            {"", "", "", ""}
        };
    }

    private Object[][] filasInfluenza() {
        return new Object[][]{
            {"Influenza Estacional", "Neumonía por virus de la influenza A y B", "Dosis Anual (temporada invernal)", "Cualquier trimestre del embarazo"},
            {"", "", "", ""},
            {"", "", "", ""},
            {"", "", "", "Personas con factores de riesgo"},
            {"", "", "", ""},
            {"", "", "", ""},
            {"", "", "", ""},
            {"", "", "", ""}
        };
    }

    private Object[][] filesCovid() {
        return new Object[][]{
            {"COVID-19", "Formas graves de la COVID-19", "Refuerzo", 
             "<html>12 meses después de la última dosis</html>"},
            {"", "", "", ""},
            {"", "", "", ""}
        };
    }

    private Object[][] filasOtras() {
        return new Object[][]{
            {"Hepatitis A", "Hepatitis A", "2 dosis", "Mayores de 18 años"},
            {"", "", "", ""},
            {"", "", "", ""},
            {"", "", "", ""},
            {"", "", "", ""}
        };
    }

    /* ── Notas al pie de la cartilla ── */
    private JPanel crearNotasPie() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(FONDO);
        p.setBorder(new EmptyBorder(8, 0, 0, 0));

        String[] notas = {
            "*  De conformidad con los Lineamientos Generales de Vacunación vigentes.",
            "** Esquema sujeto a los lineamientos vigentes según tipo de vacuna contra la COVID-19.",
            "*** A fin de disminuir las oportunidades perdidas, el intervalo mínimo podrá ser de 4 meses."
        };
        for (String n : notas) {
            JLabel lbl = new JLabel("<html><i>" + n + "</i></html>");
            lbl.setFont(new Font("Arial", Font.PLAIN, 9));
            lbl.setForeground(new Color(100,100,100));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(lbl);
            p.add(Box.createVerticalStrut(1));
        }
        return p;
    }

    /* 
       CLASE PARA ALMACENAR INFORMACIÓN DE APLICACIÓN
    */
    class AplicacionInfo {
        String fecha;
        String lote;
        String dosis;
        
        AplicacionInfo(String fecha, String lote, String dosis) {
            this.fecha = fecha;
            this.lote = lote;
            this.dosis = dosis;
        }
    }

    /* 
       BASE DE DATOS
     */
    private void cargarDatosCiudadano() {
        String sql = "SELECT nombre, apellido, genero, estado FROM ciudadanos WHERE id_ciudadano = ?";
        try (Connection con = Conexion_DB.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCiudadano);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nombreCiudadano = rs.getString("nombre") + " " + rs.getString("apellido");
                genero          = rs.getString("genero");
                estadoCiudadano = rs.getString("estado");
            }
        } catch (SQLException e) {
            genero = "Masculino"; estadoCiudadano = null; nombreCiudadano = "";
        }
    }

    private String nombre() { return nombreCiudadano != null ? nombreCiudadano : ""; }

  private Map<String, AplicacionInfo> obtenerDatosAplicacion() {
        Map<String, AplicacionInfo> mapa = new LinkedHashMap<>();
        String sql = 
            "SELECT cv.nombre AS campaña, " +
            "       DATE_FORMAT(av.fecha, '%d/%m/%Y') AS fecha_aplicacion, " +
            "       av.lote AS lote_vacuna " +
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
                String nombreCampana = rs.getString("campaña");
                String fecha = rs.getString("fecha_aplicacion");
                String lote = rs.getString("lote_vacuna");

                // Mapear el nombre de la campaña a la vacuna
                VacunaInfo vacunaInfo = mapearCampanaAVacunaCompleto(nombreCampana);

                if (vacunaInfo != null) {
                    // Para "Otras", podemos concatenar múltiples vacunas o sobrescribir
                    // Usamos put para que la última prevalezca, o podríamos concatenar
                    String nombreUI = vacunaInfo.nombreUI;

                    // Si ya existe una aplicación para esta categoría, podrías concatenar
                    if (mapa.containsKey(nombreUI) && nombreUI.equals("Otras")) {
                        AplicacionInfo existing = mapa.get(nombreUI);
                        // Concatenar múltiples vacunas en "Otras"
                        String nuevasFechas = existing.fecha + " | " + fecha;
                        String nuevosLotes = existing.lote + " | " + lote;
                        mapa.put(nombreUI, new AplicacionInfo(nuevasFechas, nuevosLotes, vacunaInfo.dosis));
                    } else {
                        mapa.put(nombreUI, new AplicacionInfo(
                            fecha != null ? fecha : "",
                            lote != null ? lote : "",
                            vacunaInfo.dosis
                        ));
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error datos aplicación: " + ex.getMessage());
            ex.printStackTrace();
        }
        return mapa;
    }

    /* 
       Clase para almacenar información completa de la vacuna
    */
    class VacunaInfo {
        String nombreUI;
        String dosis;
        
        VacunaInfo(String nombreUI, String dosis) {
            this.nombreUI = nombreUI;
            this.dosis = dosis;
        }
    }

    private VacunaInfo mapearCampanaAVacunaCompleto(String nombreCampana) {
        if (nombreCampana == null) return null;

        String nombreLower = nombreCampana.toLowerCase();

        // SR (Sarampión y Rubéola)
        if (nombreLower.contains("sr") || (nombreLower.contains("sarampión") && nombreLower.contains("rubéola"))) {
            return new VacunaInfo("SR", "Sin antecedente vacunal");
        }

        // Td (Tétanos y Difteria)
        if (nombreLower.contains("tétanos") && nombreLower.contains("difteria") && !nombreLower.contains("tosferina")) {
            return new VacunaInfo("Td", "Con esq. completo");
        }

        // Tdpa (Tétanos, Difteria y Tosferina)
        if (nombreLower.contains("tosferina") || (nombreLower.contains("tétanos") && nombreLower.contains("tosferina"))) {
            if ("Femenino".equals(genero)) {
                return new VacunaInfo("Tdpa", "Única");
            }
        }

        // Influenza
        if (nombreLower.contains("influenza")) {
            return new VacunaInfo("Influenza Estacional", "Dosis Anual (temporada invernal)");
        }

        // COVID-19
        if (nombreLower.contains("covid") || nombreLower.contains("refuerzo covid")) {
            return new VacunaInfo("COVID-19", "Refuerzo");
        }

        // TODO LO DEMÁS va a OTRAS VACUNAS
        // Hepatitis A, Hepatitis B, Neumococo, VPH, etc.
        if (nombreLower.contains("hepatitis a")) {
            return new VacunaInfo("Otras", "2 dosis");
        }

        if (nombreLower.contains("hepatitis b")) {
            return new VacunaInfo("Otras", "3 dosis");
        }

        if (nombreLower.contains("neumococo") || nombreLower.contains("prevenar")) {
            return new VacunaInfo("Otras", "Esquema completo");
        }

        if (nombreLower.contains("vph") || nombreLower.contains("gardasil")) {
            return new VacunaInfo("Otras", "2-3 dosis");
        }

        // Cualquier otra vacuna no categorizada
        if (!nombreLower.isEmpty()) {
            return new VacunaInfo("Otras", "Según esquema");
        }

        return null;
    }

    /* ── Botón guindo reutilizable ── */
    private JButton btnGuindo(String texto, Color bgNormal) {
        JButton btn = new JButton(texto);
        btn.setBackground(bgNormal);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){ btn.setBackground(GUINDO_HOVER); }
            public void mouseExited (MouseEvent e){ btn.setBackground(bgNormal); }
        });
        return btn;
    }
}