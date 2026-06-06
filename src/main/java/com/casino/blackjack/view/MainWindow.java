package com.casino.blackjack.view;

import com.casino.blackjack.controller.GameManager;
import com.casino.blackjack.manager.AchievementManager;
import com.casino.blackjack.model.GameEvent;
import com.casino.blackjack.model.GameObserver;
import com.casino.blackjack.model.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Root Swing JFrame — the application window.
 *
 * Acts as the UIManager: swaps card panels based on GameState,
 * shows dialogs (insurance, daily reward) and achievement toasts.
 *
 * Uses a CardLayout to switch between all game screens.
 */
public class MainWindow extends JFrame implements GameObserver {

    private static final String SCREEN_MENU        = "MENU";
    private static final String SCREEN_GAME        = "GAME";
    private static final String SCREEN_SETTINGS    = "SETTINGS";
    private static final String SCREEN_LEADERBOARD = "LEADERBOARD";
    private static final String SCREEN_STATISTICS  = "STATISTICS";
    private static final String SCREEN_TUTORIAL    = "TUTORIAL";
    private static final String SCREEN_GAMEOVER    = "GAMEOVER";

    private final GameManager gm = GameManager.getInstance();
    private CardLayout cardLayout;
    private JPanel cardContainer;

    // Screens (lazily created)
    private MainMenuPanel    menuPanel;
    private GameTablePanel   gamePanel;
    private SettingsPanel    settingsPanel;
    private LeaderboardPanel leaderboardPanel;
    private StatisticsPanel  statisticsPanel;
    private TutorialPanel    tutorialPanel;
    private GameOverPanel    gameOverPanel;

    public MainWindow() {
        super("Blackjack — Claude's Casino");
        setupFrame();
        buildLayout();
        gm.addObserver(this);
        showScreen(SCREEN_MENU);
    }

    // ── Frame setup ───────────────────────────────────────────────────────────

