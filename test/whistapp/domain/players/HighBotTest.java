package whistapp.domain.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.Trick;
import whistapp.domain.cards.Card;
import whistapp.domain.cards.Suit;
import whistapp.domain.cards.Value;

import java.util.ArrayList;
import java.util.Arrays;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.players.strategy.HighBotStrategy;
import whistapp.domain.round.RoundContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HighBotTest {

    private Player highBot;
    private Player p2;

    private ArrayList<Player> players;

    private ArrayList<Card> cards;

    @BeforeEach
    void setUp() {
        highBot = new Player("high", new HighBotStrategy());
        p2 = new Player("p2", new HighBotStrategy());

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
        assertTrue(highBot.getHandCards().stream().anyMatch(c -> c.isSameCard(new Card(Suit.SPADES, Value.ACE))));
        // It should play the highest overall card
        RoundContext mock = mock(RoundContext.class);
        when(mock.getTrumpSuit()).thenReturn(trick.getLeadSuit());
        ICard card = highBot.chooseCard(mock);
        assertTrue(new Card(Suit.SPADES, Value.ACE).isSameCard(card));

        assertEquals(13, highBot.getHandCards().size());
        assertTrue(highBot.getHandCards().stream().anyMatch(c -> c.isSameCard(new Card(Suit.SPADES, Value.ACE))));
    }

    @Test
    void playAutonomousCard() {
        Trick trick = new Trick(highBot);
        RoundContext mock = mock(RoundContext.class);
        when(mock.getCurrentTrickSuit()).thenReturn(trick.getLeadSuit());
        ICard card = highBot.chooseCard(mock);
        assertTrue(new Card(Suit.SPADES, Value.ACE).isSameCard(card));

        trick.playCardFromCurrentPlayerHand(card, players);
        
        assertEquals(12, highBot.getHandCards().size());
        assertFalse(highBot.getHandCards().stream().anyMatch(c -> c.isSameCard(new Card(Suit.SPADES, Value.ACE))));
        assertTrue(((Card) trick.getCards().get(highBot)).isSameCard(new Card(Suit.SPADES, Value.ACE)));

        when(mock.getCurrentTrickSuit()).thenReturn(trick.getLeadSuit());
        ICard card2 = p2.chooseCard(mock);
        assertTrue(new Card(Suit.SPADES, Value.ACE).isSameCard(card));
        trick.playCardFromCurrentPlayerHand(card2, players);

        assertEquals(12, p2.getHandCards().size());
        assertFalse(p2.getHandCards().stream().anyMatch(c -> c.isSameCard(new Card(Suit.SPADES, Value.ACE))));
        assertTrue(((Card) trick.getCards().get(p2)).isSameCard(new Card(Suit.SPADES, Value.ACE)));
    }

}