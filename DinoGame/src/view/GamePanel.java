package view;

import engine.GameEngine;
import state.GameState;
import util.ScoreFormatter;

import javax.swing.*;
import java.awt.*;

/**
 * Main game rendering panel.
 * MVC: View — reads state from GameEngine (Model), renders it.
 * Uses double-buffering via JPanel + paintComponent.
 * Pattern: Observer — repaints triggered by GameLoop.
 */
public class GamePanel extends JPanel {

    private final GameEngine engine;

    // --- Achievement popup state ---
    private String achievementText = "";
    private int    achievementAlpha = 0;
    private int    achievementTimer = 0;

    // Pixel fonts (fallback to monospaced if not found)
    private Font  pixelFont;
    private Font  pixelFontSmall;
    private Font  hudFont;

    public GamePanel(GameEngine engine) {
        this.engine = engine;
        setPreferredSize(new Dimension(GameEngine.WIDTH, GameEngine.HEIGHT));
        setBackground(new Color(247, 243, 235));
        setDoubleBuffered(true);

        try {
            // Load Press Start 2P from classpath resources (optional)
            pixelFont      = new Font("Monospaced", Font.BOLD, 14);
            pixelFontSmall = new Font("Monospaced", Font.BOLD, 10);
            hudFont        = new Font("Monospaced", Font.PLAIN, 12);
        } catch (Exception e) {
            pixelFont      = new Font(Font.MONOSPACED, Font.BOLD, 14);
            pixelFontSmall = new Font(Font.MONOSPACED, Font.BOLD, 10);
            hudFont        = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        }

        engine.setAchievementCallback(name -> {
            achievementText  = "★ " + name.toUpperCase();
            achievementAlpha = 255;
            achievementTimer = 150;
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw world
        engine.draw(g2d);

        // Draw HUD overlays based on state
        GameState state = engine.getState();
        switch (state) {
            case MENU      -> drawMenu(g2d);
            case PLAYING   -> drawPlayingHUD(g2d);
            case PAUSED    -> { drawPlayingHUD(g2d); drawPauseOverlay(g2d); }
            case GAME_OVER -> { drawPlayingHUD(g2d); drawGameOverOverlay(g2d); }
        }

        drawAchievementPopup(g2d);
    }

    private void drawMenu(Graphics2D g2d) {
        // Title
        g2d.setFont(pixelFont.deriveFont(20f));
        drawCenteredString(g2d, "DINO RUN", GameEngine.HEIGHT / 2 - 35, Color.DARK_GRAY);
        g2d.setFont(pixelFontSmall);
        drawCenteredString(g2d, "PRESS SPACE OR CLICK TO START", GameEngine.HEIGHT / 2, new Color(100, 100, 100));
        g2d.setFont(hudFont);
        drawCenteredString(g2d, "SPACE/UP: JUMP   DOWN: DUCK   P: PAUSE",
                           GameEngine.HEIGHT / 2 + 22, new Color(150, 140, 130));
        // High score
        if (engine.getHighScore() > 0) {
            g2d.setFont(pixelFontSmall);
            drawCenteredString(g2d, "BEST: " + ScoreFormatter.format(engine.getHighScore()),
                               GameEngine.HEIGHT / 2 + 44, new Color(80, 80, 80));
        }
    }

    private void drawPlayingHUD(Graphics2D g2d) {
        // Score (top right)
        g2d.setFont(pixelFontSmall);
        g2d.setColor(new Color(100, 95, 90));
        String hiStr = "HI " + ScoreFormatter.format(engine.getHighScore());
        String scStr = ScoreFormatter.format(engine.getScore());
        int margin = 14;
        g2d.drawString(hiStr, GameEngine.WIDTH - margin - g2d.getFontMetrics().stringWidth(hiStr + "  " + scStr), 20);
        g2d.setColor(new Color(50, 45, 40));
        g2d.drawString(scStr, GameEngine.WIDTH - margin - g2d.getFontMetrics().stringWidth(scStr), 20);

        // Active power-up timers (bottom left)
        int yp = GameEngine.HEIGHT - 22;
        g2d.setFont(hudFont.deriveFont(9f));
        if (engine.isShieldActive()) {
            g2d.setColor(new Color(68, 170, 255));
            g2d.drawString("SHIELD " + (engine.getShieldTimer() / 60 + 1) + "s", 10, yp);
            yp -= 14;
        }
        if (engine.isSlowMode()) {
            g2d.setColor(new Color(255, 190, 50));
            g2d.drawString("SLOW " + (engine.getSlowTimer() / 60 + 1) + "s", 10, yp);
            yp -= 14;
        }
        if (engine.getMultiplier() > 1) {
            g2d.setColor(new Color(255, 100, 50));
            g2d.drawString("x2  " + (engine.getMultiplierTimer() / 60 + 1) + "s", 10, yp);
        }
    }

    private void drawPauseOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 90));
        g2d.fillRect(0, 0, GameEngine.WIDTH, GameEngine.HEIGHT);
        g2d.setFont(pixelFont.deriveFont(16f));
        drawCenteredString(g2d, "PAUSED", GameEngine.HEIGHT / 2 - 10, Color.WHITE);
        g2d.setFont(pixelFontSmall);
        drawCenteredString(g2d, "PRESS P TO RESUME", GameEngine.HEIGHT / 2 + 14, new Color(200, 200, 200));
    }

    private void drawGameOverOverlay(Graphics2D g2d) {
        g2d.setFont(pixelFont.deriveFont(15f));
        drawCenteredString(g2d, "GAME OVER", GameEngine.HEIGHT / 2 - 22, new Color(180, 50, 30));
        boolean newHi = engine.getScore() >= engine.getHighScore() && engine.getScore() > 0;
        if (newHi) {
            g2d.setFont(pixelFontSmall);
            drawCenteredString(g2d, "NEW HIGH SCORE!", GameEngine.HEIGHT / 2 - 2, new Color(255, 150, 0));
        }
        g2d.setFont(pixelFontSmall);
        drawCenteredString(g2d, "PRESS SPACE TO RESTART", GameEngine.HEIGHT / 2 + (newHi ? 18 : 4),
                           new Color(80, 80, 80));
    }

    /** Ticks the achievement popup alpha decay. Called by game loop. */
    public void tickAchievementPopup() {
        if (achievementTimer > 0) {
            achievementTimer--;
            if (achievementTimer < 40) achievementAlpha = (int)(achievementAlpha * 0.88f);
        }
    }

    private void drawAchievementPopup(Graphics2D g2d) {
        if (achievementAlpha < 10 || achievementText.isEmpty()) return;
        g2d.setFont(hudFont.deriveFont(Font.BOLD, 10f));
        FontMetrics fm = g2d.getFontMetrics();
        int tw = fm.stringWidth(achievementText);
        int px = GameEngine.WIDTH / 2 - tw / 2 - 12;
        int py = 32;
        int pw = tw + 24, ph = 22;
        g2d.setColor(new Color(40, 40, 40, achievementAlpha));
        g2d.fillRoundRect(px, py, pw, ph, 8, 8);
        g2d.setColor(new Color(255, 220, 80, achievementAlpha));
        g2d.drawString(achievementText, px + 12, py + ph - 6);
    }

    private void drawCenteredString(Graphics2D g2d, String text, int y, Color color) {
        g2d.setColor(color);
        FontMetrics fm = g2d.getFontMetrics();
        int x = (GameEngine.WIDTH - fm.stringWidth(text)) / 2;
        g2d.drawString(text, x, y);
    }
}
