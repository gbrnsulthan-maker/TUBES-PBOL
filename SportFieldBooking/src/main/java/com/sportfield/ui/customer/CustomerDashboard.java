package com.sportfield.ui.customer;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.FieldVisuals;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Halaman Home customer.
 * Isi: navbar, banner utama, ringkasan, rekomendasi lapangan,
 * informasi aplikasi, reservasi terbaru, dan aksi cepat.
 *
 * Kode aksi untuk setOnAction:
 *   0 = Lapangan, 1 = Cari Jadwal, 2 = Booking, 3 = Reservasi Saya
 */
public class CustomerDashboard extends JPanel {

    private static final Locale ID = Locale.forLanguageTag("id-ID");
    private static final int RECOMMENDED_COUNT = 4;

    private final JLabel lblAktif   = new JLabel("0");
    private final JLabel lblTotal   = new JLabel("0");
    private final JLabel lblBayar   = new JLabel("0");

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JLabel emptyLabel;

    private final RoundedButton btnLihat;
    private final RoundedButton btnCari;
    private final RoundedButton btnBuat;

    private final JPanel recommendedGrid = new JPanel(new GridLayout(1, RECOMMENDED_COUNT, Theme.PADDING, 0));

    private JScrollPane mainScroll;
    private IntConsumer onAction;
    private Consumer<String> onBookField;

    public CustomerDashboard(String userName) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setOpaque(true);

        String displayName = userName == null || userName.trim().isEmpty() ? "Pelanggan" : userName.trim();

        // ── Navbar (tetap di atas saat konten di-scroll) ──
        add(createNavbar(displayName), BorderLayout.NORTH);

        JPanel content = new ScrollableColumn();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // ── Banner utama ──
        content.add(createHeroSection(displayName));
        content.add(Box.createVerticalStrut(Theme.PADDING_LG));

        // ── Ringkasan ──
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

        // ── Rekomendasi lapangan ──
        content.add(createSection(
            "Rekomendasi Lapangan",
            "Pilihan lapangan yang sedang ramai dipesan",
            "Lihat semua  →", 0,
            recommendedGrid, 292));
        content.add(Box.createVerticalStrut(Theme.PADDING_LG));

        // ── Informasi aplikasi ──
        content.add(createSection(
            "Kenapa SportField?",
            "Aplikasi reservasi lapangan olahraga yang praktis untuk kamu dan timmu",
            null, -1,
            createInfoGrid(), 192));
        content.add(Box.createVerticalStrut(Theme.PADDING_LG));

