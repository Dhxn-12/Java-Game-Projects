package util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles saving and loading of high scores to/from the filesystem.
 * Scores are stored as plain text (one per line) in the working directory.
 */
public final class SaveLoadManager {

    private SaveLoadManager() {}

    /**
     * Returns the top-N high scores, sorted descending.
     */
    public static List<Integer> loadHighScores() {
        List<Integer> scores = new ArrayList<>();
        File f = new File(Constants.SAVE_FILE);
        if (!f.exists()) return scores;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try { scores.add(Integer.parseInt(line)); }
                    catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.err.println("[SaveLoad] Could not load scores: " + e.getMessage());
        }

        scores.sort(Collections.reverseOrder());
        return scores;
    }

    /**
     * Adds a new score and persists the top-N list.
     */
    public static void saveScore(int newScore) {
        List<Integer> scores = loadHighScores();
        scores.add(newScore);
        scores.sort(Collections.reverseOrder());
        // Keep only top N
        while (scores.size() > Constants.MAX_HIGH_SCORES) {
            scores.remove(scores.size() - 1);
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(Constants.SAVE_FILE))) {
            for (int s : scores) pw.println(s);
        } catch (IOException e) {
            System.err.println("[SaveLoad] Could not save score: " + e.getMessage());
        }
    }

    /**
     * Returns the single highest recorded score, or 0 if none.
     */
    public static int getTopScore() {
        List<Integer> scores = loadHighScores();
        return scores.isEmpty() ? 0 : scores.get(0);
    }
}
