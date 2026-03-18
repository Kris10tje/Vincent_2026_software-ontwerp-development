package whistapp.ui;

import whistapp.application.Controller;

import java.util.*;

/**
 * CLI for playing a virtual game of Whist.
 */
public class PlayGameCLI extends GameCLI {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * Create a PlayGameCLI bound to the given controller.
     *
     * @param controller the application controller used to manage the game
     */
    public PlayGameCLI(Controller controller) {
        super(controller);
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
        System.out.println("Playing a virtual game of Whist.");
    }

    /**
     * Prompt the user for player information and start a new game with the controller.
     *
     * <p>The user is first asked how many real players will participate.
     * If the number is smaller than the total player count, bot difficulty
     * is requested and bots will fill the remaining player slots.
     *
     * <p>The method will loop until valid player names are provided.
     * On success the registered names are printed to standard output.
     */
    protected void startNewGame() {

        int nbOfRealPlayers;

        // Get valid number of real players
        while (true) {
            nbOfRealPlayers = getInputInt("How many real players will play in this game?");
            if (nbOfRealPlayers <= Controller.getPlayerCount() && nbOfRealPlayers >= 0) {
                break;
            } else {
                System.out.println("Invalid number of real players.");
            }
        }

        // Initialize player name list
        if (nbOfRealPlayers >= 0) {

            while (true) {
                // Get player names
                String[] players = getPlayers(nbOfRealPlayers);

                // Create map with player names and bot difficulties
                LinkedHashMap<String, whistapp.domain.players.BotDifficulty> playerMap = new LinkedHashMap<>();
                int botCount = 1;
                int lowBotCount = 1;
                int highBotCount = 1;

                for (int i = 0; i < Controller.getPlayerCount(); i++) {
                    if (i < nbOfRealPlayers) {
                        playerMap.put(players[i], null); // Null means human player
                    } else {
                        // Ask difficulty for this specific bot
                        whistapp.domain.players.BotDifficulty botDiff = getChoice(
                                "Choose the difficulty for Bot " + botCount + ".",
                                controller.getBotDifficultyOptions());

                        // Assign automatic name based on difficulty (must be alpha only)
                        char lowBotSuffix = (char) ('A' + lowBotCount - 1);
                        char highBotSuffix = (char) ('A' + highBotCount - 1);

                        if (botDiff == whistapp.domain.players.BotDifficulty.LOW) {
                            playerMap.put("LowBot" + lowBotSuffix, botDiff);
                            lowBotCount++;
                        } else if (botDiff == whistapp.domain.players.BotDifficulty.HIGH) {
                            playerMap.put("HighBot" + highBotSuffix, botDiff);
                            highBotCount++;
                        }

                        botCount++;
                    }
                }

                if (playerMap.size() != Controller.getPlayerCount()) {
                    System.out.println("Error: Invalid player names.\nTry again.");
                    continue;
                }

                // Start the game with the given players and bot difficulties
                try {
                    controller.startNewPlayGame(playerMap);
                } catch (Exception e) {

                    clearScreen();
                    System.out.println("Error: " + e.getMessage() + "\nTry again.");

                    continue;
                }

                // Confirmation message
                clearScreen();
                System.out.println("Players registered:\n");
                for (String key : playerMap.keySet()) {
                    System.out.println("- " + key);
                }

                break;

            }
        }
    }

