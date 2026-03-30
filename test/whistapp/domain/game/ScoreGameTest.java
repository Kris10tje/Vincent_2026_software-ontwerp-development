package whistapp.domain.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.bids.BidType;
import whistapp.domain.interfaces.IScoreGame;
import whistapp.domain.interfaces.IPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreGameTest {

    private IScoreGame game;

    private String player1;
    private String player2;
    private String player3;
    private String player4;

    private ArrayList<String> players;
    private ArrayList<String> declarers;

    @BeforeEach
    void setUp() {
        player1 = "Gary";
        player2 = "Jack";
        player3 = "Alice";
        player4 = "Frank";
        players = new ArrayList<>(Arrays.asList(player1, player2, player3, player4));
        declarers = new ArrayList<>(List.of(player1));

        game = new ScoreGame(players);
    }

    @Test
    void updateScores_valid() {
        game.startNewRound();
        ((ScoreGame) game).registerFinalBid(BidType.PROPOSAL, declarers, true);
        HashMap<IPlayer, Integer> tricksWon = new HashMap<>();
        tricksWon.put(game.getPlayers().get(0), 1);
        tricksWon.put(game.getPlayers().get(1), 2);
        tricksWon.put(game.getPlayers().get(2), 3);
        tricksWon.put(game.getPlayers().get(3), 7);
        game.updateScores(tricksWon);
        // The points should have been updated
        HashMap<IPlayer, Integer> scores = game.getScoresPerPlayer();
        assertEquals(4, scores.size());
        assertEquals(-18, scores.get(game.getPlayers().get(0)));
        assertEquals(6, scores.get(game.getPlayers().get(1)));
        assertEquals(6, scores.get(game.getPlayers().get(2)));
        assertEquals(6, scores.get(game.getPlayers().get(3)));

    }

    @Test
    void updateScores_valid2() {
        game.startNewRound();
        ((ScoreGame) game).registerFinalBid(BidType.PROPOSAL, declarers, false);
        HashMap<IPlayer, Integer> tricksWon = new HashMap<>();
        tricksWon.put(game.getPlayers().get(0), 1);
        tricksWon.put(game.getPlayers().get(1), 2);
        tricksWon.put(game.getPlayers().get(2), 3);
        tricksWon.put(game.getPlayers().get(3), 7);
        game.updateScores(tricksWon);
        // The points should have been updated
        HashMap<IPlayer, Integer> scores = game.getScoresPerPlayer();
        assertEquals(4, scores.size());
        assertEquals(-36, scores.get(game.getPlayers().get(0)));
        assertEquals(12, scores.get(game.getPlayers().get(1)));
        assertEquals(12, scores.get(game.getPlayers().get(2)));
        assertEquals(12, scores.get(game.getPlayers().get(3)));

    }

    @Test
    void updateScores_invalid() {
        game.startNewRound();
        HashMap<IPlayer, Integer> tricksWon = new HashMap<>();
        tricksWon.put(game.getPlayers().get(0), 1);
        tricksWon.put(game.getPlayers().get(1), 2);
        tricksWon.put(game.getPlayers().get(2), 3);
        assertThrows(IllegalArgumentException.class, () -> game.updateScores(tricksWon));

    }

    @Test
    void registerBids_missingPlayerBid_shouldThrow() {
        ScoreGame game = new ScoreGame();
        game.initializeHumanPlayers(players);
        game.startNewRound();

        HashMap<IPlayer, BidType> bids = new HashMap<>();
        bids.put(game.getPlayers().get(0), BidType.PASS);
        bids.put(game.getPlayers().get(1), BidType.PASS);
        bids.put(game.getPlayers().get(2), BidType.PASS);
        // Missing player4
        assertThrows(IllegalArgumentException.class, () -> game.registerBids(bids));
    }

    @Test
    void setReshuffledState_withoutRound_shouldThrow() {
        ScoreGame game = new ScoreGame(players);

        assertThrows(NullPointerException.class, () -> game.setReshuffledState(true));
    }
}