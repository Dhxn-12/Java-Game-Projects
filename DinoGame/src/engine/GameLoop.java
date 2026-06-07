package engine;

/**
 * Fixed-timestep game loop running on a background thread.
 * Core Java: Multithreading, Thread, Runnable.
 * Targets 60 FPS with sleep-based timing.
 * DSA: O(1) per tick overhead from loop management.
 */
public class GameLoop implements Runnable {

    private static final int TARGET_FPS  = 60;
    private static final long TICK_NS    = 1_000_000_000L / TARGET_FPS;

    private volatile boolean running = false;
    private Thread thread;

    private final Runnable onTick;   // update callback
    private final Runnable onRender; // repaint callback

    public GameLoop(Runnable onTick, Runnable onRender) {
        this.onTick   = onTick;
        this.onRender = onRender;
    }

    public void start() {
        running = true;
        thread  = new Thread(this, "GameLoopThread");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        while (running) {
            long now   = System.nanoTime();
            long delta = now - lastTime;

            if (delta >= TICK_NS) {
                lastTime = now;
                onTick.run();
                onRender.run();
            } else {
                // Sleep for remainder to avoid busy-waiting (reduces CPU usage)
                long sleepMs = (TICK_NS - delta) / 1_000_000L;
                if (sleepMs > 1) {
                    try { Thread.sleep(sleepMs - 1); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            }
        }
    }

    public boolean isRunning() { return running; }
}
