package whistapp.ui;

import java.util.HashMap;

import whistapp.application.*;

/**
 * Base CLI for game modes that run a Whist game loop.
 *
 */
public abstract class GameCLI extends CLI {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * Create a GameCLI bound to the given controller.
     *
     * @param controller the application controller used by the CLI
     */
    public GameCLI(IController controller, InputOutputProvider ioProvider) {
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

    protected abstract void showAllRounds();

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
     * Wait for the active player to be ready.
     *
     * <p>This is used because multiple players share the same screen.
     * This allows the next player to take their turn privately.
     */
    protected void getReady() {
        getInputString("Press enter when " + controller.getActivePlayerName() + " is ready.");
        clearScreen();
        informUser("It's " + controller.getActivePlayerName() + "'s turn.");
    }

    /**
     * Display the hand of the active player.
     */
    protected void showHand() {
        showHand("Your Hand:", controller.getCards());
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
     * Displays the hands of the open msierie players.
     */
    protected void showOpenMiserieHands() {
        HashMap<String, String[]> cards = controller.getOpenMiserieHands();
        
        // Print a purposeful notice when the active player is an Open Miserie declarer
        if ("Open Miserie".equals(controller.getFinalBidName())) {
            for (String declarer : controller.getFinalBidDeclarers()) {
                if (declarer.equals(controller.getActivePlayerName())) {
                    ioProvider.writeLine("(Your own Open Miserie hand is shown below under 'Your Hand'.)\n");
                    break;
                }
            }
        }
        
        if (cards.isEmpty()) return;
        for (String playerName : cards.keySet()) {
            showHand(playerName + "'s Hand (Open Miserie):", cards.get(playerName));
        }
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
        HashMap<String, Integer> scores = controller.getGameScoresPerPlayer();

        // Print the scores for each player
        for (String playerName : controller.getPlayerNames()) {
            ioProvider.writeLine(playerName + ": " + scores.get(playerName) + " points");
        }
    }
}
