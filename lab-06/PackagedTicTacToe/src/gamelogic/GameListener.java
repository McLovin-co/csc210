package gamelogic;


/**
 * Listener interface for receiving events from a {@link TicTacToeGame}.
 * Implement this interface and register it with
 * {@link TicTacToeGame#addListener} to be notified when the game state changes.
 */
public interface GameListener {

    /**
     * Called after a valid move has been made on the board.
     *
     * @param index the board cell that was played (0..8)
     * @param who   the mark ({@link TicTacToeGame.Mark#X} or {@link TicTacToeGame.Mark#O}) that was placed
     */
    void onMove(int index, TicTacToeGame.Mark who);

    /**
     * Called when the game has ended, either by a win or a draw.
     *
     * @param winner the winning mark, or {@link TicTacToeGame.Mark#EMPTY} if the game ended in a draw
     */
    void onGameOver(TicTacToeGame.Mark winner);

    /**
     * Called after the game has been reset and is ready to play again.
     *
     * @param starting the mark of the player who will go first
     */
    void onReset(TicTacToeGame.Mark starting);
}

