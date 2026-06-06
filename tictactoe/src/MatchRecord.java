

import java.io.Serializable;
import java.util.List;

/**
 * Immutable record of a completed match for history and replay.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class MatchRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String matchId;
    private final String timestamp;
    private final String playerXName;
    private final String playerOName;
    private final char   winner;          // 'X', 'O', or 'D'
    private final int    totalMoves;
    private final int    boardSize;
    private final long   durationMs;
    private final List<int[]> moveHistory;
    private final String gameMode;        // "PvP", "PvAI-Easy", etc.

    public MatchRecord(GameState state, Player playerX, Player playerO,
                       long durationMs, String gameMode) {
        this.matchId     = state.getMatchId();
        this.timestamp   = state.getTimestamp();
        this.playerXName = playerX.getName();
        this.playerOName = playerO.getName();
        this.winner      = state.getWinner();
        this.totalMoves  = state.getMoveCount();
        this.boardSize   = state.getBoardSize();
        this.durationMs  = durationMs;
        this.moveHistory = state.getMoveHistory();
        this.gameMode    = gameMode;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getMatchId()       { return matchId; }
    public String getTimestamp()     { return timestamp; }
    public String getPlayerXName()   { return playerXName; }
    public String getPlayerOName()   { return playerOName; }
    public char   getWinner()        { return winner; }
    public int    getTotalMoves()    { return totalMoves; }
    public int    getBoardSize()     { return boardSize; }
    public long   getDurationMs()    { return durationMs; }
    public List<int[]> getMoveHistory() { return moveHistory; }
    public String getGameMode()      { return gameMode; }

    public String getWinnerName() {
        return switch (winner) {
            case 'X' -> playerXName;
            case 'O' -> playerOName;
            default  -> "Draw";
        };
    }

    public String getDurationDisplay() {
        long sec = durationMs / 1000;
        return String.format("%dm %02ds", sec / 60, sec % 60);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s vs %s → %s (%s, %d moves, %s)",
                timestamp, playerXName, playerOName,
                getWinnerName(), gameMode, totalMoves, getDurationDisplay());
    }
}