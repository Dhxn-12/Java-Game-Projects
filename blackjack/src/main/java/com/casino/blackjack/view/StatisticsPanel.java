package com.casino.blackjack.view;

import com.casino.blackjack.controller.GameManager;
import com.casino.blackjack.manager.AchievementManager;
import com.casino.blackjack.model.HistoryEntry;
import com.casino.blackjack.model.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Statistics dashboard — shows session stats, history table, achievements.
 */
public class StatisticsPanel extends JPanel {

    private final GameManager gm = GameManager.getInstance();

    public StatisticsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(CasinoTheme.TABLE_BG);
        setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        buildUI();
    }

    private void buildUI() {
        JLabel title = new JLabel("STATISTICS", SwingConstants.CENTER);
        title.setFont(CasinoTheme.FONT_TITLE);
        title.setForeground(CasinoTheme.GOLD);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(CasinoTheme.TABLE_BG);
        tabs.setForeground(CasinoTheme.TEXT_PRIMARY);
        tabs.addTab("Overview",     buildOverview());
        tabs.addTab("History",      buildHistory());
        tabs.addTab("Achievements", buildAchievements());
        add(tabs, BorderLayout.CENTER);

        CasinoButton btnBack = new CasinoButton("BACK", CasinoButton.Style.NEUTRAL);
        btnBack.addActionListener(e -> gm.goToMainMenu());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setOpaque(false);
        south.add(btnBack);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel buildOverview() {
        JPanel p = new JPanel(new GridLayout(0, 2, 16, 10));
        p.setBackground(CasinoTheme.TABLE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        Player pl = gm.getPlayer();
        if (pl == null) {
            p.add(label("No player data.", CasinoTheme.TEXT_SECONDARY));
            return p;
        }

        addStat(p, "Player Name",      pl.getName());
        addStat(p, "Balance",          String.format("$%.2f", pl.getBalance()));
        addStat(p, "Highest Balance",  String.format("$%.2f", pl.getHighestBalance()));
        addStat(p, "Net Profit",       String.format("%+.2f", pl.getNetProfit()));
        addStat(p, "Hands Played",     String.valueOf(pl.getHandsPlayed()));
        addStat(p, "Hands Won",        pl.getHandsWon() + " (" + String.format("%.1f%%", pl.getWinRate()) + ")");
        addStat(p, "Hands Lost",       String.valueOf(pl.getHandsLost()));
        addStat(p, "Pushes",           String.valueOf(pl.getHandsPushed()));
        addStat(p, "Blackjacks",       String.valueOf(pl.getBlackjacks()));
        addStat(p, "Busts",            String.valueOf(pl.getBusts()));
        addStat(p, "Best Win Streak",  String.valueOf(pl.getStreakBest()));
        addStat(p, "Current Streak",   String.valueOf(pl.getStreakCurrent()));
        addStat(p, "Total Wagered",    String.format("$%.2f", pl.getTotalWagered()));
        addStat(p, "Total Won",        String.format("$%.2f", pl.getTotalWon()));
        addStat(p, "Login Streak",     pl.getConsecutiveDays() + " days");
        addStat(p, "Achievement Pts",  String.valueOf(AchievementManager.getInstance().getTotalPoints(pl)));
        return p;
    }

    private JPanel buildHistory() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CasinoTheme.TABLE_BG);

        String[] cols = {"Time","Result","Player","Dealer","Bet","Net","Balance"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<HistoryEntry> hist = gm.getHistory();
        for (int i = hist.size() - 1; i >= 0 && i >= hist.size() - 100; i--) {
            HistoryEntry e = hist.get(i);
            model.addRow(new Object[]{
                e.getTimestamp().toLocalTime().toString().substring(0, 8),
                e.getResult().getLabel(),
                e.getPlayerValue(),
                e.getDealerValue(),
                String.format("$%.0f", e.getBetAmount()),
                String.format("%+.0f", e.getNetResult()),
                String.format("$%.0f", e.getBalanceAfter())
            });
        }

        JTable table = new JTable(model);
        table.setBackground(CasinoTheme.TABLE_FELT);
        table.setForeground(CasinoTheme.TEXT_PRIMARY);
        table.setFont(CasinoTheme.FONT_SMALL);
        table.setGridColor(CasinoTheme.withAlpha(CasinoTheme.GOLD, 40));
        table.setSelectionBackground(CasinoTheme.withAlpha(CasinoTheme.GOLD, 60));
        table.getTableHeader().setBackground(CasinoTheme.TABLE_TRIM);
        table.getTableHeader().setForeground(CasinoTheme.GOLD);
        table.setRowHeight(22);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildAchievements() {
        JPanel p = new JPanel(new GridLayout(0, 2, 12, 10));
        p.setBackground(CasinoTheme.TABLE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        Player pl = gm.getPlayer();

        for (AchievementManager.Achievement a : AchievementManager.ALL) {
            boolean unlocked = pl != null && pl.hasAchievement(a.id());
            JPanel card = new JPanel(new BorderLayout(8, 0));
            card.setBackground(unlocked
                    ? CasinoTheme.withAlpha(CasinoTheme.GOLD, 30)
                    : CasinoTheme.withAlpha(Color.WHITE, 8));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(unlocked ? CasinoTheme.GOLD : CasinoTheme.withAlpha(Color.WHITE, 30), 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));

            JLabel icon = new JLabel(a.icon());
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            if (!unlocked) icon.setForeground(CasinoTheme.withAlpha(Color.WHITE, 60));

            JPanel info = new JPanel(new GridLayout(2, 1));
            info.setOpaque(false);
            JLabel tl = new JLabel(a.title());
            tl.setFont(CasinoTheme.FONT_BUTTON);
            tl.setForeground(unlocked ? CasinoTheme.GOLD : CasinoTheme.withAlpha(Color.WHITE, 80));
            JLabel dl = new JLabel(a.description() + " (" + a.pointValue() + " pts)");
            dl.setFont(CasinoTheme.FONT_SMALL);
            dl.setForeground(CasinoTheme.TEXT_SECONDARY);
            info.add(tl); info.add(dl);

            card.add(icon, BorderLayout.WEST);
            card.add(info, BorderLayout.CENTER);
            p.add(card);
        }

        JScrollPane scroll = new JScrollPane(p);
        scroll.getViewport().setBackground(CasinoTheme.TABLE_BG);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(CasinoTheme.TABLE_BG);
        wrap.add(scroll);
        return wrap;
    }

    private void addStat(JPanel p, String key, String val) {
        p.add(label(key, CasinoTheme.TEXT_SECONDARY));
        p.add(label(val, CasinoTheme.TEXT_PRIMARY));
    }

    private JLabel label(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(CasinoTheme.FONT_BODY);
        l.setForeground(color);
        return l;
    }
}
