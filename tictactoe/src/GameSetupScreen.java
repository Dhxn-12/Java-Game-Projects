

import javax.swing.*;
import java.awt.*;

/**
 * Pre-game setup screen: enter player names, choose AI difficulty and board size.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class GameSetupScreen extends JFrame {

    private final JFrame parent;
    private final boolean vsAI;

    private JTextField nameXField;
    private JTextField nameOField;
    private JComboBox<String> difficultyBox;
    private JComboBox<String> boardSizeBox;
    private JSpinner timerSpinner;

    public GameSetupScreen(JFrame parent, boolean vsAI) {
        this.parent = parent;
        this.vsAI   = vsAI;

        setTitle(vsAI ? "Player vs AI Setup" : "Player vs Player Setup");
        setSize(480, 520);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        buildUI();
    }

    private void buildUI() {
        ThemeManager.Theme t = ThemeManager.getInstance().getTheme();

        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) { UIUtils.paintGradientBackground(g, this); }
        };
        root.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Title
        JLabel title = UIUtils.createGlowLabel(vsAI ? "🤖  vs  AI" : "👥  Player vs Player",
                ThemeManager.FONT_HEADING, t.accent);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        root.add(title, BorderLayout.NORTH);

        // Form panel
        JPanel form = new JPanel(new GridBagLayout()) { { setOpaque(false); } };
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 4, 8, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Player X name
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0.35;
        form.add(label("⚔  Player X Name:", t), gbc);
        nameXField = UIUtils.createStyledTextField("Enter name...");
        nameXField.setText("Player 1");
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(nameXField, gbc);

        // Player O name / AI label
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.35;
        form.add(label(vsAI ? "🤖  AI Name:" : "🛡  Player O Name:", t), gbc);
        nameOField = UIUtils.createStyledTextField("Enter name...");
        nameOField.setText(vsAI ? "AI Opponent" : "Player 2");
        nameOField.setEnabled(!vsAI);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(nameOField, gbc);

        // AI difficulty (only for vsAI)
        if (vsAI) {
            gbc.gridy = 2; gbc.gridx = 0;
            form.add(label("🎯  Difficulty:", t), gbc);
            difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
            difficultyBox.setSelectedIndex(1);
            styleCombo(difficultyBox, t);
            gbc.gridx = 1;
            form.add(difficultyBox, gbc);
        }

        // Board size
        gbc.gridy = 3; gbc.gridx = 0;
        form.add(label("📐  Board Size:", t), gbc);
        boardSizeBox = new JComboBox<>(new String[]{"3×3 (Classic)", "4×4 (Extended)", "5×5 (Expert)"});
        styleCombo(boardSizeBox, t);
        gbc.gridx = 1;
        form.add(boardSizeBox, gbc);

        // Turn timer
        gbc.gridy = 4; gbc.gridx = 0;
        form.add(label("⏱  Turn Timer (s):", t), gbc);
        timerSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 120, 5));
        timerSpinner.setFont(ThemeManager.FONT_BODY);
        timerSpinner.setOpaque(false);
        gbc.gridx = 1;
        form.add(timerSpinner, gbc);

        root.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)) { { setOpaque(false); } };
        JButton startBtn = UIUtils.createStyledButton("▶  Start Game", true);
        JButton backBtn  = UIUtils.createStyledButton("◀  Back",        false);

        startBtn.addActionListener(e -> startGame());
        backBtn.addActionListener(e -> {
            SoundManager.getInstance().playClick();
            parent.setVisible(true);
            dispose();
        });

        btnPanel.add(backBtn);
        btnPanel.add(startBtn);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void startGame() {
        SoundManager.getInstance().playClick();

        String nameX = nameXField.getText().trim();
        String nameO = nameOField.getText().trim();
        if (nameX.isEmpty()) nameX = "Player 1";
        if (nameO.isEmpty()) nameO = vsAI ? "AI" : "Player 2";

        int size = boardSizeBox.getSelectedIndex() + 3;
        int timerSec = (int) timerSpinner.getValue();

        GameBoard board = new GameBoard();
            board.setTurnTimeLimit(timerSec);

        if (vsAI) {
            Player.PlayerType diff = switch (difficultyBox.getSelectedIndex()) {
                case 0 -> Player.PlayerType.AI_EASY;
                case 2 -> Player.PlayerType.AI_HARD;
                default -> Player.PlayerType.AI_MEDIUM;
            };
            board.configurePvAI(nameX, diff, size);
        } else {
            board.configurePvP(nameX, nameO, size);
        }

        board.startNewGame();

        GameScreen gameScreen = new GameScreen(this, board);
        gameScreen.setVisible(true);
        setVisible(false);
    }

    private JLabel label(String text, ThemeManager.Theme t) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.FONT_BODY);
        l.setForeground(t.textPrimary);
        return l;
    }

    private void styleCombo(JComboBox<?> cb, ThemeManager.Theme t) {
        cb.setFont(ThemeManager.FONT_BODY);
        cb.setBackground(t.surface);
        cb.setForeground(t.textPrimary);
        cb.setBorder(new UIUtils.RoundBorder(t.buttonBorder, 6));
    }
}