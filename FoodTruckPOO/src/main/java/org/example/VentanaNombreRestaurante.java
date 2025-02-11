package org.example;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VentanaNombreRestaurante {
    private JPanel panel;
    private JTextField txtNombreRestaurante;
    private JTextField txtDireccionRestaurante;
    private JTextField txtTelefonoRestaurante;
    private JButton btnConfirmar;
    private JFrame frame;
    private Registro registro;
    private String correoRestaurante;
    private String contrasenaRestaurante;

    public VentanaNombreRestaurante(Registro registro, String correo, String contrasena) {
        this.registro = registro;
        this.correoRestaurante = correo;
        this.contrasenaRestaurante = contrasena;

        frame = new JFrame("Registro de Restaurante");
        frame.setContentPane(panel);
        frame.setSize(400, 300);
        frame.setVisible(true);

        btnConfirmar.addActionListener(e -> registrarRestaurante());
    }

    private void registrarRestaurante() {
        String nombreRestaurante = txtNombreRestaurante.getText().trim();
        String direccionRestaurante = txtDireccionRestaurante.getText().trim();
        String telefonoRestaurante = txtTelefonoRestaurante.getText().trim();

        if (nombreRestaurante.isEmpty() || direccionRestaurante.isEmpty() || telefonoRestaurante.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingresa el nombre, dirección y teléfono del restaurante.");
            return;
        }

        try (Connection conexion = DatabaseConnection.conectar()) {
            // 🔥 Verificar si el correo ya existe antes de intentar registrar
            String checkSql = "SELECT id FROM restaurantes WHERE correo = ?";
            PreparedStatement checkPs = conexion.prepareStatement(checkSql);
            checkPs.setString(1, correoRestaurante);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Este correo ya está registrado. Usa otro.");
                return;
            }

            // 🔥 Si el correo no existe, registrar el restaurante
            String sql = "INSERT INTO restaurantes (nombre, correo, direccion, telefono, contraseña) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, nombreRestaurante);
            ps.setString(2, correoRestaurante);
            ps.setString(3, direccionRestaurante);
            ps.setString(4, telefonoRestaurante);
            ps.setString(5, contrasenaRestaurante);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "✅ Restaurante registrado con éxito.");

            // 🔥 Evitar errores pasando la dirección correctamente en completarRegistro()
            registro.completarRegistro(correoRestaurante, direccionRestaurante, telefonoRestaurante);

            // 🔥 Cerrar la ventana SOLO si no hubo errores
            frame.dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "❌ Error al registrar el restaurante.");
        }
    }


}