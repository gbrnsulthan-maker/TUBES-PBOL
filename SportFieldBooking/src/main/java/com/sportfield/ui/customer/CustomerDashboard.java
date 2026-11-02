package com.sportfield.ui.customer;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.IntConsumer;

public class CustomerDashboard extends JPanel {

    private final JLabel lblAktif   = new JLabel("0");
    private final JLabel lblTotal   = new JLabel("0");
    private final JLabel lblBayar   = new JLabel("0");

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel emptyLabel;

    private final RoundedButton btnLihat;
    private final RoundedButton btnCari;
    private final RoundedButton btnBuat;

    private IntConsumer onAction;

    public CustomerDashboard(String userName) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setOpaque(true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JPanel heroSection = createHeroSection(userName);
        heroSection.setBorder(BorderFactory.createEmptyBorder(0, Theme.PADDING_LG, 0, Theme.PADDING_LG));
        content.add(heroSection);
        content.add(Box.createVerticalStrut(Theme.PADDING_LG));

        JPanel statsRow = new JPanel(new GridLayout(1, 3, Theme.PADDING, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 124));
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setBorder(BorderFactory.createEmptyBorder(0, Theme.PADDING_LG, 0, Theme.PADDING_LG));

        statsRow.add(createStatCard("📅", "Reservasi Aktif", lblAktif, Theme.INFO));
        statsRow.add(createStatCard("📊", "Total Reservasi", lblTotal, Theme.SUCCESS));
        statsRow.add(createStatCard("⏳", "Menunggu Bayar", lblBayar, Theme.WARNING));

        content.add(statsRow);
        content.add(Box.createVerticalStrut(Theme.PADDING_LG));

        JPanel bottomRow = new JPanel(new GridBagLayout());
        bottomRow.setOpaque(false);
        bottomRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomRow.setBorder(BorderFactory.createEmptyBorder(0, Theme.PADDING_LG, Theme.PADDING_LG, Theme.PADDING_LG));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        CardPanel tableCard = new CardPanel(Theme.CARD_RADIUS, true);
        tableCard.setGlassStyle(true);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));
        tableCard.setBackground(Theme.BG_CARD);

        JLabel tableTitle = new JLabel("Reservasi Terbaru");
        tableTitle.setFont(Theme.FONT_SUBTITLE);
        tableTitle.setForeground(Theme.TEXT_PRIMARY);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.PADDING_SM, 0));
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"Lapangan", "Tanggal", "Jam", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_GLASS, 1));
        scrollPane.getViewport().setBackground(Theme.BG_WHITE);

        JPanel tableWrapper = new JPanel(new CardLayout());
        tableWrapper.setOpaque(false);

        emptyLabel = new JLabel("Belum ada reservasi", SwingConstants.CENTER);
        emptyLabel.setFont(Theme.FONT_BODY);
        emptyLabel.setForeground(Theme.TEXT_SECONDARY);

        JPanel emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setOpaque(false);
        emptyPanel.add(emptyLabel, BorderLayout.CENTER);
        emptyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_GLASS, 1),
                BorderFactory.createEmptyBorder(Theme.PADDING_LG, 0, Theme.PADDING_LG, 0)
        ));

        tableWrapper.add(emptyPanel, "empty");
        tableWrapper.add(scrollPane, "table");
        ((CardLayout) tableWrapper.getLayout()).show(tableWrapper, "empty");

        tableCard.add(tableWrapper, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, Theme.PADDING);
        bottomRow.add(tableCard, gbc);

        CardPanel actionCard = new CardPanel(Theme.CARD_RADIUS, true);
        actionCard.setGlassStyle(true);
        actionCard.setLayout(new BorderLayout());
        actionCard.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));
        actionCard.setBackground(Theme.BG_CARD);

        JLabel actionTitle = new JLabel("Aksi Cepat");
        actionTitle.setFont(Theme.FONT_SUBTITLE);
        actionTitle.setForeground(Theme.TEXT_PRIMARY);
        actionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.PADDING_SM, 0));
        actionCard.add(actionTitle, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        btnLihat = new RoundedButton("🏟 Lihat Lapangan", RoundedButton.Style.GRADIENT);
        btnCari = new RoundedButton("🔍 Cari Jadwal", RoundedButton.Style.SECONDARY);
        btnBuat = new RoundedButton("📝 Buat Reservasi", RoundedButton.Style.SECONDARY);

        Dimension btnSize = new Dimension(Integer.MAX_VALUE, 48);
        for (RoundedButton btn : new RoundedButton[]{btnLihat, btnCari, btnBuat}) {
            btn.setMaximumSize(btnSize);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        btnLihat.addActionListener(e -> fireAction(0));
        btnCari.addActionListener(e -> fireAction(1));
        btnBuat.addActionListener(e -> fireAction(2));

        buttonPanel.add(btnLihat);
        buttonPanel.add(Box.createVerticalStrut(Theme.PADDING_SM));
        buttonPanel.add(btnCari);
        buttonPanel.add(Box.createVerticalStrut(Theme.PADDING_SM));
        buttonPanel.add(btnBuat);
        buttonPanel.add(Box.createVerticalGlue());

        actionCard.add(buttonPanel, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 0);
        bottomRow.add(actionCard, gbc);

        content.add(bottomRow);

        JScrollPane mainScroll = new JScrollPane(content);
        mainScroll.setBorder(null);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.getViewport().setBackground(Theme.BG_MAIN);

        add(mainScroll, BorderLayout.CENTER);
    }

    private JPanel createHeroSection(String userName) {
        HeroPanel hero = new HeroPanel();
        hero.setLayout(new BorderLayout(24, 0));
        hero.setBorder(BorderFactory.createEmptyBorder(30, 34, 30, 28));
        hero.setPreferredSize(new Dimension(0, 270));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel message = new JPanel();
        message.setOpaque(false);
        message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));

        JLabel date = new JLabel(LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id", "ID"))
        ).toUpperCase(new Locale("id", "ID")));
        date.setFont(new Font("Segoe UI", Font.BOLD, 11));
        date.setForeground(new Color(203, 242, 216));
        date.setAlignmentX(Component.LEFT_ALIGNMENT);

        String displayName = userName == null || userName.trim().isEmpty() ? "Pelanggan" : userName.trim();
        JLabel greeting = new JLabel("<html>Halo, " + escapeHtml(displayName) + "!<br>Siap untuk bermain?</html>");
        greeting.setFont(new Font("Segoe UI", Font.BOLD, 30));
        greeting.setForeground(Color.WHITE);
        greeting.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("<html>Pilih lapangan favoritmu dan pesan jadwal<br>yang paling cocok untuk timmu.</html>");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(225, 242, 229));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton bookButton = new RoundedButton("Cari Lapangan  →", RoundedButton.Style.OUTLINE);
        bookButton.setBackground(Color.WHITE);
        bookButton.setForeground(new Color(24, 103, 70));
        bookButton.setPreferredSize(new Dimension(170, 42));
        bookButton.setMaximumSize(new Dimension(170, 42));
        bookButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookButton.addActionListener(e -> fireAction(1));

        message.add(date);
        message.add(Box.createVerticalStrut(18));
        message.add(greeting);
        message.add(Box.createVerticalStrut(10));
        message.add(subtitle);
        message.add(Box.createVerticalStrut(20));
        message.add(bookButton);
        hero.add(message, BorderLayout.WEST);
        hero.add(new HeroArtwork(), BorderLayout.CENTER);
        return hero;
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static class HeroPanel extends JPanel {
        private HeroPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(17, 72, 58),
                getWidth(), getHeight(), new Color(34, 139, 86)));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 26, 26));
            g2.setColor(new Color(255, 255, 255, 22));
            g2.fill(new Ellipse2D.Double(getWidth() * 0.62, -getHeight() * 0.72,
                getHeight() * 2.0, getHeight() * 2.0));
            g2.dispose();
        }
    }

    private static class HeroArtwork extends JPanel {
        private HeroArtwork() {
            setOpaque(false);
            setPreferredSize(new Dimension(330, 200));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int fieldW = Math.min(w - 18, 330);
            int fieldH = Math.min(h - 12, 190);
            int x = (w - fieldW) / 2;
            int y = (h - fieldH) / 2;

            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Float(x + 4, y + 6, fieldW, fieldH, 22, 22));
            g2.setColor(new Color(255, 255, 255, 24));
            g2.fill(new RoundRectangle2D.Float(x, y, fieldW, fieldH, 22, 22));

            Shape oldClip = g2.getClip();
            g2.clip(new RoundRectangle2D.Float(x, y, fieldW, fieldH, 22, 22));
            g2.setPaint(new GradientPaint(x, y, new Color(76, 174, 102),
                x + fieldW, y + fieldH, new Color(28, 111, 66)));
            g2.fillRect(x, y, fieldW, fieldH);

            g2.setColor(new Color(255, 255, 255, 20));
            for (int i = 0; i < 6; i++) {
                int stripeX = x + i * fieldW / 6;
                if (i % 2 == 0) g2.fillRect(stripeX, y, fieldW / 6 + 1, fieldH);
            }

            g2.setColor(new Color(255, 255, 255, 180));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x + 16, y + 14, fieldW - 32, fieldH - 28, 8, 8);
            g2.drawLine(x + fieldW / 2, y + 14, x + fieldW / 2, y + fieldH - 14);
            int circle = Math.min(fieldW, fieldH) / 3;
            g2.drawOval(x + (fieldW - circle) / 2, y + (fieldH - circle) / 2, circle, circle);
            g2.drawRect(x + 16, y + fieldH / 3, 32, fieldH / 3);
            g2.drawRect(x + fieldW - 48, y + fieldH / 3, 32, fieldH / 3);

            int ballX = x + fieldW / 2 - 7;
            int ballY = y + fieldH / 2 - 7;
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Float(ballX, ballY, 14, 14));
            g2.setColor(new Color(35, 83, 55));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new Ellipse2D.Float(ballX, ballY, 14, 14));
            g2.setClip(oldClip);
            g2.dispose();
        }
    }

    public void setStats(int aktif, int total, int menunggu) {
        lblAktif.setText(String.valueOf(aktif));
        lblTotal.setText(String.valueOf(total));
        lblBayar.setText(String.valueOf(menunggu));
    }

    public void setRecentReservations(String[][] data) {
        tableModel.setRowCount(0);
        if (data == null || data.length == 0) {
            Container parent = table.getParent().getParent().getParent();
            if (parent.getLayout() instanceof CardLayout) {
                ((CardLayout) parent.getLayout()).show(parent, "empty");
            }
        } else {
            for (String[] row : data) {
                tableModel.addRow(row);
            }
            Container parent = table.getParent().getParent().getParent();
            if (parent.getLayout() instanceof CardLayout) {
                ((CardLayout) parent.getLayout()).show(parent, "table");
            }
        }
    }

    public void setOnAction(IntConsumer onAction) {
        this.onAction = onAction;
    }

    private CardPanel createStatCard(String icon, String title, JLabel valueLabel, Color accent) {
        CardPanel card = new CardPanel(Theme.CARD_RADIUS, true);
        card.setGlassStyle(true);
        card.setLayout(new BorderLayout());
        card.setBackground(Theme.BG_CARD);

        JPanel strip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        strip.setOpaque(false);
        strip.setPreferredSize(new Dimension(6, 0));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_SMALL);
        titleLabel.setForeground(Theme.TEXT_SECONDARY);

        topRow.add(iconLabel);
        topRow.add(titleLabel);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(Theme.PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(8, 2, 0, 0));

        inner.add(topRow);
        inner.add(valueLabel);

        card.add(strip, BorderLayout.WEST);
        card.add(inner, BorderLayout.CENTER);

        return card;
    }

    private void styleTable(JTable tbl) {
        tbl.setFont(Theme.FONT_BODY);
        tbl.setRowHeight(40);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionBackground(Theme.PRIMARY_LIGHT);
        tbl.setSelectionForeground(Theme.TEXT_PRIMARY);
        tbl.setFillsViewportHeight(true);
        tbl.setBackground(Theme.BG_WHITE);
        tbl.setForeground(Theme.TEXT_PRIMARY);

        JTableHeader header = tbl.getTableHeader();
        header.setFont(Theme.FONT_HEADING);
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setBackground(Theme.BG_MAIN);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_GLASS));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, Theme.PADDING_SM, 0, Theme.PADDING_SM));

                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Theme.BG_WHITE : new Color(248, 249, 252));
                }
                return this;
            }
        };
        tbl.setDefaultRenderer(Object.class, cellRenderer);
    }

    private void fireAction(int index) {
        if (onAction != null) {
            onAction.accept(index);
        }
    }
}
