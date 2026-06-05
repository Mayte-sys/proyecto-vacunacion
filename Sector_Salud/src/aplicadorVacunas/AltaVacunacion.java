package aplicadorVacunas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import sector_salud.Conexion_DB;

public class AltaVacunacion extends JPanel {

    private static final Color GUINDO = new Color(109, 27, 46);
    private static final Color FONDO = new Color(245, 245, 248);

    private JComboBox<String> cbCampana;
    private JComboBox<String> cbLote;
    private JTextField txtCURP, txtNombre;
    private JButton btnValidar, btnRegistrar, btnLimpiar, btnRecargar;
    private JLabel lblCentro;

    private CardLayout cardLayout;
    private JPanel contenedor;
    private int idEmpleado;
    private int idCentro;
    private String correoEmpleado;

    private Map<String, Integer> mapaCampanas;
    private Map<String, LoteInventario> mapaLotes;

    private static class LoteInventario {
        int idInventario;
        String numeroLote;
        String folioActual;
        int stock;
        int numeroInicioFolio;
        int numeroFinFolio;
        
        LoteInventario(int idInventario, String numeroLote, String folioActual, int stock) {
            this.idInventario = idInventario;
            this.numeroLote = numeroLote;
            this.folioActual = folioActual;
            this.stock = stock;
            
            // Analizar el folio para obtener los números
            try {
                String[] partes = folioActual.split("-");
                if (partes.length == 2) {
                    this.numeroInicioFolio = Integer.parseInt(partes[0]);
                    this.numeroFinFolio = Integer.parseInt(partes[1]);
                } else {
                    this.numeroInicioFolio = 0;
                    this.numeroFinFolio = 0;
                }
            } catch (Exception e) {
                this.numeroInicioFolio = 0;
                this.numeroFinFolio = 0;
            }
        }
    }

    public AltaVacunacion(CardLayout cardLayout, JPanel contenedor, String correoEmpleado) {
        this.cardLayout = cardLayout;
        this.contenedor = contenedor;
        this.correoEmpleado = correoEmpleado;

        if (!obtenerDatosEmpleado()) {
            JOptionPane.showMessageDialog(this, "No se encontró información del empleado.\nLa aplicación no funcionará correctamente.",
                    "Error crítico", JOptionPane.ERROR_MESSAGE);
        }

        this.mapaCampanas = new HashMap<>();
        this.mapaLotes = new HashMap<>();

        setBackground(FONDO);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("Registro de Aplicación de Vacunas");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(GUINDO);
        add(titulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Centro de Salud
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Centro de Salud:"), gbc);
        gbc.gridx = 1;
        lblCentro = new JLabel();
        form.add(lblCentro, gbc);
        cargarNombreCentro();

        // Campaña
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Campaña:"), gbc);
        gbc.gridx = 1;
        cbCampana = new JComboBox<>();
        cbCampana.addActionListener(e -> cargarLotes());
        form.add(cbCampana, gbc);

        // Lote
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Lote de Vacuna:"), gbc);
        gbc.gridx = 1;
        cbLote = new JComboBox<>();
        form.add(cbLote, gbc);

        // CURP
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("CURP del Paciente:"), gbc);
        gbc.gridx = 1;
        txtCURP = new JTextField(20);
        form.add(txtCURP, gbc);
        btnValidar = new JButton("Validar");
        btnValidar.setBackground(GUINDO);
        btnValidar.setForeground(Color.WHITE);
        btnValidar.addActionListener(e -> validarCURP());
        gbc.gridx = 2;
        form.add(btnValidar, gbc);

        // Nombre
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(30);
        txtNombre.setEditable(false);
        txtNombre.setBackground(new Color(240, 240, 240));
        form.add(txtNombre, gbc);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        btnRegistrar = new JButton("Registrar Aplicación");
        btnRegistrar.setBackground(GUINDO);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setEnabled(false);
        btnRegistrar.addActionListener(e -> registrarAplicacion());
        
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new Color(100, 100, 100));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        btnRecargar = new JButton("🔄 Recargar");
        btnRecargar.setBackground(new Color(50, 150, 200));
        btnRecargar.setForeground(Color.WHITE);
        btnRecargar.addActionListener(e -> {
            cargarCampanas();
            JOptionPane.showMessageDialog(this, "Campañas y lotes recargados");
        });
        
        JButton btnRegresar = new JButton("← Regresar");
        btnRegresar.setBackground(GUINDO);
        btnRegresar.setForeground(Color.WHITE);
        btnRegresar.addActionListener(e -> cardLayout.show(contenedor, "modulo4"));

