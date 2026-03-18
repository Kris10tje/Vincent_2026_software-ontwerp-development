package whistapp.domain.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        ArrayList<String> cards = newHand.getHandCards();
        assertEquals(cards.size(), this.cards.size());
        assertTrue(cards.containsAll(this.cards.stream().map(Card::toString).toList()));
    }

    @Test
    void testConstructor_invalid() {
        cards.add(new Card(Suit.HEARTS, Value.ACE));
        assertThrows(IllegalArgumentException.class, () -> new Hand(cards));
        assertThrows(IllegalArgumentException.class, () -> new Hand(null));
    }

    @Test
    void playCard() {
        assertTrue(hand.playCard("two of Spades").isSameCard("two of spades"));
        assertEquals(12, hand.getHandSize());
        assertTrue(hand.playCard("four of spades").isSameCard("four of spades"));
        assertEquals(11, hand.getHandSize());
        assertTrue(hand.playCard("five of spades").isSameCard("five of spades"));
        assertEquals(10, hand.getHandSize());
        assertTrue(hand.playCard("nine of spades").isSameCard("nine of spades"));
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
        assertTrue(hand.getHandCards().containsAll(this.cards.stream().map(Card::toString).toList()));
    }

    @Test
    void getAllowedHandCards() {
        assertTrue(hand.getAllowedHandCards(Suit.HEARTS).containsAll(this.cards.stream().map(Card::toString).toList()));
        assertTrue(hand.getAllowedHandCards(Suit.DIAMONDS).containsAll(this.cards.stream().map(Card::toString).toList()));
        assertTrue(hand.getAllowedHandCards(Suit.CLUBS).containsAll(this.cards.stream().map(Card::toString).toList()));
        assertTrue(hand.getAllowedHandCards(Suit.SPADES).containsAll(this.cards.stream().map(Card::toString).toList()));
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

        assertEquals(1, hand.getAllowedHandCards(Suit.HEARTS).size());
        assertTrue(hand.getAllowedHandCards(Suit.HEARTS).contains(card1.toString()));

        assertEquals(2, hand.getAllowedHandCards(Suit.SPADES).size());
        assertTrue(hand.getAllowedHandCards(Suit.SPADES).contains(card2.toString()));
        assertTrue(hand.getAllowedHandCards(Suit.SPADES).contains(card3.toString()));

        assertEquals(1, hand.getAllowedHandCards(Suit.CLUBS).size());
        assertTrue(hand.getAllowedHandCards(Suit.CLUBS).contains(card4.toString()));

        assertEquals(4, hand.getAllowedHandCards(Suit.DIAMONDS).size());
        assertTrue(hand.getAllowedHandCards(Suit.DIAMONDS).containsAll(cards.stream().map(Card::toString).toList()));
    }

    @Test
    void isEmpty() {
        Hand newHand = new Hand(new ArrayList<>());
        assertTrue(newHand.isEmpty());
        assertFalse(hand.isEmpty());
    }
}