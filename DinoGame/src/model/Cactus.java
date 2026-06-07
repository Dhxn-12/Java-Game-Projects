package model;

import java.awt.*;
import java.util.Random;

/**
 * Cactus obstacle — extends Obstacle.
 * OOP: Inheritance + Method Overriding (draw).
 * 3 visual variants (small, medium, large cluster).
 */
public class Cactus extends Obstacle {

    private final int type; // 0=small, 1=medium, 2=large
    private static final Random RNG = new Random();

    private static final int[] WIDTHS  = {24, 44, 64};
    private static final int[] HEIGHTS = {48, 56, 60};

    public Cactus(int screenWidth, int groundY) {
        super(screenWidth + 20, 0, 0, 0);
        this.type = RNG.nextInt(3);
        this.w = WIDTHS[type];
        this.h = HEIGHTS[type];
        this.y = groundY - this.h;
    }

    @Override
    public void draw(Graphics2D g2d) {
        Color dark  = new Color(30, 100, 30);
        Color light = new Color(50, 140, 50);
        int ix = (int) x, iy = (int) y;

        g2d.setColor(dark);
        switch (type) {
            case 0 -> {
                g2d.fillRect(ix + 8, iy, 8, h);
                g2d.setColor(light);
                g2d.fillRect(ix, iy + 14, 24, 8);
                g2d.fillRect(ix, iy + 14, 8, 18);
                g2d.fillRect(ix + 16, iy + 14, 8, 10);
            }
            case 1 -> {
                g2d.fillRect(ix + 14, iy, 16, h);
                g2d.setColor(light);
                g2d.fillRect(ix, iy + 12, 44, 12);
                g2d.fillRect(ix, iy + 12, 14, 24);
                g2d.fillRect(ix + 30, iy + 12, 14, 16);
            }
            case 2 -> {
                g2d.fillRect(ix + 22, iy, 20, h);
                g2d.setColor(light);
                g2d.fillRect(ix, iy + 14, 64, 12);
                g2d.fillRect(ix, iy + 8, 14, 22);
                g2d.fillRect(ix + 50, iy + 8, 14, 22);
                g2d.setColor(dark);
                g2d.fillRect(ix, iy + 8, 14, 28);
                g2d.fillRect(ix + 50, iy + 8, 14, 20);
            }
        }
    }

    @Override
    public Rectangle getHitbox() {
        int shrink = type == 2 ? 10 : 5;
        return new Rectangle((int) x + shrink, (int) y + 4, w - shrink * 2, h - 4);
    }
}
