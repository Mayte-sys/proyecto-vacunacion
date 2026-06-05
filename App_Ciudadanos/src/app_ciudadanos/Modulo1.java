package app_ciudadanos; // ← único cambio respecto a los de Sector_Salud

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Modulo1 extends JPanel { // cambia a Modulo2, Modulo3, Modulo4

    private static final Color GUINDO       = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO        = new Color(245, 245, 248);


        public Modulo1(CardLayout cardLayout, JPanel contenedor, int idCiudadano) { // mismo nombre que la clase
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Historial de Vacunación"); //se cambio el titulo del modulo (en la app) 
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);

        // ══════════════════════════════════════════════
        //  DESARROLLAR AQUÍ el contenido del módulo
        // ══════════════════════════════════════════════
        //Se cambia el titulo
            add(titulo, BorderLayout.NORTH);

            //los encabezados para la tabla
            String[] columnas = {"#", "Lote", "Nombre Comercial", "Enfermedad(es)", "Centro de Salud", "Fecha de Aplicación"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // tabla de solo lectura
                }
            };

            //consultaa la BD, es un join directo
            String sql = "SELECT " +
                "av.id_aplicacion, " +
                "av.lote, " +
                "l.nombre_comercial, " +
                "l.enfermedades, " +
                "cv.nombre AS campana, " +
                "cs.nombre AS centro, " +
                "av.fecha " +
                "FROM aplicador_vacunas av " +
                "JOIN ciudadanos c ON av.curp_paciente = c.curp " +
                "JOIN campana_vacunacion cv ON av.id_campana = cv.id_campana " +
                "JOIN centros_salud cs ON av.id_centro = cs.id_centro " +
                "JOIN lotes l ON l.lote_vacunacion = av.lote " +
                "WHERE c.id_ciudadano = ? " +
                "ORDER BY av.fecha DESC";

            try (Connection con = Conexion_DB.obtenerConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, idCiudadano);
                ResultSet rs = ps.executeQuery();
                

                while (rs.next()) {
                    
                    modelo.addRow(new Object[]{
                        rs.getInt("id_aplicacion"),
                        rs.getString("lote"),
                        rs.getString("nombre_comercial"),
                        rs.getString("enfermedades"),
                        rs.getString("centro"),
                        rs.getTimestamp("fecha")
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar historial: " + ex.getMessage(),
                    "Error de BD", JOptionPane.ERROR_MESSAGE);
            }

            //crea y estiliza la tabla
            JTable tabla = new JTable(modelo);
            tabla.setRowHeight(28);
            tabla.getTableHeader().setBackground(GUINDO);
            tabla.getTableHeader().setForeground(Color.WHITE);
            tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
            tabla.setFont(new Font("Arial", Font.PLAIN, 12));
            tabla.setSelectionBackground(new Color(193, 154, 80, 80));
            tabla.setGridColor(new Color(220, 220, 220));
            tabla.setFillsViewportHeight(true);

            //ajustar ancho de la columna "#"
            tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
            tabla.getColumnModel().getColumn(0).setMaxWidth(60);

            //envolver en scroll (tipo de diseño)
            JScrollPane scroll = new JScrollPane(tabla);
            scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

            //mensaje si no hay vacunas (si se borra el registro actual, este mensaje se muestra)
            JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
            cuerpo.setBackground(FONDO);
            cuerpo.setBorder(new EmptyBorder(16, 0, 0, 0));

            if (modelo.getRowCount() == 0) {
                JLabel sinDatos = new JLabel("No se encontraron vacunas registradas.", SwingConstants.CENTER);
                sinDatos.setFont(new Font("Arial", Font.ITALIC, 14));
                sinDatos.setForeground(Color.GRAY);
                cuerpo.add(sinDatos, BorderLayout.CENTER);
            } else {
                cuerpo.add(scroll, BorderLayout.CENTER);
            }

            add(cuerpo, BorderLayout.CENTER);
        // ══════════════════════════════════════════════

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
            public void mouseExited(java.awt.event.MouseEvent e)  { btnRegresar.setBackground(GUINDO); }
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sur.setBackground(FONDO);
        sur.add(btnRegresar);
        add(sur, BorderLayout.SOUTH);
    }
}