        botones.add(btnRegistrar);
        botones.add(btnLimpiar);
        botones.add(btnRecargar);
        botones.add(btnRegresar);
        
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        form.add(botones, gbc);

        add(form, BorderLayout.CENTER);

        cargarCampanas();
    }

    private boolean obtenerDatosEmpleado() {
        String sql = "SELECT e.id_empleado, e.id_centro FROM empleados e " +
                     "JOIN usuarios u ON e.id_empleado = u.id_empleado " +
                     "WHERE u.correo = ?";
        try (Connection conn = Conexion_DB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correoEmpleado);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idEmpleado = rs.getInt("id_empleado");
                idCentro = rs.getInt("id_centro");
                System.out.println("Empleado cargado - ID: " + idEmpleado + ", Centro: " + idCentro);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void cargarNombreCentro() {
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            PreparedStatement ps = conn.prepareStatement("SELECT nombre FROM centros_salud WHERE id_centro = ?");
            ps.setInt(1, idCentro);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblCentro.setText(rs.getString("nombre"));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            lblCentro.setText("Error al cargar centro");
        }
    }

    private void cargarCampanas() {
        System.out.println("Cargando campañas para centro ID: " + idCentro);
        
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            String sql = "SELECT id_campana, nombre FROM campana_vacunacion WHERE id_centro = ? AND activo = 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idCentro);
            ResultSet rs = ps.executeQuery();
            
            cbCampana.removeAllItems();
            mapaCampanas.clear();
            
            while (rs.next()) {
                int id = rs.getInt("id_campana");
                String nombre = rs.getString("nombre");
                mapaCampanas.put(nombre, id);
                cbCampana.addItem(nombre);
                System.out.println("  Campaña: " + nombre + " (ID: " + id + ")");
            }
            
            rs.close();
            ps.close();
            
            if (cbCampana.getItemCount() == 0) {
                cbCampana.addItem("--- Sin campañas activas ---");
                System.out.println("  No se encontraron campañas activas");
            }
        } catch (SQLException e) { 
            e.printStackTrace();
            cbCampana.removeAllItems();
            cbCampana.addItem("--- Error al cargar campañas ---");
        }
    }

    private void cargarLotes() {
        String campana = (String) cbCampana.getSelectedItem();
        if (campana == null || !mapaCampanas.containsKey(campana)) {
            cbLote.removeAllItems();
            cbLote.addItem("--- Sin lotes disponibles ---");
            return;
        }

        int idCampana = mapaCampanas.get(campana);
        System.out.println("Cargando lotes para campaña ID: " + idCampana);

        try (Connection conn = Conexion_DB.obtenerConexion()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT d.id_inventario, a.folio, a.cantidad_asignar, l.lote_vacunacion " +
                "FROM campana_detalle_lote d " +
                "JOIN asignacion_vacunas_centro a ON d.id_inventario = a.id_inventario " +
                "JOIN lotes l ON a.id_lote = l.id_lote " +
                "WHERE d.id_campana = ? AND a.activo = 1 AND a.cantidad_asignar > 0");
            ps.setInt(1, idCampana);
            ResultSet rs = ps.executeQuery();

            cbLote.removeAllItems();
            mapaLotes.clear();

            while (rs.next()) {
                int idInv = rs.getInt("id_inventario");
                String folio = rs.getString("folio");
                int stock = rs.getInt("cantidad_asignar");
                String numeroLote = rs.getString("lote_vacunacion");

                // Extraer el número de folio ACTUAL (el primero del rango)
                String folioActual = folio.split("-")[0];

                // Mostrar información clara
                String display = numeroLote + " | Siguiente folio: " + folioActual + " | Disponibles: " + stock;
                mapaLotes.put(display, new LoteInventario(idInv, numeroLote, folio, stock));
                cbLote.addItem(display);
                System.out.println("  Lote: " + numeroLote + ", Rango: " + folio + ", Stock: " + stock);
            }

            rs.close();
            ps.close();

            if (cbLote.getItemCount() == 0) {
                cbLote.addItem("--- Sin lotes disponibles ---");
            }
        } catch (SQLException e) { 
            e.printStackTrace();
            cbLote.removeAllItems();
            cbLote.addItem("--- Error al cargar lotes ---");
        }
    }

    private void validarCURP() {
        String curp = txtCURP.getText().trim().toUpperCase();
        if (curp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una CURP");
            return;
        }
        
        try (Connection conn = Conexion_DB.obtenerConexion()) {
            PreparedStatement ps = conn.prepareStatement("SELECT CONCAT(nombre, ' ', apellido) as nombre_completo FROM ciudadanos WHERE curp = ?");
            ps.setString(1, curp);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtNombre.setText(rs.getString("nombre_completo"));
                btnRegistrar.setEnabled(true);
                txtCURP.setBackground(Color.WHITE);
            } else {
                txtNombre.setText("CURP no registrada");
                btnRegistrar.setEnabled(false);
                txtCURP.setBackground(new Color(255, 200, 200));
                JOptionPane.showMessageDialog(this, "La CURP no está registrada", "Error", JOptionPane.WARNING_MESSAGE);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) { 
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al validar CURP: " + e.getMessage());
        }
    }

    private void registrarAplicacion() {
        String curp = txtCURP.getText().trim().toUpperCase();
        String campana = (String) cbCampana.getSelectedItem();
        String loteDisplay = (String) cbLote.getSelectedItem();
        
        if (campana == null || loteDisplay == null || !mapaLotes.containsKey(loteDisplay)) {
            JOptionPane.showMessageDialog(this, "Seleccione una campaña y un lote válidos");
            return;
        }
        
        LoteInventario loteInfo = mapaLotes.get(loteDisplay);
        if (loteInfo.stock <= 0) {
            JOptionPane.showMessageDialog(this, "No hay stock disponible en este lote");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Confirmar registro?\n" +
            "Paciente: " + txtNombre.getText() + "\n" +
            "Campaña: " + campana + "\n" +
            "Lote: " + loteInfo.numeroLote + "\n" +
            "Folio actual: " + loteInfo.folioActual,
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            conn = Conexion_DB.obtenerConexion();
            conn.setAutoCommit(false);

            // 1. Insertar aplicación
            String sqlInsert = "INSERT INTO aplicador_vacunas (id_campana, curp_paciente, id_empleado, id_centro, lote) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstIns = conn.prepareStatement(sqlInsert);
            pstIns.setInt(1, mapaCampanas.get(campana));
            pstIns.setString(2, curp);
            pstIns.setInt(3, idEmpleado);
            pstIns.setInt(4, idCentro);
            pstIns.setString(5, loteInfo.numeroLote);
            pstIns.executeUpdate();
            pstIns.close();

            // 2. Actualizar folio y stock - Aquí está la parte importante
            int nuevoInicio = loteInfo.numeroInicioFolio + 1;
            String nuevoFolio = nuevoInicio + "-" + loteInfo.numeroFinFolio;
            int nuevoStock = loteInfo.stock - 1;
            
            System.out.println("Actualizando inventario:");
            System.out.println("  ID Inventario: " + loteInfo.idInventario);
            System.out.println("  Folio anterior: " + loteInfo.folioActual);
            System.out.println("  Folio nuevo: " + nuevoFolio);
            System.out.println("  Stock anterior: " + loteInfo.stock);
            System.out.println("  Stock nuevo: " + nuevoStock);
            
            String sqlUpdate = "UPDATE asignacion_vacunas_centro SET cantidad_asignar = ?, folio = ? WHERE id_inventario = ?";
            PreparedStatement pstUp = conn.prepareStatement(sqlUpdate);
            pstUp.setInt(1, nuevoStock);
            pstUp.setString(2, nuevoFolio);
            pstUp.setInt(3, loteInfo.idInventario);
            int filasActualizadas = pstUp.executeUpdate();
            pstUp.close();
            
            if (filasActualizadas == 0) {
                throw new SQLException("No se pudo actualizar el inventario");
            }

            conn.commit();
            
            JOptionPane.showMessageDialog(this, 
                "Aplicación registrada!\n\n" +
                "Lote: " + loteInfo.numeroLote + "\n" +
                "Folio utilizado: " + loteInfo.folioActual + "\n" +
                "Nuevo folio disponible: " + nuevoFolio + "\n" +
                "Stock restante: " + nuevoStock);
            
            limpiarFormulario();
            cargarLotes(); // Recargar lotes para mostrar el nuevo folio
            
        } catch (SQLException e) {
            if (conn != null) {
                try { 
                    conn.rollback(); 
                    JOptionPane.showMessageDialog(this, "Error al registrar. Operación revertida.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { 
                    conn.setAutoCommit(true); 
                    conn.close(); 
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void limpiarFormulario() {
        txtCURP.setText("");
        txtNombre.setText("");
        btnRegistrar.setEnabled(false);
        txtCURP.setBackground(Color.WHITE);
        txtCURP.requestFocus();
    }
}