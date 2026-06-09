package util;

/**
 * Central constants file for Space Invaders.
 * All magic numbers and config values live here.
 */
public final class Constants {

    private Constants() {} // Prevent instantiation

    // ── Window ──────────────────────────────────────────────────────────────
    public static final int WINDOW_WIDTH   = 900;
    public static final int WINDOW_HEIGHT  = 700;
    public static final String GAME_TITLE  = "SPACE INVADERS";

    // ── Game Loop ────────────────────────────────────────────────────────────
    public static final int TARGET_FPS      = 60;
    public static final long FRAME_TIME_NS  = 1_000_000_000L / TARGET_FPS;

    // ── Player ───────────────────────────────────────────────────────────────
    public static final int PLAYER_WIDTH        = 48;
    public static final int PLAYER_HEIGHT       = 36;
    public static final int PLAYER_SPEED        = 5;
    public static final int PLAYER_START_X      = WINDOW_WIDTH / 2 - PLAYER_WIDTH / 2;
    public static final int PLAYER_START_Y      = WINDOW_HEIGHT - 90;
    public static final int PLAYER_LIVES        = 3;
    public static final long PLAYER_INVINCIBLE_MS = 2000; // ms after being hit
    public static final long PLAYER_SHOOT_COOLDOWN_MS = 350;

    // ── Bullet ───────────────────────────────────────────────────────────────
    public static final int PLAYER_BULLET_WIDTH  = 4;
    public static final int PLAYER_BULLET_HEIGHT = 14;
    public static final int PLAYER_BULLET_SPEED  = 10;

    public static final int ENEMY_BULLET_WIDTH   = 4;
    public static final int ENEMY_BULLET_HEIGHT  = 12;
    public static final int ENEMY_BULLET_SPEED   = 5;

    // ── Enemy ────────────────────────────────────────────────────────────────
    public static final int ENEMY_COLS         = 11;
    public static final int ENEMY_ROWS         = 5;
    public static final int ENEMY_WIDTH        = 36;
    public static final int ENEMY_HEIGHT       = 28;
    public static final int ENEMY_H_SPACING    = 58;
    public static final int ENEMY_V_SPACING    = 50;
    public static final int ENEMY_START_X      = 60;
    public static final int ENEMY_START_Y      = 80;
    public static final int ENEMY_DROP_AMOUNT  = 18;
    public static final float ENEMY_BASE_SPEED = 1.2f;
    public static final float ENEMY_SPEED_INCREMENT = 0.3f; // per level
    public static final int ENEMY_SHOOT_INTERVAL_MS = 1200; // avg ms between shots
    public static final float ENEMY_SPEED_BOOST_PER_KILL = 0.04f; // speed up as fewer remain

    // Scoring
    public static final int SCORE_ROW_0 = 30; // bottom row
    public static final int SCORE_ROW_1 = 20;
    public static final int SCORE_ROW_2 = 20;
    public static final int SCORE_ROW_3 = 10;
    public static final int SCORE_ROW_4 = 10; // top row
    public static final int SCORE_BOSS  = 200;

    // ── Barriers ─────────────────────────────────────────────────────────────
    public static final int BARRIER_COUNT   = 4;
    public static final int BARRIER_Y       = WINDOW_HEIGHT - 160;
    public static final int BARRIER_WIDTH   = 64;
    public static final int BARRIER_HEIGHT  = 48;
    public static final int BARRIER_HP      = 12;

    // ── Boss ─────────────────────────────────────────────────────────────────
    public static final int BOSS_WIDTH        = 72;
    public static final int BOSS_HEIGHT       = 32;
    public static final int BOSS_SPEED        = 2;
    public static final int BOSS_Y            = 30;
    public static final int BOSS_INTERVAL_LEVELS = 3; // boss every N levels
    public static final int BOSS_HP           = 10;

    // ── Power-ups ────────────────────────────────────────────────────────────
    public static final int POWERUP_WIDTH    = 28;
    public static final int POWERUP_HEIGHT   = 28;
    public static final int POWERUP_SPEED    = 2;
    public static final long POWERUP_DURATION_MS   = 8000;
    public static final double POWERUP_DROP_CHANCE = 0.07; // 7% per kill

    // ── Particles ────────────────────────────────────────────────────────────
    public static final int PARTICLES_PER_EXPLOSION = 18;

    // ── HUD / UI ─────────────────────────────────────────────────────────────
    public static final int HUD_HEIGHT     = 40;
    public static final int GROUND_Y       = WINDOW_HEIGHT - 55;

    // ── High Score File ───────────────────────────────────────────────────────
    public static final String SAVE_FILE = "highscores.dat";
    public static final int MAX_HIGH_SCORES = 5;
}
