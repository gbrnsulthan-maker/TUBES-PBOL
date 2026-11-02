package com.sportfield.ui.components;

import com.sportfield.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CardPanel extends JPanel {

    private final int radius;
    private final boolean drawShadow;
    private boolean isGlass = true;

    public CardPanel() {
        this(Theme.CARD_RADIUS, true);
    }

    public CardPanel(int radius, boolean drawShadow) {
        this.radius = radius;
        this.drawShadow = drawShadow;
        setOpaque(false);
        setBackground(Theme.BG_CARD);
        setLayout(new BorderLayout());
    }

    public void setGlassStyle(boolean glass) {
        this.isGlass = glass;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        if (isGlass) {
            drawGlassCard(g2);
        } else {
            drawSolidCard(g2);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private void drawGlassCard(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        
        if (drawShadow) {
            g2.setColor(new Color(0, 0, 0, 8));
            g2.fill(new RoundRectangle2D.Float(1, 2, w - 2, h - 3, radius, radius));
            g2.setColor(new Color(0, 0, 0, 5));
            g2.fill(new RoundRectangle2D.Float(2, 3, w - 4, h - 4, radius, radius));
        }

        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, radius, radius));

        g2.setColor(Theme.BORDER_GLASS);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1.5f, h - 1.5f, radius, radius));
    }

    private void drawSolidCard(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();

        if (drawShadow) {
            g2.setColor(Theme.SHADOW);
            g2.fill(new RoundRectangle2D.Float(2, 2, w - 2, h - 2, radius, radius));
        }

        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, radius, radius));

        g2.setColor(Theme.BORDER);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, radius, radius));
    }
}
