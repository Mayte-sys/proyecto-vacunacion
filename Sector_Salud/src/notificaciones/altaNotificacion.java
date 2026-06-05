package notificaciones;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import sector_salud.Conexion_DB;

public class altaNotificacion extends JPanel {
    
    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color FONDO = new Color(245, 245, 248);
    
    private final CardLayout cardLayout;
    private final JPanel contenedor;
    
    private JTextField txtTitulo;
    private JTextArea txtMensaje;
    private JComboBox<String> cbTipo;
    private JList<String> listaEstados;
    private JLabel lblEstadosSeleccionados;
    
    private final String[] ESTADOS_MEXICO = {
        "Aguascalientes", "Baja California", "Baja California Sur", "Campeche",
        "Chiapas", "Chihuahua", "Ciudad de México", "Coahuila", "Colima",
        "Durango", "Estado de México", "Guanajuato", "Guerrero", "Hidalgo",
        "Jalisco", "Michoacán", "Morelos", "Nayarit", "Nuevo León", "Oaxaca",
        "Puebla", "Querétaro", "Quintana Roo", "San Luis Potosí", "Sinaloa",
        "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala", "Veracruz", "Yucatán", "Zacatecas"
    };
    
    public altaNotificacion(CardLayout cardLayout, JPanel contenedor) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        
        txtTitulo = new JTextField(35);
        txtMensaje = new JTextArea(5, 35);
        cbTipo = new JComboBox<>(new String[]{"Selecciona un tipo","Brote", "Alerta", "Informacion"});
        
        configurarUI();
    }
    
    private void configurarUI() {
        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JLabel titulo = new JLabel("Nueva Notificación");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);
        
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int y = 0;
        
        // Título
        gbc.gridx = 0; gbc.gridy = y;
        formulario.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1;
        formulario.add(txtTitulo, gbc);
        y++;
        
        // Mensaje
        gbc.gridx = 0; gbc.gridy = y;
        formulario.add(new JLabel("Mensaje:"), gbc);
        gbc.gridx = 1;
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        JScrollPane scrollMensaje = new JScrollPane(txtMensaje);
        scrollMensaje.setPreferredSize(new Dimension(400, 100));
        formulario.add(scrollMensaje, gbc);
        y++;
        
        // Tipo
        gbc.gridx = 0; gbc.gridy = y;
        formulario.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        formulario.add(cbTipo, gbc);
        y++;
        
        // Estados (selección múltiple)
        gbc.gridx = 0; gbc.gridy = y;
        JLabel lblEstados = new JLabel("Estados (Ctrl+Click):");
        lblEstados.setFont(new Font("Arial", Font.BOLD, 12));
        formulario.add(lblEstados, gbc);
        gbc.gridx = 1;
        
        DefaultListModel<String> modelEstados = new DefaultListModel<>();
        for (String estado : ESTADOS_MEXICO) {
            modelEstados.addElement(estado);
        }
        listaEstados = new JList<>(modelEstados);
        listaEstados.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaEstados.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarLabelEstados();
        });
        
        JScrollPane scrollEstados = new JScrollPane(listaEstados);
        scrollEstados.setPreferredSize(new Dimension(400, 150));
        scrollEstados.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formulario.add(scrollEstados, gbc);
        y++;
        
        // Label resumen
        gbc.gridx = 0; gbc.gridy = y;
        formulario.add(new JLabel("Seleccionados:"), gbc);
        gbc.gridx = 1;
        lblEstadosSeleccionados = new JLabel("Ninguno");
        lblEstadosSeleccionados.setFont(new Font("Arial", Font.ITALIC, 11));
        lblEstadosSeleccionados.setForeground(Color.GRAY);
        formulario.add(lblEstadosSeleccionados, gbc);
        y++;
        
        // Botones
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        pnlBotones.setBackground(FONDO);
        
        JButton btnRegresar = new JButton("Regresar");
        btnRegresar.setBackground(GUINDO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "notificacion_menu"));
        pnlBotones.add(btnRegresar);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(GUINDO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setPreferredSize(new Dimension(200, 40));
        btnGuardar.addActionListener(e -> guardarNotificacion());
        pnlBotones.add(btnGuardar);
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(GUINDO);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        pnlBotones.add(btnLimpiar);
        
        formulario.add(pnlBotones, gbc);
        
        add(new JScrollPane(formulario), BorderLayout.CENTER);
    }
    
    private void actualizarLabelEstados() {
        List<String> seleccionados = listaEstados.getSelectedValuesList();
        if (seleccionados.isEmpty()) {
            lblEstadosSeleccionados.setText("Ninguno");
            lblEstadosSeleccionados.setForeground(Color.GRAY);
        } else if (seleccionados.size() <= 3) {
            lblEstadosSeleccionados.setText(String.join(", ", seleccionados));
            lblEstadosSeleccionados.setForeground(new Color(40, 167, 69));
        } else {
            lblEstadosSeleccionados.setText(seleccionados.size() + " estados seleccionados");
            lblEstadosSeleccionados.setForeground(new Color(40, 167, 69));
        }
    }
    
    private void guardarNotificacion() {
        String titulo = txtTitulo.getText().trim();
        String mensaje = txtMensaje.getText().trim();
        String tipo = (String) cbTipo.getSelectedItem();
        List<String> estadosSeleccionados = listaEstados.getSelectedValuesList();
        
        if (titulo.isEmpty() || mensaje.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete título y mensaje", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (estadosSeleccionados.isEmpty()) {
            int respuesta = JOptionPane.showConfirmDialog(this, 
                "No seleccionó estados. ¿Enviar a todo México?", 
                "Estados no seleccionados", 
                JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) return;
            estadosSeleccionados = Arrays.asList(ESTADOS_MEXICO);
        }
        
        String estadosStr = String.join(",", estadosSeleccionados);
        
        // Ajustado a tu tabla: se insertan titulo, mensaje, tipo, estados
        // fecha y fecha_publicacion se asignan automáticamente con CURRENT_TIMESTAMP
        String sql = "INSERT INTO notificaciones (titulo, mensaje, tipo, estados) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, titulo);
            pstmt.setString(2, mensaje);
            pstmt.setString(3, tipo);
            pstmt.setString(4, estadosStr);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Notificación creada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limpiarFormulario() {
        txtTitulo.setText("");
        txtMensaje.setText("");
        cbTipo.setSelectedIndex(0);
        listaEstados.clearSelection();
        lblEstadosSeleccionados.setText("Ninguno");
        lblEstadosSeleccionados.setForeground(Color.GRAY);
    }
}