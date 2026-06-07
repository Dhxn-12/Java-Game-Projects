package model;

/**
 * Represents a single achievement.
 * DSA: Stored as values in a HashMap<String, Achievement> in GameManager.
 */
public class Achievement {

    private final String id;
    private final String name;
    private final String description;
    private boolean unlocked;

    public Achievement(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unlocked = false;
    }

    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public boolean isUnlocked()    { return unlocked; }
    public void unlock()           { this.unlocked = true; }
    public void reset()            { this.unlocked = false; }
}
