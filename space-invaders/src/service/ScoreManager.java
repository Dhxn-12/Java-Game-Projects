package service;

import util.SaveLoadManager;

/**
 * Singleton that manages the current session score and high score.
 * The score is mirrored on the Player object for display; this class
 * provides a single source of truth and persists records to disk.
 */
public final class ScoreManager {

    private static ScoreManager instance;

    private int currentScore = 0;
    private int highScore;

    private ScoreManager() {
        highScore = SaveLoadManager.getTopScore();
    }

    public static ScoreManager getInstance() {
        if (instance == null) instance = new ScoreManager();
        return instance;
    }

    public void reset() {
        currentScore = 0;
    }

    public void add(int points) {
        currentScore += points;
        if (currentScore > highScore) highScore = currentScore;
    }

    /** Call on game over to persist. */
    public void saveSession() {
        SaveLoadManager.saveScore(currentScore);
        highScore = SaveLoadManager.getTopScore();
    }

    public int getScore()     { return currentScore; }
    public int getHighScore() { return highScore; }
}
