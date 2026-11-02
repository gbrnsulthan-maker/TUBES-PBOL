package com.sportfield.ui.customer;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

/**
 * Panel untuk menampilkan riwayat reservasi pengguna.
 * Setiap reservasi ditampilkan sebagai card dengan badge status, detail, dan tombol aksi.
 */
public class HistoryPanel extends JPanel {

    // ── Data ──
    private String[][] reservations;

    // ── Callbacks ──
    private Consumer<String> onPayAction;
    private Consumer<String> onCancelAction;

    // ── UI Components ──
    private final JComboBox<String> sortCombo;
    private final JComboBox<String> filterCombo;
    private final JPanel listPanel;
    private final JLabel emptyLabel;
    private final CardPanel emptyCard;

    // ── Status Constants ──
    private static final String STATUS_MENUNGGU = "MENUNGGU_PEMBAYARAN";
    private static final String STATUS_DIKONFIRMASI = "DIKONFIRMASI";
    private static final String STATUS_SELESAI = "SELESAI";
    private static final String STATUS_DIBATALKAN = "DIBATALKAN";
    private static final String STATUS_KEDALUWARSA = "KEDALUWARSA";

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(Theme.PADDING_LG, Theme.PADDING_LG, Theme.PADDING_LG, Theme.PADDING_LG));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // ── Header ──
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("Riwayat Reservasi");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Lihat dan kelola semua reservasi Anda");
        subtitleLabel.setFont(Theme.FONT_BODY);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        content.add(headerPanel);
        content.add(Box.createVerticalStrut(Theme.PADDING));

        // ── Filter Card ──
        CardPanel filterCard = new CardPanel();
        filterCard.setLayout(new BorderLayout());
        filterCard.setBorder(new EmptyBorder(Theme.PADDING_SM, Theme.PADDING, Theme.PADDING_SM, Theme.PADDING));
        filterCard.setAlignmentX(LEFT_ALIGNMENT);
        filterCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.PADDING_SM, 0));
        filterRow.setOpaque(false);

        // Sort combo
        JLabel sortLabel = new JLabel("Urutkan:");
        sortLabel.setFont(Theme.FONT_BODY);
        sortLabel.setForeground(Theme.TEXT_SECONDARY);
        filterRow.add(sortLabel);

        sortCombo = createStyledCombo();
        sortCombo.addItem("Terbaru");
        sortCombo.addItem("Terlama");
        sortCombo.addItem("Status");
        sortCombo.setPreferredSize(new Dimension(140, 36));
        filterRow.add(sortCombo);

        filterRow.add(Box.createHorizontalStrut(Theme.PADDING_SM));

        // Filter combo
        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setFont(Theme.FONT_BODY);
        filterLabel.setForeground(Theme.TEXT_SECONDARY);
        filterRow.add(filterLabel);

        filterCombo = createStyledCombo();
        filterCombo.addItem("Semua Status");
        filterCombo.addItem("Menunggu Pembayaran");
        filterCombo.addItem("Dikonfirmasi");
        filterCombo.addItem("Selesai");
        filterCombo.addItem("Dibatalkan");
        filterCombo.setPreferredSize(new Dimension(200, 36));
        filterRow.add(filterCombo);

        filterCard.add(filterRow, BorderLayout.CENTER);
        content.add(filterCard);
        content.add(Box.createVerticalStrut(Theme.PADDING));

        // ── Empty State ──
        emptyCard = new CardPanel();
        emptyCard.setLayout(new BorderLayout());
        emptyCard.setAlignmentX(LEFT_ALIGNMENT);
        emptyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel emptyInner = new JPanel();
        emptyInner.setLayout(new BoxLayout(emptyInner, BoxLayout.Y_AXIS));
        emptyInner.setOpaque(false);
        emptyInner.setBorder(new EmptyBorder(40, 0, 40, 0));

        JLabel iconLabel = new JLabel("\uD83D\uDCCB"); // 📋 clipboard icon
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        emptyLabel = new JLabel("Belum ada riwayat reservasi.");
        emptyLabel.setFont(Theme.FONT_BODY);
        emptyLabel.setForeground(Theme.TEXT_SECONDARY);
        emptyLabel.setAlignmentX(CENTER_ALIGNMENT);
        emptyLabel.setBorder(new EmptyBorder(Theme.PADDING_SM, 0, 0, 0));

        emptyInner.add(iconLabel);
        emptyInner.add(emptyLabel);
        emptyCard.add(emptyInner, BorderLayout.CENTER);
        content.add(emptyCard);

        // ── Reservation List ──
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(listPanel);

        // Wrap in scroll
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Theme.BG_MAIN);
        scrollPane.setBackground(Theme.BG_MAIN);

        add(scrollPane, BorderLayout.CENTER);
    }

    // ── Public Interface ──

    /**
     * Menetapkan data reservasi dan membangun ulang daftar kartu.
     * @param data array 2D: [id, fieldName, date, time, duration, totalPrice, status]
     */
    public void setReservations(String[][] data) {
        this.reservations = data;
        rebuildList();
    }

    /**
     * Menetapkan callback saat tombol "Bayar" ditekan.
     * @param onPay consumer yang menerima ID reservasi
     */
    public void setOnPayAction(Consumer<String> onPay) {
        this.onPayAction = onPay;
    }

    /**
     * Menetapkan callback saat tombol "Batalkan" ditekan.
     * @param onCancel consumer yang menerima ID reservasi
     */
    public void setOnCancelAction(Consumer<String> onCancel) {
        this.onCancelAction = onCancel;
    }

    // ── Private Helpers ──

    private void rebuildList() {
        listPanel.removeAll();

        if (reservations == null || reservations.length == 0) {
            emptyCard.setVisible(true);
            listPanel.revalidate();
            listPanel.repaint();
            return;
        }

        emptyCard.setVisible(false);

        for (String[] row : reservations) {
            if (row.length < 7) continue;
            String id = row[0];
            String fieldName = row[1];
            String date = row[2];
            String time = row[3];
            String duration = row[4];
            String totalPrice = row[5];
            String status = row[6];

            CardPanel card = new CardPanel();
            card.setLayout(new BorderLayout(Theme.PADDING, 0));
            card.setBorder(new EmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));
            card.setAlignmentX(LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

            // ── Left: Status Badge ──
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            leftPanel.setOpaque(false);
            leftPanel.setPreferredSize(new Dimension(160, 40));

            JLabel badge = createStatusBadge(status);
            leftPanel.add(badge);

            // ── Center: Details ──
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);

            JLabel nameLabel = new JLabel(fieldName);
            nameLabel.setFont(Theme.FONT_HEADING);
            nameLabel.setForeground(Theme.TEXT_PRIMARY);
            nameLabel.setAlignmentX(LEFT_ALIGNMENT);

            JLabel dateTimeLabel = new JLabel(date + "  •  " + time);
            dateTimeLabel.setFont(Theme.FONT_BODY);
            dateTimeLabel.setForeground(Theme.TEXT_SECONDARY);
            dateTimeLabel.setAlignmentX(LEFT_ALIGNMENT);
            dateTimeLabel.setBorder(new EmptyBorder(4, 0, 2, 0));

            JLabel detailLabel = new JLabel("Durasi: " + duration + " jam  |  Total: Rp " + totalPrice);
            detailLabel.setFont(Theme.FONT_SMALL);
            detailLabel.setForeground(Theme.TEXT_SECONDARY);
            detailLabel.setAlignmentX(LEFT_ALIGNMENT);

            centerPanel.add(nameLabel);
            centerPanel.add(dateTimeLabel);
            centerPanel.add(detailLabel);

            // ── Right: Action Buttons ──
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            rightPanel.setOpaque(false);

            if (STATUS_MENUNGGU.equalsIgnoreCase(status)) {
                RoundedButton payBtn = new RoundedButton("Bayar", RoundedButton.Style.PRIMARY);
                payBtn.setPreferredSize(new Dimension(100, 36));
                payBtn.addActionListener(e -> {
                    if (onPayAction != null) onPayAction.accept(id);
                });
                rightPanel.add(payBtn);

                RoundedButton cancelBtn = new RoundedButton("Batalkan", RoundedButton.Style.DANGER);
                cancelBtn.setPreferredSize(new Dimension(110, 36));
                cancelBtn.addActionListener(e -> {
                    if (onCancelAction != null) onCancelAction.accept(id);
                });
                rightPanel.add(cancelBtn);
            } else if (STATUS_DIKONFIRMASI.equalsIgnoreCase(status)) {
                RoundedButton cancelBtn = new RoundedButton("Batalkan", RoundedButton.Style.DANGER);
                cancelBtn.setPreferredSize(new Dimension(110, 36));
                cancelBtn.addActionListener(e -> {
                    if (onCancelAction != null) onCancelAction.accept(id);
                });
                rightPanel.add(cancelBtn);
            }

            card.add(leftPanel, BorderLayout.WEST);
            card.add(centerPanel, BorderLayout.CENTER);
            card.add(rightPanel, BorderLayout.EAST);

            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(Theme.PADDING_SM));
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    /**
     * Membuat label badge status berwarna (pill shape).
     */
    private JLabel createStatusBadge(String status) {
        String displayText;
        Color bgColor;
        Color textColor = Color.WHITE;

        switch (status.toUpperCase()) {
            case STATUS_DIKONFIRMASI:
                displayText = "Dikonfirmasi";
                bgColor = Theme.SUCCESS;
                break;
            case STATUS_MENUNGGU:
                displayText = "Menunggu";
                bgColor = Theme.WARNING;
                break;
            case STATUS_DIBATALKAN:
                displayText = "Dibatalkan";
                bgColor = Theme.DANGER;
                break;
            case STATUS_SELESAI:
                displayText = "Selesai";
                bgColor = new Color(156, 163, 175); // gray
                break;
            case STATUS_KEDALUWARSA:
                displayText = "Kedaluwarsa";
                bgColor = new Color(156, 163, 175); // gray
                break;
            default:
                displayText = status;
                bgColor = Theme.BORDER;
                textColor = Theme.TEXT_PRIMARY;
                break;
        }

        final Color finalBg = bgColor;
        final Color finalFg = textColor;

        JLabel badge = new JLabel(displayText, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(finalBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(Theme.FONT_SMALL);
        badge.setForeground(finalFg);
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(130, 28));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setBorder(new EmptyBorder(4, 12, 4, 12));

        return badge;
    }

    @SuppressWarnings("unchecked")
    private JComboBox<String> createStyledCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(Theme.FONT_BODY);
        combo.setBackground(Color.WHITE);
        combo.setForeground(Theme.TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return combo;
    }
}
