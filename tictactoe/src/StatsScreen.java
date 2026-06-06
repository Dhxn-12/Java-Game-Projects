

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Statistics dashboard showing wins, losses, draws, and win percentages
 * across all registered players from match history.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class StatsScreen extends JDialog {

    public StatsScreen(JFrame parent) {
        super(parent, "Statistics Dashboard", true);
        setSize(560, 500);
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

        JLabel title = UIUtils.createGlowLabel("📊  Statistics Dashboard", ThemeManager.FONT_HEADING, t.accent);
        root.add(title, BorderLayout.NORTH);

        // Aggregate stats per player
        List<MatchRecord> history = ScoreManager.getInstance().getMatchHistory();
        Map<String, int[]> stats = new LinkedHashMap<>(); // name -> [wins, losses, draws]

        for (MatchRecord r : history) {
            stats.computeIfAbsent(r.getPlayerXName(), k -> new int[3]);
            stats.computeIfAbsent(r.getPlayerOName(), k -> new int[3]);
            char w = r.getWinner();
            if (w == 'X') { stats.get(r.getPlayerXName())[0]++; stats.get(r.getPlayerOName())[1]++; }
            else if (w == 'O') { stats.get(r.getPlayerOName())[0]++; stats.get(r.getPlayerXName())[1]++; }
            else { stats.get(r.getPlayerXName())[2]++; stats.get(r.getPlayerOName())[2]++; }
        }

        JPanel statsPanel = new JPanel(new GridLayout(0, 1, 0, 10)) { { setOpaque(false); } };

        if (stats.isEmpty()) {
            JLabel empty = new JLabel("No stats yet! Play some games first.", SwingConstants.CENTER);
            empty.setFont(ThemeManager.FONT_BODY);
            empty.setForeground(t.textSecondary);
            statsPanel.add(empty);
        } else {
            for (Map.Entry<String, int[]> entry : stats.entrySet()) {
                statsPanel.add(buildPlayerCard(entry.getKey(), entry.getValue(), t));
            }
        }

        JScrollPane scroll = new JScrollPane(statsPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);

        // Summary
        JLabel summary = new JLabel(
                String.format("  Total matches played: %d", history.size()), SwingConstants.LEFT);
        summary.setFont(ThemeManager.FONT_SMALL);
        summary.setForeground(t.textSecondary);

        JButton closeBtn = UIUtils.createStyledButton("✕  Close", false);
        closeBtn.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new BorderLayout()) { { setOpaque(false); } };
        bottom.add(summary, BorderLayout.WEST);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)) { { setOpaque(false); } };
        btnRow.add(closeBtn);
        bottom.add(btnRow, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel buildPlayerCard(String name, int[] wld, ThemeManager.Theme t) {
        int total = wld[0] + wld[1] + wld[2];
        double pct = total == 0 ? 0 : 100.0 * wld[0] / total;

        JPanel card = new JPanel(new BorderLayout(12, 4)) {
            @Override protected void paintComponent(Graphics g) {
                ThemeManager.Theme th = ThemeManager.getInstance().getTheme();
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);
                g2.setColor(th.surface);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(th.gridColor);
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(ThemeManager.FONT_BODY.deriveFont(Font.BOLD));
        nameLbl.setForeground(t.accent);

        JLabel statsLbl = new JLabel(String.format(
                "W: %d  |  L: %d  |  D: %d  |  Total: %d", wld[0], wld[1], wld[2], total));
        statsLbl.setFont(ThemeManager.FONT_SMALL);
        statsLbl.setForeground(t.textSecondary);

        // Win rate bar
        JPanel barPanel = new JPanel(null) {
            @Override public Dimension getPreferredSize() { return new Dimension(0, 10); }
            @Override protected void paintComponent(Graphics g) {
                ThemeManager.Theme th = ThemeManager.getInstance().getTheme();
                Graphics2D g2 = (Graphics2D) g.create();
                UIUtils.enableAntiAliasing(g2);
                g2.setColor(th.backgroundAlt);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                int fillW = (int)(getWidth() * pct / 100);
                if (fillW > 0) {
                    GradientPaint gp = new GradientPaint(0, 0, th.accent, fillW, 0, th.accentSecondary);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, fillW, getHeight(), 6, 6);
                }
                g2.dispose();
            }
        };
        barPanel.setOpaque(false);

        JLabel pctLbl = new JLabel(String.format("%.1f%% win rate", pct), SwingConstants.RIGHT);
        pctLbl.setFont(ThemeManager.FONT_SMALL);
        pctLbl.setForeground(t.accent);

        JPanel top = new JPanel(new BorderLayout()) { { setOpaque(false); } };
        top.add(nameLbl, BorderLayout.WEST);
        top.add(pctLbl,  BorderLayout.EAST);

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 4)) { { setOpaque(false); } };
        info.add(top);
        info.add(statsLbl);
        info.add(barPanel);

        card.add(info, BorderLayout.CENTER);
        return card;
    }
}