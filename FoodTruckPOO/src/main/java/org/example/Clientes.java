package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Clientes {
    public JPanel panel;
    private JLabel lblBienvenida;
    private JComboBox<String> cmbRestaurantes;
    private JTable tblMenu;
    private JSpinner spnCantidad;
    private JButton btnAgregar;
    private JTable tblPedido;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JButton btnRealizarPedido;
    private JButton btnCancelarPedido;
    private JButton btnVolver;
    private JTable tblHistorialPedidos;
    private JButton btnActualizarHistorial;
    private JFrame frame;
    private DefaultTableModel modeloMenu;
    private DefaultTableModel modeloPedido;
    private DefaultTableModel modeloHistorial;
    private String correoCliente;
    private String restauranteSeleccionado;

    public Clientes(JFrame loginFrame, String correo) {
        this.correoCliente = correo;

        frame = new JFrame("Cliente - Hacer Pedido");
        frame.setContentPane(panel);
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        modeloMenu = new DefaultTableModel(new String[]{"ID", "Plato", "Precio"}, 0);
        tblMenu.setModel(modeloMenu);

        modeloPedido = new DefaultTableModel(new String[]{"Plato", "Cantidad", "Subtotal"}, 0);
        tblPedido.setModel(modeloPedido);

        modeloHistorial = new DefaultTableModel(new String[]{"ID Pedido", "Restaurante", "Fecha", "Total", "Estado"}, 0);
        tblHistorialPedidos.setModel(modeloHistorial);

        cargarNombreCliente();
        cargarRestaurantes();
        cargarHistorialPedidos();

        cmbRestaurantes.addActionListener(e -> cargarMenu());
        btnAgregar.addActionListener(e -> agregarAlPedido());
        btnRealizarPedido.addActionListener(e -> {
            realizarPedido();
            cargarHistorialPedidos();
        });
        btnCancelarPedido.addActionListener(e -> cancelarPedido());
        btnActualizarHistorial.addActionListener(e -> cargarHistorialPedidos());
        btnVolver.addActionListener(e -> {
            frame.setVisible(false);
            loginFrame.setVisible(true);
        });
    }

    private void cargarHistorialPedidos() {
        modeloHistorial.setRowCount(0);
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT p.id, r.nombre, p.fecha_pedido, p.total, p.estado FROM pedidos p " +
                    "JOIN restaurantes r ON p.id_restaurante = r.id " +
                    "WHERE p.id_cliente = (SELECT id FROM clientes WHERE correo = ?)";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, correoCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                modeloHistorial.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getTimestamp("fecha_pedido"),
                        rs.getDouble("total"),
                        rs.getString("estado")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar el historial de pedidos.");
        }
    }

    private void cargarNombreCliente() {
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT nombre FROM clientes WHERE correo = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, correoCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblBienvenida.setText("Bienvenido, " + rs.getString("nombre"));
            }
        } catch (Exception ex) {
            lblBienvenida.setText("Bienvenido, Cliente");
        }
    }

    private void cargarRestaurantes() {
        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT nombre FROM restaurantes";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            cmbRestaurantes.removeAllItems();
            while (rs.next()) {
                cmbRestaurantes.addItem(rs.getString("nombre"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar restaurantes");
        }
    }

    private void cargarMenu() {
        modeloMenu.setRowCount(0);
        String restaurante = (String) cmbRestaurantes.getSelectedItem();
        if (restaurante == null) return;

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sql = "SELECT id, nombre_plato, precio FROM menu WHERE id_restaurante = (SELECT id FROM restaurantes WHERE nombre = ?)";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, restaurante);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modeloMenu.addRow(new Object[]{rs.getInt("id"), rs.getString("nombre_plato"), rs.getDouble("precio")});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar el menú");
        }
    }

    private void agregarAlPedido() {
        int fila = tblMenu.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un plato");
            return;
        }

        int cantidad = (int) spnCantidad.getValue();
        if (cantidad < 1) {
            JOptionPane.showMessageDialog(null, "Selecciona una cantidad válida");
            return;
        }

        if (modeloPedido.getRowCount() == 0) {
            restauranteSeleccionado = (String) cmbRestaurantes.getSelectedItem();
            cmbRestaurantes.setEnabled(false);
        } else if (!restauranteSeleccionado.equals(cmbRestaurantes.getSelectedItem())) {
            JOptionPane.showMessageDialog(null, "Solo puedes pedir de un restaurante a la vez.");
            return;
        }

        String plato = (String) modeloMenu.getValueAt(fila, 1);
        double precio = (double) modeloMenu.getValueAt(fila, 2);
        double subtotal = cantidad * precio;

        modeloPedido.addRow(new Object[]{plato, cantidad, subtotal});
    }

    private void realizarPedido() {
        if (modeloPedido.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "El pedido está vacío");
            return;
        }

        String direccion = txtDireccion.getText();
        String telefono = txtTelefono.getText();
        if (direccion.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingresa dirección y teléfono");
            return;
        }

        double total = 0;
        for (int i = 0; i < modeloPedido.getRowCount(); i++) {
            total += (double) modeloPedido.getValueAt(i, 2);
        }

        try (Connection conexion = DatabaseConnection.conectar()) {
            String sqlPedido = "INSERT INTO pedidos (id_cliente, id_restaurante, estado, total, fecha_pedido) VALUES ((SELECT id FROM clientes WHERE correo = ?), (SELECT id FROM restaurantes WHERE nombre = ?), 'Pendiente', ?, NOW())";
            PreparedStatement psPedido = conexion.prepareStatement(sqlPedido, PreparedStatement.RETURN_GENERATED_KEYS);
            psPedido.setString(1, correoCliente);
            psPedido.setString(2, restauranteSeleccionado);
            psPedido.setDouble(3, total);
            psPedido.executeUpdate();

            ResultSet rs = psPedido.getGeneratedKeys();
            if (rs.next()) {
                int idPedido = rs.getInt(1);
                for (int i = 0; i < modeloPedido.getRowCount(); i++) {
                    String sqlDetalle = "INSERT INTO detalles_pedido (id_pedido, id_menu, cantidad, subtotal) VALUES (?, (SELECT id FROM menu WHERE nombre_plato = ? AND id_restaurante = (SELECT id FROM restaurantes WHERE nombre = ?)), ?, ?)";
                    PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle);
                    psDetalle.setInt(1, idPedido);
                    psDetalle.setString(2, (String) modeloPedido.getValueAt(i, 0));
                    psDetalle.setString(3, restauranteSeleccionado);
                    psDetalle.setInt(4, (int) modeloPedido.getValueAt(i, 1));
                    psDetalle.setDouble(5, (double) modeloPedido.getValueAt(i, 2));
                    psDetalle.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(null, "Pedido realizado con éxito");
            limpiarCarrito();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al realizar el pedido");
        }
    }

    private void limpiarCarrito() {
        modeloPedido.setRowCount(0);
        restauranteSeleccionado = null;
        cmbRestaurantes.setEnabled(true);
    }

    private void cancelarPedido() {
        limpiarCarrito();
        JOptionPane.showMessageDialog(null, "Pedido cancelado.");
    }

}
