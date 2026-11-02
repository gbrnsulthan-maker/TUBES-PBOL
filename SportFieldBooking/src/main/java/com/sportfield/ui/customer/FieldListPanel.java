package com.sportfield.ui.customer;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Panel katalog lapangan olahraga — menampilkan daftar lapangan sebagai kartu modern.
 * Mendukung filter berdasarkan jenis olahraga dan callback saat lapangan dipilih.
 */
public class FieldListPanel extends JPanel {

    // ── Color map per sport type ──
    private static final Color COLOR_BADMINTON  = new Color(59, 130, 246);   // Blue
    private static final Color COLOR_FUTSAL     = new Color(34, 197, 94);    // Green
    private static final Color COLOR_BASKETBALL = new Color(249, 115, 22);   // Orange
    private static final Color COLOR_TENNIS     = new Color(139, 92, 246);   // Purple
    private static final Color COLOR_DEFAULT    = Theme.PRIMARY;

    // ── Emoji per sport type ──
    private static final String EMOJI_BADMINTON  = "🏸";
    private static final String EMOJI_FUTSAL     = "⚽";
    private static final String EMOJI_BASKETBALL = "🏀";
    private static final String EMOJI_TENNIS     = "🎾";
    private static final String EMOJI_DEFAULT    = "🏟️";

    private static final int CARD_WIDTH  = 300;
    private static final int CARD_HEIGHT = 370;
    private static final int COLUMNS     = 3;

    private final JPanel cardsContainer;
    private final JPanel emptyStatePanel;
    private final JComboBox<String> filterCombo;

    private Consumer<String> onFieldSelected;
    private Consumer<String> onFilterChanged;
    private String[][] currentFields;

    /**
     * Placeholder data default — 4 lapangan contoh.
     */
    private static final String[][] SAMPLE_FIELDS = {
        {"1", "Lapangan Badminton A", "Badminton", "75.000", "08:00", "22:00", "Lapangan indoor berstandar nasional", "Indoor"},
        {"2", "Lapangan Futsal Utama", "Futsal",   "100.000", "07:00", "23:00", "Rumput sintetis kualitas premium", "Indoor"},
        {"3", "Lapangan Basket Center", "Basketball", "85.000", "06:00", "21:00", "Ring standar NBA, lantai kayu", "Outdoor"},
        {"4", "Lapangan Tenis Hijau",  "Tennis",   "90.000", "08:00", "20:00", "Lapangan hard court profesional", "Outdoor"},
    };

