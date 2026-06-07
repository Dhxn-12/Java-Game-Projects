package model;

import java.io.Serializable;

/**
 * Persistent statistics data class.
 * Implements Serializable for file-based save/load.
 * DSA: All fields are primitive — O(1) access.
 */
public class GameStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private int highScore;
    private int totalGames;
    private int totalJumps;
    private long totalPlayTimeMs;
    private int collisions;

    public int getHighScore()       { return highScore; }
    public int getTotalGames()      { return totalGames; }
    public int getTotalJumps()      { return totalJumps; }
    public long getTotalPlayTimeMs(){ return totalPlayTimeMs; }
    public int getCollisions()      { return collisions; }

    public void updateHighScore(int score) { if (score > highScore) highScore = score; }
    public void incrementGames()    { totalGames++; }
    public void incrementJumps()    { totalJumps++; }
    public void addPlayTime(long ms){ totalPlayTimeMs += ms; }
    public void incrementCollisions(){ collisions++; }
}
