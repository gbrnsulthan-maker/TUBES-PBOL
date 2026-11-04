package com.sportfield.ui.auth;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.RoundedButton;
import com.sportfield.ui.components.RoundedPasswordField;
import com.sportfield.ui.components.RoundedTextField;
import com.sportfield.ui.components.CardFanCarousel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

public class LoginPanel extends JPanel {

    private final RoundedTextField emailField;
    private final RoundedPasswordField passwordField;
    private final JLabel errorLabel;
    private final Runnable onLogin;
    private final Runnable onSwitchToRegister;
    private final JPanel heroPanel;

    public LoginPanel(Runnable onLogin, Runnable onSwitchToRegister) {
        this.onLogin = onLogin;
        this.onSwitchToRegister = onSwitchToRegister;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        heroPanel = new HeroPanel();
        heroPanel.setPreferredSize(new Dimension(560, Theme.WINDOW_HEIGHT));
        add(heroPanel, BorderLayout.WEST);

        JPanel formArea = new JPanel(new GridBagLayout());
        formArea.setBackground(Color.WHITE);
        formArea.setBorder(new EmptyBorder(40, 48, 40, 48));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setMaximumSize(new Dimension(430, 560));

        JLabel brand = new JLabel("SPORTFIELD");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 15));
        brand.setForeground(Theme.PRIMARY);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(brand);
        form.add(Box.createVerticalStrut(38));

        JLabel title = new JLabel("Masuk");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(title);

        JLabel subtitle = new JLabel("Selamat datang kembali! Silakan masuk untuk melanjutkan.");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(Box.createVerticalStrut(8));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(30));

        JLabel emailLabel = createFieldLabel("Email");
        form.add(emailLabel);
        form.add(Box.createVerticalStrut(8));

        emailField = new RoundedTextField("nama@email.com");
        styleInput(emailField);
        form.add(emailField);
        form.add(Box.createVerticalStrut(18));

        JPanel passwordHeader = new JPanel(new BorderLayout());
        passwordHeader.setOpaque(false);
        passwordHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        passwordHeader.add(createFieldLabel("Password"), BorderLayout.WEST);

        JLabel forgotLink = new JLabel("Lupa password?");
        forgotLink.setFont(Theme.FONT_SMALL);
        forgotLink.setForeground(Theme.PRIMARY);
        forgotLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                    LoginPanel.this,
                    "Silakan hubungi admin untuk bantuan mengatur ulang password.",
                    "Bantuan password",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotLink.setText("<html><u>Lupa password?</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotLink.setText("Lupa password?");
            }
        });
        passwordHeader.add(forgotLink, BorderLayout.EAST);
        form.add(passwordHeader);
        form.add(Box.createVerticalStrut(8));

        passwordField = new RoundedPasswordField("Masukkan password");
        styleInput(passwordField);
        form.add(passwordField);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(Theme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setVisible(false);
        form.add(Box.createVerticalStrut(10));
        form.add(errorLabel);

        JCheckBox rememberCheckbox = new JCheckBox("Ingat saya");
        rememberCheckbox.setFont(Theme.FONT_SMALL);
        rememberCheckbox.setForeground(Theme.TEXT_SECONDARY);
        rememberCheckbox.setOpaque(false);
        rememberCheckbox.setFocusPainted(false);
        rememberCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(Box.createVerticalStrut(10));
        form.add(rememberCheckbox);
        form.add(Box.createVerticalStrut(22));

        RoundedButton loginButton = new RoundedButton("Masuk", RoundedButton.Style.PRIMARY);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> fireLogin());
        form.add(loginButton);

        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        registerRow.setOpaque(false);
        registerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel registerPrompt = new JLabel("Belum punya akun?");
        registerPrompt.setFont(Theme.FONT_SMALL);
        registerPrompt.setForeground(Theme.TEXT_SECONDARY);
        registerRow.add(registerPrompt);

        JLabel registerLink = new JLabel("Daftar");
        registerLink.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        registerLink.setForeground(Theme.PRIMARY);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (LoginPanel.this.onSwitchToRegister != null) {
                    LoginPanel.this.onSwitchToRegister.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                registerLink.setText("<html><u>Daftar</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerLink.setText("Daftar");
            }
        });
        registerRow.add(registerLink);
        form.add(Box.createVerticalStrut(22));
        form.add(registerRow);

        formArea.add(form);
        add(formArea, BorderLayout.CENTER);

        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    fireLogin();
                }
            }
        };
        emailField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                boolean showHero = getWidth() >= 1100;
                if (heroPanel.isVisible() != showHero) {
                    heroPanel.setVisible(showHero);
                    revalidate();
                }
            }
        });
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
        label.setForeground(Theme.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleInput(JComponent field) {
        if (field instanceof RoundedTextField) {
            ((RoundedTextField) field).setGlassStyle(false);
        } else if (field instanceof RoundedPasswordField) {
            ((RoundedPasswordField) field).setGlassStyle(false);
        }
        field.setFont(Theme.FONT_BODY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        revalidate();
        repaint();
    }

    public void clearFields() {
        emailField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
        errorLabel.setVisible(false);
        revalidate();
        repaint();
    }

    private void fireLogin() {
        errorLabel.setText(" ");
        errorLabel.setVisible(false);
        if (onLogin != null) {
            onLogin.run();
        }
    }

    // Panel hijau di sisi kiri. Dipakai juga oleh RegisterPanel (satu package).
    static class HeroPanel extends JPanel {

        HeroPanel() {
            this("BOOK YOUR GAME",
                "<html>Temukan lapangan.<br>Mulai permainanmu.</html>",
                "<html>Pesan lapangan favoritmu dengan mudah<br>dan nikmati setiap pertandingan.</html>");
        }

        HeroPanel(String eyebrowText, String headlineHtml, String detailHtml) {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(0, 48, 54, 46));
            setOpaque(false);

            JPanel copy = new JPanel();
            copy.setOpaque(false);
            copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));

            JLabel eyebrow = new JLabel(eyebrowText);
            eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 12));
            eyebrow.setForeground(new Color(220, 255, 227));
            eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);
            copy.add(eyebrow);
            copy.add(Box.createVerticalStrut(12));

            JLabel headline = new JLabel(headlineHtml);
            headline.setFont(new Font("Segoe UI", Font.BOLD, 32));
            headline.setForeground(Color.WHITE);
            headline.setAlignmentX(Component.LEFT_ALIGNMENT);
            copy.add(headline);
            copy.add(Box.createVerticalStrut(12));

            JLabel detail = new JLabel(detailHtml);
            detail.setFont(Theme.FONT_BODY);
            detail.setForeground(new Color(225, 242, 229));
            detail.setAlignmentX(Component.LEFT_ALIGNMENT);
            copy.add(detail);

            add(copy, BorderLayout.SOUTH);
            add(new CardFanCarousel(), BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();

            g2.setPaint(new GradientPaint(0, 0, new Color(17, 61, 54), width, height, new Color(22, 126, 78)));
            g2.fillRect(0, 0, width, height);

            g2.setColor(new Color(255, 255, 255, 14));
            g2.fill(new Ellipse2D.Double(width * 0.53, height * 0.09, width * 0.68, width * 0.68));
            g2.setColor(new Color(255, 255, 255, 20));
            g2.fill(new Ellipse2D.Double(width * 0.67, height * 0.24, width * 0.48, width * 0.48));

            g2.dispose();
        }
    }
}