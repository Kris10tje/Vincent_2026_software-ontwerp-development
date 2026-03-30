package whistapp.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.cards.Card;
import whistapp.domain.cards.Suit;
import whistapp.domain.cards.Value;
import whistapp.domain.players.Player;
import whistapp.domain.players.PlayerType;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TrickTest {

    private Trick trick;

    private Player player2;
    private Player player3;
    private Player player4;
    private Player player;

    private ArrayList<Player> players;

    private Card playCard;
    private Card playCard2;
    private Card playCard3;
    private Card playCard4;
    private Card playCard5;

    @BeforeEach
    void setUp() {
        player = new Player("p1", PlayerType.HUMAN);
        player2 = new Player("p2", PlayerType.HUMAN);
        player3 = new Player("p3", PlayerType.HUMAN);
        player4 = new Player("p4", PlayerType.HUMAN);
        players = new ArrayList();
        players.add(player);
        players.add(player2);
        players.add(player3);
        players.add(player4);
        trick = new Trick(player);
        playCard5 = new Card(Suit.HEARTS, Value.ACE);
        playCard = new Card(Suit.HEARTS, Value.KING);
        playCard2 = new Card(Suit.SPADES, Value.QUEEN);
        playCard3 = new Card(Suit.CLUBS, Value.TEN);
        playCard4 = new Card(Suit.DIAMONDS, Value.KING);
    }

    @Test
    void addCard() {
        trick.addCard(playCard, players);
        assertEquals(1, trick.getCardsAsStrings().size());
        assertTrue(trick.getCardsAsStrings().containsKey(player));
        trick.addCard(playCard2, players);
        assertEquals(2, trick.getCardsAsStrings().size());
        assertTrue(trick.getCardsAsStrings().containsKey(player2));
        trick.addCard(playCard3, players);
        assertEquals(3, trick.getCardsAsStrings().size());
        assertTrue(trick.getCardsAsStrings().containsKey(player3));
        trick.addCard(playCard4, players);
        assertEquals(4, trick.getCardsAsStrings().size());
        assertTrue(trick.getCardsAsStrings().containsKey(player4));
    }

    @Test
    void addCard_invalid() {
        trick.addCard(playCard, players);
        trick.addCard(playCard2, players);
        trick.addCard(playCard3, players);
        trick.addCard(playCard4, players);
        Card newCard = new Card(Suit.DIAMONDS, Value.KING);
        assertThrows(IllegalStateException.class, () -> trick.addCard(newCard, players));
    }

    @Test
    void determineWinner() {
        trick.addCard(playCard, players);
        trick.addCard(playCard2, players);
        trick.addCard(playCard3, players);
        trick.addCard(playCard4, players);
        assertEquals(player, trick.determineWinner(Suit.HEARTS));
        assertEquals(player4, trick.determineWinner(Suit.DIAMONDS));
        assertEquals(player2, trick.determineWinner(Suit.SPADES));
        assertEquals(player3, trick.determineWinner(Suit.CLUBS));
    }

    @Test
    void determineWinner2() {
        trick.addCard(playCard, players);
        trick.addCard(playCard5, players);
        trick.addCard(playCard3, players);
        trick.addCard(playCard4, players);
        assertEquals(player2, trick.determineWinner(Suit.HEARTS));
    }

    @Test
    void getCardsAsStrings() {
        trick.addCard(playCard, players);
        trick.addCard(playCard2, players);
        trick.addCard(playCard3, players);
        trick.addCard(playCard4, players);
        assertTrue(trick.getCardsAsStrings().containsKey(player));
        assertTrue(trick.getCardsAsStrings().containsKey(player2));
        assertTrue(trick.getCardsAsStrings().containsKey(player3));
        assertTrue(trick.getCardsAsStrings().containsKey(player4));
    }

    @Test
    void getLeadSuit() {
        trick.addCard(playCard, players);
        assertEquals(Suit.HEARTS, trick.getLeadSuit());
    }

    @Test
    void getLeadSuit2() {
        trick.addCard(playCard2, players);
        assertEquals(Suit.SPADES, trick.getLeadSuit());
    }
}