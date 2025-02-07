import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginUI extends JFrame {
    public LoginUI() {
        setTitle("Inicio de Sesión");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        JLabel userLabel = new JLabel("Correo:");
        JTextField userText = new JTextField();
        JLabel passwordLabel = new JLabel("Contraseña:");
        JPasswordField passwordText = new JPasswordField();
        JButton loginButton = new JButton("Iniciar Sesión");
        JButton registerButton = new JButton("Registrarse");

        add(userLabel);
        add(userText);
        add(passwordLabel);
        add(passwordText);
        add(new JLabel());
        add(loginButton);
        add(new JLabel());
        add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String correo = userText.getText();
                String contrasena = new String(passwordText.getPassword());

                try (Connection conn = FTDB.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM usuarios WHERE correo = ? AND contrasena = ?");
                    stmt.setString(1, correo);
                    stmt.setString(2, contrasena);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String rol = rs.getString("rol");
                        JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso como " + rol);
                        dispose();

                        if ("CLIENTE".equals(rol)) {
                            new Restaurantes();
                        } else if ("RESTAURANTE".equals(rol)) {
                            new Compañia();
                        } else if ("REPARTIDOR".equals(rol)) {
                            new Repartidor();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Correo o contraseña incorrectos");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error en la base de datos: " + ex.getMessage());
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterUI(); // Abre la ventana de registro
                dispose(); // Cierra la ventana de login
            }
        });

        setVisible(true);
    }
}


