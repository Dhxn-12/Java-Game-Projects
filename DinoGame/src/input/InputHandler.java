package input;

import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles all keyboard input for the game.
 * Pattern: Observer — components register listeners via interface.
 * Core Java: KeyAdapter, event handling, lambda-friendly interface.
 */
public class InputHandler extends KeyAdapter {

    /** Functional interface for jump/duck callbacks (lambda-compatible). */
    @FunctionalInterface
    public interface InputListener {
        void onJump();
    }

    @FunctionalInterface
    public interface DuckListener {
        void onDuck(boolean ducking);
    }

    @FunctionalInterface
    public interface PauseListener {
        void onPause();
    }

    private InputListener    jumpListener;
    private DuckListener     duckListener;
    private PauseListener    pauseListener;
    private final Set<Integer> heldKeys = new HashSet<>();

    public void setJumpListener(InputListener l)  { this.jumpListener  = l; }
    public void setDuckListener(DuckListener l)   { this.duckListener  = l; }
    public void setPauseListener(PauseListener l) { this.pauseListener = l; }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (heldKeys.contains(code)) return; // Prevent key repeat for jump
        heldKeys.add(code);

        switch (code) {
            case KeyEvent.VK_SPACE, KeyEvent.VK_UP ->
                { if (jumpListener != null) jumpListener.onJump(); }
            case KeyEvent.VK_DOWN ->
                { if (duckListener != null) duckListener.onDuck(true); }
            case KeyEvent.VK_ESCAPE, KeyEvent.VK_P ->
                { if (pauseListener != null) pauseListener.onPause(); }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        heldKeys.remove(code);
        if (code == KeyEvent.VK_DOWN && duckListener != null) {
            duckListener.onDuck(false);
        }
    }
}
