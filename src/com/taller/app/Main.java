package com.taller.app;

import com.taller.vista.VentanaTaller;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Esto inicia la ventana
        SwingUtilities.invokeLater(() -> {
            VentanaTaller ventana = new VentanaTaller();
            ventana.setVisible(true);
        });
    }
}