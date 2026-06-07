package factory;

import model.*;
import java.util.Random;

/**
 * Factory class for creating obstacles.
 * Pattern: Factory Method — decouples creation logic from game engine.
 * DSA: Random selection weighted by current score (difficulty).
 */
public class ObstacleFactory {

    private static final Random RNG = new Random();

    private ObstacleFactory() {}  // Utility class — no instantiation

    /**
     * Creates an appropriate obstacle based on current score (difficulty).
     * At low scores, only cacti spawn. Birds appear after score 150.
     * Time Complexity: O(1)
     *
     * @param score     current score
     * @param screenW   canvas width
     * @param groundY   ground level y-coordinate
     * @return new Obstacle instance (Cactus or Ptero)
     */
    public static Obstacle create(int score, int screenW, int groundY) {
        float birdChance = score > 150 ? Math.min(0.38f, (score - 150) / 800f) : 0f;
        if (RNG.nextFloat() < birdChance) {
            return new Ptero(screenW, groundY);
        }
        return new Cactus(screenW, groundY);
    }
}
