package whistapp.domain.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.bids.BidType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreGameTest {

    private ScoreGame game;

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
        game.registerFinalBid(BidType.PROPOSAL, declarers, true);
        HashMap<String, Integer> tricksWon = new HashMap<>();
        tricksWon.put(player1, 1);
        tricksWon.put(player2, 2);
        tricksWon.put(player3, 3);
        tricksWon.put(player4, 7);
        game.updateScores(tricksWon);
        // The points should have been updated
        HashMap<String, Integer> scores = game.getScoresPerPlayer();
        assertEquals(4, scores.size());
        assertEquals(-18, scores.get(player1));
        assertEquals(6, scores.get(player2));
        assertEquals(6, scores.get(player3));
        assertEquals(6, scores.get(player4));
        assertEquals(-18, game.getPlayers().get(0).getScore());
        assertEquals(6, game.getPlayers().get(1).getScore());
        assertEquals(6, game.getPlayers().get(2).getScore());
        assertEquals(6, game.getPlayers().get(3).getScore());

    }

    @Test
    void updateScores_valid2() {
        game.startNewRound();
        game.registerFinalBid(BidType.PROPOSAL, declarers, false);
        HashMap<String, Integer> tricksWon = new HashMap<>();
        tricksWon.put(player1, 1);
        tricksWon.put(player2, 2);
        tricksWon.put(player3, 3);
        tricksWon.put(player4, 7);
        game.updateScores(tricksWon);
        // The points should have been updated
        HashMap<String, Integer> scores = game.getScoresPerPlayer();
        assertEquals(4, scores.size());
        assertEquals(-36, scores.get(player1));
        assertEquals(12, scores.get(player2));
        assertEquals(12, scores.get(player3));
        assertEquals(12, scores.get(player4));
        assertEquals(-36, game.getPlayers().get(0).getScore());
        assertEquals(12, game.getPlayers().get(1).getScore());
        assertEquals(12, game.getPlayers().get(2).getScore());
        assertEquals(12, game.getPlayers().get(3).getScore());

    }

    @Test
    void updateScores_invalid() {
        game.startNewRound();
        HashMap<String, Integer> tricksWon = new HashMap<>();
        tricksWon.put(player1, 1);
        tricksWon.put(player2, 2);
        tricksWon.put(player3, 3);
        assertThrows(IllegalArgumentException.class, () -> game.updateScores(tricksWon));

    }

    @Test
    void startPlayingRound_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::startPlayingRound);
    }

    @Test
    void getFinalBidName_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::getFinalBidName);
    }

    @Test
    void getFinalBidDeclarers_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::getFinalBidDeclarers);
    }

    @Test
    void getPossibleBidNames_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::getPossibleBidNames);
    }

    @Test
    void getDealerName_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::getDealerName);
    }

    @Test
    void calculateAndUpdateScores_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::calculateAndUpdateScores);
    }

    @Test
    void submitBid_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class,
                () -> game.submitBid(null, null));
    }

    @Test
    void evaluateRoundBids_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::evaluateRoundBids);
    }

    @Test
    void processCardPlay_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class,
                () -> game.processCardPlay("king of hearts"));
    }

    @Test
    void proceedAutonomousBid_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::proceedAutonomousBid);
    }

    @Test
    void restartFailedRound_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::restartFailedRound);
    }

    @Test
    void processAutonomousCardPlay_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::processAutonomousCardPlay);
    }

    @Test
    void evaluateAndAdvanceTrick_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::evaluateAndAdvanceTrick);
    }

    @Test
    void getLastTrickString_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::getLastTrickString);
    }

    @Test
    void getTricksLeft_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::getTricksLeft);
    }

    @Test
    void isTrickOver_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::isTrickOver);
    }

    @Test
    void getActivePlayerName_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::getActivePlayerName);
    }

    @Test
    void getPlayerCards_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class,
                () -> game.getPlayerCards("Alice"));
    }

    @Test
    void getCurrentRoundTrumpSuit_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::getCurrentRoundTrumpSuit);
    }

    @Test
    void getHighestBid_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class, game::getHighestBid);
    }

    @Test
    void getCurrentTrickCardsAsStrings_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class,
                game::getCurrentTrickCardsAsStrings);
    }

    @Test
    void getAllowedCardsForCurrentPlayer_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class,
                game::getAllowedCardsForCurrentPlayer);
    }

    @Test
    void getTrumpSuitName_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class,
                game::getTrumpSuitName);
    }

    @Test
    void getOriginalTrumpSuitName_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class,
                game::getOriginalTrumpSuitName);
    }

    @Test
    void getCurrentTrickWinnerName_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class,
                game::getCurrentTrickWinnerName);
    }

    @Test
    void getLastDealtCard_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class,
                game::getLastDealtCard);
    }

    @Test
    void bidRequiresTrumpDeclaration_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class,
                () -> game.bidRequiresTrumpDeclaration(""));
    }

    @Test
    void getExistingBids_shouldThrow() {
        ScoreGame game = new ScoreGame();

        assertThrows(IllegalStateException.class,
                game::getExistingBids);
    }
}