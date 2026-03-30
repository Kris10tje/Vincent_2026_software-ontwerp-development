package whistapp.ui;

import whistapp.application.interfaces.*;
import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.bids.BidType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * CLI for tracking the score of a physical Whist game.
 */
public class ScoreGameCLI extends GameCLI<IScoreGameController> {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * Create a ScoreGameCLI bound to the given controller.
     *
     * @param controller the application controller used to register players,
     *                   bids and trick counts (must not be {@code null})
     */
    public ScoreGameCLI(IController controller, IInputOutputProvider inputProvider) {
        super(controller, inputProvider);
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Public Methods                             */
    /* -------------------------------------------------------------------------- */

    // show() is inherited from GameCLI and does the following in a 
    // loop until the user chooses to exit:
    //  - showIntro();
    //  - startNewGame();
    //  - showAllRounds();
    //  - showGameResults();

    /* -------------------------------------------------------------------------- */
    /*                                Protected Methods                           */
    /* -------------------------------------------------------------------------- */

    /**
     * Clear the screen and show a short introduction describing this CLI mode.
     */
    protected void showIntro() {
        clearScreen();
        ioProvider.writeLine("Keeping track of the score of a physical game of Whist.");
    }

    /**
     * Prompt the user to enter player names and start a new game with the controller.
     *
     * <p>This method will loop until valid names are provided.
     * If initialization fails the method clears the screen and asks again.
     *
     * <p>On success the registered names are printed to standard output.
     */
    protected void startNewGame() {
        while (true) {

            // Get player names from the user
            ArrayList<String> players = retrievePlayerNames();

            try {

                // There are never bots in a score game so [# real players == # players]
                specificGameController = controller.startNewScoreGame(players);

            } catch (Exception e) {

                // Clear the screen, show the error and ask again
                clearScreen();
                ioProvider.writeLine("Failed to start new game: " + e.getMessage());
                continue;

            }

            // Confirmation
            clearScreen();
            ioProvider.writeLine("Players registered: \n\n- " + String.join("\n- ", players));
            break;

        }
    }


    /**
     * Interactively register a single round:
     * <ol>
     *   <li>ask and register final active bids for each player,</li>
     *   <li>ask and register if there was a reshuffle,</li>
     *   <li>ask and register number of tricks won per player.</li>
     * </ol>
     *
     * <p>Errors when registering bids or trick counts clear the screen,
     * display the error, and allow the user to try again.
     *
     * <p>After successful registration the method prints the registered bids and
     * trick counts to standard output.
     */
    protected void showRound() {

        // Bidding phase
        this.handleBidding();

        this.handleReshuffle();

        // Feedback
        clearScreen();
        informUser("Enter round results when finished.");

        // Round phase
        this.handleTricks();

    }

    /* -------------------------------------------------------------------------- */
    /*                                  Helper Methods                            */
    /* -------------------------------------------------------------------------- */

    /**
     * A helper method for the showRound method to handle the bidding phase of a round.
     */
    private void handleBidding() {

        // Loop until valid bids are registered
        while (true) {

            // Try to register the bids, if it fails clear the screen and ask again
            try {

                // Get and try to register the bids in the domain layer
                promptForBids();

            } catch (Exception e) {
                clearScreen();
                ioProvider.writeLine("Couldn't register bids: " + e.getMessage() + " Try again.");
                continue;
            }

            // Confirmation
            clearScreen();
            informUser("Bids registered successfully.");
            break;

        }
    }

    /**
     * A helper method for the showRound method to handle the trick count registration.
     */
    private void handleTricks() {

        while (true) {

            // Get the number of tricks won per player from the user
            HashMap<IPlayer, Integer> nbOfTricksWonPerPlayer = getNbOfTricksWonPerPlayer();

            // Try to register the number of tricks won per player, if it fails clear the screen and ask again
            try {
                specificGameController.updateScores(nbOfTricksWonPerPlayer);
            } catch (Exception e) {
                clearScreen();
                ioProvider.writeLine("Couldn't register number of tricks won per player: " + e.getMessage());
                continue;
            }

            // Feedback
            clearScreen();
            informUser("Round results registered successfully. Here's a summary:");
            ArrayList<IPlayer> players = specificGameController.getPlayers();
            for (IPlayer player : players) {
                ioProvider.writeLine(player.getName() + ": " + nbOfTricksWonPerPlayer.get(player));
            }

            break;

        }

    }

    /**
     * A helper method for the showRound method to handle the reshuffle state registration.
     */
    private void handleReshuffle() {
        // The user should provide information about whether the bidding was successful first try.
        boolean reshuffled = this.getReshuffleState();

        // Register the reshuffle state in the domain layer
        specificGameController.setReshuffledState(reshuffled);
    }

    /* -------------------------------------------------------------------------- */
    /*                              User Retrieval Methods                        */
    /* -------------------------------------------------------------------------- */

    /**
     * Ask the CLI user for the player names.
     *
     * @return an array of player names
     */
    private ArrayList<String> retrievePlayerNames() {

        // Make a string array of size [# players] to store the names in
        int playerCount = controller.getPlayerCount();
        String[] names = new String[playerCount];

        // Prompt the user for the name of each player and store it in the array
        for (int i = 0; i < playerCount; i++) {
            names[i] = this.getInputString("Enter name for player " + (i + 1));
        }

        // Return the array of names
        return new ArrayList<>(Arrays.asList(names));

    }

    /**
     * Prompt the user for the final active bid for each registered player.
     * Collects all bids and registers them at once.
     */
    private void promptForBids() {
        // Get the player objects from the domain layer and extract names in the UI layer
        ArrayList<IPlayer> players = specificGameController.getPlayers();

        HashMap<IPlayer, BidType> bids = new HashMap<>();

        // Ask for each player
        for (IPlayer player : players) {
            String playerName = player.getName();
            // Prompt for bid
            String playerBidText = getChoice("What is the final active bid for " + playerName + "?",
                    controller.getBidTypes());

            bids.put(player, BidType.fromString(playerBidText));
        }

        // Register the collected bids
        specificGameController.registerBids(bids);
    }

    /**
     * Prompt the user for the number of tricks won by each player.
     *
     * @return an array containing the number of tricks won for each player.
     */
    private HashMap<IPlayer, Integer> getNbOfTricksWonPerPlayer() {

        // Get the player objects from the domain layer and extract names in the UI layer
        ArrayList<IPlayer> players = specificGameController.getPlayers();

        // Ask for each player and store the result in a HashMap
        HashMap<IPlayer, Integer> nbOfTricksPerPlayer = new HashMap<>();
        for (IPlayer player : players) {
            String playerName = player.getName();
            nbOfTricksPerPlayer.put(player, getInputInt("How many tricks were won by " + playerName));
        }

        // Return the array of tricks per player
        return nbOfTricksPerPlayer;

    }

    /**
     * Ask the user whether there was a reshuffle in the round.
     *
     * @return {@code true} if the user confirms there was a reshuffle, otherwise {@code false}.
     */
    private boolean getReshuffleState() {
        return getYesNo("Was there a reshuffle?");
    }
}
