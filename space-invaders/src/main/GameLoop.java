package main;

/**
 * Fixed-timestep game loop implementation.
 *
 * Note: The game uses a Swing Timer in GamePanel for simplicity and EDT safety.
 * This class demonstrates a pure-thread implementation with delta-time capping,
 * useful if you want to switch to a non-Swing rendering approach in the future.
 *
 * Usage example (not wired into the game by default):
 * <pre>
 *   GameLoop loop = new GameLoop(60, () -> { update(); repaint(); });
 *   loop.start();
 * </pre>
 */
public class GameLoop {

    public interface TickCallback { void tick(); }

    private final int targetFps;
    private final TickCallback callback;
    private volatile boolean running = false;
    private Thread thread;

    public GameLoop(int targetFps, TickCallback callback) {
        this.targetFps = targetFps;
        this.callback  = callback;
    }

    public void start() {
        running = true;
        thread  = new Thread(this::loop, "game-loop");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null) thread.interrupt();
    }

    private void loop() {
        final long frameNs = 1_000_000_000L / targetFps;
        long lastTime      = System.nanoTime();
        long lag           = 0L;

        while (running) {
            long now   = System.nanoTime();
            long delta = now - lastTime;
            lastTime   = now;
            lag       += delta;

            // Catch up if behind (cap at 5 frames to avoid spiral of death)
            int steps = 0;
            while (lag >= frameNs && steps < 5) {
                callback.tick();
                lag  -= frameNs;
                steps++;
            }

            // Sleep for remainder of frame
            long sleepNs = frameNs - (System.nanoTime() - now);
            if (sleepNs > 0) {
                try {
                    Thread.sleep(sleepNs / 1_000_000, (int)(sleepNs % 1_000_000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public boolean isRunning() { return running; }
}
