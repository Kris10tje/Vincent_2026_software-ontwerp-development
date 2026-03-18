package whistapp.domain.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of cards in the game of Whist.
 */
public class Deck {

    private final List<Card> cards;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public Deck() {
        this.cards = new ArrayList<>();
        fillDeck();
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Methods                                    */
    /* -------------------------------------------------------------------------- */

    /**
     * Fills the deck with all 52 cards.
     */
    private void fillDeck() {
        for (Suit suit : Suit.values()) {
            for (Value value : Value.values()) {
                cards.add(new Card(suit, value));
            }
        }
    }

    /**
     * Shuffles the deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Shuffles the deck,
     * returns the would be trump suit when dealing this deck.
     */
    public Suit shuffleGetTrump() {
        Collections.shuffle(cards);
        return cards.getLast().getSuit();
    }

    /**
     * A getter for the last card in the deck (which will always be dealt face up and determines the trump suit)
     */
    public Card getLastCard() {
        return cards.getLast();
    }

    /**
     * Deals the top card from the deck.
     *
     * @throws IllegalStateException if the deck is empty.
     */
    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Cannot deal from an empty deck.");
        }
        return cards.remove(0); // Removes and returns the top card
    }

    /**
     * Deals a hand of cards from the deck.
     *
     * @param numberOfCards The number of cards to deal.
     * @return A list of cards.
     * @throws IllegalArgumentException if there are not enough cards in the deck.
     */
    public ArrayList<Card> dealHand(int numberOfCards) {
        if (numberOfCards > cards.size()) {
            throw new IllegalArgumentException("Not enough cards left to deal this hand.");
        }
        ArrayList<Card> hand = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            hand.add(dealCard());
        }
        return hand;
    }

    /**
     * Returns the number of cards remaining in the deck.
     *
     * @return The number of cards remaining.
     */
    public int cardsRemaining() {
        return cards.size();
    }
}
