package service;

import model.Particle;
import util.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages all active particles and the screen-shake effect.
 */
public class ParticleSystem {

    private final List<Particle> particles = new ArrayList<>();
    private final Random rand = new Random();

    // Screen shake
    private long   shakeUntil  = 0;
    private float  shakeMag    = 0;
    private int    shakeX      = 0;
    private int    shakeY      = 0;

    public void spawnExplosion(int x, int y, Color baseColor, int count) {
        for (int i = 0; i < count; i++) {
            float angle = (float)(rand.nextDouble() * Math.PI * 2);
            float speed = 1.5f + rand.nextFloat() * 4f;
            float vx = (float)(Math.cos(angle) * speed);
            float vy = (float)(Math.sin(angle) * speed) - rand.nextFloat() * 2;
            float life = 0.6f + rand.nextFloat() * 0.6f;
            float size = 2f + rand.nextFloat() * 5f;

            // Randomize color slightly
            int r = clamp(baseColor.getRed()   + rand.nextInt(80) - 40, 0, 255);
            int g = clamp(baseColor.getGreen() + rand.nextInt(80) - 40, 0, 255);
            int b = clamp(baseColor.getBlue()  + rand.nextInt(80) - 40, 0, 255);
            Color c = new Color(r, g, b);

            particles.add(new Particle(x, y, vx, vy, life, size, c));
        }
    }

    public void triggerShake(float magnitude, long durationMs) {
        shakeMag   = magnitude;
        shakeUntil = System.currentTimeMillis() + durationMs;
    }

    public void update() {
        particles.removeIf(p -> { p.update(); return !p.isAlive(); });

        // Update shake offset
        if (System.currentTimeMillis() < shakeUntil) {
            shakeX = (int)((rand.nextFloat() * 2 - 1) * shakeMag);
            shakeY = (int)((rand.nextFloat() * 2 - 1) * shakeMag);
        } else {
            shakeX = shakeY = 0;
        }
    }

    public void draw(Graphics2D g) {
        for (Particle p : particles) p.draw(g);
    }

    public int  getShakeX()     { return shakeX; }
    public int  getShakeY()     { return shakeY; }
    public boolean isShaking()  { return System.currentTimeMillis() < shakeUntil; }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
