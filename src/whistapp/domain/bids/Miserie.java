package whistapp.domain.bids;

import java.util.ArrayList;
import java.util.HashMap;

import whistapp.domain.players.Player;

/**
 * Represents a Miserie bid (0 cards)
 */
public class Miserie extends Bid {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public Miserie(ArrayList<Player> declarers) throws IllegalArgumentException {
        super(declarers);
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public Methods                               */
    /* -------------------------------------------------------------------------- */

    @Override
    public String toString() {
        return "Miserie";
    }

    @Override
    public HashMap<Player, Integer> calculatePoints(HashMap<Player, Integer> tricksWon) {
        // Note again that we don't check the arguments as this is the responsibility
        // of the information experts for the number of players (Game) and the number of tricks (Round).

        // Initialize the scores map to 0
        HashMap<Player, Integer> scores = new HashMap<>();
        for (Player player : tricksWon.keySet()) {
            scores.put(player, 0);
        }

        int baseValue = getBaseValue();

        // Accumulate points for EVERY player who bid Miserie. Each bid is handled individually.
        for (Player declarer : getBidders()) {
            boolean contractWon = isBidSuccessful(tricksWon, declarer);

            // Amount the declarer should win or lose
            int declareDelta = contractWon ? baseValue * 3 : -baseValue * 3;
            // Amount the opponents should win or lose
            int opponentDelta = contractWon ? -baseValue : baseValue;

            for (Player player : scores.keySet()) {
                int currentScore = scores.get(player);
                if (player.equals(declarer)) {
                    scores.put(player, currentScore + declareDelta);
                } else {
                    scores.put(player, currentScore + opponentDelta);
                }
            }
        }

        return scores;
    }

    /* -------------------------------------------------------------------------- */
    /*                             Protected methods                              */
    /* -------------------------------------------------------------------------- */

    /**
     * Checks if the declarer won the bid.
     *
     * @param tricksWon The number of tricks won per player.
     * @param player    The player to check the win condition for.
     * @return True if the player won, False otherwise.
     */
    protected boolean isBidSuccessful(HashMap<Player, Integer> tricksWon, Player player) {
        // In Miserie, the contract is successful if the declarer doesn't win any tricks.
        return tricksWon.get(player) == 0;
    }

    /**
     * Returns the base value for the miserie. Base value is 7 (so total is 21).
     * Open miserie will override this to provide 14 (total 42).
     *
     * @return The base value of the miserie bid.
     */
    protected int getBaseValue() {
        return 7;
    }

}
