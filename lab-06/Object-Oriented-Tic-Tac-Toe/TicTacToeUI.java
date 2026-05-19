/**
 * Interface for a Tic-Tac-Toe user interface.
 * A UI is also a {@link GameListener} so it can react to game events
 * directly from the game model.
 * Implementations include {@link CLIUI} (text-based) and {@link SwingUI} (graphical).
 */
public interface TicTacToeUI extends GameListener {

    /**
     * Starts the UI for the given game.
     * Implementations should register themselves as a listener on the game
     * and then begin accepting user input or rendering the board.
     *
     * @param game       the game model to interact with
     * @param vsAI       {@code true} if one player is an AI opponent
     * @param ai         the AI opponent to use (may be null if vsAI is false)
     * @param aiPlaysAs  the mark the AI will play as
     */
    void start(TicTacToeGame game, boolean vsAI, Opponent ai, TicTacToeGame.Mark aiPlaysAs);
}