    /**
     * Drive the round loop: repeatedly play a round until the user indicates
     * they do not want to play another round.
     */
    protected void showAllRounds() {
        while (true) {
            // Very important: advance the game to the next round before playing it!
            controller.nextRound();

            // Show the round and its result
            showRound();
            showRoundPoints();

            // Ask whether to play another round
            if (!getYesNo("\nDo you want to play another round?")) {
                return;
            }
        }
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Private Methods                            */
    /* -------------------------------------------------------------------------- */

    /**
     * Ask the CLI user for the player names (only for real players).
     *
     * @param nbOfRealPlayers the number of real players participating
     * @return an array containing all human player names
     */
    // TODO: can we make this GameCLI-wide and reuse it in ScoreGameCLI?
    private String[] getPlayers(int nbOfRealPlayers) {
        // List to keep track of the player names
        String[] players = new String[nbOfRealPlayers];

        // Get player names from the user
        for (int i = 0; i < nbOfRealPlayers; i++) {
            players[i] = getInputString("Enter name for player #" + (i + 1));
        }

        // Return the list of player names
        return players;
    }

    /**
     * Play a single round.
     *
     * <p>This method first runs the bidding phase until bidding stabilizes.
     * After that, the trick phase begins where players play cards until
     * no tricks remain in the round.
     */
    private void showRound() {

        // Print out round start info
        clearScreen();
        informUser("Starting a new round!\nThe dealer is: " + controller.getDealerName());

        showRoundBiddingPhase();

        // Feedback
        clearScreen();
        printSeparator();
        System.out.println("THE BIDDING PHASE HAS ENDED.");
        System.out.println("Winning bid: " + controller.getFinalBidName());
        System.out.println("Declarer(s): " + String.join(", ", controller.getFinalBidDeclarers()));
        printSeparator();

        // We wait for user before starting the tricks
        getInputString("Press enter to continue");

        clearScreen();
        informUser("Starting the trick playing phase of the round.");

        // Start the trick phase in the domain layer
        controller.startPlayingRound();

        showRoundTrickPhase();

        // Calculate and apply scores for this round.
        controller.updateScores();
    }

    /**
     * Run the bidding phase until bidding stabilizes.
     */
    private void showRoundBiddingPhase() {

        // Loop until the proposer has accepted
        while (true) {

            // Loop until we have a valid final (winning) bid
            while (!controller.biddingStabilised()) {

                // Pass to the next player
                int activePlayerIndex = controller.getActivePlayerIndex();

                // Check if player is a bot
                if (controller.isAutonomous(activePlayerIndex)) {
                    controller.proceedAutonomousBid();
                    continue;
                }

                getReady();

                // Get a valid bid from the active player
                while (true) {

                    try {

                        // Show the last dealt card (always dealt face up, to show what the trump will be for some bids)
                        showLastDealtCard();

                        // Show all existing bids
                        showExistingBids();

                        // Show the active player's hand to help them make a decision on their bid
                        showHand();

                        // Get the bid choice
                        String chosenBid = getChoice("Choose your bid", controller.getPossibleBids());
                        if (controller.bidRequiresTrumpDeclaration(chosenBid)) {
                            controller.registerBid(chosenBid, getChoice("Choose your preferred trump suit", controller.getSuits()));
                        } else {
                            controller.registerBid(chosenBid);
                        }

                        // Stop the loop and pass to the next player
                        break;

                    } catch (Exception e) {
                        clearScreen();
                        System.out.println("Error: " + e.getMessage() + "\nPlease try again.");
                    }

                }

            }

            String proposerName = controller.getLoneProposerName();
            if (proposerName == null) {
                break;
            } else {
                getInputString("Press enter when " + proposerName + " is ready to choose to play the bid alone or pass.");
                clearScreen();

                showHand("Your Hand:", controller.getCards(proposerName));

                boolean choice = getYesNo("Do you want to play the bid alone?");
                if (choice) {
                    // The proposer wishes to play alone.
                    controller.registerLoneProposer(proposerName);
                    controller.biddingStabilised();
                    break;
                }
            }

        }
    }

    /**
     * Show all existing bids made by players
     */
    private void showExistingBids() {
        System.out.println(controller.getExistingBids() + "\n");
    }

    /**
     * Run the trick-playing phase until all tricks have been played.
     */
    private void showRoundTrickPhase() {
        while (controller.getTricksLeft() > 0) {
            showTrick();

            informUser("The trick is over.");

            String winner = controller.getCurrentTrickWinnerName();
            System.out.println("Winner of this trick: " + winner);
            System.out.println("(" + winner + " leads the next trick.)");
            System.out.println();
            getInputString("Press enter to continue");

            controller.evaluateAndAdvanceTrick();
        }
    }

    /**
     * Play a single trick.
     *
     * <p>Players take turns playing a card until the trick is complete.
     * Each player selects a card from their hand which is then registered
     * through the controller.
     */
    private void showTrick() {

        while (!controller.trickIsOver()) {

            // Make sure the next player is ready
            int activePlayerIndex = controller.getActivePlayerIndex();

            // Check if player is a bot
            if (controller.isAutonomous(activePlayerIndex)) {
                controller.proceedAutonomousCardPlay();
                continue;
            }

            getReady();

            // Show what's currently on the table (cards played by bots before this turn)
            HashMap<String, String> trickCards = controller.getCurrentTrickCardsAsStrings();
            if (!trickCards.isEmpty()) {
                System.out.println("Cards on the table (in order of play):");
                for (java.util.Map.Entry<String, String> entry : trickCards.entrySet()) {
                    System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                }
                System.out.println();
            }

            // Show the trump suits for this round.
            // The original trump is always shown. For Abondance, the active trump
            // may differ or not yet be known (chosen by first card play).
            String originalTrump = controller.getOriginalTrumpSuitName();
            String activeTrump = controller.getTrumpSuitName();
            if (originalTrump != null) {
                System.out.println("Original trump: " + originalTrump);
            }
            if (activeTrump == null) {
                // This is a bid that doesn't have a trump (e.g. Miserie)
                System.out.println("Active trump:   None");
            } else if (!activeTrump.equals(originalTrump)) {
                System.out.println("Active trump:   " + activeTrump + " (chosen by bid declarer)");
            }
            // If active trump equals original trump, no extra line is needed.
            System.out.println();

            // Show the hands of the open miserie players
            showOpenMiserieHands();

            // Show the active player's hand
            showHand();

            // Build the set of legally allowed cards
            Set<String> allowedCards = new HashSet<>(
                    Arrays.asList(controller.getAllowedCardsForCurrentPlayer()));

            // Get a valid card choice from the active player
            while (true) {

                try {

                    // Build the annotated choices: ★ prefix for legal cards
                    String[] originalCards = controller.getCards();
                    String[] choices = new String[originalCards.length + 1];
                    for (int i = 0; i < originalCards.length; i++) {
                        choices[i] = allowedCards.contains(originalCards[i])
                                ? "★ " + originalCards[i]
                                : originalCards[i];
                    }
                    choices[choices.length - 1] = "View Last Trick";

                    // Get the choice
                    String chosenCard = getChoice("Choose which card to play", choices);

                    if (chosenCard.equals("View Last Trick")) {
                        try {
                            System.out.println(controller.getLastTrickString());
                        } catch (Exception e) {
                            System.out.println("Error viewing last trick: " + e.getMessage());
                        }
                        getInputString("Press enter to return.");
                        clearScreen();
                        continue;
                    }

                    // Strip the ★ prefix before registering
                    String cardToPlay = chosenCard.startsWith("★ ") ? chosenCard.substring(2) : chosenCard;

                    // Register the card play with the controller
                    controller.registerPlayCard(cardToPlay);

                    // Stop the loop and pass to the next player
                    break;

                } catch (Exception e) {
                    clearScreen();
                    System.out.println("Error: " + e.getMessage() + "\nPlease try again.");
                }
            }

            // Advance the turn
            clearScreen();

        }
    }

    /**
     * Display the intermediate scores of every player.
     *
     * <p>This method is intended to show some intermediate score after a round,
     * not the results of an entire game.
     *
     * <p>Shows the points earned and lost per player this round
     */
    @Override
    protected void showRoundPoints() {
        // Clear the screen and show message
        informUser("Score earned/lost this round:");

        // Retrieve the points for each player from the domain layer
        HashMap<String, Integer> points = controller.getRoundScoresPerPlayer();

        // Print the points for each player
        for (String playerName : controller.getPlayerNames()) {
            System.out.println(playerName + ": " + points.get(playerName) + " points");
        }

        printSeparator();
    }

    /**
     * Shows the last dealt card, which is always dealt face up and determines the trump for some bids.
     */
    protected void showLastDealtCard() {
        System.out.println("Last dealt card this round: " + controller.getLastDealtCard() + "\n");
    }
}
