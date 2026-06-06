package com.casino.blackjack.view;

import com.casino.blackjack.manager.AchievementManager;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Non-blocking animated toast notification for achievement unlocks.
 * Slides in from the top-right corner and auto-dismisses.
 */
public class AchievementToast extends JWindow {

    private float alpha = 0f;
    private int   yOffset = -80;
    private Timer slideTimer, fadeTimer;

    public AchievementToast(JFrame parent, AchievementManager.Achievement achievement) {
        super(parent);
        setSize(320, 80);
        positionWindow(parent);
        buildUI(achievement);
        setBackground(new Color(0, 0, 0, 0));
        animate();
    }

    private void positionWindow(JFrame parent) {
        if (parent != null) {
            int px = parent.getX() + parent.getWidth() - getWidth() - 20;
            int py = parent.getY() + 60;
            setLocation(px, py);
        }
    }

    private void buildUI(AchievementManager.Achievement a) {
        JPanel panel = new JPanel(new BorderLayout(10, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.setColor(CasinoTheme.TABLE_TRIM);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(CasinoTheme.GOLD);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel icon = new JLabel(a.icon());
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);

        JLabel hdr = new JLabel("Achievement Unlocked!");
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 10));
        hdr.setForeground(CasinoTheme.GOLD);

        JLabel name = new JLabel(a.icon() + " " + a.title());
        name.setFont(CasinoTheme.FONT_BUTTON);
        name.setForeground(CasinoTheme.TEXT_PRIMARY);

        info.add(hdr); info.add(name);
        panel.add(icon, BorderLayout.WEST);
        panel.add(info, BorderLayout.CENTER);

        JLabel pts = new JLabel("+" + a.pointValue() + " pts");
        pts.setFont(CasinoTheme.FONT_SMALL);
        pts.setForeground(CasinoTheme.GOLD);
        panel.add(pts, BorderLayout.EAST);

        setContentPane(panel);
    }

    private void animate() {
        setVisible(true);
        // Slide in + fade in
        slideTimer = new Timer(16, e -> {
            alpha = Math.min(1f, alpha + 0.06f);
            repaint();
            if (alpha >= 1f) {
                slideTimer.stop();
                scheduleFadeOut();
            }
        });
        slideTimer.start();
    }

    private void scheduleFadeOut() {
        fadeTimer = new Timer(40, null);
        fadeTimer.addActionListener(e -> {
            alpha = Math.max(0f, alpha - 0.025f);
            repaint();
            if (alpha <= 0f) {
                fadeTimer.stop();
                dispose();
            }
        });
        fadeTimer.setInitialDelay(2800);
        fadeTimer.start();
    }
}
