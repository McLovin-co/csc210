import java.util.List;

/**
 * An AI opponent that uses the Minimax algorithm with alpha-beta pruning
 * to play a perfect game of Tic-Tac-Toe.
 * This opponent will never lose — at best the human can draw.
 * Implements {@link Opponent}.
 */
public class MinimaxOpponent implements Opponent {

    /**
     * Chooses the best available move using minimax search.
     * Prefers the center cell and corners as quick heuristics before
     * falling back to full minimax evaluation.
     *
     * @param game the current game state
     * @param me   the mark this AI is playing as
     * @return the board index of the best move (0..8)
     */
    @Override
    public int chooseMove(TicTacToeGame game, TicTacToeGame.Mark me) {
        TicTacToeGame.Mark opp = (me == TicTacToeGame.Mark.X ? TicTacToeGame.Mark.O : TicTacToeGame.Mark.X);

        var moves = game.legalMoves();
        if (moves.contains(4)) return 4;
        int[] corners = {0,2,6,8};
        for (int c : corners) if (moves.contains(c)) return bestByMinimax(game, me, opp);
        return bestByMinimax(game, me, opp);
    }

    /**
     * Runs minimax over all legal moves and returns the index with the highest score.
     *
     * @param game the current game state
     * @param me   the AI's mark
     * @param opp  the opponent's mark
     * @return the best move index
     */
    private int bestByMinimax(TicTacToeGame game, TicTacToeGame.Mark me, TicTacToeGame.Mark opp) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int move : game.legalMoves()) {
            Sim s = new Sim(game);
            s.play(move);
            int score = minimax(s, false, me, opp, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (score > bestScore) { bestScore = score; bestMove = move; }
        }
        return bestMove;
    }

    /**
     * Recursive minimax with alpha-beta pruning.
     * Returns +10 if the AI wins, -10 if the opponent wins, 0 for a draw.
     *
     * @param s          the simulated board state
     * @param maximizing true if this level maximizes the AI's score
     * @param me         the AI's mark
     * @param opp        the opponent's mark
     * @param alpha      best already-found value for the maximizer
     * @param beta       best already-found value for the minimizer
     * @return the minimax score for this board state
     */
    private int minimax(Sim s, boolean maximizing, TicTacToeGame.Mark me, TicTacToeGame.Mark opp, int alpha, int beta) {
        if (s.isOver()) {
            var w = s.winner();
            if (w == me) return +10;
            if (w == opp) return -10;
            return 0;
        }

        if (maximizing) {
            int best = Integer.MIN_VALUE;
            for (int m : s.legalMoves()) {
                Sim nxt = s.copy(); nxt.play(m);
                int val = minimax(nxt, false, me, opp, alpha, beta);
                best = Math.max(best, val);
                alpha = Math.max(alpha, val);
                if (beta <= alpha) break;
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int m : s.legalMoves()) {
                Sim nxt = s.copy(); nxt.play(m);
                int val = minimax(nxt, true, me, opp, alpha, beta);
                best = Math.min(best, val);
                beta = Math.min(beta, val);
                if (beta <= alpha) break;
            }
            return best;
        }
    }

    /**
     * A lightweight board simulator used internally by the minimax search.
     * Avoids the overhead of full {@link TicTacToeGame} instances and listeners.
     */
    private static class Sim {
        private final TicTacToeGame.Mark[] b = new TicTacToeGame.Mark[9];
        private TicTacToeGame.Mark turn;
        private boolean over = false;
        private TicTacToeGame.Mark win = TicTacToeGame.Mark.EMPTY;

        /**
         * Creates a Sim by copying the state of a real game.
         *
         * @param g the game to copy from
         */
        Sim(TicTacToeGame g) {
            var src = g.getBoard();
            System.arraycopy(src, 0, b, 0, 9);
            turn = g.getCurrent();
            over = g.isGameOver();
            win  = g.getWinner();
        }

        Sim(TicTacToeGame.Mark[] b, TicTacToeGame.Mark turn, boolean over, TicTacToeGame.Mark win) {
            System.arraycopy(b, 0, this.b, 0, 9);
            this.turn = turn; this.over = over; this.win = win;
        }

        /** Returns a deep copy of this simulator. */
        Sim copy() { return new Sim(b, turn, over, win); }

        /** Returns true if the simulated game has ended. */
        boolean isOver() { return over; }

        /** Returns the winner of the simulated game, or EMPTY for draw/in-progress. */
        TicTacToeGame.Mark winner() { return win; }

        /** Returns available move indices in the simulated board. */
        List<Integer> legalMoves() {
            java.util.ArrayList<Integer> ms = new java.util.ArrayList<>();
            if (over) return ms;
            for (int i = 0; i < 9; i++) if (b[i] == TicTacToeGame.Mark.EMPTY) ms.add(i);
            return ms;
        }

        /**
         * Plays a move on the simulated board.
         *
         * @param idx the cell index to play (0..8)
         * @return true if the move was accepted
         */
        boolean play(int idx) {
            if (over || idx < 0 || idx > 8 || b[idx] != TicTacToeGame.Mark.EMPTY) return false;
            b[idx] = turn;
            int[][] L = {
                    {0,1,2},{3,4,5},{6,7,8},
                    {0,3,6},{1,4,7},{2,5,8},
                    {0,4,8},{2,4,6}
            };
            for (int[] line : L) {
                TicTacToeGame.Mark a = b[line[0]];
                TicTacToeGame.Mark c = b[line[1]];
                TicTacToeGame.Mark d = b[line[2]];
                if (a != TicTacToeGame.Mark.EMPTY && a == c && c == d) {
                    win = a; over = true; return true;
                }
            }
            boolean full = true;
            for (var m : b) if (m == TicTacToeGame.Mark.EMPTY) { full = false; break; }
            if (full) { over = true; win = TicTacToeGame.Mark.EMPTY; return true; }
            turn = (turn == TicTacToeGame.Mark.X ? TicTacToeGame.Mark.O : TicTacToeGame.Mark.X);
            return true;
        }

        /** Returns whose turn it is in the simulated game. */
        TicTacToeGame.Mark turn() { return turn; }
    }
}
