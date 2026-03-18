package whistapp.domain.bids;

import whistapp.domain.Interfaces.IPlayer;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.abs;

/**
 * Represents a Proposal bid (8 tricks with a specified suit)
 */
public abstract class Proposal extends Bid {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public Proposal(ArrayList<IPlayer> declarers) throws IllegalArgumentException {
        super(declarers);
    }

    /* -------------------------------------------------------------------------- */
    /*                              Protected Methods                             */
    /* -------------------------------------------------------------------------- */

    /**
     * Calculates the COMBINED tricks won by the entire bidding team.
     * This naturally handles both 1-player (Solo) and 2-player (Accepted) proposals.
     *
     * @param tricksWon The number of tricks won by each player.
     * @return The number of tricks won by the declarers.
     */
    protected int getTricksWonByTeam(HashMap<IPlayer, Integer> tricksWon) {
        int tricksWonByTeam = 0;

        // Add up the tricks won by each bidder
        for (IPlayer bidder : getBidders()) {
            tricksWonByTeam += tricksWon.get(bidder);
        }

        return tricksWonByTeam;
    }

    /**
     * Calculates the base score value of the contract.
     * Subclasses will call this, and then distribute this score to the players.
     *
     * @param tricksWonByTeam      The number of tricks won by the declarers.
     * @param numberOfTricksNeeded The number of tricks needed to win the bid.
     * @return The base score value of the contract.
     */
    protected int calculateBaseScore(int tricksWonByTeam, int numberOfTricksNeeded) {

        // 2 base points + 1 point per over/undertrick
        int scoreValue = 2 + abs(tricksWonByTeam - numberOfTricksNeeded);

        // If the team failed the combined contract, negate the score
        if (!isTeamSuccessful(tricksWonByTeam, numberOfTricksNeeded)) {
            scoreValue = -scoreValue;
        }

        // Slam bonus: If the team won all 13 tricks
        if (tricksWonByTeam == 13) {
            scoreValue *= 2;
        }

        return scoreValue;
    }

    /**
     * Helper method to check if the declarers won the bid.
     *
     * @param tricksWonByTeam      The number of tricks won by the declarers.
     * @param numberOfTricksNeeded The number of tricks needed to win the bid.
     * @return True if the declarers won, False otherwise.
     */
    protected boolean isTeamSuccessful(int tricksWonByTeam, int numberOfTricksNeeded) {
        return tricksWonByTeam >= numberOfTricksNeeded;
    }

}
