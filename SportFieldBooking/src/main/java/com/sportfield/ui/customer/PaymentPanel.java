package com.sportfield.ui.customer;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel bukti pembayaran berhasil (receipt).
 */
public class PaymentPanel extends JPanel {

    private JLabel txIdLabel;
    private JLabel fieldLabel;
    private JLabel dateLabel;
    private JLabel timeLabel;
    private JLabel methodLabel;
    private JLabel amountLabel;
    private JLabel statusLabel;
    private JLabel timestampLabel;

    private Runnable onViewHistory;
    private Runnable onBackToDashboard;

    public PaymentPanel() {
        setBackground(Theme.BG_MAIN);
        setLayout(new GridBagLayout()); // Center the receipt
        buildUI();
    }

    private void buildUI() {
        CardPanel card = new CardPanel();
        card.setPreferredSize(new Dimension(480, 560));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 36, 32, 36));

        // ── Success icon ──
        JLabel checkIcon = new JLabel("✅", SwingConstants.CENTER);
        checkIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        checkIcon.setAlignmentX(CENTER_ALIGNMENT);
        card.add(checkIcon);
        card.add(Box.createVerticalStrut(12));

        // ── Title ──
        JLabel title = new JLabel("Pembayaran Berhasil!", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Theme.SUCCESS);
        title.setAlignmentX(CENTER_ALIGNMENT);
        card.add(title);

        JLabel subtitle = new JLabel("Reservasi Anda telah dikonfirmasi", SwingConstants.CENTER);
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));

        // ── Separator ──
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(Theme.BORDER);
        card.add(sep);
        card.add(Box.createVerticalStrut(16));

        // ── Receipt rows ──
        txIdLabel = addRow(card, "ID Transaksi", "-");
        fieldLabel = addRow(card, "Lapangan", "-");
        dateLabel = addRow(card, "Tanggal", "-");
        timeLabel = addRow(card, "Jam", "-");
        methodLabel = addRow(card, "Metode Bayar", "-");

        card.add(Box.createVerticalStrut(8));
        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setForeground(Theme.BORDER);
        card.add(sep2);
        card.add(Box.createVerticalStrut(12));

        // Total
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setAlignmentX(LEFT_ALIGNMENT);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel totalTitle = new JLabel("Total Dibayar");
        totalTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalTitle.setForeground(Theme.TEXT_PRIMARY);

        amountLabel = new JLabel("Rp 0");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        amountLabel.setForeground(Theme.PRIMARY);

        totalRow.add(totalTitle, BorderLayout.WEST);
        totalRow.add(amountLabel, BorderLayout.EAST);
        card.add(totalRow);
        card.add(Box.createVerticalStrut(8));

        // Status badge
        JPanel statusRow = new JPanel(new BorderLayout());
        statusRow.setOpaque(false);
        statusRow.setAlignmentX(LEFT_ALIGNMENT);
        statusRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel statusTitle = new JLabel("Status");
        statusTitle.setFont(Theme.FONT_BODY);
        statusTitle.setForeground(Theme.TEXT_SECONDARY);

        statusLabel = new JLabel("DIKONFIRMASI");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(Theme.SUCCESS);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(220, 252, 231));
        statusLabel.setBorder(new EmptyBorder(3, 10, 3, 10));

        statusRow.add(statusTitle, BorderLayout.WEST);
        statusRow.add(statusLabel, BorderLayout.EAST);
        card.add(statusRow);

        timestampLabel = addRow(card, "Waktu Bayar", "-");

        card.add(Box.createVerticalStrut(24));

        // ── Buttons ──
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        RoundedButton historyBtn = new RoundedButton("📜 Lihat Riwayat", RoundedButton.Style.PRIMARY);
        historyBtn.addActionListener(e -> { if (onViewHistory != null) onViewHistory.run(); });

        RoundedButton dashBtn = new RoundedButton("🏠 Dashboard", RoundedButton.Style.OUTLINE);
        dashBtn.addActionListener(e -> { if (onBackToDashboard != null) onBackToDashboard.run(); });

        btnPanel.add(historyBtn);
        btnPanel.add(dashBtn);
        card.add(btnPanel);

        add(card);
    }

    private JLabel addRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_BODY);
        lbl.setForeground(Theme.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setForeground(Theme.TEXT_PRIMARY);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        parent.add(row);
        parent.add(Box.createVerticalStrut(6));
        return val;
    }

    // ── Public API ──

    public void setPaymentDetails(String txId, String field, String date,
            String time, String method, double amount, String timestamp) {
        txIdLabel.setText(txId);
        fieldLabel.setText(field);
        dateLabel.setText(date);
        timeLabel.setText(time);
        methodLabel.setText(method);
        amountLabel.setText(String.format("Rp %,.0f", amount));
        timestampLabel.setText(timestamp);
    }

    public void setOnViewHistory(Runnable r) { this.onViewHistory = r; }
    public void setOnBackToDashboard(Runnable r) { this.onBackToDashboard = r; }
}
