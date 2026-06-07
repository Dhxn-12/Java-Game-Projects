package controller;

import engine.GameEngine;
import state.GameState;

/**
 * MVC Controller — bridges input events to GameEngine actions.
 * Decouples view/input from model logic.
 * All public methods are called by InputHandler (Observer pattern).
 */
public class GameController {

    private final GameEngine engine;

    public GameController(GameEngine engine) {
        this.engine = engine;
    }

    /** Handles jump key / mouse click. Delegates to engine. */
    public void onJump() {
        engine.onJump();
    }

    /** Handles duck key press/release. */
    public void onDuck(boolean ducking) {
        engine.onDuck(ducking);
    }

    /** Handles pause toggle. */
    public void onPause() {
        if (engine.getState() == GameState.PLAYING ||
            engine.getState() == GameState.PAUSED) {
            engine.togglePause();
        }
    }
}
