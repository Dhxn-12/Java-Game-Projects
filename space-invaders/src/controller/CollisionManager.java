package controller;

import model.*;
import service.ScoreManager;
import service.SoundManager;
import util.Constants;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Handles all collision detection using Axis-Aligned Bounding Box (AABB) tests.
 * Notifies the game of important events via a callback interface.
 */
public class CollisionManager {

    /** Callback so CollisionManager can notify the game of events. */
    public interface GameEventListener {
        void onEnemyKilled(int x, int y, int scoreValue);
        void onPlayerHit();
        void onBossKilled(int x, int y);
        void onPowerUpCollected(int type);
        void onAllEnemiesCleared();
    }

    private final GameEventListener listener;
    private final ScoreManager scoreManager;
    private final SoundManager soundManager;
    private final Random rand = new Random();

    public CollisionManager(GameEventListener listener,
                            ScoreManager scoreManager,
                            SoundManager soundManager) {
        this.listener     = listener;
        this.scoreManager = scoreManager;
        this.soundManager = soundManager;
    }

    /**
     * Main collision check. Call once per game-loop frame.
     *
     * @param player   Player reference
     * @param enemies  Live enemy list
     * @param bullets  All active bullets
     * @param barriers Barrier list
     * @param powerUps Active power-ups
     * @param boss     Boss reference (nullable)
     */
    public void checkAll(Player player,
                         List<Enemy>   enemies,
                         List<Bullet>  bullets,
                         List<Barrier> barriers,
                         List<PowerUp> powerUps,
                         Enemy         boss) {

        List<Bullet> toRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            if (!bullet.isActive()) continue;
            Rectangle br = bullet.getBounds();

            // ── Player bullets ──────────────────────────────────────────────
            if (bullet.getOwner() == Bullet.Owner.PLAYER) {

                // vs Boss
                if (boss != null && boss.isAlive() && boss.getBounds().intersects(br)) {
                    boss.hit(1);
                    bullet.deactivate();
                    soundManager.playExplosion();
                    if (!boss.isAlive()) {
                        scoreManager.add(Constants.SCORE_BOSS);
                        listener.onBossKilled(boss.getX(), boss.getY());
                    }
                    continue;
                }

                // vs Enemies
                boolean hitEnemy = false;
                for (Enemy e : enemies) {
                    if (!e.isAlive()) continue;
                    if (e.getBounds().intersects(br)) {
                        e.hit(1);
                        bullet.deactivate();
                        soundManager.playExplosion();
                        if (!e.isAlive()) {
                            scoreManager.add(e.getScoreValue());
                            listener.onEnemyKilled(e.getX(), e.getY(), e.getScoreValue());
                            maybeDropPowerUp(e, powerUps);
                        }
                        hitEnemy = true;
                        break;
                    }
                }
                if (hitEnemy) continue;

                // vs Barriers
                for (Barrier b : barriers) {
                    if (b.checkBulletCollision(br)) {
                        bullet.deactivate();
                        break;
                    }
                }

            } else {
                // ── Enemy bullets ───────────────────────────────────────────

                // vs Barriers first
                boolean hitBarrier = false;
                for (Barrier b : barriers) {
                    if (b.checkBulletCollision(br)) {
                        bullet.deactivate();
                        hitBarrier = true;
                        break;
                    }
                }
                if (hitBarrier) continue;

                // vs Player
                if (!player.isInvincible() && player.getBounds().intersects(br)) {
                    bullet.deactivate();
                    player.hit();
                    soundManager.playExplosion();
                    listener.onPlayerHit();
                }
            }
        }

        // ── Power-ups vs Player ─────────────────────────────────────────────
        for (PowerUp pu : powerUps) {
            if (!pu.isActive()) continue;
            if (player.getBounds().intersects(pu.getBounds())) {
                pu.deactivate();
                soundManager.playPowerUp();
                listener.onPowerUpCollected(pu.getType());
            }
        }

        // ── Enemy invasion (reached ground) ────────────────────────────────
        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;
            if (e.getY() + e.getHeight() >= Constants.GROUND_Y) {
                listener.onPlayerHit(); // instant kill
                return;
            }
        }

        // ── All enemies dead? ────────────────────────────────────────────────
        long alive = enemies.stream().filter(Enemy::isAlive).count();
        if (alive == 0 && (boss == null || !boss.isAlive())) {
            listener.onAllEnemiesCleared();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void maybeDropPowerUp(Enemy e, List<PowerUp> powerUps) {
        if (rand.nextDouble() < Constants.POWERUP_DROP_CHANCE) {
            int type = rand.nextInt(3);
            powerUps.add(new PowerUp(e.getX() + e.getWidth() / 2,
                                     e.getY() + e.getHeight(),
                                     type));
        }
    }
}
