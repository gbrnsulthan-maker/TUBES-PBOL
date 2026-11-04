package com.sportfield.ui.admin;

import com.sportfield.ui.Theme;
import com.sportfield.ui.components.CardPanel;
import com.sportfield.ui.components.FieldVisuals;
import com.sportfield.ui.components.RoundedButton;
import com.sportfield.ui.components.RoundedTextField;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Panel "Tambah Lapangan" (F10), juga dipakai untuk mode Edit.
 *
 * Isian: nama lapangan, jenis olahraga, harga per jam, status lapangan, dan foto lapangan.
 * Saat Simpan ditekan dan semua isian valid, callback onSave menerima data lapangan
 * dengan format yang sama dengan {@link ManageFieldPanel}:
 * [id, nama, jenis, tarif, jamBuka, jamTutup, status, fotoPath]
 * Id kosong berarti lapangan baru.
 *
 * Panel tidak menyimpan data sendiri. Penyimpanan dilakukan oleh pemanggil
 * (nanti lewat FieldService).
 */
public class AddFieldPanel extends JPanel {

    private static final String PICK_JENIS = "Pilih jenis olahraga";
    private static final String[] JENIS = {"BADMINTON", "FUTSAL", "BASKETBALL", "TENNIS"};
    private static final long MAX_PHOTO_BYTES = 2L * 1024 * 1024;

    private final JLabel titleLabel = new JLabel("Tambah Lapangan");
    private final JLabel subtitleLabel = new JLabel();

    private RoundedTextField namaField;
    private RoundedTextField hargaField;
    private JComboBox<String> jenisCombo;
    private JRadioButton rbTersedia;
    private JRadioButton rbTidak;
    private PhotoPreview photoPreview;
    private RoundedButton saveBtn;

    private final JLabel errNama = errorLabel();
    private final JLabel errJenis = errorLabel();
    private final JLabel errHarga = errorLabel();
    private final JLabel errFoto = errorLabel();

    private String editingId = null;
    private String jamBuka = "08:00";
    private String jamTutup = "22:00";
    private String fotoPath = "";

    private Consumer<String[]> onSave;
    private Runnable onCancel;
    private java.util.function.BiPredicate<String, String> nameTaken;

