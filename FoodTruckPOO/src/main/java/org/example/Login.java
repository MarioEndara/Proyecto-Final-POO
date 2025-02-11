package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login {
    private JPanel panel;
    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JButton btnRegistro;
    private JLabel lblCorreo, lblContrasena;
    private JFrame frame;

    public Login() {
        frame = new JFrame("Inicio de Sesión a FoodTruck");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Crear panel con imagen de fondo
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imagenFondo = new ImageIcon(getClass().getClassLoader().getResource("images/logo.jpeg"));
                g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);

        // Crear etiquetas
        lblCorreo = new JLabel("Correo:");
        lblContrasena = new JLabel("Contraseña:");

        // Crear campos de texto y botones
        txtCorreo = new JTextField();
        txtContrasena = new JPasswordField();
        btnLogin = new JButton("Iniciar Sesión");
        btnRegistro = new JButton("Registrarse");

        // Posicionar elementos en el panel
        lblCorreo.setBounds(50, 50, 100, 25);
        txtCorreo.setBounds(150, 50, 200, 25);
        lblContrasena.setBounds(50, 90, 100, 25);
        txtContrasena.setBounds(150, 90, 200, 25);
        btnLogin.setBounds(100, 150, 120, 30);
        btnRegistro.setBounds(230, 150, 120, 30);

        // Agregar los componentes al panel
        panel.add(lblCorreo);
        panel.add(txtCorreo);
        panel.add(lblContrasena);
        panel.add(txtContrasena);
        panel.add(btnLogin);
        panel.add(btnRegistro);

        // Agregar el panel al frame
        frame.setContentPane(panel);
        frame.setVisible(true);

        // Acción de los botones
        btnLogin.addActionListener(e -> verificarLogin());
        btnRegistro.addActionListener(e -> irARegistro());
    }

    private void verificarLogin() {
        String correo = txtCorreo.getText();
        String contrasena = new String(txtContrasena.getPassword());

        if (correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Completa todos los campos");
            return;
        }

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT 'cliente' AS tipo FROM clientes WHERE correo=? AND contraseña=? " +
                    "UNION " +
                    "SELECT 'restaurante' AS tipo FROM restaurantes WHERE correo=? AND contraseña=? " +
                    "UNION " +
                    "SELECT 'repartidor' AS tipo FROM repartidores WHERE correo=? AND contraseña=? " +
                    "UNION " +
                    "SELECT 'administrador' AS tipo FROM administradores WHERE correo=? AND contraseña=?";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            ps.setString(3, correo);
            ps.setString(4, contrasena);
            ps.setString(5, correo);
            ps.setString(6, contrasena);
            ps.setString(7, correo);
            ps.setString(8, contrasena);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String tipoUsuario = rs.getString("tipo");
                JOptionPane.showMessageDialog(null, "Bienvenido " + tipoUsuario);

                frame.setVisible(false);

                switch (tipoUsuario) {
                    case "cliente":
                        new Clientes(frame, correo);
                        break;
                    case "restaurante":
                        new Restaurantes(frame, correo);
                        break;
                    case "repartidor":
                        new Repartidores(frame, correo);
                        break;
                    case "administrador":
                        new Administrador(frame);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error de conexión");
        }
    }
    private void irARegistro() {
        frame.setVisible(false);
        new Registro(frame);
    }

    public static void main(String[] args) {
        new Login();
    }
}