        // ── Reservasi terbaru + aksi cepat ──
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
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(210);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_GLASS, 1));
        scrollPane.getViewport().setBackground(Theme.BG_WHITE);
        scrollPane.setPreferredSize(new Dimension(100, 232));

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

        mainScroll = new JScrollPane(content);
        mainScroll.setBorder(null);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.getViewport().setBackground(Theme.BG_MAIN);

        add(mainScroll, BorderLayout.CENTER);

        recommendedGrid.setOpaque(false);
    }

    /** Panel isi yang selalu mengikuti lebar jendela (tanpa scroll horizontal). */
    private static class ScrollableColumn extends JPanel implements Scrollable {
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 64; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    //  NAVBAR

    private JPanel createNavbar(String displayName) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(true);
        outer.setBackground(Theme.BG_MAIN);
        outer.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SM + 4, Theme.PADDING_LG, Theme.PADDING_SM, Theme.PADDING_LG));

        CardPanel bar = new CardPanel(Theme.CARD_RADIUS, true);
        bar.setGlassStyle(false);
        bar.setBackground(Theme.BG_WHITE);
        bar.setLayout(new BorderLayout(16, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 14));
        bar.setPreferredSize(new Dimension(0, 66));

        // Kiri: brand
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brand.setOpaque(false);
        JLabel logoIcon = new JLabel("⚽");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        JLabel logoText = new JLabel("SportField");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoText.setForeground(Theme.PRIMARY);
        brand.add(logoIcon);
        brand.add(logoText);
        bar.add(centerVertically(brand, GridBagConstraints.WEST), BorderLayout.WEST);

        // Tengah: menu
        JPanel links = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        links.setOpaque(false);
        links.add(new NavLink("Beranda", true, null));
        links.add(new NavLink("Lapangan", false, () -> fireAction(0)));
        links.add(new NavLink("Jadwal", false, () -> fireAction(1)));
        links.add(new NavLink("Reservasi Saya", false, () -> fireAction(3)));
        bar.add(centerVertically(links, GridBagConstraints.CENTER), BorderLayout.CENTER);

        // Kanan: tombol booking + user
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        RoundedButton bookNav = new RoundedButton("Booking", RoundedButton.Style.GRADIENT);
        bookNav.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD, 13f));
        bookNav.setPreferredSize(new Dimension(104, 38));
        bookNav.addActionListener(e -> fireAction(2));

        JLabel name = new JLabel(displayName);
        name.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
        name.setForeground(Theme.TEXT_PRIMARY);

        right.add(bookNav);
        right.add(new AvatarBadge(displayName));
        right.add(name);
        bar.add(centerVertically(right, GridBagConstraints.EAST), BorderLayout.EAST);

        // Jendela sempit: sembunyikan tombol & nama agar menu tidak terpotong
        bar.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                boolean wide = bar.getWidth() >= 940;
                if (bookNav.isVisible() != wide) {
                    bookNav.setVisible(wide);
                    name.setVisible(wide);
                    bar.revalidate();
                }
            }
        });

        outer.add(bar, BorderLayout.CENTER);
        return outer;
    }

    /** Membungkus komponen agar berada di tengah secara vertikal. */
    private static JPanel centerVertically(JComponent inner, int anchor) {
        JPanel box = new JPanel(new GridBagLayout());
        box.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = anchor;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.NONE;
        box.add(inner, c);
        return box;
    }

    /** Item menu di navbar (pill saat aktif / hover). */
    private static class NavLink extends JLabel {
        private final boolean active;
        private boolean hover = false;

        NavLink(String text, boolean active, Runnable onClick) {
            super(text);
            this.active = active;
            setFont(Theme.FONT_BODY.deriveFont(active ? Font.BOLD : Font.PLAIN));
            setForeground(active ? Theme.PRIMARY : Theme.TEXT_SECONDARY);
            setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            setOpaque(false);
            if (onClick != null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    if (!NavLink.this.active) setForeground(Theme.TEXT_PRIMARY);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    if (!NavLink.this.active) setForeground(Theme.TEXT_SECONDARY);
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (onClick != null) onClick.run();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (active || hover) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(active ? Theme.PRIMARY_LIGHT : new Color(243, 244, 246));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), Theme.BUTTON_RADIUS, Theme.BUTTON_RADIUS));
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /** Lingkaran berisi inisial nama user. */
    private static class AvatarBadge extends JComponent {
        private final String initial;

        AvatarBadge(String name) {
            this.initial = name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase();
            setPreferredSize(new Dimension(38, 38));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, Theme.GRADIENT_BLUE, getWidth(), getHeight(), Theme.GRADIENT_CYAN));
            g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(initial, (getWidth() - fm.stringWidth(initial)) / 2,
                (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    //  BANNER UTAMA

    private JPanel createHeroSection(String displayName) {
        HeroPanel hero = new HeroPanel();
        hero.setLayout(new BorderLayout(24, 0));
        hero.setBorder(BorderFactory.createEmptyBorder(28, 34, 28, 28));

        JPanel message = new JPanel();
        message.setOpaque(false);
        message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));

        JLabel date = new JLabel(LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", ID)
        ).toUpperCase(ID));
        date.setFont(new Font("Segoe UI", Font.BOLD, 11));
        date.setForeground(new Color(203, 242, 216));
        date.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel greeting = new JLabel("<html>Halo, " + escapeHtml(displayName) + "!<br>Siap untuk bermain?</html>");
        greeting.setFont(new Font("Segoe UI", Font.BOLD, 30));
        greeting.setForeground(Color.WHITE);
        greeting.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("<html>Pilih lapangan favoritmu dan pesan jadwal<br>yang paling cocok untuk timmu.</html>");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(225, 242, 229));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton bookButton = new RoundedButton("Booking Sekarang  →", RoundedButton.Style.OUTLINE);
        bookButton.setBackground(Color.WHITE);
        bookButton.setForeground(new Color(24, 103, 70));
        bookButton.setPreferredSize(new Dimension(190, 42));
        bookButton.setMaximumSize(new Dimension(190, 42));
        bookButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookButton.addActionListener(e -> fireAction(2));

        message.add(date);
        message.add(Box.createVerticalStrut(18));
        message.add(greeting);
        message.add(Box.createVerticalStrut(10));
        message.add(subtitle);
        message.add(Box.createVerticalStrut(20));
        message.add(bookButton);
        hero.add(message, BorderLayout.WEST);
        hero.add(new HeroArtwork(), BorderLayout.CENTER);

        // Padding kiri-kanan dipasang di pembungkus (bukan di hero)
        // supaya banner sejajar dengan navbar dan section lain.
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, Theme.PADDING_LG, 0, Theme.PADDING_LG));
        wrapper.setPreferredSize(new Dimension(0, 290));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 290));
        wrapper.add(hero, BorderLayout.CENTER);
        return wrapper;
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

    //  SECTION: header + isi

    /**
     * Membungkus isi dengan judul section.
     * linkText boleh null; jika ada, klik akan memanggil fireAction(linkAction).
     */
    private JPanel createSection(String title, String subtitle, String linkText, int linkAction,
                                 JComponent body, int bodyHeight) {
        JPanel section = new JPanel(new BorderLayout(0, Theme.PADDING_SM));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setBorder(BorderFactory.createEmptyBorder(0, Theme.PADDING_LG, 0, Theme.PADDING_LG));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_SUBTITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(Theme.FONT_SMALL);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titles.add(titleLabel);
        titles.add(Box.createVerticalStrut(2));
        titles.add(subtitleLabel);
        header.add(titles, BorderLayout.WEST);

        if (linkText != null) {
            JLabel link = new JLabel(linkText);
            link.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
            link.setForeground(Theme.PRIMARY);
            link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            link.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    fireAction(linkAction);
                }
            });
            header.add(link, BorderLayout.EAST);
        }

        body.setPreferredSize(new Dimension(0, bodyHeight));
        section.add(header, BorderLayout.NORTH);
        section.add(body, BorderLayout.CENTER);

        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48 + Theme.PADDING_SM + bodyHeight));
        return section;
    }

    //  REKOMENDASI LAPANGAN

    /**
     * Mengisi rekomendasi lapangan (maksimal 4 pertama).
     * Format tiap baris sama dengan FieldListPanel.setFields:
     * [id, nama, jenis, tarif, jamBuka, jamTutup, deskripsi, extra]
     */
    public void setRecommendedFields(String[][] fields) {
        recommendedGrid.removeAll();
        int count = fields == null ? 0 : Math.min(fields.length, RECOMMENDED_COUNT);
        for (int i = 0; i < RECOMMENDED_COUNT; i++) {
            if (i < count) {
                recommendedGrid.add(createFieldCard(fields[i]));
            } else {
                JPanel spacer = new JPanel();
                spacer.setOpaque(false);
                recommendedGrid.add(spacer);
            }
        }
        recommendedGrid.revalidate();
        recommendedGrid.repaint();
    }

    private JPanel createFieldCard(String[] f) {
        String id     = f[0];
        String name   = f[1];
        String type   = f.length > 2 ? f[2] : "";
        String tarif  = f.length > 3 ? f[3] : "0";
        String buka   = f.length > 4 ? f[4] : "";
        String tutup  = f.length > 5 ? f[5] : "";

        Color sportColor = FieldVisuals.colorFor(type);

        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout());

        JPanel banner = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(sportColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 16, Theme.CARD_RADIUS, Theme.CARD_RADIUS));
                g2.fillRect(0, getHeight() - 16, getWidth(), 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(0, 84));
        JLabel emoji = new JLabel(FieldVisuals.emojiFor(type));
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        banner.add(emoji);
        card.add(banner, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_SM, 18, 6, 18));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(Theme.FONT_HEADING);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(nameLabel);
        body.add(Box.createVerticalStrut(6));

        JLabel badge = new JLabel(FieldVisuals.capitalize(type)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(sportColor.getRed(), sportColor.getGreen(), sportColor.getBlue(), 30));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(Theme.FONT_SMALL);
        badge.setForeground(sportColor);
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        badgeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        badgeRow.add(badge);
        body.add(badgeRow);
        body.add(Box.createVerticalStrut(10));

        JLabel price = new JLabel("Rp " + formatRupiah(tarif) + "/jam");
        price.setFont(Theme.FONT_HEADING);
        price.setForeground(Theme.PRIMARY);
        price.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(price);
        body.add(Box.createVerticalStrut(4));

        JLabel hours = new JLabel("🕐 " + buka + " - " + tutup);
        hours.setFont(Theme.FONT_SMALL);
        hours.setForeground(Theme.TEXT_SECONDARY);
        hours.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(hours);

        card.add(body, BorderLayout.CENTER);

        RoundedButton bookBtn = new RoundedButton("Booking", RoundedButton.Style.GRADIENT);
        bookBtn.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD, 13f));
        bookBtn.setPreferredSize(new Dimension(0, 36));
        bookBtn.addActionListener(e -> {
            if (onBookField != null) {
                onBookField.accept(id);
            } else {
                fireAction(2);
            }
        });
        JPanel btnRow = new JPanel(new BorderLayout());
        btnRow.setOpaque(false);
        btnRow.setBorder(BorderFactory.createEmptyBorder(0, 18, 16, 18));
        btnRow.add(bookBtn, BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);

        return card;
    }

    /** "75000" atau "75.000" → "75.000". */
    private static String formatRupiah(String raw) {
        String digits = raw == null ? "" : raw.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return "0";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && (digits.length() - i) % 3 == 0) sb.append('.');
            sb.append(digits.charAt(i));
        }
        return sb.toString();
    }

    //  INFORMASI APLIKASI

    private JPanel createInfoGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 4, Theme.PADDING, 0));
        grid.setOpaque(false);
        grid.add(createInfoCard("📱", "Booking Mudah",
            "Pilih lapangan, tanggal, dan jam hanya dalam beberapa klik.", Theme.INFO));
        grid.add(createInfoCard("🗓", "Jadwal Jelas",
            "Slot yang sudah terisi langsung ditandai, tanpa bentrok.", Theme.SUCCESS));
        grid.add(createInfoCard("💰", "Harga Transparan",
            "Total biaya dihitung otomatis sesuai durasi bermain.", Theme.WARNING));
        grid.add(createInfoCard("💳", "Bayar Fleksibel",
            "Pilih metode pembayaran yang paling nyaman untukmu.", new Color(139, 92, 246)));
        return grid;
    }

    private JPanel createInfoCard(String icon, String title, String description, Color accent) {
        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(Theme.PADDING - 4, Theme.PADDING - 4, Theme.PADDING - 4, Theme.PADDING - 4));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 32));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLabel.setOpaque(false);
        iconLabel.setPreferredSize(new Dimension(46, 46));
        iconLabel.setMaximumSize(new Dimension(46, 46));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_HEADING);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // JTextArea: teks otomatis turun baris mengikuti lebar kartu
        JTextArea descLabel = new JTextArea(description);
        descLabel.setLineWrap(true);
        descLabel.setWrapStyleWord(true);
        descLabel.setEditable(false);
        descLabel.setFocusable(false);
        descLabel.setOpaque(false);
        descLabel.setBorder(null);
        descLabel.setFont(Theme.FONT_SMALL);
        descLabel.setForeground(Theme.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(descLabel);
        return card;
    }

    //  DATA & CALLBACK

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
                String[] shown = row.clone();
                if (shown.length > 3) shown[3] = prettyStatus(shown[3]);
                tableModel.addRow(shown);
            }
            Container parent = table.getParent().getParent().getParent();
            if (parent.getLayout() instanceof CardLayout) {
                ((CardLayout) parent.getLayout()).show(parent, "table");
            }
        }
    }

    /** Kembalikan tampilan ke paling atas (supaya banner utama terlihat). */
    public void scrollToTop() {
        SwingUtilities.invokeLater(() -> mainScroll.getVerticalScrollBar().setValue(0));
    }

    /** "MENUNGGU_PEMBAYARAN" → "Menunggu Pembayaran". */
    private static String prettyStatus(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String w : raw.toLowerCase(ID).split("_")) {
            if (w.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
        }
        return sb.toString();
    }

    /** Aksi: 0 = Lapangan, 1 = Cari Jadwal, 2 = Booking, 3 = Reservasi Saya. */
    public void setOnAction(IntConsumer onAction) {
        this.onAction = onAction;
    }

    /** Dipanggil saat tombol Booking pada kartu rekomendasi ditekan (menerima ID lapangan). */
    public void setOnBookField(Consumer<String> onBookField) {
        this.onBookField = onBookField;
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