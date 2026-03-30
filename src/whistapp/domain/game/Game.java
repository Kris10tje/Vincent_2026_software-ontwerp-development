package whistapp.domain.game;

import whistapp.domain.players.Player;
import whistapp.domain.interfaces.*;
import whistapp.domain.bids.BidType;
import whistapp.domain.players.PlayerType;
import whistapp.domain.round.Round;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class Game<TRound extends IRound> implements IGame {

    protected final ArrayList<Player> players = new ArrayList<>();
    private final ArrayList<TRound> rounds = new ArrayList<>(); // Multiple rounds per game
    protected TRound currentRound;

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
     * A method to initialize the given players.
     *
     * @param playerNamesAndTypes A map of player names to their PlayerType. Null means human.
     */
    protected void initializePlayers(LinkedHashMap<String, PlayerType> playerNamesAndTypes) {

        // Check if the players can be initialized
        validatePlayerInitialization(new ArrayList<>(playerNamesAndTypes.keySet()));

        // Initialize the players using the explicit Creator pattern
        for (HashMap.Entry<String, PlayerType> entry : playerNamesAndTypes.entrySet()) {
            String name = entry.getKey();
            PlayerType type = entry.getValue();

            this.players.add(new Player(name, type));
        }

        // Initialize the player's scores
        setAllScores(0);

    }

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
    public void startNewRound()
    {
        TRound round = createRound();
        rounds.add(round);
        currentRound = round;
    }

    protected abstract TRound createRound();

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
        TRound currentRound = getCurrentRound();

        // Get the declarers of the bid
        ArrayList<Player> declarers = getPlayersByName(declarerNames);

        // Set the final bid
        currentRound.setFinalBid(bidType, declarers, wasFirstTry);
    }

     /**
     * A method for updating the scores
     * of the players after a round of playing.
     *
     * @param tricksPerPlayer The amount of tricks a given player has won.
     */
    public void updateScores(HashMap<IPlayer, Integer> tricksPerPlayer) {
        if (tricksPerPlayer.size() != NUMBER_OF_PLAYERS) {
            throw new IllegalArgumentException("Invalid amount of players.");
        }

        // Find the actual player objects corresponding to the names
        HashMap<Player, Integer> tricksWon = new HashMap<>();
        for (IPlayer iPlayer : tricksPerPlayer.keySet()) {
            tricksWon.put((Player) iPlayer, tricksPerPlayer.get(iPlayer));
        }

        // We calculate the score differences
        HashMap<Player, Integer> deltaPointsPerPlayer = getCurrentRound().processRoundOutcome(tricksWon);

        // Update the scores according to the deltas
        for (Player player : deltaPointsPerPlayer.keySet()) {
            player.updateScore(deltaPointsPerPlayer.get(player));
        }
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
    public ArrayList<IPlayer> getPlayers() {
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
        for (IPlayer player : getPlayers()) {
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
        for (IPlayer playerInterface : getPlayers()) {
            Player player = (Player) playerInterface;
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
    public HashMap<IPlayer, Integer> getScoresPerPlayer() {
        // Create a map for the scores
        HashMap<IPlayer, Integer> scoresPerPlayer = new HashMap<>();

        // Add the scores to the map
        for (IPlayer playerInterface : getPlayers()) {
            Player player = (Player) playerInterface;
            scoresPerPlayer.put(playerInterface, player.getScore());
        }

        // Return the map
        return scoresPerPlayer;
    }

    /**
     * Returns the current round.
     */
    protected TRound getCurrentRound() {
        return currentRound;
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
        for (IPlayer playerInterface : getPlayers()) {
            Player player = (Player) playerInterface;
            player.updateScore(-player.getScore() + score);
        }
    }
}
