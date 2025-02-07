import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private JButton button1, button2, button3;

    public VentanaPrincipal() {
        setTitle("Categorías de Restaurantes"); // Título de la ventana
        
        // Crear los botones con imágenes
        button1 = crearBoton("Comida Rápida", "imagenes/comida_rapida.jpg");
        button2 = crearBoton("Helados", "imagenes/helados.jpeg");
        button3 = crearBoton("Parrilladas", "imagenes/parrilladas.jpg");

        // Crear etiquetas debajo de los botones
        JLabel label1 = new JLabel("Comida Rápida", SwingConstants.CENTER);
        JLabel label2 = new JLabel("Helados", SwingConstants.CENTER);
        JLabel label3 = new JLabel("Parrilladas", SwingConstants.CENTER);

        // Estilizar las etiquetas
        label1.setFont(new Font("Arial", Font.BOLD, 14));
        label2.setFont(new Font("Arial", Font.BOLD, 14));
        label3.setFont(new Font("Arial", Font.BOLD, 14));

        // Crear panel para los botones y etiquetas
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10)); // 2 filas, 3 columnas
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(label1);
        panel.add(label2);
        panel.add(label3);

        // Agregar evento a los botones
        button1.addActionListener(e -> abrirRestaurante("Comida Rápida"));
        button2.addActionListener(e -> abrirRestaurante("Helados"));
        button3.addActionListener(e -> abrirRestaurante("Parrilladas"));

        // Agregar título encima del panel
        JLabel titulo = new JLabel("Seleccione una categoría", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Configurar layout principal
        setLayout(new BorderLayout());
        add(titulo, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // Configuración de la ventana
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Método para crear botones con imágenes
    private JButton crearBoton(String texto, String rutaImagen) {
        ImageIcon icono = new ImageIcon(rutaImagen); // Cargar la imagen
        Image imagen = icono.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Redimensionar
        icono = new ImageIcon(imagen);
        
        JButton boton = new JButton(icono);
        boton.setPreferredSize(new Dimension(150, 150)); // Tamaño del botón
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        
        return boton;
    }

    // Método para abrir la ventana de restaurantes
    private void abrirRestaurante(String categoria) {
        new Restaurantes(categoria);
        this.dispose();
    }

    public static void main(String[] args) {
        new VentanaPrincipal();
    }
}
