

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Immutable snapshot of the board + mutable game metadata.
 * Supports undo/redo via a move stack and replay via move history.
 *
 * <p>OOP Principles: Encapsulation, Serializable for save/load.</p>
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Board ─────────────────────────────────────────────────────────────────
    private char[][] board;
    private final int boardSize;
    private static final char EMPTY = ' ';

    // ── Turn Tracking ─────────────────────────────────────────────────────────
    private char currentTurn;      // 'X' or 'O'
    private int  moveCount;
    private boolean gameOver;
    private char winner;           // 'X', 'O', or 'D' (draw), ' ' (none)
    private int[] winLine;         // indices of winning cells [r1,c1,r2,c2,r3,c3]

    // ── Move Stacks (Undo / Redo) ─────────────────────────────────────────────
    private final Deque<int[]> undoStack;   // each entry: {row, col, prev}
    private final Deque<int[]> redoStack;

    // ── Match History ─────────────────────────────────────────────────────────
    private final List<int[]> moveHistory;  // full ordered list for replay
    private final String matchId;
    private final String timestamp;

    // ── Pause ─────────────────────────────────────────────────────────────────
    private boolean paused;

    // ── Constructor ───────────────────────────────────────────────────────────

    public GameState(int boardSize) {
        this.boardSize   = boardSize;
        this.board       = new char[boardSize][boardSize];
        this.currentTurn = 'X';
        this.moveCount   = 0;
        this.gameOver    = false;
        this.winner      = EMPTY;
        this.winLine     = null;
        this.undoStack   = new ArrayDeque<>();
        this.redoStack   = new ArrayDeque<>();
        this.moveHistory = new ArrayList<>();
        this.matchId     = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.timestamp   = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        initBoard();
    }

    public GameState() { this(3); }

    // ── Board Operations ──────────────────────────────────────────────────────

    private void initBoard() {
        for (char[] row : board) Arrays.fill(row, EMPTY);
    }

    public boolean isValidMove(int row, int col) {
        return !gameOver && !paused
                && row >= 0 && row < boardSize
                && col >= 0 && col < boardSize
                && board[row][col] == EMPTY;
    }

    /**
     * Places a move, updates undo stack, and switches turn.
     * @return true if the move was placed successfully
     */
    public boolean makeMove(int row, int col) {
        if (!isValidMove(row, col)) return false;

        undoStack.push(new int[]{row, col});
        redoStack.clear();          // new move invalidates redo history
        moveHistory.add(new int[]{row, col});

        board[row][col] = currentTurn;
        moveCount++;
        currentTurn = (currentTurn == 'X') ? 'O' : 'X';
        return true;
    }

    /**
     * Undoes the last move.
     * @return true if undo was possible
     */
    public boolean undo() {
        if (undoStack.isEmpty() || gameOver) return false;
        int[] last = undoStack.pop();
        redoStack.push(last);
        board[last[0]][last[1]] = EMPTY;
        moveCount--;
        currentTurn = (currentTurn == 'X') ? 'O' : 'X';
        // Remove from history
        if (!moveHistory.isEmpty()) moveHistory.remove(moveHistory.size() - 1);
        return true;
    }

    /**
     * Redoes the last undone move.
     * @return true if redo was possible
     */
    public boolean redo() {
        if (redoStack.isEmpty() || gameOver) return false;
        int[] next = redoStack.pop();
        undoStack.push(next);
        moveHistory.add(next);
        board[next[0]][next[1]] = currentTurn;
        moveCount++;
        currentTurn = (currentTurn == 'X') ? 'O' : 'X';
        return true;
    }

    // ── Win / Draw Detection ──────────────────────────────────────────────────

    /**
     * Checks the board for a winner or draw. Sets gameOver, winner, and winLine.
     */
    public void evaluate() {
        // Check rows
        for (int r = 0; r < boardSize; r++) {
            if (checkLine(r, 0, 0, 1)) return;
        }
        // Check cols
        for (int c = 0; c < boardSize; c++) {
            if (checkLine(0, c, 1, 0)) return;
        }
        // Check diagonals
        if (checkLine(0, 0, 1, 1)) return;
        if (checkLine(0, boardSize - 1, 1, -1)) return;

        // Draw check
        if (moveCount == boardSize * boardSize) {
            gameOver = true;
            winner   = 'D';
        }
    }

    private boolean checkLine(int startRow, int startCol, int dr, int dc) {
        char first = board[startRow][startCol];
        if (first == EMPTY) return false;
        int[] line = new int[boardSize * 2];
        line[0] = startRow; line[1] = startCol;
        for (int i = 1; i < boardSize; i++) {
            int r = startRow + i * dr;
            int c = startCol + i * dc;
            if (board[r][c] != first) return false;
            line[i * 2] = r;
            line[i * 2 + 1] = c;
        }
        gameOver = true;
        winner   = first;
        winLine  = line;
        return true;
    }

    // ── Deep Copy (for AI Minimax) ────────────────────────────────────────────

    public GameState deepCopy() {
        GameState copy = new GameState(boardSize);
        for (int r = 0; r < boardSize; r++)
            copy.board[r] = Arrays.copyOf(board[r], boardSize);
        copy.currentTurn = this.currentTurn;
        copy.moveCount   = this.moveCount;
        copy.gameOver    = this.gameOver;
        copy.winner      = this.winner;
        copy.winLine     = (winLine != null) ? Arrays.copyOf(winLine, winLine.length) : null;
        return copy;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public char getCell(int r, int c)   { return board[r][c]; }
    public char[][] getBoard()          { return board; }
    public int   getBoardSize()         { return boardSize; }
    public char  getCurrentTurn()       { return currentTurn; }
    public int   getMoveCount()         { return moveCount; }
    public boolean isGameOver()         { return gameOver; }
    public char  getWinner()            { return winner; }
    public int[] getWinLine()           { return winLine; }
    public boolean canUndo()            { return !undoStack.isEmpty(); }
    public boolean canRedo()            { return !redoStack.isEmpty(); }
    public boolean isPaused()           { return paused; }
    public void  setPaused(boolean p)   { this.paused = p; }
    public List<int[]> getMoveHistory() { return Collections.unmodifiableList(moveHistory); }
    public String getMatchId()          { return matchId; }
    public String getTimestamp()        { return timestamp; }
    public boolean isEmpty()            { return moveCount == 0; }
    public char EMPTY()                 { return EMPTY; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Match #").append(matchId).append(" | ").append(timestamp).append("\n");
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                sb.append(board[r][c] == EMPTY ? '.' : board[r][c]);
                if (c < boardSize - 1) sb.append("|");
            }
            sb.append("\n");
            if (r < boardSize - 1) sb.append("-".repeat(boardSize * 2 - 1)).append("\n");
        }
        return sb.toString();
    }
}