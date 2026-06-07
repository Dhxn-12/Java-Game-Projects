package factory;

import model.*;
import java.util.Random;

/**
 * Factory class for creating power-ups.
 * Pattern: Factory Method.
 * DSA: Weighted random using cumulative probability bands.
 */
public class PowerUpFactory {

    private static final Random RNG = new Random();

    private PowerUpFactory() {}

    /**
     * Creates a random power-up.
     * Only spawns when score > 50 and a random threshold is met.
     * Time Complexity: O(1)
     */
    public static PowerUp create(int screenW, int groundY) {
        float r = RNG.nextFloat();
        PowerUpType type;
        if      (r < 0.40f) type = PowerUpType.SHIELD;
        else if (r < 0.70f) type = PowerUpType.SLOW_MOTION;
        else                type = PowerUpType.SCORE_X2;
        return new PowerUp(type, screenW, groundY);
    }

    /**
     * Returns true if a power-up should spawn this obstacle cycle.
     * @param score current score
     */
    public static boolean shouldSpawn(int score) {
        return score > 50 && RNG.nextFloat() < 0.13f;
    }
}
