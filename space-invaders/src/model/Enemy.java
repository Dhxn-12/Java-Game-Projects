package model;

import util.AssetLoader;
import util.Constants;
import java.awt.*;

/**
 * Represents a single enemy alien.
 * Stores row/column position in the grid, pixel coordinates, HP, and animation state.
 */
public class Enemy {

    public enum Type { GRUNT, ELITE, BOSS }

    private int x, y;
    private final int width  = Constants.ENEMY_WIDTH;
    private final int height = Constants.ENEMY_HEIGHT;

    private final int row, col;  // grid position
    private final Type type;
    private int hp;

    private boolean alive = true;
    private int animFrame = 0;   // 0 or 1, toggled by the swarm
    private long deathTime = -1; // for death flash

    // Score value depends on row
    private final int scoreValue;

    public Enemy(int x, int y, int row, int col) {
        this.x   = x;
        this.y   = y;
        this.row = row;
        this.col = col;
        this.type = Type.GRUNT;
        this.hp   = 1;
        int sv;
        if (row == 0)      sv = Constants.SCORE_ROW_0;
        else if (row == 1) sv = Constants.SCORE_ROW_1;
        else if (row == 2) sv = Constants.SCORE_ROW_2;
        else if (row == 3) sv = Constants.SCORE_ROW_3;
        else               sv = Constants.SCORE_ROW_4;
        this.scoreValue = sv;
    }

    /** Constructor used by EnemyFactory for boss enemies. */
    public Enemy(int x, int y, int hp, Type type) {
        this.x   = x;
        this.y   = y;
        this.row = 0;
        this.col = 0;
        this.type = type;
        this.hp   = hp;
        this.scoreValue = Constants.SCORE_BOSS;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setAnimFrame(int frame) {
        this.animFrame = frame;
    }

    public void draw(Graphics2D g) {
        if (!alive) {
            // Brief white flash on death
            if (System.currentTimeMillis() - deathTime < 120) {
                g.setColor(Color.WHITE);
                g.fillRect(x + 4, y + 4, width - 8, height - 8);
            }
            return;
        }

        if (type == Type.BOSS) {
            g.drawImage(AssetLoader.getBoss(animFrame), x, y, null);
            // HP bar
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, y - 8, width, 5);
            g.setColor(new Color(220, 0, 220));
            g.fillRect(x, y - 8, (int)(width * (hp / (float) Constants.BOSS_HP)), 5);
        } else {
            g.drawImage(AssetLoader.getEnemy(row, animFrame), x, y, null);
        }
    }

    public void hit(int damage) {
        hp -= damage;
        if (hp <= 0) {
            alive     = false;
            deathTime = System.currentTimeMillis();
        }
    }

    public boolean isAlive()   { return alive; }
    public boolean isDeathFlashActive() {
        return !alive && System.currentTimeMillis() - deathTime < 120;
    }

    public Rectangle getBounds() {
        return new Rectangle(x + 3, y + 3, width - 6, height - 6);
    }

    public int  getX()          { return x; }
    public int  getY()          { return y; }
    public int  getWidth()      { return width; }
    public int  getHeight()     { return height; }
    public int  getRow()        { return row; }
    public int  getCol()        { return col; }
    public int  getScoreValue() { return scoreValue; }
    public int  getHp()         { return hp; }
    public Type getType()       { return type; }

    // Cannon tip for shooting
    public int getCannonX() { return x + width / 2; }
    public int getCannonY() { return y + height; }
}
