
package com.sportfield.ui.customer;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;
import com.sportfield.ui.components.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ReservationFormPanel extends JPanel {

    private JLabel fieldNameLabel;
    private JLabel fieldTypeLabel;
    private JLabel dateLabel;
    private JLabel timeLabel;
    private JLabel durationLabel;
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel totalLabel;

    private JRadioButton cashRadio;
    private JRadioButton transferRadio;
    private JPanel transferFields;
    private RoundedTextField bankNameField;
    private RoundedTextField accountField;

    private RoundedButton confirmBtn;

    private Runnable onConfirm;
    private Runnable onBack;

    public ReservationFormPanel() {
        setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(
                Theme.PADDING,
                Theme.PADDING,
                Theme.PADDING,
                Theme.PADDING
        ));
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel title = new JLabel("Konfirmasi Reservasi");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        content.add(title);

        JLabel subtitle = new JLabel(
                "Periksa detail dan pilih metode pembayaran"
        );
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(20));

        JPanel columns = new JPanel(new GridLayout(1, 2, 20, 0));
        columns.setOpaque(false);
        columns.setAlignmentX(LEFT_ALIGNMENT);
        columns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        columns.add(buildDetailsCard());
        columns.add(buildPaymentCard());

        content.add(columns);
        content.add(Box.createVerticalStrut(24));

        JPanel btnPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 0)
        );
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        RoundedButton backBtn = new RoundedButton(
                "← Kembali",
                RoundedButton.Style.SECONDARY
        );

        backBtn.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });

        confirmBtn = new RoundedButton(
                "Buat Reservasi",
                RoundedButton.Style.PRIMARY
        );

        confirmBtn.setPreferredSize(new Dimension(200, 44));

        confirmBtn.addActionListener(e -> {
            if (onConfirm != null) onConfirm.run();
        });

        btnPanel.add(backBtn);
        btnPanel.add(confirmBtn);
        content.add(btnPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Theme.BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(scrollPane, BorderLayout.CENTER);
    }

    private CardPanel buildDetailsCard() {
        CardPanel card = new CardPanel();
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel header = new JLabel("📋 Detail Reservasi");
        header.setFont(Theme.FONT_SUBTITLE);
        header.setForeground(Theme.TEXT_PRIMARY);
        header.setAlignmentX(LEFT_ALIGNMENT);
        card.add(header);
        card.add(Box.createVerticalStrut(16));

        fieldNameLabel = addInfoRow(card, "Lapangan", "-");
        fieldTypeLabel = addInfoRow(card, "Jenis", "-");
        dateLabel = addInfoRow(card, "Tanggal", "-");
        timeLabel = addInfoRow(card, "Jam", "-");
        durationLabel = addInfoRow(card, "Durasi", "-");

        card.add(Box.createVerticalStrut(12));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(Theme.BORDER);
        card.add(sep);
        card.add(Box.createVerticalStrut(12));

        subtotalLabel = addInfoRow(card, "Subtotal", "Rp 0");
        discountLabel = addInfoRow(card, "Diskon", "- Rp 0");

        card.add(Box.createVerticalStrut(8));

        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setForeground(Theme.BORDER);
        card.add(sep2);
        card.add(Box.createVerticalStrut(12));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setAlignmentX(LEFT_ALIGNMENT);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel totalTitle = new JLabel("Total Pembayaran");
        totalTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalTitle.setForeground(Theme.TEXT_PRIMARY);

        totalLabel = new JLabel("Rp 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(Theme.PRIMARY);

        totalRow.add(totalTitle, BorderLayout.WEST);
        totalRow.add(totalLabel, BorderLayout.EAST);
        card.add(totalRow);

        return card;
    }

    private JLabel addInfoRow(
            JPanel parent,
            String label,
            String value
    ) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_BODY);
        lbl.setForeground(Theme.TEXT_SECONDARY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setForeground(Theme.TEXT_PRIMARY);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        parent.add(row);
        parent.add(Box.createVerticalStrut(6));

        return val;
    }

    private CardPanel buildPaymentCard() {
        CardPanel card = new CardPanel();
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel header = new JLabel("💳 Metode Pembayaran");
        header.setFont(Theme.FONT_SUBTITLE);
        header.setForeground(Theme.TEXT_PRIMARY);
        header.setAlignmentX(LEFT_ALIGNMENT);
        card.add(header);
        card.add(Box.createVerticalStrut(16));

        ButtonGroup group = new ButtonGroup();

        cashRadio = new JRadioButton("  💵  Tunai");
        cashRadio.setFont(Theme.FONT_BODY);
        cashRadio.setForeground(Theme.TEXT_PRIMARY);
        cashRadio.setOpaque(false);
        cashRadio.setSelected(true);
        cashRadio.setAlignmentX(LEFT_ALIGNMENT);
        cashRadio.setCursor(new Cursor(Cursor.HAND_CURSOR));

        transferRadio = new JRadioButton("  🏦  Transfer Bank");
        transferRadio.setFont(Theme.FONT_BODY);
        transferRadio.setForeground(Theme.TEXT_PRIMARY);
        transferRadio.setOpaque(false);
        transferRadio.setAlignmentX(LEFT_ALIGNMENT);
        transferRadio.setCursor(new Cursor(Cursor.HAND_CURSOR));

        group.add(cashRadio);
        group.add(transferRadio);

        JPanel cashPanel = createRadioPanel(cashRadio);
        JPanel transferPanel = createRadioPanel(transferRadio);

        card.add(cashPanel);
        card.add(Box.createVerticalStrut(8));
        card.add(transferPanel);
        card.add(Box.createVerticalStrut(12));

        transferFields = new JPanel();
        transferFields.setLayout(
                new BoxLayout(transferFields, BoxLayout.Y_AXIS)
        );
        transferFields.setOpaque(false);
        transferFields.setAlignmentX(LEFT_ALIGNMENT);
        transferFields.setVisible(false);

        JLabel bankLabel = new JLabel("Nama Bank");
        bankLabel.setFont(Theme.FONT_SMALL);
        bankLabel.setForeground(Theme.TEXT_SECONDARY);
        bankLabel.setAlignmentX(LEFT_ALIGNMENT);
        transferFields.add(bankLabel);
        transferFields.add(Box.createVerticalStrut(4));

        bankNameField = new RoundedTextField(
                "Contoh: BCA, Mandiri, BNI"
        );
        bankNameField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44)
        );
        bankNameField.setAlignmentX(LEFT_ALIGNMENT);
        transferFields.add(bankNameField);
        transferFields.add(Box.createVerticalStrut(10));

        JLabel accLabel = new JLabel("Nomor Rekening");
        accLabel.setFont(Theme.FONT_SMALL);
        accLabel.setForeground(Theme.TEXT_SECONDARY);
        accLabel.setAlignmentX(LEFT_ALIGNMENT);
        transferFields.add(accLabel);
        transferFields.add(Box.createVerticalStrut(4));

        accountField = new RoundedTextField(
                "Masukkan nomor rekening"
        );
        accountField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44)
        );
        accountField.setAlignmentX(LEFT_ALIGNMENT);
        transferFields.add(accountField);

        card.add(transferFields);

        cashRadio.addActionListener(e -> {
            transferFields.setVisible(false);
            confirmBtn.setText("Buat Reservasi");
            revalidate();
            repaint();
        });

        transferRadio.addActionListener(e -> {
            transferFields.setVisible(true);
            confirmBtn.setText("Bayar Sekarang");
            revalidate();
            repaint();
        });

        return card;
    }

    private JPanel createRadioPanel(JRadioButton radio) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_MAIN);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        Theme.BORDER, 1, true
                ),
                new EmptyBorder(12, 12, 12, 12)
        ));

        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(radio, BorderLayout.CENTER);

        radio.addActionListener(e -> {
            Container parent = panel.getParent();

            if (parent != null) {
                for (Component c : parent.getComponents()) {
                    if (c instanceof JPanel) {
                        ((JPanel) c).setBorder(
                                BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(
                                                Theme.BORDER, 1, true
                                        ),
                                        new EmptyBorder(12, 12, 12, 12)
                                )
                        );

                        ((JPanel) c).setBackground(Theme.BG_MAIN);
                    }
                }
            }

            panel.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    Theme.PRIMARY, 2, true
                            ),
                            new EmptyBorder(11, 11, 11, 11)
                    )
            );

            panel.setBackground(Theme.PRIMARY_LIGHT);
        });

        return panel;
    }

    public void setReservationDetails(
            String fieldName,
            String type,
            String date,
            String time,
            int duration,
            double pricePerHour,
            double discount,
            double total
    ) {
        fieldNameLabel.setText(fieldName);
        fieldTypeLabel.setText(type);
        dateLabel.setText(date);
        timeLabel.setText(time);
        durationLabel.setText(duration + " jam");

        subtotalLabel.setText(
                String.format("Rp %,.0f", pricePerHour * duration)
        );

        discountLabel.setText(
                String.format("- Rp %,.0f", discount)
        );

        totalLabel.setText(
                String.format("Rp %,.0f", total)
        );
    }

    public String getSelectedPaymentMethod() {
        return cashRadio.isSelected() ? "TUNAI" : "TRANSFER";
    }

    public String getBankName() {
        return bankNameField.getText().trim();
    }

    public String getAccountNumber() {
        return accountField.getText().trim();
    }

    public void setOnConfirm(Runnable onConfirm) {
        this.onConfirm = onConfirm;
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
}
