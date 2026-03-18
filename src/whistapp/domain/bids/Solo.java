package whistapp.domain.bids;

import java.util.HashMap;

import whistapp.domain.Interfaces.IPlayer;

public class Solo extends Bid {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public Solo(IPlayer declarer) {
        super(createDeclarersFromPlayer(declarer));
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public Methods                               */
    /* -------------------------------------------------------------------------- */

    @Override
    public HashMap<IPlayer, Integer> calculatePoints(HashMap<IPlayer, Integer> tricksWon) {
        // Determine success
        boolean contractWon = isBidSuccessful(tricksWon);

        // Get the win/loss score points for the contract depending on the outcome
        int declarerScore = contractWon ? getWinScore() : -getWinScore();
        int nonDeclarerScore = contractWon ? getLossScore() : -getLossScore();

        return generateScores(tricksWon, declarerScore, nonDeclarerScore);
    }

    /* -------------------------------------------------------------------------- */
    /*                             Protected methods                              */
    /* -------------------------------------------------------------------------- */

    /**
     * Checks if the bid was successful.
     *
     * @param tricksWon The number of tricks won per player.
     * @return True if the declarer won all the tricks, False otherwise.
     */
    protected boolean isBidSuccessful(HashMap<IPlayer, Integer> tricksWon) {
        IPlayer declarer = getBidders().getFirst();

        for (IPlayer player : tricksWon.keySet()) {
            // If anyone EXCEPT the declarer won a trick, the Solo has failed.
            if (!player.equals(declarer) && tricksWon.get(player) > 0) {
                return false;
            }
        }

        // No one else won anything, so the declarer must have won everything.
        return true;
    }

    /**
     * Gets the win score for the bid.
     *
     * @return The win score for the bid.
     */
    protected int getWinScore() {
        return 75;
    }

    /**
     * Gets the loss score for the bid.
     *
     * @return The loss score for the bid.
     */
    protected int getLossScore() {
        return -25;
    }

    @Override
    public String toString() {
        return "Solo";
    }

}
