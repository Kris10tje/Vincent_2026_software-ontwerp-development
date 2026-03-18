package whistapp.domain.cards;

import java.util.ArrayList;
import java.util.Comparator;

import whistapp.domain.Interfaces.ICard;

/**
 * Represents a card in the game of Whist.
 */
public class Card implements ICard {

    private Suit suit;
    private Value value;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public Card(Suit suit, Value value) {
        setSuit(suit);
        setValue(value);
    }

    /* -------------------------------------------------------------------------- */
    /*                              Public methods                                */
    /* -------------------------------------------------------------------------- */

    /**
     * A checker to see if a given card equals this card.
     *
     * @param card The card to check.
     * @return     True if they are equal.
     *             False otherwise.
     */
    public boolean isSameCard(String card) {
        return this.toString().equalsIgnoreCase(card);
    }

    /**
     * A simple helper method that sorts a given list of cards.
     */
    public static void sortCards(ArrayList<ICard> cards) {
        cards.sort(new Comparator<ICard>() {
            @Override
            public int compare(ICard c1, ICard c2) {
                if (c1.getSuit() == c2.getSuit()) {
                    return c1.getValue().compareTo(c2.getValue());
                } else {
                    return c1.getSuit().compareTo(c2.getSuit());
                }
            }
        });
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Getters                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * A simple getter for the suit of this card.
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * A simple getter for the value of this card.
     */
    public Value getValue() {
        return value;
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Setters                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * A simple setter for the suit of this card.
     */
    private void setSuit(Suit suit) {
        this.suit = suit;
    }

    /**
     * A simple setter for the value of this card.
     */
    private void setValue(Value value) {
        this.value = value;
    }

    /* -------------------------------------------------------------------------- */
    /*                                 toString                                   */
    /* -------------------------------------------------------------------------- */

    @Override
    public String toString() {
        return switch (value) {
            case ACE -> "Ace of " + suit;
            case KING -> "King of " + suit;
            case QUEEN -> "Queen of " + suit;
            case JACK -> "Jack of " + suit;
            default -> value + " of " + suit;
        };
    }

}
