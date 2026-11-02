package com.sportfield;

import com.formdev.flatlaf.FlatLightLaf;
import com.sportfield.ui.MainFrame;

import javax.swing.*;

/**
 * Entry point aplikasi SportField Booking.
 */
public class Main {

    public static void main(String[] args) {
        // Setup FlatLaf Look and Feel
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.trackArc", 999);
            UIManager.put("ScrollBar.width", 8);
        } catch (Exception e) {
            System.err.println("FlatLaf gagal dimuat, menggunakan default: " + e.getMessage());
        }

        // Launch UI on EDT
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
