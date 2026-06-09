package util;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * AssetLoader generates all game sprites programmatically using Java2D.
 * No external image files are required — everything is drawn in code.
 * Uses a simple cache so each image is created only once.
 */
public final class AssetLoader {

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    private AssetLoader() {}

    // ── Public API ───────────────────────────────────────────────────────────

    public static BufferedImage getPlayer() {
        return cache.computeIfAbsent("player", k -> drawPlayer());
    }

    public static BufferedImage getPlayerThrust() {
        return cache.computeIfAbsent("player_thrust", k -> drawPlayerThrust());
    }

    public static BufferedImage getEnemy(int row, int frame) {
        String key = "enemy_" + row + "_" + frame;
        return cache.computeIfAbsent(key, k -> drawEnemy(row, frame));
    }

    public static BufferedImage getBoss(int frame) {
        String key = "boss_" + frame;
        return cache.computeIfAbsent(key, k -> drawBoss(frame));
    }

    public static BufferedImage getBullet(boolean playerBullet) {
        String key = playerBullet ? "pbullet" : "ebullet";
        return cache.computeIfAbsent(key, k -> drawBullet(playerBullet));
    }

    public static BufferedImage getPowerUp(int type) {
        String key = "powerup_" + type;
        return cache.computeIfAbsent(key, k -> drawPowerUp(type));
    }

    public static BufferedImage getBarrierBlock() {
        return cache.computeIfAbsent("barrier", k -> drawBarrierBlock());
    }

    // ── Drawing Methods ──────────────────────────────────────────────────────

    private static BufferedImage drawPlayer() {
        int w = Constants.PLAYER_WIDTH, h = Constants.PLAYER_HEIGHT;
        BufferedImage img = createTransparent(w, h);
        Graphics2D g = img.createGraphics();
        applyHints(g);

        Color hull  = new Color(0, 230, 180);
        Color light = new Color(100, 255, 220);
        Color dark  = new Color(0, 140, 110);
        Color cockpit = new Color(0, 180, 255, 200);

        // Main body
        int[] xb = {w/2, w-4, w-10, 10, 4};
        int[] yb = {0,   h-8, h,    h,  h-8};
        g.setColor(hull);
        g.fillPolygon(xb, yb, 5);

        // Wing left
        g.setColor(dark);
        g.fillPolygon(new int[]{4, 0, 10, 14}, new int[]{h-8, h, h, h-16}, 4);
        // Wing right
        g.fillPolygon(new int[]{w-4, w, w-10, w-14}, new int[]{h-8, h, h, h-16}, 4);

        // Highlight on body
        g.setColor(light);
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(w/2, 2, w-8, h-9);
        g.drawLine(w/2, 2, 8, h-9);

        // Cannon
        g.setColor(light);
        g.fillRoundRect(w/2-3, 0, 6, 12, 3, 3);

        // Cockpit
        g.setColor(cockpit);
        g.fillOval(w/2-7, h/2-4, 14, 12);

        g.dispose();
        return img;
    }

    private static BufferedImage drawPlayerThrust() {
        // Same as player but with flame at bottom
        BufferedImage base = drawPlayer();
        int w = Constants.PLAYER_WIDTH, h = Constants.PLAYER_HEIGHT;
        BufferedImage img = createTransparent(w, h + 16);
        Graphics2D g = img.createGraphics();
        applyHints(g);
        // Draw flame
        GradientPaint flame = new GradientPaint(w/2, h, new Color(255,120,0,220),
                                                 w/2, h+14, new Color(255,255,100,0));
        g.setPaint(flame);
        g.fillOval(w/2 - 8, h-2, 16, 18);
        // Draw ship on top
        g.drawImage(base, 0, 0, null);
        g.dispose();
        return img;
    }

    private static BufferedImage drawEnemy(int row, int frame) {
        int w = Constants.ENEMY_WIDTH, h = Constants.ENEMY_HEIGHT;
        BufferedImage img = createTransparent(w, h);
        Graphics2D g = img.createGraphics();
        applyHints(g);

        Color[] colors = {
            new Color(255, 80,  80),   // row 0 – red
            new Color(255, 165, 0),    // row 1 – orange
            new Color(220, 220, 0),    // row 2 – yellow
            new Color(80,  220, 80),   // row 3 – green
            new Color(80,  180, 255),  // row 4 – blue
        };
        Color c = colors[Math.min(row, colors.length - 1)];
        Color bright = c.brighter();
        Color dark   = c.darker();

        int cx = w / 2;

        if (row <= 1) {
            // Crab-style enemy
            // Body
            g.setColor(c);
            g.fillRoundRect(6, 6, w-12, h-10, 8, 8);
            // Eyes
            g.setColor(bright);
            g.fillOval(cx-8, 8, 7, 7);
            g.fillOval(cx+1, 8, 7, 7);
            g.setColor(Color.BLACK);
            g.fillOval(cx-6, 9, 4, 4);
            g.fillOval(cx+3, 9, 4, 4);
            // Legs (animated by frame)
            g.setColor(dark);
            int legOff = (frame == 0) ? 3 : 0;
            g.fillRect(2,  h-14+legOff, 4, 8);
            g.fillRect(w-6, h-14+legOff, 4, 8);
            g.fillRect(8,  h-10+legOff, 4, 6);
            g.fillRect(w-12, h-10+legOff, 4, 6);
            // Antennae
            g.setColor(bright);
            g.setStroke(new BasicStroke(2f));
            g.drawLine(cx-4, 6, cx-8, 0);
            g.drawLine(cx+4, 6, cx+8, 0);
        } else if (row <= 3) {
            // Squid-style enemy
            g.setColor(c);
            g.fillOval(6, 4, w-12, h-10);
            // Tentacles
            g.setColor(dark);
            int tOff = (frame == 0) ? 2 : -2;
            g.fillRect(8,  h-10, 5, 8+tOff);
            g.fillRect(cx-2, h-10, 5, 8-tOff);
            g.fillRect(w-13, h-10, 5, 8+tOff);
            // Single large eye
            g.setColor(bright);
            g.fillOval(cx-6, 8, 12, 10);
            g.setColor(Color.BLACK);
            g.fillOval(cx-3, 10, 6, 6);
        } else {
            // UFO-style top row
            g.setColor(c);
            g.fillOval(4, 8, w-8, h-12);
            g.setColor(bright);
            g.fillOval(cx-7, 4, 14, 10); // dome
            g.setColor(dark);
            // Lights
            int lOff = (frame == 0) ? 0 : 2;
            for (int i = 0; i < 3; i++) {
                g.setColor(i % 2 == lOff % 2 ? Color.WHITE : dark);
                g.fillOval(8 + i*10, 12, 5, 5);
            }
        }

        g.dispose();
        return img;
    }

