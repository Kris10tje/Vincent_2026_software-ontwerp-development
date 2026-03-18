package whistapp.domain.cards;

/**
 * Represents the suit of a card in the game of Whist.
 */
public enum Suit {
    HEARTS,
    CLUBS,
    DIAMONDS,
    SPADES;

    @Override
    public String toString() {
        String name = name().toLowerCase();
        String first = String.valueOf(name.charAt(0)).toUpperCase();
        return first + name.substring(1);
    }
}
