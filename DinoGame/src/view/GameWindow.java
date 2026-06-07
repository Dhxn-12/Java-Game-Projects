package view;

import controller.GameController;
import engine.GameEngine;
import engine.GameLoop;
import input.InputHandler;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window.
 * Pattern: Singleton — only one window exists at a time.
 * MVC: View — creates and wires GamePanel, Controller, and Loop.
 * Core Java: JFrame, SwingUtilities, setDefaultCloseOperation.
 */
public class GameWindow extends JFrame {

    private static GameWindow instance;

    private final GameEngine     engine;
    private final GamePanel      panel;
    private final GameController controller;
    private final GameLoop       loop;

    /** Singleton accessor. */
    public static GameWindow getInstance() {
        if (instance == null) instance = new GameWindow();
        return instance;
    }

    private GameWindow() {
        engine     = new GameEngine();
        panel      = new GamePanel(engine);
        controller = new GameController(engine);

        // Wire input to controller
        InputHandler input = new InputHandler();
        input.setJumpListener(controller::onJump);
        input.setDuckListener(controller::onDuck);
        input.setPauseListener(controller::onPause);
        panel.addKeyListener(input);

        // Mouse click also triggers jump/start
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                controller.onJump();
            }
        });

        // Game loop: update engine + repaint panel on EDT
        loop = new GameLoop(
            () -> {
                engine.update();
                panel.tickAchievementPopup();
            },
            () -> SwingUtilities.invokeLater(panel::repaint)
        );

        // JFrame setup
        setTitle("Dino Run");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(panel);
        pack();
        setLocationRelativeTo(null);

        // Scale up 2x for better visibility
        panel.setPreferredSize(new Dimension(
            GameEngine.WIDTH  * 2,
            GameEngine.HEIGHT * 2
        ));
        pack();

        // Add window close handler to save & stop loop
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                loop.stop();
            }
        });

        panel.setFocusable(true);
        panel.requestFocusInWindow();

        loop.start();
    }
}
