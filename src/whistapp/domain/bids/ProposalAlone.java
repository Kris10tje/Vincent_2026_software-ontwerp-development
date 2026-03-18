package whistapp.domain.bids;

import whistapp.domain.players.Player;

import java.util.HashMap;

/**
 * Represents a proposal to play alone in the game of Whist.
 */
public class ProposalAlone extends Proposal {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public ProposalAlone(Player declarer) {
        super(Bid.createDeclarersFromPlayer(declarer));
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public Methods                               */
    /* -------------------------------------------------------------------------- */

    @Override
    public HashMap<Player, Integer> calculatePoints(HashMap<Player, Integer> tricksWon) {

        // Get the single base score value from the helper method in super
        int tricksWonByTeam = getTricksWonByTeam(tricksWon);
        int score = super.calculateBaseScore(tricksWonByTeam, 5);

        // Get number of players
        int numberOfPlayers = tricksWon.size();

        // One declarer gets (score * opponents), the opponents get (-score) each
        int declarerScore = score * (numberOfPlayers - 1);
        int nonDeclarerScore = -score;

        return generateScores(tricksWon, declarerScore, nonDeclarerScore);
    }

    @Override
    public String toString() {
        return "Proposal (alone)";
    }

}
