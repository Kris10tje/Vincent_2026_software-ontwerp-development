package whistapp.domain.bids;

import whistapp.domain.players.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a proposal to play with a partner in the game of Whist.
 */
public class ProposalWithPartner extends Proposal {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public ProposalWithPartner(ArrayList<Player> declarers) throws IllegalArgumentException {
        super(declarers);
        if (!isRightAmountOfDeclarers(declarers, 2))
            throw new IllegalArgumentException("Invalid amount of declarers.");
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public Methods                               */
    /* -------------------------------------------------------------------------- */

    @Override
    public HashMap<Player, Integer> calculatePoints(HashMap<Player, Integer> tricksWon)
            throws IllegalArgumentException {

        // Get the base score from the helper method in super
        int tricksWonByTeam = getTricksWonByTeam(tricksWon);
        int declarerScore = calculateBaseScore(tricksWonByTeam, 8);
        int nonDeclarerScore = -declarerScore;

        return generateScores(tricksWon, declarerScore, nonDeclarerScore);

    }

    @Override
    public String toString() {
        return "Proposal with Partner";
    }

}
