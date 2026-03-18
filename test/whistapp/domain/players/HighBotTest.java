package whistapp.domain.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.Trick;
import whistapp.domain.cards.Card;
import whistapp.domain.cards.Suit;
import whistapp.domain.cards.Value;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class HighBotTest {

    private HighBot highBot;
    private HighBot p2;

    private ArrayList<Player> players;

    private ArrayList<Card> cards;

    @BeforeEach
    void setUp() {
        highBot = new HighBot("high");
        p2 = new HighBot("p2");

        cards = new ArrayList<>();
        cards.add(new Card(Suit.SPADES, Value.ACE));
        cards.add(new Card(Suit.SPADES, Value.KING));
        cards.add(new Card(Suit.HEARTS, Value.QUEEN));
        cards.add(new Card(Suit.DIAMONDS, Value.QUEEN));
        cards.add(new Card(Suit.SPADES, Value.TEN));
        cards.add(new Card(Suit.SPADES, Value.NINE));
        cards.add(new Card(Suit.CLUBS, Value.SEVEN));
        cards.add(new Card(Suit.SPADES, Value.SEVEN));
        cards.add(new Card(Suit.CLUBS, Value.SIX));
        cards.add(new Card(Suit.CLUBS, Value.QUEEN));
        cards.add(new Card(Suit.DIAMONDS, Value.FOUR));
        cards.add(new Card(Suit.HEARTS, Value.QUEEN));
        cards.add(new Card(Suit.HEARTS, Value.TWO));

        highBot.giveHand(cards);
        p2.giveHand(new ArrayList<>(cards));

        players = new ArrayList<>();
        players.add(highBot);
        players.add(p2);
    }

    @Test
    void findAutonomousCard() {
        Trick trick = new Trick(highBot);
        assertTrue(Arrays.asList(highBot.getHandCards()).contains("Ace of Spades"));
        // It should play the highest overall card
        String card = highBot.findAutonomousCard(trick.getLeadSuit());
        assertEquals("Ace of Spades", card);
        assertEquals(13, highBot.getHandCards().length);
        assertTrue(Arrays.asList(highBot.getHandCards()).contains("Ace of Spades"));
    }

    @Test
    void playAutonomousCard() {
        Trick trick = new Trick(highBot);
        String card = highBot.findAutonomousCard(trick.getLeadSuit());
        assertEquals("Ace of Spades", card);
        trick.playCardFromCurrentPlayerHand(card, players);
        assertEquals(12, highBot.getHandCards().length);
        assertFalse(Arrays.asList(highBot.getHandCards()).contains("Ace of Spades"));
        assertEquals("Ace of Spades", trick.getCardsAsStrings().get(highBot));

        card = p2.findAutonomousCard(trick.getLeadSuit());
        assertEquals("Ace of Spades", card);
        trick.playCardFromCurrentPlayerHand(card, players);
        assertEquals(12, p2.getHandCards().length);
        assertFalse(Arrays.asList(p2.getHandCards()).contains("Ace of Spades"));
        assertEquals("Ace of Spades", trick.getCardsAsStrings().get(p2));
    }

}