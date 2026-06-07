package save;

import model.GameStats;
import java.io.*;
import java.nio.file.*;

/**
 * Handles saving and loading GameStats to disk using Java Serialization.
 * Core Java: File Handling + Serialization + Exception Handling.
 * DSA: File I/O is O(1) for our small data structure.
 */
public class SaveManager {

    private static final String SAVE_DIR  = System.getProperty("user.home") + File.separator + ".dinorun";
    private static final String SAVE_FILE = SAVE_DIR + File.separator + "stats.dat";

    private SaveManager() {}

    /**
     * Saves GameStats to disk via ObjectOutputStream.
     * @param stats the stats to save
     */
    public static void save(GameStats stats) {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(SAVE_FILE))) {
                oos.writeObject(stats);
            }
        } catch (IOException e) {
            System.err.println("[SaveManager] Could not save: " + e.getMessage());
        }
    }

    /**
     * Loads GameStats from disk. Returns a fresh instance if not found.
     * @return loaded or new GameStats
     */
    public static GameStats load() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return new GameStats();
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return (GameStats) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[SaveManager] Could not load: " + e.getMessage());
            return new GameStats();
        }
    }

    /** Deletes the save file (used by reset). */
    public static void deleteSave() {
        new File(SAVE_FILE).delete();
    }
}
