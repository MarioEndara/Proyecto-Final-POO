package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Datos de conexión de Clever Cloud
    private static final String URL = "jdbc:mysql://byhlvbmuwx8wwyxy7l1k-mysql.services.clever-cloud.com:3306/byhlvbmuwx8wwyxy7l1k?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "unaougcn0ijp3jcg";
    private static final String CONTRASEÑA = "pI9TmESUznvhci6mvs5T";

    public static Connection conectar() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Cargar el driver JDBC
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
            System.out.println("✅ Conexión exitosa a la base de datos.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: No se encontró el driver JDBC.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión a la base de datos.");
            e.printStackTrace();
        }
        return conexion;
    }

    public static void main(String[] args) {
        conectar(); // Probar la conexión
    }
}

