package engine;

import model.Achievement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Manages all game achievements.
 * DSA: HashMap<String, Achievement> — O(1) lookup, O(n) reset.
 * Pattern: Observer — notifies UI via Consumer<Achievement> callback.
 * Core Java: Lambda expressions, functional interfaces, generics.
 */
public class AchievementManager {

    /** DSA: HashMap stores achievements keyed by ID — O(1) access. */
    private final Map<String, Achievement> achievements = new HashMap<>();

    /** Observer callback (lambda) invoked when an achievement unlocks. */
    private Consumer<Achievement> onUnlock;

    public AchievementManager() {
        register("first_jump",   "First Jump!",        "Jump for the first time");
        register("score_100",    "100 Club",            "Reach score 100");
        register("score_500",    "500 Runner",          "Reach score 500");
        register("score_1000",   "Master Runner",       "Reach score 1000");
        register("score_2000",   "Legendary",           "Reach score 2000");
        register("night_mode",   "Night Owl",           "Survive into night mode");
        register("shield_used",  "Shield Bearer",       "Collect a shield power-up");
        register("double_jump",  "Double Jumper",       "Perform a double jump");
        register("no_crash_100", "Clean Run",           "Reach 100 without a hit");
    }

    private void register(String id, String name, String desc) {
        achievements.put(id, new Achievement(id, name, desc));
    }

    public void setOnUnlock(Consumer<Achievement> callback) {
        this.onUnlock = callback;
    }

    /**
     * Checks whether named achievement should unlock, given current state.
     * Triggers callback if newly unlocked.
     */
    public void check(String id, boolean condition) {
        Achievement a = achievements.get(id);
        if (a != null && !a.isUnlocked() && condition) {
            a.unlock();
            if (onUnlock != null) onUnlock.accept(a);
        }
    }

    /** Resets all achievements (per-session). */
    public void resetSession() {
        achievements.values().forEach(Achievement::reset);
    }

    public Map<String, Achievement> getAll() { return achievements; }
}
