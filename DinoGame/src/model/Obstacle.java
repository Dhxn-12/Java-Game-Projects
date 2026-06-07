package model;

import java.awt.*;

/**
 * Abstract base class for all obstacles.
 *
 * OOP: Abstraction + Polymorphism — Cactus and Ptero both extend this.
 * Pattern: Strategy — subclasses override draw() for their own rendering.
 */
public abstract class Obstacle {

    protected float x, y;
    protected int w, h;
    protected boolean dead;

    public Obstacle(float x, float y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.dead = false;
    }

    /**
     * Moves obstacle left by current speed.
     * O(1) per obstacle; O(n) total — called for entire list each frame.
     * @param speed current game speed
     * @param slowMode slow-motion power-up active
     */
    public void update(float speed, boolean slowMode) {
        x -= slowMode ? speed * 0.4f : speed;
        if (x + w < -20) dead = true;
    }

    /** Renders this obstacle. Overridden by each subclass. */
    public abstract void draw(Graphics2D g2d);

    /** Hitbox for AABB collision detection. */
    public Rectangle getHitbox() {
        return new Rectangle((int) x + 4, (int) y + 4, w - 8, h - 8);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getW()   { return w; }
    public int getH()   { return h; }
    public boolean isDead() { return dead; }
}
