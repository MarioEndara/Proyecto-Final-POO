package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PedidosRestaurante {
    private JPanel panel;
    private JTable tblPedidos;
    private JButton btnMarcarEnCamino;
    private JButton btnVolver;
    private JFrame frame;
    private DefaultTableModel modeloPedidos;
    private String correoRestaurante;

    public PedidosRestaurante(JFrame restauranteFrame, String correo) {
        this.correoRestaurante = correo;

        frame = new JFrame("Pedidos Recibidos");
        frame.setContentPane(panel);
        frame.setSize(700, 500);
        frame.setVisible(true);

        modeloPedidos = new DefaultTableModel(new String[]{"ID Pedido", "Cliente", "Fecha", "Total", "Estado"}, 0);
        tblPedidos.setModel(modeloPedidos);

        cargarPedidos();

        btnMarcarEnCamino.addActionListener(e -> marcarPedidoEnCamino());
        btnVolver.addActionListener(e -> {
            frame.setVisible(false);
            restauranteFrame.setVisible(true);
        });
    }

    private void cargarPedidos() {
        modeloPedidos.setRowCount(0);
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT p.id, c.nombre, p.fecha_pedido, p.total, p.estado FROM pedidos p " +
                    "JOIN clientes c ON p.id_cliente = c.id " +
                    "WHERE p.id_restaurante = (SELECT id FROM restaurantes WHERE correo = ?) " +
                    "AND p.estado IN ('Pendiente', 'En preparación')";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, correoRestaurante);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modeloPedidos.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getTimestamp("fecha_pedido"),
                        rs.getDouble("total"),
                        rs.getString("estado")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar los pedidos");
        }
    }

    private void marcarPedidoEnCamino() {
        int fila = tblPedidos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un pedido para marcarlo como 'En Camino'");
            return;
        }

        int idPedido = (int) modeloPedidos.getValueAt(fila, 0);

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "UPDATE pedidos SET estado = 'En Camino' WHERE id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, idPedido);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "Pedido marcado como 'En Camino'");
            cargarPedidos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el estado del pedido");
        }
    }
}

