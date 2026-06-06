
/**
 * Public factory for creating AI players by difficulty level.
 * Bridges package visibility to the rest of the application.
 *
 * @author TicTacToe Pro Team
 * @version 2.0.0
 */
public class AIFactory {

    private AIFactory() {}

    /**
     * Creates and returns an AI player of the given difficulty.
     * @param difficulty AI_EASY | AI_MEDIUM | AI_HARD
     * @param symbol     'X' or 'O'
     * @return fully configured AIPlayer instance
     */
    public static AIPlayer create(Player.PlayerType difficulty, char symbol) {
        return AIPlayerFactory.create(difficulty, symbol);
    }
}