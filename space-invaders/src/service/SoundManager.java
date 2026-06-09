package service;

import javax.sound.sampled.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton SoundManager that synthesizes all game sounds programmatically.
 * No external audio files are required.
 *
 * All audio runs on a small thread-pool so game-loop latency is unaffected.
 */
public final class SoundManager {

    private static SoundManager instance;

    private boolean muted = false;

    // Background "heartbeat" tick state
    private volatile boolean bgRunning = false;
    private Thread bgThread;
    private volatile float bgTempo = 600; // ms between ticks (decreases with level)

    private final ExecutorService pool = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r, "sound-pool");
        t.setDaemon(true);
        return t;
    });

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void playShoot()     { if (!muted) pool.submit(() -> synth(shootSamples())); }
    public void playExplosion() { if (!muted) pool.submit(() -> synth(explosionSamples())); }
    public void playPowerUp()   { if (!muted) pool.submit(() -> synth(powerUpSamples())); }
    public void playInvader()   { if (!muted) pool.submit(() -> synth(invaderStepSamples())); }

    public void startBackground(int level) {
        stopBackground();
        bgTempo = Math.max(150, 600 - (level - 1) * 50);
        bgRunning = true;
        bgThread = new Thread(() -> {
            int tick = 0;
            while (bgRunning) {
                if (!muted) {
                    final int t = tick % 4;
                    pool.submit(() -> synth(bgTickSamples(t)));
                }
                tick++;
                try { Thread.sleep((long) bgTempo); }
                catch (InterruptedException e) { break; }
            }
        }, "bg-music");
        bgThread.setDaemon(true);
        bgThread.start();
    }

    public void stopBackground() {
        bgRunning = false;
        if (bgThread != null) bgThread.interrupt();
    }

    public void setTempo(float ms) { bgTempo = Math.max(80, ms); }

    public void toggleMute() { muted = !muted; }
    public boolean isMuted() { return muted; }

    // ── Sample Generators ─────────────────────────────────────────────────────

    private static final int SAMPLE_RATE = 44100;

    /** Play raw PCM samples. */
    private static void synth(byte[] samples) {
        try {
            AudioFormat fmt = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
            if (!AudioSystem.isLineSupported(info)) return;
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(fmt, samples.length);
            line.start();
            line.write(samples, 0, samples.length);
            line.drain();
            line.close();
        } catch (Exception ignored) {}
    }

    private static byte[] shootSamples() {
        int len = (int)(SAMPLE_RATE * 0.07);
        byte[] buf = new byte[len];
        for (int i = 0; i < len; i++) {
            double t   = i / (double) SAMPLE_RATE;
            double freq = 1200 - 600 * t / 0.07;
            buf[i] = (byte)(70 * Math.sin(2 * Math.PI * freq * t) * Math.exp(-t * 25));
        }
        return buf;
    }

    private static byte[] explosionSamples() {
        int len = (int)(SAMPLE_RATE * 0.25);
        byte[] buf = new byte[len];
        java.util.Random rng = new java.util.Random(42);
        for (int i = 0; i < len; i++) {
            double t    = i / (double) SAMPLE_RATE;
            double noise = (rng.nextDouble() * 2 - 1);
            double env  = Math.exp(-t * 12);
            buf[i] = (byte)(100 * noise * env);
        }
        return buf;
    }

    private static byte[] powerUpSamples() {
        int len = (int)(SAMPLE_RATE * 0.3);
        byte[] buf = new byte[len];
        for (int i = 0; i < len; i++) {
            double t    = i / (double) SAMPLE_RATE;
            double freq = 400 + 1200 * t / 0.3;
            buf[i] = (byte)(80 * Math.sin(2 * Math.PI * freq * t) * Math.exp(-t * 5));
        }
        return buf;
    }

    private static byte[] invaderStepSamples() {
        int len = (int)(SAMPLE_RATE * 0.06);
        byte[] buf = new byte[len];
        for (int i = 0; i < len; i++) {
            double t    = i / (double) SAMPLE_RATE;
            double freq = 160;
            buf[i] = (byte)(90 * Math.sin(2 * Math.PI * freq * t) * Math.exp(-t * 20));
        }
        return buf;
    }

    private static final double[] BG_FREQS = {130.81, 110.0, 98.0, 123.47}; // C3 A2 G2 B2

    private static byte[] bgTickSamples(int tick) {
        int len = (int)(SAMPLE_RATE * 0.06);
        byte[] buf = new byte[len];
        double freq = BG_FREQS[tick % BG_FREQS.length];
        for (int i = 0; i < len; i++) {
            double t = i / (double) SAMPLE_RATE;
            buf[i] = (byte)(85 * Math.sin(2 * Math.PI * freq * t) * Math.exp(-t * 20));
        }
        return buf;
    }
}
