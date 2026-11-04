package com.sportfield.ui.customer;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Panel katalog lapangan olahraga — menampilkan daftar lapangan sebagai kartu modern.
 * Mendukung filter berdasarkan jenis olahraga dan callback saat lapangan dipilih.
 */
public class FieldListPanel extends JPanel {

    // ── Color map per sport type ──
    private static final Color COLOR_BADMINTON  = new Color(59, 130, 246);
    private static final Color COLOR_FUTSAL     = new Color(34, 197, 94);
    private static final Color COLOR_BASKETBALL = new Color(249, 115, 22);
    private static final Color COLOR_TENNIS     = new Color(139, 92, 246);
    private static final Color COLOR_DEFAULT    = Theme.PRIMARY;
    private static final Color COLOR_GREEN      = new Color(105, 190, 75);
    private static final Color COLOR_GREEN_LIGHT = new Color(239, 248, 235);

    private static final int CARD_WIDTH = 310;
    private static final int CARD_HEIGHT = 430;

    private final JPanel cardsContainer;
    private final JPanel emptyStatePanel;
    private final JTextField searchField;

    private Consumer<String> onFieldSelected;
    private Consumer<String> onFilterChanged;
    private String[][] currentFields;
    private String selectedSport = "";

    /**
     * Placeholder data default — 4 lapangan contoh.
     */
    private static final String[][] SAMPLE_FIELDS = {
        {"1", "Lapangan Badminton A", "Badminton", "75.000", "08:00", "22:00", "Lapangan indoor berstandar nasional", "Indoor", "Tersedia"},
        {"2", "Lapangan Futsal Utama", "Futsal",   "100.000", "07:00", "23:00", "Rumput sintetis kualitas premium", "Indoor", "Tersedia"},
        {"3", "Lapangan Basket Center", "Basketball", "85.000", "06:00", "21:00", "Ring standar NBA, lantai kayu", "Outdoor", "Penuh"},
        {"4", "Lapangan Tenis Hijau",  "Tennis",   "90.000", "08:00", "20:00", "Lapangan hard court profesional", "Outdoor", "Tersedia"},
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

        JLabel titleLabel = new JLabel("Cari Lapangan");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JPanel searchBox = new JPanel(new BorderLayout(10, 0));
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 239, 233), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        searchBox.setMaximumSize(new Dimension(520, 46));
        searchBox.setAlignmentX(LEFT_ALIGNMENT);

