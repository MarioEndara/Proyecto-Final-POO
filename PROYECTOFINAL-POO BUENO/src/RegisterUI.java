import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterUI extends JFrame {
    public RegisterUI() {
        setTitle("Registro de Usuario");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2));

        JLabel nameLabel = new JLabel("Nombre:");
        JTextField nameField = new JTextField();
        JLabel emailLabel = new JLabel("Correo:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Contraseña:");
        JPasswordField passwordField = new JPasswordField();
        JLabel roleLabel = new JLabel("Rol:");
        String[] roles = {"CLIENTE", "RESTAURANTE", "REPARTIDOR"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        JButton registerButton = new JButton("Registrarse");
        JButton backButton = new JButton("Volver");

        add(nameLabel); add(nameField);
        add(emailLabel); add(emailField);
        add(passwordLabel); add(passwordField);
        add(roleLabel); add(roleBox);
        add(registerButton); add(backButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = nameField.getText();
                String correo = emailField.getText();
                String contrasena = new String(passwordField.getPassword());
                String rol = roleBox.getSelectedItem().toString();

                if (contrasena.length() < 4) {
                    JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos 4 caracteres.");
                    return;
                }

                try (Connection conn = FTDB.getConnection()) {
                    PreparedStatement checkUser = conn.prepareStatement("SELECT * FROM usuarios WHERE correo = ?");
                    checkUser.setString(1, correo);
                    ResultSet rs = checkUser.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "El correo ya está registrado.");
                        return;
                    }

                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO usuarios (nombre, correo, contrasena, rol) VALUES (?, ?, ?, ?)");
                    stmt.setString(1, nombre);
                    stmt.setString(2, correo);
                    stmt.setString(3, contrasena);
                    stmt.setString(4, rol);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Registro exitoso. Ahora puedes iniciar sesión.");
                    dispose();
                    new LoginUI();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error en la base de datos: " + ex.getMessage());
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginUI(); // Vuelve al Login
                dispose();
            }
        });

        setVisible(true);
    }
}

