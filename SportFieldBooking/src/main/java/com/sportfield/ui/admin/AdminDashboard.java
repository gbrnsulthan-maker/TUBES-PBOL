package com.sportfield.ui.admin;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AdminDashboard extends JPanel {

    private final JLabel lblTotalLapangan;
    private final JLabel lblReservasiHariIni;
    private final JLabel lblMenungguKonfirmasi;
    private final JLabel lblPendapatan;

    private final DefaultTableModel reservationTableModel;
    private final JTable reservationTable;

    private final JPanel activityListPanel;

    public AdminDashboard() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING_LG, Theme.PADDING_LG, Theme.PADDING_LG, Theme.PADDING_LG));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Dashboard Admin");
        lblTitle.setFont(Theme.FONT_TITLE);
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Panel administrasi SportField");
        lblSubtitle.setFont(Theme.FONT_BODY);
        lblSubtitle.setForeground(Theme.TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(lblSubtitle);

        String todayStr = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID")));
        JLabel lblDate = new JLabel(todayStr);
        lblDate.setFont(Theme.FONT_BODY);
        lblDate.setForeground(Theme.TEXT_SECONDARY);

        topBar.add(titlePanel, BorderLayout.WEST);
        topBar.add(lblDate, BorderLayout.EAST);

        content.add(topBar);
        content.add(Box.createVerticalStrut(Theme.PADDING));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, Theme.PADDING_SM, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        lblTotalLapangan = new JLabel("0");
        lblReservasiHariIni = new JLabel("0");
        lblMenungguKonfirmasi = new JLabel("0");
        lblPendapatan = new JLabel("Rp 0");

        Color purpleAccent = new Color(139, 92, 246);

        statsRow.add(createStatCard("🏟", "Total Lapangan", lblTotalLapangan, Theme.INFO));
        statsRow.add(createStatCard("📅", "Reservasi Hari Ini", lblReservasiHariIni, Theme.SUCCESS));
        statsRow.add(createStatCard("⏳", "Menunggu Konfirmasi", lblMenungguKonfirmasi, Theme.WARNING));
        statsRow.add(createStatCard("💰", "Pendapatan Bulan Ini", lblPendapatan, purpleAccent));

        content.add(statsRow);
        content.add(Box.createVerticalStrut(Theme.PADDING));

        JPanel bottomRow = new JPanel(new GridBagLayout());
        bottomRow.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, Theme.PADDING_SM / 2);

        gbc.gridx = 0;
        gbc.weightx = 0.65;
        CardPanel reservationCard = new CardPanel(Theme.CARD_RADIUS, true);
        reservationCard.setGlassStyle(true);
        reservationCard.setLayout(new BorderLayout());
        reservationCard.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));
        reservationCard.setBackground(Theme.BG_CARD);

        JLabel lblReservationTitle = new JLabel("Reservasi Terbaru");
        lblReservationTitle.setFont(Theme.FONT_SUBTITLE);
        lblReservationTitle.setForeground(Theme.TEXT_PRIMARY);
        lblReservationTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.PADDING_SM, 0));

        String[] columns = {"ID", "Pelanggan", "Lapangan", "Tanggal", "Status", "Aksi"};
        String[][] placeholderData = {
                {"R001", "Andi Pratama", "Lapangan A", "2024-03-15", "Dikonfirmasi", ""},
                {"R002", "Budi Santoso", "Lapangan B", "2024-03-15", "Menunggu", ""},
                {"R003", "Citra Dewi", "Lapangan C", "2024-03-16", "Dikonfirmasi", ""},
                {"R004", "Dian Permata", "Lapangan A", "2024-03-16", "Dibatalkan", ""},
                {"R005", "Eko Saputra", "Lapangan B", "2024-03-17", "Menunggu", ""},
        };

        reservationTableModel = new DefaultTableModel(placeholderData, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        reservationTable = new JTable(reservationTableModel);
        styleTable(reservationTable);

        reservationTable.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonRenderer());
        reservationTable.getColumnModel().getColumn(5).setCellEditor(new ActionButtonEditor());

        reservationTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        JScrollPane tableScroll = new JScrollPane(reservationTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(Theme.BG_WHITE);

        reservationCard.add(lblReservationTitle, BorderLayout.NORTH);
        reservationCard.add(tableScroll, BorderLayout.CENTER);

        bottomRow.add(reservationCard, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, Theme.PADDING_SM / 2, 0, 0);

        CardPanel activityCard = new CardPanel(Theme.CARD_RADIUS, true);
        activityCard.setGlassStyle(true);
        activityCard.setLayout(new BorderLayout());
        activityCard.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));
        activityCard.setBackground(Theme.BG_CARD);

        JLabel lblActivityTitle = new JLabel("Aktivitas");
        lblActivityTitle.setFont(Theme.FONT_SUBTITLE);
        lblActivityTitle.setForeground(Theme.TEXT_PRIMARY);
        lblActivityTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.PADDING_SM, 0));

        activityListPanel = new JPanel();
        activityListPanel.setLayout(new BoxLayout(activityListPanel, BoxLayout.Y_AXIS));
        activityListPanel.setOpaque(false);

        addActivityItem("🟢", "Reservasi baru #R001", "Andi Pratama – Lapangan A", "2 menit lalu");
        addActivityItem("🔵", "Pembayaran diterima #R002", "Budi Santoso – Rp 150.000", "15 menit lalu");
        addActivityItem("🟢", "Reservasi baru #R003", "Citra Dewi – Lapangan C", "1 jam lalu");
        addActivityItem("🟠", "Menunggu konfirmasi #R005", "Eko Saputra – Lapangan B", "2 jam lalu");
        addActivityItem("🔴", "Reservasi dibatalkan #R004", "Dian Permata – Lapangan A", "3 jam lalu");
        addActivityItem("🔵", "Pembayaran diterima #R001", "Andi Pratama – Rp 200.000", "5 jam lalu");

        JScrollPane activityScroll = new JScrollPane(activityListPanel);
        activityScroll.setBorder(BorderFactory.createEmptyBorder());
        activityScroll.getViewport().setBackground(Theme.BG_WHITE);
        activityScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        activityCard.add(lblActivityTitle, BorderLayout.NORTH);
        activityCard.add(activityScroll, BorderLayout.CENTER);

        bottomRow.add(activityCard, gbc);

        content.add(bottomRow);

        JScrollPane mainScroll = new JScrollPane(content);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.getViewport().setBackground(Theme.BG_MAIN);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(mainScroll, BorderLayout.CENTER);
    }

    public void setStats(int lapangan, int reservasiHariIni, int menunggu, double pendapatan) {
        lblTotalLapangan.setText(String.valueOf(lapangan));
        lblReservasiHariIni.setText(String.valueOf(reservasiHariIni));
        lblMenungguKonfirmasi.setText(String.valueOf(menunggu));

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);
        lblPendapatan.setText(nf.format(pendapatan));
    }

    public void setRecentReservations(String[][] data) {
        reservationTableModel.setRowCount(0);
        for (String[] row : data) {
            String[] fullRow = new String[6];
            System.arraycopy(row, 0, fullRow, 0, Math.min(row.length, 5));
            fullRow[5] = "";
            reservationTableModel.addRow(fullRow);
        }
    }

    private CardPanel createStatCard(String icon, String title, JLabel valueLabel, Color accentColor) {
        CardPanel card = new CardPanel(Theme.CARD_RADIUS, true);
        card.setGlassStyle(true);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));
        card.setBackground(Theme.BG_CARD);

        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(52, 52));
        iconPanel.setLayout(new GridBagLayout());

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconPanel.add(lblIcon);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, Theme.PADDING_SM, 0, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Theme.FONT_SMALL);
        lblTitle.setForeground(Theme.TEXT_SECONDARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(valueLabel);

        JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        inner.setOpaque(false);
        inner.add(iconPanel);
        inner.add(textPanel);

        card.add(inner, BorderLayout.WEST);
        return card;
    }

    private void addActivityItem(String statusDot, String title, String detail, String time) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_GLASS),
                BorderFactory.createEmptyBorder(Theme.PADDING_SM, 0, Theme.PADDING_SM, 0)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel lblItemTitle = new JLabel(statusDot + " " + title);
        lblItemTitle.setFont(Theme.FONT_HEADING);
        lblItemTitle.setForeground(Theme.TEXT_PRIMARY);
        lblItemTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDetail = new JLabel(detail);
        lblDetail.setFont(Theme.FONT_SMALL);
        lblDetail.setForeground(Theme.TEXT_SECONDARY);
        lblDetail.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(lblItemTitle);
        leftPanel.add(Box.createVerticalStrut(2));
        leftPanel.add(lblDetail);

        JLabel lblTime = new JLabel(time);
        lblTime.setFont(Theme.FONT_SMALL);
        lblTime.setForeground(Theme.TEXT_SECONDARY);
        lblTime.setVerticalAlignment(SwingConstants.TOP);

        item.add(leftPanel, BorderLayout.CENTER);
        item.add(lblTime, BorderLayout.EAST);

        activityListPanel.add(item);
    }

    private void styleTable(JTable table) {
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Theme.BG_WHITE);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setSelectionBackground(Theme.PRIMARY_LIGHT);
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.FONT_HEADING);
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setBackground(Theme.BG_WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.BORDER_GLASS));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer paddedRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_GLASS),
                        BorderFactory.createEmptyBorder(0, 8, 0, 8)
                ));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 4 && i != 5) {
                table.getColumnModel().getColumn(i).setCellRenderer(paddedRenderer);
            }
        }

        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(60);
        cm.getColumn(1).setPreferredWidth(140);
        cm.getColumn(2).setPreferredWidth(110);
        cm.getColumn(3).setPreferredWidth(100);
        cm.getColumn(4).setPreferredWidth(110);
        cm.getColumn(5).setPreferredWidth(80);
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            String status = value != null ? value.toString() : "";
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(Theme.FONT_SMALL);
            label.setOpaque(true);

            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_GLASS),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));

            if (!isSelected) {
                switch (status) {
                    case "Dikonfirmasi":
                        label.setForeground(new Color(21, 128, 61));
                        label.setBackground(new Color(220, 252, 231));
                        break;
                    case "Menunggu":
                        label.setForeground(new Color(161, 98, 7));
                        label.setBackground(new Color(254, 249, 195));
                        break;
                    case "Dibatalkan":
                        label.setForeground(new Color(185, 28, 28));
                        label.setBackground(new Color(254, 226, 226));
                        break;
                    default:
                        label.setForeground(Theme.TEXT_PRIMARY);
                        label.setBackground(Theme.BG_WHITE);
                        break;
                }
            }

            return label;
        }
    }

    private static class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private final RoundedButton btnDetail;

        public ActionButtonRenderer() {
            setLayout(new GridBagLayout());
            setOpaque(true);
            setBackground(Theme.BG_WHITE);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_GLASS));
            btnDetail = new RoundedButton("Detail", RoundedButton.Style.OUTLINE);
            btnDetail.setPreferredSize(new Dimension(70, 28));
            btnDetail.setFont(Theme.FONT_SMALL);
            add(btnDetail);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(Theme.PRIMARY_LIGHT);
            } else {
                setBackground(Theme.BG_WHITE);
            }
            return this;
        }
    }

    private class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final RoundedButton btnDetail;

        public ActionButtonEditor() {
            panel = new JPanel(new GridBagLayout());
            panel.setOpaque(true);
            panel.setBackground(Theme.BG_WHITE);
            panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_GLASS));

            btnDetail = new RoundedButton("Detail", RoundedButton.Style.OUTLINE);
            btnDetail.setPreferredSize(new Dimension(70, 28));
            btnDetail.setFont(Theme.FONT_SMALL);
            btnDetail.addActionListener(e -> {
                int row = reservationTable.getEditingRow();
                if (row >= 0) {
                    String id = reservationTableModel.getValueAt(row, 0).toString();
                    JOptionPane.showMessageDialog(AdminDashboard.this,
                            "Detail reservasi: " + id,
                            "Detail", JOptionPane.INFORMATION_MESSAGE);
                }
                fireEditingStopped();
            });
            panel.add(btnDetail);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            panel.setBackground(Theme.PRIMARY_LIGHT);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