        searchField = new JTextField();
        searchField.setFont(Theme.FONT_BODY);
        searchField.setForeground(Theme.TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setToolTipText("Cari nama lapangan atau jenis olahraga");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                refreshFieldCards();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                refreshFieldCards();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                refreshFieldCards();
            }
        });
        searchBox.add(searchField, BorderLayout.CENTER);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.setAlignmentX(LEFT_ALIGNMENT);
        ButtonGroup filterGroup = new ButtonGroup();
        addSportChip(filterRow, filterGroup, "Semua", "", true);
        addSportChip(filterRow, filterGroup, "Futsal", "FUTSAL", false);
        addSportChip(filterRow, filterGroup, "Badminton", "BADMINTON", false);
        addSportChip(filterRow, filterGroup, "Basket", "BASKETBALL", false);
        addSportChip(filterRow, filterGroup, "Tennis", "TENNIS", false);

        topSection.add(titleLabel);
        topSection.add(Box.createVerticalStrut(Theme.PADDING_SM));
        topSection.add(searchBox);
        topSection.add(Box.createVerticalStrut(Theme.PADDING_SM));
        topSection.add(filterRow);
        topSection.add(Box.createVerticalStrut(Theme.PADDING));

        JLabel featuredTitle = new JLabel("Lapangan Pilihan");
        featuredTitle.setFont(Theme.FONT_SUBTITLE);
        featuredTitle.setForeground(Theme.TEXT_PRIMARY);
        featuredTitle.setAlignmentX(LEFT_ALIGNMENT);
        topSection.add(featuredTitle);

        JLabel featuredSubtitle = new JLabel("Klik kartu untuk melihat info lapangan, lalu pilih Lihat Detail");
        featuredSubtitle.setFont(Theme.FONT_SMALL);
        featuredSubtitle.setForeground(Theme.TEXT_SECONDARY);
        featuredSubtitle.setAlignmentX(LEFT_ALIGNMENT);
        topSection.add(featuredSubtitle);

        add(topSection, BorderLayout.NORTH);

        // ═══ Cards Container (scrollable) ═══
        cardsContainer = new ScrollableCardsContainer();
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(
                Theme.PADDING_SM, Theme.PADDING_SM, Theme.PADDING_SM, Theme.PADDING_SM));

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

        JLabel emptyText = new JLabel("Belum ada lapangan tersedia.");
        emptyText.setFont(Theme.FONT_SUBTITLE);
        emptyText.setForeground(Theme.TEXT_SECONDARY);
        emptyText.setAlignmentX(CENTER_ALIGNMENT);
        emptyText.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

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
     * @param fields array 2D: [id, name, type, tarif, jamBuka, jamTutup, description, extra, status]
     */
    public void setFields(String[][] fields) {
        this.currentFields = fields;
        refreshFieldCards();
    }

    private void refreshFieldCards() {
        cardsContainer.removeAll();

        JPanel centerWrapper = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        CardLayout cl = (CardLayout) centerWrapper.getLayout();

        ArrayList<String[]> visibleFields = new ArrayList<>();
        if (currentFields != null) {
            String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
            for (String[] field : currentFields) {
                if (field == null || field.length < 3) continue;
                boolean matchesSport = selectedSport.isEmpty()
                        || field[2].equalsIgnoreCase(selectedSport);
                boolean matchesQuery = query.isEmpty()
                        || field[1].toLowerCase(Locale.ROOT).contains(query)
                        || field[2].toLowerCase(Locale.ROOT).contains(query);
                if (matchesSport && matchesQuery) {
                    visibleFields.add(field);
                }
            }
        }

        if (visibleFields.isEmpty()) {
            emptyStatePanel.removeAll();
            JLabel emptyText = new JLabel(currentFields == null || currentFields.length == 0
                    ? "Belum ada lapangan tersedia."
                    : "Lapangan tidak ditemukan.", SwingConstants.CENTER);
            emptyText.setFont(Theme.FONT_SUBTITLE);
            emptyText.setForeground(Theme.TEXT_SECONDARY);
            emptyStatePanel.add(emptyText);
            emptyStatePanel.revalidate();
            emptyStatePanel.repaint();
            cl.show(centerWrapper, "empty");
        } else {
            cardsContainer.add(new AnimatedFieldSelector(visibleFields.toArray(new String[0][])),
                    BorderLayout.CENTER);
            cl.show(centerWrapper, "cards");
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    /**
     * Mengatur callback ketika tombol "Lihat Detail" ditekan.
     *
     * @param onSelect consumer yang menerima field ID
     */
    public void setOnFieldSelected(Consumer<String> onSelect) {
        this.onFieldSelected = onSelect;
    }

    public String[] getFieldById(String id) {
        if (currentFields == null) return null;
        for (String[] field : currentFields) {
            if (field != null && field.length > 0 && field[0].equals(id)) {
                return field.clone();
            }
        }
        return null;
    }

    /**
     * Mengatur callback ketika filter jenis olahraga berubah.
     *
     * @param onFilter consumer yang menerima tipe filter (String kosong = semua)
     */
    public void setFilterListener(Consumer<String> onFilter) {
        this.onFilterChanged = onFilter;
    }

    private void addSportChip(JPanel row, ButtonGroup group, String label, String sport, boolean selected) {
        JToggleButton chip = new JToggleButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? COLOR_GREEN : COLOR_GREEN_LIGHT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
        chip.setForeground(new Color(38, 63, 34));
        chip.setOpaque(false);
        chip.setContentAreaFilled(false);
        chip.setFocusPainted(false);
        chip.setBorderPainted(false);
        chip.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.setSelected(selected);
        chip.addActionListener(e -> {
            selectedSport = sport;
            refreshFieldCards();
            if (onFilterChanged != null) {
                onFilterChanged.accept(sport);
            }
        });
        group.add(chip);
        row.add(chip);
    }

    private static final class ScrollableCardsContainer extends JPanel implements Scrollable {
        private ScrollableCardsContainer() {
            super(new BorderLayout());
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(24, orientation == SwingConstants.VERTICAL
                    ? visibleRect.height - 24 : visibleRect.width - 24);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private final class AnimatedFieldSelector extends JPanel implements Scrollable {
        private final String[][] fields;
        private final double[] active;
        private final int[] cardStarts;
        private final Rectangle[] detailHitAreas;
        private Timer animationTimer;
        private int activeIndex;

        private AnimatedFieldSelector(String[][] fields) {
            this.fields = fields;
            this.active = new double[fields.length];
            this.cardStarts = new int[fields.length + 1];
            this.detailHitAreas = new Rectangle[fields.length];
            this.active[0] = 1;
            setOpaque(false);
            setPreferredSize(new Dimension(900, 380));
            setMinimumSize(new Dimension(700, 340));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            animationTimer = new Timer(16, e -> {
                boolean animating = false;
                for (int i = 0; i < active.length; i++) {
                    double target = i == activeIndex ? 1 : 0;
                    active[i] += (target - active[i]) * 0.14;
                    if (Math.abs(target - active[i]) < 0.002) {
                        active[i] = target;
                    } else {
                        animating = true;
                    }
                }
                if (!animating) animationTimer.stop();
                repaint();
            });

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    int clickedIndex = -1;
                    for (int i = 0; i < fields.length; i++) {
                        if (e.getX() >= cardStarts[i] && e.getX() < cardStarts[i + 1]) {
                            clickedIndex = i;
                            break;
                        }
                    }
                    if (clickedIndex < 0) return;
                    if (clickedIndex == activeIndex
                            && detailHitAreas[activeIndex] != null
                            && detailHitAreas[activeIndex].contains(e.getPoint())
                            && onFieldSelected != null) {
                        onFieldSelected.accept(fields[activeIndex][0]);
                        return;
                    }
                    activeIndex = clickedIndex;
                    animationTimer.start();
                }
            });
        }

        @Override
        public void addNotify() {
            super.addNotify();
            animationTimer.start();
        }

        @Override
        public void removeNotify() {
            animationTimer.stop();
            super.removeNotify();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int width = getWidth();
            int height = getHeight();
            g.setColor(new Color(240, 240, 240));
            g.fillRect(0, 0, width, height);
            int gap = 8;
            double totalWeight = 0;
            double[] weights = new double[fields.length];
            for (int i = 0; i < fields.length; i++) {
                weights[i] = 1 + 6 * active[i];
                totalWeight += weights[i];
            }

            double cursor = 0;
            for (int i = 0; i < fields.length; i++) {
                detailHitAreas[i] = null;
                int x = (int) Math.round(cursor / totalWeight * width);
                cursor += weights[i];
                int end = (int) Math.round(cursor / totalWeight * width);
                cardStarts[i] = x;
                paintFieldCard(g, fields[i], i, x, end - x, height, gap);
            }
            cardStarts[fields.length] = width;
            g.dispose();
        }

        private void paintFieldCard(Graphics2D graphics, String[] field, int index,
                int x, int width, int height, int gap) {
            if (width <= gap) return;
            int left = x + gap / 2 + 2;
            int cardWidth = width - gap - 4;
            int top = 8;
            int cardHeight = height - 16;
            int radius = 16;
            double contentProgress = active[index];
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            for (int spread = 8; spread > 0; spread--) {
                int alpha = 2 + spread;
                g.setColor(new Color(25, 32, 45, alpha));
                g.fill(new RoundRectangle2D.Float(left - spread / 2f, top + spread / 2f,
                        cardWidth + spread, cardHeight + spread / 2f,
                        radius + spread, radius + spread));
            }

            Shape cardShape = new RoundRectangle2D.Float(left, top, cardWidth, cardHeight, radius, radius);
            g.setColor(Color.WHITE);
            g.fill(cardShape);

            Shape oldClip = g.getClip();
            g.clip(cardShape);
            int padding = Math.min(16, Math.max(8, cardWidth / 8));
            int photoX = left + padding;
            int photoY = top + padding;
            int photoWidth = Math.max(1, cardWidth - padding * 2);
            int photoHeight = Math.max(1, (int) ((cardHeight - padding * 2) * 0.45));
            paintCourtPlaceholder(g, photoX, photoY, photoWidth, photoHeight);

            if (contentProgress > 0.002 && cardWidth > 150) {
                Graphics2D content = (Graphics2D) g.create();
                content.setComposite(AlphaComposite.SrcOver.derive((float) contentProgress));
                boolean available = isAvailable(getFieldStatus(field));
                paintStatusBadge(content, photoX + 10, photoY + 10, available);
                paintTicketContent(content, field, index, left, top, cardWidth, cardHeight,
                        padding, photoY + photoHeight, available);
                content.dispose();
            } else if (cardWidth > 58) {
                paintCompactInfo(g, field, left, cardWidth, top, cardHeight,
                        padding, photoY + photoHeight);
            }

            g.setClip(oldClip);
            g.setColor(new Color(236, 233, 226));
            g.setStroke(new BasicStroke(1f));
            g.draw(cardShape);
            g.dispose();
        }

        private void paintCourtPlaceholder(Graphics2D g, int x, int y, int width, int height) {
            Shape photoShape = new RoundRectangle2D.Float(x, y, width, height, 12, 12);
            Graphics2D photo = (Graphics2D) g.create();
            photo.clip(photoShape);
            photo.setPaint(new GradientPaint(x, y, new Color(221, 237, 202),
                    x + width, y + height, new Color(132, 177, 115)));
            photo.fillRect(x, y, width, height);

            photo.setColor(new Color(255, 255, 255, 105));
            photo.setStroke(new BasicStroke(1.2f));
            int linePadding = Math.max(8, width / 8);
            int courtWidth = Math.max(1, width - linePadding * 2);
            int lineY = y + 10;
            int courtHeight = Math.max(1, height - 20);
            photo.drawRoundRect(x + linePadding, lineY, courtWidth, courtHeight, 8, 8);
            photo.drawLine(x + width / 2, lineY, x + width / 2, lineY + courtHeight);
            int circleSize = Math.min(width / 5, height / 3);
            if (circleSize > 4) {
                photo.drawOval(x + width / 2 - circleSize / 2,
                        y + height / 2 - circleSize / 2, circleSize, circleSize);
            }
            photo.dispose();
        }

        private void paintStatusBadge(Graphics2D g, int x, int y, boolean available) {
            String label = available ? "Tersedia" : "Penuh";
            Font font = new Font("SansSerif", Font.BOLD, 11);
            FontMetrics metrics = g.getFontMetrics(font);
            int badgeWidth = metrics.stringWidth(label) + 20;
            int badgeHeight = 22;
            g.setColor(available ? new Color(217, 176, 58) : new Color(192, 57, 75));
            g.fill(new RoundRectangle2D.Float(x, y, badgeWidth, badgeHeight, 12, 12));
            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawString(label, x + 10, y + (badgeHeight + metrics.getAscent() - metrics.getDescent()) / 2);
        }

        private void paintCompactInfo(Graphics2D g, String[] field,
                int x, int width, int y, int height, int padding, int photoBottom) {
            int centerX = x + width / 2;
            int maxTextWidth = Math.max(1, width - padding * 2);
            int lineY = photoBottom + 22;

            Font sportFont = new Font("SansSerif", Font.BOLD, 10);
            g.setFont(sportFont);
            g.setColor(new Color(55, 65, 81));
            drawCentered(g, ellipsize(g, field[2].toUpperCase(Locale.ROOT), maxTextWidth), centerX, lineY);

            lineY += 19;
            Font priceFont = new Font("Georgia", Font.BOLD, 12);
            g.setFont(priceFont);
            g.setColor(new Color(15, 27, 61));
            String compactPrice = "Rp " + formatCompactPrice(field[3]) + "/jam";
            drawCentered(g, ellipsize(g, compactPrice, maxTextWidth), centerX, lineY);

            lineY += 18;
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g.setColor(isAvailable(getFieldStatus(field))
                    ? new Color(22, 101, 52) : new Color(185, 28, 28));
            drawCentered(g, ellipsize(g, getFieldStatus(field), maxTextWidth), centerX, lineY);
        }

        private String formatCompactPrice(String value) {
            String digits = value == null ? "" : value.replaceAll("[^0-9]", "");
            try {
                long price = digits.isEmpty() ? 0 : Long.parseLong(digits);
                if (price >= 1000 && price % 1000 == 0) {
                    return NumberFormat.getNumberInstance(new Locale("id", "ID")).format(price / 1000) + "rb";
                }
                return NumberFormat.getNumberInstance(new Locale("id", "ID")).format(price);
            } catch (NumberFormatException exception) {
                return value == null ? "-" : value;
            }
        }

        private void drawCentered(Graphics2D g, String text, int centerX, int baseline) {
            g.drawString(text, centerX - g.getFontMetrics().stringWidth(text) / 2, baseline);
        }

        private void paintTicketContent(Graphics2D g, String[] field, int index,
                int x, int y, int width, int height, int padding, int photoBottom, boolean available) {
            int left = x + padding;
            int right = x + width - padding;
            int textWidth = Math.max(1, right - left);
            int titleBaseline = photoBottom + 25;
            int subtitleBaseline = titleBaseline + 20;
            int hoursBaseline = subtitleBaseline + 19;

            g.setColor(new Color(15, 27, 61));
            g.setFont(new Font("Georgia", Font.BOLD, 20));
            g.drawString(ellipsize(g, field[1], textWidth), left, titleBaseline);

            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.setColor(new Color(107, 114, 128));
            String facility = field.length > 7 && !isAvailabilityValue(field[7]) ? field[7] : "";
            String subtitle = facility.isEmpty() ? field[2] : field[2] + "  " + facility;
            g.drawString(ellipsize(g, subtitle, textWidth), left, subtitleBaseline);
            g.drawString(ellipsize(g, field[4] + " - " + field[5], textWidth), left, hoursBaseline);

            int dividerY = hoursBaseline + 13;
            int cardBottom = y + height;
            g.setColor(new Color(236, 233, 226));
            g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[]{4f, 4f}, 0f));
            g.drawLine(x, dividerY, x + width, dividerY);

            int notchSize = 14;
            g.setColor(new Color(240, 240, 240));
            g.fillOval(x - notchSize / 2, dividerY - notchSize / 2, notchSize, notchSize);
            g.fillOval(x + width - notchSize / 2, dividerY - notchSize / 2, notchSize, notchSize);

            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.setColor(new Color(107, 114, 128));
            g.drawString("Mulai dari", left, dividerY + 27);

            g.setFont(new Font("Georgia", Font.BOLD, 19));
            g.setColor(new Color(15, 27, 61));
            String price = "Rp " + formatPrice(field[3]) + " / jam";
            g.drawString(ellipsize(g, price, textWidth - 98), left, dividerY + 53);

            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.setColor(new Color(192, 57, 75));
            String action = available ? "Lihat Detail" : "Tidak tersedia";
            FontMetrics metrics = g.getFontMetrics();
            int actionX = right - metrics.stringWidth(action);
            int actionBaseline = dividerY + 52;
            g.drawString(action, actionX, actionBaseline);
            if (available) {
                detailHitAreas[index] = new Rectangle(
                        actionX - 8,
                        actionBaseline - metrics.getAscent() - 6,
                        metrics.stringWidth(action) + 16,
                        metrics.getHeight() + 12);
            }
        }

        private String ellipsize(Graphics2D g, String text, int maxWidth) {
            FontMetrics fm = g.getFontMetrics();
            if (maxWidth <= 0) return "";
            if (fm.stringWidth(text) <= maxWidth) return text;
            String shortened = text;
            while (!shortened.isEmpty() && fm.stringWidth(shortened + "...") > maxWidth) {
                shortened = shortened.substring(0, shortened.length() - 1);
            }
            return shortened + "...";
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(24, orientation == SwingConstants.VERTICAL ? visibleRect.height - 24 : visibleRect.width - 24);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
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
        String extra       = field.length > 7 && !isAvailabilityValue(field[7]) ? field[7] : "";
        String status      = field.length > 8 ? field[8]
                : field.length > 7 && isAvailabilityValue(field[7]) ? field[7] : "Tersedia";
        boolean available  = isAvailable(status);

        Color sportColor = getColorForType(type);

        // ── Outer wrapper to enforce fixed card size ──
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));

        // ── Card ──
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout());

        // ── Photo placeholder banner ──
        JPanel banner = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(145, 202, 86),
                        getWidth(), getHeight(), new Color(55, 125, 55)));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 16,
                        Theme.CARD_RADIUS, Theme.CARD_RADIUS));
                g2.fillRect(0, getHeight() - 16, getWidth(), 16);

                g2.setColor(new Color(255, 255, 255, 55));
                g2.setStroke(new BasicStroke(2f));
                int courtX = getWidth() / 4;
                int courtY = 16;
                int courtWidth = getWidth() / 2;
                int courtHeight = getHeight() - 32;
                g2.drawRoundRect(courtX, courtY, courtWidth, courtHeight, 12, 12);
                g2.drawLine(getWidth() / 2, courtY, getWidth() / 2, courtY + courtHeight);
                g2.drawOval(getWidth() / 2 - 18, getHeight() / 2 - 18, 36, 36);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(CARD_WIDTH, 130));

        JPanel photoPlaceholder = new JPanel();
        photoPlaceholder.setLayout(new BoxLayout(photoPlaceholder, BoxLayout.Y_AXIS));
        photoPlaceholder.setOpaque(false);
        photoPlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel photoLabel = new JLabel("Foto lapangan");
        photoLabel.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        photoLabel.setForeground(Color.WHITE);
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        photoPlaceholder.add(photoLabel);
        banner.add(photoPlaceholder);

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
        JPanel badgeRow = new JPanel(new BorderLayout());
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

        JLabel availabilityBadge = createAvailabilityBadge(available);
        badgeRow.add(availabilityBadge, BorderLayout.EAST);

        content.add(badgeRow);
        content.add(Box.createVerticalStrut(12));

        JLabel priceCaption = new JLabel("Harga sewa");
        priceCaption.setFont(Theme.FONT_SMALL);
        priceCaption.setForeground(Theme.TEXT_SECONDARY);
        priceCaption.setAlignmentX(LEFT_ALIGNMENT);
        content.add(priceCaption);

        JLabel priceLabel = new JLabel("Rp " + formatPrice(tarif) + " / jam");
        priceLabel.setFont(Theme.FONT_HEADING.deriveFont(18f));
        priceLabel.setForeground(Theme.TEXT_PRIMARY);
        priceLabel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(Box.createVerticalStrut(2));
        content.add(priceLabel);
        content.add(Box.createVerticalStrut(8));

        content.add(createInfoRow("Jam operasional", jamBuka + " - " + jamTutup));
        content.add(Box.createVerticalStrut(4));
        if (!extra.isEmpty()) {
            content.add(createInfoRow("Fasilitas", extra));
        }

        if (description != null && !description.trim().isEmpty()) {
            JLabel descLabel = new JLabel("<html><body style='width:" + (CARD_WIDTH - 60) + "px'>"
                    + description + "</body></html>");
            descLabel.setFont(Theme.FONT_SMALL);
            descLabel.setForeground(Theme.TEXT_SECONDARY);
            descLabel.setAlignmentX(LEFT_ALIGNMENT);
            content.add(Box.createVerticalStrut(6));
            content.add(descLabel);
        }

        content.add(Box.createVerticalGlue());

        // ── Bottom button ──
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonRow.setOpaque(false);
        buttonRow.setBorder(BorderFactory.createEmptyBorder(8, Theme.PADDING, Theme.PADDING_SM, Theme.PADDING));

        RoundedButton viewBtn = new RoundedButton("Lihat Detail", RoundedButton.Style.SUCCESS);
        viewBtn.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        viewBtn.setPreferredSize(new Dimension(CARD_WIDTH - 2 * Theme.PADDING, 38));
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
    private JPanel createInfoRow(String label, String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel textLabel = new JLabel(label + ": " + text);
        textLabel.setFont(Theme.FONT_SMALL);
        textLabel.setForeground(Theme.TEXT_SECONDARY);

        row.add(textLabel);
        return row;
    }

    private JLabel createAvailabilityBadge(boolean available) {
        Color badgeColor = available ? new Color(22, 101, 52) : new Color(185, 28, 28);
        Color badgeBackground = available ? new Color(220, 252, 231) : new Color(254, 226, 226);
        JLabel badge = new JLabel(available ? "Tersedia" : "Penuh", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(badgeBackground);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        badge.setForeground(badgeColor);
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        badge.setPreferredSize(new Dimension(available ? 76 : 58, 24));
        return badge;
    }

    private boolean isAvailabilityValue(String value) {
        if (value == null) return false;
        String normalized = value.trim().toLowerCase();
        return normalized.equals("true") || normalized.equals("false")
                || normalized.equals("tersedia") || normalized.equals("penuh")
                || normalized.equals("tidak tersedia") || normalized.equals("available")
                || normalized.equals("unavailable");
    }

    private boolean isAvailable(String status) {
        if (status == null) return true;
        String normalized = status.trim().toLowerCase();
        return !normalized.equals("false") && !normalized.equals("penuh")
                && !normalized.equals("tidak tersedia") && !normalized.equals("unavailable");
    }

    private String getFieldStatus(String[] field) {
        String status = field.length > 8 ? field[8]
                : field.length > 7 && isAvailabilityValue(field[7]) ? field[7] : "Tersedia";
        return isAvailable(status) ? "Tersedia" : "Penuh";
    }

    private String formatPrice(String price) {
        if (price == null || price.trim().isEmpty()) return "-";
        String digits = price.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return price;
        try {
            NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));
            return formatter.format(Long.parseLong(digits));
        } catch (NumberFormatException exception) {
            return price;
        }
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

}
