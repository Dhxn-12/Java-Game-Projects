package model;

import java.awt.*;

/**
 * Model class for the Dinosaur player character.
 *
 * OOP: Encapsulation of all dino state (position, velocity, status).
 * Physics: Simple gravity-based jump system with double-jump support.
 * DSA: jump counter acts as a simple integer stack depth check.
 */
public class Dino {

    // --- Constants ---
    public static final int START_X = 80;
    public static final int DINO_W = 44;
    public static final int DINO_H = 50;
    public static final int DUCK_H = 30;
    private static final float GRAVITY = 0.7f;
    private static final float JUMP_FORCE = -13f;
    private static final int MAX_JUMPS = 2;

    // --- State ---
    private float x, y;
    private float vy;
    private boolean onGround;
    private boolean ducking;
    private int jumpCount;
    private boolean dead;
    private int invincibleFrames;

    // --- Animation ---
    private int animFrame;
    private int animTimer;

    // --- Ground Y (set by engine) ---
    private final int groundY;

    public Dino(int groundY) {
        this.groundY = groundY;
        reset();
    }

    /** Resets dino to initial state at game start. */
    public void reset() {
        this.x = START_X;
        this.y = groundY - DINO_H;
        this.vy = 0;
        this.onGround = true;
        this.ducking = false;
        this.jumpCount = 0;
        this.dead = false;
        this.invincibleFrames = 0;
        this.animFrame = 0;
        this.animTimer = 0;
    }

    /**
     * Triggers a jump if jump quota not exhausted.
     * DSA: jumpCount is bounded like a stack of depth MAX_JUMPS.
     * @return true if jump was performed
     */
    public boolean jump() {
        if (jumpCount < MAX_JUMPS && !dead) {
            vy = JUMP_FORCE;
            onGround = false;
            jumpCount++;
            return true;
        }
        return false;
    }

    /** Enables or disables ducking. Adjusts hitbox height. */
    public void setDucking(boolean duck) {
        this.ducking = duck;
        if (!duck && y > groundY - DINO_H) {
            y = groundY - DINO_H;
        }
    }

    /**
     * Updates position, gravity, animation.
     * Called every frame by the game loop.
     * @param slowMode whether slow-motion power-up is active
     */
    public void update(boolean slowMode) {
        if (dead) return;
        if (invincibleFrames > 0) invincibleFrames--;

        float g = slowMode ? GRAVITY * 0.45f : GRAVITY;
        if (!onGround) {
            vy += g;
            y += vy;
        }

        int floorY = groundY - (ducking ? DUCK_H : DINO_H);
        if (y >= floorY) {
            y = floorY;
            vy = 0;
            onGround = true;
            jumpCount = 0;
        }

        // Animate legs
        animTimer++;
        if (animTimer > 6) {
            animFrame = (animFrame + 1) % 2;
            animTimer = 0;
        }
    }

    /**
     * Renders the dinosaur using Java2D pixel-art style shapes.
     * OOP: Drawing logic encapsulated within the model (simplified MVC-V blend).
     */
    public void draw(Graphics2D g2d, boolean shieldActive) {
        if (dead) return;
        // Blink when invincible
        if (invincibleFrames > 0 && (invincibleFrames / 4) % 2 == 1) return;

        Color base = new Color(51, 51, 51);
        Color accent = new Color(80, 80, 80);
        Color eyeWhite = new Color(247, 243, 235);

        if (shieldActive) {
            g2d.setColor(new Color(68, 170, 255, 60));
            g2d.fillOval((int)x - 8, (int)y - 8, getWidth() + 16, getHeight() + 16);
        }

        int ix = (int) x, iy = (int) y;

        if (ducking) {
            g2d.setColor(base);
            g2d.fillRect(ix, iy + 12, DINO_W, DUCK_H - 12);
            g2d.setColor(accent);
            g2d.fillRect(ix + DINO_W - 14, iy + 14, 12, 10);
            g2d.setColor(eyeWhite);
            g2d.fillRect(ix + DINO_W - 12, iy + 16, 6, 5);
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(ix + DINO_W - 10, iy + 17, 3, 3);
            g2d.setColor(base);
            if (animFrame == 0) {
                g2d.fillRect(ix + 4, iy + DUCK_H + 4, 10, 9);
                g2d.fillRect(ix + 22, iy + DUCK_H, 10, 5);
            } else {
                g2d.fillRect(ix + 4, iy + DUCK_H, 10, 5);
                g2d.fillRect(ix + 22, iy + DUCK_H + 4, 10, 9);
            }
        } else {
            g2d.setColor(base);
            g2d.fillRect(ix + 8, iy, DINO_W - 8, DINO_H - 18);
            g2d.fillRect(ix, iy + 14, 18, DINO_H - 30);
            g2d.setColor(accent);
            g2d.fillRect(ix + DINO_W - 16, iy + 4, 14, 14);
            g2d.setColor(eyeWhite);
            g2d.fillRect(ix + DINO_W - 14, iy + 6, 7, 7);
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(ix + DINO_W - 11, iy + 8, 3, 3);
            g2d.setColor(base);
            if (animFrame == 0) {
                g2d.fillRect(ix + 10, iy + DINO_H - 18, 10, 18);
                g2d.fillRect(ix + 24, iy + DINO_H - 12, 10, 12);
            } else {
                g2d.fillRect(ix + 10, iy + DINO_H - 12, 10, 12);
                g2d.fillRect(ix + 24, iy + DINO_H - 18, 10, 18);
            }
        }
    }

    // --- Hitbox for collision detection ---
    public Rectangle getHitbox() {
        return new Rectangle((int) x + 4, (int) y + 4, getWidth() - 8, getHeight() - 8);
    }

    public int getWidth()  { return DINO_W; }
    public int getHeight() { return ducking ? DUCK_H : DINO_H; }
    public float getX() { return x; }
    public float getY() { return y; }
    public boolean isDead() { return dead; }
    public boolean isDucking() { return ducking; }
    public boolean isOnGround() { return onGround; }
    public int getAnimFrame() { return animFrame; }
    public void setDead(boolean dead) { this.dead = dead; }
    public void setInvincible(int frames) { this.invincibleFrames = frames; }
    public boolean isInvincible() { return invincibleFrames > 0; }
}
