
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Singleton that persists match records and leaderboard data to disk.
 * Uses Java object serialization for simplicity; swap to SQLite for production.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class ScoreManager {

    private static final Logger LOG = Logger.getLogger(ScoreManager.class.getName());
    private static final String DATA_DIR  = System.getProperty("user.home") + File.separator + ".tictactoepro";
    private static final String SCORES_FILE = DATA_DIR + File.separator + "matches.dat";
    private static ScoreManager instance;

    private final List<MatchRecord> matchHistory = new ArrayList<>();

    // ── Singleton ─────────────────────────────────────────────────────────────

    private ScoreManager() {}

    public static ScoreManager getInstance() {
        if (instance == null) instance = new ScoreManager();
        return instance;
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public void loadScores() {
        File file = new File(SCORES_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<MatchRecord> loaded = (List<MatchRecord>) ois.readObject();
            matchHistory.clear();
            matchHistory.addAll(loaded);
            LOG.info("Loaded " + matchHistory.size() + " match records.");
        } catch (Exception e) {
            LOG.warning("Could not load scores: " + e.getMessage());
        }
    }

    public void saveScores() {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) dir.mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORES_FILE))) {
                oos.writeObject(matchHistory);
            }
        } catch (Exception e) {
            LOG.warning("Could not save scores: " + e.getMessage());
        }
    }

    public void saveMatch(MatchRecord record) {
        matchHistory.add(0, record); // newest first
        if (matchHistory.size() > 500) matchHistory.remove(matchHistory.size() - 1);
        saveScores();
    }

    // ── Query ─────────────────────────────────────────────────────────────────

    public List<MatchRecord> getMatchHistory() {
        return Collections.unmodifiableList(matchHistory);
    }

    public List<MatchRecord> getRecentMatches(int count) {
        return Collections.unmodifiableList(
                matchHistory.subList(0, Math.min(count, matchHistory.size())));
    }

    /**
     * Returns a summary stats string for a given player name.
     */
    public String getPlayerStats(String playerName) {
        int wins = 0, losses = 0, draws = 0;
        for (MatchRecord r : matchHistory) {
            boolean isX = r.getPlayerXName().equals(playerName);
            boolean isO = r.getPlayerOName().equals(playerName);
            if (!isX && !isO) continue;

            char w = r.getWinner();
            if (w == 'D') draws++;
            else if ((w == 'X' && isX) || (w == 'O' && isO)) wins++;
            else losses++;
        }
        int total = wins + losses + draws;
        double pct = total == 0 ? 0.0 : 100.0 * wins / total;
        return String.format("%s: %d W | %d L | %d D | %.1f%%", playerName, wins, losses, draws, pct);
    }

    public void clearHistory() {
        matchHistory.clear();
        saveScores();
    }
}
