

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main menu screen — the hub for navigating all game modes and settings.
 * Uses a custom-painted background with particle effects.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class MainMenuScreen extends JFrame {

    // Floating particle animation
    private final float[] px = new float[30];
    private final float[] py = new float[30];
    private final float[] pv = new float[30];
    private final float[] pa = new float[30];
    private Timer particleTimer;

    public MainMenuScreen() {
        setTitle("TicTacToe Pro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 620);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(620, 520));

        initParticles();
        buildUI();
        setupKeyBindings();
    }

    private void initParticles() {
        for (int i = 0; i < px.length; i++) {
            px[i] = (float)(Math.random() * 800);
            py[i] = (float)(Math.random() * 620);
            pv[i] = (float)(0.2 + Math.random() * 0.5);
            pa[i] = (float)(Math.random() * 255);
        }
        particleTimer = new Timer(50, e -> {
            for (int i = 0; i < py.length; i++) {
                py[i] -= pv[i];
                if (py[i] < 0) { py[i] = 620; px[i] = (float)(Math.random() * 800); }
                pa[i] = (float)(120 + 80 * Math.sin(System.currentTimeMillis() * 0.001 + i));
            }
            getContentPane().repaint();
        });
        particleTimer.start();
    }

    private void buildUI() {
        ThemeManager.Theme theme = ThemeManager.getInstance().getTheme();

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                UIUtils.paintGradientBackground(g, this);
                // Particles
                Graphics2D g2 = (Graphics2D) g;
                UIUtils.enableAntiAliasing(g2);
                ThemeManager.Theme t = ThemeManager.getInstance().getTheme();
                for (int i = 0; i < px.length; i++) {
                    int alpha = (int) Math.min(255, Math.max(0, pa[i]));
                    g2.setColor(new Color(t.accent.getRed(), t.accent.getGreen(),
                            t.accent.getBlue(), alpha / 4));
                    int size = (int)(2 + (i % 3));
                    g2.fillOval((int)px[i], (int)py[i], size, size);
                }
            }
        };
        root.setOpaque(false);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new GridBagLayout()) { { setOpaque(false); } };
        JLabel logo = UIUtils.createGlowLabel("⚡ TIC TAC TOE PRO ⚡",
                ThemeManager.FONT_TITLE.deriveFont(38f), theme.accent);
        logo.setBorder(BorderFactory.createEmptyBorder(30, 0, 4, 0));

        JLabel version = new JLabel("v2.0  ·  Ultimate Edition", SwingConstants.CENTER);
        version.setFont(ThemeManager.FONT_SMALL);
        version.setForeground(theme.textSecondary);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; header.add(logo, gbc);
        gbc.gridy = 1; header.add(version, gbc);
        root.add(header, BorderLayout.NORTH);

        // ── Centre Buttons ────────────────────────────────────────────────────
        JPanel centre = new JPanel(new GridBagLayout()) { { setOpaque(false); } };
        GridBagConstraints c = new GridBagConstraints();
        c.insets  = new Insets(8, 20, 8, 20);
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        // Row 0 — mode buttons
        JButton pvpBtn   = UIUtils.createStyledButton("👥  Player vs Player",  true);
        JButton pvaiBtn  = UIUtils.createStyledButton("🤖  Player vs AI",       true);
        c.gridy = 0; c.gridx = 0; centre.add(pvpBtn,  c);
        c.gridy = 0; c.gridx = 1; centre.add(pvaiBtn, c);

        // Row 1 — secondary
        JButton histBtn  = UIUtils.createStyledButton("📋  Match History",      false);
        JButton statsBtn = UIUtils.createStyledButton("📊  Statistics",          false);
        c.gridy = 1; c.gridx = 0; centre.add(histBtn,  c);
        c.gridy = 1; c.gridx = 1; centre.add(statsBtn, c);

        // Row 2 — tertiary
        JButton settBtn  = UIUtils.createStyledButton("⚙  Settings",            false);
        JButton exitBtn  = UIUtils.createStyledButton("🚪  Exit",               false);
        c.gridy = 2; c.gridx = 0; centre.add(settBtn, c);
        c.gridy = 2; c.gridx = 1; centre.add(exitBtn, c);

        // Resize all buttons uniformly
        for (JButton btn : new JButton[]{pvpBtn, pvaiBtn, histBtn, statsBtn, settBtn, exitBtn}) {
            btn.setPreferredSize(new Dimension(240, 52));
        }

        root.add(centre, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout()) { { setOpaque(false); } };
        JLabel footerLbl = new JLabel(
                "  🎮  Use keyboard arrows or mouse  ·  Press F11 for fullscreen",
                SwingConstants.CENTER);
        footerLbl.setFont(ThemeManager.FONT_SMALL);
        footerLbl.setForeground(theme.textSecondary);
        footerLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        footer.add(footerLbl, BorderLayout.CENTER);

        // Theme indicator
        JLabel themeLabel = new JLabel(
                ThemeManager.getInstance().getTheme().name + "  ", SwingConstants.RIGHT);
        themeLabel.setFont(ThemeManager.FONT_SMALL);
        themeLabel.setForeground(theme.accent);
        footer.add(themeLabel, BorderLayout.EAST);

        root.add(footer, BorderLayout.SOUTH);

        // ── Action Wiring ─────────────────────────────────────────────────────
        pvpBtn.addActionListener(e -> {
            SoundManager.getInstance().playClick();
            openGameSetup(false);
        });
        pvaiBtn.addActionListener(e -> {
            SoundManager.getInstance().playClick();
            openGameSetup(true);
        });
        histBtn.addActionListener(e -> {
            SoundManager.getInstance().playClick();
            new HistoryScreen(this).setVisible(true);
        });
        statsBtn.addActionListener(e -> {
            SoundManager.getInstance().playClick();
            new StatsScreen(this).setVisible(true);
        });
        settBtn.addActionListener(e -> {
            SoundManager.getInstance().playClick();
            new SettingsScreen(this).setVisible(true);
        });
        exitBtn.addActionListener(e -> {
            if (UIUtils.confirm(this, "Exit TicTacToe Pro?", "Exit")) {
                SoundManager.getInstance().shutdown();
                System.exit(0);
            }
        });

        setContentPane(root);
    }

    private void openGameSetup(boolean vsAI) {
        particleTimer.stop();
        new GameSetupScreen(this, vsAI).setVisible(true);
        setVisible(false);
    }

    private void setupKeyBindings() {
        // F11 fullscreen toggle
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F11"), "fullscreen");
        getRootPane().getActionMap().put("fullscreen", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice();
                if (gd.getFullScreenWindow() == null) {
                    dispose(); setUndecorated(true); gd.setFullScreenWindow(MainMenuScreen.this);
                } else {
                    gd.setFullScreenWindow(null); dispose(); setUndecorated(false); setVisible(true);
                }
            }
        });
    }

    @Override public void setVisible(boolean b) {
        if (b && !particleTimer.isRunning()) particleTimer.start();
        super.setVisible(b);
    }
}
