package main;

import controller.CollisionManager;
import controller.InputHandler;
import model.*;
import service.*;
import util.Constants;
import util.SaveLoadManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * GamePanel is the core game component.
 *
 * Responsibilities:
 *  - Owns all game-world objects (player, enemies, bullets, barriers, power-ups)
 *  - Runs the update loop via a Swing Timer
 *  - Renders everything each frame
 *  - Delegates input to InputHandler, collisions to CollisionManager
 *
 * State machine: MENU → RUNNING ↔ PAUSED → GAME_OVER → MENU
 */
public class GamePanel extends JPanel implements ActionListener, CollisionManager.GameEventListener {

    // ── State ─────────────────────────────────────────────────────────────────
    public enum GameState { MENU, RUNNING, PAUSED, GAME_OVER, LEVEL_TRANSITION }

    private GameState state = GameState.MENU;

    // ── Game objects ──────────────────────────────────────────────────────────
    private Player         player;
    private List<Enemy>    enemies;
    private List<Bullet>   bullets;
    private List<Barrier>  barriers;
    private List<PowerUp>  powerUps;
    private Enemy          boss;

    // ── Services ─────────────────────────────────────────────────────────────
    private final ScoreManager   scoreManager  = ScoreManager.getInstance();
    private final SoundManager   soundManager  = SoundManager.getInstance();
    private final LevelManager   levelManager  = new LevelManager();
    private final ParticleSystem particles     = new ParticleSystem();

    // ── Controllers ──────────────────────────────────────────────────────────
    private final InputHandler     input;
    private final CollisionManager collisions;

    // ── Loop timer ───────────────────────────────────────────────────────────
    private final Timer loopTimer;

    // ── Level state ──────────────────────────────────────────────────────────
    private int  currentLevel  = 1;
    private long levelTransitionEnd = 0;
    private static final long TRANSITION_MS = 2000;

    // ── Boss ─────────────────────────────────────────────────────────────────
    private int  bossDir = 1;
    private boolean bossActive = false;

    // ── Score popup labels ────────────────────────────────────────────────────
    private final List<ScorePopup> scorePopups = new ArrayList<>();

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private Font arcadeFont;
    private Font hudFont;
    private Font bigFont;

    // ── Starfield ─────────────────────────────────────────────────────────────
    private final int[]   starX = new int[120];
    private final int[]   starY = new int[120];
    private final int[]   starSize = new int[120];
    private final float[] starSpeed = new float[120];
    private final float[] starAlpha = new float[120];

    // ── Menu animation ────────────────────────────────────────────────────────
    private int menuEnemyX = 0;
    private int menuEnemyDir = 1;

    // ─────────────────────────────────────────────────────────────────────────

    public GamePanel(InputHandler input) {
        this.input = input;
        this.collisions = new CollisionManager(this, scoreManager, soundManager);

        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(input);

        initFonts();
        initStars();

        // Swing Timer fires every ~16ms (≈60 fps)
        loopTimer = new Timer(16, this);
        loopTimer.start();
    }

    // ── Initialisation ────────────────────────────────────────────────────────

    private void initFonts() {
        // Try to load a monospaced font; fall back to standard
        arcadeFont = new Font("Courier New", Font.BOLD, 22);
        hudFont    = new Font("Courier New", Font.BOLD, 14);
        bigFont    = new Font("Courier New", Font.BOLD, 42);
    }

    private void initStars() {
        java.util.Random rand = new java.util.Random(999);
        for (int i = 0; i < starX.length; i++) {
            starX[i]     = rand.nextInt(Constants.WINDOW_WIDTH);
            starY[i]     = rand.nextInt(Constants.WINDOW_HEIGHT);
            starSize[i]  = 1 + rand.nextInt(2);
            starSpeed[i] = 0.2f + rand.nextFloat() * 0.6f;
            starAlpha[i] = 0.3f + rand.nextFloat() * 0.7f;
        }
    }

    private void startNewGame() {
        currentLevel = 1;
        scoreManager.reset();
        player   = new Player();
        bullets  = new ArrayList<>();
        powerUps = new ArrayList<>();
        boss     = null;
        bossActive = false;
        scorePopups.clear();
        initBarriers();
        spawnLevel();
        state = GameState.RUNNING;
        soundManager.startBackground(currentLevel);
    }

