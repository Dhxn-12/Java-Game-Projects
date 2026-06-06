

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton that manages all visual themes.
 * Themes: NEON, ARCADE, MINIMAL — each supports DARK and LIGHT variants.
 *
 * <p>OOP Principles: Singleton, Encapsulation.</p>
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class ThemeManager {

    private static ThemeManager instance;

    private String currentThemeName = "NEON";
    private boolean darkMode = true;
    private Theme   activeTheme;

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE     = loadFont(42f, Font.BOLD);
    public static final Font FONT_HEADING   = loadFont(22f, Font.BOLD);
    public static final Font FONT_BODY      = loadFont(15f, Font.PLAIN);
    public static final Font FONT_SYMBOL    = loadFont(52f, Font.BOLD);
    public static final Font FONT_BUTTON    = loadFont(15f, Font.BOLD);
    public static final Font FONT_SMALL     = loadFont(12f, Font.PLAIN);
    public static final Font FONT_MONO      = new Font("Monospaced", Font.BOLD, 13);

    private static Font loadFont(float size, int style) {
        // Try a modern system font, fall back to Dialog
        String[] candidates = {"Segoe UI", "SF Pro Display", "Helvetica Neue", "Ubuntu", "Dialog"};
        for (String name : candidates) {
            Font f = new Font(name, style, (int) size);
            if (!f.getFamily().equals("Dialog") || name.equals("Dialog")) return f;
        }
        return new Font("Dialog", style, (int) size);
    }

    // ── Singleton ─────────────────────────────────────────────────────────────

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        if (instance == null) instance = new ThemeManager();
        return instance;
    }

    // ── Theme Loading ─────────────────────────────────────────────────────────

    public void loadTheme(String themeName) {
        this.currentThemeName = themeName;
        buildActiveTheme();
    }

    public void toggleDarkMode() {
        darkMode = !darkMode;
        buildActiveTheme();
    }

    public void setDarkMode(boolean dark) {
        this.darkMode = dark;
        buildActiveTheme();
    }

    private void buildActiveTheme() {
        activeTheme = switch (currentThemeName.toUpperCase()) {
            case "ARCADE"  -> darkMode ? buildArcadeDark()  : buildArcadeLight();
            case "MINIMAL" -> darkMode ? buildMinimalDark() : buildMinimalLight();
            default        -> darkMode ? buildNeonDark()    : buildNeonLight();
        };
    }

    // ── Theme Definitions ─────────────────────────────────────────────────────

    private Theme buildNeonDark() {
        Theme t = new Theme();
        t.name             = "Neon Dark";
        t.background       = new Color(10, 10, 20);
        t.backgroundAlt    = new Color(18, 18, 35);
        t.surface          = new Color(25, 25, 50);
        t.surfaceHover     = new Color(35, 35, 70);
        t.accent           = new Color(0, 255, 200);
        t.accentSecondary  = new Color(180, 0, 255);
        t.textPrimary      = new Color(220, 220, 255);
        t.textSecondary    = new Color(130, 130, 180);
        t.symbolX          = new Color(255, 80, 100);
        t.symbolO          = new Color(0, 230, 200);
        t.winHighlight     = new Color(255, 220, 0);
        t.buttonBg         = new Color(30, 30, 60);
        t.buttonHover      = new Color(0, 255, 200, 50);
        t.buttonBorder     = new Color(0, 255, 200);
        t.dialogBg         = new Color(15, 15, 35);
        t.gradientStart    = new Color(10, 10, 30);
        t.gradientEnd      = new Color(5, 5, 20);
        t.gridColor        = new Color(0, 255, 200, 60);
        t.cellBg           = new Color(20, 20, 45);
        t.cellHover        = new Color(0, 255, 200, 20);
        t.timerWarning     = new Color(255, 150, 0);
        t.timerDanger      = new Color(255, 50, 50);
        t.scanlineOpacity  = 0.04f;
        return t;
    }

    private Theme buildNeonLight() {
        Theme t = new Theme();
        t.name             = "Neon Light";
        t.background       = new Color(240, 242, 255);
        t.backgroundAlt    = new Color(230, 233, 255);
        t.surface          = new Color(255, 255, 255);
        t.surfaceHover     = new Color(245, 245, 255);
        t.accent           = new Color(0, 180, 160);
        t.accentSecondary  = new Color(130, 0, 200);
        t.textPrimary      = new Color(30, 30, 60);
        t.textSecondary    = new Color(100, 100, 140);
        t.symbolX          = new Color(220, 40, 70);
        t.symbolO          = new Color(0, 160, 150);
        t.winHighlight     = new Color(200, 160, 0);
        t.buttonBg         = new Color(245, 246, 255);
        t.buttonHover      = new Color(0, 180, 160, 30);
        t.buttonBorder     = new Color(0, 180, 160);
        t.dialogBg         = new Color(255, 255, 255);
        t.gradientStart    = new Color(235, 238, 255);
        t.gradientEnd      = new Color(220, 225, 255);
        t.gridColor        = new Color(0, 180, 160, 80);
        t.cellBg           = new Color(248, 249, 255);
        t.cellHover        = new Color(0, 180, 160, 15);
        t.timerWarning     = new Color(200, 120, 0);
        t.timerDanger      = new Color(200, 30, 30);
        t.scanlineOpacity  = 0.0f;
        return t;
    }

    private Theme buildArcadeDark() {
        Theme t = new Theme();
        t.name             = "Arcade Dark";
        t.background       = new Color(5, 0, 15);
        t.backgroundAlt    = new Color(15, 5, 30);
        t.surface          = new Color(20, 10, 40);
        t.surfaceHover     = new Color(30, 15, 55);
        t.accent           = new Color(255, 220, 0);
        t.accentSecondary  = new Color(255, 100, 0);
        t.textPrimary      = new Color(255, 255, 200);
        t.textSecondary    = new Color(180, 170, 120);
        t.symbolX          = new Color(255, 80, 50);
        t.symbolO          = new Color(100, 200, 255);
        t.winHighlight     = new Color(255, 220, 0);
        t.buttonBg         = new Color(20, 10, 45);
        t.buttonHover      = new Color(255, 220, 0, 40);
        t.buttonBorder     = new Color(255, 220, 0);
        t.dialogBg         = new Color(10, 5, 25);
        t.gradientStart    = new Color(8, 0, 20);
        t.gradientEnd      = new Color(2, 0, 10);
        t.gridColor        = new Color(255, 100, 0, 70);
        t.cellBg           = new Color(15, 8, 30);
        t.cellHover        = new Color(255, 220, 0, 20);
        t.timerWarning     = new Color(255, 150, 0);
        t.timerDanger      = new Color(255, 0, 0);
        t.scanlineOpacity  = 0.06f;
        return t;
    }

    private Theme buildArcadeLight() {
        Theme t = buildArcadeDark();
        t.name = "Arcade Light";
        t.background    = new Color(255, 252, 230);
        t.backgroundAlt = new Color(255, 248, 210);
        t.surface       = new Color(255, 255, 245);
        t.textPrimary   = new Color(40, 20, 80);
        t.textSecondary = new Color(100, 80, 40);
        t.cellBg        = new Color(255, 250, 230);
        t.dialogBg      = new Color(255, 255, 250);
        t.scanlineOpacity = 0.0f;
        return t;
    }

    private Theme buildMinimalDark() {
        Theme t = new Theme();
        t.name             = "Minimal Dark";
        t.background       = new Color(18, 18, 18);
        t.backgroundAlt    = new Color(24, 24, 24);
        t.surface          = new Color(30, 30, 30);
        t.surfaceHover     = new Color(40, 40, 40);
        t.accent           = new Color(255, 255, 255);
        t.accentSecondary  = new Color(160, 160, 160);
        t.textPrimary      = new Color(240, 240, 240);
        t.textSecondary    = new Color(140, 140, 140);
        t.symbolX          = new Color(255, 255, 255);
        t.symbolO          = new Color(160, 160, 160);
        t.winHighlight     = new Color(255, 200, 0);
        t.buttonBg         = new Color(35, 35, 35);
        t.buttonHover      = new Color(50, 50, 50);
        t.buttonBorder     = new Color(80, 80, 80);
        t.dialogBg         = new Color(22, 22, 22);
        t.gradientStart    = new Color(20, 20, 20);
        t.gradientEnd      = new Color(15, 15, 15);
        t.gridColor        = new Color(70, 70, 70);
        t.cellBg           = new Color(26, 26, 26);
        t.cellHover        = new Color(45, 45, 45);
        t.timerWarning     = new Color(220, 180, 0);
        t.timerDanger      = new Color(220, 60, 60);
        t.scanlineOpacity  = 0.0f;
        return t;
    }

    private Theme buildMinimalLight() {
        Theme t = new Theme();
        t.name             = "Minimal Light";
        t.background       = new Color(252, 252, 252);
        t.backgroundAlt    = new Color(246, 246, 246);
        t.surface          = new Color(255, 255, 255);
        t.surfaceHover     = new Color(248, 248, 248);
        t.accent           = new Color(20, 20, 20);
        t.accentSecondary  = new Color(100, 100, 100);
        t.textPrimary      = new Color(20, 20, 20);
        t.textSecondary    = new Color(100, 100, 100);
        t.symbolX          = new Color(20, 20, 20);
        t.symbolO          = new Color(100, 100, 100);
        t.winHighlight     = new Color(200, 160, 0);
        t.buttonBg         = new Color(250, 250, 250);
        t.buttonHover      = new Color(240, 240, 240);
        t.buttonBorder     = new Color(180, 180, 180);
        t.dialogBg         = new Color(255, 255, 255);
        t.gradientStart    = new Color(252, 252, 252);
        t.gradientEnd      = new Color(240, 240, 240);
        t.gridColor        = new Color(180, 180, 180);
        t.cellBg           = new Color(255, 255, 255);
        t.cellHover        = new Color(240, 240, 240);
        t.timerWarning     = new Color(180, 130, 0);
        t.timerDanger      = new Color(180, 40, 40);
        t.scanlineOpacity  = 0.0f;
        return t;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public Theme getTheme()                { return activeTheme; }
    public boolean isDarkMode()            { return darkMode; }
    public String  getCurrentThemeName()   { return currentThemeName; }

    // ── Theme Data Class ──────────────────────────────────────────────────────

    /**
     * Value object holding all color tokens for a theme.
     */
    public static class Theme {
        public String name;
        public Color background, backgroundAlt, surface, surfaceHover;
        public Color accent, accentSecondary;
        public Color textPrimary, textSecondary;
        public Color symbolX, symbolO;
        public Color winHighlight;
        public Color buttonBg, buttonHover, buttonBorder;
        public Color dialogBg;
        public Color gradientStart, gradientEnd;
        public Color gridColor;
        public Color cellBg, cellHover;
        public Color timerWarning, timerDanger;
        public float scanlineOpacity;

        public Color withAlpha(Color c, int alpha) {
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
        }
    }
}