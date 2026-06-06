

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The main gameplay window.
 * Composes the BoardPanel (view) with a HUD showing scores, timer, and controls.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class GameScreen extends JFrame implements GameEventListener {

    private final JFrame parent;
    private final GameBoard gameBoard;
    private BoardPanel boardPanel;

    // ── HUD labels ────────────────────────────────────────────────────────────
    private JLabel statusLabel;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JLabel turnIndicator;

    // ── Timer visual ─────────────────────────────────────────────────────────
    private int timerSeconds;

    public GameScreen(JFrame parent, GameBoard gameBoard) {
        this.parent    = parent;
        this.gameBoard = gameBoard;

        setTitle("TicTacToe Pro  ·  " + gameBoard.getPlayerX().getName()
                + " vs " + gameBoard.getPlayerO().getName());
        setSize(720, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(560, 580));

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { exitGame(); }
        });

        buildUI();
        setupKeyBindings();
        gameBoard.addListener(this);
    }

    // ── UI Construction ───────────────────────────────────────────────────────

    private void buildUI() {
        ThemeManager.Theme t = ThemeManager.getInstance().getTheme();

        JPanel root = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) { UIUtils.paintGradientBackground(g, this); }
        };
        root.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // ── Top HUD ───────────────────────────────────────────────────────────
        JPanel topHud = buildTopHud(t);
        root.add(topHud, BorderLayout.NORTH);

        // ── Board ─────────────────────────────────────────────────────────────
        boardPanel = new BoardPanel(gameBoard);
        JPanel boardWrapper = new JPanel(new GridBagLayout()) { { setOpaque(false); } };
        boardWrapper.add(boardPanel);
        root.add(boardWrapper, BorderLayout.CENTER);

        // ── Bottom Controls ───────────────────────────────────────────────────
        JPanel bottomPanel = buildBottomPanel(t);
        root.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(root);
        refreshHUD();
    }

    private JPanel buildTopHud(ThemeManager.Theme t) {
        JPanel hud = new JPanel(new BorderLayout(12, 0)) { { setOpaque(false); } };
        hud.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // Left: player X info
        JPanel leftInfo = buildPlayerInfo(gameBoard.getPlayerX(), t);
        hud.add(leftInfo, BorderLayout.WEST);

        // Centre: status + turn indicator
        JPanel centrePanel = new JPanel(new GridLayout(3, 1, 0, 2)) { { setOpaque(false); } };

        turnIndicator = new JLabel("X's Turn", SwingConstants.CENTER);
        turnIndicator.setFont(ThemeManager.FONT_HEADING);
        turnIndicator.setForeground(t.accent);

        statusLabel = new JLabel("Game in progress", SwingConstants.CENTER);
        statusLabel.setFont(ThemeManager.FONT_BODY);
        statusLabel.setForeground(t.textSecondary);

        timerLabel = new JLabel("30", SwingConstants.CENTER);
        timerLabel.setFont(ThemeManager.FONT_HEADING.deriveFont(28f));
        timerLabel.setForeground(t.accent);

        centrePanel.add(turnIndicator);
        centrePanel.add(statusLabel);
        centrePanel.add(timerLabel);
        hud.add(centrePanel, BorderLayout.CENTER);

        // Right: player O info
        JPanel rightInfo = buildPlayerInfo(gameBoard.getPlayerO(), t);
        hud.add(rightInfo, BorderLayout.EAST);

        return hud;
    }

    private JPanel buildPlayerInfo(Player p, ThemeManager.Theme t) {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 2)) { { setOpaque(false); } };
        panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JLabel nameLbl = new JLabel(p.getAvatarCode() + "  " + p.getName(), SwingConstants.CENTER);
        nameLbl.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        nameLbl.setForeground(p.getSymbol() == 'X' ? t.symbolX : t.symbolO);

        JLabel typeLbl = new JLabel(
                "[" + p.getSymbol() + "]  " + p.getType().name().replace("_", " ").toLowerCase(),
                SwingConstants.CENTER);
        typeLbl.setFont(ThemeManager.FONT_SMALL);
        typeLbl.setForeground(t.textSecondary);

        scoreLabel = new JLabel(p.getStatsDisplay(), SwingConstants.CENTER);
        scoreLabel.setFont(ThemeManager.FONT_SMALL);
        scoreLabel.setForeground(t.textSecondary);
        scoreLabel.setName("score-" + p.getSymbol());

        panel.add(nameLbl);
        panel.add(typeLbl);
        panel.add(scoreLabel);
        return panel;
    }

    private JPanel buildBottomPanel(ThemeManager.Theme t) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)) { { setOpaque(false); } };
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JButton undoBtn    = UIUtils.createStyledButton("↩ Undo",    false);
        JButton redoBtn    = UIUtils.createStyledButton("↪ Redo",    false);
        JButton pauseBtn   = UIUtils.createStyledButton("⏸ Pause",   false);
        JButton restartBtn = UIUtils.createStyledButton("↺ Restart", false);
        JButton menuBtn    = UIUtils.createStyledButton("⌂ Menu",    false);

        for (JButton b : new JButton[]{undoBtn, redoBtn, pauseBtn, restartBtn, menuBtn})
            b.setPreferredSize(new Dimension(118, 38));

        undoBtn.addActionListener(e -> {
            SoundManager.getInstance().playClick();
            gameBoard.undo();
            refreshHUD();
        });
        redoBtn.addActionListener(e -> {
            SoundManager.getInstance().playClick();
            gameBoard.redo();
            refreshHUD();
        });
        pauseBtn.addActionListener(e -> togglePause(pauseBtn));
        restartBtn.addActionListener(e -> {
            if (UIUtils.confirm(this, "Restart this match?", "Restart")) {
                SoundManager.getInstance().playClick();
                gameBoard.startNewGame();
            }
        });
        menuBtn.addActionListener(e -> exitGame());

        panel.add(undoBtn); panel.add(redoBtn); panel.add(pauseBtn);
        panel.add(restartBtn); panel.add(menuBtn);
        return panel;
    }

    // ── GameEventListener ─────────────────────────────────────────────────────

    @Override
    public void onGameEvent(GameEvent event, int[] data) {
        switch (event) {
            case MOVE_MADE, GAME_STARTED -> refreshHUD();
            case TIMER_TICK -> {
                if (data != null) {
                    timerSeconds = data[0];
                    updateTimerDisplay();
                }
            }
            case AI_THINKING -> {
                turnIndicator.setText("🤖 AI thinking...");
                statusLabel.setText("Please wait");
            }
            case GAME_OVER -> {
                refreshHUD();
                showGameOverDialog();
            }
            case GAME_PAUSED -> statusLabel.setText("⏸  PAUSED");
            case GAME_RESUMED -> statusLabel.setText("Game in progress");
        }
    }

    // ── HUD Updates ───────────────────────────────────────────────────────────

    private void refreshHUD() {
        ThemeManager.Theme t = ThemeManager.getInstance().getTheme();
        var state = gameBoard.getState();
        if (state == null) return;

        if (!state.isGameOver()) {
            Player cur = gameBoard.getCurrentPlayer();
            boolean isX = cur.getSymbol() == 'X';
            turnIndicator.setForeground(isX ? t.symbolX : t.symbolO);
            turnIndicator.setText(cur.getAvatarCode() + "  " + cur.getName() + "'s Turn");
            statusLabel.setText("Move #" + (state.getMoveCount() + 1) + "  ·  "
                    + state.getBoardSize() + "×" + state.getBoardSize());
        } else {
            char winner = state.getWinner();
            if (winner == 'D') {
                turnIndicator.setText("🤝  Draw!");
                turnIndicator.setForeground(t.accentSecondary);
            } else {
                Player w = (winner == 'X') ? gameBoard.getPlayerX() : gameBoard.getPlayerO();
                turnIndicator.setText("🏆  " + w.getName() + " Wins!");
                turnIndicator.setForeground(t.winHighlight);
            }
            statusLabel.setText("Game over  ·  " + state.getMoveCount() + " moves");
        }

        timerSeconds = gameBoard.getSecondsRemaining();
        updateTimerDisplay();
    }

    private void updateTimerDisplay() {
        ThemeManager.Theme t = ThemeManager.getInstance().getTheme();
        timerLabel.setText(String.valueOf(timerSeconds));
        if      (timerSeconds <= 5)  timerLabel.setForeground(t.timerDanger);
        else if (timerSeconds <= 10) timerLabel.setForeground(t.timerWarning);
        else                         timerLabel.setForeground(t.accent);
    }

    // ── Pause ─────────────────────────────────────────────────────────────────

    private void togglePause(JButton pauseBtn) {
        SoundManager.getInstance().playClick();
        if (gameBoard.isPaused()) {
            gameBoard.resume();
            pauseBtn.setText("⏸ Pause");
        } else {
            gameBoard.pause();
            pauseBtn.setText("▶ Resume");
        }
    }

    // ── Game Over Dialog ──────────────────────────────────────────────────────

    private void showGameOverDialog() {
        var state = gameBoard.getState();
        char w = state.getWinner();
        String title, msg;

        if (w == 'D') {
            title = "🤝  Draw!";
            msg   = "No winner this time — perfectly matched!";
        } else {
            Player winner = (w == 'X') ? gameBoard.getPlayerX() : gameBoard.getPlayerO();
            title = "🏆  " + winner.getName() + " Wins!";
            msg   = winner.getName() + " has won the match in " + state.getMoveCount() + " moves!";
        }

        String[] options = {"Play Again", "New Match", "Main Menu"};
        int choice = JOptionPane.showOptionDialog(this, msg, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        switch (choice) {
            case 0 -> { SoundManager.getInstance().playClick(); gameBoard.startNewGame(); }
            case 1 -> openSetup();
            default -> exitGame();
        }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void openSetup() {
        boardPanel.cleanup();
        gameBoard.shutdown();
        parent.setVisible(true);
        dispose();
    }

    private void exitGame() {
        if (gameBoard.getState() != null && !gameBoard.getState().isGameOver()
                && !gameBoard.getState().isEmpty()) {
            if (!UIUtils.confirm(this, "Quit to menu? Current game will be lost.", "Quit")) return;
        }
        boardPanel.cleanup();
        gameBoard.shutdown();
        parent.setVisible(true);
        dispose();
    }

    // ── Keyboard Shortcuts ────────────────────────────────────────────────────

    private void setupKeyBindings() {
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke("control Z"), "undo");
        am.put("undo", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { gameBoard.undo(); refreshHUD(); }
        });

        im.put(KeyStroke.getKeyStroke("control Y"), "redo");
        am.put("redo", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { gameBoard.redo(); refreshHUD(); }
        });

        im.put(KeyStroke.getKeyStroke("ESCAPE"), "pause");
        am.put("pause", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (gameBoard.isPaused()) gameBoard.resume(); else gameBoard.pause();
            }
        });

        im.put(KeyStroke.getKeyStroke("F5"), "restart");
        am.put("restart", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { gameBoard.startNewGame(); }
        });
    }
}