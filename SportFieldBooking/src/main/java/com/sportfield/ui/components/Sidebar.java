package com.sportfield.ui.components;

import com.sportfield.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Sidebar extends JPanel {

    private final List<NavItem> items = new ArrayList<>();
    private int selectedIndex = 0;

    public Sidebar() {
        setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));
        setBackground(Theme.BG_SIDEBAR);
        setLayout(new BorderLayout());
        setOpaque(false);
    }

    public void build(String role, String userName, java.util.function.IntConsumer onNavigate, Runnable onLogout) {
        removeAll();

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(Theme.SIDEBAR_WIDTH, 56));

        JLabel logoIcon = new JLabel("⚽");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel logoText = new JLabel("SportField");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoText.setForeground(Theme.PRIMARY);

        logoPanel.add(logoIcon);
        logoPanel.add(logoText);
        topPanel.add(logoPanel);
        topPanel.add(Box.createVerticalStrut(12));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 14, 0, 14));
        searchPanel.setMaximumSize(new Dimension(Theme.SIDEBAR_WIDTH, 44));

        JTextField searchField = new JTextField("  🔍  Cari menu...");
        searchField.setFont(Theme.FONT_SMALL);
        searchField.setForeground(Theme.TEXT_SECONDARY);
        searchField.setBackground(Theme.GLASS_WHITE_30);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER_GLASS, 1, true),
            new EmptyBorder(8, 8, 8, 8)
        ));
        searchField.setEditable(false);
        searchPanel.add(searchField);
        topPanel.add(searchPanel);
        topPanel.add(Box.createVerticalStrut(20));

        JLabel menuLabel = new JLabel("   MENU UTAMA");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        menuLabel.setForeground(Theme.TEXT_SECONDARY);
        menuLabel.setMaximumSize(new Dimension(Theme.SIDEBAR_WIDTH, 20));
        topPanel.add(menuLabel);
        topPanel.add(Box.createVerticalStrut(8));

        items.clear();
        String[][] menuItems;
        if (role.equals("ADMIN")) {
            menuItems = new String[][] {
                {"📊", "Dashboard"},
                {"🏟", "Kelola Lapangan"},
                {"📅", "Kelola Jadwal"},
                {"📋", "Reservasi"},
                {"📈", "Laporan"},
            };
        } else {
            menuItems = new String[][] {
                {"🏠", "Dashboard"},
                {"🏟", "Lapangan"},
                {"🔍", "Cari Jadwal"},
                {"📝", "Reservasi Saya"},
                {"📜", "Riwayat"},
            };
        }

        for (int i = 0; i < menuItems.length; i++) {
            NavItem item = new NavItem(menuItems[i][0], menuItems[i][1]);
            final int idx = i;
            item.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setSelected(idx);
                    onNavigate.accept(idx);
                }
            });
            items.add(item);
            topPanel.add(item);
        }

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_GLASS);
        sep.setMaximumSize(new Dimension(Theme.SIDEBAR_WIDTH, 1));
        bottomPanel.add(sep);
        bottomPanel.add(Box.createVerticalStrut(12));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        userPanel.setOpaque(false);
        userPanel.setMaximumSize(new Dimension(Theme.SIDEBAR_WIDTH, 52));

        JLabel avatar = createAvatarLabel(userName);
        userPanel.add(avatar);

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);

        JLabel nameLabel = new JLabel(userName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel roleLabel = new JLabel(role.equals("ADMIN") ? "Administrator" : "Pelanggan");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(Theme.TEXT_SECONDARY);

        userInfo.add(nameLabel);
        userInfo.add(roleLabel);
        userPanel.add(userInfo);
        bottomPanel.add(userPanel);
        bottomPanel.add(Box.createVerticalStrut(8));

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 8));
        logoutPanel.setOpaque(false);
        logoutPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutPanel.setMaximumSize(new Dimension(Theme.SIDEBAR_WIDTH, 40));

        JLabel logoutIcon = new JLabel("🚪");
        logoutIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        JLabel logoutLabel = new JLabel("Keluar");
        logoutLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutLabel.setForeground(Theme.DANGER);

        logoutPanel.add(logoutIcon);
        logoutPanel.add(logoutLabel);

        logoutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onLogout != null) onLogout.run();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutPanel.setOpaque(true);
                logoutPanel.setBackground(new Color(254, 226, 226));
                logoutPanel.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutPanel.setOpaque(false);
                logoutPanel.repaint();
            }
        });

        bottomPanel.add(logoutPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        setSelected(0);
        revalidate();
        repaint();
    }

    private JLabel createAvatarLabel(String name) {
        String initial = name.isEmpty() ? "U" : name.substring(0, 1).toUpperCase();
        JLabel avatar = new JLabel(initial, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRIMARY_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(Theme.PRIMARY);
        avatar.setPreferredSize(new Dimension(36, 36));
        return avatar;
    }

    public void setSelected(int index) {
        this.selectedIndex = index;
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setActive(i == index);
        }
    }

    // ── Inner class: NavItem ──
    private static class NavItem extends JPanel {
        private final JLabel textLabel;
        private boolean active = false;

        NavItem(String icon, String text) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Theme.SIDEBAR_WIDTH, 44));
            setBorder(new EmptyBorder(0, 10, 0, 10));

            JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 9));
            inner.setOpaque(false);

            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));

            textLabel = new JLabel(text);
            textLabel.setFont(Theme.FONT_SIDEBAR);
            textLabel.setForeground(Theme.TEXT_SECONDARY);

            inner.add(iconLabel);
            inner.add(textLabel);
            add(inner, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!active) {
                        inner.setOpaque(true);
                        inner.setBackground(Theme.BG_MAIN);
                        inner.repaint();
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!active) {
                        inner.setOpaque(false);
                        inner.repaint();
                    }
                }
            });
        }

        void setActive(boolean active) {
            this.active = active;
            JPanel inner = (JPanel) getComponent(0);
            if (active) {
                inner.setOpaque(true);
                inner.setBackground(Theme.PRIMARY_LIGHT);
                textLabel.setForeground(Theme.PRIMARY);
                textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                inner.setOpaque(false);
                textLabel.setForeground(Theme.TEXT_SECONDARY);
                textLabel.setFont(Theme.FONT_SIDEBAR);
            }
            repaint();
        }
    }
}
