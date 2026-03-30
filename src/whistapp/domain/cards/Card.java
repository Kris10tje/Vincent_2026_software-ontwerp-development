package whistapp.domain.cards;

import whistapp.domain.interfaces.ICard;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

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
     * A checker to see if a given ICard equals this card.
     * 
     * @param card The ICard to check.
     * @return     True if they are equal.
     *             False otherwise.
     */
    @Override
    public boolean isSameCard(ICard card) {
        if (card == null) return false;
        return this.getSuit() == card.getSuit() && this.getValue() == card.getValue();
    }
    
    @Override
    public String toString() {
        String valueStr = value.name().charAt(0) + value.name().substring(1).toLowerCase();
        String suitStr = suit.name().charAt(0) + suit.name().substring(1).toLowerCase();
        return valueStr + " of " + suitStr;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof ICard)) return false;
        ICard other = (ICard) obj;
        return suit == other.getSuit() && value == other.getValue();
    }

    @Override
    public int hashCode() {
        int result = suit != null ? suit.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    /**
     * A simple helper method that sorts a given list of cards.
     */
    public static void sortCards(List<? extends ICard> cards) {
        cards.sort((c1, c2) -> {
            if (c1.getSuit() == c2.getSuit()) {
                return c1.getValue().compareTo(c2.getValue());
            } else {
                return c1.getSuit().compareTo(c2.getSuit());
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

}
