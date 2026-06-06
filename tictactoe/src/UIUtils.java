
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Static utility methods for UI painting, styled buttons, and common dialogs.
 * Centralises all custom Swing rendering to avoid duplication.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public final class UIUtils {

    private UIUtils() {}

    // ── Gradient Painting ─────────────────────────────────────────────────────

    public static void paintGradientBackground(Graphics g, Component c) {
        ThemeManager.Theme theme = ThemeManager.getInstance().getTheme();
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(
                0, 0, theme.gradientStart,
                0, c.getHeight(), theme.gradientEnd);
        g2.setPaint(gp);
        g2.fillRect(0, 0, c.getWidth(), c.getHeight());

        // Scanlines overlay (neon/arcade themes)
        if (theme.scanlineOpacity > 0) {
            g2.setColor(new Color(0, 0, 0, (int)(theme.scanlineOpacity * 255)));
            for (int y = 0; y < c.getHeight(); y += 2) {
                g2.drawLine(0, y, c.getWidth(), y);
            }
        }
    }

    public static void enableAntiAliasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_QUALITY);
    }

    // ── Glow Effect ───────────────────────────────────────────────────────────

    public static void paintGlow(Graphics2D g2, Shape shape, Color glowColor, int radius) {
        for (int i = radius; i > 0; i--) {
            float alpha = (float)(radius - i + 1) / radius * 0.3f;
            g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                    (int)(alpha * 255)));
            BasicStroke stroke = new BasicStroke(i * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            g2.setStroke(stroke);
            g2.draw(shape);
        }
        g2.setStroke(new BasicStroke(2f));
    }

    // ── Styled Button ─────────────────────────────────────────────────────────

    public static JButton createStyledButton(String text, boolean primary) {
        ThemeManager.Theme theme = ThemeManager.getInstance().getTheme();

        JButton btn = new JButton(text) {
            private boolean hovered = false;
            private float   glowAnim = 0f;
            private Timer   animator;

            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        SoundManager.getInstance().playHover();
                        startAnim(true);
                    }
                    @Override public void mouseExited(MouseEvent e) {
                        hovered = false;
                        startAnim(false);
                    }
                    private void startAnim(boolean in) {
                        if (animator != null) animator.stop();
                        animator = new Timer(16, ev -> {
                            glowAnim += in ? 0.1f : -0.1f;
                            glowAnim  = Math.max(0f, Math.min(1f, glowAnim));
                            repaint();
                            if ((in && glowAnim >= 1f) || (!in && glowAnim <= 0f))
                                ((Timer)ev.getSource()).stop();
                        });
                        animator.start();
                    }
                });
            }

            @Override protected void paintComponent(Graphics g) {
                ThemeManager.Theme t = ThemeManager.getInstance().getTheme();
                Graphics2D g2 = (Graphics2D) g.create();
                enableAntiAliasing(g2);

                int w = getWidth(), h = getHeight();
                RoundRectangle2D rr = new RoundRectangle2D.Float(1, 1, w-2, h-2, 12, 12);

                // Background fill
                Color bg = primary
                        ? new Color(t.accent.getRed(), t.accent.getGreen(), t.accent.getBlue(),
                              (int)(40 + 60 * glowAnim))
                        : new Color(t.buttonBg.getRed(), t.buttonBg.getGreen(), t.buttonBg.getBlue(),
                              (int)(200 + 55 * glowAnim));
                g2.setColor(bg);
                g2.fill(rr);

                // Border glow
                if (glowAnim > 0) paintGlow(g2, rr, t.accent, (int)(4 * glowAnim));
                g2.setColor(primary ? t.accent : t.buttonBorder);
                g2.setStroke(new BasicStroke(primary ? 2f : 1.5f));
                g2.draw(rr);

                // Text
                g2.setFont(ThemeManager.FONT_BUTTON);
                g2.setColor(primary ? t.accent : t.textPrimary);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(text)) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, tx, ty);
                g2.dispose();
            }

            @Override protected void paintBorder(Graphics g) {}
        };

        btn.setPreferredSize(new Dimension(180, 44));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(ThemeManager.FONT_BUTTON);
        return btn;
    }

    // ── Text Field ────────────────────────────────────────────────────────────

    public static JTextField createStyledTextField(String placeholder) {
        ThemeManager.Theme theme = ThemeManager.getInstance().getTheme();
        JTextField tf = new JTextField(16) {
            @Override protected void paintComponent(Graphics g) {
                ThemeManager.Theme t = ThemeManager.getInstance().getTheme();
                Graphics2D g2 = (Graphics2D) g.create();
                enableAntiAliasing(g2);
                g2.setColor(t.surface);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    g2.setFont(getFont());
                    g2.setColor(t.textSecondary);
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                }
                g2.dispose();
            }
        };
        tf.setOpaque(false);
        tf.setFont(ThemeManager.FONT_BODY);
        tf.setForeground(theme.textPrimary);
        tf.setCaretColor(theme.accent);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(theme.buttonBorder, 8),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return tf;
    }

    // ── Round Border ──────────────────────────────────────────────────────────

    public static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int   radius;
        RoundBorder(Color c, int r) { this.color = c; this.radius = r; }

        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4,4,4,4); }
    }

    // ── Glowing Label ────────────────────────────────────────────────────────

    public static JLabel createGlowLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                enableAntiAliasing(g2);
                FontMetrics fm = g2.getFontMetrics(font);
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                // Glow layers
                for (int i = 6; i > 0; i--) {
                    g2.setFont(font);
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                            (int)(20.0 / i * 255 / 255)));
                    g2.drawString(getText(), x - i/2, y);
                    g2.drawString(getText(), x + i/2, y);
                    g2.drawString(getText(), x, y - i/2);
                    g2.drawString(getText(), x, y + i/2);
                }
                g2.setColor(color);
                g2.setFont(font);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    // ── Confirm Dialog ────────────────────────────────────────────────────────

    public static boolean confirm(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}