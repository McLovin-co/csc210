package main;

import gamelogic.*;
import userinterface.*;

/**
 * Entry point for the Tic-Tac-Toe application.
 * Parses command-line flags to choose between CLI and Swing UIs
 * and to optionally enable an AI opponent.
 *
 * <p>Usage examples:
 * <pre>
 *   java Main              // CLI, two human players
 *   java Main --swing      // Swing GUI, two human players
 *   java Main --ai         // CLI, human vs AI (AI plays O)
 *   java Main --ai --ai-first  // CLI, human vs AI (AI plays X)
 * </pre>
 */
public class Main {

    /**
     * Main method. Reads flags from {@code args} and launches the appropriate UI.
     *
     * @param args command-line arguments: {@code --swing}, {@code --cli}, {@code --ai}, {@code --ai-first}
     */
    public static void main(String[] args) {
        boolean useSwing = argsContain(args, "--swing");
        boolean useCLI   = argsContain(args, "--cli") || !useSwing;
        boolean vsAI     = argsContain(args, "--ai");
        boolean aiFirst  = argsContain(args, "--ai-first");

        TicTacToeGame game = new TicTacToeGame();
        Opponent ai = new MinimaxOpponent();
        TicTacToeGame.Mark aiAs = aiFirst ? TicTacToeGame.Mark.X : TicTacToeGame.Mark.O;

        if (useSwing) {
            TicTacToeUI ui = new SwingUI();
            ui.start(game, vsAI, ai, aiAs);
        } else if (useCLI) {
            TicTacToeUI ui = new CLIUI();
            ui.start(game, vsAI, ai, aiAs);
        }
    }

    /**
     * Checks whether a specific flag string is present in the args array.
     * Comparison is case-insensitive.
     *
     * @param args the command-line arguments array
     * @param flag the flag to search for (e.g. {@code "--swing"})
     * @return {@code true} if the flag was found
     */
    private static boolean argsContain(String[] args, String flag) {
        if (args == null) return false;
        for (String a : args) if (flag.equalsIgnoreCase(a)) return true;
        return false;
    }
}

