package com.sportfield.ui.components;

import com.sportfield.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {

    public enum Style { PRIMARY, SECONDARY, DANGER, SUCCESS, OUTLINE, GRADIENT }

    private Color bgColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color fgColor;
    private final int radius;
    private boolean hovered = false;
    private boolean pressed = false;
    private Style style;

    public RoundedButton(String text) {
        this(text, Style.PRIMARY);
    }

    public RoundedButton(String text, Style style) {
        super(text);
        this.style = style;
        this.radius = Theme.BUTTON_RADIUS;
        setFont(Theme.FONT_BUTTON);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(160, 44));

        applyStyle(style);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    private void applyStyle(Style style) {
        switch (style) {
            case PRIMARY:
                bgColor = Theme.PRIMARY;
                hoverColor = Theme.PRIMARY_HOVER;
                pressedColor = new Color(60, 95, 230);
                fgColor = Theme.TEXT_WHITE;
                break;
            case SECONDARY:
                bgColor = Theme.BG_CARD;
                hoverColor = Theme.BORDER;
                pressedColor = new Color(209, 213, 219);
                fgColor = Theme.TEXT_PRIMARY;
                break;
            case DANGER:
                bgColor = Theme.DANGER;
                hoverColor = new Color(220, 38, 38);
                pressedColor = new Color(185, 28, 28);
                fgColor = Theme.TEXT_WHITE;
                break;
            case SUCCESS:
                bgColor = Theme.SUCCESS;
                hoverColor = new Color(22, 163, 74);
                pressedColor = new Color(21, 128, 61);
                fgColor = Theme.TEXT_WHITE;
                break;
            case OUTLINE:
                bgColor = Theme.BG_WHITE;
                hoverColor = Theme.PRIMARY_LIGHT;
                pressedColor = new Color(191, 219, 254);
                fgColor = Theme.PRIMARY;
                break;
            case GRADIENT:
                bgColor = Theme.GRADIENT_BLUE;
                hoverColor = Theme.PRIMARY_HOVER;
                pressedColor = new Color(60, 95, 230);
                fgColor = Theme.TEXT_WHITE;
                break;
        }
        setForeground(fgColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Color bg;
        if (!isEnabled()) {
            bg = new Color(209, 213, 219);
            setForeground(Theme.TEXT_SECONDARY);
        } else if (pressed) {
            bg = pressedColor;
            setForeground(fgColor);
        } else if (hovered) {
            bg = hoverColor;
            setForeground(fgColor);
        } else {
            bg = bgColor;
            setForeground(fgColor);
        }

        if (style == Style.GRADIENT && isEnabled()) {
            GradientPaint gradient = new GradientPaint(
                0, 0, Theme.GRADIENT_BLUE,
                getWidth(), getHeight(), Theme.GRADIENT_CYAN
            );
            g2.setPaint(gradient);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
        } else {
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
        }

        if (style == Style.OUTLINE) {
            g2.setColor(Theme.PRIMARY);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, radius, radius));
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
