package whistapp.domain.bids;

import whistapp.domain.Interfaces.IPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Abstract class representing the current Round's bid (not a player's bid)
 */
public abstract class Bid {

    protected ArrayList<IPlayer> declarers;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * A constructor for an abstract bid. This performs the general checks.
     *
     * @param declarers A list of the declarers for this bid.
     *
     * @throws IllegalArgumentException Throws when the declarer list is empty.
     *                                  Throws when the declarer list has repeated declarers.
     */
    public Bid(ArrayList<IPlayer> declarers) throws IllegalArgumentException {

        // Bids cannot be empty.
        // Because the round orchestrates the bidding phase, it is physically impossible
        // to have a bid with more declarers than the number of players, so we don't check
        // against this.
        if (declarers.isEmpty()) {
            throw new IllegalArgumentException("Invalid amount of declarers.");
        }

        // Bids cannot have repeated declarers
        for (IPlayer p : declarers) {
            if (declarers.indexOf(p) != declarers.lastIndexOf(p))
                throw new IllegalArgumentException("Repeated declarers.");
        }

        // Set the declarers
        this.declarers = declarers;
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public methods                               */
    /* -------------------------------------------------------------------------- */

    /**
     * A method to calculate the points for a trick
     * when given the amount of tricks won per player,
     * and if these players bid or not.
     *
     * @param tricksWon The amount of tricks won per player.
     * @return The calculated points per player for this bid.
     * @throws IllegalArgumentException Throws when the given arrays aren't of a correct length.
     *                                  Throws when the sum of won tricks isn't equal to 13.
     * <p><b>Precondition:</b> The map must contain exactly the players currently active in the round:
     * {@code tricksWon.keySet().containsAll(activePlayers)}
     * <p><b>Precondition:</b> The sum of all tricks won must equal the total number of tricks played:
     * {@code sum(tricksWon.values()) == 13}
     * <p><b>Note:</b> Inconsistencies (like the map being size 4, matching the
     * number of players, or the sum of tricks won being 13) are not checked here; that is the
     * responsibility of the information experts for players (Game) and tricks (Round).
     */
    public abstract HashMap<IPlayer, Integer> calculatePoints(HashMap<IPlayer, Integer> tricksWon);

    /**
     * Simple checker for if a given player is a declarer of this bid
     * @param player the player to check for
     * @return {@code true} if that player is a declarer of this bid, {@code false} otherwise
     */
    public boolean isDeclarer(IPlayer player) {
        return this.declarers.contains(player);
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Getters                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * Simple getter to retrieve the declarers of this bid.
     * @return a list containing all players that declared this bid
     */
    public List<IPlayer> getBidders() {
        return this.declarers;
    }

    /**
     * A getter for finding the open miserie hands the given player can see.
     * This means the player's own hand will never be included in the output.
     *
     * @param currentPlayer The current player that needs to see the hands.
     * @return A map of players with their hands.
     * This map is empty if the bid isn't open miserie,
     * or if the other players haven't bid open miserie.
     */
    public HashMap<IPlayer, String[]> getOpenMiserieHands(IPlayer currentPlayer) {
        return new HashMap<>();
    }

    /* -------------------------------------------------------------------------- */
    /*                             Protected methods                              */
    /* -------------------------------------------------------------------------- */

    /**
     * A simple helper method that checks for
     * a certain amount of declarers in a given declarer array.
     *
     * @param declarers         The declarer list to be checked.
     * @param amountOfDeclarers The correct number of declarers it should be.
     * @return True if the number of declarers matches the given amount,
     * False otherwise.
     */
    protected boolean isRightAmountOfDeclarers(ArrayList<IPlayer> declarers, int amountOfDeclarers) {
        return declarers.size() == amountOfDeclarers;
    }

    /**
     * A helper method that creates an ArrayList from a single Player.
     * By only accepting a single Player in Abondance, ProposalAlone, and
     * other single declarer bids, we make sure that the declarer ArrayList
     * is always of size 1.
     *
     * @param declarer The Player to be added to the ArrayList.
     * @return An ArrayList containing the given Player.
     */
    protected static ArrayList<IPlayer> createDeclarersFromPlayer(IPlayer declarer) {
        return new ArrayList<>(asList(declarer));
    }

    /**
     * A helper method that generates the scores array
     * when given the positive and negative scores
     * for the bidders and the non-bidders (not respectively per se).
     *
     * @param tricksWon        The number of tricks won per player,
     *                         needed for building the scores per player.
     * @param declarerScore    The score for the players that declared this bid.
     * @param nonDeclarerScore The score for the players that didn't declare for this bid.
     * @return The final scores for each of the players.
     */
    protected HashMap<IPlayer, Integer> generateScores(HashMap<IPlayer, Integer> tricksWon,
                                                      int declarerScore, int nonDeclarerScore) {

        // Keep track of the scores per player
        HashMap<IPlayer, Integer> scores = new HashMap<>();

        // Calculate the scores
        for (IPlayer p : tricksWon.keySet()) {
            scores.put(p, isDeclarer(p) ? declarerScore : nonDeclarerScore);
        }

        return scores;
    }
}
