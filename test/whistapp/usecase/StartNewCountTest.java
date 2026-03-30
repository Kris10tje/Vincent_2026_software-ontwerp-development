package whistapp.usecase;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;

import whistapp.application.interfaces.IController;
import whistapp.application.interfaces.IScoreGameController;
import whistapp.domain.interfaces.IPlayer;
import whistapp.ui.IInputOutputProvider;
import whistapp.ui.TestClasses.TestScoreGameCLI;

/**
 * Scenario tests for Use Case 1: Start new count (score tracking).
 *
 * <p>Each test maps to numbered steps in the assignment UC description.
 * Comments like {@code // step 4a.} indicate which UC step is exercised.
 *
 * <p>Mock strategy:
 * - {@code mockInputOutput} controls all terminal I/O.
 * - {@code mockController} replaces the application layer.
 * - {@code mockGame} is a fully mocked {@link IScoreGameController}.
 */
public class StartNewCountTest {

    private IInputOutputProvider mockInputOutput;
    private IController mockController;
    private IScoreGameController mockGame;
    private TestScoreGameCLI cli;

    @SuppressWarnings("unchecked")
    public StartNewCountTest() {
        mockInputOutput = mock(IInputOutputProvider.class);
        mockController = mock(IController.class);
        mockGame = mock(IScoreGameController.class);

        cli = new TestScoreGameCLI(mockController, mockInputOutput);
    }

    @Test
    @DisplayName("Steps 1-3: System asks for player names and confirms registration")
    void testSteps1to3_registrationAndConfirmation() {
        // step 1.: The user selects to start a new count.
        // (Implicit: ScoreGameCLI is instantiated and startNewGame() is invoked.)

        // step 2.: The system asks for player names.
        when(mockController.getPlayerCount()).thenReturn(4);
        when(mockInputOutput.readLine()).thenReturn("Alice", "Bob", "Charlie", "Diana");
        when(mockController.startNewScoreGame(any(ArrayList.class))).thenReturn(mockGame);

        // Act
        cli.callStartNewGame();

        // step 3.: The system confirms the players are registered.
        verify(mockInputOutput).writeLine(contains("Enter name for player 1"));
        verify(mockInputOutput).writeLine(contains("Enter name for player 2"));
        verify(mockInputOutput).writeLine(contains("Enter name for player 3"));
        verify(mockInputOutput).writeLine(contains("Enter name for player 4"));
        verify(mockInputOutput).writeLine("Players registered: \n\n- Alice\n- Bob\n- Charlie\n- Diana");
    }

    @Test
    @DisplayName("Step 3 (exception path): System retries on invalid player names")
    void testStep3_retriesOnControllerException() {
        // step 3.: If the domain rejects the names, the system asks again.

        when(mockController.getPlayerCount()).thenReturn(4);
        when(mockInputOutput.readLine()).thenReturn(
                "Alice", "Alice", "Alice", "Alice",      // first attempt (duplicate names → throws)
                "Alice", "Bob", "Charlie", "Diana");     // second attempt (succeeds)

        doThrow(new IllegalArgumentException("duplicate names"))
                .doReturn(mockGame)
                .when(mockController)
                .startNewScoreGame(any(ArrayList.class));

        // Act
        cli.callStartNewGame();

        // Assert: controller was called twice, first failure surfaced to user
        verify(mockController, times(2)).startNewScoreGame(any(ArrayList.class));
        verify(mockInputOutput).writeLine(contains("Failed to start new game"));
        verify(mockInputOutput).writeLine(contains("Alice\n- Bob\n- Charlie\n- Diana"));
    }

    @Test
    @DisplayName("Step 4: System asks each player for their final active bid")
    void testStep4_systemAsksForBids() {
        // step 4.: The user registers which bid was the final active bid for each player.

        // Set up 2 mocked players for brevity
        IPlayer playerA = mock(IPlayer.class);
        IPlayer playerB = mock(IPlayer.class);
        when(playerA.getName()).thenReturn("Alice");
        when(playerB.getName()).thenReturn("Bob");

        ArrayList<IPlayer> players = new ArrayList<>();
        players.add(playerA);
        players.add(playerB);

        when(mockGame.getPlayers()).thenReturn(players);
        when(mockController.getBidTypes()).thenReturn(new String[]{"Pass", "Proposal", "Solo"});
        when(mockGame.getScoresPerPlayer()).thenReturn(new HashMap<>());

        // Bids: Alice = "Pass" (1), Bob = "Pass" (1)
        // Reshuffle: No (1)
        // Tricks: Alice = 7, Bob = 6
        when(mockInputOutput.readLine()).thenReturn("1", "1", "1", "7", "6");

        cli.setGame(mockGame);

        // Act
        cli.callShowRound();

        // step 4.: Verify the bid prompt was shown for each player
        verify(mockInputOutput).writeLine(contains("What is the final active bid for Alice?"));
        verify(mockInputOutput).writeLine(contains("What is the final active bid for Bob?"));
    }