    private void setupFrame() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    MainWindow.this,
                    "Save game and quit?", "Quit",
                    JOptionPane.YES_NO_CANCEL_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    gm.saveGame();
                    gm.shutdown();
                    System.exit(0);
                } else if (choice == JOptionPane.NO_OPTION) {
                    gm.shutdown();
                    System.exit(0);
                }
            }
        });

        setMinimumSize(new Dimension(900, 680));
        setPreferredSize(new Dimension(1100, 760));

        // App icon (drawn programmatically)
        setIconImage(createAppIcon());

        // System L&F
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        // Dark global defaults
        UIManager.put("TabbedPane.selected",          CasinoTheme.TABLE_FELT);
        UIManager.put("TabbedPane.background",        CasinoTheme.TABLE_BG);
        UIManager.put("TabbedPane.foreground",        CasinoTheme.TEXT_PRIMARY);
        UIManager.put("TabbedPane.darkShadow",        CasinoTheme.TABLE_TRIM);
        UIManager.put("ScrollPane.background",        CasinoTheme.TABLE_BG);
        UIManager.put("ComboBox.background",          CasinoTheme.TABLE_FELT);
        UIManager.put("ComboBox.foreground",          CasinoTheme.TEXT_PRIMARY);
        UIManager.put("Spinner.background",           CasinoTheme.TABLE_FELT);

        pack();
        setLocationRelativeTo(null);
    }

    private Image createAppIcon() {
        int sz = 64;
        java.awt.image.BufferedImage img =
            new java.awt.image.BufferedImage(sz, sz, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0x1E4A1E));
        g2.fillRoundRect(0, 0, sz, sz, 14, 14);
        g2.setColor(new Color(0xD4AF37));
        g2.setFont(new Font("Georgia", Font.BOLD, 40));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("BJ", (sz - fm.stringWidth("BJ")) / 2, 46);
        g2.dispose();
        return img;
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private void buildLayout() {
        cardLayout    = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(CasinoTheme.TABLE_BG);

        menuPanel        = new MainMenuPanel();
        gamePanel        = new GameTablePanel();
        settingsPanel    = new SettingsPanel();
        leaderboardPanel = new LeaderboardPanel();
        statisticsPanel  = new StatisticsPanel();
        tutorialPanel    = new TutorialPanel();
        gameOverPanel    = new GameOverPanel();

        cardContainer.add(menuPanel,        SCREEN_MENU);
        cardContainer.add(gamePanel,        SCREEN_GAME);
        cardContainer.add(settingsPanel,    SCREEN_SETTINGS);
        cardContainer.add(leaderboardPanel, SCREEN_LEADERBOARD);
        cardContainer.add(statisticsPanel,  SCREEN_STATISTICS);
        cardContainer.add(tutorialPanel,    SCREEN_TUTORIAL);
        cardContainer.add(gameOverPanel,    SCREEN_GAMEOVER);

        // Menu bar
        setJMenuBar(buildMenuBar());
        setContentPane(cardContainer);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.setBackground(CasinoTheme.TABLE_TRIM);

        JMenu mGame = menu("Game");
        mGame.add(menuItem("New Game",      e -> gm.goToMainMenu()));
        mGame.add(menuItem("Save Game",     e -> { gm.saveGame(); showToastMsg("Game saved!"); }));
        mGame.add(menuItem("Statistics",    e -> gm.goToStatistics()));
        mGame.add(menuItem("Leaderboard",   e -> gm.goToLeaderboard()));
        mGame.addSeparator();
        mGame.add(menuItem("Quit",          e -> { gm.shutdown(); System.exit(0); }));

        JMenu mView = menu("View");
        for (CasinoTheme.Theme t : CasinoTheme.Theme.values()) {
            mView.add(menuItem(t.name().replace('_', ' '), e -> {
                CasinoTheme.applyTheme(t);
                gm.getSettings().setTheme(t.name());
                SwingUtilities.updateComponentTreeUI(this);
                repaint();
            }));
        }
        mView.addSeparator();
        mView.add(menuItem("Toggle Fullscreen", e -> toggleFullscreen()));

        JMenu mHelp = menu("Help");
        mHelp.add(menuItem("How to Play",   e -> gm.goToTutorial()));
        mHelp.add(menuItem("About",         e -> showAbout()));

        mb.add(mGame); mb.add(mView); mb.add(mHelp);
        return mb;
    }

    private JMenu menu(String text) {
        JMenu m = new JMenu(text);
        m.setForeground(CasinoTheme.TEXT_PRIMARY);
        m.setFont(CasinoTheme.FONT_BUTTON);
        return m;
    }

    private JMenuItem menuItem(String text, java.awt.event.ActionListener al) {
        JMenuItem mi = new JMenuItem(text);
        mi.setFont(CasinoTheme.FONT_BODY);
        mi.addActionListener(al);
        return mi;
    }

    // ── Screen switching ─────────────────────────────────────────────────────

    private void showScreen(String screen) {
        cardLayout.show(cardContainer, screen);
    }

    private void refreshScreen(GameState state) {
        switch (state) {
            case MAIN_MENU   -> {
                settingsPanel    = new SettingsPanel();
                leaderboardPanel = new LeaderboardPanel();
                statisticsPanel  = new StatisticsPanel();
                cardContainer.add(settingsPanel,    SCREEN_SETTINGS);
                cardContainer.add(leaderboardPanel, SCREEN_LEADERBOARD);
                cardContainer.add(statisticsPanel,  SCREEN_STATISTICS);
                showScreen(SCREEN_MENU);
            }
            case PLACING_BET, DEALING, PLAYER_TURN,
                 PLAYER_TURN_SPLIT, DEALER_TURN, ROUND_OVER -> showScreen(SCREEN_GAME);
            case SETTINGS    -> showScreen(SCREEN_SETTINGS);
            case LEADERBOARD -> showScreen(SCREEN_LEADERBOARD);
            case STATISTICS  -> showScreen(SCREEN_STATISTICS);
            case TUTORIAL    -> showScreen(SCREEN_TUTORIAL);
            case GAME_OVER   -> {
                gameOverPanel = new GameOverPanel();
                cardContainer.add(gameOverPanel, SCREEN_GAMEOVER);
                showScreen(SCREEN_GAMEOVER);
            }
            default -> showScreen(SCREEN_MENU);
        }
    }

    // ── Observer ──────────────────────────────────────────────────────────────

    @Override
    public void onGameEvent(GameEvent event, Object payload) {
        SwingUtilities.invokeLater(() -> {
            switch (event) {
                case STATE_CHANGED -> {
                    GameState s = (GameState) payload;
                    refreshScreen(s);
                    if (s == GameState.SETTINGS) {
                        boolean fs = gm.getSettings().isFullscreen();
                        if (fs != isUndecorated()) applyFullscreen(fs);
                    }
                }
                case INSURANCE_OFFERED -> {
                    InsuranceDialog dlg = new InsuranceDialog(this);
                    dlg.setVisible(true);
                }
                case DAILY_REWARD_AVAILABLE -> {
                    DailyRewardDialog dlg = new DailyRewardDialog(this);
                    dlg.setVisible(true);
                }
                case ACHIEVEMENT_UNLOCKED -> {
                    AchievementManager.Achievement a = (AchievementManager.Achievement) payload;
                    new AchievementToast(this, a).setVisible(true);
                }
                case SAVE_SUCCESS -> showToastMsg("Game saved ✓");
                default -> {}
            }
        });
    }

    // ── Fullscreen ────────────────────────────────────────────────────────────

    private void toggleFullscreen() {
        applyFullscreen(!isUndecorated());
    }

    private void applyFullscreen(boolean enable) {
        dispose();
        setUndecorated(enable);
        if (enable) {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice();
            gd.setFullScreenWindow(this);
        } else {
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().setFullScreenWindow(null);
            setSize(1100, 760);
            setLocationRelativeTo(null);
        }
        setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void showToastMsg(String msg) {
        JWindow toast = new JWindow(this);
        JLabel lbl = new JLabel("  " + msg + "  ", SwingConstants.CENTER);
        lbl.setFont(CasinoTheme.FONT_BODY);
        lbl.setForeground(CasinoTheme.GOLD);
        lbl.setBackground(CasinoTheme.TABLE_TRIM);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createLineBorder(CasinoTheme.GOLD, 1));
        toast.add(lbl);
        toast.pack();
        toast.setLocation(getX() + getWidth() / 2 - toast.getWidth() / 2,
                          getY() + getHeight() - 80);
        toast.setVisible(true);
        new Timer(2000, e -> toast.dispose()) {{ setRepeats(false); start(); }};
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            "<html><center><b>Claude's Casino Blackjack</b><br>" +
            "Version 1.0.0<br><br>" +
            "A professional casino-style Blackjack game<br>" +
            "built with Java Swing.<br><br>" +
            "Patterns: Singleton, Observer, MVC, Factory<br>" +
            "Features: 21 achievements, 4 themes,<br>" +
            "basic strategy hints, animated cards,<br>" +
            "synthesised sound, daily rewards.</center></html>",
            "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
