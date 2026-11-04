package com.sportfield.ui.customer;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.Locale;

public class FieldDetailPanel extends JPanel {

    private static final Color GREEN = new Color(105, 190, 75);

    private String[] field;
    private Runnable onBack;
    private Runnable onBook;

    private final JPanel content = new JPanel();
    private final RoundedButton bookButton = new RoundedButton("Booking", RoundedButton.Style.SUCCESS);

    public FieldDetailPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(20, 28, 20, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        RoundedButton backButton = new RoundedButton("Kembali", RoundedButton.Style.SECONDARY);
        backButton.setPreferredSize(new Dimension(120, 38));
        backButton.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });
        JLabel heading = new JLabel("Detail Lapangan");
        heading.setFont(Theme.FONT_TITLE.deriveFont(26f));
        heading.setForeground(Theme.TEXT_PRIMARY);
        header.add(backButton, BorderLayout.WEST);
        header.add(heading, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JPanel centeredContent = new JPanel(new GridBagLayout());
        centeredContent.setOpaque(false);
        centeredContent.add(content, new GridBagConstraints(
                0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        JScrollPane scrollPane = new JScrollPane(centeredContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        bookButton.setFont(Theme.FONT_BUTTON);
        bookButton.setPreferredSize(new Dimension(180, 44));
        bookButton.addActionListener(e -> {
            if (onBook != null) onBook.run();
        });
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(bookButton);
        add(footer, BorderLayout.SOUTH);
    }

    public void setField(String[] field) {
        this.field = field == null ? null : field.clone();
        content.removeAll();

        if (this.field == null || this.field.length < 7) {
            content.add(new JLabel("Detail lapangan tidak tersedia."));
            bookButton.setEnabled(false);
            content.revalidate();
            content.repaint();
            return;
        }

        boolean available = isAvailable(getStatus());
        bookButton.setEnabled(available);

        JPanel photo = createPhotoBanner();
        photo.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(photo);
        content.add(Box.createVerticalStrut(16));

        CardPanel infoCard = new CardPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBackground(Color.WHITE);
        infoCard.setBorder(new EmptyBorder(22, 28, 22, 28));
        infoCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(1000, Integer.MAX_VALUE));

        JLabel name = new JLabel(this.field[1]);
        name.setFont(new Font("Georgia", Font.BOLD, 28));
        name.setForeground(new Color(15, 27, 61));
        name.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(name);
        infoCard.add(Box.createVerticalStrut(10));

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        badges.setOpaque(false);
        badges.setAlignmentX(Component.LEFT_ALIGNMENT);
        badges.add(createBadge(this.field[2], new Color(239, 248, 235), new Color(50, 105, 40)));
        badges.add(createBadge(available ? "Tersedia" : "Penuh",
                available ? new Color(232, 185, 35) : new Color(192, 57, 75), Color.WHITE));
        infoCard.add(badges);
        infoCard.add(Box.createVerticalStrut(18));

        JPanel priceStatusRow = new JPanel(new BorderLayout(16, 0));
        priceStatusRow.setOpaque(false);
        priceStatusRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceStatusRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JPanel priceBlock = new JPanel();
        priceBlock.setLayout(new BoxLayout(priceBlock, BoxLayout.Y_AXIS));
        priceBlock.setOpaque(false);
        JLabel priceCaption = new JLabel("Harga sewa");
        priceCaption.setFont(Theme.FONT_SMALL);
        priceCaption.setForeground(Theme.TEXT_SECONDARY);
        JLabel price = new JLabel("Rp " + formatPrice(this.field[3]) + " / jam");
        price.setFont(new Font("Georgia", Font.BOLD, 23));
        price.setForeground(new Color(15, 27, 61));
        priceBlock.add(priceCaption);
        priceBlock.add(Box.createVerticalStrut(3));
        priceBlock.add(price);
        priceStatusRow.add(priceBlock, BorderLayout.WEST);

        JLabel status = new JLabel(available ? "Lapangan tersedia untuk dipesan" : "Lapangan sedang penuh");
        status.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
        status.setForeground(available ? new Color(22, 101, 52) : new Color(185, 28, 28));
        priceStatusRow.add(status, BorderLayout.EAST);
        infoCard.add(priceStatusRow);
        infoCard.add(Box.createVerticalStrut(14));
        infoCard.add(createDivider());
        infoCard.add(Box.createVerticalStrut(14));
        infoCard.add(createSectionTitle("Informasi lapangan"));
        infoCard.add(Box.createVerticalStrut(8));
        infoCard.add(createInfo("Jam operasional", safeField(4) + " - " + safeField(5)));
        infoCard.add(createInfo("Fasilitas", getFacilities()));
        infoCard.add(Box.createVerticalStrut(12));
        infoCard.add(createSectionTitle("Tentang lapangan"));
        JLabel description = new JLabel("<html><div style='width:880px'>"
                + escapeHtml(safeField(6)) + "</div></html>");
        description.setFont(Theme.FONT_BODY);
        description.setForeground(Theme.TEXT_SECONDARY);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(Box.createVerticalStrut(5));
        infoCard.add(description);

        content.add(infoCard);
        content.revalidate();
        content.repaint();
    }

    public String[] getField() {
        return field == null ? null : field.clone();
    }

    public double getHourlyPrice() {
        if (field == null || field.length < 4) return 0;
        String digits = field[3].replaceAll("[^0-9]", "");
        try {
            return digits.isEmpty() ? 0 : Double.parseDouble(digits);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void setOnBook(Runnable onBook) {
        this.onBook = onBook;
    }

    private JPanel createPhotoBanner() {
        JPanel banner = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                Shape rounded = new RoundRectangle2D.Float(0, 0, width, height, 18, 18);
                g.clip(rounded);
                g.setPaint(new GradientPaint(0, 0, new Color(221, 237, 202),
                        width, height, new Color(132, 177, 115)));
                g.fillRect(0, 0, width, height);
                g.setColor(new Color(255, 255, 255, 125));
                g.setStroke(new BasicStroke(1.5f));
                int marginX = Math.max(28, width / 8);
                int marginY = 18;
                int courtWidth = Math.max(1, width - marginX * 2);
                int courtHeight = Math.max(1, height - marginY * 2);
                g.drawRoundRect(marginX, marginY, courtWidth, courtHeight, 12, 12);
                g.drawLine(width / 2, marginY, width / 2, marginY + courtHeight);
                int circle = Math.min(56, height / 3);
                g.drawOval(width / 2 - circle / 2, height / 2 - circle / 2, circle, circle);
                g.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(1000, 220));
        banner.setMaximumSize(new Dimension(1000, 220));
        banner.setMinimumSize(new Dimension(400, 180));
        return banner;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_HEADING);
        label.setForeground(Theme.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createInfo(String title, String value) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel key = new JLabel(title);
        key.setFont(Theme.FONT_BODY);
        key.setForeground(Theme.TEXT_SECONDARY);
        JLabel val = new JLabel(value);
        val.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
        val.setForeground(Theme.TEXT_PRIMARY);
        row.add(key, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    private JPanel createDivider() {
        JPanel divider = new JPanel();
        divider.setBackground(new Color(236, 233, 226));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(1, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        return divider;
    }

    private JLabel createBadge(String text, Color background, Color foreground) {
        JLabel badge = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(background);
                g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g.dispose();
                super.paintComponent(graphics);
            }
        };
        badge.setFont(Theme.FONT_SMALL.deriveFont(Font.BOLD));
        badge.setForeground(foreground);
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(6, 12, 6, 12));
        return badge;
    }

    private String getFacilities() {
        if (field.length > 7 && !isAvailabilityValue(field[7])) return field[7];
        return "Informasi fasilitas belum tersedia";
    }

    private String getStatus() {
        if (field.length > 8) return field[8];
        if (field.length > 7 && isAvailabilityValue(field[7])) return field[7];
        return "Tersedia";
    }

    private boolean isAvailable(String value) {
        String status = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return !status.equals("false") && !status.equals("penuh")
                && !status.equals("tidak tersedia") && !status.equals("unavailable");
    }

    private boolean isAvailabilityValue(String value) {
        if (value == null) return false;
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.equals("true") || normalized.equals("false")
                || normalized.equals("tersedia") || normalized.equals("penuh")
                || normalized.equals("tidak tersedia") || normalized.equals("available")
                || normalized.equals("unavailable");
    }

    private String safeField(int index) {
        return field.length > index && field[index] != null ? field[index] : "-";
    }

    private String formatPrice(String value) {
        String digits = value == null ? "" : value.replaceAll("[^0-9]", "");
        try {
            return digits.isEmpty() ? "-" : NumberFormat.getNumberInstance(new Locale("id", "ID"))
                    .format(Long.parseLong(digits));
        } catch (NumberFormatException exception) {
            return value;
        }
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
