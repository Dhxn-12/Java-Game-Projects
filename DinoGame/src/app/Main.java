package app;

import view.GameWindow;
import javax.swing.SwingUtilities;

/**
 * Entry point for the Chrome Dinosaur Game.
 * Launches the Swing GUI on the Event Dispatch Thread (EDT).
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = GameWindow.getInstance();
            window.setVisible(true);
        });
    }
}
