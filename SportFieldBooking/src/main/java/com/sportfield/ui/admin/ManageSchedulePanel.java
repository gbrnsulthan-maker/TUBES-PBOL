package com.sportfield.ui.admin;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel kelola jadwal operasional lapangan.
 */
public class ManageSchedulePanel extends JPanel {

    private JComboBox<String> fieldCombo;
    private String[] fieldIds;
    private JComboBox<String> bukaCombo;
    private JComboBox<String> tutupCombo;
    private JPanel slotGrid;
    private Runnable onSave;

    public ManageSchedulePanel() {
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
        JLabel title = new JLabel("Kelola Jadwal Operasional");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        content.add(title);

        JLabel subtitle = new JLabel("Atur jam buka dan tutup setiap lapangan");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(20));

        // ── Field selector ──
        CardPanel selectorCard = new CardPanel();
        selectorCard.setBorder(new EmptyBorder(20, 24, 20, 24));
        selectorCard.setLayout(new BoxLayout(selectorCard, BoxLayout.Y_AXIS));
        selectorCard.setAlignmentX(LEFT_ALIGNMENT);
        selectorCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel selectLabel = new JLabel("Pilih Lapangan");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectLabel.setForeground(Theme.TEXT_SECONDARY);
        selectLabel.setAlignmentX(LEFT_ALIGNMENT);
        selectorCard.add(selectLabel);
        selectorCard.add(Box.createVerticalStrut(8));

        fieldCombo = new JComboBox<>(new String[]{"-- Pilih Lapangan --"});
        fieldCombo.setFont(Theme.FONT_BODY);
        fieldCombo.setMaximumSize(new Dimension(400, 40));
        fieldCombo.setAlignmentX(LEFT_ALIGNMENT);
        selectorCard.add(fieldCombo);

        content.add(selectorCard);
        content.add(Box.createVerticalStrut(16));

        // ── Schedule card ──
        CardPanel scheduleCard = new CardPanel();
        scheduleCard.setBorder(new EmptyBorder(24, 24, 24, 24));
        scheduleCard.setLayout(new BoxLayout(scheduleCard, BoxLayout.Y_AXIS));
        scheduleCard.setAlignmentX(LEFT_ALIGNMENT);
        scheduleCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel schedTitle = new JLabel("⏰ Jam Operasional");
        schedTitle.setFont(Theme.FONT_SUBTITLE);
        schedTitle.setForeground(Theme.TEXT_PRIMARY);
        schedTitle.setAlignmentX(LEFT_ALIGNMENT);
        scheduleCard.add(schedTitle);
        scheduleCard.add(Box.createVerticalStrut(16));

        JPanel jamRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        jamRow.setOpaque(false);
        jamRow.setAlignmentX(LEFT_ALIGNMENT);

        String[] jamOptions = {"06:00","07:00","08:00","09:00","10:00","11:00","12:00",
            "13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};

        JLabel bukaLabel = new JLabel("Jam Buka:");
        bukaLabel.setFont(Theme.FONT_BODY);
        bukaLabel.setForeground(Theme.TEXT_PRIMARY);

        bukaCombo = new JComboBox<>(jamOptions);
        bukaCombo.setFont(Theme.FONT_BODY);
        bukaCombo.setSelectedItem("08:00");
        bukaCombo.setPreferredSize(new Dimension(100, 36));

        JLabel tutupLabel = new JLabel("Jam Tutup:");
        tutupLabel.setFont(Theme.FONT_BODY);
        tutupLabel.setForeground(Theme.TEXT_PRIMARY);

        tutupCombo = new JComboBox<>(jamOptions);
        tutupCombo.setFont(Theme.FONT_BODY);
        tutupCombo.setSelectedItem("22:00");
        tutupCombo.setPreferredSize(new Dimension(100, 36));

        RoundedButton saveBtn = new RoundedButton("💾 Simpan Jadwal", RoundedButton.Style.PRIMARY);
        saveBtn.setPreferredSize(new Dimension(180, 38));
        saveBtn.addActionListener(e -> { if (onSave != null) onSave.run(); });

        jamRow.add(bukaLabel);
        jamRow.add(bukaCombo);
        jamRow.add(Box.createHorizontalStrut(16));
        jamRow.add(tutupLabel);
        jamRow.add(tutupCombo);
        jamRow.add(Box.createHorizontalStrut(24));
        jamRow.add(saveBtn);

        scheduleCard.add(jamRow);
        content.add(scheduleCard);
        content.add(Box.createVerticalStrut(16));

        // ── Slot preview ──
        CardPanel previewCard = new CardPanel();
        previewCard.setBorder(new EmptyBorder(24, 24, 24, 24));
        previewCard.setLayout(new BorderLayout());
        previewCard.setAlignmentX(LEFT_ALIGNMENT);

        JLabel previewTitle = new JLabel("📅 Preview Slot");
        previewTitle.setFont(Theme.FONT_SUBTITLE);
        previewTitle.setForeground(Theme.TEXT_PRIMARY);
        previewCard.add(previewTitle, BorderLayout.NORTH);

        slotGrid = new JPanel(new GridLayout(0, 7, 6, 6));
        slotGrid.setOpaque(false);
        slotGrid.setBorder(new EmptyBorder(16, 0, 0, 0));
        refreshSlotPreview();

        previewCard.add(slotGrid, BorderLayout.CENTER);
        content.add(previewCard);

        // Update preview when combo changes
        bukaCombo.addActionListener(e -> refreshSlotPreview());
        tutupCombo.addActionListener(e -> refreshSlotPreview());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.BG_MAIN);
        add(scroll, BorderLayout.CENTER);
    }

    private void refreshSlotPreview() {
        slotGrid.removeAll();
        String buka = (String) bukaCombo.getSelectedItem();
        String tutup = (String) tutupCombo.getSelectedItem();
        if (buka == null || tutup == null) return;

        int start = Integer.parseInt(buka.split(":")[0]);
        int end = Integer.parseInt(tutup.split(":")[0]);

        for (int h = start; h < end; h++) {
            JLabel slot = new JLabel(String.format("%02d:00 - %02d:00", h, h + 1), SwingConstants.CENTER);
            slot.setFont(Theme.FONT_SMALL);
            slot.setOpaque(true);
            slot.setBackground(new Color(220, 252, 231)); // Light green = available
            slot.setForeground(new Color(22, 101, 52));
            slot.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
                new EmptyBorder(8, 6, 8, 6)
            ));
            slotGrid.add(slot);
        }

        slotGrid.revalidate();
        slotGrid.repaint();
    }

    // ── Public API ──

    public void setFieldOptions(String[] names, String[] ids) {
        this.fieldIds = ids;
        fieldCombo.removeAllItems();
        for (String name : names) {
            fieldCombo.addItem(name);
        }
    }

    public void setSchedule(String jamBuka, String jamTutup) {
        bukaCombo.setSelectedItem(jamBuka);
        tutupCombo.setSelectedItem(jamTutup);
        refreshSlotPreview();
    }

    public String getSelectedFieldId() {
        int idx = fieldCombo.getSelectedIndex();
        return (fieldIds != null && idx >= 0 && idx < fieldIds.length) ? fieldIds[idx] : "";
    }

    public String getJamBuka() { return (String) bukaCombo.getSelectedItem(); }
    public String getJamTutup() { return (String) tutupCombo.getSelectedItem(); }
    public void setOnSave(Runnable r) { this.onSave = r; }
}
