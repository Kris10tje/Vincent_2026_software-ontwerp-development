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

class LowBotTest {

    private LowBot lowBot;
    private LowBot p2;

    private ArrayList<Player> players;

    private ArrayList<Card> cards;

    @BeforeEach
    void setUp() {
        lowBot = new LowBot("LowBot");
        p2 = new LowBot("p2");


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
        cards.add(new Card(Suit.HEARTS, Value.FIVE));
        cards.add(new Card(Suit.HEARTS, Value.TWO));

        lowBot.giveHand(cards);
        ArrayList<Card> cards2 = new ArrayList<>(cards);
        cards2.removeLast();
        cards2.add(new Card(Suit.CLUBS, Value.SEVEN));
        p2.giveHand(cards2);

        players = new ArrayList<>();
        players.add(lowBot);
        players.add(p2);
    }

    @Test
    void findAutonomousCard() {
        Trick trick = new Trick(lowBot);
        assertTrue(Arrays.asList(lowBot.getHandCards()).contains("Two of Hearts"));
        // It should find the lowest overall card
        String card = lowBot.findAutonomousCard(trick.getLeadSuit());
        assertEquals("Two of Hearts", card);
        assertEquals(13, lowBot.getHandCards().length);
        assertTrue(Arrays.asList(lowBot.getHandCards()).contains("Two of Hearts"));
    }

    @Test
    void playAutonomousCard() {
        Trick trick = new Trick(lowBot);
        String card = lowBot.findAutonomousCard(trick.getLeadSuit());
        assertEquals("Two of Hearts", card);

        trick.playCardFromCurrentPlayerHand(card, players);
        assertEquals(12, lowBot.getHandCards().length);
        assertFalse(Arrays.asList(lowBot.getHandCards()).contains("Two of Hearts"));
        assertEquals("Two of Hearts", trick.getCardsAsStrings().get(lowBot));

        // This isn't the four card because it has to follow the leading suit
        card = p2.findAutonomousCard(trick.getLeadSuit());
        assertEquals("Five of Hearts", card);
        trick.playCardFromCurrentPlayerHand(card, players);
        assertEquals(12, p2.getHandCards().length);
        assertFalse(Arrays.asList(p2.getHandCards()).contains("Five of Hearts"));
        assertEquals("Five of Hearts", trick.getCardsAsStrings().get(p2));
    }
}