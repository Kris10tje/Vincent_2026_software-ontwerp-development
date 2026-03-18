package whistapp.application;

import whistapp.domain.cards.Suit;
import whistapp.domain.game.ScoreGame;
import whistapp.domain.players.BotDifficulty;
import whistapp.domain.game.PlayGame;
import whistapp.domain.game.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * The controller; acts as a thin communication layer between the UI layer and DOMAIN layer
 */
public class Controller implements IController{

    private Game game;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public Controller() {

    }

    /* -------------------------------------------------------------------------- */
    /*                                 Public Methods                             */
    /* -------------------------------------------------------------------------- */

    /**
     * Start a new game of ScoreWhist with the given player names.
     */
    public void startNewScoreGame(ArrayList<String> playerNames) {
        this.game = new ScoreGame(playerNames);
    }

    /**
     * Start a new game of PlayWhist with the given player names and bot difficulties.
     * The player map contains player name to BotDifficulty (null if human player).
     */
    public void startNewPlayGame(LinkedHashMap<String, BotDifficulty> playerNamesAndBotDifficulties) {
        this.game = new PlayGame(playerNamesAndBotDifficulties);
    }

    /**
     * A function that retrieves a string showing the last played trick
     *
     * @return the string representing the last played trick
     */
    public String getLastTrickString() {

        // Check if a game is active
        if (game == null) {
            throw new IllegalStateException("No game is currently active.");
        }

        // Get the last trick from the current round via the Polymorphic interface
        String lastTrick = game.getLastTrickString();
        if (lastTrick == null) {
            throw new IllegalStateException("No tricks have been played yet in this round.");
        }
        return lastTrick;
    }

    /**
     * Exit the application.
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * A simple getter returning all bids a player can make.
     *
     * @return An array of BidTypes, that can be printed as a string
     */
    public static String[] getBidTypes() {
        return Game.getBidTypes();
    }

    /**
     * A simple getter for player names
     *
     * @return The names of every player in the game, as an ArrayList of strings
     */
    public ArrayList<String> getPlayerNames() {
        return game.getPlayerNames();
    }

    // Number of tricks per round minus tricks already won by someone

    /**
     * A simple getter for the amount of tricks left in a round
     *
     * <p> Equivalent to the amount of rounds left including the current one.
     *
     * @return the amount of tricks left
     */
    public int getTricksLeft() {
        return game.getTricksLeft();
    }

    /**
     * A simple getter for the player count of a game
     *
     * @return The number of players (including bots) in a game.
     */
    public static int getPlayerCount() {
        return Game.getPlayerCount();
    }

    // register bid for active player

    /**
     * Registers a bid as the current player and advances to the next player.
     *
     * @param bid the bid to register
     */
    public void registerBid(String bid) {
        game.submitBid(bid, null);
    }

    /**
     * Registers a bid with newTrumpSuit as the current player and advances to the next player.
     *
     * @param bid the bid to register
     * @param newTrumpSuit The new trump suit to go along with a bid.
     *                     {@code null} is also allowed if the bid does not require declaring new trump suit at bid time.
     */
    public void registerBid(String bid, Suit newTrumpSuit) {
        game.submitBid(bid, newTrumpSuit);
    }

    /**
     * A method for processing a certain bid for the players.
     * This bid is to be given as a map playerName -> BidType.
     *
     * @param bids The submitted bids.
     */
    public void registerBids(HashMap<String, String> bids) {
        game.registerBids(bids);
    }

    /**
     * Registers the reshuffle state during the bidding stage of a round.
     *
     * <p>Will enable double points, as per whist rules.
     *
     * @param reshuffled {@code true} if there was a reshuffle during the bidding stage, otherwise {@code false}
     */
    public void setReshuffledState(boolean reshuffled) {
        if (game != null) {
            game.setReshuffledState(reshuffled);
        }
    }

    /**
     * Registers the number of tricks won per player for every player at once.
     *
     * @param nbOfTricksWonPerPlayer A hashmap with keys being player names (as strings) and values the amount of tricks that player won
     */
    public void registerNbOfTricksWonPerPlayer(HashMap<String, Integer> nbOfTricksWonPerPlayer) {
        game.updateScores(nbOfTricksWonPerPlayer);
    }

