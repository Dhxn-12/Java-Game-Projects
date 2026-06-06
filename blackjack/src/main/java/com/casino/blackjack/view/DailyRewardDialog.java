package com.casino.blackjack.view;

import com.casino.blackjack.controller.GameManager;
import javax.swing.*;
import java.awt.*;

/**
 * Popup dialog that awards the daily login bonus.
 */
public class DailyRewardDialog extends JDialog {

    public DailyRewardDialog(JFrame parent) {
        super(parent, "Daily Reward!", true);
        setResizable(false);
        buildUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(12, 16));
        main.setBackground(CasinoTheme.TABLE_FELT);
        main.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CasinoTheme.GOLD, 2),
            BorderFactory.createEmptyBorder(24, 36, 24, 36)));

        JLabel icon = new JLabel("🎁", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));

        JLabel title = new JLabel("DAILY REWARD!", SwingConstants.CENTER);
        title.setFont(CasinoTheme.FONT_TITLE);
        title.setForeground(CasinoTheme.GOLD);

        GameManager gm = GameManager.getInstance();
        int days = gm.getPlayer() != null ? gm.getPlayer().getConsecutiveDays() + 1 : 1;
        double reward = 100 + (days * 50);

        JLabel msg = new JLabel(
            String.format("<html><center>Day %d login streak!<br><br>" +
                "<font size='+2' color='#FFD700'><b>+$%.0f</b></font><br><br>" +
                "<i>Come back tomorrow for even more!</i></center></html>", days, reward),
            SwingConstants.CENTER);
        msg.setFont(CasinoTheme.FONT_BODY);
        msg.setForeground(CasinoTheme.TEXT_PRIMARY);

        CasinoButton btnClaim = new CasinoButton("CLAIM REWARD", CasinoButton.Style.GOLD);
        btnClaim.setPreferredSize(new Dimension(180, 48));
        btnClaim.addActionListener(e -> {
            gm.claimDailyReward();
            dispose();
        });

        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.setOpaque(false);
        top.add(icon, BorderLayout.NORTH);
        top.add(title, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setOpaque(false);
        south.add(btnClaim);

        main.add(top,   BorderLayout.NORTH);
        main.add(msg,   BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);

        setContentPane(main);
    }
}
