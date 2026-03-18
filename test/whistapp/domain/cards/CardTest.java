package whistapp.domain.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    private Card card;
    private Card card2;
    private Card card3;
    private Card card4;
    private Card card5;


    @BeforeEach
    void setUp() {
        card = new Card(Suit.SPADES, Value.ACE);
        card2 = new Card(Suit.HEARTS, Value.KING);
        card3 = new Card(Suit.SPADES, Value.QUEEN);
        card4 = new Card(Suit.CLUBS, Value.JACK);
        card5 = new Card(Suit.DIAMONDS, Value.EIGHT);
    }

    @Test
    void isSameCard() {
        assertTrue(card.isSameCard("Ace of spades"));
        assertTrue(card2.isSameCard("King oF HeartS"));
        assertTrue(card3.isSameCard("QueeN of Spades"));
        assertTrue(card4.isSameCard("jack of clubs"));
        assertTrue(card5.isSameCard("eiGht of diamondS"));
    }

    @Test
    void testToString() {
        assertEquals("Ace of Spades", card.toString());
        assertEquals("King of Hearts", card2.toString());
        assertEquals("Queen of Spades", card3.toString());
        assertEquals("Jack of Clubs", card4.toString());
        assertEquals("Eight of Diamonds", card5.toString());
    }

    @Test
    void testSortCards() {
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(card);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        cards.add(card5);
        Card.sortCards(cards);
        // Suit order: H, C, D, S
        assertEquals(card2, cards.get(0));
        assertEquals(card4, cards.get(1));
        assertEquals(card5, cards.get(2));
        assertEquals(card, cards.get(3));
        assertEquals(card3, cards.get(4));

    }
}