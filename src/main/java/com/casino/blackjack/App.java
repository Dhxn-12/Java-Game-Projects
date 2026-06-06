package com.casino.blackjack;

import com.casino.blackjack.controller.GameManager;
import com.casino.blackjack.view.CasinoTheme;
import com.casino.blackjack.view.MainWindow;

import javax.swing.*;

/**
 * Application entry point.
 *
 * Boot order:
 *   1. Apply Nimbus look-and-feel (fallback to system L&F)
 *   2. Apply casino theme
 *   3. Initialise GameManager (loads settings + saved state)
 *   4. Show MainWindow on the EDT
 */
public class App {

    public static void main(String[] args) {
        // 1. Look and feel
        applyLookAndFeel();

        // 2. Theme
        CasinoTheme.applyTheme(CasinoTheme.Theme.CLASSIC_GREEN);

        // 3. Init game engine
        GameManager.getInstance().init();

        // 4. Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }

    private static void applyLookAndFeel() {
        // Try Nimbus first (best dark-theme support)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    // Override Nimbus defaults for our dark theme
                    UIManager.put("control",         new java.awt.Color(0x1E4A1E));
                    UIManager.put("info",            new java.awt.Color(0x1E4A1E));
                    UIManager.put("nimbusBase",      new java.awt.Color(0x1A2F1A));
                    UIManager.put("nimbusBlueGrey",  new java.awt.Color(0x2A4A2A));
                    UIManager.put("nimbusLightBackground", new java.awt.Color(0x0D2210));
                    UIManager.put("text",            new java.awt.Color(0xF5F0E8));
                    return;
                }
            }
        } catch (Exception ignored) {}

        // Fallback: system L&F
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }
}
