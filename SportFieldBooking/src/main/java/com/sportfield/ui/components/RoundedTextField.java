package com.sportfield.ui.components;

import com.sportfield.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextField extends JTextField {

    private String placeholder;
    private boolean focused = false;
    private final int radius;
    private boolean glassStyle = true;

    public RoundedTextField(String placeholder) {
        this(placeholder, 20);
    }

    public RoundedTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        this.radius = Theme.INPUT_RADIUS;
        setFont(Theme.FONT_BODY);
        setForeground(Theme.TEXT_PRIMARY);
        setCaretColor(Theme.TEXT_PRIMARY);
        setOpaque(false);
        setBorder(new EmptyBorder(12, 16, 12, 16));
        setPreferredSize(new Dimension(300, 48));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                repaint();
            }
            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public void setGlassStyle(boolean glass) {
        this.glassStyle = glass;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (glassStyle) {
            g2.setColor(Theme.GLASS_WHITE_40);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            
            g2.setColor(focused ? Theme.BORDER_FOCUS : Theme.BORDER_GLASS);
            g2.setStroke(new BasicStroke(focused ? 2f : 1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 2, getHeight() - 2, radius, radius));
        } else {
            g2.setColor(Theme.BG_WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));

            g2.setColor(focused ? Theme.BORDER_FOCUS : Theme.BORDER);
            g2.setStroke(new BasicStroke(focused ? 2f : 1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 2, getHeight() - 2, radius, radius));
        }

        g2.dispose();
        super.paintComponent(g);

        if (getText().isEmpty() && !focused) {
            Graphics2D gp = (Graphics2D) g.create();
            gp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gp.setColor(Theme.TEXT_SECONDARY);
            gp.setFont(Theme.FONT_BODY);
            Insets insets = getInsets();
            gp.drawString(placeholder, insets.left, getHeight() / 2 + gp.getFontMetrics().getAscent() / 2 - 2);
            gp.dispose();
        }
    }
}
