package engine;

import factory.*;
import model.*;
import save.SaveManager;
import state.GameState;
import util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Core game engine — manages state, updates, and rendering data.
 * Pattern: MVC Model layer. GamePanel (View) calls draw methods.
 * DSA:
 *   - ArrayList<Obstacle>  obstacles — O(n) update/collision per frame
 *   - ArrayList<PowerUp>   powerUps  — O(n) update/collision per frame
 *   - ArrayList<Particle>  via ParticleSystem
 *   - HashMap              via AchievementManager
 */
public class GameEngine {

    // --- Screen constants ---
    public static final int WIDTH    = 800;
    public static final int HEIGHT   = 200;
    public static final int GROUND_Y = 155;

    // --- Game objects ---
    private final Dino dino;
    private final ArrayList<Obstacle> obstacles  = new ArrayList<>();
    private final ArrayList<PowerUp>  powerUps   = new ArrayList<>();
    private final ParticleSystem      particles  = new ParticleSystem();
    private final DifficultyManager   difficulty = new DifficultyManager();
    private final AchievementManager  achievements = new AchievementManager();

    // --- Game state ---
    private GameState state = GameState.MENU;
    private int   score;
    private float speed;
    private int   spawnTimer;
    private int   frame;
    private long  sessionStart;

    // --- Power-up timers ---
    private boolean shieldActive;
    private int     shieldTimer;
    private boolean slowMode;
    private int     slowTimer;
    private int     multiplier;
    private int     multiplierTimer;

    // --- Night mode ---
    private boolean nightMode;
    private float   nightAlpha;

    // --- Ground scroll ---
    private float groundOffset;

    // --- Cloud positions ---
    private float cloud1X, cloud2X;

    // --- Per-session stats ---
    private int sessionJumps;
    private boolean hitOccurred;

    // --- Persistent stats ---
    private final GameStats stats;

    // --- Callback for achievement popup in view ---
    private java.util.function.Consumer<String> achievementCallback;

    public GameEngine() {
        this.dino  = new Dino(GROUND_Y);
        this.stats = SaveManager.load();
        this.cloud1X = WIDTH * 0.7f;
        this.cloud2X = WIDTH * 0.3f;

        achievements.setOnUnlock(a -> {
            if (achievementCallback != null)
                achievementCallback.accept(a.getName());
        });
    }

    public void setAchievementCallback(java.util.function.Consumer<String> cb) {
        this.achievementCallback = cb;
    }

    // ─── STATE TRANSITIONS ────────────────────────────────────────────────────

    public void startGame() {
        state         = GameState.PLAYING;
        score         = 0;
        frame         = 0;
        speed         = difficulty.getSpeed(0);
        spawnTimer    = 70;
        shieldActive  = false; shieldTimer    = 0;
        slowMode      = false; slowTimer      = 0;
        multiplier    = 1;    multiplierTimer = 0;
        nightMode     = false; nightAlpha     = 0;
        groundOffset  = 0;
        sessionJumps  = 0;
        hitOccurred   = false;
        obstacles.clear();
        powerUps.clear();
        particles.clear();
        dino.reset();
        achievements.resetSession();
        stats.incrementGames();
        sessionStart = System.currentTimeMillis();
    }

    public void togglePause() {
        if (state == GameState.PLAYING) state = GameState.PAUSED;
        else if (state == GameState.PAUSED) state = GameState.PLAYING;
    }

    public void endGame() {
        state = GameState.GAME_OVER;
        dino.setDead(true);
        stats.updateHighScore(score);
        stats.addPlayTime(System.currentTimeMillis() - sessionStart);
        stats.incrementCollisions();
        SaveManager.save(stats);
        particles.spawnExplosion(dino.getX() + Dino.DINO_W / 2f,
                                 dino.getY() + Dino.DINO_H / 2f,
                                 new Color(255, 80, 50));
    }

    // ─── MAIN UPDATE ──────────────────────────────────────────────────────────

    /**
     * Called every frame by GameLoop (60 FPS).
     * Time complexity: O(n) where n = number of active obstacles + power-ups.
     */
    public void update() {
        if (state != GameState.PLAYING) return;
        frame++;
        groundOffset += slowMode ? speed * 0.4f : speed;

        // Score increments every frame; multiplier and slow affect rate
        score += slowMode ? (int)(0.05 * multiplier) : (int)(0.08 * multiplier);
        speed = difficulty.getSpeed(score);

        // Spawn obstacles via queue-like timer
        if (spawnTimer <= 0) {
            spawnObstacle();
            spawnTimer = difficulty.getSpawnInterval(score);
        } else {
            spawnTimer--;
        }

        // Night mode transition at score 500
        if (score >= 500) {
            nightMode = true;
            nightAlpha = Math.min(0.82f, nightAlpha + 0.003f);
        }

        // Scroll clouds
        cloud1X -= 0.5f; cloud2X -= 0.3f;
        if (cloud1X < -110) cloud1X = WIDTH + 80;
        if (cloud2X < -130) cloud2X = WIDTH + 120;

        // Update dino
        dino.update(slowMode);

        // Update obstacles — O(n), remove off-screen
        Iterator<Obstacle> obsIt = obstacles.iterator();
        while (obsIt.hasNext()) {
            Obstacle o = obsIt.next();
            o.update(speed, slowMode);
            if (o.isDead()) { obsIt.remove(); continue; }
            // Collision check O(1) per obstacle
            if (!shieldActive && CollisionDetector.overlaps(dino.getHitbox(), o.getHitbox())) {
                endGame();
                return;
            }
        }

        // Update power-ups — O(n)
        Iterator<PowerUp> puIt = powerUps.iterator();
        while (puIt.hasNext()) {
            PowerUp pu = puIt.next();
            pu.update(speed, slowMode);
            if (pu.isOffScreen() || pu.isCollected()) { puIt.remove(); continue; }
            if (CollisionDetector.overlaps(dino.getHitbox(), pu.getHitbox())) {
                applyPowerUp(pu);
                pu.collect();
            }
        }

        // Power-up timers
        if (shieldTimer  > 0 && --shieldTimer  == 0) shieldActive = false;
        if (slowTimer    > 0 && --slowTimer    == 0) slowMode    = false;
        if (multiplierTimer > 0 && --multiplierTimer == 0) multiplier = 1;

        // Particles
        particles.update();

        // Achievement checks
        achievements.check("first_jump",   sessionJumps >= 1);
        achievements.check("score_100",    score >= 100);
        achievements.check("score_500",    score >= 500);
        achievements.check("score_1000",   score >= 1000);
        achievements.check("score_2000",   score >= 2000);
        achievements.check("night_mode",   nightMode);
        achievements.check("no_crash_100", score >= 100 && !hitOccurred);
    }

