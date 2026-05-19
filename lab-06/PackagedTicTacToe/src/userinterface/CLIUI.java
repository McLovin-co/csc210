package userinterface;

import java.util.Scanner;
import gamelogic.*;

/**
 * A command-line (text-based) user interface for Tic-Tac-Toe.
 * Players enter a number 1-9 to choose a cell, with 1 being top-left
 * and 9 being bottom-right.
 * Implements {@link TicTacToeUI} and {@link GameListener} to receive
 * and display game events.
 */
public class CLIUI implements TicTacToeUI {

    private TicTacToeGame game;
    private boolean vsAI;
    private Opponent ai;
    private TicTacToeGame.Mark aiAs;
    private final Scanner sc = new Scanner(System.in);

    /**
     * Starts the CLI game loop.
     * Registers this object as a game listener, then loops until the game ends,
     * prompting human players and delegating AI turns automatically.
     *
     * @param game       the game model
     * @param vsAI       whether one player is an AI
     * @param ai         the AI opponent (used only if vsAI is true)
     * @param aiPlaysAs  which mark the AI uses
     */
    @Override
    public void start(TicTacToeGame game, boolean vsAI, Opponent ai, TicTacToeGame.Mark aiPlaysAs) {
        this.game = game;
        this.vsAI = vsAI;
        this.ai = ai;
        this.aiAs = aiPlaysAs;

        game.addListener(this);
        draw(game.getBoard());

        while (!game.isGameOver()) {
            if (vsAI && game.getCurrent() == aiAs) {
                int move = ai.chooseMove(game, aiAs);
                game.play(move);
            } else {
                int move = promptMove();
                if (!game.play(move)) {
                    System.out.println("Illegal move. Try again.");
                }
            }
        }
    }

    /**
     * Prompts the current human player to enter a cell number (1-9).
     * Loops until valid input is received.
     *
     * @return the chosen board index (0..8)
     */
    private int promptMove() {
        while (true) {
            System.out.print("Player " + game.getCurrent() + " move (1-9): ");
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s) - 1;
                if (v >= 0 && v <= 8) return v;
            } catch (NumberFormatException ignored) {}
            System.out.println("Enter a number 1..9.");
        }
    }

    /**
     * Prints the current board state to stdout.
     *
     * @param b the 9-element board array
     */
    private void draw(TicTacToeGame.Mark[] b) {
        System.out.println();
        for (int r = 0; r < 3; r++) {
            int i = r * 3;
            System.out.printf(" %s | %s | %s %n", sym(b[i]), sym(b[i+1]), sym(b[i+2]));
            if (r < 2) System.out.println("---+---+---");
        }
        System.out.println();
    }

    /**
     * Converts a {@link TicTacToeGame.Mark} to a display character.
     *
     * @param m the mark to convert
     * @return "X", "O", or " " for empty
     */
    private String sym(TicTacToeGame.Mark m) {
        return switch (m) { case X -> "X"; case O -> "O"; default -> " "; };
    }

    /** Redraws the board after each move. */
    @Override public void onMove(int index, TicTacToeGame.Mark who) { draw(game.getBoard()); }

    /**
     * Prints the game result when the game ends.
     *
     * @param winner the winner, or EMPTY for a draw
     */
    @Override public void onGameOver(TicTacToeGame.Mark winner) {
        if (winner == TicTacToeGame.Mark.EMPTY) System.out.println("Draw!");
        else System.out.println("Winner: " + winner);
    }

    /**
     * Prints a message when the game is reset.
     *
     * @param starting the mark of the first player
     */
    @Override public void onReset(TicTacToeGame.Mark starting) {
        System.out.println("=== New Game. " + starting + " starts. ===");
    }
}

