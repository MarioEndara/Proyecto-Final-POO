package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class VentanaRepartidorRegistro {
    private JPanel panel;
    private JTextField txtTelefono;
    private JCheckBox chkTieneVehiculo;
    private JTextField txtVehiculo;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JFrame frame;
    private String correoRepartidor;

    public VentanaRepartidorRegistro(String correo) {
        this.correoRepartidor = correo;

        frame = new JFrame("Registro de Repartidor");
        frame.setContentPane(panel);
        frame.setSize(400, 250);
        frame.setVisible(true);

        txtVehiculo.setEnabled(false);
        txtVehiculo.setText("No especificado"); // 🔥 Se inicia como "No especificado"

        chkTieneVehiculo.addActionListener(e -> {
            if (chkTieneVehiculo.isSelected()) {
                txtVehiculo.setEnabled(true);
                txtVehiculo.setText("");
            } else {
                txtVehiculo.setEnabled(false);
                txtVehiculo.setText("No especificado");
            }
        });

        btnRegistrar.addActionListener(e -> registrarRepartidor());

        btnCancelar.addActionListener(e -> {
            frame.setVisible(false);
            new Login(); // 🔥 Vuelve a Login si cancela
        });
    }

    private void registrarRepartidor() {
        String telefono = txtTelefono.getText().trim();
        String vehiculo = txtVehiculo.getText().trim();

        // 📌 **Validaciones**
        if (telefono.isEmpty() || !telefono.matches("^[0-9]{10}$")) {
            JOptionPane.showMessageDialog(null, "Debes ingresar un número de teléfono válido (10 dígitos).");
            return;
        }

        if (vehiculo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debes especificar un vehículo o marcar 'No tengo'.");
            return;
        }

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "UPDATE repartidores SET telefono = ?, vehiculo = ? WHERE correo = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, telefono);
            ps.setString(2, vehiculo);
            ps.setString(3, correoRepartidor);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Registro de repartidor completado.");
            frame.setVisible(false);
            new Login(); // 🔥 Ahora sí vuelve a Login tras registrar
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al registrar el repartidor.");
        }
    }
}
