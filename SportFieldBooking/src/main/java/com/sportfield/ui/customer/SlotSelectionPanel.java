package com.sportfield.ui.customer;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Panel untuk mencari dan memilih slot waktu yang tersedia.
 * Menampilkan filter lapangan/tanggal, grid slot waktu, dan ringkasan reservasi.
 */
public class SlotSelectionPanel extends JPanel {

    // ── Data ──
    private String[] fieldNames = {};
    private String[] fieldIds = {};
    private boolean[] slotAvailability;
    private final List<Integer> selectedSlots = new ArrayList<>();
    private double pricePerHour = 0;

    // ── Callbacks ──
    private Runnable onSearch;
    private Runnable onBook;

    // ── UI Components ──
    private final JComboBox<String> fieldCombo;
    private final JComboBox<String> dateCombo;
    private final RoundedButton searchButton;

    private final CardPanel slotCard;
    private final JLabel slotTitleLabel;
    private final JPanel slotGridPanel;
    private final JLabel summaryLabel;
    private final RoundedButton bookButton;

    private final JLabel emptyLabel;

    // ── Constants ──
    private static final int TOTAL_SLOTS = 14; // 08:00 - 22:00
    private static final int START_HOUR = 8;
    private static final int MAX_CONSECUTIVE = 3;
    private static final String[] SLOT_LABELS = new String[TOTAL_SLOTS];

