package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Centralized keyboard input handler.
 * Tracks key-held state and fires single-press events for actions
 * like shoot, pause, and restart.
 */
public class InputHandler extends KeyAdapter {

    private final boolean[] keys = new boolean[256];

    // Single-press action flags (consumed after reading)
    private volatile boolean shootPressed   = false;
    private volatile boolean pausePressed   = false;
    private volatile boolean restartPressed = false;
    private volatile boolean enterPressed   = false;
    private volatile boolean mutePressed    = false;

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < keys.length) keys[code] = true;

        switch (code) {
            case KeyEvent.VK_SPACE:   shootPressed   = true; break;
            case KeyEvent.VK_P:
            case KeyEvent.VK_ESCAPE:  pausePressed   = true; break;
            case KeyEvent.VK_R:       restartPressed = true; break;
            case KeyEvent.VK_ENTER:   enterPressed   = true; break;
            case KeyEvent.VK_M:       mutePressed    = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < keys.length) keys[code] = false;
    }

    // ── Continuous hold state ─────────────────────────────────────────────────

    public boolean isLeftHeld() {
        return keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
    }

    public boolean isRightHeld() {
        return keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];
    }

    // ── Single-press consumers ────────────────────────────────────────────────

    public boolean consumeShoot() {
        if (shootPressed) { shootPressed = false; return true; }
        return false;
    }

    public boolean consumePause() {
        if (pausePressed) { pausePressed = false; return true; }
        return false;
    }

    public boolean consumeRestart() {
        if (restartPressed) { restartPressed = false; return true; }
        return false;
    }

    public boolean consumeMute() {
        if (mutePressed) { mutePressed = false; return true; }
        return false;
    }

    public boolean isSpaceHeld() {
        return keys[KeyEvent.VK_SPACE];
    }
}
