package com.sportfield.ui.auth;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;
import com.sportfield.ui.components.RoundedPasswordField;
import com.sportfield.ui.components.RoundedTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class RegisterPanel extends JPanel {

    private final RoundedTextField namaField;
    private final RoundedTextField emailField;
    private final RoundedPasswordField passwordField;
    private final RoundedPasswordField confirmPasswordField;
    private final JLabel errorLabel;
    private final Runnable onRegister;
    private final Runnable onSwitchToLogin;

    public RegisterPanel(Runnable onRegister, Runnable onSwitchToLogin) {
        this.onRegister = onRegister;
        this.onSwitchToLogin = onSwitchToLogin;

        setPreferredSize(new Dimension(Theme.WINDOW_WIDTH, Theme.WINDOW_HEIGHT));
        setLayout(new GridBagLayout());
        setOpaque(false);

        CardPanel card = new CardPanel(Theme.CARD_RADIUS, true);
        card.setGlassStyle(true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(460, 660));
        card.setMaximumSize(new Dimension(460, 660));
        card.setBorder(BorderFactory.createEmptyBorder(48, 48, 48, 48));
        card.setBackground(Theme.BG_CARD);

        JLabel logo = new JLabel("⚽ SportField");
        logo.setFont(Theme.FONT_TITLE.deriveFont(Font.BOLD, 32f));
        logo.setForeground(Theme.PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(logo);

        card.add(Box.createVerticalStrut(12));

        JLabel subtitle = new JLabel("Buat Akun Baru");
        subtitle.setFont(Theme.FONT_SUBTITLE);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitle);

        card.add(Box.createVerticalStrut(32));

        namaField = new RoundedTextField("Nama lengkap");
        namaField.setGlassStyle(true);
        namaField.setFont(Theme.FONT_BODY);
        namaField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        namaField.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(namaField);

        card.add(Box.createVerticalStrut(14));

        emailField = new RoundedTextField("Email Anda");
        emailField.setGlassStyle(true);
        emailField.setFont(Theme.FONT_BODY);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(emailField);

        card.add(Box.createVerticalStrut(14));

        passwordField = new RoundedPasswordField("Password (min. 6 karakter)");
        passwordField.setGlassStyle(true);
        passwordField.setFont(Theme.FONT_BODY);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(passwordField);

        card.add(Box.createVerticalStrut(14));

        confirmPasswordField = new RoundedPasswordField("Konfirmasi password");
        confirmPasswordField.setGlassStyle(true);
        confirmPasswordField.setFont(Theme.FONT_BODY);
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(confirmPasswordField);

        card.add(Box.createVerticalStrut(12));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(Theme.DANGER);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);
        card.add(errorLabel);

        card.add(Box.createVerticalStrut(12));

        RoundedButton registerButton = new RoundedButton("Daftar", RoundedButton.Style.GRADIENT);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> fireRegister());
        card.add(registerButton);

        card.add(Box.createVerticalStrut(24));

        JPanel linkRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        linkRow.setOpaque(false);
        linkRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel prompt = new JLabel("Sudah punya akun?");
        prompt.setFont(Theme.FONT_SMALL);
        prompt.setForeground(Theme.TEXT_SECONDARY);

        JLabel loginLink = new JLabel("Masuk");
        loginLink.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        loginLink.setForeground(Theme.PRIMARY);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onSwitchToLogin != null) {
                    onSwitchToLogin.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginLink.setText("<html><u>Masuk</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginLink.setText("Masuk");
            }
        });

        linkRow.add(prompt);
        linkRow.add(loginLink);
        card.add(linkRow);

        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    fireRegister();
                }
            }
        };
        namaField.addKeyListener(enterKey);
        emailField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);
        confirmPasswordField.addKeyListener(enterKey);

        add(card);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(245, 247, 250),
            getWidth(), getHeight(), new Color(230, 240, 255)
        );
        g2.setPaint(gradient);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 0, 0));

        g2.dispose();
        super.paintComponent(g);
    }

    public String getNama() {
        return namaField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    public void setError(String message) {
        errorLabel.setForeground(Theme.DANGER);
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        revalidate();
        repaint();
    }

    public void setSuccess(String message) {
        errorLabel.setForeground(Theme.SUCCESS);
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        revalidate();
        repaint();
    }

    public void clearFields() {
        namaField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        errorLabel.setText(" ");
        errorLabel.setForeground(Theme.DANGER);
        errorLabel.setVisible(false);
        revalidate();
        repaint();
    }

    private void fireRegister() {
        if (onRegister != null) {
            onRegister.run();
        }
    }
}