    static {
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            int startH = START_HOUR + i;
            int endH = startH + 1;
            SLOT_LABELS[i] = String.format("%02d:00 - %02d:00", startH, endH);
        }
    }

    public SlotSelectionPanel() {
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

        JLabel titleLabel = new JLabel("Cari Jadwal");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Pilih lapangan dan tanggal untuk melihat slot tersedia");
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
        filterCard.setBorder(new EmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));
        filterCard.setAlignmentX(LEFT_ALIGNMENT);
        filterCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.PADDING_SM, 0));
        filterRow.setOpaque(false);

        // Lapangan combo
        JPanel fieldGroup = createFormGroup("Lapangan");
        fieldCombo = createStyledCombo();
        fieldCombo.setPreferredSize(new Dimension(220, 38));
        fieldGroup.add(fieldCombo);
        filterRow.add(fieldGroup);

        // Tanggal combo
        JPanel dateGroup = createFormGroup("Tanggal");
        dateCombo = createStyledCombo();
        dateCombo.setPreferredSize(new Dimension(260, 38));
        populateDates();
        dateGroup.add(dateCombo);
        filterRow.add(dateGroup);

        // Search button
        searchButton = new RoundedButton("Cari", RoundedButton.Style.PRIMARY);
        searchButton.setPreferredSize(new Dimension(120, 38));
        searchButton.addActionListener(e -> {
            if (onSearch != null) onSearch.run();
        });

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnWrapper.setOpaque(false);
        btnWrapper.setBorder(new EmptyBorder(20, 0, 0, 0));
        btnWrapper.add(searchButton);
        filterRow.add(btnWrapper);

        filterCard.add(filterRow, BorderLayout.CENTER);
        content.add(filterCard);
        content.add(Box.createVerticalStrut(Theme.PADDING));

        // ── Empty State ──
        emptyLabel = new JLabel("Pilih lapangan dan tanggal untuk melihat ketersediaan.");
        emptyLabel.setFont(Theme.FONT_BODY);
        emptyLabel.setForeground(Theme.TEXT_SECONDARY);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setAlignmentX(LEFT_ALIGNMENT);
        emptyLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        emptyLabel.setPreferredSize(new Dimension(800, 200));

        CardPanel emptyCard = new CardPanel();
        emptyCard.setLayout(new BorderLayout());
        emptyCard.setAlignmentX(LEFT_ALIGNMENT);
        emptyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        emptyCard.add(emptyLabel, BorderLayout.CENTER);
        content.add(emptyCard);

        // ── Slot Card ──
        slotCard = new CardPanel();
        slotCard.setLayout(new BorderLayout());
        slotCard.setBorder(new EmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));
        slotCard.setAlignmentX(LEFT_ALIGNMENT);
        slotCard.setVisible(false);

        // Slot title
        slotTitleLabel = new JLabel("Slot Tersedia");
        slotTitleLabel.setFont(Theme.FONT_SUBTITLE);
        slotTitleLabel.setForeground(Theme.TEXT_PRIMARY);
        slotTitleLabel.setBorder(new EmptyBorder(0, 0, Theme.PADDING_SM, 0));

        // Slot grid
        slotGridPanel = new JPanel(new GridLayout(0, 4, Theme.PADDING_SM, Theme.PADDING_SM));
        slotGridPanel.setOpaque(false);

        // Summary + Book
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(Theme.PADDING, 0, 0, 0));

        summaryLabel = new JLabel("Pilih slot untuk melihat ringkasan");
        summaryLabel.setFont(Theme.FONT_BODY);
        summaryLabel.setForeground(Theme.TEXT_SECONDARY);

        bookButton = new RoundedButton("Buat Reservasi", RoundedButton.Style.PRIMARY);
        bookButton.setPreferredSize(new Dimension(180, 42));
        bookButton.setEnabled(false);
        bookButton.addActionListener(e -> {
            if (onBook != null) onBook.run();
        });

        bottomPanel.add(summaryLabel, BorderLayout.CENTER);
        bottomPanel.add(bookButton, BorderLayout.EAST);

        JPanel slotInner = new JPanel();
        slotInner.setLayout(new BoxLayout(slotInner, BoxLayout.Y_AXIS));
        slotInner.setOpaque(false);

        slotTitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        slotGridPanel.setAlignmentX(LEFT_ALIGNMENT);
        bottomPanel.setAlignmentX(LEFT_ALIGNMENT);

        slotInner.add(slotTitleLabel);
        slotInner.add(slotGridPanel);
        slotInner.add(bottomPanel);

        slotCard.add(slotInner, BorderLayout.CENTER);
        content.add(slotCard);

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
     * Mengisi pilihan lapangan pada dropdown.
     */
    public void setFieldOptions(String[] fieldNames, String[] fieldIds) {
        this.fieldNames = fieldNames;
        this.fieldIds = fieldIds;
        fieldCombo.removeAllItems();
        for (String name : fieldNames) {
            fieldCombo.addItem(name);
        }
    }

    /**
     * Menampilkan slot yang tersedia untuk lapangan dan tanggal tertentu.
     * @param fieldName  nama lapangan
     * @param date       tanggal display string
     * @param available  array 14 boolean (08:00-22:00), true = tersedia
     */
    public void setSlots(String fieldName, String date, boolean[] available) {
        this.slotAvailability = available;
        this.selectedSlots.clear();

        slotTitleLabel.setText("Slot Tersedia — " + fieldName + " — " + date);

        slotGridPanel.removeAll();
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            final int idx = i;
            if (available != null && i < available.length && available[i]) {
                // Available slot
                JButton slotBtn = createSlotButton(SLOT_LABELS[i], true);
                slotBtn.addActionListener(e -> toggleSlot(idx));
                slotGridPanel.add(slotBtn);
            } else {
                // Booked slot
                JButton slotBtn = createSlotButton("Terisi", false);
                slotBtn.setEnabled(false);
                slotGridPanel.add(slotBtn);
            }
        }

        emptyLabel.getParent().setVisible(false);
        slotCard.setVisible(true);
        updateSummary();
        slotGridPanel.revalidate();
        slotGridPanel.repaint();
    }

    /**
     * Mendapatkan ID lapangan yang dipilih.
     */
    public String getSelectedFieldId() {
        int idx = fieldCombo.getSelectedIndex();
        if (idx >= 0 && idx < fieldIds.length) {
            return fieldIds[idx];
        }
        return null;
    }

    /** Memilih lapangan berdasarkan ID dari halaman daftar lapangan. */
    public void setSelectedFieldId(String fieldId) {
        for (int i = 0; i < fieldIds.length; i++) {
            if (fieldIds[i].equals(fieldId)) {
                fieldCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Mendapatkan tanggal yang dipilih dari dropdown.
     */
    public String getSelectedDate() {
        return (String) dateCombo.getSelectedItem();
    }

    /**
     * Mendapatkan indeks slot yang dipilih.
     */
    public int[] getSelectedSlotIndices() {
        return selectedSlots.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Menetapkan callback saat tombol "Cari" ditekan.
     */
    public void setOnSearch(Runnable onSearch) {
        this.onSearch = onSearch;
    }

    /**
     * Menetapkan callback saat tombol "Buat Reservasi" ditekan.
     */
    public void setOnBook(Runnable onBook) {
        this.onBook = onBook;
    }

    /**
     * Menetapkan harga per jam untuk menampilkan total.
     */
    public void setPricePerHour(double price) {
        this.pricePerHour = price;
        updateSummary();
    }

    // ── Private Helpers ──

    private void toggleSlot(int index) {
        if (selectedSlots.contains(index)) {
            // Deselect
            selectedSlots.remove(Integer.valueOf(index));
        } else {
            // Check if adding maintains consecutiveness and max limit
            if (selectedSlots.size() >= MAX_CONSECUTIVE) {
                return; // Already at max
            }
            if (canAddSlot(index)) {
                selectedSlots.add(index);
                selectedSlots.sort(Integer::compareTo);
            }
        }
        refreshSlotGrid();
        updateSummary();
    }

    /**
     * Checks whether adding the given index keeps the selection consecutive.
     */
    private boolean canAddSlot(int index) {
        if (selectedSlots.isEmpty()) return true;

        List<Integer> test = new ArrayList<>(selectedSlots);
        test.add(index);
        test.sort(Integer::compareTo);

        // Check consecutive
        for (int i = 1; i < test.size(); i++) {
            if (test.get(i) - test.get(i - 1) != 1) {
                return false;
            }
        }
        return true;
    }

    private void refreshSlotGrid() {
        Component[] components = slotGridPanel.getComponents();
        for (int i = 0; i < components.length && i < TOTAL_SLOTS; i++) {
            if (components[i] instanceof JButton) {
                JButton btn = (JButton) components[i];
                if (!btn.isEnabled()) continue; // booked slot

                if (selectedSlots.contains(i)) {
                    btn.setBackground(Theme.PRIMARY);
                    btn.setForeground(Theme.TEXT_WHITE);
                    btn.setText(SLOT_LABELS[i]);
                } else {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(Theme.SUCCESS);
                    btn.setText(SLOT_LABELS[i]);
                }
            }
        }
        slotGridPanel.repaint();
    }

    private void updateSummary() {
        if (selectedSlots.isEmpty()) {
            summaryLabel.setText("Pilih slot untuk melihat ringkasan");
            summaryLabel.setForeground(Theme.TEXT_SECONDARY);
            bookButton.setEnabled(false);
        } else {
            int duration = selectedSlots.size();
            double total = duration * pricePerHour;
            NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
            summaryLabel.setText("Durasi: " + duration + " jam | Total: Rp " + nf.format(total));
            summaryLabel.setForeground(Theme.TEXT_PRIMARY);
            summaryLabel.setFont(Theme.FONT_HEADING);
            bookButton.setEnabled(true);
        }
    }

    private JButton createSlotButton(String text, boolean available) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int r = 10;
                if (!isEnabled()) {
                    // Booked — gray
                    g2.setColor(new Color(243, 244, 246));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));
                } else {
                    g2.setColor(getBackground());
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), r, r));

                    // Border
                    if (getBackground().equals(Color.WHITE)) {
                        g2.setColor(Theme.SUCCESS);
                    } else {
                        g2.setColor(Theme.PRIMARY);
                    }
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, r, r));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setPreferredSize(new Dimension(160, 44));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (available) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Theme.SUCCESS);
        } else {
            btn.setBackground(new Color(243, 244, 246));
            btn.setForeground(Theme.TEXT_SECONDARY);
            btn.setCursor(Cursor.getDefaultCursor());
        }
        return btn;
    }

    private JPanel createFormGroup(String label) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        panel.add(lbl);

        return panel;
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

    private void populateDates() {
        Locale locale = new Locale("id", "ID");
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 14; i++) {
            LocalDate date = today.plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
            // Capitalize first letter
            dayName = dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
            String monthName = date.getMonth().getDisplayName(TextStyle.SHORT, locale);
            // Capitalize first letter
            monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
            String display = dayName + ", " + date.getDayOfMonth() + " " + monthName + " " + date.getYear();
            dateCombo.addItem(display);
        }
    }
}
