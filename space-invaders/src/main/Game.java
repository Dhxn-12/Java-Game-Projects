package main;

import controller.InputHandler;
import util.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              SPACE INVADERS — Java Edition                  ║
 * ║                                                              ║
 * ║  Entry point.  Creates the JFrame, wires up InputHandler    ║
 * ║  and GamePanel, then shows the window.                      ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * Run instructions:
 *   javac -d out -sourcepath src src/main/Game.java
 *   java  -cp out main.Game
 *
 * Or use the provided build script / run.sh.
 */
public class Game {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::createAndShowWindow);
    }

    private static void createAndShowWindow() {
        JFrame frame = new JFrame(Constants.GAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Try to use a dark title bar on supported systems
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        InputHandler input = new InputHandler();
        GamePanel    panel = new GamePanel(input);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null); // center on screen
        frame.setVisible(true);

        // Give focus to the game panel so key events are captured
        panel.requestFocusInWindow();

        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║      SPACE INVADERS  —  Java Edition  ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║  Controls:                            ║");
        System.out.println("║   A / ← → Move                       ║");
        System.out.println("║   SPACE  — Shoot                      ║");
        System.out.println("║   P / ESC — Pause                     ║");
        System.out.println("║   R       — Restart (in-game)         ║");
        System.out.println("║   M       — Mute / Unmute             ║");
        System.out.println("║   ENTER   — Confirm / Start           ║");
        System.out.println("╚═══════════════════════════════════════╝");
    }
}
