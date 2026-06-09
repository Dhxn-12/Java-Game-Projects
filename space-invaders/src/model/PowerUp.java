package model;

import util.AssetLoader;
import util.Constants;
import java.awt.*;

/**
 * A power-up that drops from a killed enemy.
 *
 *  Type 0 → Rapid Fire
 *  Type 1 → Shield
 *  Type 2 → Extra Life
 */
public class PowerUp {

    private int x, y;
    private final int width  = Constants.POWERUP_WIDTH;
    private final int height = Constants.POWERUP_HEIGHT;
    private final int type;
    private boolean active = true;

    // Bobbing animation
    private double bobAngle = 0;
    private int baseY;

    public PowerUp(int x, int y, int type) {
        this.x    = x - width / 2;
        this.y    = y;
        this.baseY = y;
        this.type = type;
    }

    public void update() {
        y += Constants.POWERUP_SPEED;
        baseY += Constants.POWERUP_SPEED;
        bobAngle += 0.08;

        if (y > Constants.WINDOW_HEIGHT) active = false;
    }

    public void draw(Graphics2D g) {
        int drawY = baseY + (int)(Math.sin(bobAngle) * 4);
        g.drawImage(AssetLoader.getPowerUp(type), x, drawY, null);

        // Glow aura
        long now = System.currentTimeMillis();
        float alpha = 0.2f + 0.15f * (float) Math.sin(now / 200.0);
        Color[] glowColors = {
            new Color(0, 200, 255,  (int)(alpha * 255)),
            new Color(0, 255, 100,  (int)(alpha * 255)),
            new Color(255, 200, 0,  (int)(alpha * 255)),
        };
        g.setColor(glowColors[type % glowColors.length]);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(x - 4, drawY - 4, width + 8, height + 8);
    }

    public boolean isActive() { return active; }
    public void deactivate()  { active = false; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getType() { return type; }
    public int getX()    { return x; }
    public int getY()    { return y; }
}
