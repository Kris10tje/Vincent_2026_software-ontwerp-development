package whistapp.ui;

import org.junit.jupiter.api.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.LinkedHashMap;

import whistapp.application.interfaces.IController;
import whistapp.application.interfaces.IPlayGameController;
import whistapp.domain.players.PlayerType;
import whistapp.ui.TestClasses.TestPlayGameCLI;

/**
 * Unit tests for {@link PlayGameCLI} using Mockito test doubles.
 *
 * <p>Mock strategy in this class:
 * - {@code mockInputOutput} replaces terminal I/O so tests can fully script user input.
 * - {@code mockController} simulates the application layer (player creation, game setup).
 * - {@code mockGame} is returned by the controller on successful startup.
 *
 * <p>We use a {@code TestPlayGameCLI} subclass to expose the protected game methods.
 */
public class PlayGameCLITest {

    private IInputOutputProvider mockInputOutput;
    private IController mockController;
    private TestPlayGameCLI cli;

    public PlayGameCLITest() {
        this.mockInputOutput = mock(IInputOutputProvider.class);
        this.mockController = mock(IController.class);
        this.cli = new TestPlayGameCLI(mockController, mockInputOutput);
    }

    @Test
    @DisplayName("startNewGame - all human players, happy path")
    void testStartNewGame_allHuman() {
        // Arrange
        IPlayGameController mockGame = mock(IPlayGameController.class);

        // 4 real players, no bots
        when(mockController.getPlayerCount()).thenReturn(4);

        // Input: number of real players = 4, then 4 names
        when(mockInputOutput.readLine()).thenReturn("4", "Alice", "Bob", "Charlie", "Diana");

        // The controller successfully creates a game on the first call
        when(mockController.startNewPlayGame(any(LinkedHashMap.class))).thenReturn(mockGame);

        // Act
        cli.callStartNewGame();

        // Assert: controller was called exactly once with the player map
        verify(mockController, times(1)).startNewPlayGame(any(LinkedHashMap.class));

        // Verify the confirmation message was shown for each registered player
        verify(mockInputOutput).writeLine("Players registered:\n");
        verify(mockInputOutput).writeLine("- Alice");
        verify(mockInputOutput).writeLine("- Bob");
        verify(mockInputOutput).writeLine("- Charlie");
        verify(mockInputOutput).writeLine("- Diana");
    }

    @Test
    @DisplayName("startNewGame - controller throws exception, then succeeds on retry")
    void testStartNewGame_ControllerThrowsOnFirstAttempt() {
        // Arrange
        IPlayGameController mockGame = mock(IPlayGameController.class);

        // 4 real players
        when(mockController.getPlayerCount()).thenReturn(4);

        // First attempt: 0 real players (will fail domain validation), second attempt: 4 human players
        when(mockInputOutput.readLine()).thenReturn(
                "4", "Alice", "Bob", "Charlie", "Diana",   // first attempt (throws)
                "4", "Alice", "Bob", "Charlie", "Diana");  // second attempt (succeeds)

        // First call throws, second call succeeds
        doThrow(new IllegalArgumentException("invalid players"))
                .doReturn(mockGame)
                .when(mockController)
                .startNewPlayGame(any(LinkedHashMap.class));

        // Act
        cli.callStartNewGame();

        // Assert: controller was called twice (once failing, once succeeding)
        verify(mockController, times(2)).startNewPlayGame(any(LinkedHashMap.class));

        // Verify the error is reported and the confirmation is eventually shown
        verify(mockInputOutput).writeLine(contains("Error"));
        verify(mockInputOutput).writeLine("Players registered:\n");
    }

    @Test
    @DisplayName("startNewGame - bots fill remaining player slots")
    void testStartNewGame_withBots() {
        // Arrange
        IPlayGameController mockGame = mock(IPlayGameController.class);

        when(mockController.getPlayerCount()).thenReturn(4);
        when(mockController.getBotTypes()).thenReturn(PlayerType.values());

        // 2 real players + 2 Low bots
        when(mockInputOutput.readLine()).thenReturn(
                "2",        // number of real players
                "Alice",    // player 1 name
                "Bob",      // player 2 name
                "1",        // bot 1 type = LOW_BOT
                "1"         // bot 2 type = LOW_BOT
        );

        when(mockController.startNewPlayGame(any(LinkedHashMap.class))).thenReturn(mockGame);

        // Act
        cli.callStartNewGame();

        // Assert: game was started with the bot+human player map
        verify(mockController, times(1)).startNewPlayGame(any(LinkedHashMap.class));

        // Confirm human players and auto-named bots are listed
        verify(mockInputOutput).writeLine("- Alice");
        verify(mockInputOutput).writeLine("- Bob");
        verify(mockInputOutput, times(2)).writeLine(contains("Low Bot"));
    }

    @Test
    @DisplayName("startNewGame - invalid real player count retries")
    void testStartNewGame_invalidPlayerCount() {
        // Arrange
        IPlayGameController mockGame = mock(IPlayGameController.class);

        // 4 real players allowed
        when(mockController.getPlayerCount()).thenReturn(4);

        // First input: 9 (> 4, invalid). Second input: 4 (valid). Then 4 names.
        when(mockInputOutput.readLine()).thenReturn(
                "9",         // invalid count (> 4)
                "4",         // valid count
                "Alice", "Bob", "Charlie", "Diana"
        );

        when(mockController.startNewPlayGame(any(LinkedHashMap.class))).thenReturn(mockGame);

        // Act
        cli.callStartNewGame();

        // Assert: error feedback was shown for the invalid count
        verify(mockInputOutput).writeLine(contains("Invalid number"));

        // Assert: controller was called exactly once with valid players
        verify(mockController, times(1)).startNewPlayGame(any(LinkedHashMap.class));
    }
}
