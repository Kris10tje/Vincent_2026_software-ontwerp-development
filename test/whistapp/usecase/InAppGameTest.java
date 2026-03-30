package whistapp.usecase;

import org.junit.jupiter.api.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.application.interfaces.IController;
import whistapp.application.interfaces.IPlayGameController;
import whistapp.domain.cards.Suit;
import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.players.PlayerType;
import whistapp.ui.IInputOutputProvider;
import whistapp.ui.TestClasses.TestPlayGameCLI;

/**
 * Scenario tests for Use Case 2: In-app play game (fully autonomous bot game).
 *
 * <p>Each test maps to a numbered step in the assignment UC description.
 * Comments like {@code // step 4a.} indicate which UC step is exercised.
 *
 * <p>Mock strategy:
 * - All players are flagged as autonomous via {@code mockGame.isAutonomous(anyInt()) = true},
 *   so the CLI plays through bidding and all tricks without needing user input.
 * - {@code mockGame.biddingStabilised()} returns {@code true} immediately so bidding terminates.
 * - {@code mockGame.isTrickOver()} + {@code mockGame.evaluateAndAdvanceTrick()} control the trick loop.
 */
public class InAppGameTest {

    private IInputOutputProvider mockInputOutput;
    private IController mockController;
    private IPlayGameController mockGame;
    private TestPlayGameCLI cli;

    public InAppGameTest() {
        mockInputOutput = mock(IInputOutputProvider.class);
        mockController = mock(IController.class);
        mockGame = mock(IPlayGameController.class);

        cli = new TestPlayGameCLI(mockController, mockInputOutput);
        cli.setGame(mockGame);
    }

    @Test
    @DisplayName("Step 2: System deals cards and runs the bidding phase autonomously")
    void testStep2_biddingPhaseRunsAutonomously() {
        // step 2.: The system deals cards to all players and runs the bidding phase.

        // Arrange: bidding is immediately stable, all players are bots
        when(mockGame.biddingStabilised()).thenReturn(true);
        when(mockGame.isAutonomous(anyInt())).thenReturn(true);
        when(mockGame.getLoneProposer()).thenReturn(null);
        when(mockGame.evaluateAndAdvanceTrick()).thenReturn(true);
        when(mockGame.isTrickOver()).thenReturn(true);
        IPlayer dealer = mockPlayer("Dealer");
        when(mockGame.getDealer()).thenReturn(dealer);
        when(mockGame.getFinalBidName()).thenReturn("Pass");
        when(mockGame.getFinalBidDeclarers()).thenReturn(new IPlayer[]{});
        when(mockGame.getScoresPerPlayer()).thenReturn(new HashMap<>());
        when(mockGame.getPlayers()).thenReturn(new ArrayList<>());
        when(mockInputOutput.readLine()).thenReturn(""); // "Press enter to continue"
        when(mockGame.getCurrentTrickWinner()).thenReturn(dealer);

        // Act
        cli.callShowRound();

        // step 2.: verify bidding phase feedback is shown
        verify(mockGame, atLeastOnce()).biddingStabilised();
        verify(mockInputOutput).writeLine(contains("THE BIDDING PHASE HAS ENDED."));
        verify(mockInputOutput).writeLine(contains("Winning bid: Pass"));
    }

    @Test
    @DisplayName("Step 3: System plays all tricks autonomously")
    void testStep3_allTricksPlayedAutonomously() {
        // step 3.: The system plays through all 13 tricks without human input.

        IPlayer winner = mockPlayer("BotA");

        setupFullyAutonomousGame(winner);

        // Tricks left: 1 initially, then 0 after first evaluateAndAdvanceTrick call
        when(mockGame.evaluateAndAdvanceTrick()).thenReturn(true);
        when(mockGame.isTrickOver()).thenReturn(false, true);
        when(mockGame.getCurrentTrickWinner()).thenReturn(winner);
        when(mockInputOutput.readLine()).thenReturn("");

        // Act
        cli.callShowRound();

        // step 3.: verify the trick result was shown after the autonomous play
        verify(mockGame, atLeastOnce()).processAutonomousCardPlay();
        verify(mockInputOutput).writeLine(contains("Winner of this trick: " + winner.getName()));
    }

