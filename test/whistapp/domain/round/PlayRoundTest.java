package whistapp.domain.round;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.Trick;
import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Card;
import whistapp.domain.cards.Suit;
import whistapp.domain.cards.Value;
import whistapp.domain.game.Game;
import whistapp.domain.players.Player;

import whistapp.domain.interfaces.ICard;
import whistapp.domain.players.PlayerType;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayRoundTest {

    private Player player2;
    private Player player3;
    private Player player4;
    private Player player;

    private ArrayList<Player> players;

    private PlayRound round;

    @BeforeEach
    void setUp() {
        player = new Player("Gary", PlayerType.HUMAN);
        player2 = new Player("Jack", PlayerType.HUMAN);
        player3 = new Player("Jacky", PlayerType.HUMAN);
        player4 = new Player("Alice", PlayerType.HIGH_BOT);
        players = new ArrayList();
        players.add(player);
        players.add(player2);
        players.add(player3);
        players.add(player4);

        round = new PlayRound(players);
    }

    @Test
    void testConstructor() {
        // A dealer should have been set.
        assertNotNull(round.getDealer());
        assertNotNull(round.getCurrentBiddingPlayer());
        assertEquals(Player.getNextPlayer(players, round.getDealer()), round.getCurrentBiddingPlayer());
        assertTrue(round.getPlayers().containsAll(players));
        assertEquals(13, round.getPlayers().get(0).getHandCards().size());
        assertEquals(13, round.getPlayers().get(1).getHandCards().size());
        assertEquals(13, round.getPlayers().get(2).getHandCards().size());
        assertEquals(13, round.getPlayers().get(3).getHandCards().size());

    }

    @Test
    void proceedAutonomousBid() {
        for (int i = 0; i < Game.getPlayerCount(); i++) {
            if (round.getCurrentBiddingPlayer().isAutonomous()) {
                round.proceedAutonomousBid();
            } else {
                round.submitBid(BidType.PASS, null);
            }
        }

        // The bot should also have passed
        assertFalse(round.evaluateRoundBids());
    }

    @Test
    void proceedAutonomousBid_invalid() {

        for (int i = 0; i < Game.getPlayerCount(); i++) {
            if (round.getCurrentBiddingPlayer().isAutonomous()) {
                assertDoesNotThrow(() -> round.proceedAutonomousBid());
            } else {
                assertThrows(IllegalStateException.class, () -> round.proceedAutonomousBid());
            }
        }
    }

    @Test
    void submitBid() {
        assertEquals(Player.getNextPlayer(players, round.getDealer()), round.getCurrentBiddingPlayer());
        round.submitBid(BidType.MISERIE, null);
        assertEquals(BidType.MISERIE, round.getHighestBid());
        assertEquals(Player.getNextPlayer(players, Player.getNextPlayer(players, round.getDealer())), round.getCurrentBiddingPlayer());
    }

    @Test
    void submitBid2() {
        assertEquals(Player.getNextPlayer(players, round.getDealer()), round.getCurrentBiddingPlayer());
        round.submitBid(BidType.ABONDANCE_9, Suit.DIAMONDS);
        BidType expectedBid = (round.getOriginalTrumpSuit() == Suit.DIAMONDS) ? BidType.ABONDANCE_9_ORIGINAL_TRUMP : BidType.ABONDANCE_9;
        assertEquals(expectedBid, round.getHighestBid());
        assertEquals(Player.getNextPlayer(players, Player.getNextPlayer(players, round.getDealer())), round.getCurrentBiddingPlayer());
    }

    @Test
    void submitBid3() {
        assertEquals(Player.getNextPlayer(players, round.getDealer()), round.getCurrentBiddingPlayer());
        round.submitBid(BidType.PASS, null);
        assertEquals(BidType.PASS, round.getHighestBid());
        assertEquals(Player.getNextPlayer(players, Player.getNextPlayer(players, round.getDealer())), round.getCurrentBiddingPlayer());
    }

    @Test
    void hasBeenProposed() {
        assertFalse(round.hasBeenProposed());
        round.submitBid(BidType.PROPOSAL, null);
        assertTrue(round.hasBeenProposed());
    }

    @Test
    void evaluateRoundBids() {
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        assertEquals(BidType.PASS, round.getHighestBid());
        assertFalse(round.evaluateRoundBids());
        assertNull(round.getFinalBid());
    }

    @Test
    void evaluateRoundBids2() {
        round.submitBid(BidType.ABONDANCE_9, Suit.SPADES);
        round.submitBid(BidType.SOLO, Suit.HEARTS);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        assertEquals(BidType.SOLO, round.getHighestBid());
        assertTrue(round.evaluateRoundBids());
        assertNotNull(round.getFinalBid());
        assertEquals(Player.getNextPlayer(players, Player.getNextPlayer(players, round.getDealer())),
                round.getFinalBid().getBidders().getFirst());

    }

    @Test
    void evaluateRoundBids3() {
        round.submitBid(BidType.PROPOSAL, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.ACCEPT, null);
        round.submitBid(BidType.PASS, null);
        assertEquals(BidType.ACCEPT, round.getHighestBid());
        assertTrue(round.evaluateRoundBids());
        assertNotNull(round.getFinalBid());
        Player p1 = Player.getNextPlayer(players, round.getDealer());
        Player p2 = Player.getNextPlayer(players, Player.getNextPlayer(players, p1));
        assertTrue(round.getFinalBid().isDeclarer(p1));
        assertTrue(round.getFinalBid().isDeclarer(p2));

    }

    @Test
    void evaluateRoundBids_invalid() {
        round.submitBid(BidType.PROPOSAL, null);
        round.submitBid(BidType.PASS, null);
        assertEquals(BidType.PROPOSAL, round.getHighestBid());
        assertThrows(IllegalStateException.class, () -> round.evaluateRoundBids());
    }

    @Test
    void restartRound() {
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);

        round.restartRound();
        assertFalse(round.wasFirstTry);
        assertNull(round.getHighestBid());
        assertEquals(Player.getNextPlayer(players, round.getDealer()), round.getCurrentBiddingPlayer());
    }

    @Test
    void restartRound_invalid() {
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);

        assertThrows(IllegalStateException.class, () -> round.restartRound());
    }

    @Test
    void restartRound_invalid2() {
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.MISERIE, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);

        assertThrows(IllegalStateException.class, () -> round.restartRound());
    }

    @Test
    void startPlayingRound() {
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.ABONDANCE_9, Suit.SPADES);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        assertTrue(round.evaluateRoundBids());
        round.startPlayingRound();
        assertEquals(Player.getNextPlayer(players, Player.getNextPlayer(players, round.getDealer())), round.getCurrentPlayingPlayer());
        assertDoesNotThrow(() -> round.getCardsInTrick());
    }

    @Test
    void startPlayingRound2() {
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.MISERIE, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        assertTrue(round.evaluateRoundBids());
        round.startPlayingRound();
        assertEquals(Player.getNextPlayer(players, round.getDealer()), round.getCurrentPlayingPlayer());
        assertDoesNotThrow(() -> round.getCardsInTrick());
    }

    @Test
    void startPlayingRound_invalid() {
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.MISERIE, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        // We haven't evaluated yet
        assertThrows(IllegalStateException.class, () -> round.startPlayingRound());
    }

    @Test
    void evaluateAndAdvanceTrick() {
        assertTrue(round.getTricksWon().containsValue(0));
        assertFalse(round.getTricksWon().containsValue(1));
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.ABONDANCE_9, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        round.evaluateRoundBids();
        round.startPlayingRound();
        playValidCard(round);
        playValidCard(round);
        playValidCard(round);
        playValidCard(round);
        assertFalse(round.evaluateAndAdvanceTrick());
        assertTrue(round.getTricksWon().containsValue(1));
    }

    @Test
    void evaluateAndAdvanceTrick_invalid() {
        assertTrue(round.getTricksWon().containsValue(0));
        assertFalse(round.getTricksWon().containsValue(1));
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.MISERIE, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        round.evaluateRoundBids();
        round.startPlayingRound();
        playValidCard(round);
        playValidCard(round);
        playValidCard(round);
        assertThrows(IllegalStateException.class, () -> round.evaluateAndAdvanceTrick());
    }

    @Test
    void getTrumpSuit() {
        assertNotNull(round.getTrumpSuit());
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.MISERIE, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        round.evaluateRoundBids();
        assertNull(round.getTrumpSuit());
    }

    @Test
    void getTrumpSuit2() {
        // For free-trump Abondance, trump is NOT set at bid time.
        // It is revealed when the Abondance declarer plays their first card.
        assertNotNull(round.getTrumpSuit()); // Original trump from deck
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.ABONDANCE_9, null); // No suit passed at bid time
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        round.evaluateRoundBids();
        // Active trump should now be null: Abondance declarer hasn't played yet
        assertNull(round.getTrumpSuit());
        // The original trump from the deck is unchanged
        assertNotNull(round.getOriginalTrumpSuit());
    }

    @Test
    void getTrumpSuit3() {
        // ABONDANCE_X_ORIGINAL_TRUMP keeps the original deck trump as the active trump.
        Suit originalTrump = round.getTrumpSuit();
        assertNotNull(originalTrump);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.ABONDANCE_9, null);                   // Free Abondance first
        round.submitBid(BidType.ABONDANCE_9_ORIGINAL_TRUMP, null);    // Superseded with original trump
        round.submitBid(BidType.PASS, null);
        round.evaluateRoundBids();
        // Active trump should still be the original trump
        assertEquals(originalTrump, round.getTrumpSuit());
    }

    @Test
    void getCurrentBiddingPlayer() {
        assertEquals(Player.getNextPlayer(players, round.getDealer()), round.getCurrentBiddingPlayer());
        Player bidder = round.getCurrentBiddingPlayer();
        round.submitBid(BidType.PASS, null);
        assertEquals(Player.getNextPlayer(players, bidder), round.getCurrentBiddingPlayer());
    }

    @Test
    void getCardsFromPreviousTrick() {
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.ABONDANCE_9, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        round.evaluateRoundBids();
        round.startPlayingRound();
        ICard c1 = playValidCard(round);
        ICard c2 = playValidCard(round);
        ICard c3 = playValidCard(round);
        ICard c4 = playValidCard(round);
        round.evaluateAndAdvanceTrick();
        assertTrue(round.getCardsFromPreviousTrick().containsValue(c1));
        assertTrue(round.getCardsFromPreviousTrick().containsValue(c2));
        assertTrue(round.getCardsFromPreviousTrick().containsValue(c3));
        assertTrue(round.getCardsFromPreviousTrick().containsValue(c4));
    }

    @Test
    void getCardsFromPreviousTrick_invalid() {
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.MISERIE, null);
        round.submitBid(BidType.PASS, null);
        round.submitBid(BidType.PASS, null);
        round.evaluateRoundBids();
        round.startPlayingRound();
        assertThrows(IllegalStateException.class, () -> round.getCardsFromPreviousTrick());
    }

    @Test
    void getOpenMiserieHands() {
        assertTrue(round.getOpenMiserieHands(player).isEmpty());
        round.submitBid(BidType.OPEN_MISERIE, null);
        assertEquals(Player.getNextPlayer(players, round.getDealer()), round.getOpenMiserieHands(round.getDealer()).keySet().iterator().next());
        round.submitBid(BidType.OPEN_MISERIE, null);
        assertEquals(2, (long) round.getOpenMiserieHands(round.getDealer()).size());
        round.submitBid(BidType.OPEN_MISERIE, null);
        assertEquals(3, (long) round.getOpenMiserieHands(round.getDealer()).size());
    }

    private ICard playValidCard(PlayRound r) {
        for (ICard card : r.getCurrentPlayingPlayer().getHandCards()) {
            try {
                r.processCardPlay(card);
                return card;
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }
        throw new IllegalStateException("No valid card found for player!");
    }
}