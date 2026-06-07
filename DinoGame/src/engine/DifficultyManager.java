package engine;

/**
 * Controls game difficulty scaling over time.
 * Pattern: Strategy — difficulty algorithm is encapsulated here,
 *          swappable without changing GameEngine.
 * DSA: Linear/stepped functions of score.
 */
public class DifficultyManager {

    private static final float BASE_SPEED   = 5.0f;
    private static final float MAX_SPEED    = 14.0f;
    private static final int   BASE_INTERVAL = 90;
    private static final int   MIN_INTERVAL  = 38;

    /**
     * Computes current game speed from score.
     * Speed increases every 200 points — O(1).
     */
    public float getSpeed(int score) {
        float speed = BASE_SPEED + (score / 200) * 0.7f;
        return Math.min(speed, MAX_SPEED);
    }

    /**
     * Computes spawn interval (frames between obstacles).
     * Decreases with score — obstacles come faster.
     */
    public int getSpawnInterval(int score) {
        int interval = BASE_INTERVAL - (score / 100) * 4;
        return Math.max(interval, MIN_INTERVAL);
    }

    /**
     * Returns score multiplier string description for HUD.
     */
    public String getDifficultyLabel(int score) {
        if (score < 200)  return "Easy";
        if (score < 500)  return "Normal";
        if (score < 1000) return "Hard";
        return "Insane";
    }
}
