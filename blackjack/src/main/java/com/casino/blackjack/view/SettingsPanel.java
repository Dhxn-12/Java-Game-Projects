package com.casino.blackjack.view;

import com.casino.blackjack.controller.GameManager;
import com.casino.blackjack.model.GameSettings;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Settings screen — all game options configurable here.
 */
public class SettingsPanel extends JPanel {

    private final GameManager gm = GameManager.getInstance();
    private GameSettings settings;

    // Controls
    private JComboBox<String> cbDecks, cbTheme, cbDifficulty, cbAnimSpeed;
    private JCheckBox chkDealerPeek, chkS17, chkDAS, chkSurrender;
    private JCheckBox chkSound, chkMusic, chkHints, chkProbs, chkAnim, chkTimer, chkFullscreen;
    private JSlider sldSound, sldMusic, sldTimer;
    private JSpinner spnMinBet, spnMaxBet, spnStartBalance;
    private CasinoButton btnSave, btnCancel, btnReset;

    public SettingsPanel() {
        settings = gm.getSettings();
        setLayout(new BorderLayout(10, 10));
        setBackground(CasinoTheme.TABLE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        buildUI();
    }

    private void buildUI() {
        // Title
        JLabel title = new JLabel("SETTINGS", SwingConstants.CENTER);
        title.setFont(CasinoTheme.FONT_TITLE);
        title.setForeground(CasinoTheme.GOLD);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        add(title, BorderLayout.NORTH);

        // Tabbed sections
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(CasinoTheme.TABLE_BG);
        tabs.setForeground(CasinoTheme.TEXT_PRIMARY);
        tabs.addTab("Game Rules",    buildGameRulesPanel());
        tabs.addTab("Betting",       buildBettingPanel());
        tabs.addTab("Audio",         buildAudioPanel());
        tabs.addTab("Visual",        buildVisualPanel());
        tabs.addTab("Gameplay",      buildGameplayPanel());
        add(tabs, BorderLayout.CENTER);

        // Bottom buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        btnRow.setOpaque(false);
        btnSave   = new CasinoButton("SAVE",   CasinoButton.Style.GOLD);
        btnCancel = new CasinoButton("CANCEL", CasinoButton.Style.NEUTRAL);
        btnReset  = new CasinoButton("RESET",  CasinoButton.Style.DANGER);
        btnSave.addActionListener(e -> saveAndClose());
        btnCancel.addActionListener(e -> gm.goToMainMenu());
        btnReset.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "Reset all settings to defaults?", "Reset Settings",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                settings = new GameSettings();
                refreshAll();
            }
        });
        btnRow.add(btnSave); btnRow.add(btnCancel); btnRow.add(btnReset);
        add(btnRow, BorderLayout.SOUTH);

        refreshAll();
    }

    // ── Section builders ─────────────────────────────────────────────────────

    private JPanel buildGameRulesPanel() {
        JPanel p = section();
        cbDecks = combo(new String[]{"1 Deck","2 Decks","4 Decks","6 Decks","8 Decks"});
        chkDealerPeek = chk("Dealer Peeks for Blackjack");
        chkS17        = chk("Dealer Stands on Soft 17");
        chkDAS        = chk("Double After Split (DAS)");
        chkSurrender  = chk("Late Surrender Allowed");

        addRow(p, "Number of Decks:", cbDecks);
        p.add(chkDealerPeek); p.add(chkS17); p.add(chkDAS); p.add(chkSurrender);
        return p;
    }

    private JPanel buildBettingPanel() {
        JPanel p = section();
        spnMinBet      = spinner(1, 1, 500, 1);
        spnMaxBet      = spinner(100, 10, 100000, 50);
        spnStartBalance= spinner(100, 100, 1000000, 100);
        addRow(p, "Minimum Bet ($):",      spnMinBet);
        addRow(p, "Maximum Bet ($):",      spnMaxBet);
        addRow(p, "Starting Balance ($):", spnStartBalance);
        return p;
    }

    private JPanel buildAudioPanel() {
        JPanel p = section();
        chkSound = chk("Sound Effects Enabled");
        chkMusic = chk("Background Music Enabled");
        sldSound = slider(0, 100, 80);
        sldMusic = slider(0, 100, 40);
        p.add(chkSound);
        addRow(p, "Sound Volume:", sldSound);
        p.add(chkMusic);
        addRow(p, "Music Volume:", sldMusic);
        return p;
    }

    private JPanel buildVisualPanel() {
        JPanel p = section();
        cbTheme     = combo(new String[]{"CLASSIC_GREEN","MIDNIGHT_BLUE","ROYAL_RED","DESERT_GOLD"});
        cbAnimSpeed = combo(new String[]{"Slow","Normal","Fast"});
        chkAnim     = chk("Enable Animations");
        chkFullscreen = chk("Fullscreen Mode");
        addRow(p, "Casino Theme:", cbTheme);
        p.add(chkAnim);
        addRow(p, "Animation Speed:", cbAnimSpeed);
        p.add(chkFullscreen);
        return p;
    }

    private JPanel buildGameplayPanel() {
        JPanel p = section();
        cbDifficulty = combo(new String[]{"EASY","NORMAL","HARD"});
        chkHints  = chk("Show Basic Strategy Hints");
        chkProbs  = chk("Show Win Probabilities");
        chkTimer  = chk("Enable Timer Mode");
        sldTimer  = slider(10, 60, 30);
        addRow(p, "Difficulty:", cbDifficulty);
        p.add(chkHints); p.add(chkProbs); p.add(chkTimer);
        addRow(p, "Timer Seconds:", sldTimer);
        return p;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JPanel section() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CasinoTheme.TABLE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        return p;
    }

    private void addRow(JPanel p, String label, JComponent comp) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JLabel lbl = new JLabel(label);
        lbl.setFont(CasinoTheme.FONT_BODY);
        lbl.setForeground(CasinoTheme.TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(200, 28));
        row.add(lbl, BorderLayout.WEST);
        row.add(comp, BorderLayout.CENTER);
        p.add(row);
        p.add(Box.createVerticalStrut(8));
    }

    private JComboBox<String> combo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(CasinoTheme.TABLE_FELT);
        cb.setForeground(CasinoTheme.TEXT_PRIMARY);
        cb.setFont(CasinoTheme.FONT_BODY);
        return cb;
    }

    private JCheckBox chk(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setOpaque(false);
        cb.setForeground(CasinoTheme.TEXT_PRIMARY);
        cb.setFont(CasinoTheme.FONT_BODY);
        return cb;
    }

    private JSlider slider(int min, int max, int val) {
        JSlider s = new JSlider(min, max, val);
        s.setOpaque(false);
        s.setForeground(CasinoTheme.GOLD);
        s.setPaintTicks(true);
        s.setPaintLabels(true);
        return s;
    }

    private JSpinner spinner(int val, int min, int max, int step) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(val, min, max, step));
        sp.setBackground(CasinoTheme.TABLE_FELT);
        sp.setForeground(CasinoTheme.TEXT_PRIMARY);
        sp.setFont(CasinoTheme.FONT_BODY);
        return sp;
    }

    private void refreshAll() {
        // Game rules
        int di = switch (settings.getDeckCount()) {
            case 2 -> 1; case 4 -> 2; case 6 -> 3; case 8 -> 4; default -> 0;
        };
        cbDecks.setSelectedIndex(di);
        chkDealerPeek.setSelected(settings.isDealerPeek());
        chkS17.setSelected(settings.isStandOnSoft17());
        chkDAS.setSelected(settings.isDoubleAfterSplit());
        chkSurrender.setSelected(settings.isSurrenderAllowed());

        // Betting
        spnMinBet.setValue((int) settings.getMinimumBet());
        spnMaxBet.setValue((int) settings.getMaximumBet());
        spnStartBalance.setValue((int) settings.getStartingBalance());

        // Audio
        chkSound.setSelected(settings.isSoundEnabled());
        chkMusic.setSelected(settings.isMusicEnabled());
        sldSound.setValue((int)(settings.getSoundVolume() * 100));
        sldMusic.setValue((int)(settings.getMusicVolume() * 100));

        // Visual
        cbTheme.setSelectedItem(settings.getTheme());
        chkAnim.setSelected(settings.isAnimationsEnabled());
        cbAnimSpeed.setSelectedIndex(settings.getAnimationSpeed() - 1);
        chkFullscreen.setSelected(settings.isFullscreen());

        // Gameplay
        cbDifficulty.setSelectedItem(settings.getDifficulty());
        chkHints.setSelected(settings.isShowHints());
        chkProbs.setSelected(settings.isShowProbabilities());
        chkTimer.setSelected(settings.isTimerEnabled());
        sldTimer.setValue(settings.getTimerSeconds());
    }

    private void saveAndClose() {
        // Game rules
        int[] deckMap = {1, 2, 4, 6, 8};
        settings.setDeckCount(deckMap[cbDecks.getSelectedIndex()]);
        settings.setDealerPeek(chkDealerPeek.isSelected());
        settings.setStandOnSoft17(chkS17.isSelected());
        settings.setDoubleAfterSplit(chkDAS.isSelected());
        settings.setSurrenderAllowed(chkSurrender.isSelected());

        // Betting
        settings.setMinimumBet((Integer) spnMinBet.getValue());
        settings.setMaximumBet((Integer) spnMaxBet.getValue());
        settings.setStartingBalance((Integer) spnStartBalance.getValue());

        // Audio
        settings.setSoundEnabled(chkSound.isSelected());
        settings.setMusicEnabled(chkMusic.isSelected());
        settings.setSoundVolume(sldSound.getValue() / 100f);
        settings.setMusicVolume(sldMusic.getValue() / 100f);

        // Visual
        settings.setTheme((String) cbTheme.getSelectedItem());
        settings.setAnimationsEnabled(chkAnim.isSelected());
        settings.setAnimationSpeed(cbAnimSpeed.getSelectedIndex() + 1);
        settings.setFullscreen(chkFullscreen.isSelected());

        // Gameplay
        settings.setDifficulty((String) cbDifficulty.getSelectedItem());
        settings.setShowHints(chkHints.isSelected());
        settings.setShowProbabilities(chkProbs.isSelected());
        settings.setTimerEnabled(chkTimer.isSelected());
        settings.setTimerSeconds(sldTimer.getValue());

        gm.applySettings(settings);
        gm.goToMainMenu();
    }
}