    private void spawnLevel() {
        enemies = levelManager.nextLevel(currentLevel);
        bullets.clear();
        powerUps.clear();
        boss     = null;
        bossActive = false;

        // Spawn boss every N levels instead of regular wave
        if (currentLevel % Constants.BOSS_INTERVAL_LEVELS == 0) {
            bossActive = true;
            boss = LevelManager.EnemyFactory.createBoss();
            enemies.clear();
        }

        soundManager.startBackground(currentLevel);
    }

    private void initBarriers() {
        barriers = new ArrayList<>();
        int spacing = Constants.WINDOW_WIDTH / (Constants.BARRIER_COUNT + 1);
        for (int i = 1; i <= Constants.BARRIER_COUNT; i++) {
            int bx = i * spacing - Constants.BARRIER_WIDTH / 2;
            barriers.add(new Barrier(bx, Constants.BARRIER_Y));
        }
    }

    // ── Game Loop ─────────────────────────────────────────────────────────────

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        // Scroll stars always
        updateStars();

        switch (state) {
            case MENU:              updateMenu();       break;
            case RUNNING:           updateGame();       break;
            case PAUSED:                                break;
            case LEVEL_TRANSITION:  updateTransition(); break;
            case GAME_OVER:         updateGameOver();   break;
        }
    }

    private void updateMenu() {
        menuEnemyX += menuEnemyDir * 2;
        if (menuEnemyX > Constants.WINDOW_WIDTH - 40 || menuEnemyX < 0) menuEnemyDir *= -1;
        if (input.consumeMute()) startNewGame();
        if (input.consumeShoot()) startNewGame();
        if (input.consumeMute())  soundManager.toggleMute();
    }

    private void updateGame() {
        // Input
        player.update(input.isLeftHeld(), input.isRightHeld());

        // Shoot: fire on SPACE press OR hold (cooldown prevents spam)
        if ((input.consumeShoot() || input.isSpaceHeld()) && player.canShoot()) {
            spawnPlayerBullet();
        }

        if (input.consumeMute()) {
            soundManager.toggleMute();
        }

        if (input.consumeRestart()) {
            startNewGame();
            return;
        }

        if (input.consumePause()) {
            state = GameState.PAUSED;
            soundManager.stopBackground();
            return;
        }

        // Boss movement
        if (bossActive && boss != null && boss.isAlive()) {
            int bx = boss.getX() + Constants.BOSS_SPEED * bossDir;
            if (bx + Constants.BOSS_WIDTH > Constants.WINDOW_WIDTH || bx < 0) bossDir *= -1;
            boss.setPosition(bx, boss.getY());
            boss.setAnimFrame((int)(System.currentTimeMillis() / 200) % 8);
        }

        // Enemy swarm
        levelManager.updateSwarm(enemies);

        // Enemy shooting
        Enemy shooter = levelManager.getShootingEnemy(enemies);
        if (shooter != null) {
            spawnEnemyBullet(shooter);
        }

        // Bullet updates
        bullets.removeIf(b -> { b.update(); return !b.isActive(); });
        powerUps.removeIf(p -> { p.update(); return !p.isActive(); });

        // Collisions
        collisions.checkAll(player, enemies, bullets, barriers, powerUps, boss);

        // Particles
        particles.update();

        // Score popups
        scorePopups.removeIf(sp -> sp.update());

        // Remove dead barrier cells
        barriers.removeIf(Barrier::isDestroyed);

        // Game over
        if (!player.isAlive()) {
            triggerGameOver();
        }
    }

    private void spawnPlayerBullet() {
        bullets.add(new Bullet(player.getX() + player.getWidth() / 2,
                               player.getY(),
                               Bullet.Owner.PLAYER));
        player.recordShot();
        soundManager.playShoot();
    }

    private void spawnEnemyBullet(Enemy e) {
        bullets.add(new Bullet(e.getCannonX(), e.getCannonY(), Bullet.Owner.ENEMY));
    }

    private void updateTransition() {
        if (System.currentTimeMillis() > levelTransitionEnd) {
            spawnLevel();
            player.respawn();
            state = GameState.RUNNING;
        }
    }

    private void updateGameOver() {
        if (input.consumeMute() || input.consumeShoot()) {
            state = GameState.MENU;
        }
    }

    private void updateStars() {
        for (int i = 0; i < starY.length; i++) {
            starY[i] += starSpeed[i];
            if (starY[i] > Constants.WINDOW_HEIGHT) {
                starY[i]  = 0;
                starX[i]  = (int)(Math.random() * Constants.WINDOW_WIDTH);
            }
        }
    }

    private void triggerGameOver() {
        state = GameState.GAME_OVER;
        scoreManager.saveSession();
        soundManager.stopBackground();
        // Big explosion on player
        particles.spawnExplosion(player.getX(), player.getY(),
                                 new Color(255, 120, 0), 40);
        particles.triggerShake(10, 800);
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics gfx) {
        super.paintComponent(gfx);
        Graphics2D g = (Graphics2D) gfx;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Apply screen shake
        g.translate(particles.getShakeX(), particles.getShakeY());

        drawBackground(g);

        switch (state) {
            case MENU:
                drawMenu(g); break;
            case RUNNING:
            case PAUSED:
                drawGame(g);
                if (state == GameState.PAUSED) drawPause(g);
                break;
            case LEVEL_TRANSITION:
                drawGame(g); drawTransition(g); break;
            case GAME_OVER:
                drawGame(g); drawGameOver(g); break;
        }
    }

    private void drawBackground(Graphics2D g) {
        // Deep space gradient
        GradientPaint bg = new GradientPaint(0, 0, new Color(2, 2, 18),
                                              0, Constants.WINDOW_HEIGHT, new Color(5, 5, 30));
        g.setPaint(bg);
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        // Stars
        for (int i = 0; i < starX.length; i++) {
            g.setColor(new Color(1f, 1f, 1f, starAlpha[i]));
            g.fillOval(starX[i], starY[i], starSize[i], starSize[i]);
        }
    }

    private void drawMenu(Graphics2D g) {
        // Title glow
        long t = System.currentTimeMillis();
        float pulse = 0.75f + 0.25f * (float) Math.sin(t / 400.0);

        // Title shadow
        g.setFont(bigFont);
        g.setColor(new Color(0, 80, 180, 80));
        g.drawString("SPACE INVADERS", 48, 162);

        // Title
        Color titleColor = new Color(0, 210, 255);
        g.setColor(new Color(
            (int)(titleColor.getRed()   * pulse),
            (int)(titleColor.getGreen() * pulse),
            (int)(titleColor.getBlue()  * pulse)));
        g.drawString("SPACE INVADERS", 46, 160);

        // Subtitle
        g.setFont(arcadeFont);
        g.setColor(new Color(200, 200, 200));
        g.drawString("ARCADE EDITION", 230, 200);

        // Animated enemy showcase row
        g.setFont(hudFont);
        g.setColor(new Color(100, 255, 100));
        for (int i = 0; i < 5; i++) {
            int ex = menuEnemyX + i * 70;
            if (ex + Constants.ENEMY_WIDTH < Constants.WINDOW_WIDTH) {
                int frame = (int)(t / 300) % 2;
                g.drawImage(util.AssetLoader.getEnemy(i % 5, frame), ex, 240, null);
            }
        }

        // Score legend
        g.setColor(new Color(180, 180, 180));
        g.setFont(hudFont);
        int ly = 340;
        String[][] rows = {
            {"=  30 PTS", "0"},
            {"=  20 PTS", "1"},
            {"=  10 PTS", "3"},
        };
        for (String[] row : rows) {
            g.drawImage(util.AssetLoader.getEnemy(Integer.parseInt(row[1]), 0), 310, ly - 14, null);
            g.drawString(row[0], 358, ly);
            ly += 36;
        }

        // High score
        g.setColor(new Color(255, 200, 0));
        g.setFont(arcadeFont);
        g.drawString("HIGH SCORE: " + scoreManager.getHighScore(), 256, 470);

        // Press enter
        long blink = t / 500;
        if (blink % 2 == 0) {
            g.setColor(new Color(0, 230, 180));
            g.drawString("PRESS ENTER TO START", 218, 530);
        }

        // Controls
        g.setColor(new Color(120, 120, 120));
        g.setFont(hudFont);
        g.drawString("A/D or ARROWS = MOVE    SPACE = SHOOT    P/ESC = PAUSE", 120, 590);
        g.drawString("M = MUTE    R = RESTART (in-game)", 240, 612);

        // Mute status
        if (soundManager.isMuted()) {
            g.setColor(Color.RED);
            g.drawString("[MUTED]", 380, 640);
        }
    }

    private void drawGame(Graphics2D g) {
        // Ground line
        g.setColor(new Color(0, 180, 80, 80));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(0, Constants.GROUND_Y, Constants.WINDOW_WIDTH, Constants.GROUND_Y);

        // Barriers
        for (Barrier b : barriers) b.draw(g);

        // Enemies
        for (Enemy e : enemies) e.draw(g);

        // Boss
        if (boss != null) boss.draw(g);

        // Bullets
        for (Bullet b : bullets) b.draw(g);

        // Power-ups
        for (PowerUp pu : powerUps) pu.draw(g);

        // Player
        if (player != null) player.draw(g);

        // Particles
        particles.draw(g);

        // Score popups
        for (ScorePopup sp : scorePopups) sp.draw(g);

        // HUD
        drawHUD(g);
    }

    private void drawHUD(Graphics2D g) {
        // HUD bar background
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.HUD_HEIGHT);

        g.setFont(hudFont);

        // Score
        g.setColor(new Color(0, 220, 180));
        g.drawString("SCORE", 12, 16);
        g.setColor(Color.WHITE);
        g.drawString(String.format("%06d", scoreManager.getScore()), 12, 32);

        // High score
        g.setColor(new Color(0, 220, 180));
        g.drawString("HI-SCORE", Constants.WINDOW_WIDTH / 2 - 44, 16);
        g.setColor(new Color(255, 200, 0));
        g.drawString(String.format("%06d", scoreManager.getHighScore()),
                     Constants.WINDOW_WIDTH / 2 - 30, 32);

        // Level
        g.setColor(new Color(0, 220, 180));
        g.drawString("LEVEL", Constants.WINDOW_WIDTH - 120, 16);
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(currentLevel), Constants.WINDOW_WIDTH - 100, 32);

        // Lives
        g.setColor(new Color(0, 220, 180));
        g.drawString("LIVES", Constants.WINDOW_WIDTH - 240, 16);
        if (player != null) {
            for (int i = 0; i < player.getLives(); i++) {
                g.drawImage(util.AssetLoader.getPlayer(),
                            Constants.WINDOW_WIDTH - 230 + i * 28, 20, 20, 16, null);
            }
        }

        // Power-up indicators
        if (player != null) {
            int px = 200;
            if (player.isRapidFireActive()) {
                g.setColor(new Color(0, 200, 255));
                g.drawString("[RAPID FIRE]", px, 26);
                px += 120;
            }
            if (player.isShieldActive()) {
                g.setColor(new Color(0, 255, 100));
                g.drawString("[SHIELD]", px, 26);
            }
        }

        // Mute indicator
        if (soundManager.isMuted()) {
            g.setColor(new Color(255, 80, 80, 180));
            g.setFont(new Font("Courier New", Font.BOLD, 11));
            g.drawString("♪ MUTED", Constants.WINDOW_WIDTH - 80, Constants.WINDOW_HEIGHT - 8);
        }
    }

    private void drawPause(Graphics2D g) {
        // Dim overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setFont(bigFont);
        g.setColor(new Color(0, 210, 255));
        String s = "PAUSED";
        int sw = g.getFontMetrics().stringWidth(s);
        g.drawString(s, (Constants.WINDOW_WIDTH - sw) / 2, Constants.WINDOW_HEIGHT / 2 - 20);

        g.setFont(arcadeFont);
        g.setColor(Color.WHITE);
        String r = "PRESS P TO RESUME";
        int rw = g.getFontMetrics().stringWidth(r);
        g.drawString(r, (Constants.WINDOW_WIDTH - rw) / 2, Constants.WINDOW_HEIGHT / 2 + 40);
    }

    private void drawTransition(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setFont(bigFont);
        g.setColor(new Color(0, 255, 120));
        String s = "LEVEL " + currentLevel;
        int sw = g.getFontMetrics().stringWidth(s);
        g.drawString(s, (Constants.WINDOW_WIDTH - sw) / 2, Constants.WINDOW_HEIGHT / 2);
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        g.setFont(bigFont);
        g.setColor(new Color(255, 60, 60));
        String go = "GAME OVER";
        int gw = g.getFontMetrics().stringWidth(go);
        g.drawString(go, (Constants.WINDOW_WIDTH - gw) / 2, 260);

        g.setFont(arcadeFont);
        g.setColor(Color.WHITE);
        String sc = "SCORE: " + scoreManager.getScore();
        int scw = g.getFontMetrics().stringWidth(sc);
        g.drawString(sc, (Constants.WINDOW_WIDTH - scw) / 2, 330);

        g.setColor(new Color(255, 200, 0));
        String hi = "HIGH SCORE: " + scoreManager.getHighScore();
        int hiw = g.getFontMetrics().stringWidth(hi);
        g.drawString(hi, (Constants.WINDOW_WIDTH - hiw) / 2, 375);

        // High score list
        List<Integer> top = SaveLoadManager.loadHighScores();
        g.setFont(hudFont);
        g.setColor(new Color(180, 180, 180));
        g.drawString("TOP SCORES:", (Constants.WINDOW_WIDTH / 2) - 60, 420);
        for (int i = 0; i < top.size(); i++) {
            String entry = (i + 1) + ". " + String.format("%06d", top.get(i));
            int ew = g.getFontMetrics().stringWidth(entry);
            g.setColor(i == 0 ? new Color(255, 200, 0) : new Color(180, 180, 180));
            g.drawString(entry, (Constants.WINDOW_WIDTH - ew) / 2, 445 + i * 22);
        }

        // Blink "press enter"
        long blink = System.currentTimeMillis() / 500;
        if (blink % 2 == 0) {
            g.setColor(new Color(0, 230, 180));
            g.setFont(arcadeFont);
            String pe = "PRESS ENTER TO CONTINUE";
            int pew = g.getFontMetrics().stringWidth(pe);
            g.drawString(pe, (Constants.WINDOW_WIDTH - pew) / 2, 590);
        }
    }

    // ── CollisionManager.GameEventListener ───────────────────────────────────

    @Override
    public void onEnemyKilled(int x, int y, int scoreValue) {
        // Colour based on score
        Color c = scoreValue >= 30 ? new Color(255, 80, 80)
                : scoreValue >= 20 ? new Color(255, 165, 0)
                : new Color(80, 220, 80);
        particles.spawnExplosion(x + Constants.ENEMY_WIDTH / 2,
                                 y + Constants.ENEMY_HEIGHT / 2, c,
                                 Constants.PARTICLES_PER_EXPLOSION);
        particles.triggerShake(3, 180);
        scorePopups.add(new ScorePopup("+" + scoreValue, x + 10, y, c));
        // Increase background tempo as enemies die
        soundManager.setTempo(Math.max(100, 600 - enemies.stream().filter(Enemy::isAlive).count() * 4));
    }

    @Override
    public void onPlayerHit() {
        particles.spawnExplosion(player.getX() + Constants.PLAYER_WIDTH / 2,
                                 player.getY() + Constants.PLAYER_HEIGHT / 2,
                                 new Color(255, 120, 0), 25);
        particles.triggerShake(8, 400);
    }

    @Override
    public void onBossKilled(int x, int y) {
        particles.spawnExplosion(x + Constants.BOSS_WIDTH / 2,
                                 y + Constants.BOSS_HEIGHT / 2,
                                 new Color(200, 0, 220), 50);
        particles.triggerShake(12, 600);
        scorePopups.add(new ScorePopup("BOSS +" + Constants.SCORE_BOSS,
                                       x, y, new Color(220, 0, 220)));
    }

    @Override
    public void onPowerUpCollected(int type) {
        switch (type) {
            case 0: player.activateRapidFire(); break;
            case 1: player.activateShield();    break;
            case 2: player.addLife();           break;
        }
    }

    @Override
    public void onAllEnemiesCleared() {
        currentLevel++;
        state = GameState.LEVEL_TRANSITION;
        levelTransitionEnd = System.currentTimeMillis() + TRANSITION_MS;
        soundManager.stopBackground();
    }

    // ── Inner: floating score popup ───────────────────────────────────────────

    private static class ScorePopup {
        private final String text;
        private int x;
        private float y;
        private final Color color;
        private float life = 1.0f;

        ScorePopup(String text, int x, int y, Color color) {
            this.text  = text;
            this.x     = x;
            this.y     = y;
            this.color = color;
        }

        /** @return true when expired */
        boolean update() {
            y    -= 1.2f;
            life -= 0.018f;
            return life <= 0;
        }

        void draw(Graphics2D g) {
            int alpha = (int)(life * 255);
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                                 Math.min(255, alpha)));
            g.setFont(new Font("Courier New", Font.BOLD, 13));
            g.drawString(text, x, (int) y);
        }
    }
}
