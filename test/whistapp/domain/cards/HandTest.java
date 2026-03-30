package whistapp.domain.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.cards.Card;
import whistapp.domain.cards.Suit;
import whistapp.domain.cards.Value;

import whistapp.domain.interfaces.ICard;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    private ArrayList<Card> cards;

    private Hand hand;

    @BeforeEach
    void setUp() {
        cards = new ArrayList<>();
        cards.add(new Card(Suit.SPADES, Value.ACE));
        cards.add(new Card(Suit.SPADES, Value.KING));
        cards.add(new Card(Suit.SPADES, Value.QUEEN));
        cards.add(new Card(Suit.SPADES, Value.JACK));
        cards.add(new Card(Suit.SPADES, Value.TEN));
        cards.add(new Card(Suit.SPADES, Value.NINE));
        cards.add(new Card(Suit.SPADES, Value.EIGHT));
        cards.add(new Card(Suit.SPADES, Value.SEVEN));
        cards.add(new Card(Suit.SPADES, Value.SIX));
        cards.add(new Card(Suit.SPADES, Value.FIVE));
        cards.add(new Card(Suit.SPADES, Value.FOUR));
        cards.add(new Card(Suit.SPADES, Value.THREE));
        cards.add(new Card(Suit.SPADES, Value.TWO));

        hand = new Hand(cards);

    }

    @Test
    void testConstructor_valid() {
        Hand newHand = new Hand(cards);
        ArrayList<ICard> hCards = newHand.getHandCards();
        assertEquals(hCards.size(), this.cards.size());
        assertTrue(hCards.containsAll(this.cards));
    }

    @Test
    void testConstructor_invalid() {
        cards.add(new Card(Suit.HEARTS, Value.ACE));
        assertThrows(IllegalArgumentException.class, () -> new Hand(cards));
        assertThrows(IllegalArgumentException.class, () -> new Hand(null));
    }

    @Test
    void playCard() {
        assertTrue(hand.playCard(new Card(Suit.SPADES, Value.TWO))
                .isSameCard(new Card(Suit.SPADES, Value.TWO)));
        assertEquals(12, hand.getHandSize());
        assertTrue(hand.playCard(new Card(Suit.SPADES, Value.FOUR))
                .isSameCard(new Card(Suit.SPADES, Value.FOUR)));
        assertEquals(11, hand.getHandSize());
        assertTrue(hand.playCard(new Card(Suit.SPADES, Value.FIVE))
                .isSameCard(new Card(Suit.SPADES, Value.FIVE)));
        assertEquals(10, hand.getHandSize());
        assertTrue(hand.playCard(new Card(Suit.SPADES, Value.NINE))
                .isSameCard(new Card(Suit.SPADES, Value.NINE)));
        assertEquals(9, hand.getHandSize());
    }

    @Test
    void playCard_invalid() {
        assertThrows(IllegalArgumentException.class, () -> new Hand(null));
        cards.add(new Card(Suit.HEARTS, Value.ACE));
        assertThrows(IllegalArgumentException.class, () -> new Hand(cards));
    }

    @Test
    void getHandCards() {
        assertTrue(hand.getHandCards().containsAll(this.cards));
    }

    @Test
    void getAllowedHandCards() {
        assertTrue(hand.getAllowedHandCardsAsCards(Suit.HEARTS).containsAll(this.cards));
        assertTrue(hand.getAllowedHandCardsAsCards(Suit.DIAMONDS).containsAll(this.cards));
        assertTrue(hand.getAllowedHandCardsAsCards(Suit.CLUBS).containsAll(this.cards));
        assertTrue(hand.getAllowedHandCardsAsCards(Suit.SPADES).containsAll(this.cards));
    }

    @Test
    void getAllowedHandCards2() {
        Card card1 = new Card(Suit.HEARTS, Value.ACE);
        Card card2 = new Card(Suit.SPADES, Value.JACK);
        Card card3 = new Card(Suit.SPADES, Value.TEN);
        Card card4 = new Card(Suit.CLUBS, Value.EIGHT);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        Hand hand = new Hand(cards);

        assertEquals(1, hand.getAllowedHandCardsAsCards(Suit.HEARTS).size());
        assertTrue(hand.getAllowedHandCardsAsCards(Suit.HEARTS).contains(card1));

        assertEquals(2, hand.getAllowedHandCardsAsCards(Suit.SPADES).size());
        assertTrue(hand.getAllowedHandCardsAsCards(Suit.SPADES).contains(card2));
        assertTrue(hand.getAllowedHandCardsAsCards(Suit.SPADES).contains(card3));

        assertEquals(1, hand.getAllowedHandCardsAsCards(Suit.CLUBS).size());
        assertTrue(hand.getAllowedHandCardsAsCards(Suit.CLUBS).contains(card4));

        assertEquals(4, hand.getAllowedHandCardsAsCards(Suit.DIAMONDS).size());
        assertTrue(hand.getAllowedHandCardsAsCards(Suit.DIAMONDS).containsAll(cards));
    }

    @Test
    void isEmpty() {
        Hand newHand = new Hand(new ArrayList<>());
        assertTrue(newHand.isEmpty());
        assertFalse(hand.isEmpty());
    }
}