    public FieldListPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(Theme.PADDING_LG, Theme.PADDING_LG, Theme.PADDING_LG, Theme.PADDING_LG));

        // ═══ Top Section ═══
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.PADDING, 0));

        // Title
        JLabel titleLabel = new JLabel("Lapangan Tersedia");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Pilih lapangan untuk melihat jadwal dan memesan");
        subtitleLabel.setFont(Theme.FONT_BODY);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, Theme.PADDING_SM, 0));

        // Filter row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filterRow.setOpaque(false);
        filterRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel filterLabel = new JLabel("Filter: ");
        filterLabel.setFont(Theme.FONT_BODY);
        filterLabel.setForeground(Theme.TEXT_SECONDARY);

        filterCombo = new JComboBox<>(new String[]{"Semua Jenis", "Badminton", "Futsal", "Basketball", "Tennis"});
        filterCombo.setFont(Theme.FONT_BODY);
        filterCombo.setBackground(Theme.BG_WHITE);
        filterCombo.setForeground(Theme.TEXT_PRIMARY);
        filterCombo.setPreferredSize(new Dimension(180, 36));
        filterCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        filterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                label.setFont(Theme.FONT_BODY);
                if (isSelected) {
                    label.setBackground(Theme.PRIMARY_LIGHT);
                    label.setForeground(Theme.PRIMARY);
                } else {
                    label.setBackground(Theme.BG_WHITE);
                    label.setForeground(Theme.TEXT_PRIMARY);
                }
                return label;
            }
        });
        filterCombo.addActionListener(e -> {
            String selected = (String) filterCombo.getSelectedItem();
            if (onFilterChanged != null && selected != null) {
                onFilterChanged.accept(selected.equals("Semua Jenis") ? "" : selected);
            }
        });

        filterRow.add(filterLabel);
        filterRow.add(filterCombo);

        topSection.add(titleLabel);
        topSection.add(subtitleLabel);
        topSection.add(filterRow);

        add(topSection, BorderLayout.NORTH);

        // ═══ Cards Container (scrollable) ═══
        cardsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.PADDING_SM, Theme.PADDING_SM));
        cardsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // ═══ Empty State ═══
        emptyStatePanel = new JPanel(new GridBagLayout());
        emptyStatePanel.setOpaque(false);

        JPanel emptyContent = new JPanel();
        emptyContent.setLayout(new BoxLayout(emptyContent, BoxLayout.Y_AXIS));
        emptyContent.setOpaque(false);

        JLabel emptyIcon = new JLabel("🏟️");
        emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        emptyIcon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel emptyText = new JLabel("Belum ada lapangan tersedia.");
        emptyText.setFont(Theme.FONT_SUBTITLE);
        emptyText.setForeground(Theme.TEXT_SECONDARY);
        emptyText.setAlignmentX(CENTER_ALIGNMENT);
        emptyText.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        emptyContent.add(emptyIcon);
        emptyContent.add(emptyText);
        emptyStatePanel.add(emptyContent);

        // Use CardLayout to switch between cards view and empty state
        JPanel centerWrapper = new JPanel(new CardLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(scrollPane, "cards");
        centerWrapper.add(emptyStatePanel, "empty");
        add(centerWrapper, BorderLayout.CENTER);

        // Load sample data by default
        setFields(SAMPLE_FIELDS);
    }

    /**
     * Mengatur daftar lapangan dan membangun ulang kartu.
     *
     * @param fields array 2D: [id, name, type, tarif, jamBuka, jamTutup, description, extra]
     */
    public void setFields(String[][] fields) {
        this.currentFields = fields;
        cardsContainer.removeAll();

        JPanel centerWrapper = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        CardLayout cl = (CardLayout) centerWrapper.getLayout();

        if (fields == null || fields.length == 0) {
            cl.show(centerWrapper, "empty");
        } else {
            cl.show(centerWrapper, "cards");
            for (String[] field : fields) {
                cardsContainer.add(createFieldCard(field));
            }
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    /**
     * Mengatur callback ketika tombol "Lihat Jadwal" ditekan.
     *
     * @param onSelect consumer yang menerima field ID
     */
    public void setOnFieldSelected(Consumer<String> onSelect) {
        this.onFieldSelected = onSelect;
    }

    /**
     * Mengatur callback ketika filter jenis olahraga berubah.
     *
     * @param onFilter consumer yang menerima tipe filter (String kosong = semua)
     */
    public void setFilterListener(Consumer<String> onFilter) {
        this.onFilterChanged = onFilter;
    }

    // ═══════════════════════════════════════════
    //  Card Builder
    // ═══════════════════════════════════════════

    private JPanel createFieldCard(String[] field) {
        String id          = field[0];
        String name        = field[1];
        String type        = field[2];
        String tarif       = field[3];
        String jamBuka     = field[4];
        String jamTutup    = field[5];
        String description = field[6];
        String extra       = field.length > 7 ? field[7] : "";

        Color sportColor = getColorForType(type);
        String emoji     = getEmojiForType(type);

        // ── Outer wrapper to enforce fixed card size ──
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));

        // ── Card ──
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout());

        // ── Banner (top color area with emoji) ──
        JPanel banner = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw rounded top corners, flat bottom
                g2.setColor(sportColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 16, Theme.CARD_RADIUS, Theme.CARD_RADIUS));
                g2.fillRect(0, getHeight() - 16, getWidth(), 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(CARD_WIDTH, 100));

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        banner.add(emojiLabel);

        card.add(banner, BorderLayout.NORTH);

        // ── Content area ──
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
            Theme.PADDING_SM, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        // Name
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(Theme.FONT_HEADING);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(nameLabel);
        content.add(Box.createVerticalStrut(6));

        // Type badge (pill shape)
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(LEFT_ALIGNMENT);
        badgeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel badge = new JLabel(type) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Pill background — lighter version of sport color
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
        badge.setPreferredSize(new Dimension(badge.getPreferredSize().width + 20, 22));
        badgeRow.add(badge);

        content.add(badgeRow);
        content.add(Box.createVerticalStrut(10));

        // Info rows
        content.add(createInfoRow("💰", "Tarif: Rp " + tarif + "/jam"));
        content.add(Box.createVerticalStrut(4));
        content.add(createInfoRow("🕐", "Jam: " + jamBuka + " - " + jamTutup));
        content.add(Box.createVerticalStrut(4));
        if (!extra.isEmpty()) {
            String extraIcon = extra.equalsIgnoreCase("Indoor") ? "🏠" : "🌤️";
            content.add(createInfoRow(extraIcon, extra));
        }
        content.add(Box.createVerticalStrut(4));

        // Description (small text)
        JLabel descLabel = new JLabel("<html><body style='width:" + (CARD_WIDTH - 60) + "px'>"
                + description + "</body></html>");
        descLabel.setFont(Theme.FONT_SMALL);
        descLabel.setForeground(Theme.TEXT_SECONDARY);
        descLabel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(descLabel);

        content.add(Box.createVerticalGlue());

        // ── Bottom button ──
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonRow.setOpaque(false);
        buttonRow.setBorder(BorderFactory.createEmptyBorder(8, Theme.PADDING, Theme.PADDING_SM, Theme.PADDING));

        RoundedButton viewBtn = new RoundedButton("Lihat Jadwal", RoundedButton.Style.OUTLINE);
        viewBtn.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        viewBtn.setPreferredSize(new Dimension(CARD_WIDTH - 2 * Theme.PADDING, 34));
        viewBtn.addActionListener(e -> {
            if (onFieldSelected != null) {
                onFieldSelected.accept(id);
            }
        });
        buttonRow.add(viewBtn);

        card.add(content, BorderLayout.CENTER);
        card.add(buttonRow, BorderLayout.SOUTH);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Creates a small info row with icon and text.
     */
    private JPanel createInfoRow(String icon, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(Theme.FONT_SMALL);
        textLabel.setForeground(Theme.TEXT_SECONDARY);

        row.add(iconLabel);
        row.add(textLabel);
        return row;
    }

    // ═══════════════════════════════════════════
    //  Helpers
    // ═══════════════════════════════════════════

    private Color getColorForType(String type) {
        if (type == null) return COLOR_DEFAULT;
        switch (type.toLowerCase()) {
            case "badminton":  return COLOR_BADMINTON;
            case "futsal":     return COLOR_FUTSAL;
            case "basketball": return COLOR_BASKETBALL;
            case "tennis":     return COLOR_TENNIS;
            default:           return COLOR_DEFAULT;
        }
    }

    private String getEmojiForType(String type) {
        if (type == null) return EMOJI_DEFAULT;
        switch (type.toLowerCase()) {
            case "badminton":  return EMOJI_BADMINTON;
            case "futsal":     return EMOJI_FUTSAL;
            case "basketball": return EMOJI_BASKETBALL;
            case "tennis":     return EMOJI_TENNIS;
            default:           return EMOJI_DEFAULT;
        }
    }
}
