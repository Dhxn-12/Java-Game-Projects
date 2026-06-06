package com.casino.blackjack.view;

import com.casino.blackjack.controller.GameManager;
import com.casino.blackjack.model.GameObserver;
import com.casino.blackjack.model.GameEvent;
import com.casino.blackjack.model.GameState;
import com.casino.blackjack.save.SaveSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Animated main-menu screen.
 * Shown on launch; lets player create a profile or continue a saved game.
 */
public class MainMenuPanel extends JPanel implements GameObserver {

    private final GameManager gm = GameManager.getInstance();
    private float titlePulse = 0f;
    private Timer animTimer;

    private CasinoButton btnNewGame, btnContinue, btnSettings,
                          btnLeaderboard, btnTutorial, btnQuit;
    private JTextField    tfPlayerName;
    private JLabel        lblSubtitle, lblSavedInfo;

    public MainMenuPanel() {
        setLayout(null);
        setBackground(CasinoTheme.TABLE_BG);
        buildUI();
        gm.addObserver(this);
        startAnimation();
    }

    private void buildUI() {
        // Title is painted — no label needed

        lblSubtitle = new JLabel("Professional Casino Blackjack", SwingConstants.CENTER);
        lblSubtitle.setFont(CasinoTheme.FONT_SUBTITLE);
        lblSubtitle.setForeground(CasinoTheme.TEXT_SECONDARY);
        add(lblSubtitle);

        // Player name field
        tfPlayerName = new JTextField("Player");
        tfPlayerName.setFont(CasinoTheme.FONT_BODY);
        tfPlayerName.setBackground(new Color(0x0D2210));
        tfPlayerName.setForeground(CasinoTheme.TEXT_PRIMARY);
        tfPlayerName.setCaretColor(CasinoTheme.GOLD);
        tfPlayerName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CasinoTheme.GOLD, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tfPlayerName.setHorizontalAlignment(SwingConstants.CENTER);
        add(tfPlayerName);

        lblSavedInfo = new JLabel("", SwingConstants.CENTER);
        lblSavedInfo.setFont(CasinoTheme.FONT_SMALL);
        lblSavedInfo.setForeground(CasinoTheme.TEXT_SECONDARY);
        add(lblSavedInfo);

        // Buttons
        btnNewGame     = new CasinoButton("NEW GAME",    CasinoButton.Style.GOLD);
        btnContinue    = new CasinoButton("CONTINUE",    CasinoButton.Style.PRIMARY);
        btnSettings    = new CasinoButton("SETTINGS",    CasinoButton.Style.NEUTRAL);
        btnLeaderboard = new CasinoButton("LEADERBOARD", CasinoButton.Style.NEUTRAL);
        btnTutorial    = new CasinoButton("HOW TO PLAY", CasinoButton.Style.NEUTRAL);
        btnQuit        = new CasinoButton("QUIT",        CasinoButton.Style.DANGER);

        btnNewGame.setPreferredSize(new Dimension(200, 50));
        btnContinue.setPreferredSize(new Dimension(200, 50));
        btnSettings.setPreferredSize(new Dimension(160, 44));
        btnLeaderboard.setPreferredSize(new Dimension(160, 44));
        btnTutorial.setPreferredSize(new Dimension(160, 44));
        btnQuit.setPreferredSize(new Dimension(100, 44));

        btnNewGame.addActionListener(e -> startNewGame());
        btnContinue.addActionListener(e -> gm.loadExistingPlayer());
        btnSettings.addActionListener(e -> gm.goToSettings());
        btnLeaderboard.addActionListener(e -> gm.goToLeaderboard());
        btnTutorial.addActionListener(e -> gm.goToTutorial());
        btnQuit.addActionListener(e -> {
            gm.shutdown();
            System.exit(0);
        });

        add(btnNewGame); add(btnContinue); add(btnSettings);
        add(btnLeaderboard); add(btnTutorial); add(btnQuit);

        refreshSaveState();
    }

    private void refreshSaveState() {
        boolean hasSave = SaveSystem.getInstance().playerSaveExists();
        btnContinue.setEnabled(hasSave);
        if (hasSave && gm.getPlayer() != null) {
            lblSavedInfo.setText(String.format("Saved: %s  $%.0f",
                    gm.getPlayer().getName(), gm.getPlayer().getBalance()));
        } else if (hasSave) {
            lblSavedInfo.setText("Saved game found — click CONTINUE");
        } else {
            lblSavedInfo.setText("No saved game");
        }
    }

