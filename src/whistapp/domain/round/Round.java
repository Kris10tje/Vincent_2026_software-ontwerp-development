package whistapp.domain.round;

import whistapp.domain.bids.OpenMiserie;
import whistapp.domain.interfaces.*;
import whistapp.domain.bids.Bid;
import whistapp.domain.bids.BidType;
import whistapp.domain.game.Game;
import whistapp.domain.players.Player;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class Round implements IRound {

    protected Bid finalBid = null;
    protected LinkedHashMap<Player, Integer> tricksWon;
    protected boolean wasFirstTry = true;

    public static final int NUMBER_OF_TRICKS = 13;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * A constructor for the abstract Round class instantiating the players.
     *
     * @param players The players playing.
     */
    public Round(ArrayList<Player> players) {
        tricksWon = new LinkedHashMap<>();
        // The game has already processed these players
        // The players should be in a correct state
        // So we don't check their legitimacy anymore
        for (Player player : players) {
            tricksWon.put(player, 0);
        }
    }

    /* -------------------------------------------------------------------------- */
    /*                              Public methods                                */
    /* -------------------------------------------------------------------------- */

    /**
     * A method for processing the outcome of the round
     * according to the number of tricks won per player.
     *
     * @throws IllegalArgumentException If the sum of number of tricks won isn't 13.
     * <p><b>Precondition:</b> The sum of the number of tricks won
     * is equal to the NUMBER_OF_TRICKS:
     * {@code tricksWon.values().stream().mapToInt(Integer::intValue).sum() != NUMBER_OF_TRICKS}
     * <p><b>Precondition:</b> Each player has a valid number of tricks won:
     * {@code for (Player player : tricksWon.keySet())}
     * {@code   tricksWon.get(player) >= 0 && tricksWon.get(player) <= NUMBER_OF_TRICKS}
     */
    public HashMap<Player, Integer> processRoundOutcome(HashMap<Player, Integer> tricksWon)
            throws IllegalArgumentException {
        int count = 0;
        for (Player player : getPlayers()) {
            int numberOfTricksWon = tricksWon.get(player);
            if (numberOfTricksWon < 0 || numberOfTricksWon > NUMBER_OF_TRICKS) {
                throw new IllegalArgumentException("Invalid tricks won.");
            }
            count += numberOfTricksWon;
        }
        if (count == 0) {
            // We don't check if the total sums to the number of tricks because of early ending of rounds
            throw new IllegalArgumentException("Invalid tricks won.");
        }
        HashMap<Player, Integer> scores = getFinalBid().calculatePoints(tricksWon);

        // We should also multiply by two if the deck had to be reshuffled when finding a bid
        if (!wasFirstTry) {
            for (Player player : scores.keySet()) {
                scores.put(player, scores.get(player) * 2);
            }
        }
        return scores;
    }

    /**
     * A method for verifying the validity of an array containing the number of bids won per player.
     *
     * @param nbOfTricksWonPerPlayer An array containing the number of tricks won per player.
     * @return {@code true} if the length matches the amount of players, and the total number of tricks won
     * matches the total number of tricks, otherwise {@code false}
     */
    public static boolean nbOfTricksWonPerPlayerValid(int[] nbOfTricksWonPerPlayer) {

        if (nbOfTricksWonPerPlayer.length < Game.getPlayerCount()) {

            // If the length of the array does not match the number of players, invalid.
            return false;

        } else if (Arrays.stream(nbOfTricksWonPerPlayer).sum() != getTrickCountPerRound()) {

            // If the total number of tricks won doesn't sum to the total number of tricks per round, invalid.
            return false;

        } else {

            // Check if any player won a negative number of tricks.
            for (int i = 0; i < nbOfTricksWonPerPlayer.length; i++) {
                if (nbOfTricksWonPerPlayer[i] < 0) {
                    return false;
                }
            }

            // Otherwise, valid.
            return true;

        }

    }

    /* -------------------------------------------------------------------------- */
    /*                            Protected methods                               */
    /* -------------------------------------------------------------------------- */

    /**
     * Determines all players who are part of the team declaring the given final bid.
     * High Cohesion GRASP: Abstracted so both PlayRound and ScoreRound can use it.
     *
     * @param bids     The map of bids each player made.
     * @param finalBid The highest bid that was made.
     * @return A list of the players declaring this bid.
     */
    protected static ArrayList<Player> determineBidDeclarers(HashMap<Player, BidType> bids, BidType finalBid) {
        ArrayList<Player> declarers = new ArrayList<>();

        // First: a special case if the type was ACCEPT
        if (finalBid == BidType.ACCEPT) {
            // We must find the original PROPOSAL player to form the duo team
            for (Player player : bids.keySet()) {
                if (bids.get(player) == BidType.PROPOSAL) {
                    declarers.add(player);
                }
            }
        }

        // We now add all the players that natively bid the final highest bid
        for (Player player : bids.keySet()) {
            if (bids.get(player) == finalBid) {
                declarers.add(player);
            }
        }

        return declarers;
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Getters                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * Returns the final bid.
     */
    public Bid getFinalBid() {
        return this.finalBid;
    }

    /**
     * Returns the players playing in this round.
     */
    protected ArrayList<Player> getPlayers() {
        return new ArrayList<>(tricksWon.keySet());
    }

    /**
     * Returns a map containing the number of tricks won per player.
     */
    public LinkedHashMap<Player, Integer> getTricksWon() {
        return new LinkedHashMap<>(tricksWon);
    }

    /**
     * Returns the number of tricks in the round.
     */
    public static int getTrickCountPerRound() {
        return NUMBER_OF_TRICKS;
    }

    /**
     * Finds the highest bid from a map of bids.
     * High Cohesion GRASP: Abstracted so both PlayRound and ScoreRound can use it.
     *
     * @param bids The map of bids each player made.
     * @return The highest BidType.
     */
    protected static BidType getHighestBid(HashMap<Player, BidType> bids) {
        BidType highestBid = null;
        for (BidType bid : bids.values()) {
            if (highestBid == null || bid.isHigherBidThan(highestBid)) {
                highestBid = bid;
            }
        }
        return highestBid;
    }


    /* -------------------------------------------------------------------------- */
    /*                                 Setters                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * Sets the final bid.
     *
     * @param bid The bid to set.
     */
    protected void setFinalBid(Bid bid) {
        this.finalBid = bid;
    }

    /**
     * A method for setting the final bid in this round.
     *
     * @param bidType     The bid type of the chosen bid.
     * @param declarers   The names of the players declaring this bid.
     * @param wasFirstTry True, if the bid was chosen in the first try
     *                    False, otherwise.
     * @throws IllegalArgumentException The bidType is not valid.
     * <p><b>Precondition:</b> bidType is of a valid type:
     * {@code bidType.isValidBid()}
     */
    public void setFinalBid(BidType bidType, ArrayList<Player> declarers, boolean wasFirstTry)
            throws IllegalArgumentException {
        if (!bidType.isValidFinalRoundBid())
            throw new IllegalArgumentException("Bid type is pass.");
        this.finalBid = bidType.createBidFromBidType(declarers);
        this.wasFirstTry = wasFirstTry;
    }

    /**
     * Set whether this round was played on the first try.
     */
    public void setWasFirstTry(boolean wasFirstTry) {
        this.wasFirstTry = wasFirstTry;
    }

}