    @Test
    @DisplayName("Step 4: System shows dealer and score feedback after round")
    void testStep4_roundFeedbackAndScores() {
        // step 4.: After the round the system shows the winning bid and declarers.

        IPlayer dealer = mockPlayer("DealerBot");

        setupFullyAutonomousGame(dealer);
        when(mockGame.isTrickOver()).thenReturn(false, true);
        when(mockGame.evaluateAndAdvanceTrick()).thenReturn(true);
        when(mockInputOutput.readLine()).thenReturn("");
        when(mockGame.getCurrentTrickWinner()).thenReturn(dealer);

        cli.callShowRound();

        // step 4.: dealer info and final bid feedback are shown
        verify(mockInputOutput).writeLine(contains("The dealer is: " + dealer.getName()));
        verify(mockInputOutput).writeLine(contains("THE BIDDING PHASE HAS ENDED."));
    }

    @Test
    @DisplayName("Step 5: System asks whether to play another round after a completed game")
    void testStep5_continuePromptAfterRound() {
        // step 5.: The system prompts the user to continue after the round is over.

        when(mockController.getPlayerCount()).thenReturn(4);
        when(mockController.startNewPlayGame(any(LinkedHashMap.class))).thenReturn(mockGame);
        when(mockController.getBotTypes()).thenReturn(PlayerType.getBotTypes());

        IPlayer botDealer = mockPlayer("Bot");

        setupFullyAutonomousGame(botDealer);
        when(mockGame.evaluateAndAdvanceTrick()).thenReturn(true);
        when(mockGame.getCurrentTrickWinner()).thenReturn(botDealer);
        when(mockGame.isTrickOver()).thenReturn(true);

        // Input: 
        // 1. number of real players (0)
        // 2-5. difficulty for 4 bots (assuming 4 players total). Use "1" (LOW_BOT).
        // 6. "No" (1) to another round
        // 7. "No" (1) to another game
        when(mockInputOutput.readLine()).thenReturn("0", "1", "1", "1", "1", "1", "1");

        // Act
        cli.show();

        // step 5.: continuation prompts are shown
        verify(mockInputOutput).writeLine(contains("Do you want to play another round?"));
        verify(mockInputOutput).writeLine(contains("Do you want to play another game?"));
    }

    /* ---------------------------------------------------------------------- */

    /** Sets up a minimal fully-autonomous round (bidding immediately stable, no tricks remain). */
    private void setupFullyAutonomousGame(IPlayer dealer) {
        when(mockGame.biddingStabilised()).thenReturn(true);
        when(mockGame.isAutonomous(anyInt())).thenReturn(true);
        when(mockGame.getLoneProposer()).thenReturn(null);
        when(mockGame.getDealer()).thenReturn(dealer);
        when(mockGame.getFinalBidName()).thenReturn("Pass");
        when(mockGame.getFinalBidDeclarers()).thenReturn(new IPlayer[]{});
        when(mockGame.getScoresPerPlayer()).thenReturn(new HashMap<>());
        when(mockGame.getPlayers()).thenReturn(new ArrayList<>());
        when(mockGame.getCurrentTrickCards()).thenReturn(new LinkedHashMap<>());
        when(mockGame.getOpenMiserieHands()).thenReturn(new HashMap<>());
        when(mockGame.getCardsForCurrentPlayer()).thenReturn(new ArrayList<>());
        when(mockGame.getAllowedCardsForCurrentPlayer()).thenReturn(new ArrayList<>());
        when(mockGame.getOriginalTrumpSuit()).thenReturn(Suit.HEARTS);
        when(mockGame.getTrumpSuit()).thenReturn(Suit.HEARTS);
    }

    private IPlayer mockPlayer(String name) {
        IPlayer p = mock(IPlayer.class);
        when(p.getName()).thenReturn(name);
        return p;
    }
}
