package com.sportfield.ui;

import java.awt.*;

public final class Theme {

    private Theme() {}

    // ── Glass Morphism Colors ──
    public static final Color GLASS_WHITE_20   = new Color(255, 255, 255, 51);    // 20% opacity
    public static final Color GLASS_WHITE_30   = new Color(255, 255, 255, 76);    // 30% opacity
    public static final Color GLASS_WHITE_40   = new Color(255, 255, 255, 102);   // 40% opacity
    public static final Color GLASS_DARK_10    = new Color(0, 0, 0, 25);          // 10% opacity dark
    
    // ── Gradient Colors ──
    public static final Color GRADIENT_BLUE    = new Color(74, 111, 255);         // #4A6FFF
    public static final Color GRADIENT_CYAN    = new Color(0, 201, 255);          // #00C9FF
    public static final Color GRADIENT_PURPLE  = new Color(156, 39, 176);         // #9C27B0

    // ── Warna Utama ──
    public static final Color PRIMARY         = new Color(74, 111, 255);          // Biru gradient #4A6FFF
    public static final Color PRIMARY_HOVER   = new Color(66, 103, 230);
    public static final Color PRIMARY_LIGHT   = new Color(219, 234, 254);
    public static final Color ACCENT          = new Color(0, 201, 255);           // Cyan #00C9FF

    // ── Background ──
    public static final Color BG_MAIN         = new Color(245, 247, 250);         // Putih kebiruan (#F5F7FA)
    public static final Color BG_WHITE        = Color.WHITE;
    public static final Color BG_SIDEBAR      = new Color(255, 255, 255, 20);     // Glass white 20%
    public static final Color BG_CARD         = new Color(255, 255, 255, 30);     // Glass white 30%
    public static final Color BG_OVERLAY      = new Color(0, 0, 0, 40);           // Dark overlay 40%

    // ── Teks ──
    public static final Color TEXT_PRIMARY    = new Color(17, 24, 39);            // Hitam/navy (#111827)
    public static final Color TEXT_SECONDARY  = new Color(107, 114, 128);         // Abu-abu (#6B7280)
    public static final Color TEXT_WHITE      = Color.WHITE;
    public static final Color TEXT_SIDEBAR    = new Color(107, 114, 128);
    public static final Color TEXT_SIDEBAR_ACTIVE = Color.WHITE;

    // ── Status ──
    public static final Color SUCCESS         = new Color(34, 197, 94);           // Hijau (#22C55E)
    public static final Color WARNING         = new Color(245, 158, 11);          // Kuning/oranye (#F59E0B)
    public static final Color DANGER          = new Color(239, 68, 68);           // Merah (#EF4444)
    public static final Color INFO            = new Color(59, 130, 246);          // Biru (#3B82F6)

    // ── Border ──
    public static final Color BORDER          = new Color(229, 231, 235);         // Abu-abu terang (#E5E7EB)
    public static final Color BORDER_GLASS    = new Color(255, 255, 255, 100);    // Glass border
    public static final Color BORDER_FOCUS    = PRIMARY;

    // ── Shadow ──
    public static final Color SHADOW          = new Color(0, 0, 0, 20);
    public static final Color SHADOW_GLASS    = new Color(0, 0, 0, 15);

    // ── Font ──
    public static final Font FONT_TITLE       = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_SUBTITLE    = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADING     = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY        = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL       = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SIDEBAR     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON      = new Font("Segoe UI", Font.BOLD, 14);

    // ── Dimensi ──
    public static final int SIDEBAR_WIDTH     = 240;
    public static final int CARD_RADIUS       = 24;           // Glass cards larger radius
    public static final int BUTTON_RADIUS     = 12;           // Rounder buttons
    public static final int INPUT_RADIUS      = 12;
    public static final int PADDING           = 24;
    public static final int PADDING_SM        = 12;
    public static final int PADDING_LG        = 32;

    // ── Dimensi Window ──
    public static final int WINDOW_WIDTH      = 1400;
    public static final int WINDOW_HEIGHT     = 900;
}
