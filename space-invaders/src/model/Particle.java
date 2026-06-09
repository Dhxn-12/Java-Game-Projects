package model;

import java.awt.*;

/**
 * A single particle emitted during an explosion.
 * Uses basic physics (velocity + gravity + drag) for realistic movement.
 */
public class Particle {

    private float x, y;
    private float vx, vy;
    private float life;       // 0.0 → 1.0, decreasing
    private float decay;      // how fast life drains per frame
    private float size;
    private final Color color;

    public Particle(float x, float y, float vx, float vy,
                    float life, float size, Color color) {
        this.x     = x;
        this.y     = y;
        this.vx    = vx;
        this.vy    = vy;
        this.life  = life;
        this.decay = 0.025f + (float)(Math.random() * 0.02);
        this.size  = size;
        this.color = color;
    }

    public void update() {
        x    += vx;
        y    += vy;
        vy   += 0.12f; // gravity
        vx   *= 0.97f; // drag
        size *= 0.97f;
        life -= decay;
    }

    public void draw(Graphics2D g) {
        if (life <= 0) return;
        int alpha = (int)(Math.min(1f, life) * 255);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        int s = Math.max(1, (int) size);
        g.fillOval((int)x - s/2, (int)y - s/2, s, s);
    }

    public boolean isAlive() { return life > 0; }
}
