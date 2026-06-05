/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Folios;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import sector_salud.Conexion_DB;

public class Bajas extends JPanel {
    
    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color GUINDO_HOVER = new Color(140, 40, 62);
    private static final Color FONDO = new Color(245, 245, 248);
    
    private JComboBox<String> cbBuscarPor;
    private JTextField txtBuscar;
    private JTextArea txtResultado;
    private JButton btnBuscar, btnEliminar;
    private CardLayout cardLayout;
    private JPanel contenedor;
    
    private int loteEncontradoId = -1;
    private String loteEncontradoNombre = "";
    
    public Bajas(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Título
        JLabel titulo = new JLabel("Eliminar Lote Específico");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);
        
        // Panel central
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(FONDO);
        centro.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBusqueda.setBackground(FONDO);
        
        JLabel lblBuscarPor = new JLabel("Buscar por:");
        lblBuscarPor.setFont(new Font("Arial", Font.BOLD, 14));
        
        cbBuscarPor = new JComboBox<>(new String[]{"ID de Lote", "Nombre de Lote"});
        cbBuscarPor.setFont(new Font("Arial", Font.PLAIN, 14));
        
        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(GUINDO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 12));
        btnBuscar.addActionListener(e -> buscarLote());
        
        panelBusqueda.add(lblBuscarPor);
        panelBusqueda.add(cbBuscarPor);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        
        centro.add(panelBusqueda, BorderLayout.NORTH);
        
        // Área de resultado
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtResultado.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(txtResultado);
        scroll.setBorder(BorderFactory.createTitledBorder("Información del lote"));
        centro.add(scroll, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelBotones.setBackground(FONDO);
        
        btnEliminar = new JButton("Eliminar este lote");
        btnEliminar.setBackground(new Color(180, 50, 50));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setPreferredSize(new Dimension(200, 40));
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminarLote());
        
        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.setBackground(Color.GRAY);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.setPreferredSize(new Dimension(120, 40));
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo2"));
        
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRegresar);
        
        centro.add(panelBotones, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
    }
    
    private void buscarLote() {
        String criterio = txtBuscar.getText().trim();
        
        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un valor para buscar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql;
            PreparedStatement pstmt;
            
            if (cbBuscarPor.getSelectedIndex() == 0) {
                // Buscar por ID
                sql = "SELECT l.id_lote, l.lote_vacunacion, l.folio, l.empresa, " +
                      "l.nombre_comercial, l.fecha_caducidad, l.cantidad, l.enfermedades, p.nombre AS pais " +
                      "FROM lotes l " +
                      "JOIN paises p ON l.id_pais = p.id " +
                      "WHERE l.id_lote = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(criterio));
            } else {
                // Buscar por nombre de lote
                sql = "SELECT l.id_lote, l.lote_vacunacion, l.folio, l.empresa, " +
                      "l.nombre_comercial, l.fecha_caducidad, l.cantidad, l.enfermedades, p.nombre AS pais " +
                      "FROM lotes l " +
                      "JOIN paises p ON l.id_pais = p.id " +
                      "WHERE l.lote_vacunacion LIKE ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "%" + criterio + "%");
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                loteEncontradoId = rs.getInt("id_lote");
                loteEncontradoNombre = rs.getString("lote_vacunacion");
                
                StringBuilder info = new StringBuilder();
                info.append("=== LOTE ENCONTRADO ===\n\n");
                info.append("ID Lote: ").append(rs.getInt("id_lote")).append("\n");
                info.append("Lote de Vacunación: ").append(rs.getString("lote_vacunacion")).append("\n");
                info.append("Folio: ").append(rs.getString("folio")).append("\n");
                info.append("País: ").append(rs.getString("pais")).append("\n");
                info.append("Empresa: ").append(rs.getString("empresa")).append("\n");
                info.append("Nombre Comercial: ").append(rs.getString("nombre_comercial")).append("\n");
                info.append("Fecha de Caducidad: ").append(rs.getDate("fecha_caducidad")).append("\n");
                info.append("Cantidad: ").append(rs.getInt("cantidad")).append("\n");
                info.append("Enfermedades: ").append(rs.getString("enfermedades")).append("\n\n");
                
                // Verificar si está caducado
                java.sql.Date fechaCaducidad = rs.getDate("fecha_caducidad");
                java.sql.Date hoy = new java.sql.Date(System.currentTimeMillis());
                if (fechaCaducidad.before(hoy)) {
                    info.append("⚠️ ESTE LOTE ESTÁ CADUCADO ⚠️\n");
                }
                
                // Verificar si tiene cantidad 0
                if (rs.getInt("cantidad") == 0) {
                    info.append("⚠️ ESTE LOTE TIENE CANTIDAD 0 ⚠️\n");
                }
                
                txtResultado.setText(info.toString());
                btnEliminar.setEnabled(true);
                
            } else {
                txtResultado.setText("No se encontró ningún lote con ese criterio.");
                btnEliminar.setEnabled(false);
                loteEncontradoId = -1;
            }
            
            rs.close();
            pstmt.close();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido para ID", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void eliminarLote() {
        if (loteEncontradoId == -1) {
            JOptionPane.showMessageDialog(this, "Primero busque un lote para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar el lote?\n\n" +
            "ID: " + loteEncontradoId + "\n" +
            "Lote: " + loteEncontradoNombre + "\n\n" +
            "Esta acción no se puede deshacer.", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Conexion_DB.obtenerConexion()) {
                String sql = "DELETE FROM lotes WHERE id_lote = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, loteEncontradoId);
                int filasAfectadas = pstmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Lote eliminado exitosamente:\n" +
                        "ID: " + loteEncontradoId + "\n" +
                        "Lote: " + loteEncontradoNombre, 
                        "Eliminado", JOptionPane.INFORMATION_MESSAGE);
                    
                    txtResultado.setText("Lote eliminado correctamente.");
                    btnEliminar.setEnabled(false);
                    loteEncontradoId = -1;
                    txtBuscar.setText("");
                    
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el lote", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
                pstmt.close();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}