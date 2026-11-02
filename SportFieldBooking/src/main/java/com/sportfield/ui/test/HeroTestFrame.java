package com.sportfield.ui.test;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.WebBrowserPanel;

import javax.swing.*;
import java.awt.*;

public class HeroTestFrame extends JFrame {

    public HeroTestFrame() {
        setTitle("Tilted Grid Hero Test - SportField");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BG_MAIN);

        JLabel titleLabel = new JLabel("Hero Section Integration Test", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        WebBrowserPanel webPanel = new WebBrowserPanel("hero-demo");
        mainPanel.add(webPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
        } catch (Exception e) {
            System.err.println("FlatLaf failed: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            HeroTestFrame frame = new HeroTestFrame();
            frame.setVisible(true);
        });
    }
}
