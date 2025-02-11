package org.example;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Connection conexion = DatabaseConnection.conectar();
                if (conexion != null) {
                    System.out.println("Se puede conectar");
                } else {
                    System.out.println("Ni madres estas mal");
                }
                new Login();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al iniciar la aplicación.");
            }
        });
    }
}
