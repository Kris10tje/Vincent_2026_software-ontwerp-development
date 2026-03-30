package whistapp.domain.game;

import whistapp.domain.players.Player;
import whistapp.domain.players.PlayerType;
import whistapp.domain.round.ScoreRound;
import whistapp.domain.interfaces.*;
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
        return new ScoreRound(new ArrayList<>(players));
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

        LinkedHashMap<String, PlayerType> playersAndTypes = new LinkedHashMap<>();
        for (String player : players) {
            playersAndTypes.put(player, PlayerType.HUMAN);
        }
        initializePlayers(playersAndTypes);
    }


    /**
     * Transforms a String-BidType map into a Player-BidType
     * ordered map and delegates to ScoreRound.
     *
     * @param bids The map of player names to bids.
     */
    public void registerBids(HashMap<IPlayer, BidType> bids) {
        LinkedHashMap<Player, BidType> playerBids = new LinkedHashMap<>();

        // Ensure we preserve the order of players by iterating over Game's players list
        for (Player player : players) {
            BidType bid = bids.get(player);
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
