package com.casino.blackjack.view;

import com.casino.blackjack.controller.GameManager;
import com.casino.blackjack.save.SaveSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Leaderboard screen showing top players by balance.
 */
public class LeaderboardPanel extends JPanel {

    private final GameManager gm = GameManager.getInstance();

    public LeaderboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(CasinoTheme.TABLE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        buildUI();
    }

    private void buildUI() {
        // Title
        JLabel title = new JLabel("LEADERBOARD", SwingConstants.CENTER);
        title.setFont(CasinoTheme.FONT_TITLE);
        title.setForeground(CasinoTheme.GOLD);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"#", "Player", "Balance", "Wins", "Win Rate", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<SaveSystem.LeaderboardEntry> lb = SaveSystem.getInstance().loadLeaderboard();
        for (int i = 0; i < lb.size(); i++) {
            SaveSystem.LeaderboardEntry e = lb.get(i);
            String medal = switch (i) { case 0 -> "🥇"; case 1 -> "🥈"; case 2 -> "🥉"; default -> String.valueOf(i + 1); };
            model.addRow(new Object[]{
                medal, e.getName(),
                String.format("$%.0f", e.getBalance()),
                e.getWins(),
                String.format("%.1f%%", e.getWinRate()),
                e.getTimestamp()
            });
        }

        if (lb.isEmpty()) {
            model.addRow(new Object[]{"—", "No entries yet", "—", "—", "—", "—"});
        }

        JTable table = new JTable(model);
        table.setBackground(CasinoTheme.TABLE_FELT);
        table.setForeground(CasinoTheme.TEXT_PRIMARY);
        table.setFont(CasinoTheme.FONT_BODY);
        table.setGridColor(CasinoTheme.withAlpha(CasinoTheme.GOLD, 40));
        table.setRowHeight(28);
        table.setShowVerticalLines(false);

        // Gold top-3 rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setOpaque(true);
                if (row == 0)      { setBackground(CasinoTheme.withAlpha(new Color(0xFFD700), 60)); setForeground(Color.WHITE); }
                else if (row == 1) { setBackground(CasinoTheme.withAlpha(new Color(0xC0C0C0), 40)); setForeground(CasinoTheme.TEXT_PRIMARY); }
                else if (row == 2) { setBackground(CasinoTheme.withAlpha(new Color(0xCD7F32), 40)); setForeground(CasinoTheme.TEXT_PRIMARY); }
                else               { setBackground(CasinoTheme.TABLE_FELT); setForeground(CasinoTheme.TEXT_PRIMARY); }
                setFont(CasinoTheme.FONT_BODY);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        table.getTableHeader().setBackground(CasinoTheme.TABLE_TRIM);
        table.getTableHeader().setForeground(CasinoTheme.GOLD);
        table.getTableHeader().setFont(CasinoTheme.FONT_BUTTON);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(CasinoTheme.TABLE_FELT);
        scroll.setBorder(BorderFactory.createLineBorder(CasinoTheme.withAlpha(CasinoTheme.GOLD, 80)));
        add(scroll, BorderLayout.CENTER);

        // Save current player to leaderboard button + back
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        south.setOpaque(false);

        CasinoButton btnSubmit = new CasinoButton("SUBMIT SCORE", CasinoButton.Style.GOLD);
        btnSubmit.addActionListener(e -> {
            if (gm.getPlayer() != null) {
                SaveSystem.getInstance().saveLeaderboardEntry(
                    gm.getPlayer().getName(),
                    gm.getPlayer().getBalance(),
                    gm.getPlayer().getHandsWon(),
                    gm.getPlayer().getWinRate());
                JOptionPane.showMessageDialog(this, "Score submitted!", "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        CasinoButton btnBack = new CasinoButton("BACK", CasinoButton.Style.NEUTRAL);
        btnBack.addActionListener(e -> gm.goToMainMenu());

        south.add(btnSubmit);
        south.add(btnBack);
        add(south, BorderLayout.SOUTH);
    }
}
