
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Displays full match history in a styled table with summary stats.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class HistoryScreen extends JDialog {

    public HistoryScreen(JFrame parent) {
        super(parent, "Match History", true);
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setResizable(true);
        buildUI();
    }

    private void buildUI() {
        ThemeManager.Theme t = ThemeManager.getInstance().getTheme();

        JPanel root = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) { UIUtils.paintGradientBackground(g, this); }
        };
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        // Title
        JLabel title = UIUtils.createGlowLabel("📋  Match History", ThemeManager.FONT_HEADING, t.accent);
        root.add(title, BorderLayout.NORTH);

        // Table
        List<MatchRecord> history = ScoreManager.getInstance().getMatchHistory();
        String[] cols = {"#", "Date", "Player X", "Player O", "Winner", "Mode", "Moves", "Duration"};
        Object[][] rows = new Object[history.size()][cols.length];

        for (int i = 0; i < history.size(); i++) {
            MatchRecord r = history.get(i);
            rows[i] = new Object[]{
                i + 1,
                r.getTimestamp(),
                r.getPlayerXName(),
                r.getPlayerOName(),
                r.getWinnerName(),
                r.getGameMode(),
                r.getTotalMoves(),
                r.getDurationDisplay()
            };
        }

        JTable table = new JTable(rows, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                ThemeManager.Theme th = ThemeManager.getInstance().getTheme();
                c.setBackground(isRowSelected(row) ? th.accent : (row % 2 == 0 ? th.surface : th.backgroundAlt));
                c.setForeground(isRowSelected(row) ? th.background : th.textPrimary);
                return c;
            }
        };

        table.setFont(ThemeManager.FONT_SMALL);
        table.setRowHeight(26);
        table.getTableHeader().setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(t.surface);
        table.getTableHeader().setForeground(t.accent);
        table.setGridColor(t.gridColor);
        table.setBackground(t.background);
        table.setForeground(t.textPrimary);
        table.setSelectionBackground(t.accent);
        table.setSelectionForeground(t.background);
        table.setFillsViewportHeight(true);

        // Auto-resize columns
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setMaxWidth(35);
        cm.getColumn(6).setMaxWidth(55);
        cm.getColumn(7).setMaxWidth(80);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(t.background);
        scroll.getViewport().setBackground(t.background);
        scroll.setBorder(new UIUtils.RoundBorder(t.gridColor, 8));
        root.add(scroll, BorderLayout.CENTER);

        // Empty state
        if (history.isEmpty()) {
            JLabel empty = new JLabel("No matches played yet. Start a game!", SwingConstants.CENTER);
            empty.setFont(ThemeManager.FONT_BODY);
            empty.setForeground(t.textSecondary);
            root.add(empty, BorderLayout.CENTER);
        }

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0)) { { setOpaque(false); } };
        JButton clearBtn = UIUtils.createStyledButton("🗑  Clear History", false);
        JButton closeBtn = UIUtils.createStyledButton("✕  Close", false);

        clearBtn.addActionListener(e -> {
            if (UIUtils.confirm(this, "Clear all match history?", "Clear")) {
                ScoreManager.getInstance().clearHistory();
                dispose();
            }
        });
        closeBtn.addActionListener(e -> dispose());
        btnRow.add(clearBtn); btnRow.add(closeBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
