package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Restaurantes {
    private JPanel panel;
    private JLabel lblBienvenida;
    private JTable tblMenu;
    private JTextField txtNombrePlato;
    private JTextArea txtDescripcionPlato;
    private JTextField txtPrecioPlato;
    private JButton btnAgregarPlato;
    private JButton btnEditarPlato;
    private JButton btnEliminarPlato;
    private JButton btnVerPedidos;
    private JButton btnVolver;
    private JFrame frame;
    private DefaultTableModel modeloMenu;
    private String correoRestaurante;

    public Restaurantes(JFrame loginFrame, String correo) {
        this.correoRestaurante = correo;

        frame = new JFrame("Restaurante - Gestión de Menú");
        frame.setContentPane(panel);
        frame.setSize(700, 500);
        frame.setVisible(true);

        modeloMenu = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripción", "Precio"}, 0);
        tblMenu.setModel(modeloMenu);

        cargarNombreRestaurante();
        cargarMenu();

        btnVerPedidos.addActionListener(e -> {
            frame.setVisible(false);
            new PedidosRestaurante(frame, correoRestaurante);
        });

        btnAgregarPlato.addActionListener(e -> agregarPlato());
        btnEditarPlato.addActionListener(e -> editarPlato());
        btnEliminarPlato.addActionListener(e -> eliminarPlato());
        btnVolver.addActionListener(e -> {
            frame.setVisible(false);
            loginFrame.setVisible(true);
        });
    }

    private void cargarNombreRestaurante() {
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT nombre FROM restaurantes WHERE correo = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, correoRestaurante);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblBienvenida.setText("Bienvenido, " + rs.getString("nombre"));
            }
        } catch (Exception ex) {
            lblBienvenida.setText("Bienvenido, Restaurante");
        }
    }

    private void cargarMenu() {
        modeloMenu.setRowCount(0);
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT id, nombre_plato, descripcion, precio FROM menu WHERE id_restaurante = (SELECT id FROM restaurantes WHERE correo = ?)";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, correoRestaurante);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modeloMenu.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre_plato"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar el menú");
        }
    }

    private void agregarPlato() {
        String nombre = txtNombrePlato.getText();
        String descripcion = txtDescripcionPlato.getText();
        String precioStr = txtPrecioPlato.getText();

        if (nombre.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Completa todos los campos");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El precio debe ser un número válido");
            return;
        }

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "INSERT INTO menu (id_restaurante, nombre_plato, descripcion, precio) VALUES ((SELECT id FROM restaurantes WHERE correo = ?), ?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, correoRestaurante);
            ps.setString(2, nombre);
            ps.setString(3, descripcion);
            ps.setDouble(4, precio);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Plato agregado con éxito");
            cargarMenu();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al agregar el plato");
        }
    }

    private void editarPlato() {
        int fila = tblMenu.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un plato para editar");
            return;
        }

        int id = (int) modeloMenu.getValueAt(fila, 0);
        String nombre = txtNombrePlato.getText();
        String descripcion = txtDescripcionPlato.getText();
        String precioStr = txtPrecioPlato.getText();

        if (nombre.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Completa todos los campos");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El precio debe ser un número válido");
            return;
        }

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "UPDATE menu SET nombre_plato = ?, descripcion = ?, precio = ? WHERE id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setDouble(3, precio);
            ps.setInt(4, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Plato actualizado con éxito");
            cargarMenu();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el plato");
        }
    }


    private void eliminarPlato() {
        int fila = tblMenu.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un plato para eliminar");
            return;
        }

        int id = (int) modeloMenu.getValueAt(fila, 0);

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "DELETE FROM menu WHERE id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Plato eliminado con éxito");
            cargarMenu();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el plato");
        }

    }
}

