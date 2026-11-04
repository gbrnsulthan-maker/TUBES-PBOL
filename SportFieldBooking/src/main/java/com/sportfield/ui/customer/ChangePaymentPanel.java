
package com.sportfield.ui.customer;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ChangePaymentPanel extends JPanel {

    private JLabel idLabel;
    private JLabel fieldLabel;
    private JLabel dateLabel;
    private JLabel timeLabel;
    private JLabel amountLabel;

    private JPanel transferCard;
    private JLabel transferStatusLabel;

    private boolean transferSelected = false;

    private Consumer<String> onConfirm;
    private Runnable onBack;

    public ChangePaymentPanel() {
        setBackground(Theme.BG_MAIN);
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        CardPanel card = new CardPanel();
        card.setPreferredSize(new Dimension(480, 620));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 36, 30, 36));

       
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(
                new BoxLayout(headerPanel, BoxLayout.Y_AXIS)
        );
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel icon = new JLabel("💳", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(
                "Ubah Metode Pembayaran",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Pilih metode pembayaran yang Anda inginkan",
                SwingConstants.CENTER
        );
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        headerPanel.add(icon);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subtitle);

       headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
       headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(headerPanel);
        card.add(Box.createVerticalStrut(22));


        // Separator
        card.add(createSeparator());
        card.add(Box.createVerticalStrut(18));

        // Detail reservasi
        JLabel detailTitle = new JLabel("Detail Reservasi");
        detailTitle.setFont(Theme.FONT_HEADING);
        detailTitle.setForeground(Theme.TEXT_PRIMARY);
        detailTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(detailTitle);
        card.add(Box.createVerticalStrut(14));

        idLabel = addRow(card, "ID Reservasi", "-");
        fieldLabel = addRow(card, "Lapangan", "-");
        dateLabel = addRow(card, "Tanggal", "-");
        timeLabel = addRow(card, "Jam", "-");

        card.add(Box.createVerticalStrut(5));
        card.add(createSeparator());
        card.add(Box.createVerticalStrut(12));

        // Total biaya
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel totalTitle = new JLabel("Total Pembayaran");
        totalTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalTitle.setForeground(Theme.TEXT_PRIMARY);

        amountLabel = new JLabel("Rp 0");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        amountLabel.setForeground(Theme.PRIMARY);

        totalRow.add(totalTitle, BorderLayout.WEST);
        totalRow.add(amountLabel, BorderLayout.EAST);

        card.add(totalRow);
        card.add(Box.createVerticalStrut(18));

        // Current method
        JLabel currentTitle = new JLabel("Metode Pembayaran Saat Ini");
        currentTitle.setFont(Theme.FONT_HEADING);
        currentTitle.setForeground(Theme.TEXT_PRIMARY);
        currentTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(currentTitle);
        card.add(Box.createVerticalStrut(10));

        card.add(createMethodCard(
                "Tunai",
                "Pembayaran dilakukan langsung di kasir.",
                false
        ));

        card.add(Box.createVerticalStrut(16));

        // New method
        JLabel newTitle = new JLabel("Metode Pembayaran Baru");
        newTitle.setFont(Theme.FONT_HEADING);
        newTitle.setForeground(Theme.TEXT_PRIMARY);
        newTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(newTitle);
        card.add(Box.createVerticalStrut(10));

        transferCard = createMethodCard(
                "Transfer",
                "Bayar sekarang untuk mengonfirmasi reservasi.",
                true
        );

        transferCard.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        transferCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                transferSelected = true;
                updateTransferCard();
            }
        });

        card.add(transferCard);
        card.add(Box.createVerticalStrut(22));

        // Buttons
        JPanel buttonPanel = new JPanel(
                new GridLayout(1, 2, 12, 0)
        );

        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44)
        );

        RoundedButton backButton = new RoundedButton(
                "Kembali",
                RoundedButton.Style.OUTLINE
        );

        RoundedButton payButton = new RoundedButton(
                "Bayar Sekarang",
                RoundedButton.Style.PRIMARY
        );

        backButton.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        payButton.addActionListener(e -> {
            if (!transferSelected) {
                JOptionPane.showMessageDialog(
                        this,
                        "Silakan pilih metode Pembayaranterlebih dahulu.",
                        "Metode Pembayaran",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Apakah Anda yakin ingin melanjutkan pembayaran melalui Transfer?",
                    "Konfirmasi Pembayaran",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION && onConfirm != null) {
                onConfirm.accept("TRANSFER");
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(payButton);

        card.add(buttonPanel);

        add(card);
    }

    // Membuat baris informasi seperti PaymentPanel
    private JLabel addRow(
            JPanel parent,
            String title,
            String value
    ) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_BODY);
        titleLabel.setForeground(Theme.TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setForeground(Theme.TEXT_PRIMARY);

        row.add(titleLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);

        parent.add(row);
        parent.add(Box.createVerticalStrut(6));

        return valueLabel;
    }

    // Kartu metode pembayaran
    private JPanel createMethodCard(
            String method,
            String description,
            boolean selectable
    ) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel methodIcon = new JLabel(
                selectable ? "💳" : "💵"
        );
        methodIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 25));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel methodLabel = new JLabel(method);
        methodLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        methodLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(Theme.FONT_SMALL);
        descriptionLabel.setForeground(Theme.TEXT_SECONDARY);

        textPanel.add(methodLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(descriptionLabel);

        JLabel statusLabel = new JLabel(
                selectable ? "Belum dipilih" : "Saat ini"
        );
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.TEXT_SECONDARY);

        panel.add(methodIcon, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.EAST);

        if (selectable) {
            panel.putClientProperty("statusLabel", statusLabel);
        }

        return panel;
    }

    // Memperbarui tampilan ketika Transfer diklik
    private void updateTransferCard() {
        if (transferSelected) {
            transferCard.setBorder(
                    BorderFactory.createCompoundBorder(
                            new LineBorder(Theme.PRIMARY, 2, true),
                            new EmptyBorder(11, 13, 11, 13)
                    )
            );

            transferStatusLabel.setText("✓ Dipilih");
            transferStatusLabel.setForeground(Theme.PRIMARY);

        } else {
            transferCard.setBorder(
                    BorderFactory.createCompoundBorder(
                            new LineBorder(Theme.BORDER, 1, true),
                            new EmptyBorder(12, 14, 12, 14)
                    )
            );

            transferStatusLabel.setText("Belum dipilih");
            transferStatusLabel.setForeground(Theme.TEXT_SECONDARY);
        }

        transferCard.repaint();
    }

    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(Theme.BORDER);
        separator.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 1)
        );
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        return separator;
    }

    // Public API
    public void setReservationDetails(
            String id,
            String field,
            String date,
            String time,
            String currentMethod,
            double total
    ) {
        idLabel.setText(id);
        fieldLabel.setText(field);
        dateLabel.setText(date);
        timeLabel.setText(time);
        amountLabel.setText(String.format("Rp %,.0f", total));

        transferSelected = false;

        transferStatusLabel = (JLabel) transferCard.getClientProperty(
                "statusLabel"
        );

        updateTransferCard();
    }

    public void setOnConfirm(Consumer<String> action) {
        this.onConfirm = action;
    }

    public void setOnBack(Runnable action) {
        this.onBack = action;
    }
}
