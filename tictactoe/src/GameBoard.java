
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Central game controller (MVC — Controller).
 * Manages game flow, AI turns, timers, and notifies registered listeners.
 *
 * <p>OOP Principles: Encapsulation, Observer pattern via GameEventListener.</p>
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class GameBoard {

    // ── Game Configuration ────────────────────────────────────────────────────
    private int boardSize;
    private GameMode gameMode;
    private int turnTimeLimitSeconds = 30;

    // ── State ─────────────────────────────────────────────────────────────────
    private GameState state;
    private Player playerX;
    private Player playerO;
    private AIPlayer aiPlayer;

    // ── Turn Timer ────────────────────────────────────────────────────────────
    private Timer turnTimer;
    private int   secondsRemaining;
    private long  matchStartTime;
    private long  turnStartTime;

    // ── Observers ─────────────────────────────────────────────────────────────
    private final List<GameEventListener> listeners = new ArrayList<>();

    // ── Enums ─────────────────────────────────────────────────────────────────
    public enum GameMode { PVP, PV_AI }

    // ── Constructor ───────────────────────────────────────────────────────────

    public GameBoard() {
        this.boardSize = 3;
        this.gameMode  = GameMode.PVP;
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    public void configurePvP(String nameX, String nameO, int size) {
        this.boardSize = size;
        this.gameMode  = GameMode.PVP;
        this.playerX   = new Player(nameX, 'X');
        this.playerO   = new Player(nameO, 'O');
        this.aiPlayer  = null;
    }

    public void configurePvAI(String humanName, Player.PlayerType difficulty, int size) {
        this.boardSize = size;
        this.gameMode  = GameMode.PV_AI;
        this.playerX   = new Player(humanName, 'X');
        this.aiPlayer  = AIFactory.create(difficulty, 'O');
        this.playerO   = aiPlayer;
    }

    public void startNewGame() {
        stopTimer();
        state = new GameState(boardSize);
        matchStartTime = System.currentTimeMillis();
        fireEvent(GameEvent.GAME_STARTED, null);
        startTurnTimer();

        // If AI goes first (won't happen as X is always human, but future-proof)
        triggerAIIfNeeded();
    }

    // ── Move Handling ─────────────────────────────────────────────────────────

    /**
     * Human clicks cell (row, col).
     */
    public boolean humanMove(int row, int col) {
        if (state == null || state.isGameOver() || state.isPaused()) return false;
        // Block move if it's AI's turn in PvAI
        if (gameMode == GameMode.PV_AI && state.getCurrentTurn() == 'O') return false;

        if (!state.makeMove(row, col)) return false;

        SoundManager.getInstance().playClick();
        stopTimer();
        state.evaluate();
        fireEvent(GameEvent.MOVE_MADE, new int[]{row, col});

        if (state.isGameOver()) {
            handleGameOver();
        } else {
            startTurnTimer();
            triggerAIIfNeeded();
        }
        return true;
    }

    /**
     * Triggers AI move asynchronously if it's the AI's turn.
     */
    private void triggerAIIfNeeded() {
        if (gameMode != GameMode.PV_AI || aiPlayer == null) return;
        if (state.getCurrentTurn() != aiPlayer.getSymbol()) return;
        if (state.isGameOver()) return;

        fireEvent(GameEvent.AI_THINKING, null);

        // AI "thinking" delay for UX
        new Thread(() -> {
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}

            int[] move = aiPlayer.chooseMove(state);
            if (move != null) {
                state.makeMove(move[0], move[1]);
                SoundManager.getInstance().playClick();
                stopTimer();
                state.evaluate();

                fireEvent(GameEvent.MOVE_MADE, move);
                if (state.isGameOver()) {
                    handleGameOver();
                } else {
                    startTurnTimer();
                }
            }
        }).start();
    }

    // ── Undo / Redo ───────────────────────────────────────────────────────────

    public boolean undo() {
        if (state == null) return false;
        boolean ok = state.undo();
        // In PvAI, undo twice so human gets their turn back
        if (ok && gameMode == GameMode.PV_AI) state.undo();
        if (ok) fireEvent(GameEvent.MOVE_MADE, null);
        return ok;
    }

    public boolean redo() {
        if (state == null) return false;
        boolean ok = state.redo();
        if (ok && gameMode == GameMode.PV_AI) {
            state.redo();
            triggerAIIfNeeded();
        }
        if (ok) fireEvent(GameEvent.MOVE_MADE, null);
        return ok;
    }

    // ── Pause / Resume ────────────────────────────────────────────────────────

    public void pause() {
        if (state != null && !state.isGameOver()) {
            state.setPaused(true);
            stopTimer();
            fireEvent(GameEvent.GAME_PAUSED, null);
        }
    }

    public void resume() {
        if (state != null && state.isPaused()) {
            state.setPaused(false);
            fireEvent(GameEvent.GAME_RESUMED, null);
            startTurnTimer();
        }
    }

    // ── Timer ─────────────────────────────────────────────────────────────────

    private void startTurnTimer() {
        stopTimer();
        secondsRemaining = turnTimeLimitSeconds;
        turnStartTime    = System.currentTimeMillis();
        turnTimer = new Timer("TurnTimer", true);
        turnTimer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                secondsRemaining--;
                fireEvent(GameEvent.TIMER_TICK, new int[]{secondsRemaining});
                if (secondsRemaining <= 0) {
                    stopTimer();
                    handleTimeOut();
                }
            }
        }, 1000, 1000);
    }

    private void stopTimer() {
        if (turnTimer != null) { turnTimer.cancel(); turnTimer = null; }
    }

    private void handleTimeOut() {
        // Auto-move: random cell for the current player
        if (state == null || state.isGameOver()) return;
        List<int[]> empties = new ArrayList<>();
        for (int r = 0; r < boardSize; r++)
            for (int c = 0; c < boardSize; c++)
                if (state.getCell(r,c) == state.EMPTY()) empties.add(new int[]{r,c});
        if (!empties.isEmpty()) {
            int[] move = empties.get((int)(Math.random() * empties.size()));
            state.makeMove(move[0], move[1]);
            state.evaluate();
            fireEvent(GameEvent.MOVE_MADE, move);
            if (state.isGameOver()) { handleGameOver(); } else { startTurnTimer(); }
        }
    }

    // ── Game Over ─────────────────────────────────────────────────────────────

    private void handleGameOver() {
        stopTimer();
        char winner = state.getWinner();
        long durationMs = System.currentTimeMillis() - matchStartTime;
        String modeName = (gameMode == GameMode.PVP) ? "PvP" :
                "PvAI-" + playerO.getType().name().replace("AI_","");

        // Update player stats
        if (winner == 'X')      { playerX.recordWin(); playerO.recordLoss(); }
        else if (winner == 'O') { playerO.recordWin(); playerX.recordLoss(); }
        else                    { playerX.recordDraw(); playerO.recordDraw(); }

        // Save match record
        MatchRecord record = new MatchRecord(state, playerX, playerO, durationMs, modeName);
        ScoreManager.getInstance().saveMatch(record);

        // Sound
        if (winner == 'D') SoundManager.getInstance().playDraw();
        else               SoundManager.getInstance().playWin();

        fireEvent(GameEvent.GAME_OVER, null);
    }

    // ── Observer Pattern ──────────────────────────────────────────────────────

    public void addListener(GameEventListener l) { listeners.add(l); }
    public void removeListener(GameEventListener l) { listeners.remove(l); }

    private void fireEvent(GameEvent event, int[] data) {
        for (GameEventListener l : listeners) {
            javax.swing.SwingUtilities.invokeLater(() -> l.onGameEvent(event, data));
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public GameState getState()            { return state; }
    public Player    getPlayerX()          { return playerX; }
    public Player    getPlayerO()          { return playerO; }
    public GameMode  getGameMode()         { return gameMode; }
    public int       getBoardSize()        { return boardSize; }
    public int       getSecondsRemaining() { return secondsRemaining; }
    public boolean   isPaused()            { return state != null && state.isPaused(); }

    public Player getCurrentPlayer() {
        if (state == null) return playerX;
        return (state.getCurrentTurn() == 'X') ? playerX : playerO;
    }

    public void setTurnTimeLimit(int seconds) { this.turnTimeLimitSeconds = seconds; }

    public void shutdown() { stopTimer(); }
}