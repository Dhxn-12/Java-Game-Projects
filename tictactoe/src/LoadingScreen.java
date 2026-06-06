
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Animated loading/splash screen shown on startup.
 * Transitions to MainMenuScreen after completing the progress animation.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class LoadingScreen extends JWindow {

    private float progress = 0f;
    private String statusText = "Initializing...";
    private final String[] steps = {
        "Loading assets...", "Building AI engine...",
        "Preparing sounds...", "Applying themes...",
        "Ready to play!"
    };

    public LoadingScreen() {
        setSize(520, 340);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));

        JPanel panel = createPanel();
        add(panel);

        // Progress animation
        Timer timer = new Timer(30, null);
        int[] frame = {0};
        timer.addActionListener(e -> {
            progress += 0.012f;
            if (progress >= 1f) progress = 1f;

            int step = (int)(progress * steps.length);
            if (step < steps.length) statusText = steps[step];

            panel.repaint();

            if (progress >= 1f) {
                timer.stop();
                Timer delay = new Timer(500, ev -> {
                    dispose();
                    new MainMenuScreen().setVisible(true);
                });
                delay.setRepeats(false);
                delay.start();
            }
        });
        timer.start();
    }

    private JPanel createPanel() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                ThemeManager.Theme t = ThemeManager.getInstance().getTheme();
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);

                int w = getWidth(), h = getHeight();

                // Rounded window background
                RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, w, h, 20, 20);
                g2.setClip(roundedRect);

                // Background gradient
                GradientPaint grad = new GradientPaint(0, 0, t.gradientStart, w, h, t.gradientEnd);
                g2.setPaint(grad);
                g2.fill(roundedRect);

                // Border glow
                g2.setColor(new Color(t.accent.getRed(), t.accent.getGreen(), t.accent.getBlue(), 80));
                g2.setStroke(new BasicStroke(2f));
                g2.draw(roundedRect);

                // Logo / Title
                g2.setFont(ThemeManager.FONT_TITLE.deriveFont(48f));
                String title = "TIC TAC TOE";
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(title)) / 2;

                // Glow shadow
                for (int i = 8; i > 0; i--) {
                    g2.setColor(new Color(t.accent.getRed(), t.accent.getGreen(), t.accent.getBlue(),
                            10 * i));
                    g2.drawString(title, tx - i/2, 115 - i/2);
                    g2.drawString(title, tx + i/2, 115 + i/2);
                }
                g2.setColor(t.accent);
                g2.drawString(title, tx, 115);

                // Subtitle
                g2.setFont(ThemeManager.FONT_BODY);
                g2.setColor(t.textSecondary);
                String sub = "PRO EDITION  ·  v2.0";
                g2.drawString(sub, (w - g2.getFontMetrics().stringWidth(sub)) / 2, 142);

                // Divider
                g2.setColor(new Color(t.accent.getRed(), t.accent.getGreen(), t.accent.getBlue(), 60));
                g2.drawLine(60, 160, w - 60, 160);

                // Progress bar track
                int bx = 60, by = 200, bw = w - 120, bh = 6;
                g2.setColor(t.surface);
                g2.fillRoundRect(bx, by, bw, bh, 6, 6);

                // Progress bar fill with glow
                int fillW = (int)(bw * progress);
                if (fillW > 0) {
                    GradientPaint barGrad = new GradientPaint(bx, 0, t.accent, bx + fillW, 0, t.accentSecondary);
                    g2.setPaint(barGrad);
                    g2.fillRoundRect(bx, by, fillW, bh, 6, 6);
                }

                // Status text
                g2.setFont(ThemeManager.FONT_SMALL);
                g2.setColor(t.textSecondary);
                g2.drawString(statusText, bx, by + 24);

                // Percentage
                String pct = (int)(progress * 100) + "%";
                g2.drawString(pct, bx + bw - g2.getFontMetrics().stringWidth(pct), by + 24);

                g2.dispose();
            }
        };
    }
}