package com.sportfield.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class CardFanCarousel extends JPanel {

    private static final String[] TITLES = {
        "Lapangan Futsal", "Arena Indoor", "Premium Stadium", "Mini Soccer", "Court Komunitas"
    };
    private static final Color[] PITCH_COLORS = {
        new Color(51, 142, 82), new Color(44, 125, 92), new Color(61, 153, 89),
        new Color(65, 137, 72), new Color(40, 130, 77)
    };
    private int centerIndex = 2;

    public CardFanCarousel() {
        setOpaque(false);
        setPreferredSize(new Dimension(490, 355));
        setToolTipText("Klik sisi kiri atau kanan untuk melihat lapangan lainnya");
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getX() < getWidth() / 3) {
                    centerIndex = (centerIndex - 1 + TITLES.length) % TITLES.length;
                } else {
                    centerIndex = (centerIndex + 1) % TITLES.length;
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        double scale = Math.min(getWidth() / 490.0, getHeight() / 355.0);
        g2.translate((getWidth() - 490 * scale) / 2, (getHeight() - 355 * scale) / 2);
        g2.scale(scale, scale);

        for (int offset = -2; offset <= 2; offset++) {
            int cardIndex = Math.floorMod(centerIndex + offset, TITLES.length);
            drawCard(g2, cardIndex, offset);
        }

        drawNavigationHint(g2);
        g2.dispose();
    }

    private void drawCard(Graphics2D g, int cardIndex, int offset) {
        final int cardWidth = 150;
        final int cardHeight = 218;
        double centerX = 245 + offset * 61;
        double centerY = 167 + Math.abs(offset) * 5;
        double scale = offset == 0 ? 1.0 : 0.88 - Math.abs(offset) * 0.025;
        double rotation = Math.toRadians(offset * 7.5);

        Graphics2D card = (Graphics2D) g.create();
        card.translate(centerX, centerY);
        card.rotate(rotation);
        card.scale(scale, scale);
        card.translate(-cardWidth / 2.0, -cardHeight / 2.0);
        card.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, offset == 0 ? 1f : 0.93f
        ));

        card.setColor(new Color(0, 0, 0, offset == 0 ? 75 : 48));
        card.fill(new RoundRectangle2D.Float(3, 7, cardWidth, cardHeight, 19, 19));
        card.setColor(Color.WHITE);
        card.fill(new RoundRectangle2D.Float(0, 0, cardWidth, cardHeight, 18, 18));
        card.setClip(new RoundRectangle2D.Float(0, 0, cardWidth, cardHeight, 18, 18));

        drawPitch(card, cardIndex, cardWidth, cardHeight - 48);
        card.setColor(Color.WHITE);
        card.fillRect(0, cardHeight - 48, cardWidth, 48);

        card.setColor(new Color(31, 41, 55));
        card.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.drawString(TITLES[cardIndex], 12, cardHeight - 27);
        card.setColor(new Color(107, 114, 128));
        card.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        card.drawString("SPORTFIELD  •  BOOK NOW", 12, cardHeight - 11);

        card.dispose();
    }

    private void drawPitch(Graphics2D g, int cardIndex, int width, int height) {
        Color pitch = PITCH_COLORS[cardIndex];
        g.setPaint(new GradientPaint(0, 0, new Color(129, 188, 214), 0, height * 0.6f, new Color(222, 231, 205)));
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(35, 57, 55, 190));
        g.fillRect(0, 58, width, 27);
        for (int i = 0; i < 7; i++) {
            g.setColor(new Color(255, 246, 202, 180));
            g.fillOval(7 + i * 23, 18 + (i % 2) * 8, 4, 4);
        }

        g.setPaint(new GradientPaint(0, 80, pitch.brighter(), 0, height, pitch.darker()));
        g.fillRect(0, 80, width, height - 80);

        g.setColor(new Color(255, 255, 255, 45));
        for (int i = 0; i < 6; i++) {
            g.fillRect(i * 28, 80, 14, height - 80);
        }

        g.setColor(new Color(255, 255, 255, 190));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRect(10, 91, width - 20, height - 102);
        g.drawLine(width / 2, 91, width / 2, height - 11);
        g.drawOval(width / 2 - 17, height / 2 + 11, 34, 34);
        g.drawRect(10, 111, 28, 49);
        g.drawRect(width - 38, 111, 28, 49);
        g.drawRect(10, 122, 12, 27);
        g.drawRect(width - 22, 122, 12, 27);

        g.setColor(new Color(255, 255, 255, 220));
        g.fillOval(width / 2 - 3, height / 2 + 25, 6, 6);
    }

    private void drawNavigationHint(Graphics2D g) {
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g.setColor(new Color(235, 250, 239, 220));
        String text = "KLIK UNTUK LIHAT LAPANGAN LAIN";
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, (490 - metrics.stringWidth(text)) / 2, 335);
    }
}
