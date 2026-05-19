/**
 * Strategy interface for an AI opponent in Tic-Tac-Toe.
 * Implementations decide which board cell to play given the current game state.
 * The most capable built-in implementation is {@link MinimaxOpponent}.
 */
public interface Opponent {

    /**
     * Chooses the board index (0..8) for the AI to play.
     * This method is only called when it is the opponent's turn
     * and the game is not yet over.
     *
     * @param game the current game state
     * @param me   the mark this opponent is playing as
     * @return the chosen board index (0..8)
     */
    int chooseMove(TicTacToeGame game, TicTacToeGame.Mark me);
}
