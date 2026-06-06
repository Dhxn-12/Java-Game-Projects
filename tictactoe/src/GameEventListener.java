

/**
 * Observer interface for game events (Observer Pattern).
 * UI components implement this to react to game state changes.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
@FunctionalInterface
public interface GameEventListener {

    /**
     * Called when a game event occurs.
     * Always invoked on the Swing Event Dispatch Thread.
     *
     * @param event the type of event
     * @param data  optional extra data (e.g., move coordinates, timer value)
     */
    void onGameEvent(GameEvent event, int[] data);
}