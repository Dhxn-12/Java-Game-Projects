package com.casino.blackjack.view;

import com.casino.blackjack.controller.GameManager;
import javax.swing.*;
import java.awt.*;

/**
 * Modal dialog shown when the dealer's up-card is an Ace.
 * Offers the player an insurance bet.
 */
public class InsuranceDialog extends JDialog {

    private final GameManager gm = GameManager.getInstance();

    public InsuranceDialog(JFrame parent) {
        super(parent, "Insurance?", true);
        setUndecorated(false);
        setResizable(false);
        buildUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBackground(CasinoTheme.TABLE_FELT);
        main.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CasinoTheme.GOLD, 2),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        JLabel icon = new JLabel("🛡️", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));

        JLabel title = new JLabel("INSURANCE?", SwingConstants.CENTER);
        title.setFont(CasinoTheme.FONT_TITLE);
        title.setForeground(CasinoTheme.GOLD);

        JLabel msg = new JLabel("<html><center>The dealer shows an Ace.<br>" +
            "Would you like to place an insurance bet<br>" +
            "worth half your current bet?<br><br>" +
            "<i>Insurance pays 2:1 if the dealer has Blackjack.</i></center></html>",
            SwingConstants.CENTER);
        msg.setFont(CasinoTheme.FONT_BODY);
        msg.setForeground(CasinoTheme.TEXT_PRIMARY);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        top.add(icon, BorderLayout.NORTH);
        top.add(title, BorderLayout.CENTER);

        CasinoButton btnYes = new CasinoButton("YES — INSURE",    CasinoButton.Style.GOLD);
        CasinoButton btnNo  = new CasinoButton("NO — CONTINUE",   CasinoButton.Style.DANGER);

        btnYes.setPreferredSize(new Dimension(160, 44));
        btnNo.setPreferredSize(new Dimension(160, 44));

        btnYes.addActionListener(e -> { gm.acceptInsurance(); dispose(); });
        btnNo.addActionListener(e  -> { gm.declineInsurance(); dispose(); });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btns.setOpaque(false);
        btns.add(btnYes); btns.add(btnNo);

        main.add(top,  BorderLayout.NORTH);
        main.add(msg,  BorderLayout.CENTER);
        main.add(btns, BorderLayout.SOUTH);

        setContentPane(main);
    }
}
