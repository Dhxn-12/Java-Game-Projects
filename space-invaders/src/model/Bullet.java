package model;

import util.AssetLoader;
import util.Constants;
import java.awt.*;

/**
 * Represents a bullet fired by the player or an enemy.
 */
public class Bullet {

    public enum Owner { PLAYER, ENEMY }

    private int x, y;
    private final int width, height;
    private final int speed;
    private final Owner owner;
    private boolean active = true;

    // Animation frame counter (for enemy zigzag visual)
    private int frame = 0;

    public Bullet(int x, int y, Owner owner) {
        this.owner  = owner;
        this.width  = owner == Owner.PLAYER ? Constants.PLAYER_BULLET_WIDTH  : Constants.ENEMY_BULLET_WIDTH;
        this.height = owner == Owner.PLAYER ? Constants.PLAYER_BULLET_HEIGHT : Constants.ENEMY_BULLET_HEIGHT;
        this.speed  = owner == Owner.PLAYER ? -Constants.PLAYER_BULLET_SPEED : Constants.ENEMY_BULLET_SPEED;

        // Center bullet on source x
        this.x = x - width / 2;
        this.y = y;
    }

    public void update() {
        y += speed;
        frame++;
        // Deactivate if off screen
        if (y + height < 0 || y > Constants.WINDOW_HEIGHT) {
            active = false;
        }
    }

    public void draw(Graphics2D g) {
        g.drawImage(AssetLoader.getBullet(owner == Owner.PLAYER), x, y, null);
    }

    public boolean isActive() { return active; }
    public void    deactivate() { active = false; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Owner  getOwner() { return owner; }
    public int    getX()     { return x; }
    public int    getY()     { return y; }
    public int    getWidth() { return width; }
    public int    getHeight(){ return height; }
}
