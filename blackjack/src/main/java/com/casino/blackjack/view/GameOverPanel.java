package com.casino.blackjack.view;

import com.casino.blackjack.controller.GameManager;
import com.casino.blackjack.model.Player;
import com.casino.blackjack.save.SaveSystem;

import javax.swing.*;
import java.awt.*;

/**
 * Shown when the player runs out of money.
 */
public class GameOverPanel extends JPanel {

    private final GameManager gm = GameManager.getInstance();

    public GameOverPanel() {
        setLayout(new GridBagLayout());
        setBackground(CasinoTheme.TABLE_BG);
        buildUI();
    }

    private void buildUI() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 20, 10, 20);
        c.gridx = 0; c.anchor = GridBagConstraints.CENTER;

        JLabel skull = new JLabel("💀", SwingConstants.CENTER);
        skull.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        c.gridy = 0; add(skull, c);

        JLabel title = new JLabel("GAME OVER", SwingConstants.CENTER);
        title.setFont(CasinoTheme.FONT_DISPLAY);
        title.setForeground(new Color(0xFF4444));
        c.gridy = 1; add(title, c);

        Player pl = gm.getPlayer();
        String stats = pl != null
            ? String.format("<html><center>You've run out of chips!<br><br>" +
                "Hands Played: <b>%d</b><br>" +
                "Hands Won: <b>%d</b> (%.1f%%)<br>" +
                "Blackjacks: <b>%d</b><br>" +
                "Best Streak: <b>%d</b></center></html>",
                pl.getHandsPlayed(), pl.getHandsWon(), pl.getWinRate(),
                pl.getBlackjacks(), pl.getStreakBest())
            : "Better luck next time!";

        JLabel info = new JLabel(stats, SwingConstants.CENTER);
        info.setFont(CasinoTheme.FONT_BODY);
        info.setForeground(CasinoTheme.TEXT_PRIMARY);
        c.gridy = 2; add(info, c);

        // Save to leaderboard
        if (pl != null) {
            SaveSystem.getInstance().saveLeaderboardEntry(
                pl.getName(), pl.getBalance(), pl.getHandsWon(), pl.getWinRate());
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btns.setOpaque(false);

        CasinoButton btnNew  = new CasinoButton("NEW GAME",    CasinoButton.Style.GOLD);
        CasinoButton btnMenu = new CasinoButton("MAIN MENU",   CasinoButton.Style.NEUTRAL);
        CasinoButton btnQuit = new CasinoButton("QUIT",        CasinoButton.Style.DANGER);

        btnNew.addActionListener(e -> {
            String name = pl != null ? pl.getName() : "Player";
            gm.createNewPlayer(name);
        });
        btnMenu.addActionListener(e -> gm.goToMainMenu());
        btnQuit.addActionListener(e -> { gm.shutdown(); System.exit(0); });

        btns.add(btnNew); btns.add(btnMenu); btns.add(btnQuit);
        c.gridy = 3; add(btns, c);
    }
}
