package com.sportfield.ui.admin;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.RoundedButton;
import com.sportfield.ui.components.RoundedTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Panel CRUD lapangan untuk admin.
 */
public class ManageFieldPanel extends JPanel {

    public static final int F_ID = 0;
    public static final int F_NAMA = 1;
    public static final int F_JENIS = 2;
    public static final int F_TARIF = 3;
    public static final int F_BUKA = 4;
    public static final int F_TUTUP = 5;
    public static final int F_STATUS = 6;
    public static final int F_FOTO = 7;

    public static final String STATUS_TERSEDIA = "Tersedia";
    public static final String STATUS_TIDAK = "Tidak Tersedia";

    public static String normalizeStatus(String status) {
        if (status == null) return STATUS_TIDAK;

        if (status.equalsIgnoreCase("Aktif")
                || status.equalsIgnoreCase("Tersedia")) {
            return STATUS_TERSEDIA;
        }

        return STATUS_TIDAK;
    }

    private DefaultTableModel tableModel;
    private JTable table;
    private Runnable onAddField;
    private Consumer<String> onEditField;
    private Consumer<String> onDeleteField;

    public ManageFieldPanel() {
        setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));
        buildUI();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // ── Header row ──
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(LEFT_ALIGNMENT);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Kelola Lapangan");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        titlePanel.add(title);

        JLabel subtitle = new JLabel("Tambah, ubah, atau hapus data lapangan");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_SECONDARY);
        titlePanel.add(subtitle);

        headerRow.add(titlePanel, BorderLayout.WEST);

        RoundedButton addBtn = new RoundedButton("+ Tambah Lapangan", RoundedButton.Style.PRIMARY);
        addBtn.setPreferredSize(new Dimension(200, 40));
        addBtn.addActionListener(e -> { if (onAddField != null) onAddField.run(); });
        headerRow.add(addBtn, BorderLayout.EAST);

        content.add(headerRow);
        content.add(Box.createVerticalStrut(20));

        // ── Table card ──
        CardPanel tableCard = new CardPanel();
        tableCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        tableCard.setLayout(new BorderLayout());
        tableCard.setAlignmentX(LEFT_ALIGNMENT);

        String[] columns = {"No", "Nama", "Jenis", "Tarif/Jam", "Jam Operasional", "Status", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only action column
            }
        };

        table = new JTable(tableModel);
        styleTable(table);

        // Action column with buttons
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionCellEditor());

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(140);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        content.add(tableCard);

        add(content, BorderLayout.CENTER);
    }

    private void styleTable(JTable table) {
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(48);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Theme.PRIMARY_LIGHT);
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Theme.BG_MAIN);
        header.setForeground(Theme.TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        header.setPreferredSize(new Dimension(0, 44));

        // Alternate row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));

                // Status badge styling
                if (col == 5 && val != null) {
                    String status = val.toString();
                    if (status.equals("Aktif")) {
                        setForeground(Theme.SUCCESS);
                    } else {
                        setForeground(Theme.TEXT_SECONDARY);
                    }
                } else {
                    setForeground(Theme.TEXT_PRIMARY);
                }
                return c;
            }
        });
    }

    // ── Public API ──

    public void setFields(String[][] data) {
        tableModel.setRowCount(0);
        for (int i = 0; i < data.length; i++) {
            tableModel.addRow(new Object[]{
                i + 1,
                data[i][1], // nama
                data[i][2], // jenis
                "Rp " + data[i][3], // tarif
                data[i][4] + " - " + data[i][5], // jam
                data[i][6], // status
                data[i][0]  // id (hidden, used by action)
            });
        }
    }

    public void showFieldDialog(String dialogTitle, String[] prefill) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), dialogTitle, true);
        dialog.setSize(440, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));
        panel.setBackground(Color.WHITE);

        boolean isEdit = prefill != null;

        // Jenis
        addDialogLabel(panel, "Jenis Lapangan");
        JComboBox<String> jenisCombo = new JComboBox<>(new String[]{"BADMINTON", "FUTSAL", "BASKETBALL", "TENNIS"});
        jenisCombo.setFont(Theme.FONT_BODY);
        jenisCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        jenisCombo.setAlignmentX(LEFT_ALIGNMENT);
        if (isEdit) {
            jenisCombo.setSelectedItem(prefill[2]);
            jenisCombo.setEnabled(false);
        }
        panel.add(jenisCombo);
        panel.add(Box.createVerticalStrut(12));

        // Nama
        addDialogLabel(panel, "Nama Lapangan");
        RoundedTextField namaField = new RoundedTextField("Masukkan nama lapangan");
        namaField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        namaField.setAlignmentX(LEFT_ALIGNMENT);
        if (isEdit) namaField.setText(prefill[1]);
        panel.add(namaField);
        panel.add(Box.createVerticalStrut(12));

        // Tarif
        addDialogLabel(panel, "Tarif per Jam (Rp)");
        RoundedTextField tarifField = new RoundedTextField("Contoh: 75000");
        tarifField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        tarifField.setAlignmentX(LEFT_ALIGNMENT);
        if (isEdit) tarifField.setText(prefill[3]);
        panel.add(tarifField);
        panel.add(Box.createVerticalStrut(12));

        // Jam operasional
        addDialogLabel(panel, "Jam Operasional");
        JPanel jamRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        jamRow.setOpaque(false);
        jamRow.setAlignmentX(LEFT_ALIGNMENT);
        jamRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        String[] jamOptions = {"06:00","07:00","08:00","09:00","10:00","11:00","12:00",
            "13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"};
        JComboBox<String> bukaCombo = new JComboBox<>(jamOptions);
        JComboBox<String> tutupCombo = new JComboBox<>(jamOptions);
        bukaCombo.setFont(Theme.FONT_BODY);
        tutupCombo.setFont(Theme.FONT_BODY);
        if (isEdit) {
            bukaCombo.setSelectedItem(prefill[4]);
            tutupCombo.setSelectedItem(prefill[5]);
        } else {
            bukaCombo.setSelectedItem("08:00");
            tutupCombo.setSelectedItem("22:00");
        }
        jamRow.add(bukaCombo);
        jamRow.add(new JLabel("s.d."));
        jamRow.add(tutupCombo);
        panel.add(jamRow);
        panel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        RoundedButton cancelBtn = new RoundedButton("Batal", RoundedButton.Style.SECONDARY);
        cancelBtn.addActionListener(e -> dialog.dispose());

        RoundedButton saveBtn = new RoundedButton("Simpan", RoundedButton.Style.PRIMARY);
        saveBtn.addActionListener(e -> dialog.dispose());

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);
        panel.add(btnRow);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void addDialogLabel(JPanel parent, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));
    }

    public void setOnAddField(Runnable r) { this.onAddField = r; }
    public void setOnEditField(Consumer<String> c) { this.onEditField = c; }
    public void setOnDeleteField(Consumer<String> c) { this.onDeleteField = c; }

    // ── Action cell renderer ──
    private class ActionCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JLabel editLabel = new JLabel("✏️ Edit");
        private final JLabel deleteLabel = new JLabel("🗑️ Hapus");

        ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
            setOpaque(true);
            editLabel.setFont(Theme.FONT_SMALL);
            editLabel.setForeground(Theme.PRIMARY);
            editLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteLabel.setFont(Theme.FONT_SMALL);
            deleteLabel.setForeground(Theme.DANGER);
            deleteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            add(editLabel);
            add(new JLabel("|"));
            add(deleteLabel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int row, int col) {
            setBackground(sel ? Theme.PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251)));
            return this;
        }
    }

    // ── Action cell editor ──
    private class ActionCellEditor extends DefaultCellEditor {
        private String fieldId;

        ActionCellEditor() {
            super(new JCheckBox());
            setClickCountToStart(1);
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object val, boolean sel, int row, int col) {
            fieldId = val != null ? val.toString() : "";

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
            panel.setBackground(Theme.PRIMARY_LIGHT);

            JLabel editLabel = new JLabel("✏️ Edit");
            editLabel.setFont(Theme.FONT_SMALL);
            editLabel.setForeground(Theme.PRIMARY);
            editLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    fireEditingStopped();
                    if (onEditField != null) onEditField.accept(fieldId);
                }
            });

            JLabel deleteLabel = new JLabel("🗑️ Hapus");
            deleteLabel.setFont(Theme.FONT_SMALL);
            deleteLabel.setForeground(Theme.DANGER);
            deleteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    fireEditingStopped();
                    if (onDeleteField != null) onDeleteField.accept(fieldId);
                }
            });

            panel.add(editLabel);
            panel.add(new JLabel("|"));
            panel.add(deleteLabel);
            return panel;
        }
    }
}
