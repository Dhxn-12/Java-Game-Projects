

import javax.swing.*;
import java.awt.*;

/**
 * Settings screen for theme selection, sound, and preferences.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class SettingsScreen extends JDialog {

    private final JFrame parent;

    public SettingsScreen(JFrame parent) {
        super(parent, "Settings", true);
        this.parent = parent;
        setSize(460, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        ThemeManager tm = ThemeManager.getInstance();
        ThemeManager.Theme t = tm.getTheme();

        JPanel root = new JPanel(new BorderLayout(0, 16)) {
            @Override protected void paintComponent(Graphics g) { UIUtils.paintGradientBackground(g, this); }
        };
        root.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Title
        JLabel title = UIUtils.createGlowLabel("⚙  Settings", ThemeManager.FONT_HEADING, t.accent);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        root.add(title, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout()) { { setOpaque(false); } };
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 4, 10, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // Theme selector
        gbc.gridy = 0; gbc.gridx = 0;
        form.add(styledLabel("🎨  Theme:", t), gbc);
        JComboBox<String> themeBox = new JComboBox<>(new String[]{"NEON", "ARCADE", "MINIMAL"});
        themeBox.setSelectedItem(tm.getCurrentThemeName());
        themeBox.setFont(ThemeManager.FONT_BODY);
        themeBox.setBackground(t.surface);
        themeBox.setForeground(t.textPrimary);
        gbc.gridx = 1;
        form.add(themeBox, gbc);

        // Dark mode toggle
        gbc.gridy = 1; gbc.gridx = 0;
        form.add(styledLabel("🌙  Dark Mode:", t), gbc);
        JToggleButton darkToggle = new JToggleButton(tm.isDarkMode() ? "ON" : "OFF");
        darkToggle.setSelected(tm.isDarkMode());
        darkToggle.setFont(ThemeManager.FONT_BUTTON);
        darkToggle.setBackground(t.buttonBg);
        darkToggle.setForeground(t.accent);
        darkToggle.setBorder(new UIUtils.RoundBorder(t.buttonBorder, 8));
        gbc.gridx = 1;
        form.add(darkToggle, gbc);

        // Sound toggle
        gbc.gridy = 2; gbc.gridx = 0;
        form.add(styledLabel("🔊  Sound:", t), gbc);
        JToggleButton soundToggle = new JToggleButton(
                SoundManager.getInstance().isSoundEnabled() ? "ON" : "OFF");
        soundToggle.setSelected(SoundManager.getInstance().isSoundEnabled());
        soundToggle.setFont(ThemeManager.FONT_BUTTON);
        soundToggle.setBackground(t.buttonBg);
        soundToggle.setForeground(t.accent);
        soundToggle.setBorder(new UIUtils.RoundBorder(t.buttonBorder, 8));
        gbc.gridx = 1;
        form.add(soundToggle, gbc);

        // Volume slider
        gbc.gridy = 3; gbc.gridx = 0;
        form.add(styledLabel("🎚  Volume:", t), gbc);
        JSlider volumeSlider = new JSlider(0, 100, (int)(SoundManager.getInstance().getVolume() * 100));
        volumeSlider.setOpaque(false);
        volumeSlider.setForeground(t.accent);
        gbc.gridx = 1;
        form.add(volumeSlider, gbc);

        // Info label
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        JLabel info = new JLabel("<html><center>Changes apply immediately.<br>Restart the app to see full theme effects.</center></html>",
                SwingConstants.CENTER);
        info.setFont(ThemeManager.FONT_SMALL);
        info.setForeground(t.textSecondary);
        form.add(info, gbc);

        root.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)) { { setOpaque(false); } };
        JButton applyBtn = UIUtils.createStyledButton("✓  Apply", true);
        JButton closeBtn = UIUtils.createStyledButton("✕  Close", false);

        applyBtn.addActionListener(e -> {
            SoundManager.getInstance().playClick();
            String selectedTheme = (String) themeBox.getSelectedItem();
            tm.loadTheme(selectedTheme);
            tm.setDarkMode(darkToggle.isSelected());
            SoundManager.getInstance().setSoundEnabled(soundToggle.isSelected());
            SoundManager.getInstance().setVolume(volumeSlider.getValue() / 100f);
            darkToggle.setText(darkToggle.isSelected() ? "ON" : "OFF");
            soundToggle.setText(soundToggle.isSelected() ? "ON" : "OFF");
            SwingUtilities.updateComponentTreeUI(parent);
            JOptionPane.showMessageDialog(this, "Settings applied!", "Settings", JOptionPane.INFORMATION_MESSAGE);
        });

        closeBtn.addActionListener(e -> dispose());

        btnRow.add(closeBtn);
        btnRow.add(applyBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JLabel styledLabel(String text, ThemeManager.Theme t) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.FONT_BODY);
        l.setForeground(t.textPrimary);
        return l;
    }
}