    @Test
    @DisplayName("Step 5: System asks whether there was a reshuffle")
    void testStep5_reshufflePrompt() {
        // step 5.: After bidding, the system asks whether there was a reshuffle.

        IPlayer playerA = mock(IPlayer.class);
        when(playerA.getName()).thenReturn("Alice");

        ArrayList<IPlayer> players = new ArrayList<>();
        players.add(playerA);

        when(mockGame.getPlayers()).thenReturn(players);
        when(mockController.getBidTypes()).thenReturn(new String[]{"Pass"});
        when(mockGame.getScoresPerPlayer()).thenReturn(new HashMap<>());

        when(mockInputOutput.readLine()).thenReturn("1", "1", "7");

        cli.setGame(mockGame);

        cli.callShowRound();

        // step 5.: Verify the reshuffle prompt appears
        verify(mockInputOutput).writeLine(contains("Was there a reshuffle?"));
    }

    @Test
    @DisplayName("Step 6: System asks how many tricks each player won")
    void testStep6_trickCountPrompts() {
        // step 6.: The user enters the round result: number of tricks won per player.

        IPlayer playerA = mock(IPlayer.class);
        IPlayer playerB = mock(IPlayer.class);
        when(playerA.getName()).thenReturn("Alice");
        when(playerB.getName()).thenReturn("Bob");

        ArrayList<IPlayer> players = new ArrayList<>();
        players.add(playerA);
        players.add(playerB);

        when(mockGame.getPlayers()).thenReturn(players);
        when(mockController.getBidTypes()).thenReturn(new String[]{"Pass"});
        when(mockGame.getScoresPerPlayer()).thenReturn(new HashMap<>());

        // Bid (1 = Pass) for each, reshuffle No, tricks for Alice and Bob
        when(mockInputOutput.readLine()).thenReturn("1", "1", "1", "7", "6");

        cli.setGame(mockGame);

        // Act
        cli.callShowRound();

        // step 6.: Verify the trick count prompt was shown for each player
        verify(mockInputOutput).writeLine(contains("How many tricks were won by Alice"));
        verify(mockInputOutput).writeLine(contains("How many tricks were won by Bob"));
    }

    @Test
    @DisplayName("Step 7: System shows scores after round; asks to play another round")
    void testStep7_scoresAndContinuationPrompt() {
        // step 7.: The system shows updated scores and asks whether to play another round.

        when(mockController.getPlayerCount()).thenReturn(4);
        when(mockController.startNewScoreGame(any(ArrayList.class))).thenReturn(mockGame);
        when(mockInputOutput.readLine()).thenReturn(
                "Alice", "Bob", "Charlie", "Diana",  // player names (step 2)
                "1", "1", "1", "1",                  // bids (all Pass)
                "1",                                  // reshuffle: No
                "4", "3", "3", "3",                  // tricks per player
                "1",                                  // another round: No
                "1"                                   // another game: No
        );

        IPlayer playerA = mock(IPlayer.class), playerB = mock(IPlayer.class);
        IPlayer playerC = mock(IPlayer.class), playerD = mock(IPlayer.class);
        when(playerA.getName()).thenReturn("Alice");
        when(playerB.getName()).thenReturn("Bob");
        when(playerC.getName()).thenReturn("Charlie");
        when(playerD.getName()).thenReturn("Diana");

        ArrayList<IPlayer> players = new ArrayList<>();
        players.add(playerA);
        players.add(playerB);
        players.add(playerC);
        players.add(playerD);

        when(mockController.getPlayerCount()).thenReturn(4);
        when(mockController.getBidTypes()).thenReturn(new String[]{"Pass", "Proposal"});
        when(mockGame.getPlayers()).thenReturn(players);
        when(mockGame.getScoresPerPlayer()).thenReturn(new HashMap<>());

        // Act: full game loop
        cli.show();

        // step 7.: scores and round/game continuation prompts are shown
        verify(mockInputOutput).writeLine(contains("Scores after this round:"));
        verify(mockInputOutput).writeLine(contains("Do you want to play another round?"));

        // step 8.: after choosing not to continue, the game results are shown
        verify(mockInputOutput).writeLine(contains("Game results:"));
        verify(mockInputOutput).writeLine(contains("Do you want to play another game?"));
    }
}
