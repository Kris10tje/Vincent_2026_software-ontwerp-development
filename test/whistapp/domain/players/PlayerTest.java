package whistapp.domain.players;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.cards.Card;
import whistapp.domain.cards.Suit;
import whistapp.domain.cards.Value;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;
    private Player player2;
    private Player player3;

    private ArrayList<Card> cards;

    @BeforeEach
    void setUp() {
        player = new Player("Name");
        player2 = new LowBot("Name2");
        player3 = new HighBot("Name3");

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
        cards.add(new Card(Suit.HEARTS, Value.THREE));
        cards.add(new Card(Suit.SPADES, Value.TWO));
    }

    @Test
    void updateScore() {
        player.updateScore(20);
        assertEquals(20, player.getScore());
        player2.updateScore(20);
        assertEquals(20, player2.getScore());
        player3.updateScore(20);
        assertEquals(20, player3.getScore());
        player.updateScore(20);
        assertEquals(40, player.getScore());
        player2.updateScore(-20);
        assertEquals(0, player2.getScore());
        player3.updateScore(-40);
        assertEquals(-20, player3.getScore());
    }

    @Test
    void playCard_invalid() {
        assertThrows(IllegalStateException.class, () -> player.playCard("two of spades", Suit.SPADES));
        assertThrows(IllegalStateException.class, () -> player.playCard("five of hearts", Suit.SPADES));
    }

    @Test
    void playCard_invalid2() {
        player.giveHand(new ArrayList<>());
        assertThrows(IllegalStateException.class, () -> player.playCard("two of spades", Suit.SPADES));
    }

    @Test
    void playCard_invalid3() {
        player.giveHand(cards);
        assertThrows(IllegalArgumentException.class, () -> player.playCard("two of spades", Suit.HEARTS));
    }

    @Test
    void playCard_valid() {
        player.giveHand(cards);
        player.playCard("two of spades", Suit.DIAMONDS);
        assertEquals(12, player.getHandCards().length);
        player.playCard("King of spades", Suit.CLUBS);
        assertEquals(11, player.getHandCards().length);
    }

    @Test
    void giveHand_valid() {
        player.giveHand(cards);
        assertTrue(Arrays.asList(player.getHandCards()).containsAll(this.cards.stream().map(Card::toString).toList()));
    }

    @Test
    void giveHand_valid2() {
        player.giveHand(new ArrayList<>());
        player.giveHand(cards);
        assertTrue(Arrays.asList(player.getHandCards()).containsAll(this.cards.stream().map(Card::toString).toList()));
    }


}