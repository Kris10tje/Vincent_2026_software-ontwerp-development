package whistapp.domain.game;

import whistapp.domain.players.Player;
import whistapp.domain.round.ScoreRound;
import whistapp.domain.Interfaces.*;
import whistapp.domain.bids.BidType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ScoreGame extends Game<IScoreRound> implements IScoreGame {

    /**
     * A no-arg constructor for the ScoreGame class.
     */
    public ScoreGame() {
        super();
    }

    /**
     * A constructor immediately incorporating the players into the game.
     *
     * @param players The players playing this game.
     * @throws IllegalArgumentException The players are not of correct size.
     */
    public ScoreGame(ArrayList<String> players) throws IllegalArgumentException {
        super();

        // No Bots allowed in a ScoreGame
        initializeHumanPlayers(players);
    }

    @Override
    protected IScoreRound createRound() {
        return new ScoreRound(getPlayers());
    }


    /* -------------------------------------------------------------------------- */
    /*                               Public methods                               */
    /* -------------------------------------------------------------------------- */

    /**
     * A method initializing given players for a game.
     *
     * @param players The human players to be added to the game.
     * @throws IllegalArgumentException The number of players provided isn't correct.
     * @throws IllegalStateException    The game already had specified players.
     * <p><b>Precondition:</b> The number of players provided is 4:
     * {@code players.size() == 4}
     */
    public void initializeHumanPlayers(ArrayList<String> players)
            throws IllegalArgumentException, IllegalStateException {

        // Check if the players can be initialized
        validatePlayerInitialization(players);

        // Initialize the players
        for (String player : players) {
            Player humanPlayer = new Player(player);
            this.players.add(humanPlayer);
        }

        // We initialize the player's scores
        setAllScores(0);

    }


    /**
     * Transforms a String-BidType map into a Player-BidType
     * ordered map and delegates to ScoreRound.
     *
     * @param bids The map of player names to bids.
     */
    public void registerBids(HashMap<String, String> bids) {
        LinkedHashMap<Player, BidType> playerBids = new LinkedHashMap<>();

        // Ensure we preserve the order of players by iterating over Game's players list
        for (Player player : getPlayers()) {
            BidType bid = BidType.fromString(bids.get(player.getName()));
            if (bid == null) {
                throw new IllegalArgumentException("Missing bid for player: " + player.getName());
            }
            playerBids.put(player, bid);
        }

        // Delegate to round
        getCurrentRound().registerBids(playerBids);
    }

    /* -------------------------------------------------------------------------- */
    /*                                   Getters                                  */
    /* -------------------------------------------------------------------------- */


    /* -------------------------------------------------------------------------- */
    /*                                   Setters                                  */
    /* -------------------------------------------------------------------------- */

    /**
     * Set whether the deck was reshuffled this round.
     *
     * @param reshuffled True if reshuffled, false otherwise.
     */
    public void setReshuffledState(boolean reshuffled) {
        getCurrentRound().setWasFirstTry(!reshuffled);
    }


}