    private void spawnObstacle() {
        obstacles.add(ObstacleFactory.create(score, WIDTH, GROUND_Y));
        if (PowerUpFactory.shouldSpawn(score)) {
            powerUps.add(PowerUpFactory.create(WIDTH, GROUND_Y));
        }
    }

    private void applyPowerUp(PowerUp pu) {
        switch (pu.getType()) {
            case SHIELD      -> { shieldActive = true; shieldTimer = 320;
                                  if (achievementCallback != null) achievementCallback.accept("Shield Bearer!"); }
            case SLOW_MOTION -> { slowMode = true; slowTimer = 260; }
            case SCORE_X2    -> { multiplier = 2; multiplierTimer = 320; }
        }
        achievements.check("shield_used", pu.getType() == PowerUpType.SHIELD);
    }

    // ─── DRAWING ──────────────────────────────────────────────────────────────

    /**
     * Draws the full game frame to the provided Graphics2D.
     * Called by GamePanel.paintComponent().
     */
    public void draw(Graphics2D g2d) {
        drawBackground(g2d);
        drawGround(g2d);
        particles.draw(g2d);
        for (PowerUp pu : powerUps)  pu.draw(g2d);
        for (Obstacle o : obstacles) o.draw(g2d);
        dino.draw(g2d, shieldActive);
    }

    private void drawBackground(Graphics2D g2d) {
        // Sky
        g2d.setColor(new Color(247, 243, 235));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Night overlay
        if (nightAlpha > 0) {
            g2d.setColor(new Color(10, 10, 30, (int)(nightAlpha * 200)));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            // Stars
            if (nightAlpha > 0.3f) {
                g2d.setColor(new Color(255, 255, 200, (int)(nightAlpha * 200)));
                for (int i = 0; i < 45; i++) {
                    int sx = (int)((i * 97 + frame * 0.2) % WIDTH);
                    int sy = (int)((i * 53) % (GROUND_Y - 20));
                    g2d.fillRect(sx, sy, 2, 2);
                }
            }
        }

        // Clouds
        g2d.setColor(new Color(180, 175, 165, (int)(nightMode ? 80 : 160)));
        drawCloud(g2d, cloud1X, 28, 70, 22);
        drawCloud(g2d, cloud2X, 52, 90, 18);
    }

    private void drawCloud(Graphics2D g2d, float cx, float cy, int w, int h) {
        g2d.fillOval((int)cx - w/2, (int)cy - h/2, w, h);
        g2d.fillOval((int)cx - w/2 - (int)(w*0.35), (int)cy, (int)(w*0.6), (int)(h*0.7));
        g2d.fillOval((int)cx + (int)(w*0.15),        (int)cy, (int)(w*0.55),(int)(h*0.7));
    }

    private void drawGround(Graphics2D g2d) {
        g2d.setColor(new Color(130, 120, 110));
        g2d.fillRect(0, GROUND_Y, WIDTH, 2);
        g2d.setColor(new Color(180, 170, 160));
        for (int i = 0; i < WIDTH / 20 + 2; i++) {
            int xp = (int)(i * 20 - groundOffset % 20);
            if (((int)((i * 20 + groundOffset) / 20)) % 3 == 0)
                g2d.fillRect(xp, GROUND_Y + 4, 9, 2);
        }
    }

    // ─── GETTERS ──────────────────────────────────────────────────────────────

    public GameState getState()      { return state; }
    public int  getScore()           { return score; }
    public int  getHighScore()       { return stats.getHighScore(); }
    public boolean isShieldActive()  { return shieldActive; }
    public boolean isSlowMode()      { return slowMode; }
    public int  getMultiplier()      { return multiplier; }
    public int  getShieldTimer()     { return shieldTimer; }
    public int  getSlowTimer()       { return slowTimer; }
    public int  getMultiplierTimer() { return multiplierTimer; }
    public GameStats getStats()      { return stats; }
    public Dino getDino()            { return dino; }

    public void onJump() {
        if (state == GameState.PLAYING) {
            boolean jumped = dino.jump();
            if (jumped) {
                sessionJumps++;
                achievements.check("first_jump", true);
                if (sessionJumps > 1) achievements.check("double_jump", true);
                particles.spawnDust(dino.getX() + Dino.DINO_W / 2f, dino.getY() + Dino.DINO_H);
                stats.incrementJumps();
            }
        } else if (state == GameState.MENU || state == GameState.GAME_OVER) {
            startGame();
        }
    }

    public void onDuck(boolean ducking) {
        if (state == GameState.PLAYING) dino.setDucking(ducking);
    }
}
