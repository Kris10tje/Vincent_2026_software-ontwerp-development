package whistapp.domain.game;

import whistapp.domain.players.BotDifficulty;
import whistapp.domain.players.Player;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;
import whistapp.domain.round.Round;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class Game {

    protected final ArrayList<Player> players = new ArrayList<>();

    public static final int NUMBER_OF_PLAYERS = 4;

    /* -------------------------------------------------------------------------- */
    /*                              Constructors                                  */
    /* -------------------------------------------------------------------------- */

    public Game() {
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public methods                               */
    /* -------------------------------------------------------------------------- */

    /**
     * A method for checking if the number of tricks won per player is valid.
     * This method dispatches to the Round class.
     *
     * @param nbOfTricksWonPerPlayer The number of tricks won per player.
     * @return True, if the number of tricks won per player is valid,
     * False, otherwise.
     */
    public static boolean nbOfTricksWonPerPlayerValid(int[] nbOfTricksWonPerPlayer) {
        return Round.nbOfTricksWonPerPlayerValid(nbOfTricksWonPerPlayer);
    }

    /**
     * A method for checking if the players are valid.
     * This method dispatches to the Player class.
     *
     * @param names The names of the players.
     * @return True, if the players are valid,
     * False, otherwise.
     */
    public static boolean playersValid(ArrayList<String> names) {
        return Player.playersValid(names);
    }

    /**
     * A method for starting a new round.
     * This is an abstract method, because the implementation depends on the type of game.
     */
    public abstract void startNewRound();

    /**
     * A method for register the final bid into the current round.
     *
     * @param bidType       The winning bid type.
     * @param declarerNames The players declaring this bid.
     * @param wasFirstTry   True, if the final bid type was found first try,
     *                      False, if the deck had to be redealt
     *                      at least once because everyone passed.
     */
    public void registerFinalBid(BidType bidType, ArrayList<String> declarerNames, boolean wasFirstTry) {
        // Get the current round
        Round currentRound = getCurrentRound();

        // Get the declarers of the bid
        ArrayList<Player> declarers = getPlayersByName(declarerNames);

        // Set the final bid
        currentRound.setFinalBid(bidType, declarers, wasFirstTry);
    }

    /**
     * A method for processing a certain bid for the players.
     * This bid is to be given as a map playerName -> BidType.
     * By default, this is not supported and throws an exception.
     *
     * @param bids The submitted bids.
     */
    public void registerBids(HashMap<String, String> bids) {
        throw new IllegalStateException(
                "registerBids is only supported for ScoreGames. PlayGames should use interactive bidding.");
    }

    /**
     * Set whether the deck was reshuffled this round.
     * By default, this is not supported and throws an exception.
     *
     * @param reshuffled True if reshuffled, false otherwise.
     */
    public void setReshuffledState(boolean reshuffled) {
        throw new IllegalStateException("setReshuffledState is only supported for ScoreGames.");
    }

    /**
     * A method for starting the playing phase of the round.
     * By default, this is not supported and throws an exception.
     */
    public void startPlayingRound() {
        throw new IllegalStateException("startPlayingRound is only supported for PlayGames.");
    }

    /**
     * Returns the name of the final winning bid.
     */
    public String getFinalBidName() {
        throw new IllegalStateException("getFinalBidName is only supported for PlayGames.");
    }

    /**
     * Returns the names of the declarers of the final winning bid.
     */
    public String[] getFinalBidDeclarers() {
        throw new IllegalStateException("getFinalBidDeclarers is only supported for PlayGames.");
    }

    /**
     * Returns the possible bids that can still be played.
     */
    public String[] getPossibleBidNames() {
        throw new IllegalStateException("getPossibleBidNames is only supported for PlayGames.");
    }

    /**
     * Returns the name of the dealer for the current round.
     */
    public String getDealerName() {
        throw new IllegalStateException("getDealerName is only supported for PlayGames.");
    }

    /**
     * A method for updating the scores
     * of the players after a round of playing.
     *
     * @param tricksPerPlayer The amount of tricks a given player has won.
     */
    public void updateScores(HashMap<String, Integer> tricksPerPlayer) {
        if (tricksPerPlayer.size() != NUMBER_OF_PLAYERS) {
            throw new IllegalArgumentException("Invalid amount of players.");
        }

        // Find the actual player objects corresponding to the names
        HashMap<Player, Integer> tricksWon = new HashMap<>();
        ArrayList<Player> players = getPlayersByName(new ArrayList<>(tricksPerPlayer.keySet()));
        for (Player player : players) {
            tricksWon.put(player, tricksPerPlayer.get(player.getName()));
        }

        // We calculate the score differences
        HashMap<Player, Integer> deltaPointsPerPlayer = getCurrentRound().processRoundOutcome(tricksWon);

        // Update the scores according to the deltas
        for (Player player : deltaPointsPerPlayer.keySet()) {
            player.updateScore(deltaPointsPerPlayer.get(player));
        }
    }

    public void calculateAndUpdateScores() {
        throw new IllegalStateException("calculateAndUpdateScores is only supported for PlayGames.");
    }

    /**
     * A method for submitting a single bid interactively for the current player.
     * By default, this is not supported and throws an exception.
     *
     * @param bidType      The bid to submit.
     * @param newTrumpSuit The optional new trump suit.
     */
    public void submitBid(String bidType, Suit newTrumpSuit) {
        throw new IllegalStateException("Interactive bidding is only supported for PlayGames.");
    }

    /**
     * Evaluates the submitted interactive bids of the current round to determine the final bid and the declarers.
     * By default, this is not supported and throws an exception.
     *
     * @return True, if a final bid was chosen and the playing can start.
     * False, if everyone passed.
     */
    public boolean evaluateRoundBids() {
        throw new IllegalStateException("Evaluating interactive bids is only supported for PlayGames.");
    }

    /**
     * A method for processing a given card as the card played by the current player.
     * By default, this is not supported and throws an exception.
     *
     * @param card The card played.
     */
    public void processCardPlay(String card) {
        throw new IllegalStateException("Interactive playing is only supported for PlayGames.");
    }

    /**
     * A method for processing an autonomous bid.
     * By default, this is not supported and throws an exception.
     */
    public void proceedAutonomousBid() {
        throw new IllegalStateException("Autonomous bidding is only supported for PlayGames.");
    }

    /**
     * A method for restarting a round where the bidding phase failed.
     * By default, this is not supported and throws an exception.
     */
    public void restartFailedRound() {
        throw new IllegalStateException("Restarting failed rounds is only supported for PlayGames.");
    }

    /**
     * A method for processing an autonomous card play.
     * By default, this is not supported and throws an exception.
     */
    public void processAutonomousCardPlay() {
        throw new IllegalStateException("Autonomous card play is only supported for PlayGames.");
    }

    /**
     * A method for evaluating the current trick of the current round.
     * By default, this is not supported and throws an exception.
     */
    public boolean evaluateAndAdvanceTrick() {
        throw new IllegalStateException("Evaluating trick is only supported for PlayGames.");
    }

    /**
     * A method for viewing the last completed trick from the current round.
     * By default, this is not supported and throws an exception.
     *
     * @return A string representation of the last trick.
     */
    public String getLastTrickString() {
        throw new IllegalStateException("Viewing the last trick is only supported for PlayGames.");
    }

    /**
     * A simple getter that returns true if the given bid name
     * equates to a bid that requires a trump to be declared at bid time.
     * By default, this is not supported and throws an exception.
     *
     * @param chosenBid The name of the bid
     * @return {@code true} if the bid requires a trump to be declared at bid time,
     *         {@code false} otherwise.
     */
    public boolean bidRequiresTrumpDeclaration(String chosenBid) {
        throw new IllegalStateException("bidRequiresTrumpDeclaration is only supported for PlayGames.");
    }

    /**
     * A simple function that transforms a string representation of a bid into an enum value.
     * By default, this is not supported and throws an exception.
     *
     * @param bid The name of the bid.
     * @return The enum value.
     */
    public BidType bidFromString(String bid) {
        return BidType.fromString(bid);
    }

    /**
     * Processes the lone proposer to play a proposal alone bid.
     * By default, this is not supported and throws an exception.
     *
     * @param proposer The proposer to register.
     */
    public void registerLoneProposer(String proposer) {
        throw new IllegalStateException("registerLoneProposer is only supported for PlayGames.");
    }

    /* -------------------------------------------------------------------------- */
    /*                             Protected methods                              */
    /* -------------------------------------------------------------------------- */

    /**
     * A method to check if the players can be initialized.
     *
     * @param players The list of players to check.
     */
    protected void validatePlayerInitialization(ArrayList<String> players)
            throws IllegalArgumentException, IllegalStateException {

        // Check the size
        if (players.size() != getPlayerCount()) {
            throw new IllegalArgumentException("Invalid amount of players.");
        }
        if (!this.players.isEmpty()) {
            throw new IllegalStateException("Can't add players to an already initialized Game.");
        }

        // Check the names
        if (!playersValid(players)) {
            throw new IllegalArgumentException("Invalid player names.");
        }

    }

    /* -------------------------------------------------------------------------- */
    /*                                 Getters                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * Finds if the player at the given index is autonomous (a bot).
     * By default, this is not supported and returns false.
     *
     * @param playerIndex The index of the player to check
     * @return True if autonomous, false otherwise
     */
    public boolean isAutonomous(int playerIndex) {
        return false;
    }

    /**
     * Returns true if the current trick is completely played.
     * By default, this is not supported and throws an exception.
     */
    public boolean isTrickOver() {
        throw new IllegalStateException("Trick completion tracking is only supported for PlayGames.");
    }

    /**
     * A simple getter for the different bot difficulty options.
     */
    public static BotDifficulty[] getBotDifficultyOptions() {
        return BotDifficulty.values();
    }

    /**
     * A simple getter for the different bid types.
     */
    public static String[] getBidTypes() {
        return Arrays.stream(BidType.getBidTypes()).map(BidType::toString).toArray(String[]::new);
    }

    /**
     * A simple dispatch getter giving the number of tricks per round.
     * This method dispatches to the Round class.
     *
     * @return The number of tricks per round.
     */
    public static int getTrickCountPerRound() {
        return Round.getTrickCountPerRound();
    }

    /**
     * A simple getter giving the number of players.
     *
     * @return The number of players.
     */
    public static int getPlayerCount() {
        return NUMBER_OF_PLAYERS;
    }

    /**
     * A simple getter giving the player objects.
     */
    protected ArrayList<Player> getPlayers() {
        // We copy the list so it can't be changed
        return new ArrayList<>(players);
    }

    /**
     * A simple getter giving the player names.
     */
    public ArrayList<String> getPlayerNames() {
        // Create a list for the player names
        ArrayList<String> playerNames = new ArrayList<>();

        // Add the player names to the list
        for (Player player : getPlayers()) {
            playerNames.add(player.getName());
        }

        // Return the list
        return playerNames;
    }

    /**
     * A simple getter finding the player with the given name.
     * Returns null if there is no such player.
     */
    public Player getPlayerByName(String name) {

        // Find the player with the given name
        for (Player player : getPlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }

        // No player found
        return null;

    }

    /**
     * A simple getter finding multiple players with given names.
     * Can have null values if there exists no player with that name.
     */
    protected ArrayList<Player> getPlayersByName(ArrayList<String> playerNames) {
        // Create a list for the players
        ArrayList<Player> players = new ArrayList<>();

        // Add the players to the list
        for (String playerName : playerNames) {
            players.add(getPlayerByName(playerName));
        }

        // Return the list
        return players;
    }

    /**
     * A simple getter finding the scores for each of the players of the game.
     */
    public HashMap<String, Integer> getScoresPerPlayer() {
        // Create a map for the scores
        HashMap<String, Integer> scoresPerPlayer = new HashMap<>();

        // Add the scores to the map
        for (Player player : getPlayers()) {
            scoresPerPlayer.put(player.getName(), player.getScore());
        }

        // Return the map
        return scoresPerPlayer;
    }

    /**
     * A getter finding the round scores for each of the players of the game.
     */
    public HashMap<String, Integer> getRoundScoresPerPlayer() {
        throw new IllegalStateException("getRoundScoresPerPlayer is only implemented for PlayGame");
    }

    /**
     * Returns the current round.
     */
    protected abstract Round getCurrentRound();

    /**
     * Returns the number of tricks left in this round.
     * By default, this is not supported and throws an exception.
     */
    public int getTricksLeft() {
        throw new IllegalStateException("Trick counting is only supported for PlayGames.");
    }

    /**
     * Returns the name of the active player (either bidding or playing).
     * By default, this is not supported and throws an exception.
     */
    public String getActivePlayerName() {
        throw new IllegalStateException("Interactive tracking is only supported for PlayGames.");
    }

    /**
     * Returns the cards of the specified player.
     * By default, this is not supported and throws an exception.
     */
    public String[] getPlayerCards(String playerName) {
        throw new IllegalStateException("Interactive tracking is only supported for PlayGames.");
    }

    /**
     * Returns the trump suit of the current round.
     * By default, this is not supported and throws an exception.
     */
    public Suit getCurrentRoundTrumpSuit() {
        throw new IllegalStateException("Trump suit tracking is only supported for PlayGames.");
    }

    /**
     * Returns the highest bid of the current round.
     * By default, this is not supported and throws an exception.
     */
    public BidType getHighestBid() {
        throw new IllegalStateException("Interactive bid tracking is only supported for PlayGames.");
    }

    /**
     * Returns the open miserie hands of the current round.
     * By default, this is not supported and throws an exception.
     *
     * @param currentPlayer The player currently playing.
     */
    public HashMap<String, String[]> getOpenMiserieHands(String currentPlayer) {
        throw new IllegalStateException("Interactive hands is only supported for PlayGames.");
    }

    /**
     * Returns the cards played in the current trick.
     * By default, this is not supported and throws an exception.
     */
    public LinkedHashMap<String, String> getCurrentTrickCardsAsStrings() {
        throw new IllegalStateException("getCurrentTrickCardsAsStrings is only supported for PlayGames.");
    }

    /**
     * Returns the cards the current player is legally allowed to play.
     * By default, this is not supported and throws an exception.
     */
    public String[] getAllowedCardsForCurrentPlayer() {
        throw new IllegalStateException("getAllowedCardsForCurrentPlayer is only supported for PlayGames.");
    }

    /**
     * Returns a display name for the trump suit of the current round.
     * By default, this is not supported and throws an exception.
     */
    public String getTrumpSuitName() {
        throw new IllegalStateException("getTrumpSuitName is only supported for PlayGames.");
    }

    /**
     * Returns the original (deck-dealt) trump suit name for display.
     * By default, this is not supported and throws an exception.
     */
    public String getOriginalTrumpSuitName() {
        throw new IllegalStateException("getOriginalTrumpSuitName is only supported for PlayGames.");
    }

    /**
     * Returns the name of the winner of the current trick.
     * By default, this is not supported and throws an exception.
     */
    public String getCurrentTrickWinnerName() {
        throw new IllegalStateException("getCurrentTrickWinnerName is only supported for PlayGames.");
    }

    /**
     * Simple getter that returns the last dealt card as a string.
     * By default, this is not supported and throws an exception.
     *
     * @return the name of the last dealt card.
     */
    public String getLastDealtCard() {
        throw new IllegalStateException("getLastDealtCard is only supported for PlayGames.");
    }

    /**
     * A simple getter that returns all suits.
     * By default, this is not supported and throws an exception.
     *
     * @return An array containing all suits
     */
    public Suit[] getSuits() {
        return Suit.values();
    }

    /**
     * Returns a string showing the existing bids made by players this round.
     * By default, this is not supported and throws an exception.
     *
     * @return The string.
     */
    public String getExistingBids() {
        throw new IllegalStateException("getExistingBids is only supported for PlayGames.");
    }

    /**
     * Returns the string of the name of the lone proposer.
     * By default, this is not supported and throws an exception.
     *
     * @return The name.
     */
    public String getLoneProposerName() {
        throw new IllegalStateException("getLoneProposerName is only supported for PlayGames.");
    }

    /* -------------------------------------------------------------------------- */
    /*                                   Setters                                  */
    /* -------------------------------------------------------------------------- */

    /**
     * A method to set the scores of all players to a given amount.
     *
     * @param score The score to set to.
     * <p><b>Precondition:</b> The game has been initialized with players:
     * {@code getPlayers().size() == 4}
     */
    protected void setAllScores(int score) {
        // Check if the game has been initialized with players
        if (getPlayers().isEmpty()) {
            throw new IllegalStateException("Can't set scores to an uninitialized Game.");
        }

        // Set the scores of all players
        for (Player player : getPlayers()) {
            player.updateScore(-player.getScore() + score);
        }
    }
}
