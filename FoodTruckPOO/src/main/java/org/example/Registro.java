package org.example;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Registro {
    private JPanel panel;
    private JTextField txtNombre;
    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JComboBox<String> cmbTipo;
    private JButton btnRegistrar;
    private JButton btnVolver;
    private JFrame frame;

    public Registro(JFrame loginFrame) {
        frame = new JFrame("Registro");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); //Centrar la ventana en la pantalla
        frame.setVisible(true);

        cmbTipo.addItem("Cliente");
        cmbTipo.addItem("Restaurante");
        cmbTipo.addItem("Repartidor");
        cmbTipo.addItem("Administrador");

        btnRegistrar.addActionListener(e -> registrarUsuario());
        btnVolver.addActionListener(e -> {
            frame.setVisible(false);
            loginFrame.setVisible(true);
        });
    }

    //Registro de usuarios
    private void registrarUsuario() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();
        String tipo = (String) cmbTipo.getSelectedItem();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
            return;
        }

        if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(null, "Correo no válido. Ingresa un formato correcto.");
            return;
        }

        if (correoExiste(correo)) {
            JOptionPane.showMessageDialog(null, "El correo ya está registrado. Usa otro.");
            return;
        }

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "";

            switch (tipo) {
                case "Cliente":
                    sql = "INSERT INTO clientes (nombre, correo, contraseña, direccion, telefono) VALUES (?, ?, ?, ?, ?)";
                    break;
                case "Restaurante":
                    sql = "INSERT INTO restaurantes (nombre, correo, contraseña, direccion, telefono) VALUES (?, ?, ?, ?, ?)";
                    break;
                case "Repartidor":
                    sql = "INSERT INTO repartidores (nombre, correo, contraseña, telefono, vehiculo) VALUES (?, ?, ?, ?, ?)";
                    break;
                case "Administrador":
                    sql = "INSERT INTO administradores (nombre, correo, contraseña) VALUES (?, ?, ?)";
                    break;
            }

            PreparedStatement ps = conexion.prepareStatement(sql);

            if (tipo.equals("Cliente")) {
                ps.setString(1, nombre);
                ps.setString(2, correo);
                ps.setString(3, contrasena);
                ps.setString(4, "No especificada");
                ps.setString(5, "No especificado");
                ps.executeUpdate();
            } else if (tipo.equals("Restaurante")) {
                String direccion = JOptionPane.showInputDialog("Ingresa la dirección del restaurante:");
                String telefono = JOptionPane.showInputDialog("Ingresa el teléfono del restaurante:");

                if (direccion == null || direccion.isEmpty()) direccion = "No especificada";
                if (telefono == null || telefono.isEmpty()) telefono = "000-000-0000";

                ps.setString(1, nombre);
                ps.setString(2, correo);
                ps.setString(3, contrasena);
                ps.setString(4, direccion);
                ps.setString(5, telefono);
                ps.executeUpdate();
            } else if (tipo.equals("Repartidor")) {
                ps.setString(1, nombre);
                ps.setString(2, correo);
                ps.setString(3, contrasena);
                ps.setString(4, "000-000-0000");
                ps.setString(5, "No especificado");
                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "Registro de repartidor exitoso. Ahora ingresa los datos adicionales.");
                frame.setVisible(false);
                new VentanaRepartidorRegistro(correo);
                return;
            } else {
                ps.setString(1, nombre);
                ps.setString(2, correo);
                ps.setString(3, contrasena);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(null, "Registro exitoso");
            frame.setVisible(false);
            new Login();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al registrar");
        }
    }

    private boolean correoExiste(String correo) {
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT correo FROM clientes WHERE correo = ? " +
                    "UNION SELECT correo FROM restaurantes WHERE correo = ? " +
                    "UNION SELECT correo FROM repartidores WHERE correo = ? " +
                    "UNION SELECT correo FROM administradores WHERE correo = ?";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, correo);
            ps.setString(2, correo);
            ps.setString(3, correo);
            ps.setString(4, correo);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }

    public void completarRegistro(String correoRestaurante, String direccion, String telefono) {
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "UPDATE restaurantes SET direccion = ?, telefono = ? WHERE correo = ?";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, direccion);
            ps.setString(2, telefono);
            ps.setString(3, correoRestaurante);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registro de restaurante completado.");
            new Login();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error en completarRegistro()");
        }
    }
}
