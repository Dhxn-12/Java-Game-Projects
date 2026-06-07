package util;

import model.Particle;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Manages all active particles.
 * DSA: ArrayList — O(n) update + draw, O(1) amortized add, O(n) removal.
 * Removal is done with Iterator to avoid ConcurrentModificationException.
 */
public class ParticleSystem {

    private final ArrayList<Particle> particles = new ArrayList<>();
    private static final Random RNG = new Random();

    /**
     * Spawns dust particles at the given position (e.g. on landing).
     * @param x center x
     * @param y center y
     */
    public void spawnDust(float x, float y) {
        for (int i = 0; i < 7; i++) {
            float vx = (RNG.nextFloat() - 0.5f) * 3.5f;
            float vy = -RNG.nextFloat() * 2.5f;
            particles.add(new Particle(x, y, vx, vy, 22, new Color(180, 170, 150), 3));
        }
    }

    /**
     * Spawns explosion particles for shield break or death.
     */
    public void spawnExplosion(float x, float y, Color color) {
        for (int i = 0; i < 14; i++) {
            float angle = (float)(Math.PI * 2 * i / 14);
            float speed = 1.5f + RNG.nextFloat() * 2.5f;
            float vx = (float) Math.cos(angle) * speed;
            float vy = (float) Math.sin(angle) * speed;
            particles.add(new Particle(x, y, vx, vy, 30, color, 4));
        }
    }

    /** Updates all particles and removes dead ones. Time: O(n). */
    public void update() {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update();
            if (p.isDead()) it.remove();
        }
    }

    /** Draws all particles. Time: O(n). */
    public void draw(Graphics2D g2d) {
        for (Particle p : particles) p.draw(g2d);
    }

    public void clear() { particles.clear(); }
}
