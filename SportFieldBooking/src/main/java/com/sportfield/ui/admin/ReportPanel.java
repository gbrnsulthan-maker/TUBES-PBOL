package com.sportfield.ui.admin;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Panel laporan reservasi dan transaksi untuk admin.
 */
public class ReportPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JLabel totalReservasi;
    private JLabel totalPendapatan;
    private JLabel avgPerHari;

    public ReportPanel() {
        setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // ── Title ──
        JLabel title = new JLabel("Laporan");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        content.add(title);

        JLabel subtitle = new JLabel("Ringkasan reservasi dan pendapatan");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(20));

        // ── Stats cards ──
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        totalReservasi = createStatCard(statsRow, "📋", "Total Reservasi", "0", Theme.PRIMARY);
        totalPendapatan = createStatCard(statsRow, "💰", "Total Pendapatan", "Rp 0", Theme.SUCCESS);
        avgPerHari = createStatCard(statsRow, "📊", "Rata-rata/Hari", "0", new Color(139, 92, 246));

        content.add(statsRow);
        content.add(Box.createVerticalStrut(20));

        // ── Filter row ──
        CardPanel filterCard = new CardPanel();
        filterCard.setBorder(new EmptyBorder(14, 20, 14, 20));
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterCard.setAlignmentX(LEFT_ALIGNMENT);
        filterCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(Theme.FONT_BODY);
        filterLabel.setForeground(Theme.TEXT_SECONDARY);

        JComboBox<String> statusFilter = new JComboBox<>(new String[]{
            "Semua Status", "Menunggu Pembayaran", "Dikonfirmasi", "Selesai", "Dibatalkan", "Kedaluwarsa"});
        statusFilter.setFont(Theme.FONT_BODY);

        JComboBox<String> periodeFilter = new JComboBox<>(new String[]{
            "Semua Waktu", "Hari Ini", "7 Hari Terakhir", "30 Hari Terakhir", "Bulan Ini"});
        periodeFilter.setFont(Theme.FONT_BODY);

        RoundedButton exportBtn = new RoundedButton("📥 Ekspor CSV", RoundedButton.Style.OUTLINE);
        exportBtn.setPreferredSize(new Dimension(150, 36));

        filterCard.add(filterLabel);
        filterCard.add(statusFilter);
        filterCard.add(periodeFilter);
        filterCard.add(Box.createHorizontalStrut(20));
        filterCard.add(exportBtn);

        content.add(filterCard);
        content.add(Box.createVerticalStrut(16));

        // ── Table ──
        CardPanel tableCard = new CardPanel();
        tableCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        tableCard.setLayout(new BorderLayout());
        tableCard.setAlignmentX(LEFT_ALIGNMENT);

        String[] columns = {"No", "ID", "Pelanggan", "Lapangan", "Tanggal", "Jam", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Theme.PRIMARY_LIGHT);
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Theme.BG_MAIN);
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        header.setPreferredSize(new Dimension(0, 40));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                setBorder(new EmptyBorder(0, 10, 0, 10));

                if (col == 7 && v != null) {
                    String s = v.toString();
                    switch (s) {
                        case "DIKONFIRMASI": setForeground(Theme.SUCCESS); break;
                        case "MENUNGGU_PEMBAYARAN": setForeground(Theme.WARNING); break;
                        case "DIBATALKAN": setForeground(Theme.DANGER); break;
                        case "SELESAI": setForeground(Theme.INFO); break;
                        default: setForeground(Theme.TEXT_SECONDARY);
                    }
                } else {
                    setForeground(Theme.TEXT_PRIMARY);
                }
                return c;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        content.add(tableCard);

        JScrollPane mainScroll = new JScrollPane(content);
        mainScroll.setBorder(null);
        mainScroll.getViewport().setBackground(Theme.BG_MAIN);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JLabel createStatCard(JPanel parent, String icon, String label, String value, Color accent) {
        CardPanel card = new CardPanel();
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        card.setLayout(new BorderLayout());

        // Color strip at left
        JPanel strip = new JPanel();
        strip.setPreferredSize(new Dimension(4, 0));
        strip.setBackground(accent);
        card.add(strip, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(0, 12, 0, 0));

        JLabel iconLabel = new JLabel(icon + "  " + label);
        iconLabel.setFont(Theme.FONT_SMALL);
        iconLabel.setForeground(Theme.TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Theme.TEXT_PRIMARY);

        info.add(iconLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(valueLabel);

        card.add(info, BorderLayout.CENTER);
        parent.add(card);
        return valueLabel;
    }

    // ── Public API ──

    public void setStats(int reservasi, double pendapatan, double rataRata) {
        totalReservasi.setText(String.valueOf(reservasi));
        totalPendapatan.setText(String.format("Rp %,.0f", pendapatan));
        avgPerHari.setText(String.format("%.1f", rataRata));
    }

    public void setReportData(String[][] data) {
        tableModel.setRowCount(0);
        for (int i = 0; i < data.length; i++) {
            tableModel.addRow(new Object[]{
                i + 1, data[i][0], data[i][1], data[i][2],
                data[i][3], data[i][4], data[i][5], data[i][6]
            });
        }
    }
}
