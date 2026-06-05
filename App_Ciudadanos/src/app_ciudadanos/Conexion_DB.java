package app_ciudadanos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion_DB {

    // ← base de datos diferente a la del sector salud
    private static final String URL      = "jdbc:mysql://localhost:3306/SectorSalud";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "123456"; // tu contraseña de MySQL

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
