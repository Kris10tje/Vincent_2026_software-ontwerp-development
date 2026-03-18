package whistapp.domain.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.bids.Abondance;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;
import whistapp.domain.players.BotDifficulty;
import whistapp.domain.players.BotPlayer;
import whistapp.domain.players.Player;
import whistapp.domain.round.PlayRound;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PlayGameTest {

    private PlayGame game;

    private String player1;
    private String player2;
    private String player3;
    private String player4;

    private LinkedHashMap<String, BotDifficulty> players;
    private ArrayList<String> declarers;

    @BeforeEach
    void setUp() {
        player1 = "Gary";
        player2 = "Jack";
        player3 = "Alice";
        player4 = "Frank";
        players = new LinkedHashMap<String, BotDifficulty>();
        players.put("Gary", null);
        players.put("Jack", null);
        players.put("Alice", BotDifficulty.HIGH);
        players.put("Frank", BotDifficulty.HIGH);
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
        assertFalse(game.getPlayers().get(0) instanceof BotPlayer);
        assertFalse(game.getPlayers().get(1) instanceof BotPlayer);
        assertInstanceOf(BotPlayer.class, game.getPlayers().get(2));
        assertInstanceOf(BotPlayer.class, game.getPlayers().get(3));
    }

    @Test
    void startNewRound() {
        PlayRound round = game.getCurrentRound();
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
        game.submitBid(BidType.ABONDANCE_9.toString(), Suit.SPADES);
        game.submitBid(BidType.ABONDANCE_10.toString(), Suit.HEARTS);
        game.submitBid(BidType.PASS.toString(), null);
        game.submitBid(BidType.PASS.toString(), null);
        assertTrue(game.evaluateRoundBids());
        game.startPlayingRound();
        assertEquals(0, game.getCurrentRoundCurrentTrickCards().size());
        String card = game.getPlayerCards(game.getActivePlayerName())[0];
        game.processCardPlay(card);
        assertEquals(1, game.getCurrentRoundCurrentTrickCards().size());
        assertTrue(game.getCurrentRoundCurrentTrickCards().containsValue(card));
    }

    @Test
    void registerBids_shouldThrow() {
        PlayGame game = new PlayGame();

        assertThrows(IllegalStateException.class,
                () -> game.registerBids(new HashMap<>()));
    }

    @Test
    void setReshuffledState_shouldThrow() {
        PlayGame game = new PlayGame();

        assertThrows(IllegalStateException.class,
                () -> game.setReshuffledState(true));
    }
}