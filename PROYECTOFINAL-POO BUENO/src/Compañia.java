import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Compañia extends JFrame {
    private JComboBox<String> restaurantComboBox;
    private JTextField productNameField, priceField;
    private JButton addButton, backButton;

    public Compañia() {
        setTitle("Añadir Productos a Menú");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        JLabel restaurantLabel = new JLabel("Restaurante:");
        restaurantComboBox = new JComboBox<>(getRestaurantNames());
        JLabel productLabel = new JLabel("Producto:");
        productNameField = new JTextField();
        JLabel priceLabel = new JLabel("Precio:");
        priceField = new JTextField();
        addButton = new JButton("Añadir Producto");
        backButton = new JButton("Volver");

        add(restaurantLabel); add(restaurantComboBox);
        add(productLabel); add(productNameField);
        add(priceLabel); add(priceField);
        add(addButton); add(backButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductToMenu();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private String[] getRestaurantNames() {
        ArrayList<String> restaurantList = new ArrayList<>();
        try (Connection conn = FTDB.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT nombre FROM restaurantes");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                restaurantList.add(rs.getString("nombre"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener restaurantes: " + ex.getMessage());
        }
        return restaurantList.toArray(new String[0]);
    }

    private void addProductToMenu() {
        String restaurante = (String) restaurantComboBox.getSelectedItem();
        String producto = productNameField.getText();
        String precio = priceField.getText();

        if (producto.isEmpty() || precio.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, completa todos los campos.");
            return;
        }

        try (Connection conn = FTDB.getConnection()) {
            PreparedStatement getRestaurantId = conn.prepareStatement("SELECT id FROM restaurantes WHERE nombre = ?");
            getRestaurantId.setString(1, restaurante);
            ResultSet rs = getRestaurantId.executeQuery();

            if (rs.next()) {
                int restaurantId = rs.getInt("id");
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO menu (restaurante_id, nombre_platillo, precio) VALUES (?, ?, ?)");
                stmt.setInt(1, restaurantId);
                stmt.setString(2, producto);
                stmt.setDouble(3, Double.parseDouble(precio));
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Producto añadido con éxito.");
            }
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Error al añadir producto: " + ex.getMessage());
        }
    }
}