    public AddFieldPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(
            Theme.PADDING_LG, Theme.PADDING_LG, Theme.PADDING_LG, Theme.PADDING_LG));
        buildUI();
        prepareNew();
    }

    // ════════════════════════════════════════
    //  UI
    // ════════════════════════════════════════

    private void buildUI() {
        // ── Judul ──
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.PADDING, 0));

        RoundedButton back = new RoundedButton("← Kembali", RoundedButton.Style.OUTLINE);
        back.setPreferredSize(new Dimension(120, 36));
        back.addActionListener(e -> cancel());
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backRow.setOpaque(false);
        backRow.setAlignmentX(LEFT_ALIGNMENT);
        backRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        backRow.add(back);

        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setFont(Theme.FONT_BODY);
        subtitleLabel.setForeground(Theme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        top.add(backRow);
        top.add(Box.createVerticalStrut(14));
        top.add(titleLabel);
        top.add(Box.createVerticalStrut(2));
        top.add(subtitleLabel);
        add(top, BorderLayout.NORTH);

        // ── Kartu form ──
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 20, 28));

        JPanel columns = new JPanel(new GridBagLayout());
        columns.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weighty = 1.0;
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 0, 0, 32);
        columns.add(buildFieldsColumn(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 0, 0, 0);
        columns.add(buildPhotoColumn(), gbc);

        card.add(columns, BorderLayout.CENTER);
        card.add(buildButtonRow(), BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Theme.BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildFieldsColumn() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);

        // Nama
        col.add(fieldLabel("Nama Lapangan"));
        namaField = new RoundedTextField("Contoh: Lapangan Futsal A");
        namaField.setGlassStyle(false);
        sizeFull(namaField, 46);
        col.add(namaField);
        col.add(errNama);
        col.add(Box.createVerticalStrut(12));

        // Jenis
        col.add(fieldLabel("Jenis Olahraga"));
        String[] items = new String[JENIS.length + 1];
        items[0] = PICK_JENIS;
        for (int i = 0; i < JENIS.length; i++) items[i + 1] = FieldVisuals.capitalize(JENIS[i]);
        jenisCombo = new JComboBox<>(items);
        FieldVisuals.styleCombo(jenisCombo);
        sizeFull(jenisCombo, 44);
        col.add(jenisCombo);
        col.add(errJenis);
        col.add(Box.createVerticalStrut(12));

        // Harga
        col.add(fieldLabel("Harga per Jam (Rp)"));
        hargaField = new RoundedTextField("Contoh: 75000");
        hargaField.setGlassStyle(false);
        sizeFull(hargaField, 46);
        ((AbstractDocument) hargaField.getDocument()).setDocumentFilter(new DigitsOnlyFilter(9));
        col.add(hargaField);
        col.add(errHarga);
        col.add(Box.createVerticalStrut(12));

        // Status
        col.add(fieldLabel("Status Lapangan"));
        rbTersedia = radio(ManageFieldPanel.STATUS_TERSEDIA);
        rbTidak = radio(ManageFieldPanel.STATUS_TIDAK);
        ButtonGroup group = new ButtonGroup();
        group.add(rbTersedia);
        group.add(rbTidak);
        rbTersedia.setSelected(true);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusRow.setOpaque(false);
        statusRow.setAlignmentX(LEFT_ALIGNMENT);
        statusRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        statusRow.add(rbTersedia);
        statusRow.add(Box.createHorizontalStrut(20));
        statusRow.add(rbTidak);
        col.add(statusRow);

        JLabel hint = new JLabel("Lapangan \"Tidak Tersedia\" tidak tampil untuk pelanggan.");
        hint.setFont(Theme.FONT_SMALL);
        hint.setForeground(Theme.TEXT_SECONDARY);
        hint.setAlignmentX(LEFT_ALIGNMENT);
        hint.setBorder(new EmptyBorder(4, 0, 0, 0));
        col.add(hint);

        return col;
    }

    private JPanel buildPhotoColumn() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);

        col.add(fieldLabel("Foto Lapangan"));

        photoPreview = new PhotoPreview();
        photoPreview.setAlignmentX(LEFT_ALIGNMENT);
        photoPreview.setPreferredSize(new Dimension(340, 214));
        photoPreview.setMinimumSize(new Dimension(220, 150));
        photoPreview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        photoPreview.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            @SuppressWarnings("unchecked")
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) return false;
                try {
                    List<File> files = (List<File>) support.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) acceptPhoto(files.get(0));
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        });
        col.add(photoPreview);
        col.add(Box.createVerticalStrut(10));

        RoundedButton pick = new RoundedButton("Pilih Foto", RoundedButton.Style.OUTLINE);
        pick.setPreferredSize(new Dimension(120, 36));
        pick.addActionListener(e -> choosePhoto());
        RoundedButton remove = new RoundedButton("Hapus Foto", RoundedButton.Style.OUTLINE);
        remove.setPreferredSize(new Dimension(120, 36));
        remove.addActionListener(e -> { setPhoto(""); errFoto.setText(" "); });

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.add(pick);
        row.add(Box.createHorizontalStrut(10));
        row.add(remove);
        col.add(row);

        col.add(errFoto);
        JLabel hint = new JLabel("JPG atau PNG, maksimal 2 MB. Tanpa foto, kartu memakai warna sesuai jenis olahraga.");
        hint.setFont(Theme.FONT_SMALL);
        hint.setForeground(Theme.TEXT_SECONDARY);
        hint.setAlignmentX(LEFT_ALIGNMENT);
        col.add(hint);

        return col;
    }

    private JPanel buildButtonRow() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER);
        wrap.add(sep, BorderLayout.NORTH);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        RoundedButton cancel = new RoundedButton("Batal", RoundedButton.Style.OUTLINE);
        cancel.setPreferredSize(new Dimension(120, 42));
        cancel.addActionListener(e -> cancel());

        saveBtn = new RoundedButton("Simpan", RoundedButton.Style.PRIMARY);
        saveBtn.setPreferredSize(new Dimension(160, 42));
        saveBtn.addActionListener(e -> save());

        row.add(cancel);
        row.add(saveBtn);
        wrap.add(row, BorderLayout.CENTER);
        return wrap;
    }

    // ════════════════════════════════════════
    //  API publik
    // ════════════════════════════════════════

    /** Menyiapkan form kosong untuk lapangan baru. */
    public void prepareNew() {
        editingId = null;
        jamBuka = "08:00";
        jamTutup = "22:00";
        titleLabel.setText("Tambah Lapangan");
        subtitleLabel.setText("Isi data lapangan baru. Semua isian kecuali foto wajib diisi.");
        saveBtn.setText("Simpan");

        namaField.setText("");
        hargaField.setText("");
        jenisCombo.setSelectedIndex(0);
        jenisCombo.setEnabled(true);
        rbTersedia.setSelected(true);
        setPhoto("");
        clearErrors();
    }

    /** Menyiapkan form berisi data lapangan yang ada. Jenis tidak bisa diubah (F10-R03). */
    public void prepareEdit(String[] row) {
        prepareNew();
        editingId = value(row, ManageFieldPanel.F_ID);
        jamBuka = valueOr(row, ManageFieldPanel.F_BUKA, "08:00");
        jamTutup = valueOr(row, ManageFieldPanel.F_TUTUP, "22:00");

        titleLabel.setText("Edit Lapangan");
        subtitleLabel.setText("Ubah data lapangan. Jenis olahraga tidak bisa diubah.");
        saveBtn.setText("Simpan Perubahan");

        namaField.setText(value(row, ManageFieldPanel.F_NAMA));
        hargaField.setText(value(row, ManageFieldPanel.F_TARIF).replaceAll("\\D", ""));

        String jenis = value(row, ManageFieldPanel.F_JENIS);
        for (int i = 1; i < jenisCombo.getItemCount(); i++) {
            if (jenisCombo.getItemAt(i).equalsIgnoreCase(jenis)) jenisCombo.setSelectedIndex(i);
        }
        jenisCombo.setEnabled(false);

        boolean ok = ManageFieldPanel.STATUS_TERSEDIA.equals(
            ManageFieldPanel.normalizeStatus(value(row, ManageFieldPanel.F_STATUS)));
        rbTersedia.setSelected(ok);
        rbTidak.setSelected(!ok);

        setPhoto(value(row, ManageFieldPanel.F_FOTO));
    }

    public void setOnSave(Consumer<String[]> onSave) { this.onSave = onSave; }
    public void setOnCancel(Runnable onCancel) { this.onCancel = onCancel; }

    /**
     * Opsional: pengecekan nama ganda. Predikat menerima (nama, idYangDiedit) dan
     * mengembalikan true jika nama sudah dipakai lapangan lain.
     */
    public void setNameTakenCheck(java.util.function.BiPredicate<String, String> check) {
        this.nameTaken = check;
    }

    // ════════════════════════════════════════
    //  Aksi
    // ════════════════════════════════════════

    private void cancel() {
        if (onCancel != null) onCancel.run();
    }

    private void save() {
        if (!validateForm()) return;

        String jenis = JENIS[jenisCombo.getSelectedIndex() - 1];
        String status = rbTersedia.isSelected()
            ? ManageFieldPanel.STATUS_TERSEDIA : ManageFieldPanel.STATUS_TIDAK;
        long harga = Long.parseLong(hargaField.getText().trim());

        String[] row = new String[8];
        row[ManageFieldPanel.F_ID] = editingId == null ? "" : editingId;
        row[ManageFieldPanel.F_NAMA] = namaField.getText().trim();
        row[ManageFieldPanel.F_JENIS] = jenis;
        row[ManageFieldPanel.F_TARIF] = NumberFormat.getInstance(Locale.forLanguageTag("id-ID")).format(harga);
        row[ManageFieldPanel.F_BUKA] = jamBuka;
        row[ManageFieldPanel.F_TUTUP] = jamTutup;
        row[ManageFieldPanel.F_STATUS] = status;
        row[ManageFieldPanel.F_FOTO] = fotoPath;

        if (onSave != null) onSave.accept(row);
    }

    private boolean validateForm() {
        clearErrors();
        JComponent firstInvalid = null;

        String nama = namaField.getText().trim();
        if (nama.isEmpty()) {
            errNama.setText("Nama lapangan wajib diisi.");
            firstInvalid = namaField;
        } else if (nama.length() < 3) {
            errNama.setText("Nama lapangan minimal 3 karakter.");
            firstInvalid = namaField;
        } else if (nameTaken != null && nameTaken.test(nama, editingId == null ? "" : editingId)) {
            errNama.setText("Nama ini sudah dipakai lapangan lain.");
            firstInvalid = namaField;
        }

        if (jenisCombo.getSelectedIndex() <= 0) {
            errJenis.setText("Pilih jenis olahraga.");
            if (firstInvalid == null) firstInvalid = jenisCombo;
        }

        String hargaText = hargaField.getText().trim();
        long harga = 0;
        if (!hargaText.isEmpty()) {
            try { harga = Long.parseLong(hargaText); } catch (NumberFormatException ignored) { }
        }
        if (harga <= 0) {
            errHarga.setText("Tarif per jam harus lebih dari Rp 0.");
            if (firstInvalid == null) firstInvalid = hargaField;
        }

        if (firstInvalid != null) firstInvalid.requestFocusInWindow();
        return firstInvalid == null;
    }

    private void clearErrors() {
        errNama.setText(" ");
        errJenis.setText(" ");
        errHarga.setText(" ");
        errFoto.setText(" ");
    }

    // ── Foto ──

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih foto lapangan");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Gambar (JPG, PNG)", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            acceptPhoto(chooser.getSelectedFile());
        }
    }

    private void acceptPhoto(File file) {
        errFoto.setText(" ");
        if (file == null || !file.isFile()) {
            errFoto.setText("File tidak ditemukan.");
            return;
        }
        if (file.length() > MAX_PHOTO_BYTES) {
            errFoto.setText("Ukuran foto melebihi 2 MB. Pilih foto yang lebih kecil.");
            return;
        }
        BufferedImage img;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            img = null;
        }
        if (img == null) {
            errFoto.setText("File bukan gambar yang valid. Gunakan JPG atau PNG.");
            return;
        }
        setPhoto(file.getAbsolutePath());
    }

    private void setPhoto(String path) {
        fotoPath = path == null ? "" : path;
        photoPreview.setPath(fotoPath);
    }

    // ════════════════════════════════════════
    //  Helper UI
    // ════════════════════════════════════════

    private static String value(String[] row, int i) {
        return row != null && i < row.length && row[i] != null ? row[i] : "";
    }

    private static String valueOr(String[] row, int i, String def) {
        String v = value(row, i);
        return v.isEmpty() ? def : v;
    }

    private static JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Theme.TEXT_SECONDARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 5, 0));
        return lbl;
    }

    private static JLabel errorLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.DANGER);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(3, 2, 0, 0));
        return lbl;
    }

    private static void sizeFull(JComponent c, int height) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        c.setPreferredSize(new Dimension(300, height));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    private static JRadioButton radio(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(Theme.FONT_BODY);
        rb.setForeground(Theme.TEXT_PRIMARY);
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return rb;
    }

    /** Hanya mengizinkan angka (untuk kolom harga). */
    private static class DigitsOnlyFilter extends DocumentFilter {
        private final int maxLength;

        DigitsOnlyFilter(int maxLength) { this.maxLength = maxLength; }

        @Override
        public void insertString(FilterBypass fb, int offset, String s, AttributeSet a) throws BadLocationException {
            replace(fb, offset, 0, s, a);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String s, AttributeSet a) throws BadLocationException {
            if (s == null) { super.replace(fb, offset, length, s, a); return; }
            String clean = s.replaceAll("\\D", "");
            int room = maxLength - (fb.getDocument().getLength() - length);
            if (room <= 0) return;
            if (clean.length() > room) clean = clean.substring(0, room);
            super.replace(fb, offset, length, clean, a);
        }
    }

    /** Area pratinjau foto. Klik untuk memilih foto, atau seret file ke sini. */
    private class PhotoPreview extends JPanel {
        private BufferedImage source;
        private BufferedImage rendered;
        private int renderedW = -1;
        private int renderedH = -1;
        private boolean hover = false;

        PhotoPreview() {
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { choosePhoto(); }
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        void setPath(String path) {
            source = FieldVisuals.loadImage(path);
            rendered = null;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int arc = 20;

            if (source != null) {
                if (rendered == null || renderedW != w || renderedH != h) {
                    rendered = FieldVisuals.roundedCover(source, w, h, arc);
                    renderedW = w;
                    renderedH = h;
                }
                g2.drawImage(rendered, 0, 0, null);
                g2.setColor(Theme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, arc, arc));
            } else {
                g2.setColor(hover ? Theme.PRIMARY_LIGHT : new Color(243, 244, 246));
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc));
                g2.setColor(hover ? Theme.PRIMARY : new Color(156, 163, 175));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{6f, 5f}, 0));
                g2.draw(new RoundRectangle2D.Float(1, 1, w - 3, h - 3, arc, arc));

                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "📷";
                int cy = h / 2 - 14;
                g2.setColor(Theme.TEXT_SECONDARY);
                g2.drawString(icon, (w - fm.stringWidth(icon)) / 2, cy);

                g2.setFont(Theme.FONT_HEADING);
                fm = g2.getFontMetrics();
                String line1 = "Klik untuk memilih foto";
                g2.setColor(Theme.TEXT_PRIMARY);
                g2.drawString(line1, (w - fm.stringWidth(line1)) / 2, cy + 30);

                g2.setFont(Theme.FONT_SMALL);
                fm = g2.getFontMetrics();
                String line2 = "atau seret file gambar ke sini";
                g2.setColor(Theme.TEXT_SECONDARY);
                g2.drawString(line2, (w - fm.stringWidth(line2)) / 2, cy + 50);
            }
            g2.dispose();
        }
    }
}