    private void startNewGame() {
        String name = tfPlayerName.getText().trim();
        if (name.isEmpty()) name = "Player";
        gm.createNewPlayer(name);
    }

    private void startAnimation() {
        animTimer = new Timer(32, e -> {
            titlePulse += 0.05f;
            repaint();
        });
        animTimer.start();
    }

    public void stopAnimation() {
        if (animTimer != null) animTimer.stop();
    }

    @Override
    public void doLayout() {
        int w = getWidth(), h = getHeight();
        if (w == 0) return;
        int cx = w / 2;

        // Title painted at ~h*0.22
        lblSubtitle.setBounds(cx - 200, (int)(h * 0.32), 400, 28);
        tfPlayerName.setBounds(cx - 120, (int)(h * 0.42), 240, 36);
        lblSavedInfo.setBounds(cx - 180, (int)(h * 0.49), 360, 20);

        btnNewGame.setBounds(cx - 100, (int)(h * 0.54), 200, 50);
        btnContinue.setBounds(cx - 100, (int)(h * 0.63), 200, 50);

        int row2y = (int)(h * 0.73);
        btnSettings.setBounds(cx - 255, row2y, 160, 44);
        btnLeaderboard.setBounds(cx - 80, row2y, 160, 44);
        btnTutorial.setBounds(cx + 95, row2y, 160, 44);

        btnQuit.setBounds(cx - 50, (int)(h * 0.84), 100, 44);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth(), h = getHeight();

        // Gradient background
        g2.setPaint(new GradientPaint(0, 0, CasinoTheme.darken(CasinoTheme.TABLE_FELT, 0.4f),
                w, h, CasinoTheme.TABLE_BG));
        g2.fillRect(0, 0, w, h);

        // Decorative circles
        g2.setColor(CasinoTheme.withAlpha(CasinoTheme.GOLD, 12));
        g2.fillOval(-100, -100, 400, 400);
        g2.fillOval(w - 200, h - 200, 400, 400);

        // Card suit watermarks
        g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 120));
        g2.setColor(CasinoTheme.withAlpha(CasinoTheme.TABLE_FELT, 60));
        g2.drawString("♠", 20, 180);
        g2.drawString("♥", w - 130, 180);
        g2.drawString("♦", 20, h - 40);
        g2.drawString("♣", w - 130, h - 40);

        // Animated title
        drawTitle(g2, w, h);

        g2.dispose();
    }

    private void drawTitle(Graphics2D g2, int w, int h) {
        int titleY = (int)(h * 0.25);
        float pulse = (float)(1.0 + 0.04 * Math.sin(titlePulse));

        g2.setFont(new Font("Georgia", Font.BOLD, (int)(48 * pulse)));
        FontMetrics fm = g2.getFontMetrics();
        String title = "BLACKJACK";
        int tx = (w - fm.stringWidth(title)) / 2;

        // Gold shadow
        g2.setColor(CasinoTheme.withAlpha(CasinoTheme.darken(CasinoTheme.GOLD, 0.4f), 180));
        g2.drawString(title, tx + 3, titleY + 3);

        // Gradient gold text
        g2.setPaint(new GradientPaint(tx, titleY - 40, CasinoTheme.lighten(CasinoTheme.GOLD, 0.3f),
                tx, titleY + 10, CasinoTheme.darken(CasinoTheme.GOLD, 0.2f)));
        g2.drawString(title, tx, titleY);

        // Underline accent
        int uw = fm.stringWidth(title);
        g2.setPaint(new GradientPaint(tx, 0, new Color(0, 0, 0, 0),
                tx + uw / 2, 0, CasinoTheme.GOLD));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(tx, titleY + 10, tx + uw, titleY + 10);
    }

    @Override
    public void onGameEvent(GameEvent event, Object payload) {
        SwingUtilities.invokeLater(() -> {
            if (event == GameEvent.LOAD_SUCCESS || event == GameEvent.SAVE_SUCCESS) {
                refreshSaveState();
            }
        });
    }
}
