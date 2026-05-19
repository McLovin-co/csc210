import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents the state and logic of a Tic-Tac-Toe game.
 * The board is stored as a flat array of 9 cells (index 0..8),
 * read left-to-right, top-to-bottom.
 * Registered {@link GameListener}s are notified on moves, game-over, and reset.
 */
public class TicTacToeGame {

    /**
     * The three possible states a board cell can hold.
     * {@code EMPTY} means the cell has not been played yet.
     */
    public enum Mark { X, O, EMPTY }

    private final Mark[] board = new Mark[9];
    private Mark current = Mark.X;
    private boolean gameOver = false;
    private Mark winner = Mark.EMPTY;
    private final List<GameListener> listeners = new ArrayList<>();

    /** Creates a new game and resets the board so it is ready to play. */
    public TicTacToeGame() { reset(); }

    /**
     * Resets the board to an empty state and sets X as the starting player.
     * All registered listeners are notified via {@link GameListener#onReset}.
     */
    public void reset() {
        Arrays.fill(board, Mark.EMPTY);
        current = Mark.X;
        winner = Mark.EMPTY;
        gameOver = false;
        fireReset(current);
    }

    /**
     * Returns a defensive copy of the board array.
     * Index 0 is top-left; index 8 is bottom-right.
     *
     * @return a 9-element array of {@link Mark} values
     */
    public Mark[] getBoard() { return board.clone(); }

    /**
     * Returns the mark of the player whose turn it currently is.
     *
     * @return {@link Mark#X} or {@link Mark#O}
     */
    public Mark getCurrent() { return current; }

    /**
     * Returns whether the game has ended (win or draw).
     *
     * @return {@code true} if the game is over
     */
    public boolean isGameOver() { return gameOver; }

    /**
     * Returns the winner of the game.
     * If the game is still in progress, this returns {@link Mark#EMPTY}.
     * If the game ended in a draw, this also returns {@link Mark#EMPTY}.
     *
     * @return the winning {@link Mark}, or {@code EMPTY} for draw/in-progress
     */
    public Mark getWinner() { return winner; }

    /**
     * Attempts to play at the given board index for the current player.
     * The move is rejected if the game is over, the index is out of bounds,
     * or the cell is already occupied.
     * On a valid move, listeners are notified. If the move ends the game,
     * {@link GameListener#onGameOver} is fired.
     *
     * @param index the cell to play (0..8)
     * @return {@code true} if the move was accepted, {@code false} otherwise
     */
    public boolean play(int index) {
        if (gameOver || index < 0 || index > 8 || board[index] != Mark.EMPTY) return false;

        board[index] = current;
        fireMove(index, current);

        Mark w = computeWinner();
        if (w != Mark.EMPTY) {
            winner = w;
            gameOver = true;
            fireGameOver(winner);
        } else if (isFull()) {
            gameOver = true;
            winner = Mark.EMPTY;
            fireGameOver(Mark.EMPTY);
        } else {
            current = (current == Mark.X ? Mark.O : Mark.X);
        }
        return true;
    }

    /**
     * Returns the list of board indices that are still available to play.
     * Returns an empty list if the game is over.
     *
     * @return list of legal move indices (0..8)
     */
    public List<Integer> legalMoves() {
        List<Integer> moves = new ArrayList<>();
        if (gameOver) return moves;
        for (int i = 0; i < 9; i++) if (board[i] == Mark.EMPTY) moves.add(i);
        return moves;
    }

    /**
     * Registers a listener to receive game events.
     *
     * @param l the listener to add; must not be null
     */
    public void addListener(GameListener l) { listeners.add(Objects.requireNonNull(l)); }

    /**
     * Removes a previously registered listener.
     *
     * @param l the listener to remove
     */
    public void removeListener(GameListener l) { listeners.remove(l); }

    private void fireMove(int idx, Mark who) { for (var l : listeners) l.onMove(idx, who); }
    private void fireGameOver(Mark winner) { for (var l : listeners) l.onGameOver(winner); }
    private void fireReset(Mark starting) { for (var l : listeners) l.onReset(starting); }
    private boolean isFull() { for (Mark m : board) if (m == Mark.EMPTY) return false; return true; }

    private static final int[][] LINES = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
    };

    private Mark computeWinner() {
        for (int[] line : LINES) {
            Mark a = board[line[0]], b = board[line[1]], c = board[line[2]];
            if (a != Mark.EMPTY && a == b && b == c) return a;
        }
        return Mark.EMPTY;
    }
}