    /**
     * Starts the next round
     *
     * <p>Might reset some state regarding the round, depends on the type of game.
     */
    public void nextRound() {
        if (game != null) {
            game.startNewRound();
        }
    }

    /**
     * A function that parses the round bidtype, when everyone has picked a bid.
     *
     * @return {@code true} if bidding has stabilized and at least one person has picked a bid. {@code false} if everone passed and there was a reshuffle.
     */
    public boolean biddingStabilised() {
        try {
            boolean stabilised = game.evaluateRoundBids();
            if (!stabilised) {
                System.out.println("Everyone passed! Reshuffling and dealing new cards...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
                game.restartFailedRound();
            }
            return stabilised;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Starts the playing phase of a round
     */
    public void startPlayingRound() {
        if (game != null) {
            game.startPlayingRound();
        }
    }

    /**
     * Returns the active chosen bids name.
     *
     * @return The name of the final round bid.
     */
    public String getFinalBidName() {
        return game.getFinalBidName();
    }

    /**
     * Return the active round bids declarers.
     *
     * @return The names of the players declaring the rounds bid.
     */
    public String[] getFinalBidDeclarers() {
        return game.getFinalBidDeclarers();
    }

    /**
     * Return the available bids that can still be played.
     *
     * @return The available bids that can still be played.
     */
    public String[] getPossibleBids() {
        return game.getPossibleBidNames();
    }

    /**
     * A getter for the name of the active dealer.
     *
     * @return the name of the active dealer.
     */
    public String getDealerName() {
        return game.getDealerName();
    }

    /**
     * A getter for the index of the player whose turn it currently is.
     *
     * @return the index of the player whose turn it currently is.
     */
    public int getActivePlayerIndex() {
        try {
            String activePlayerName = game.getActivePlayerName();
            return getPlayerNames().indexOf(activePlayerName);
        } catch (IllegalStateException e) {
            return -1;
        }
    }

    /**
     * A getter to determine when the current trick is over.
     *
     * @return {@code true} if all cards have been played, {@code false} otherwise.
     */
    public boolean trickIsOver() {
        return game.isTrickOver();
    }

    /**
     * Advances the current round to the next trick.
     *
     * @return {@code true} if this was the final trick, and we could not advance to the next trick, {@code false} otherwise.
     */
    public boolean evaluateAndAdvanceTrick() {
        return game.evaluateAndAdvanceTrick();
    }

    /**
     * Triggers score calculation for the completed round.
     * Must be called after all tricks are played, before showing scores.
     * Should not be used when all tricks of a round are registered at once (e.g. ScoreGame).
     */
    public void updateScores() {
        game.calculateAndUpdateScores();
    }

    /**
     * Called when the active user (tries to) play a card.
     *
     * @param card The played card, as a string.
     */
    public void registerPlayCard(String card) {
        game.processCardPlay(card);
    }

    /**
     * Gets the cards of the currently active player.
     *
     * @return An array containing the string representations of every card in the active players hand.
     */
    public String[] getCards() {
        return game.getPlayerCards(game.getActivePlayerName());
    }

    /**
     * Gets the cards of the currently active player.
     *
     * @return An array containing the string representations
     *         of every card in the active players hand.
     */
    public String[] getCards(String playerName) {
        return game.getPlayerCards(playerName);
    }

    /**
     * Gets the hands of the open miserie players.
     *
     * @return A map of player names and their hands.
     */
    public HashMap<String, String[]> getOpenMiserieHands() {
        return game.getOpenMiserieHands(game.getActivePlayerName());
    }

    /**
     * Gets the possible bot difficulties.
     *
     * @return An array containing all possible difficulties.
     */
    public BotDifficulty[] getBotDifficultyOptions() {
        return Game.getBotDifficultyOptions();
    }

    /**
     * Determines if the player at the given index is autonomous (a bot).
     *
     * @return {@code true} if the player at the given index is a bot,
     *         {@code false} otherwise.
     */
    public boolean isAutonomous(int playerIndex) {
        if (game == null) {
            return false;
        }
        return game.isAutonomous(playerIndex);
    }

    /**
     * Commands the game to proceed with an autonomous bid.
     */
    public void proceedAutonomousBid() {
        if (game != null) {
            game.proceedAutonomousBid();
        }
    }

    /**
     * Commands the game to proceed with an autonomous card play.
     */
    public void proceedAutonomousCardPlay() {
        if (game != null) {
            game.processAutonomousCardPlay();
        }
    }

    /**
     * Gets the results of the entire game, namely the amount of points for every player.
     *
     * @return A hashmap with keys equal to the names of every player as a string,
     * and values the amount of points that player scored this game.
     */
    public HashMap<String, Integer> getGameScoresPerPlayer() {
        return game.getScoresPerPlayer();
    }

    /**
     * Gets the results of a round, only implemented for PlayGameCLI
     *
     * @return A hashmap with keys equal to the names of every player as a string,
     * and values the amount of points that player scored this round.
     */
    public HashMap<String, Integer> getRoundScoresPerPlayer() {
        return game.getRoundScoresPerPlayer();
    }

    /**
     * A method reporting the played cards in the active trick.
     *
     * @return A hashmap with keys equal to the names of every player as a string,
     * and values equal to the card they played this trick as a string.
     */
    public HashMap<String, String> getCurrentTrickCardsAsStrings() {
        return game.getCurrentTrickCardsAsStrings();
    }

    /**
     * Returns the allowed card names for the current active player.
     *
     * @return An array containing the card names
     *         of the cards the active player is allowed to play.
     */
    public String[] getAllowedCardsForCurrentPlayer() {
        return game.getAllowedCardsForCurrentPlayer();
    }

    /**
     * Returns the name of the trump suit for the current round.
     *
     * @return The name of the trump suit.
     */
    public String getTrumpSuitName() {
        return game.getTrumpSuitName();
    }

    /**
     * Returns the original (deck-dealt) trump suit name,
     * equal to the suit of the last dealt card.
     *
     * @return The name of the original trump suit.
     */
    public String getOriginalTrumpSuitName() {
        return game.getOriginalTrumpSuitName();
    }

    /**
     * A getter for the name of the winner of the current trick.
     *
     * @return The name of the winner of the current trick
     */
    public String getCurrentTrickWinnerName() {
        return game.getCurrentTrickWinnerName();
    }

    /**
     * A getter for the currently active players name.
     *
     * @return The name of the currently active player.
     */
    public String getActivePlayerName() {
        return game.getActivePlayerName();
    }

    /**
     * A getter for the last dealt card this round (which is always dealt face up).
     *
     * @return the name of the last dealt card.
     */
    public String getLastDealtCard() {
        return game.getLastDealtCard();
    }

    /**
     * A getter that returns true if the given bid name
     * equates to a bid that requires a trump to be declared at bid time.
     *
     * @param chosenBid The bid name
     * @return {@code true} if the bid requires a trump to be declared at bid time,
     *         {@code false} otherwise.
     */
    public boolean bidRequiresTrumpDeclaration(String chosenBid) {
        return game.bidRequiresTrumpDeclaration(chosenBid);
    }

    /**
     * A simple getter that returns all suits.
     *
     * @return An array containing all suits.
     */
    public Suit[] getSuits() {
        return game.getSuits();
    }

    /**
     * Returns a string showing every active bid in this round.
     *
     * @return The string of the active bids.
     */
    public String getExistingBids() {
        return game.getExistingBids();
    }

    /**
     * Returns the string of the name of the lone proposer.
     * @return the name
     */
    public String getLoneProposerName() {
        return game.getLoneProposerName();
    }

    /**
     * Processes the lone proposer to play a proposal alone bid.
     * @param proposer The proposer to register.
     */
    public void registerLoneProposer(String proposer) {
        game.registerLoneProposer(proposer);
    }


}
