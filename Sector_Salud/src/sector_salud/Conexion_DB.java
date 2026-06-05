package sector_salud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar la conexión con la base de datos MySQL.
 */
public class Conexion_DB 
{
    private static final String URL = "jdbc:mysql://localhost:3306/SectorSalud";
    
    private static final String USUARIO = "root";
    
    private static final String PASSWORD = "123456";
    
    /*
     * Devuelve una conexión activa a la base de datos.
     * Se llama así: Connection con = ConexionDB.obtenerConexion();
     */
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
