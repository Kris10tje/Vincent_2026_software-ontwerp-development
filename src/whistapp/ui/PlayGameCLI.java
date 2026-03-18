package whistapp.ui;

import whistapp.application.Interfaces.*;

import java.util.*;

/**
 * CLI for playing a virtual game of Whist.
 */
public class PlayGameCLI extends GameCLI<IPlayGameController> {


    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * Create a PlayGameCLI bound to the given controller.
     *
     * @param controller the application controller used to manage the game
     * @param ioProvider the input/output provider for user interaction
     */
    public PlayGameCLI(IController controller, InputOutputProvider ioProvider) {
        super(controller, ioProvider);
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
        ioProvider.writeLine("Playing a virtual game of Whist.");
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
            if (nbOfRealPlayers <= game.getPlayerCount() && nbOfRealPlayers >= 0) {
                break;
            } else {
                ioProvider.writeLine("Invalid number of real players.");
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

                for (int i = 0; i < game.getPlayerCount(); i++) {
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

                if (playerMap.size() != game.getPlayerCount()) {
                    ioProvider.writeLine("Error: Invalid player names.\nTry again.");
                    continue;
                }

                // Start the game with the given players and bot difficulties
                try {
                    game = controller.startNewPlayGame(playerMap);
                } catch (Exception e) {

                    clearScreen();
                    ioProvider.writeLine("Error: " + e.getMessage() + "\nTry again.");

                    continue;
                }

                // Confirmation message
                clearScreen();
                ioProvider.writeLine("Players registered:\n");
                for (String key : playerMap.keySet()) {
                    ioProvider.writeLine("- " + key);
                }

                break;

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
    @Override
    protected void showRound() {

        // Print out round start info
        clearScreen();
        informUser("Starting a new round!\nThe dealer is: " + game.getDealerName());

        showRoundBiddingPhase();

        // Feedback
        clearScreen();
        printSeparator();
        ioProvider.writeLine("THE BIDDING PHASE HAS ENDED.");
        ioProvider.writeLine("Winning bid: " + game.getFinalBidName());
        ioProvider.writeLine("Declarer(s): " + String.join(", ", game.getFinalBidDeclarers()));
        printSeparator();

        // We wait for user before starting the tricks
        getInputString("Press enter to continue");

        clearScreen();
        informUser("Starting the trick playing phase of the round.");

        // Start the trick phase in the domain layer
        game.startPlayingRound();

        showRoundTrickPhase();

        // Calculate and apply scores for this round.
        game.calculateAndUpdateScores();
    }

    /**
     * Run the bidding phase until bidding stabilizes.
     */
    private void showRoundBiddingPhase() {

        // Loop until the proposer has accepted
        while (true) {

            // Loop until we have a valid final (winning) bid
            while (!game.biddingStabilised()) {

                // Pass to the next player
                int activePlayerIndex = game.getActivePlayerIndex();

                // Check if player is a bot
                if (game.isAutonomous(activePlayerIndex)) {
                    game.proceedAutonomousBid();
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
                        String chosenBid = getChoice("Choose your bid", game.getPossibleBidNames());
                        if (game.bidRequiresTrumpDeclaration(chosenBid)) {
                            game.submitBid(chosenBid, getChoice("Choose your preferred trump suit", controller.getSuits()));
                        } else {
                            game.submitBid(chosenBid, null);
                        }

                        // Stop the loop and pass to the next player
                        break;

                    } catch (Exception e) {
                        clearScreen();
                        ioProvider.writeLine("Error: " + e.getMessage() + "\nPlease try again.");
                    }

                }

            }

            String proposerName = game.getLoneProposerName();
            if (proposerName == null) {
                break;
            } else {
                getInputString("Press enter when " + proposerName + " is ready to choose to play the bid alone or pass.");
                clearScreen();

                showHand("Your Hand:", game.getPlayerCards(proposerName));

                boolean choice = getYesNo("Do you want to play the bid alone?");
                if (choice) {
                    // The proposer wishes to play alone.
                    game.registerLoneProposer(proposerName);
                    game.biddingStabilised();
                    break;
                }
            }

        }
    }


    /**
     * Show all existing bids made by players
     */
    private void showExistingBids() {
        ioProvider.writeLine(game.getExistingBids() + "\n");
    }

    /**
     * Run the trick-playing phase until all tricks have been played.
     */
    private void showRoundTrickPhase() {
        while (game.getTricksLeft() > 0) {
            showTrick();

            informUser("The trick is over.");

            String winner = game.getCurrentTrickWinnerName();
            ioProvider.writeLine("Winner of this trick: " + winner);
            ioProvider.writeLine("(" + winner + " leads the next trick.)");
            ioProvider.writeLine("");
            getInputString("Press enter to continue");

            game.evaluateAndAdvanceTrick();
        }
    }

    /**
     * Wait for the active player to be ready.
     *
     * <p>This is used because multiple players share the same screen.
     * This allows the next player to take their turn privately.
     */
    private void getReady() {
        getInputString("Press enter when " + game.getActivePlayerName() + " is ready.");
        clearScreen();
        informUser("It's " + game.getActivePlayerName() + "'s turn.");
    }

    /**
     * Play a single trick.
     *
     * <p>Players take turns playing a card until the trick is complete.
     * Each player selects a card from their hand which is then registered
     * through the controller.
     */
    private void showTrick() {

        while (!game.isTrickOver()) {

            // Make sure the next player is ready
            int activePlayerIndex = getActivePlayerIndex();

            // Check if player is a bot
            if (game.isAutonomous(activePlayerIndex)) {
                game.processAutonomousCardPlay();
                continue;
            }

            getReady();

            // Show what's currently on the table (cards played by bots before this turn)
            HashMap<String, String> trickCards = game.getCurrentTrickCardsAsStrings();
            if (!trickCards.isEmpty()) {
                ioProvider.writeLine("Cards on the table (in order of play):");
                for (java.util.Map.Entry<String, String> entry : trickCards.entrySet()) {
                    ioProvider.writeLine("  " + entry.getKey() + ": " + entry.getValue());
                }
                ioProvider.writeLine("");
            }

            // Show the trump suits for this round.
            // The original trump is always shown. For Abondance, the active trump
            // may differ or not yet be known (chosen by first card play).
            String originalTrump = game.getOriginalTrumpSuitName();
            String activeTrump = game.getTrumpSuitName();
            if (originalTrump != null) {
                ioProvider.writeLine("Original trump: " + originalTrump);
            }
            if (activeTrump == null) {
                // This is a bid that doesn't have a trump (e.g. Miserie)
                ioProvider.writeLine("Active trump:   None");
            } else if (!activeTrump.equals(originalTrump)) {
                ioProvider.writeLine("Active trump:   " + activeTrump + " (chosen by bid declarer)");
            }
            // If active trump equals original trump, no extra line is needed.
            ioProvider.writeLine("");

            // Show the hands of the open miserie players
            showOpenMiserieHands();

            // Show the active player's hand
            showHand();

            // Build the set of legally allowed cards
            Set<String> allowedCards = new HashSet<>(
                    Arrays.asList(game.getAllowedCardsForCurrentPlayer()));

            // Get a valid card choice from the active player
            while (true) {

                try {

                    // Build the annotated choices: ★ prefix for legal cards
                    String[] originalCards = game.getPlayerCards();
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
                            ioProvider.writeLine(game.getLastTrickString());
                        } catch (Exception e) {
                            ioProvider.writeLine("Error viewing last trick: " + e.getMessage());
                        }
                        getInputString("Press enter to return.");
                        clearScreen();
                        continue;
                    }

                    // Strip the ★ prefix before registering
                    String cardToPlay = chosenCard.startsWith("★ ") ? chosenCard.substring(2) : chosenCard;

                    // Register the card play with the controller
                    game.processCardPlay(cardToPlay);

                    // Stop the loop and pass to the next player
                    break;

                } catch (Exception e) {
                    clearScreen();
                    ioProvider.writeLine("Error: " + e.getMessage() + "\nPlease try again.");
                }
            }

            // Advance the turn
            clearScreen();

        }
    }

    /**
     * Display the hand of the active player.
     */
    private void showHand() {
        showHand("Your Hand:", game.getPlayerCards());
    }

    /**
     * Displays the hands of the open msierie players.
     */
    private void showOpenMiserieHands() {
        HashMap<String, String[]> cards = game.getOpenMiserieHands();
        
        // Print a purposeful notice when the active player is an Open Miserie declarer
        if ("Open Miserie".equals(game.getFinalBidName())) {
            for (String declarer : game.getFinalBidDeclarers()) {
                if (declarer.equals(game.getActivePlayerName())) {
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
        HashMap<String, Integer> points = game.getRoundScoresPerPlayer();

        // Print the points for each player
        for (String playerName : game.getPlayerNames()) {
            ioProvider.writeLine(playerName + ": " + points.get(playerName) + " points");
        }

        printSeparator();
    }

    /**
     * Shows the last dealt card, which is always dealt face up and determines the trump for some bids.
     */
    protected void showLastDealtCard() {
        System.out.println("Last dealt card this round: " + game.getLastDealtCard() + "\n");
    }
}
