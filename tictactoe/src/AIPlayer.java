

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract base for all AI strategies.
 * Demonstrates Abstraction and Polymorphism — each difficulty overrides {@code chooseMove()}.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public abstract class AIPlayer extends Player {

    protected final Random random = new Random();

    public AIPlayer(String name, char symbol, PlayerType type) {
        super(name, symbol, type);
    }

    /**
     * Core AI decision method — overridden by each difficulty subclass.
     * @param state current (read-only) game state
     * @return int[] { row, col } of chosen move
     */
    public abstract int[] chooseMove(GameState state);

    // ── Shared Utility Methods ────────────────────────────────────────────────

    protected List<int[]> getAvailableMoves(GameState state) {
        List<int[]> moves = new ArrayList<>();
        int size = state.getBoardSize();
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if (state.getCell(r, c) == state.EMPTY())
                    moves.add(new int[]{r, c});
        return moves;
    }

    protected int[] randomMove(GameState state) {
        List<int[]> moves = getAvailableMoves(state);
        return moves.isEmpty() ? null : moves.get(random.nextInt(moves.size()));
    }

    /** Finds an immediate winning or blocking move (row, col) or null. */
    protected int[] findWinOrBlock(GameState state, char symbol) {
        int size = state.getBoardSize();
        for (int[] move : getAvailableMoves(state)) {
            GameState copy = state.deepCopy();
            // Override currentTurn via reflection-free approach: use makeMove on copy
            // We simulate placing 'symbol' directly
            char[][] board = copy.getBoard();
            board[move[0]][move[1]] = symbol;
            copy.evaluate();
            if (copy.isGameOver() && copy.getWinner() == symbol)
                return move;
        }
        return null;
    }

    protected int[] getCenterMove(GameState state) {
        int mid = state.getBoardSize() / 2;
        return state.isValidMove(mid, mid) ? new int[]{mid, mid} : null;
    }

    protected int[] getCornerMove(GameState state) {
        int last = state.getBoardSize() - 1;
        int[][] corners = {{0,0},{0,last},{last,0},{last,last}};
        List<int[]> available = new ArrayList<>();
        for (int[] c : corners) if (state.isValidMove(c[0], c[1])) available.add(c);
        if (available.isEmpty()) return null;
        return available.get(random.nextInt(available.size()));
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// EASY AI — Random moves
// ═══════════════════════════════════════════════════════════════════════════════

class EasyAI extends AIPlayer {

    public EasyAI(char symbol) {
        super("AI (Easy)", symbol, PlayerType.AI_EASY);
    }

    @Override
    public int[] chooseMove(GameState state) {
        return randomMove(state);
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MEDIUM AI — Win > Block > Center > Corner > Random
// ═══════════════════════════════════════════════════════════════════════════════

class MediumAI extends AIPlayer {

    public MediumAI(char symbol) {
        super("AI (Medium)", symbol, PlayerType.AI_MEDIUM);
    }

    @Override
    public int[] chooseMove(GameState state) {
        char opponent = (getSymbol() == 'X') ? 'O' : 'X';

        // 1. Win immediately
        int[] win = findWinOrBlock(state, getSymbol());
        if (win != null) return win;

        // 2. Block opponent's win
        int[] block = findWinOrBlock(state, opponent);
        if (block != null) return block;

        // 3. Take center
        int[] center = getCenterMove(state);
        if (center != null) return center;

        // 4. Take a corner
        int[] corner = getCornerMove(state);
        if (corner != null) return corner;

        // 5. Random remaining
        return randomMove(state);
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HARD AI — Minimax with Alpha-Beta Pruning (unbeatable on 3x3)
// ═══════════════════════════════════════════════════════════════════════════════

class HardAI extends AIPlayer {

    private static final int WIN_SCORE  =  10;
    private static final int LOSE_SCORE = -10;
    private static final int DRAW_SCORE =   0;

    public HardAI(char symbol) {
        super("AI (Hard)", symbol, PlayerType.AI_HARD);
    }

    @Override
    public int[] chooseMove(GameState state) {
        List<int[]> moves = getAvailableMoves(state);
        if (moves.isEmpty()) return null;

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = moves.get(0);

        for (int[] move : moves) {
            GameState copy = state.deepCopy();
            copy.makeMove(move[0], move[1]);
            copy.evaluate();

            int score = minimax(copy, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (score > bestScore) {
                bestScore = score;
                bestMove  = move;
            }
        }
        return bestMove;
    }

    /**
     * Minimax with Alpha-Beta Pruning.
     * @param state    board state
     * @param depth    recursion depth
     * @param isMax    true if maximising player's turn
     * @param alpha    best score maximiser can guarantee
     * @param beta     best score minimiser can guarantee
     * @return heuristic score
     */
    private int minimax(GameState state, int depth, boolean isMax, int alpha, int beta) {
        if (state.isGameOver()) {
            char w = state.getWinner();
            if (w == getSymbol())   return WIN_SCORE  - depth;
            if (w == 'D')           return DRAW_SCORE;
            return LOSE_SCORE + depth;
        }

        List<int[]> moves = getAvailableMoves(state);
        if (moves.isEmpty()) return DRAW_SCORE;

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int[] move : moves) {
                GameState copy = state.deepCopy();
                copy.makeMove(move[0], move[1]);
                copy.evaluate();
                best = Math.max(best, minimax(copy, depth + 1, false, alpha, beta));
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break; // prune
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int[] move : moves) {
                GameState copy = state.deepCopy();
                copy.makeMove(move[0], move[1]);
                copy.evaluate();
                best = Math.min(best, minimax(copy, depth + 1, true, alpha, beta));
                beta = Math.min(beta, best);
                if (beta <= alpha) break; // prune
            }
            return best;
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Factory
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Factory that creates the correct AI implementation by difficulty.
 * Demonstrates Factory Pattern + Polymorphism.
 */
class AIPlayerFactory {

    private AIPlayerFactory() {}

    public static AIPlayer create(Player.PlayerType difficulty, char symbol) {
        return switch (difficulty) {
            case AI_EASY   -> new EasyAI(symbol);
            case AI_MEDIUM -> new MediumAI(symbol);
            case AI_HARD   -> new HardAI(symbol);
            default -> throw new IllegalArgumentException("Not an AI type: " + difficulty);
        };
    }
}