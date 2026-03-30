package whistapp.domain.round;

import whistapp.domain.interfaces.IScoreRound;
import whistapp.domain.bids.BidType;
import whistapp.domain.players.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ScoreRound extends Round implements IScoreRound {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public ScoreRound(ArrayList<Player> players) {
        super(players);
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public Methods                               */
    /* -------------------------------------------------------------------------- */

    /**
     * Registers the final bids of the players for this ScoreRound and determines the final bid.
     * High Cohesion GRASP: This round is responsible for evaluating bids.
     *
     * @param bids A map of player to their respective final bid.
     * @throws IllegalArgumentException If everyone passed.
     */
    public void registerBids(LinkedHashMap<Player, BidType> bids) throws IllegalArgumentException {
        // Find the highest bid
        BidType highestBid = getHighestBid(bids);

        if (highestBid == BidType.PASS || highestBid == null) {
            throw new IllegalArgumentException("There should be at least one bid different from pass.");
        }

        // Determine declarers
        ArrayList<Player> declarers = determineBidDeclarers(bids, highestBid);

        // Set the final bid
        setFinalBid(highestBid, declarers, true);
    }

}
