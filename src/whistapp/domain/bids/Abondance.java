package whistapp.domain.bids;

import whistapp.domain.players.Player;

import java.util.HashMap;

/**
 * Represents an Abondance bid (9, 10, 11, or 12 cards)
 */
public class Abondance extends Bid {

    private int numberOfCards; // 9, 10, 11, or 12
    private final boolean originalTrump;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * A constructor for the Abondance class.
     *
     * @param declarer      The declarer for this bid.
     * @param numberOfCards The number of cards this declarer will win.
     * @param originalTrump Whether this Abondance bid enforces the original trump.
     */
    public Abondance(Player declarer, int numberOfCards, boolean originalTrump) {

        // Super constructor
        super(createDeclarersFromPlayer(declarer));

        // Set the number of cards in the bid
        setNumberOfCards(numberOfCards);
        this.originalTrump = originalTrump;

    }

    /* -------------------------------------------------------------------------- */
    /*                               Public Methods                               */
    /* -------------------------------------------------------------------------- */

    @Override
    public HashMap<Player, Integer> calculatePoints(HashMap<Player, Integer> tricksWon) throws IllegalStateException {
        // Note again that we don't check the arguments as this is the responsibility
        // of the information experts for the number of players (Game) and the number of tricks (Round).

        // The different scores for each of the abondances
        int scoreValue = switch (numberOfCards) {
            case 9 -> 5;
            case 10 -> 6;
            case 11 -> 8;
            case 12 -> 9;
            default -> throw new IllegalStateException("Unexpected value: " + numberOfCards);
        };

        // Determine success
        boolean contractWon = isBidSuccessful(tricksWon, declarers.getFirst());

        // The total pot is scoreValue * 3. So declarer winspot, non-declarers lose scoreValue.
        int declarerScore = contractWon ? scoreValue * 3 : -scoreValue * 3;
        int nonDeclarerScore = contractWon ? -scoreValue : scoreValue;

        return generateScores(tricksWon, declarerScore, nonDeclarerScore);
    }

    @Override
    public String toString() {
        return "Abondance " + numberOfCards + (originalTrump ? " (original trump)" : "");
    }

    /* -------------------------------------------------------------------------- */
    /*                              Private methods                               */
    /* -------------------------------------------------------------------------- */

    /**
     * Simple validator that determines if a number of cards is a possible abondance type
     * @param numberOfCards the number of cards to test
     * @return {@code true} if abondance numberOfCards is possible, {@code false} otherwise.
     */
    private static boolean isValidNumberOfCards(int numberOfCards) {
        return numberOfCards >= 9 && numberOfCards <= 12;
    }

    /**
     * Checks if the declarer won the bid.
     *
     * @param tricksWon The number of tricks won per player.
     * @param player    The player to check the win condition for.
     * @return True if the player won, False otherwise.
     */
    protected boolean isBidSuccessful(HashMap<Player, Integer> tricksWon, Player player) {
        return tricksWon.get(player) >= numberOfCards;
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Setters                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * Sets the number of cards in this abondance bid
     * @param numberOfCards the number of tricks a player needs to win in order to win this bid
     * @throws IllegalArgumentException when abondance numberOfCards does not exist
     */
    private void setNumberOfCards(int numberOfCards) throws IllegalArgumentException {
        // Check the number of cards
        if (!isValidNumberOfCards(numberOfCards)) {
            throw new IllegalArgumentException("Number of cards must be between 9 and 12");
        }

        // Set the number of cards
        this.numberOfCards = numberOfCards;
    }

}
