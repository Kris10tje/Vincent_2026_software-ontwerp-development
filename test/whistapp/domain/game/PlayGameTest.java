package whistapp.domain.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.interfaces.IPlayRound;
import whistapp.domain.players.PlayerType;
import whistapp.domain.players.Player;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PlayGameTest {

    private PlayGame game;

    private String player1;
    private String player2;
    private String player3;
    private String player4;

    private LinkedHashMap<String, PlayerType> players;
    private ArrayList<String> declarers;

    @BeforeEach
    void setUp() {
        player1 = "Gary";
        player2 = "Jack";
        player3 = "Alice";
        player4 = "Frank";
        players = new LinkedHashMap<String, PlayerType>();
        players.put("Gary", PlayerType.HUMAN);
        players.put("Jack", PlayerType.HUMAN);
        players.put("Alice", PlayerType.HIGH_BOT);
        players.put("Frank", PlayerType.HIGH_BOT);
        declarers = new ArrayList<>(List.of(player1));

        game = new PlayGame(players);
    }

    @Test
    void testConstructor() {
        // In the setup, a game was created, we test it.
        assertEquals(player1, game.getPlayers().get(0).getName());
        assertEquals(player2, game.getPlayers().get(1).getName());
        assertEquals(player3, game.getPlayers().get(2).getName());
        assertEquals(player4, game.getPlayers().get(3).getName());

        assertInstanceOf(Player.class, game.getPlayers().get(0));
        assertInstanceOf(Player.class, game.getPlayers().get(1));
        assertFalse(game.getPlayers().get(0).isAutonomous());
        assertFalse(game.getPlayers().get(1).isAutonomous());
        assertTrue(game.getPlayers().get(2).isAutonomous());
        assertTrue(game.getPlayers().get(3).isAutonomous());
    }

    @Test
    void startNewRound() {
        IPlayRound round = game.getCurrentRound();
        game.startNewRound();
        assertNotEquals(round, game.getCurrentRound());

    }

    @Test
    void getCurrentRound() {
        assertNull(game.getCurrentRound());
        game.startNewRound();
        assertNotNull(game.getCurrentRound());
    }

    @Test
    void getCurrentRoundCurrentTrickCards() {
        game.startNewRound();
        game.submitBid(BidType.ABONDANCE_9, Suit.SPADES);
        game.submitBid(BidType.ABONDANCE_10, Suit.HEARTS);
        game.submitBid(BidType.PASS, null);
        game.submitBid(BidType.PASS, null);
        assertTrue(game.evaluateRoundBids());
        game.startPlayingRound();
        assertEquals(0, game.getCurrentRoundCurrentTrickCards().size());
        ICard card = game.getCardsByPlayer(game.getActivePlayer()).get(0);
        game.processCardPlay(card);
        assertEquals(1, game.getCurrentRoundCurrentTrickCards().size());
        assertTrue(game.getCurrentRoundCurrentTrickCards().containsValue(card.toString()));
    }
}