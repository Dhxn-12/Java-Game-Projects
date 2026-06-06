

import javax.sound.sampled.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton SoundManager that synthesizes all audio procedurally.
 * No external sound files required — generates tones via PCM.
 *
 * <p>Runs playback on a dedicated thread pool to avoid blocking the EDT.</p>
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class SoundManager {

    private static SoundManager instance;
    private boolean soundEnabled = true;
    private float   volume       = 0.7f;
    private ExecutorService executor;

    // ── Singleton ─────────────────────────────────────────────────────────────

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    public void initialize() {
        executor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "SoundThread");
            t.setDaemon(true);
            return t;
        });
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void playClick()    { play(() -> beep(880, 60,  WaveType.SQUARE));  }
    public void playWin()      { play(() -> winFanfare());                      }
    public void playDraw()     { play(() -> drawChord());                       }
    public void playTick()     { play(() -> beep(440, 40,  WaveType.SINE));    }
    public void playWarning()  { play(() -> beep(330, 80,  WaveType.TRIANGLE));}
    public void playError()    { play(() -> beep(200, 120, WaveType.SQUARE));  }
    public void playHover()    { play(() -> beep(1200, 20, WaveType.SINE));    }

    // ── Playback Helpers ──────────────────────────────────────────────────────

    private void play(Runnable task) {
        if (!soundEnabled || executor == null) return;
        executor.submit(task);
    }

    private void winFanfare() {
        int[] notes = {523, 659, 784, 1047}; // C E G C
        for (int i = 0; i < notes.length; i++) {
            beep(notes[i], 120, WaveType.SINE);
            try { Thread.sleep(80); } catch (InterruptedException ignored) {}
        }
    }

    private void drawChord() {
        // Play two notes "disappointingly flat"
        beep(440, 200, WaveType.TRIANGLE);
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        beep(415, 200, WaveType.TRIANGLE);
    }

    // ── PCM Synthesis ─────────────────────────────────────────────────────────

    private enum WaveType { SINE, SQUARE, TRIANGLE }

    private void beep(int freq, int durationMs, WaveType type) {
        try {
            int sampleRate = 44100;
            int numSamples = sampleRate * durationMs / 1000;
            byte[] buf = new byte[2 * numSamples];

            for (int i = 0; i < numSamples; i++) {
                double t   = (double) i / sampleRate;
                double ang = 2.0 * Math.PI * freq * t;
                double val = switch (type) {
                    case SINE     -> Math.sin(ang);
                    case SQUARE   -> Math.signum(Math.sin(ang));
                    case TRIANGLE -> 2.0 / Math.PI * Math.asin(Math.sin(ang));
                };

                // Fade in/out (10 ms)
                int fade = sampleRate / 100;
                if (i < fade)               val *= (double) i / fade;
                if (i > numSamples - fade)  val *= (double)(numSamples - i) / fade;

                short s = (short)(val * volume * Short.MAX_VALUE);
                buf[2 * i]     = (byte)(s & 0xff);
                buf[2 * i + 1] = (byte)((s >> 8) & 0xff);
            }

            AudioFormat fmt = new AudioFormat(sampleRate, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
            if (!AudioSystem.isLineSupported(info)) return;

            try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
                line.open(fmt);
                line.start();
                line.write(buf, 0, buf.length);
                line.drain();
            }
        } catch (Exception ignored) {
            // Sound unavailable — silently skip
        }
    }

    // ── Settings ─────────────────────────────────────────────────────────────

    public boolean isSoundEnabled()          { return soundEnabled; }
    public void   setSoundEnabled(boolean e) { this.soundEnabled = e; }
    public float  getVolume()                { return volume; }
    public void   setVolume(float v)         { this.volume = Math.max(0f, Math.min(1f, v)); }

    public void shutdown() {
        if (executor != null) executor.shutdownNow();
    }
}
