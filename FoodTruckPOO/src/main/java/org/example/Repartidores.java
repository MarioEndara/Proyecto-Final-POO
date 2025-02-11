package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Repartidores {
    private JPanel panel;
    private JLabel lblBienvenida;
    private JTable tblPedidos;
    private JComboBox<String> cmbVehiculos;
    private JButton btnIniciarEntrega;
    private JButton btnFinalizarEntrega;
    private JButton btnVolver;
    private JFrame frame;
    private DefaultTableModel modeloPedidos;
    private String correoRepartidor;

    public Repartidores(JFrame loginFrame, String correo) {
        this.correoRepartidor = correo;

        frame = new JFrame("Repartidor - Entregas");
        frame.setContentPane(panel);
        frame.setSize(700, 500);
        frame.setVisible(true);

        modeloPedidos = new DefaultTableModel(new String[]{"ID Pedido", "Restaurante", "Cliente", "Dirección", "Estado"}, 0);
        tblPedidos.setModel(modeloPedidos);

        cargarNombreRepartidor();
        cargarPedidos();
        cargarVehiculos();

        btnIniciarEntrega.addActionListener(e -> iniciarEntrega());
        btnFinalizarEntrega.addActionListener(e -> finalizarEntrega());
        btnVolver.addActionListener(e -> {
            frame.setVisible(false);
            loginFrame.setVisible(true);
        });
    }

    private void cargarNombreRepartidor() {
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT nombre FROM repartidores WHERE correo = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, correoRepartidor);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblBienvenida.setText("Bienvenido, " + rs.getString("nombre"));
            }
        } catch (Exception ex) {
            lblBienvenida.setText("Bienvenido, Repartidor");
        }
    }

    private void cargarPedidos() {
        modeloPedidos.setRowCount(0);
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT p.id, r.nombre AS restaurante, c.nombre AS cliente, c.direccion, p.estado " +
                    "FROM pedidos p " +
                    "JOIN restaurantes r ON p.id_restaurante = r.id " +
                    "JOIN clientes c ON p.id_cliente = c.id " +
                    "WHERE p.estado IN ('Pendiente', 'En Camino')";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modeloPedidos.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("restaurante"),
                        rs.getString("cliente"),
                        rs.getString("direccion"),
                        rs.getString("estado")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los pedidos.");
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

    private void iniciarEntrega() {
        int fila = tblPedidos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un pedido para iniciar la entrega.");
            return;
        }

        String vehiculo = (String) cmbVehiculos.getSelectedItem();
        if (vehiculo == null || vehiculo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecciona un vehículo antes de iniciar la entrega.");
            return;
        }

        int idPedido = (int) modeloPedidos.getValueAt(fila, 0);

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sqlRepartidor = "SELECT id FROM repartidores WHERE correo = ?";
            PreparedStatement psRepartidor = conexion.prepareStatement(sqlRepartidor);
            psRepartidor.setString(1, correoRepartidor);
            ResultSet rsRepartidor = psRepartidor.executeQuery();

            if (!rsRepartidor.next()) {
                JOptionPane.showMessageDialog(null, "Error: El repartidor no existe en la base de datos.");
                return;
            }

            int idRepartidor = rsRepartidor.getInt("id");

            String sql = "UPDATE pedidos SET estado = 'En Camino', id_repartidor = ? WHERE id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, idRepartidor);
            ps.setInt(2, idPedido);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Entrega iniciada con éxito.");
                cargarPedidos();
            } else {
                JOptionPane.showMessageDialog(null, "⚠ No se pudo iniciar la entrega. Verifica el pedido.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al iniciar la entrega: " + ex.getMessage());
        }
    }

    private void finalizarEntrega() {
        int fila = tblPedidos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un pedido para finalizar la entrega.");
            return;
        }

        int idPedido = (int) modeloPedidos.getValueAt(fila, 0);

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "UPDATE pedidos SET estado = 'Entregado' WHERE id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, idPedido);
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "Pedido entregado con éxito.");
                cargarPedidos();
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo finalizar la entrega. Verifica el pedido.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al finalizar la entrega: " + ex.getMessage());
        }
    }
}
