package service;

import model.Enemy;
import util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages level state: enemy formation, swarm movement, and enemy shooting schedule.
 * Uses a Factory pattern for creating different enemy wave configurations.
 */
public class LevelManager {

    private int level = 1;
    private float enemySpeed;          // current horizontal speed (px/frame)
    private int   direction = 1;       // +1 = right, -1 = left
    private boolean pendingDrop = false;

    // Swarm animation tick
    private int  stepTick    = 0;
    private int  stepsPerAnim = 30;    // frames between animation frames
    private int  animFrame   = 0;

    // Enemy shooting
    private final Random rand = new Random();
    private long lastEnemyShot = 0;

    public LevelManager() {
        this.enemySpeed = Constants.ENEMY_BASE_SPEED;
    }

    /** Advance to next level and return a fresh enemy grid. */
    public List<Enemy> nextLevel(int currentLevel) {
        this.level = currentLevel;
        this.direction = 1;
        this.pendingDrop = false;
        this.stepTick = 0;
        this.animFrame = 0;
        this.enemySpeed = Constants.ENEMY_BASE_SPEED
                          + (level - 1) * Constants.ENEMY_SPEED_INCREMENT;
        return EnemyFactory.createWave(level);
    }

    /**
     * Update swarm movement for the current frame.
     * @param enemies live enemy list
     */
    public void updateSwarm(List<Enemy> enemies) {
        List<Enemy> alive = enemies.stream().filter(Enemy::isAlive)
                                   .collect(java.util.stream.Collectors.toList());
        if (alive.isEmpty()) return;

        // Speed bonus as formation thins
        float boost = (Constants.ENEMY_COLS * Constants.ENEMY_ROWS - alive.size())
                       * Constants.ENEMY_SPEED_BOOST_PER_KILL;
        float dx = (enemySpeed + boost) * direction;

        // Move entire formation
        for (Enemy e : alive) {
            e.setPosition(e.getX() + (int) dx, e.getY());
        }

        // Check edge
        int minX = alive.stream().mapToInt(Enemy::getX).min().orElse(0);
        int maxX = alive.stream().mapToInt(e -> e.getX() + e.getWidth()).max().orElse(0);

        if (maxX >= Constants.WINDOW_WIDTH - 4 || minX <= 4) {
            direction *= -1;
            // Drop
            for (Enemy e : alive) {
                e.setPosition(e.getX(), e.getY() + Constants.ENEMY_DROP_AMOUNT);
            }
        }

        // Animation toggle
        stepTick++;
        if (stepTick >= stepsPerAnim) {
            stepTick  = 0;
            animFrame = 1 - animFrame;
            for (Enemy e : enemies) e.setAnimFrame(animFrame);
        }
    }

    /**
     * Possibly fire a bullet from a random enemy in the bottom row.
     * @return the enemy that should shoot, or null
     */
    public Enemy getShootingEnemy(List<Enemy> enemies) {
        long now = System.currentTimeMillis();
        // Variable interval based on level (faster shooting at higher levels)
        int interval = Math.max(300, Constants.ENEMY_SHOOT_INTERVAL_MS - (level - 1) * 80);

        if (now - lastEnemyShot < interval) return null;
        lastEnemyShot = now;

        // Collect eligible shooters: lowest alive enemy per column
        List<Enemy> shooters = new ArrayList<>();
        for (int col = 0; col < Constants.ENEMY_COLS; col++) {
            int finalCol = col;
            enemies.stream()
                   .filter(e -> e.isAlive() && e.getCol() == finalCol)
                   .max(java.util.Comparator.comparingInt(Enemy::getRow))
                   .ifPresent(shooters::add);
        }
        if (shooters.isEmpty()) return null;
        return shooters.get(rand.nextInt(shooters.size()));
    }

    // ── EnemyFactory (inner static class) ────────────────────────────────────

    public static class EnemyFactory {

        /** Create a full enemy wave appropriate for the given level. */
        public static List<Enemy> createWave(int level) {
            List<Enemy> wave = new ArrayList<>();
            int rows = Math.min(Constants.ENEMY_ROWS, 3 + (level - 1) / 2);
            int cols = Constants.ENEMY_COLS;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    int x = Constants.ENEMY_START_X + col * Constants.ENEMY_H_SPACING;
                    int y = Constants.ENEMY_START_Y + row * Constants.ENEMY_V_SPACING;
                    wave.add(new Enemy(x, y, row, col));
                }
            }
            return wave;
        }

        /** Create the boss enemy for boss levels. */
        public static Enemy createBoss() {
            return new Enemy(-Constants.BOSS_WIDTH, Constants.BOSS_Y,
                             Constants.BOSS_HP, Enemy.Type.BOSS);
        }
    }

    public int   getLevel()     { return level; }
    public float getEnemySpeed(){ return enemySpeed; }
}
