package org;

import com.formdev.flatlaf.FlatDarkLaf;
import org.view.LoadingScreen;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Setup Look and Feel (FlatLaf Dark)
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("Gagal menginisialisasi Look and Feel: " + e.getMessage());
        }

        // Launch Loading Screen
        SwingUtilities.invokeLater(() -> {
            new LoadingScreen().startLoading();
        });
    }
}