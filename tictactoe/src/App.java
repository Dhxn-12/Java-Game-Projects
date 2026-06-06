
import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for TicTacToe Pro.
 * Initializes core managers and launches the loading screen.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class App {

    public static void main(String[] args) {
        // Enable anti-aliasing for smoother text/graphics
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize core singletons
                ThemeManager.getInstance().loadTheme("NEON");
                SoundManager.getInstance().initialize();
                ScoreManager.getInstance().loadScores();

                // Show loading screen then transition to main menu
                LoadingScreen loadingScreen = new LoadingScreen();
                loadingScreen.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Failed to start TicTacToe Pro: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
