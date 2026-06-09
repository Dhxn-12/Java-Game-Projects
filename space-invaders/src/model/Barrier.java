package model;

import util.AssetLoader;
import util.Constants;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A destructible barrier composed of a grid of 8×8 pixel blocks.
 * Each block has its own HP; bullets carve holes on impact.
 */
public class Barrier {

    private static final int BLOCK = 8; // px per cell

    // Grid dimensions (in blocks)
    private final int cols = Constants.BARRIER_WIDTH  / BLOCK;  // 8
    private final int rows = Constants.BARRIER_HEIGHT / BLOCK;  // 6

    private final int originX, originY;
    private final int[][] hp; // hp[row][col], -1 = destroyed

    private final BufferedImage blockImg;

    public Barrier(int x, int y) {
        this.originX  = x;
        this.originY  = y;
        this.hp       = new int[rows][cols];
        this.blockImg = AssetLoader.getBarrierBlock();
        initShape();
    }

    /** Carve arch-like shape (classic Space Invaders bunker shape). */
    private void initShape() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                hp[r][c] = Constants.BARRIER_HP;
            }
        }
        // Carve bottom-center notch (the arch)
        for (int r = rows - 2; r < rows; r++) {
            for (int c = 2; c < cols - 2; c++) {
                hp[r][c] = -1; // destroyed
            }
        }
        // Rounded top corners
        hp[0][0] = -1;
        hp[0][cols-1] = -1;
    }

    public void draw(Graphics2D g) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (hp[r][c] <= 0) continue;
                int px = originX + c * BLOCK;
                int py = originY + r * BLOCK;

                // Shade based on remaining HP
                float ratio = hp[r][c] / (float) Constants.BARRIER_HP;
                int alpha = 80 + (int)(175 * ratio);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                          Math.min(1f, alpha / 255f)));
                g.drawImage(blockImg, px, py, null);
                g.setComposite(AlphaComposite.SrcOver);
            }
        }
    }

    /**
     * Test bullet collision with this barrier.
     * Returns true if the bullet should be destroyed.
     */
    public boolean checkBulletCollision(Rectangle bulletBounds) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (hp[r][c] <= 0) continue;
                int px = originX + c * BLOCK;
                int py = originY + r * BLOCK;
                Rectangle cell = new Rectangle(px, py, BLOCK, BLOCK);
                if (cell.intersects(bulletBounds)) {
                    hp[r][c] -= 3;
                    if (hp[r][c] < 0) hp[r][c] = 0;
                    // Also chip neighbors for crater effect
                    chipNeighbour(r-1, c);
                    chipNeighbour(r+1, c);
                    return true;
                }
            }
        }
        return false;
    }

    private void chipNeighbour(int r, int c) {
        if (r >= 0 && r < rows && c >= 0 && c < cols && hp[r][c] > 0) {
            hp[r][c] -= 1;
        }
    }

    public boolean isDestroyed() {
        for (int[] row : hp)
            for (int v : row)
                if (v > 0) return false;
        return true;
    }

    public int getOriginX() { return originX; }
    public int getOriginY() { return originY; }
}
