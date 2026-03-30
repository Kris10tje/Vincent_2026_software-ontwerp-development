package whistapp.ui;

import java.util.HashMap;

import whistapp.application.interfaces.*;
import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.interfaces.IPlayerInputProvider;

/**
 * Base CLI for game modes that run a Whist game loop.
 *
 */
public abstract class GameCLI<TGame extends IGameController> extends CLI  {

    // TGame is the type of controller (i.e. the interface type that
    // extends IGameController, being either IPlayGameController or
    // IScoreGameController [for now]).
    //
    // If we now create PlayGameCLI and give the type IPlayGameController
    // between the diamond operator, the PlayGameCLI will store the game
    // with type IPlayGameController instead of TGame.
    //
    // This means that if we type specificGameController.[...], the IDE only suggests the methods
    // from that specific interface (game mode). Not only that, but the Java code
    // simply won't compile if you try to call specificGameController.[methodNotInInterface]!
    protected TGame specificGameController;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * Create a GameCLI bound to the given controller.
     *
     * @param controller the application controller used by the CLI
     */
    public GameCLI(IController controller, IInputOutputProvider ioProvider) {
        super(controller, ioProvider);
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Public Methods                             */
    /* -------------------------------------------------------------------------- */

    /**
     * Start the interactive score-tracking loop.
     *
    * <p>This method runs until the user chooses not to start another game.
     * It repeatedly:
     * <ol>
     *   <li>shows an introduction,</li>
     *   <li>reads and registers player names,</li>
     *   <li>plays all rounds,</li>
     *   <li>shows game results,</li>
    *   <li>and asks whether to start another game.</li>
     * </ol>
     *
     * <p>When the user chooses not to continue the loop breaks, naturally falling 
     * back to the caller (MainMenuCLI).
     */
    public void show() {
        while (true) {

            showIntro();

            startNewGame();

            showAllRounds();

            showGameResults();

            if (!this.getNextGameChoice()) {
                // By returning, we fall back to the MainMenu loop
                return;
            }

        }
    }

    /* -------------------------------------------------------------------------- */
    /*                                Protected Methods                           */
    /* -------------------------------------------------------------------------- */

    protected abstract void showIntro();

    protected abstract void startNewGame();

    /**
     * Drive the round loop: repeatedly play a round until the user indicates
     * they do not want to play another round.
     */
    protected void showAllRounds() {
        while (true) {
            // Very important: advance the game to the next round before playing it!
            specificGameController.startNewRound();

            // Show the round and its result
            showRound();
            showRoundPoints();

            // Ask whether to play another round
            if (!getYesNo("\nDo you want to play another round?")) {
                return;
            }
        }
    }

    protected abstract void showRound();

    /**
    * Shows an exit message and gracefully exits the game.
     */
    protected void exit() {
        ioProvider.writeLine("Thanks for playing!");
        this.controller.exit();
    }

    /**
    * Ask the user whether they want to start another game.
     *
     * @return {@code true} if the user chooses to play another game,
     * otherwise {@code false}
     */
    protected boolean getNextGameChoice() {
        return this.getYesNo("\nDo you want to play another game?");
    }

    /**
     * Display a given hand.
     */
    protected void showHand(String header, String[] cards) {
        ioProvider.writeLine(header);
        for (int i = 0; i < cards.length; i++) {
            ioProvider.writeLine("  • " + String.format("%-20s", cards[i]));
            if ((i + 1) % 3 == 0) {
                ioProvider.writeLine("");
            }
        }
        if (cards.length % 3 != 0) {
            ioProvider.writeLine("");
        }
        ioProvider.writeLine("");
    }



    /**
     * Display the final game results.
     *
     * <p>This method is intended to show the results of the entire game,
     * not the results of individual rounds.
     */
    protected void showGameResults() {
        // Clear the screen and show message
        clearScreen();
        ioProvider.writeLine("Game results:");

        showPlayerPoints();
    }

    /**
     * Display the intermediate scores of every player.
     *
     * <p>This method is intended to show some intermediate score after a round,
    * not the results of an entire game.
     */
    protected void showRoundPoints() {
        // Clear the screen and show message
        informUser("Scores after this round:");

        showPlayerPoints();
    }

    /**
     * Display the current scores of every player
     */
    protected void showPlayerPoints() {
        // Retrieve the final cumulative scores for each player
        HashMap<IPlayer, Integer> scores = specificGameController.getScoresPerPlayer();

        // Print the scores for each player
        for (IPlayer player : specificGameController.getPlayers()) {
            ioProvider.writeLine(player.getName() + ": " + scores.get(player) + " points");
        }
    }
}
