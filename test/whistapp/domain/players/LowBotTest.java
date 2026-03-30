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
import whistapp.domain.players.strategy.LowBotStrategy;
import whistapp.domain.round.RoundContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LowBotTest {

    private Player lowBot;
    private Player p2;

    private ArrayList<Player> players;

    private ArrayList<Card> cards;

    @BeforeEach
    void setUp() {
        lowBot = new Player("LowBot", new LowBotStrategy());
        p2 = new Player("p2", new LowBotStrategy());


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
        assertTrue(lowBot.getHandCards().stream().anyMatch(c -> ((Card) c).isSameCard(new Card(Suit.HEARTS, Value.TWO).toString())));
        // It should play the highest overall card
        RoundContext mock = mock(RoundContext.class);
        when(mock.getTrumpSuit()).thenReturn(trick.getLeadSuit());
        ICard card = lowBot.chooseCard(mock);
        assertTrue(new Card(Suit.HEARTS, Value.TWO).isSameCard(card));
        assertEquals(13, lowBot.getHandCards().size());
        assertTrue(lowBot.getHandCards().stream().anyMatch(c -> ((Card) c).isSameCard(new Card(Suit.HEARTS, Value.TWO).toString())));
    }

    @Test
    void playAutonomousCard() {
        Trick trick = new Trick(lowBot);
        RoundContext mock = mock(RoundContext.class);
        when(mock.getCurrentTrickSuit()).thenReturn(trick.getLeadSuit());
        ICard card = lowBot.chooseCard(mock);
        assertTrue(new Card(Suit.HEARTS, Value.TWO).isSameCard(card));

        trick.playCardFromCurrentPlayerHand(card, players);
        
        assertEquals(12, lowBot.getHandCards().size());
        assertFalse(lowBot.getHandCards().stream().anyMatch(c -> ((Card) c).isSameCard(new Card(Suit.HEARTS, Value.TWO).toString())));
        assertTrue(((Card) trick.getCards().get(lowBot)).isSameCard(new Card(Suit.HEARTS, Value.TWO).toString()));

        when(mock.getCurrentTrickSuit()).thenReturn(trick.getLeadSuit());
        ICard card2 = p2.chooseCard(mock);
        assertTrue(new Card(Suit.HEARTS, Value.FIVE).isSameCard(card2));

        trick.playCardFromCurrentPlayerHand(card2, players);

        assertEquals(12, p2.getHandCards().size());
        assertFalse(p2.getHandCards().stream().anyMatch(c -> ((Card) c).isSameCard(new Card(Suit.HEARTS, Value.FIVE).toString())));
        assertTrue(((Card) trick.getCards().get(p2)).isSameCard(new Card(Suit.HEARTS, Value.FIVE).toString()));
    }
}