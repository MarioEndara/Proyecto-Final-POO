import javax.swing.*;
import java.awt.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MenuRestaurante extends JFrame {
    private String restaurante;
    private JList<String> listaProductos;
    private JButton btnVolver, btnVerTotal, btnAgregar, btnPagar;
    private JTextArea carritoArea;
    private double total;

    public MenuRestaurante(String restaurante) {
        this.restaurante = restaurante;
        total = 0.0;

        setTitle("Menú de " + restaurante);
        setLayout(new BorderLayout());

        // Obtener productos dinámicamente desde la base de datos
        String[] productos = obtenerProductosDesdeDB(restaurante);
        listaProductos = new JList<>(productos);
        JScrollPane scrollPane = new JScrollPane(listaProductos);

        carritoArea = new JTextArea(5, 20);
        carritoArea.setEditable(false);
        JScrollPane carritoScroll = new JScrollPane(carritoArea);

        btnVolver = new JButton("Agregar al carrito");
        btnVerTotal = new JButton("Ver Total");
        btnAgregar = new JButton("Volver");
        btnPagar = new JButton("Pagar");

        btnVolver.addActionListener(e -> agregarAlCarrito());
        btnVerTotal.addActionListener(e -> mostrarTotal());
        btnPagar.addActionListener(e -> confirmarPago());
        btnAgregar.addActionListener(e -> {
            dispose();
            new Restaurantes();
        });

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout());
        panelBotones.add(btnVolver);
        panelBotones.add(btnVerTotal);
        panelBotones.add(btnPagar);
        panelBotones.add(btnAgregar);

        add(scrollPane, BorderLayout.CENTER);
        add(carritoScroll, BorderLayout.SOUTH);
        add(panelBotones, BorderLayout.NORTH);

        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private String[] obtenerProductosDesdeDB(String restaurante) {
        ArrayList<String> productosList = new ArrayList<>();
        try (Connection conn = FTDB.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT nombre_platillo, precio FROM menu WHERE restaurante_id = (SELECT id FROM restaurantes WHERE nombre = ?)");
            stmt.setString(1, restaurante);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                productosList.add(rs.getString("nombre_platillo") + " - " + rs.getDouble("precio"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener productos: " + ex.getMessage());
        }
        return productosList.toArray(new String[0]);
    }

    private void agregarAlCarrito() {
        String productoSeleccionado = listaProductos.getSelectedValue();
        if (productoSeleccionado != null) {
            carritoArea.append(productoSeleccionado + "\n");
            actualizarTotal(productoSeleccionado);
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un producto.");
        }
    }

    private void actualizarTotal(String producto) {
        String[] partes = producto.split(" - ");
        if (partes.length == 2) {
            try {
                total += Double.parseDouble(partes[1]);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error al procesar el precio.");
            }
        }
    }

    private void mostrarTotal() {
        carritoArea.append("\nTotal: $" + total + "\n");
    }

    private void confirmarPago() {
        if (total == 0) {
            JOptionPane.showMessageDialog(this, "No tienes productos en el carrito.");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this, "¿Estás seguro de pagar $" + total + "?", "Confirmar Pago", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Su compra se ha realizado con éxito.");
            carritoArea.setText("");
            total = 0.0;
        }
    }

    public static void main(String[] args) {
        new MenuRestaurante("KFC");
    }
}

