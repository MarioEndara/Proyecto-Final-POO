package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Administrador {
    private JPanel panel;
    private JLabel lblBienvenida;
    private JTable tblRestaurantes;
    private JButton btnEliminarRestaurante;
    private JTable tblRepartidores;
    private JComboBox<String> cmbVehiculos;
    private JButton btnAsignarVehiculo;
    private JTable tblHistorialPedidos;
    private JButton btnActualizarHistorial;
    private JButton btnVolver;
    private JFrame frame;
    private DefaultTableModel modeloRestaurantes;
    private DefaultTableModel modeloRepartidores;
    private DefaultTableModel modeloHistorial;

    public Administrador(JFrame loginFrame) {
        frame = new JFrame("Administrador - Gestión");
        frame.setContentPane(panel);
        frame.setSize(800, 600);
        frame.setVisible(true);

        modeloRestaurantes = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo"}, 0);
        tblRestaurantes.setModel(modeloRestaurantes);

        modeloRepartidores = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo", "Vehículo"}, 0);
        tblRepartidores.setModel(modeloRepartidores);

        modeloHistorial = new DefaultTableModel(new String[]{"ID Pedido", "Restaurante", "Cliente", "Fecha", "Total", "Estado"}, 0);
        tblHistorialPedidos.setModel(modeloHistorial);

        cargarRestaurantes();
        cargarRepartidores();
        cargarHistorialPedidos();
        cargarVehiculos();

        btnEliminarRestaurante.addActionListener(e -> eliminarRestaurante());
        btnAsignarVehiculo.addActionListener(e -> asignarVehiculo());
        btnActualizarHistorial.addActionListener(e -> cargarHistorialPedidos());
        btnVolver.addActionListener(e -> {
            frame.setVisible(false);
            loginFrame.setVisible(true);
        });
    }

    private void cargarRestaurantes() {
        modeloRestaurantes.setRowCount(0);
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT id, nombre, correo FROM restaurantes";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloRestaurantes.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los restaurantes");
        }
    }

    private void cargarHistorialPedidos() {
        modeloHistorial.setRowCount(0);
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT p.id, r.nombre AS restaurante, c.nombre AS cliente, p.fecha_pedido, p.total, p.estado " +
                    "FROM pedidos p " +
                    "JOIN restaurantes r ON p.id_restaurante = r.id " +
                    "JOIN clientes c ON p.id_cliente = c.id";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modeloHistorial.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("restaurante"),
                        rs.getString("cliente"),
                        rs.getTimestamp("fecha_pedido"),
                        rs.getDouble("total"),
                        rs.getString("estado")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar el historial de pedidos");
        }
    }

    private void eliminarRestaurante() {
        int fila = tblRestaurantes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un restaurante para eliminar");
            return;
        }

        int id = (int) modeloRestaurantes.getValueAt(fila, 0);

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "DELETE FROM restaurantes WHERE id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Restaurante eliminado con éxito");
            cargarRestaurantes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el restaurante");
        }
    }

    private void cargarRepartidores() {
        modeloRepartidores.setRowCount(0);
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT r.id, r.nombre, r.correo, r.vehiculo FROM repartidores r";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloRepartidores.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("vehiculo")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los repartidores");
        }
    }

    private void cargarVehiculos() {
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT placa FROM vehiculos";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            cmbVehiculos.removeAllItems();
            while (rs.next()) {
                cmbVehiculos.addItem(rs.getString("placa"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los vehículos");
        }
    }

    private void asignarVehiculo() {
        int fila = tblRepartidores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un repartidor");
            return;
        }

        String vehiculo = (String) cmbVehiculos.getSelectedItem();
        if (vehiculo == null) {
            JOptionPane.showMessageDialog(null, "Selecciona un vehículo");
            return;
        }

        int idRepartidor = (int) modeloRepartidores.getValueAt(fila, 0);

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "UPDATE repartidores SET vehiculo = ? WHERE id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, vehiculo);
            ps.setInt(2, idRepartidor);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Vehículo asignado con éxito");
            cargarRepartidores();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al asignar el vehículo");
        }
    }
}
