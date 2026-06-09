package model;

import util.AssetLoader;
import util.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents the player's spaceship.
 * Tracks position, lives, shield state, power-up timers, and invincibility frames.
 */
public class Player {

    // Position & size
    private int x, y;
    private final int width  = Constants.PLAYER_WIDTH;
    private final int height = Constants.PLAYER_HEIGHT;

    // State
    private int  lives;
    private int  score;
    private boolean moving;

    // Invincibility after being hit
    private long invincibleUntil = 0;

    // Power-ups
    private boolean shieldActive      = false;
    private long    shieldUntil       = 0;
    private boolean rapidFireActive   = false;
    private long    rapidFireUntil    = 0;

    // Shooting cooldown
    private long lastShotTime = 0;

    // Animation thrust toggle
    private boolean thrusting = false;

    public Player() {
        reset();
    }

    /** Full reset (new game). */
    public void reset() {
        x      = Constants.PLAYER_START_X;
        y      = Constants.PLAYER_START_Y;
        lives  = Constants.PLAYER_LIVES;
        score  = 0;
        invincibleUntil = 0;
        clearPowerUps();
    }

    /** Soft reset after losing a life (position only). */
    public void respawn() {
        x = Constants.PLAYER_START_X;
        y = Constants.PLAYER_START_Y;
        invincibleUntil = System.currentTimeMillis() + Constants.PLAYER_INVINCIBLE_MS;
        clearPowerUps();
    }

    public void update(boolean leftHeld, boolean rightHeld) {
        thrusting = leftHeld || rightHeld;
        if (leftHeld)  x -= Constants.PLAYER_SPEED;
        if (rightHeld) x += Constants.PLAYER_SPEED;

        // Clamp to window
        x = Math.max(0, Math.min(Constants.WINDOW_WIDTH - width, x));

        // Expire power-ups
        long now = System.currentTimeMillis();
        if (shieldActive    && now > shieldUntil)    shieldActive    = false;
        if (rapidFireActive && now > rapidFireUntil) rapidFireActive = false;
    }

    public void draw(Graphics2D g) {
        long now = System.currentTimeMillis();
        boolean invis = isInvincible();

        // Blink while invincible
        if (invis && (now / 100) % 2 == 0) return;

        BufferedImage img = thrusting ? AssetLoader.getPlayerThrust() : AssetLoader.getPlayer();
        g.drawImage(img, x, y, null);

        // Shield bubble
        if (shieldActive) {
            float alpha = 0.35f + 0.25f * (float) Math.sin(now / 150.0);
            g.setColor(new Color(0, 180, 255, (int)(alpha * 255)));
            g.setStroke(new BasicStroke(3f));
            g.drawOval(x - 10, y - 10, width + 20, height + 20);
            g.setColor(new Color(0, 180, 255, 40));
            g.fillOval(x - 10, y - 10, width + 20, height + 20);
        }

        // Power-up indicator bar
        if (rapidFireActive) {
            long remaining = rapidFireUntil - now;
            int barW = (int)(width * remaining / (double) Constants.POWERUP_DURATION_MS);
            g.setColor(new Color(0, 200, 255, 180));
            g.fillRect(x, y - 8, barW, 4);
        }
    }

    // ── Combat ───────────────────────────────────────────────────────────────

    public boolean canShoot() {
        long now = System.currentTimeMillis();
        long cd  = rapidFireActive ? 120 : Constants.PLAYER_SHOOT_COOLDOWN_MS;
        return now - lastShotTime >= cd;
    }

    public void recordShot() {
        lastShotTime = System.currentTimeMillis();
    }

    public boolean isInvincible() {
        return System.currentTimeMillis() < invincibleUntil;
    }

    public void hit() {
        if (shieldActive) {
            shieldActive = false; // shield absorbs hit
            return;
        }
        lives--;
        respawn();
    }

    public void addScore(int pts) { score += pts; }

    // ── Power-ups ─────────────────────────────────────────────────────────────

    public void activateShield() {
        shieldActive = true;
        shieldUntil  = System.currentTimeMillis() + Constants.POWERUP_DURATION_MS;
    }

    public void activateRapidFire() {
        rapidFireActive = true;
        rapidFireUntil  = System.currentTimeMillis() + Constants.POWERUP_DURATION_MS;
    }

    public void addLife() {
        lives = Math.min(lives + 1, 5);
    }

    private void clearPowerUps() {
        shieldActive    = false;
        rapidFireActive = false;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Rectangle getBounds() { return new Rectangle(x + 4, y + 4, width - 8, height - 6); }
    public int getX()     { return x; }
    public int getY()     { return y; }
    public int getWidth() { return width; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public boolean isAlive() { return lives > 0; }
    public boolean isShieldActive()    { return shieldActive; }
    public boolean isRapidFireActive() { return rapidFireActive; }
}
