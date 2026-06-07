package model;

import java.awt.*;
import java.util.Random;

/**
 * Pterodactyl flying obstacle — extends Obstacle.
 * OOP: Inheritance + Overriding.
 * Flies at 3 possible heights requiring jump, duck, or precise timing.
 */
public class Ptero extends Obstacle {

    private static final int W = 48, H = 30;
    private static final Random RNG = new Random();

    private boolean wingUp = true;
    private int wingTimer = 0;
    private final int flightY;

    public Ptero(int screenWidth, int groundY) {
        super(screenWidth + 20, 0, W, H);
        // Three heights: high (must duck), mid (jump over), low (jump high)
        int[] heights = { groundY - 84, groundY - 58, groundY - 42 };
        this.flightY = heights[RNG.nextInt(heights.length)];
        this.y = flightY;
    }

    @Override
    public void update(float speed, boolean slowMode) {
        super.update(speed, slowMode);
        wingTimer++;
        if (wingTimer > 12) {
            wingUp = !wingUp;
            wingTimer = 0;
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        Color body = new Color(60, 60, 100);
        Color wing = new Color(80, 80, 130);
        int ix = (int) x, iy = (int) y;

        g2d.setColor(body);
        g2d.fillRect(ix + 10, iy + 10, 28, 12);

        g2d.setColor(wing);
        if (wingUp) {
            g2d.fillRect(ix + 8, iy, 16, 10);
            g2d.fillRect(ix + 26, iy, 16, 10);
        } else {
            g2d.fillRect(ix + 4, iy + 18, 16, 10);
            g2d.fillRect(ix + 26, iy + 18, 16, 10);
        }

        // Head and beak
        g2d.setColor(body);
        g2d.fillRect(ix + 34, iy + 6, 14, 10);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(ix + 38, iy + 8, 5, 5);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(ix + 39, iy + 9, 2, 2);
    }
}
