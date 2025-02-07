import javax.swing.*;
import java.awt.*;

public class Restaurantes extends JFrame {
    private JButton btnVolver;
    private String categoria;
    private JPanel panelRestaurantes;

    public Restaurantes() {
        this.categoria = "Comida Rápida";
        setTitle("Restaurantes - " + categoria);

        // Crear panel para los botones
        panelRestaurantes = new JPanel(new GridLayout(0, 3, 10, 10)); // Filas dinámicas, 3 columnas

        // Lista de restaurantes y sus imágenes
        String[] restaurantes;
        String[] imagenes;

        if (categoria.equals("Comida Rápida")) {
            restaurantes = new String[]{"Burger King", "McDonald's", "KFC"};
            imagenes = new String[]{"imagenes/burgerking.jpeg", "imagenes/mcdonalds.png", "imagenes/kfc.jpeg"};
        } else if (categoria.equals("Helados y Postres")) {
            restaurantes = new String[]{"Juan Valdez", "Sweet and Coffee", "Bogati EC"};
            imagenes = new String[]{"imagenes/juanvaldez.jpg", "imagenes/sweetcoffee.jpeg", "imagenes/bogati.jpeg"};
        } else if (categoria.equals("Parrilladas")) {
            restaurantes = new String[]{"La Parrilla del Tio Jessy", "Las Menestras del Negro"};
            imagenes = new String[]{"imagenes/tiojessy.jpeg", "imagenes/menestras.png"};
        } else {
            restaurantes = new String[]{"No hay restaurantes disponibles"};
            imagenes = new String[]{"imagenes/no_disponible.jpg"};
        }

        // Crear botones con imágenes y etiquetas
        for (int i = 0; i < restaurantes.length; i++) {
            final String restaurante = restaurantes[i];
            JButton boton = crearBoton(restaurante, imagenes[i]);
            boton.addActionListener(e -> abrirMenuRestaurante(restaurante));
            panelRestaurantes.add(boton);
        }

        // Etiqueta con la categoría
        JLabel titulo = new JLabel("Restaurantes que venden " + categoria, SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));

        // Botón para volver a la ventana principal
        btnVolver = new JButton("Volver");
        btnVolver.addActionListener(e -> {
            new VentanaPrincipal();
            dispose();
        });

        // Diseño del panel
        setLayout(new BorderLayout());
        add(titulo, BorderLayout.NORTH);
        add(panelRestaurantes, BorderLayout.CENTER);
        add(btnVolver, BorderLayout.SOUTH);

        // Configuración de la ventana
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JButton crearBoton(String texto, String imagenes) {
        ImageIcon icono = new ImageIcon(imagenes);
        Image imagen = icono.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        icono = new ImageIcon(imagen);

        JButton boton = new JButton(icono);
        boton.setText(texto);
        boton.setHorizontalTextPosition(SwingConstants.CENTER);
        boton.setVerticalTextPosition(SwingConstants.BOTTOM);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);

        return boton;
    }

    private void abrirMenuRestaurante(String restaurante) {
        new MenuRestaurante(restaurante);
        dispose();
    }

    public static void main(String[] args) {
        new Restaurantes();
    }
}

