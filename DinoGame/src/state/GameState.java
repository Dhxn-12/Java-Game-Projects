package state;

/**
 * Enum representing all possible game states.
 * Used by the State Machine pattern in GameManager.
 * DSA: Enum acts like a key in a state-transition map.
 */
public enum GameState {
    MENU,       // Main menu screen
    PLAYING,    // Active gameplay
    PAUSED,     // Game paused
    GAME_OVER   // Player died
}
