import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FTDB {
    private static final String URL = "jdbc:mysql://b7p2irkq8jczjnrghaf4-mysql.services.clever-cloud.com:3306/b7p2irkq8jczjnrghaf4";
    private static final String USER = "u7ywwwyi0isrhtf0";
    private static final String PASSWORD = "VbZG4AapcgbtGMzVzizi";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión exitosa a la base de datos.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Error en la conexión: " + e.getMessage());
        }
        return connection;
    }

    public static void main(String[] args) {
        getConnection();
    }
}
