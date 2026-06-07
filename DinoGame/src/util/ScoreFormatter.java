package util;

/**
 * Utility class for score formatting.
 * Core Java: static methods, String.format.
 */
public class ScoreFormatter {
    private ScoreFormatter() {}

    /** Formats score as zero-padded 5-digit string. */
    public static String format(int score) {
        return String.format("%05d", Math.max(0, score));
    }

    /** Formats milliseconds as MM:SS */
    public static String formatTime(long ms) {
        long secs  = ms / 1000;
        long mins  = secs / 60;
        secs       = secs % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