    private static BufferedImage drawBoss(int frame) {
        int w = Constants.BOSS_WIDTH, h = Constants.BOSS_HEIGHT;
        BufferedImage img = createTransparent(w, h);
        Graphics2D g = img.createGraphics();
        applyHints(g);

        Color body    = new Color(180, 0, 220);
        Color bright  = new Color(240, 100, 255);
        Color engine  = new Color(255, 200, 0);

        // Saucer body
        g.setColor(body);
        g.fillOval(4, h/2-6, w-8, h-6);
        // Dome
        g.setColor(bright);
        g.fillOval(w/2-12, 2, 24, 16);
        // Portholes (animated)
        int pOff = (frame % 4) * 5;
        g.setColor(engine);
        for (int i = 0; i < 4; i++) {
            int px = 10 + ((i * 14 + pOff) % (w - 20));
            g.fillOval(px, h/2+2, 7, 7);
        }
        // Outline
        g.setColor(bright);
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(4, h/2-6, w-8, h-6);

        g.dispose();
        return img;
    }

    private static BufferedImage drawBullet(boolean player) {
        int w = player ? Constants.PLAYER_BULLET_WIDTH  : Constants.ENEMY_BULLET_WIDTH;
        int h = player ? Constants.PLAYER_BULLET_HEIGHT : Constants.ENEMY_BULLET_HEIGHT;
        BufferedImage img = createTransparent(w, h);
        Graphics2D g = img.createGraphics();
        applyHints(g);

        if (player) {
            GradientPaint gp = new GradientPaint(0, 0, new Color(150, 255, 255),
                                                  0, h, new Color(0, 200, 255, 80));
            g.setPaint(gp);
            g.fillRoundRect(0, 0, w, h, w, w);
            g.setColor(Color.WHITE);
            g.fillRect(w/2-1, 0, 2, 4);
        } else {
            GradientPaint gp = new GradientPaint(0, 0, new Color(255, 80, 80),
                                                  0, h, new Color(255, 0, 0, 80));
            g.setPaint(gp);
            // Zigzag enemy bullet
            int[] xs = {0, w, 0, w};
            int[] ys = {0, h/3, 2*h/3, h};
            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawPolyline(xs, ys, 4);
        }

        g.dispose();
        return img;
    }

    private static BufferedImage drawPowerUp(int type) {
        int s = Constants.POWERUP_WIDTH;
        BufferedImage img = createTransparent(s, s);
        Graphics2D g = img.createGraphics();
        applyHints(g);

        Color[] colors = {
            new Color(0,   200, 255),  // 0 = rapid fire (blue)
            new Color(0,   255, 100),  // 1 = shield     (green)
            new Color(255, 200, 0),    // 2 = extra life  (gold)
        };
        String[] labels = {"R", "S", "♥"};

        Color c = colors[type % colors.length];
        // Hex shape
        g.setColor(c.darker());
        g.fillOval(2, 2, s-4, s-4);
        g.setColor(c);
        g.fillOval(4, 4, s-8, s-8);
        // Label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        String lbl = labels[type % labels.length];
        g.drawString(lbl, (s - fm.stringWidth(lbl)) / 2, (s + fm.getAscent()) / 2 - 2);
        // Glow border
        g.setColor(c.brighter());
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(3, 3, s-6, s-6);

        g.dispose();
        return img;
    }

    private static BufferedImage drawBarrierBlock() {
        int s = 8; // each block is 8x8
        BufferedImage img = createTransparent(s, s);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0, 220, 80));
        g.fillRect(0, 0, s, s);
        g.setColor(new Color(0, 150, 50));
        g.drawRect(0, 0, s-1, s-1);
        g.dispose();
        return img;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static BufferedImage createTransparent(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    private static void applyHints(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,      RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}
