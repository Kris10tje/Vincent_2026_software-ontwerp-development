package whistapp.ui;

import whistapp.application.interfaces.*;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.players.PlayerType;

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
    public PlayGameCLI(IController controller, IInputOutputProvider ioProvider) {
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
     * If the number is smaller than the total player count, bot type
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
            if (nbOfRealPlayers <= controller.getPlayerCount() && nbOfRealPlayers >= 0) {
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
                LinkedHashMap<String, PlayerType> playerMap = new LinkedHashMap<>();
                int botCount = 1;

                for (int i = 0; i < controller.getPlayerCount(); i++) {
                    if (i < nbOfRealPlayers) {
                        // We give the human players a type of HUMAN
                        playerMap.put(players[i], PlayerType.HUMAN);
                    } else {
                        // Ask type for this specific bot
                        PlayerType botType = getChoice(
                                "Choose the type for Bot " + botCount + ".",
                                controller.getBotTypes());

                        // Assign automatic name based on difficulty (must be alpha only)
                        char botSuffix = (char) ('A' + botCount - 1);
                        playerMap.put(botType.toString().replace(" ", "") + botSuffix, botType);
                        botCount++;
                    }
                }

                if (playerMap.size() != controller.getPlayerCount()) {
                    ioProvider.writeLine("Error: Invalid player names.\nTry again.");
                    continue;
                }

                // Start the game with the given players and bot difficulties
                try {
                    specificGameController = controller.startNewPlayGame(playerMap);
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
        informUser("Starting a new round!\nThe dealer is: " + specificGameController.getDealer().getName());

        showRoundBiddingPhase();

        // Feedback
        clearScreen();
        printSeparator();
        ioProvider.writeLine("THE BIDDING PHASE HAS ENDED.");
        ioProvider.writeLine("Winning bid: " + specificGameController.getFinalBidName());
        ioProvider.writeLine("Declarer(s): " + formatPlayers(specificGameController.getFinalBidDeclarers()));
        printSeparator();

        // We wait for user before starting the tricks
        getInputString("Press enter to continue");

        clearScreen();
        informUser("Starting the trick playing phase of the round.");

        // Start the trick phase in the domain layer
        specificGameController.startPlayingRound();

        showRoundTrickPhase();

        // Calculate and apply scores for this round.
        specificGameController.calculateAndUpdateScores();
    }

    /**
     * Run the bidding phase until bidding stabilizes.
     */
    private void showRoundBiddingPhase() {

        // Loop until the proposer has accepted
        while (true) {

            // Loop until we have a valid final (winning) bid
            while (!specificGameController.biddingStabilised()) {

                // Pass to the next player
                int activePlayerIndex = specificGameController.getActivePlayerIndex();

                // Check if player is a bot
                if (specificGameController.isAutonomous(activePlayerIndex)) {
                    specificGameController.proceedAutonomousBid();
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

                        // Show the players (that already bid open miserie) their hands
                        showOpenMiserieHands();

                        // Show the active player's hand to help them make a decision on their bid
                        showHand();

                        // Get the bid choice
                        BidType chosenBid = getChoice("Choose your bid", specificGameController.getPossibleBids());
                        if (specificGameController.bidRequiresTrumpDeclaration(chosenBid)) {
                            specificGameController.submitBid(chosenBid, getChoice("Choose your preferred trump suit", controller.getSuits()));
                        } else {
                            specificGameController.submitBid(chosenBid, null);
                        }

                        // Stop the loop and pass to the next player
                        break;

                    } catch (Exception e) {
                        clearScreen();
                        ioProvider.writeLine("Error: " + e.getMessage() + "\nPlease try again.");
                    }

                }

            }

            IPlayer proposer = specificGameController.getLoneProposer();
            if (proposer == null) {
                break;
            } else {
                getInputString("Press enter when " + proposer.getName() + " is ready to choose to play the bid alone or pass.");
                clearScreen();

                showHand("Your Hand:", specificGameController.getCardsByPlayer(proposer));

                boolean choice = getYesNo("Do you want to play the bid alone?");
                if (choice) {
                    // The proposer wishes to play alone.
                    specificGameController.registerLoneProposer(proposer);
                    specificGameController.biddingStabilised();
                    break;
                }
            }

        }
    }


    /**
     * Show all existing bids made by players.
     */
    private void showExistingBids() {
        ioProvider.writeLine("Currently active bids:");
        LinkedHashMap<IPlayer, BidType> bids = specificGameController.getExistingBids();
        for (IPlayer player : bids.keySet()) {
            ioProvider.writeLine(" - " + player.getName() + ": " + bids.get(player));
        }
    }

    /**
     * Run the trick-playing phase until all tricks have been played.
     */
    private void showRoundTrickPhase() {
        while (true) {
            showTrick();

            informUser("The trick is over.");

            IPlayer winner = specificGameController.getCurrentTrickWinner();
            ioProvider.writeLine("Winner of this trick: " + winner.getName());
            ioProvider.writeLine("(" + winner.getName() + " leads the next trick.)");
            ioProvider.writeLine("");
            getInputString("Press enter to continue");

            // Advance the trick
            boolean finish = specificGameController.evaluateAndAdvanceTrick();

            if (finish) {
                // All tricks have been played (this can also mean the game has stopped early)
                break;
            }
        }
    }

    /**
     * Wait for the active player to be ready.
     *
     * <p>This is used because multiple players share the same screen.
     * This allows the next player to take their turn privately.
     */
    private void getReady() {
        getInputString("Press enter when " + specificGameController.getActivePlayer().getName() + " is ready.");
        clearScreen();
        informUser("It's " + specificGameController.getActivePlayer().getName() + "'s turn.");
    }

    /**
     * Play a single trick.
     *
     * <p>Players take turns playing a card until the trick is complete.
     * Each player selects a card from their hand which is then registered
     * through the controller.
     */
    private void showTrick() {

        while (!specificGameController.isTrickOver()) {

            // Make sure the next player is ready
            int activePlayerIndex = specificGameController.getActivePlayerIndex();

            // Check if player is a bot
            if (specificGameController.isAutonomous(activePlayerIndex)) {
                specificGameController.processAutonomousCardPlay();
                continue;
            }

            getReady();

            // Show what's currently on the table (cards played by bots before this turn)
            LinkedHashMap<IPlayer, ICard> trickCards = specificGameController.getCurrentTrickCards();
            if (!trickCards.isEmpty()) {
                ioProvider.writeLine("Cards on the table (in order of play):");
                for (java.util.Map.Entry<IPlayer, ICard> entry : trickCards.entrySet()) {
                    ioProvider.writeLine("  " + entry.getKey().getName() + ": " + CardFormatter.formatCard(entry.getValue()));
                }
                ioProvider.writeLine("");
            }

            // Show the trump suits for this round.
            // The original trump is always shown. For Abondance, the active trump
            // may differ or not yet be known (chosen by first card play).
            Suit originalTrump = specificGameController.getOriginalTrumpSuit();
            Suit activeTrump = specificGameController.getTrumpSuit();
            if (originalTrump != null) {
                ioProvider.writeLine("Original trump: " + CardFormatter.formatCardSuit(originalTrump));
            }
            if (activeTrump == null) {
                // This is a bid that doesn't have a trump (e.g. Miserie)
                ioProvider.writeLine("Active trump:   None");
            } else if (!activeTrump.equals(originalTrump)) {
                ioProvider.writeLine("Active trump:   " + CardFormatter.formatCardSuit(activeTrump) + " (chosen by bid declarer)");
            }
            // If active trump equals original trump, no extra line is needed.
            ioProvider.writeLine("");

            // Show the hands of the open miserie players
            showOpenMiserieHands();

            // Show the active player's hand
            showHand();

            // Build the set of legally allowed cards
            Set<String> allowedCards = new HashSet<>();
            for (ICard card : specificGameController.getAllowedCardsForCurrentPlayer()) {
                allowedCards.add(CardFormatter.formatCard(card));
            }

            // Get a valid card choice from the active player
            while (true) {

                try {

                    // Build the annotated choices: ★ prefix for legal cards
                    ArrayList<ICard> originalCards = specificGameController.getCardsForCurrentPlayer();
                    String[] choices = new String[originalCards.size() + 1];
                    for (int i = 0; i < originalCards.size(); i++) {
                        String formattedCard = CardFormatter.formatCard(originalCards.get(i));
                        choices[i] = allowedCards.contains(formattedCard)
                                ? "★ " + formattedCard
                                : formattedCard;
                    }
                    choices[choices.length - 1] = "View Last Trick";

                    // Get the choice
                    String chosenCard = getChoice("Choose which card to play", choices);

                    if (chosenCard.equals("View Last Trick")) {
                        try {
                            showLastTrick();
                        } catch (Exception e) {
                            ioProvider.writeLine("Error viewing last trick: " + e.getMessage());
                        }
                        getInputString("Press enter to return.");
                        clearScreen();
                        continue;
                    }

                    // Strip the ★ prefix before registering
                    String cardToPlay = chosenCard.startsWith("★ ") ? chosenCard.substring(2) : chosenCard;

                    ICard selectedCard = null;
                    for (ICard card : originalCards) {
                        if (CardFormatter.formatCard(card).equals(cardToPlay)) {
                            selectedCard = card;
                            break;
                        }
                    }
                    if (selectedCard == null) {
                        throw new IllegalArgumentException("Selected card could not be found in hand.");
                    }

                    // Register the card play with the controller
                    specificGameController.processCardPlay(selectedCard);

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
        showHand("Your Hand:", specificGameController.getCardsForCurrentPlayer());
    }

    /**
     * Display a given hand of domain Card objects.
     * 
     * <p>This method accepts domain objects (ArrayList<ICard>) and handles the responsibility
     * of formatting them for display. This maintains GRASP Separation of Concerns: the
     * Application layer returns domain objects, and the UI layer handles presentation.
     * 
     * @param header the header/title for the hand display
     * @param cards the list of card domain objects to display
     */
    protected void showHand(String header, ArrayList<ICard> cards) {
        ioProvider.writeLine(header);
        for (int i = 0; i < cards.size(); i++) {
            // Format each card using CardFormatter, delegating presentation responsibility to the UI layer
            String formattedCard = CardFormatter.formatCard(cards.get(i));
            ioProvider.writeLine("  • " + String.format("%-20s", formattedCard));
            if ((i + 1) % 3 == 0) {
                ioProvider.writeLine("");
            }
        }
        if (cards.size() % 3 != 0) {
            ioProvider.writeLine("");
        }
        ioProvider.writeLine("");
    }

    /**
     * Displays the hands of the open miserie players.
     */
    private void showOpenMiserieHands() {
        HashMap<IPlayer, ArrayList<ICard>> cards = specificGameController.getOpenMiserieHands();

        if (cards.isEmpty()) return;
        for (IPlayer player : cards.keySet()) {
            showHand(player.getName() + "'s Hand (Open Miserie):", cards.get(player));
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
        HashMap<IPlayer, Integer> points = specificGameController.getRoundScoresPerPlayer();

        // Print the points for each player
        for (IPlayer player : specificGameController.getPlayers()) {
            ioProvider.writeLine(player.getName() + ": " + points.get(player) + " points");
        }

        printSeparator();
    }

    /**
     * Shows the last dealt card, which is always dealt face up and determines the trump for some bids.
     */
    protected void showLastDealtCard() {
        ioProvider.writeLine("Last dealt card this round: " + CardFormatter.formatCard(specificGameController.getLastDealtCard()) + "\n");
    }

    /**
     * Displays the last completed trick in order of play.
     */
    private void showLastTrick() {
        LinkedHashMap<IPlayer, ICard> trick = specificGameController.getPreviousTrickCards();
        if (trick.isEmpty()) {
            ioProvider.writeLine("No tricks have been played yet.");
            return;
        }

        ioProvider.writeLine("Last trick (in order of play):");
        for (Map.Entry<IPlayer, ICard> entry : trick.entrySet()) {
            ioProvider.writeLine(entry.getKey().getName() + ": " + CardFormatter.formatCard(entry.getValue()));
        }
    }

    /**
     * Formats an array of players to a comma-separated display string.
     */
    private String formatPlayers(IPlayer[] players) {
        if (players.length == 0) {
            return "None";
        }

        String[] names = new String[players.length];
        for (int i = 0; i < players.length; i++) {
            names[i] = players[i].getName();
        }
        return String.join(", ", names);
    }
}
