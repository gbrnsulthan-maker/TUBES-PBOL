package com.sportfield.ui.components;

import com.sportfield.ui.Theme;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper visual untuk lapangan: warna dan emoji per jenis olahraga,
 * pemuat foto, thumbnail, serta styling combo box yang dipakai
 * ManageFieldPanel dan AddFieldPanel.
 */
public final class FieldVisuals {

    private static final Map<String, Icon> ICON_CACHE = new HashMap<>();

    private FieldVisuals() {}

    // ── Warna & emoji per jenis (sama dengan FieldListPanel) ──

    public static Color colorFor(String type) {
        if (type == null) return Theme.PRIMARY;
        switch (type.toLowerCase()) {
            case "badminton":  return new Color(59, 130, 246);
            case "futsal":     return new Color(34, 197, 94);
            case "basketball": return new Color(249, 115, 22);
            case "tennis":     return new Color(139, 92, 246);
            default:           return Theme.PRIMARY;
        }
    }

    public static String emojiFor(String type) {
        if (type == null) return "🏟️";
        switch (type.toLowerCase()) {
            case "badminton":  return "🏸";
            case "futsal":     return "⚽";
            case "basketball": return "🏀";
            case "tennis":     return "🎾";
            default:           return "🏟️";
        }
    }

    /** Mengubah "BADMINTON" menjadi "Badminton". */
    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    // ── Foto ──

    /** Membaca gambar dari path. Mengembalikan null jika path kosong atau file tidak valid. */
    public static BufferedImage loadImage(String path) {
        if (path == null || path.isEmpty()) return null;
        try {
            File f = new File(path);
            if (f.isFile()) return ImageIO.read(f);
        } catch (IOException ignored) {
            // file rusak: dianggap tidak ada foto
        }
        return null;
    }

    /**
     * Memotong gambar agar memenuhi ukuran w x h (mode "cover") dengan sudut membulat.
     */
    public static BufferedImage roundedCover(BufferedImage src, int w, int h, int arc) {
        BufferedImage out = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
        g.setComposite(AlphaComposite.SrcIn);

        double scale = Math.max((double) w / src.getWidth(), (double) h / src.getHeight());
        int dw = (int) Math.ceil(src.getWidth() * scale);
        int dh = (int) Math.ceil(src.getHeight() * scale);
        Image scaled = src.getScaledInstance(dw, dh, Image.SCALE_SMOOTH);
        g.drawImage(scaled, (w - dw) / 2, (h - dh) / 2, null);
        g.dispose();
        return out;
    }

    /**
     * Thumbnail lapangan. Memakai foto jika ada, jika tidak memakai blok warna
     * dengan emoji sesuai jenis olahraga.
     */
    public static Icon thumbnail(String path, String type, int w, int h, int arc) {
        String key = path + "|" + type + "|" + w + "x" + h;
        Icon cached = ICON_CACHE.get(key);
        if (cached != null) return cached;

        BufferedImage photo = loadImage(path);
        BufferedImage img;
        if (photo != null) {
            img = roundedCover(photo, w, h, arc);
        } else {
            img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(colorFor(type));
            g.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, Math.round(h * 0.5f)));
            FontMetrics fm = g.getFontMetrics();
            String emoji = emojiFor(type);
            g.setColor(Color.WHITE);
            g.drawString(emoji, (w - fm.stringWidth(emoji)) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());
            g.dispose();
        }
        Icon icon = new ImageIcon(img);
        ICON_CACHE.put(key, icon);
        return icon;
    }

    // ── Combo box ──

    /** Gaya combo box yang sama dengan filter di FieldListPanel. */
    public static void styleCombo(JComboBox<String> combo) {
        combo.setFont(Theme.FONT_BODY);
        combo.setBackground(Theme.BG_WHITE);
        combo.setForeground(Theme.TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        combo.setRenderer(new DefaultListCellRenderer() {
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
    }
}
