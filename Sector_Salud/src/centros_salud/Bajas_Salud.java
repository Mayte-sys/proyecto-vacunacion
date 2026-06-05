/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package centros_salud;

/**
 *
 * @author marga
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import sector_salud.Conexion_DB;

public class Bajas_Salud extends JPanel {
    
    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    private static final Color ROJO = new Color(180, 50, 50);
    private static final Color ROJO_HOVER = new Color(200, 70, 70);
    
    private JTextField txtBuscar;
    private JButton btnBuscar, btnEliminar, btnLimpiar;
    private JTextArea txtResultado;
    
    private CardLayout cardLayout;
    private JPanel contenedor;
    
    private int centroEncontradoId = -1;
    private String centroEncontradoNombre = "";
    
    public Bajas_Salud(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Bajas de Centros de Salud");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);
        
        // Panel central
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(FONDO);
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBusqueda.setBackground(FONDO);
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar Centro de Salud"));
        
        JLabel lblBuscar = new JLabel("Nombre del Centro:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 14));
        
        txtBuscar = new JTextField(30);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(GUINDO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> buscarCentro());
        
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        
        centro.add(panelBusqueda, BorderLayout.NORTH);
        
        // Área de resultado
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtResultado.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(txtResultado);
        scroll.setBorder(BorderFactory.createTitledBorder("Información del Centro de Salud"));
        centro.add(scroll, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBotones.setBackground(FONDO);
        
        btnEliminar = new JButton("Eliminar Centro");
        btnEliminar.setBackground(ROJO);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setPreferredSize(new Dimension(180, 40));
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminarCentro());
        
        btnEliminar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                if (btnEliminar.isEnabled()) {
                    btnEliminar.setBackground(ROJO_HOVER); 
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) { 
                if (btnEliminar.isEnabled()) {
                    btnEliminar.setBackground(ROJO); 
                }
            }
        });
        
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(Color.GRAY);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setFont(new Font("Arial", Font.BOLD, 14));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.setPreferredSize(new Dimension(120, 40));
        btnLimpiar.addActionListener(e -> limpiarBusqueda());
        
        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.setBackground(GUINDO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.setPreferredSize(new Dimension(120, 40));
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo3"));
        
        btnRegresar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                btnRegresar.setBackground(GUINDO_HOVER); 
            }
            public void mouseExited(java.awt.event.MouseEvent e) { 
                btnRegresar.setBackground(GUINDO); 
            }
        });
        
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRegresar);
        
        centro.add(panelBotones, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
    }
    
    private void buscarCentro() {
        String nombreBuscar = txtBuscar.getText().trim();
        
        if (nombreBuscar.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese el nombre del centro de salud para buscar", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_centro, nombre, calle, numero_exterior, numero_interior, " +
                        "colonia, municipio, estado, codigo_postal, telefono, " +
                        "horario_apertura, horario_cierre, activo " +
                        "FROM centros_salud WHERE nombre LIKE ?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + nombreBuscar + "%");
            ResultSet rs = pstmt.executeQuery();
            
            StringBuilder resultado = new StringBuilder();
            int contador = 0;
            
            while (rs.next()) {
                contador++;
                int id = rs.getInt("id_centro");
                String nombre = rs.getString("nombre");
                
                resultado.append("╔════════════════════════════════════════════════════════════════╗\n");
                resultado.append("║                    CENTRO DE SALUD #").append(String.format("%-4d", contador)).append("                         ║\n");
                resultado.append("╠════════════════════════════════════════════════════════════════╣\n");
                resultado.append("║ ID Centro:      ").append(String.format("%-40s", id)).append("║\n");
                resultado.append("║ Nombre:         ").append(String.format("%-40s", truncar(nombre, 40))).append("║\n");
                resultado.append("║────────────────────────────────────────────────────────────────║\n");
                resultado.append("║ DIRECCIÓN:                                                        ║\n");
                resultado.append("║ Calle:          ").append(String.format("%-40s", truncar(rs.getString("calle"), 40))).append("║\n");
                resultado.append("║ Núm. Exterior:  ").append(String.format("%-40s", rs.getString("numero_exterior"))).append("║\n");
                
                String numInt = rs.getString("numero_interior");
                if (numInt != null && !numInt.isEmpty()) {
                    resultado.append("║ Núm. Interior:  ").append(String.format("%-40s", numInt)).append("║\n");
                }
                
                resultado.append("║ Colonia:        ").append(String.format("%-40s", truncar(rs.getString("colonia"), 40))).append("║\n");
                resultado.append("║ Municipio:      ").append(String.format("%-40s", truncar(rs.getString("municipio"), 40))).append("║\n");
                resultado.append("║ Estado:         ").append(String.format("%-40s", rs.getString("estado"))).append("║\n");
                resultado.append("║ C.P.:           ").append(String.format("%-40s", rs.getString("codigo_postal"))).append("║\n");
                resultado.append("║────────────────────────────────────────────────────────────────║\n");
                resultado.append("║ CONTACTO:                                                         ║\n");
                resultado.append("║ Teléfono:       ").append(String.format("%-40s", rs.getString("telefono"))).append("║\n");
                resultado.append("║────────────────────────────────────────────────────────────────║\n");
                resultado.append("║ HORARIO:                                                          ║\n");
                resultado.append("║ Apertura:       ").append(String.format("%-40s", rs.getTime("horario_apertura"))).append("║\n");
                resultado.append("║ Cierre:         ").append(String.format("%-40s", rs.getTime("horario_cierre"))).append("║\n");
                resultado.append("║────────────────────────────────────────────────────────────────║\n");
                resultado.append("║ ESTADO:                                                           ║\n");
                
                boolean activo = rs.getBoolean("activo");
                if (activo) {
                    resultado.append("║ Estado:         ").append(String.format("%-40s", "ACTIVO")).append("║\n");
                } else {
                    resultado.append("║ Estado:         ").append(String.format("%-40s", "INACTIVO")).append("║\n");
                }
                
                resultado.append("╚════════════════════════════════════════════════════════════════╝\n\n");
            }
            
            rs.close();
            pstmt.close();
            
            if (contador == 0) {
                txtResultado.setText("No se encontró ningún centro de salud con el nombre: " + nombreBuscar);
                btnEliminar.setEnabled(false);
                centroEncontradoId = -1;
            } else if (contador == 1) {
                // Si solo hay un resultado, guardar el ID para eliminación directa
                // Volver a buscar para obtener el ID específico
                String sqlExacto = "SELECT id_centro, nombre FROM centros_salud WHERE nombre LIKE ? LIMIT 1";
                PreparedStatement pstmtExacto = conn.prepareStatement(sqlExacto);
                pstmtExacto.setString(1, "%" + nombreBuscar + "%");
                ResultSet rsExacto = pstmtExacto.executeQuery();
                if (rsExacto.next()) {
                    centroEncontradoId = rsExacto.getInt("id_centro");
                    centroEncontradoNombre = rsExacto.getString("nombre");
                    btnEliminar.setEnabled(true);
                }
                rsExacto.close();
                pstmtExacto.close();
                txtResultado.setText(resultado.toString());
            } else {
                txtResultado.setText(resultado.toString());
                JOptionPane.showMessageDialog(this, 
                    "Se encontraron " + contador + " centros.\n" +
                    "Escriba el nombre completo para seleccionar uno específico.", 
                    "Múltiples resultados", JOptionPane.INFORMATION_MESSAGE);
                btnEliminar.setEnabled(false);
                centroEncontradoId = -1;
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al buscar: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void eliminarCentro() {
        if (centroEncontradoId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Primero busque un centro de salud para eliminar", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de dar de baja el centro de salud?\n\n" +
            "ID: " + centroEncontradoId + "\n" +
            "Nombre: " + centroEncontradoNombre + "\n\n" +
            "El centro quedará como INACTIVO pero se conservará en la base de datos.", 
            "Confirmar baja", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Conexion_DB.obtenerConexion()) {
                String sql = "UPDATE centros_salud SET activo = false WHERE id_centro = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, centroEncontradoId);
                int filasAfectadas = pstmt.executeUpdate();

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Centro de Salud dado de baja exitosamente:\n" +
                        "ID: " + centroEncontradoId + "\n" +
                        "Nombre: " + centroEncontradoNombre, 
                        "Baja realizada", JOptionPane.INFORMATION_MESSAGE);

                    limpiarBusqueda();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se pudo dar de baja el centro de salud", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                pstmt.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al dar de baja: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void limpiarBusqueda() {
        txtBuscar.setText("");
        txtResultado.setText("");
        btnEliminar.setEnabled(false);
        centroEncontradoId = -1;
        centroEncontradoNombre = "";
    }
    
    private String truncar(String texto, int maxLargo) {
        if (texto == null) return "";
        if (texto.length() <= maxLargo) return texto;
        return texto.substring(0, maxLargo - 3) + "...";
    }
}