

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a player with profile data, statistics, and achievements.
 * Supports both human and AI players through polymorphism.
 *
 * <p>OOP Principles: Encapsulation (private fields + getters/setters),
 * designed for Inheritance (AIPlayer extends this).</p>
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Identity ────────────────────────────────────────────────────────────
    private final String id;
    private String name;
    private char symbol;          // 'X' or 'O'
    private PlayerType type;

    // ── Statistics ───────────────────────────────────────────────────────────
    private int wins;
    private int losses;
    private int draws;
    private int totalGames;
    private int currentStreak;
    private int bestStreak;
    private long totalTimePlayed;  // milliseconds

    // ── Visual ───────────────────────────────────────────────────────────────
    private String avatarCode;    // emoji or code for avatar
    private String colorHex;      // player accent color

    // ── Achievements ─────────────────────────────────────────────────────────
    private boolean achievedFirstWin;
    private boolean achievedWinStreak5;
    private boolean achievedPerfectGame;
    private boolean achievedUndefeated10;

    public enum PlayerType {
        HUMAN, AI_EASY, AI_MEDIUM, AI_HARD
    }

    // ── Constructors ─────────────────────────────────────────────────────────

    public Player(String name, char symbol, PlayerType type) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.symbol = symbol;
        this.type = type;
        this.avatarCode = (symbol == 'X') ? "⚔" : "🛡";
        this.colorHex = (symbol == 'X') ? "#FF6B6B" : "#4ECDC4";
    }

    public Player(String name, char symbol) {
        this(name, symbol, PlayerType.HUMAN);
    }

    // ── Statistics Methods ───────────────────────────────────────────────────

    public void recordWin() {
        wins++;
        totalGames++;
        currentStreak++;
        if (currentStreak > bestStreak) bestStreak = currentStreak;
        checkAchievements();
    }

    public void recordLoss() {
        losses++;
        totalGames++;
        currentStreak = 0;
    }

    public void recordDraw() {
        draws++;
        totalGames++;
    }

    public double getWinPercentage() {
        if (totalGames == 0) return 0.0;
        return (double) wins / totalGames * 100.0;
    }

    public String getStatsDisplay() {
        return String.format("W:%d  L:%d  D:%d  (%.1f%%)", wins, losses, draws, getWinPercentage());
    }

    // ── Achievements ─────────────────────────────────────────────────────────

    private void checkAchievements() {
        if (!achievedFirstWin && wins >= 1) {
            achievedFirstWin = true;
        }
        if (!achievedWinStreak5 && currentStreak >= 5) {
            achievedWinStreak5 = true;
        }
        if (!achievedUndefeated10 && wins >= 10 && losses == 0) {
            achievedUndefeated10 = true;
        }
    }

    public void markPerfectGame() {
        achievedPerfectGame = true;
    }

    public String getAchievementSummary() {
        StringBuilder sb = new StringBuilder();
        if (achievedFirstWin)      sb.append("🏆 First Win  ");
        if (achievedWinStreak5)    sb.append("🔥 5-Win Streak  ");
        if (achievedPerfectGame)   sb.append("✨ Perfect Game  ");
        if (achievedUndefeated10)  sb.append("👑 Undefeated 10  ");
        return sb.length() == 0 ? "No achievements yet" : sb.toString().trim();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getId()              { return id; }
    public String getName()            { return name; }
    public void   setName(String n)    { this.name = n; }
    public char   getSymbol()          { return symbol; }
    public void   setSymbol(char s)    { this.symbol = s; }
    public PlayerType getType()        { return type; }
    public boolean isHuman()           { return type == PlayerType.HUMAN; }
    public int    getWins()            { return wins; }
    public int    getLosses()          { return losses; }
    public int    getDraws()           { return draws; }
    public int    getTotalGames()      { return totalGames; }
    public int    getCurrentStreak()   { return currentStreak; }
    public int    getBestStreak()      { return bestStreak; }
    public String getAvatarCode()      { return avatarCode; }
    public void   setAvatarCode(String a) { this.avatarCode = a; }
    public String getColorHex()        { return colorHex; }
    public void   setColorHex(String c)   { this.colorHex = c; }
    public long   getTotalTimePlayed() { return totalTimePlayed; }
    public void   addTimePlayed(long ms) { totalTimePlayed += ms; }

    public void resetStats() {
        wins = losses = draws = totalGames = currentStreak = bestStreak = 0;
        totalTimePlayed = 0;
    }

    @Override
    public String toString() {
        return String.format("Player{name='%s', symbol=%c, type=%s, W:%d/L:%d/D:%d}",
                name, symbol, type, wins, losses, draws);
    }
}
    

