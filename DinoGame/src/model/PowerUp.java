package model;

import java.awt.*;

/**
 * Power-up collectible item.
 * OOP: Encapsulates type, position, and pulse animation.
 * Pattern: Created by PowerUpFactory.
 */
public class PowerUp {

    private final PowerUpType type;
    private float x;
    private final float y;
    private final int w = 22, h = 22;
    private boolean collected = false;
    private float pulse = 0;

    public PowerUp(PowerUpType type, int screenX, int groundY) {
        this.type = type;
        this.x = screenX + 20;
        this.y = groundY - 72;
    }

    public void update(float speed, boolean slowMode) {
        x -= slowMode ? speed * 0.4f : speed;
        pulse = (pulse + 0.12f) % ((float) Math.PI * 2);
    }

    public void draw(Graphics2D g2d) {
        if (collected) return;
        float alpha = (float)(Math.sin(pulse) * 0.35 + 0.65);
        int ix = (int) x, iy = (int) y;

        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(ac);

        Color fill, text;
        String label;
        switch (type) {
            case SHIELD      -> { fill = new Color(68, 170, 255);  text = Color.WHITE; label = "S"; }
            case SLOW_MOTION -> { fill = new Color(255, 190, 50);  text = Color.WHITE; label = "T"; }
            default          -> { fill = new Color(255, 100, 60);  text = Color.WHITE; label = "x2"; }
        }

        g2d.setColor(fill);
        g2d.fillOval(ix, iy, w, h);
        g2d.setColor(text);
        g2d.setFont(new Font("Arial", Font.BOLD, 9));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(label, ix + (w - fm.stringWidth(label)) / 2, iy + (h + fm.getAscent()) / 2 - 2);

        g2d.setComposite(AlphaComposite.SrcOver);
    }

    public Rectangle getHitbox() {
        return new Rectangle((int) x, (int) y, w, h);
    }

    public float getX() { return x; }
    public PowerUpType getType() { return type; }
    public boolean isCollected() { return collected; }
    public boolean isOffScreen() { return x + w < -10; }
    public void collect() { this.collected = true; }
}
