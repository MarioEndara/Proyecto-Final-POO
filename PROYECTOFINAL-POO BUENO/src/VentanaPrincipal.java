import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private JButton button1, button2, button3;

    public VentanaPrincipal() {
        setTitle("PRODUCTOS DESTACADOS"); // Título de la ventana

        //Crear los botones con imágenes más grandes
        button1 = crearBoton("Comida Rápida", "imagenes/comida_rapida.jpg");
        button2 = crearBoton("Helados y Postres", "imagenes/helados.jpeg");
        button3 = crearBoton("Parrilladas", "imagenes/parrilladas.jpg");

        // Crear etiquetas debajo de los botones
        JLabel label1 = new JLabel("Comida Rápida", SwingConstants.CENTER);
        JLabel label2 = new JLabel("Helados y Postres", SwingConstants.CENTER);
        JLabel label3 = new JLabel("Parrilladas", SwingConstants.CENTER);

        //Estilizar las etiquetas
        Font fontLabels = new Font("Arial", Font.BOLD, 14);
        label1.setFont(fontLabels);
        label2.setFont(fontLabels);
        label3.setFont(fontLabels);

        //Crear panel para los botones y etiquetas
        JPanel panel = new JPanel(new GridLayout(2, 3, 5, 5)); // Menos separación entre botones
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(label1);
        panel.add(label2);
        panel.add(label3);

        // Agregar eventos a los botones
        button1.addActionListener(e -> abrirRestaurante("Comida Rápida"));
        button2.addActionListener(e -> abrirRestaurante("Helados y Postres"));
        button3.addActionListener(e -> abrirRestaurante("Parrilladas"));

        // Crear panel para los títulos
        JPanel panelTitulo = new JPanel(new GridLayout(2, 1));
        JLabel titulo = new JLabel("¿Qué deseas ordenar?", SwingConstants.CENTER);
        JLabel titulo2 = new JLabel("Productos que te podrían gustar:", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo2.setFont(new Font("Arial", Font.PLAIN, 14));
        panelTitulo.add(titulo);
        panelTitulo.add(titulo2);

        // Configurar layout principal
        setLayout(new BorderLayout());
        add(panelTitulo, BorderLayout.NORTH); // Ahora los títulos sí aparecen
        add(panel, BorderLayout.CENTER);

        // Configuración de la ventana
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    //Método para crear botones con imágenes más grandes
    private JButton crearBoton(String texto, String imagenes) {
        ImageIcon icono = new ImageIcon(imagenes);
        Image imagen = icono.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Tamaño ajustado
        icono = new ImageIcon(imagen);

        JButton boton = new JButton(icono);
        boton.setPreferredSize(new Dimension(150, 150));
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);

        return boton;
    }

    // Abrir el nuevo formulario
    private void abrirRestaurante(String categoria) {
        new Restaurantes();
        this.dispose();
    }

    //Iniciar la ventana principal
    public static void main(String[] args) {
        new VentanaPrincipal();
    }
}


