package whistapp.domain.round;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.bids.Bid;
import whistapp.domain.bids.BidType;
import whistapp.domain.bids.ProposalAlone;
import whistapp.domain.bids.Solo;
import whistapp.domain.players.Player;
import whistapp.domain.players.PlayerType;
import whistapp.domain.players.strategy.HumanStrategy;
import whistapp.domain.players.strategy.LowBotStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoundTest {

    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;

    private ArrayList<Player> players;
    private ArrayList<Player> declarers;

    private Round round;

    @BeforeEach
    void setUp() {
        player1 = new Player("Gary", new HumanStrategy(null));
        player2 = new Player("Jack", new LowBotStrategy());
        player3 = new Player("Alice", new LowBotStrategy());
        player4 = new Player("Frank", new LowBotStrategy());
        players = new ArrayList<>(Arrays.asList(player1, player2, player3, player4));
        round = new ScoreRound(players);
        declarers = new ArrayList<>(List.of(player3));
    }

    @Test
    void testConstructorInitializesPlayers() {
        assertNotNull(round.tricksWon);
        assertEquals(4, round.tricksWon.size());
        assertEquals(0, round.tricksWon.get(player1));
        assertEquals(0, round.tricksWon.get(player2));
        assertEquals(0, round.tricksWon.get(player3));
        assertEquals(0, round.tricksWon.get(player4));
    }

    @Test
    void setFinalBid_valid() {
        BidType bidType = BidType.PROPOSAL;

        round.setFinalBid(bidType, declarers, true);

        assertInstanceOf(ProposalAlone.class, round.getFinalBid());
        assertTrue(round.wasFirstTry);

    }

    @Test
    void setFinalBid_invalid() {
        BidType bidType = BidType.PASS;

        assertThrows(IllegalArgumentException.class, () -> round.setFinalBid(bidType, declarers, true));
        assertNull(round.getFinalBid());
        assertTrue(round.wasFirstTry);

    }

    @Test
    void setFinalBid_protected() {
        Bid bid = new Solo(declarers.getFirst());

        round.setFinalBid(bid);
        assertInstanceOf(Solo.class, round.getFinalBid());

    }

    @Test
    void processRoundOutcome_invalid() {
        HashMap<Player, Integer> tricksWon = new HashMap<>();
        tricksWon.put(player1, 0);
        tricksWon.put(player2, 0);
        tricksWon.put(player3, 0);
        tricksWon.put(player4, 0);
        assertThrows(IllegalArgumentException.class, () -> round.processRoundOutcome(tricksWon));
    }

    @Test
    void processRoundOutcome_invalid2() {
        HashMap<Player, Integer> tricksWon = new HashMap<>();
        tricksWon.put(player1, -5);
        tricksWon.put(player2, 0);
        tricksWon.put(player3, 14);
        tricksWon.put(player4, 4);
        assertThrows(IllegalArgumentException.class, () -> round.processRoundOutcome(tricksWon));
    }

}