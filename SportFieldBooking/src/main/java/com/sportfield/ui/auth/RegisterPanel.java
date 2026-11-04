package com.sportfield.ui.auth;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.RoundedButton;
import com.sportfield.ui.components.RoundedPasswordField;
import com.sportfield.ui.components.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;

/**
 * Halaman registrasi. Gaya mengikuti LoginPanel:
 * panel hijau di kiri, form putih di kanan.
 */
public class RegisterPanel extends JPanel {

    private final RoundedTextField namaField;
    private final RoundedTextField emailField;
    private final RoundedTextField noHpField;
    private final RoundedPasswordField passwordField;
    private final RoundedPasswordField confirmPasswordField;
    private final JLabel errorLabel;
    private final Runnable onRegister;
    private final Runnable onSwitchToLogin;
    private final JPanel heroPanel;

    public RegisterPanel(Runnable onRegister, Runnable onSwitchToLogin) {
        this.onRegister = onRegister;
        this.onSwitchToLogin = onSwitchToLogin;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        heroPanel = new LoginPanel.HeroPanel(
            "JOIN THE GAME",
            "<html>Buat akun.<br>Pesan lapangan.</html>",
            "<html>Daftar gratis dan mulai booking<br>lapangan favoritmu hari ini.</html>"
        );
        heroPanel.setPreferredSize(new Dimension(560, Theme.WINDOW_HEIGHT));
        add(heroPanel, BorderLayout.WEST);

        JPanel formArea = new JPanel(new GridBagLayout());
        formArea.setBackground(Color.WHITE);
        formArea.setBorder(new EmptyBorder(32, 48, 32, 48));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(430, 640));
        form.setMaximumSize(new Dimension(430, 640));

        JLabel brand = new JLabel("SPORTFIELD");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 15));
        brand.setForeground(Theme.PRIMARY);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(brand);
        form.add(Box.createVerticalStrut(26));

        JLabel title = new JLabel("Daftar");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(title);

        JLabel subtitle = new JLabel("Buat akun baru untuk mulai memesan lapangan.");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(Box.createVerticalStrut(8));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(24));

        // Nama lengkap
        form.add(createFieldLabel("Nama Lengkap"));
        form.add(Box.createVerticalStrut(8));
        namaField = new RoundedTextField("Nama lengkap Anda");
        styleInput(namaField);
        form.add(namaField);
        form.add(Box.createVerticalStrut(14));

        // Email
        form.add(createFieldLabel("Email"));
        form.add(Box.createVerticalStrut(8));
        emailField = new RoundedTextField("nama@email.com");
        styleInput(emailField);
        form.add(emailField);
        form.add(Box.createVerticalStrut(14));

        // Nomor HP
        form.add(createFieldLabel("Nomor HP"));
        form.add(Box.createVerticalStrut(8));
        noHpField = new RoundedTextField("08xxxxxxxxxx");
        styleInput(noHpField);
        ((AbstractDocument) noHpField.getDocument()).setDocumentFilter(new PhoneFilter());
        form.add(noHpField);
        form.add(Box.createVerticalStrut(14));

        // Password + konfirmasi (berdampingan)
        passwordField = new RoundedPasswordField("Min. 6 karakter");
        styleInput(passwordField);
        confirmPasswordField = new RoundedPasswordField("Ulangi password");
        styleInput(confirmPasswordField);

        JPanel passwordRow = new JPanel(new GridLayout(1, 2, 14, 0));
        passwordRow.setOpaque(false);
        passwordRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));
        passwordRow.add(labeled("Password", passwordField));
        passwordRow.add(labeled("Konfirmasi Password", confirmPasswordField));
        form.add(passwordRow);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_SMALL);
        errorLabel.setForeground(Theme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setVisible(false);
        form.add(Box.createVerticalStrut(10));
        form.add(errorLabel);
        form.add(Box.createVerticalStrut(12));

        RoundedButton registerButton = new RoundedButton("Daftar", RoundedButton.Style.PRIMARY);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.addActionListener(e -> fireRegister());
        form.add(registerButton);

        JPanel linkRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        linkRow.setOpaque(false);
        linkRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel prompt = new JLabel("Sudah punya akun?");
        prompt.setFont(Theme.FONT_SMALL);
        prompt.setForeground(Theme.TEXT_SECONDARY);
        linkRow.add(prompt);

        JLabel loginLink = new JLabel("Masuk");
        loginLink.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        loginLink.setForeground(Theme.PRIMARY);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (RegisterPanel.this.onSwitchToLogin != null) {
                    RegisterPanel.this.onSwitchToLogin.run();
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
        linkRow.add(loginLink);
        form.add(Box.createVerticalStrut(18));
        form.add(linkRow);

        formArea.add(form);

        // Dibungkus scroll supaya tetap bisa diakses jika jendela dikecilkan
        JScrollPane scroll = new JScrollPane(formArea);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

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
        noHpField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);
        confirmPasswordField.addKeyListener(enterKey);

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

    /** Label + input dalam satu kolom (dipakai untuk baris password). */
    private JPanel labeled(String text, JComponent input) {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(createFieldLabel(text));
        box.add(Box.createVerticalStrut(8));
        box.add(input);
        return box;
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

    /** Hanya mengizinkan angka (dan "+" di awal), maksimal 15 karakter. */
    private static class PhoneFilter extends DocumentFilter {
        private static final int MAX_LENGTH = 15;

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                throws BadLocationException {
            replace(fb, offset, 0, text, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }
            StringBuilder clean = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                boolean leadingPlus = c == '+' && offset == 0 && i == 0;
                if (Character.isDigit(c) || leadingPlus) {
                    clean.append(c);
                }
            }
            int room = MAX_LENGTH - (fb.getDocument().getLength() - length);
            if (room <= 0 && clean.length() > 0) {
                return;
            }
            if (clean.length() > room) {
                clean.setLength(Math.max(room, 0));
            }
            super.replace(fb, offset, length, clean.toString(), attrs);
        }
    }

    public String getNama() {
        return namaField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getNoHp() {
        return noHpField.getText().trim();
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
        noHpField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        errorLabel.setText(" ");
        errorLabel.setForeground(Theme.DANGER);
        errorLabel.setVisible(false);
        revalidate();
        repaint();
    }

    private void fireRegister() {
        errorLabel.setText(" ");
        errorLabel.setVisible(false);
        if (onRegister != null) {
            onRegister.run();
        }
    }
}