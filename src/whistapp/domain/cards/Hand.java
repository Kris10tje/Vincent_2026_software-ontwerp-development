package whistapp.domain.cards;

import whistapp.domain.Interfaces.ICard;
import whistapp.domain.round.Round;

import java.util.ArrayList;

/**
 * Represents a hand of cards in the game of Whist.
 */
public class Hand {

    /**
     * The list of cards in this hand.
     */
    private ArrayList<ICard> cards = new ArrayList<>();

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * A constructor for a Hand of a player.
     *
     * @param cards The starting cards of this player.
     * @throws IllegalArgumentException The given cards are of invalid length.
     */
    public Hand(ArrayList<ICard> cards) throws IllegalArgumentException {
        if (cards == null || cards.size() > Round.NUMBER_OF_TRICKS) {
            // The number of cards can't be higher than the number of tricks
            throw new IllegalArgumentException("Invalid cards.");
        }
        this.cards = cards;
    }

    /* -------------------------------------------------------------------------- */
    /*                              Public methods                                */
    /* -------------------------------------------------------------------------- */

    /**
     * A method that plays a card from this hand.
     *
     * @param card The string card to be played.
     * @return The corresponding card in the hand.
     * @throws IllegalArgumentException The given card isn't found in this hand.
     */
    public ICard playCard(String card) throws IllegalArgumentException {
        ICard playedCard = getCardFromString(card);
        cards.remove(playedCard); // We also remove this card from this hand
        return playedCard;
    }

    /**
     * A simple checker to see if a certain played card
     * is valid for playing from this hand.
     */
    public boolean isValidCardForPlaying(String card, Suit currentSuit) {
        for (String s : getAllowedHandCards(currentSuit)) {
            if (card.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    /* -------------------------------------------------------------------------- */
    /*                                  Getters                                   */
    /* -------------------------------------------------------------------------- */

    /**
     * A simple getter finding the string values
     * of all the cards in this hand.
     */
    public ArrayList<String> getHandCards() {
        // First we sort the cards.
        Card.sortCards(cards);
        ArrayList<String> handCards = new ArrayList<>();
        for (ICard card : cards) {
            handCards.add(card.toString());
        }
        return handCards;
    }


    /**
     * A method to find all cards allowed to be played for a given trick.
     *
     * @param currentSuit The current suit of the trick.
     * @return A list of all the allowed cards.
     */
    private ArrayList<ICard> getAllowedCards(Suit currentSuit) {
        // If there is no current suit, any card can be played.
        if (currentSuit == null) {
            ArrayList<ICard> allowedCards = new ArrayList<>(cards);
            Card.sortCards(allowedCards);
            return allowedCards;
        }

        // First we check if we have any cards of the current suit.
        ArrayList<ICard> allowedCards = new ArrayList<>();
        if (hasSuit(currentSuit)) {
            // If it does have this suit, we can only play cards of that suit.
            for (ICard card : cards) {
                if (card.getSuit().equals(currentSuit)) {
                    allowedCards.add(card);
                }
            }
        } else {
            // If it doesn't have this suit, we can play any suit
            allowedCards = new ArrayList<>(cards);
        }
        Card.sortCards(allowedCards);
        return allowedCards;
    }

    /**
     * A helper method for finding the highest or lowest value card
     * in this hand for a given suit.
     * If the given suit isn't found in this hand or is null
     * the returned card is the overall highest or lowest card
     * in the entire hand.
     *
     * @param suit The suit of the card.
     * @return An empty string if there are no cards in this hand,
     * A string of the highest or lowest value card
     * if the suit is present.
     */
    public String getOuterCard(Suit suit, boolean highest) {
        if (isEmpty()) {
            return "";
        }

        // Get the list of all legally allowed cards for this turn
        ArrayList<ICard> allowedCards = getAllowedCards(suit);

        // We keep track of an outer card within the legal bounds
        ICard outerCard = allowedCards.getFirst();
        int outerValue = outerCard.getValue().getNumericValue();

        for (ICard card : allowedCards) {
            // This is the value of the current card
            int value = card.getValue().getNumericValue();

            // Since all cards in allowedCards are legally valid, we only check the value
            if (highest ? value > outerValue : value < outerValue) {
                // The value is higher or lower
                outerCard = card;
                outerValue = value;
            }
        }
        return outerCard.toString();
    }

    /**
     * A method for finding all the allowed hand cards for a given suit.
     *
     * @param currentSuit The suit that is currently on the table.
     * @return The list of all allowed cards.
     */
    public ArrayList<String> getAllowedHandCards(Suit currentSuit) {
        // We only return the allowed cards
        ArrayList<ICard> allowedCards = getAllowedCards(currentSuit);
        // We map all cards to their string variant.
        return new ArrayList<>(allowedCards.stream().map(ICard::toString).toList());
    }

    /**
     * A simple checker to see if this hand has a card of a given suit.
     */
    public boolean hasSuit(Suit suit) {
        if (suit == null) return false;
        for (ICard card : cards) {
            if (card.getSuit().equals(suit)) {
                return true;
            }
        }
        return false;
    }

    /**
     * A simple getter for the size of this hand.
     */
    public int getHandSize() {
        return cards.size();
    }

    /**
     * A getter finding the card corresponding
     * to a given string in this hand.
     *
     * @throws IllegalArgumentException The given card isn't found in this hand.
     */
    private ICard getCardFromString(String card) throws IllegalArgumentException {
        for (ICard c : cards) {
            if (c.isSameCard(card)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Card not found");
    }

    /**
     * A simple getter giving whether this hand is empty or not.
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }


}
