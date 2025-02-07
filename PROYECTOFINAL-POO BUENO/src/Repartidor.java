import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Repartidor extends JFrame {
    private JComboBox<String> pedidosComboBox, vehiculosComboBox;
    private JButton iniciarEntregaButton, entregarButton, backButton;

    public Repartidor() {
        setTitle("Gestión de Entregas");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        JLabel pedidosLabel = new JLabel("Pedidos Disponibles:");
        pedidosComboBox = new JComboBox<>(getPedidosDisponibles());
        JLabel vehiculosLabel = new JLabel("Seleccionar Vehículo:");
        vehiculosComboBox = new JComboBox<>(new String[]{"Moto 005", "Moto 008"});
        iniciarEntregaButton = new JButton("Iniciar Entrega");
        entregarButton = new JButton("Entregar Pedido");
        backButton = new JButton("Volver");
        entregarButton.setEnabled(false); // Deshabilitado hasta iniciar entrega

        add(pedidosLabel); add(pedidosComboBox);
        add(vehiculosLabel); add(vehiculosComboBox);
        add(iniciarEntregaButton); add(entregarButton);
        add(backButton);

        iniciarEntregaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                entregarButton.setEnabled(true);
                JOptionPane.showMessageDialog(null, "Entrega iniciada. Ahora puedes entregar el pedido.");
            }
        });

        entregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Entrega exitosa.");
                dispose();
                new Repartidor();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginUI();
                dispose();
            }
        });

        setVisible(true);
    }

    private String[] getPedidosDisponibles() {
        ArrayList<String> pedidosList = new ArrayList<>();
        try (Connection conn = FTDB.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, total FROM pedidos WHERE estado = 'PENDIENTE'");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pedidosList.add("Pedido #" + rs.getInt("id") + " - Total: $" + rs.getDouble("total"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener pedidos: " + ex.getMessage());
        }
        return pedidosList.toArray(new String[0]);
    }

    public static void main(String[] args) {
        new Repartidor();
    }
}

