package model;

import java.awt.*;

/**
 * Individual dust/debris particle for visual effects.
 * DSA: Stored in an ArrayList; dead particles are removed each frame.
 */
public class Particle {

    private float x, y;
    private final float vx, vy;
    private int life;
    private final int maxLife;
    private final Color color;
    private final int size;

    public Particle(float x, float y, float vx, float vy, int life, Color color, int size) {
        this.x = x; this.y = y;
        this.vx = vx; this.vy = vy;
        this.life = life; this.maxLife = life;
        this.color = color; this.size = size;
    }

    public void update() {
        x += vx;
        y += vy;
        life--;
    }

    public void draw(Graphics2D g2d) {
        float alpha = (float) life / maxLife;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(color);
        g2d.fillRect((int) x, (int) y, size, size);
        g2d.setComposite(AlphaComposite.SrcOver);
    }

    public boolean isDead() { return life <= 0; }
}
