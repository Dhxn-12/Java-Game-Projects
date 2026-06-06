package com.casino.blackjack.view;

import com.casino.blackjack.controller.GameManager;
import javax.swing.*;
import java.awt.*;

/**
 * How-to-play / tutorial screen with rules and keyboard shortcuts.
 */
public class TutorialPanel extends JPanel {

    private final GameManager gm = GameManager.getInstance();

    public TutorialPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(CasinoTheme.TABLE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        buildUI();
    }

    private void buildUI() {
        JLabel title = new JLabel("HOW TO PLAY", SwingConstants.CENTER);
        title.setFont(CasinoTheme.FONT_TITLE);
        title.setForeground(CasinoTheme.GOLD);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(CasinoTheme.TABLE_BG);
        tabs.setForeground(CasinoTheme.TEXT_PRIMARY);
        tabs.addTab("Basics",    buildPage(BASICS));
        tabs.addTab("Actions",   buildPage(ACTIONS));
        tabs.addTab("Strategy",  buildPage(STRATEGY));
        tabs.addTab("Keys",      buildPage(KEYS));
        add(tabs, BorderLayout.CENTER);

        CasinoButton btnBack = new CasinoButton("BACK", CasinoButton.Style.NEUTRAL);
        btnBack.addActionListener(e -> gm.goToMainMenu());
        JPanel s = new JPanel(new FlowLayout(FlowLayout.CENTER));
        s.setOpaque(false); s.add(btnBack);
        add(s, BorderLayout.SOUTH);
    }

    private JPanel buildPage(String html) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CasinoTheme.TABLE_BG);
        JTextPane tp = new JTextPane();
        tp.setContentType("text/html");
        tp.setText("<html><body style='font-family:Segoe UI;color:#F5F0E8;background:#1E4A1E;font-size:13px;padding:12px'>" + html + "</body></html>");
        tp.setEditable(false);
        tp.setBackground(CasinoTheme.TABLE_FELT);
        JScrollPane sc = new JScrollPane(tp);
        sc.getViewport().setBackground(CasinoTheme.TABLE_FELT);
        p.add(sc);
        return p;
    }

    private static final String BASICS = """
        <h2 style='color:#D4AF37'>Objective</h2>
        <p>Beat the dealer by getting a hand closer to <b>21</b> without going over (busting).</p>
        <h2 style='color:#D4AF37'>Card Values</h2>
        <ul>
        <li><b>2–10</b> → face value</li>
        <li><b>Jack, Queen, King</b> → 10 points</li>
        <li><b>Ace</b> → 1 <i>or</i> 11 (whichever is better)</li>
        </ul>
        <h2 style='color:#D4AF37'>Blackjack</h2>
        <p>An Ace + any 10-value card on the first two cards = Blackjack! Pays <b>3:2</b>.</p>
        <h2 style='color:#D4AF37'>Dealer Rules</h2>
        <p>The dealer must hit on 16 or less, and stand on all 17s.</p>
        <h2 style='color:#D4AF37'>Winning</h2>
        <ul>
        <li><b>Win</b> → your bet is doubled</li>
        <li><b>Blackjack</b> → pays 3:2</li>
        <li><b>Push (tie)</b> → bet returned</li>
        <li><b>Bust or less than dealer</b> → you lose your bet</li>
        </ul>
        """;

    private static final String ACTIONS = """
        <h2 style='color:#D4AF37'>Player Actions</h2>
        <ul>
        <li><b>HIT</b> — Take another card. You can hit as many times as you like.</li>
        <li><b>STAND</b> — End your turn. The dealer then plays.</li>
        <li><b>DOUBLE DOWN</b> — Double your bet and receive exactly one more card.</li>
        <li><b>SPLIT</b> — If your first two cards match, split into two separate hands. Each hand gets a new second card.</li>
        <li><b>SURRENDER</b> — Give up your hand and recover half your bet.</li>
        <li><b>INSURANCE</b> — When the dealer shows an Ace, you may bet up to half your original bet that the dealer has Blackjack. Pays 2:1.</li>
        </ul>
        <h2 style='color:#D4AF37'>Betting</h2>
        <p>Click chip buttons to add chips to your bet. Press <b>DEAL</b> to start the round.
        Use <b>RE-BET</b> to repeat your last bet, or <b>MAX BET</b> to go all-in.</p>
        """;

    private static final String STRATEGY = """
        <h2 style='color:#D4AF37'>Basic Strategy Tips</h2>
        <p>Basic Strategy is a mathematically proven set of decisions that minimises the house edge.</p>
        <h3 style='color:#C8B89A'>Hard Totals</h3>
        <ul>
        <li><b>8 or less</b> → Always Hit</li>
        <li><b>9</b> → Double vs dealer 3–6, else Hit</li>
        <li><b>10</b> → Double vs dealer 2–9, else Hit</li>
        <li><b>11</b> → Double vs dealer 2–10, else Hit</li>
        <li><b>12</b> → Stand vs dealer 4–6, else Hit</li>
        <li><b>13–16</b> → Stand vs dealer 2–6, else Hit</li>
        <li><b>17+</b> → Always Stand</li>
        </ul>
        <h3 style='color:#C8B89A'>Always Split</h3>
        <p>Aces and 8s — always split these pairs.</p>
        <h3 style='color:#C8B89A'>Never Split</h3>
        <p>5s (treat as 10) and 10s (you already have a great hand).</p>
        <p><i>Enable "Show Hints" in Settings to see strategy suggestions during play!</i></p>
        """;

    private static final String KEYS = """
        <h2 style='color:#D4AF37'>Keyboard Shortcuts</h2>
        <table style='border-collapse:collapse;width:100%'>
        <tr style='background:#0D2210'><th style='padding:8px;color:#D4AF37;text-align:left'>Key</th><th style='color:#D4AF37;text-align:left'>Action</th></tr>
        <tr><td style='padding:8px'><b>H</b></td><td>Hit</td></tr>
        <tr style='background:#0D2210'><td style='padding:8px'><b>S</b></td><td>Stand</td></tr>
        <tr><td style='padding:8px'><b>D</b></td><td>Double Down</td></tr>
        <tr style='background:#0D2210'><td style='padding:8px'><b>P</b></td><td>Split</td></tr>
        <tr><td style='padding:8px'><b>Enter</b></td><td>Deal (confirm bet)</td></tr>
        <tr style='background:#0D2210'><td style='padding:8px'><b>Escape</b></td><td>Clear Bet</td></tr>
        </table>
        <br>
        <h2 style='color:#D4AF37'>Tips</h2>
        <ul>
        <li>The hint bar at the bottom shows the mathematically best move.</li>
        <li>The shoe counter (top right) tracks remaining cards.</li>
        <li>Daily login rewards give bonus chips every day.</li>
        <li>Unlock achievements for bonus milestones.</li>
        </ul>
        """;
}
