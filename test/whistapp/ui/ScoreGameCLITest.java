package whistapp.ui;

import org.junit.jupiter.api.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import whistapp.application.interfaces.IController;
import whistapp.application.interfaces.IScoreGameController;
import whistapp.ui.TestClasses.TestScoreGameCLI;

/**
 * Tests ScoreGameCLI startup behavior with Mockito-controlled collaborators.
 *
 * <p>Mock strategy in this class:
 * - {@code mockInputOutput} simulates terminal input/output.
 * - {@code mockController} simulates application-layer game creation.
 * - {@code mockGame} is returned by the controller on successful startup.
 *
 * <p>The test below specifically validates retry behavior: first startup attempt fails,
 * second attempt succeeds, and expected user feedback is printed.
 */
public class ScoreGameCLITest {

    private IInputOutputProvider mockInputOutput;
    private IController mockController;
    private TestScoreGameCLI cli;

    public ScoreGameCLITest() {
        // Create mocks for all external dependencies used by the CLI.
        this.mockInputOutput = mock(IInputOutputProvider.class);
        this.mockController = mock(IController.class);

        // Use a test wrapper to call protected CLI behavior directly.
        this.cli = new TestScoreGameCLI(mockController, mockInputOutput);
    }

    @Test // Collecting 4 unique player names prints confirmation
    @DisplayName("Start new game - controller throws exception on first call")
    void testStartNewGame_ControllerThrowsException() {
        //Arrange

        // This mock stands for a successfully created score-game controller.
        IScoreGameController mockGame = mock(IScoreGameController.class);

        // Startup asks for 4 player names each attempt.
        when(mockController.getPlayerCount()).thenReturn(4);

        // Provide names for two attempts:
        // - first 4 names are used for the failing attempt
        // - second 4 names are used after retry
        when(mockInputOutput.readLine()).thenReturn(
                "Alice", "Bob", "Charlie", "Diana", "Alice", "Bob", "Charlie", "Diana");

        // Onderstaande test de try-catch die de exception van
        // Controller voor startNewGame moet opvangen.
        //
        // Als de Controller nog niet geïmplementeerd zou zijn,
        // kan je dit niet testen. Met de mock+interface gaat dit wél.
        // We mocken het throwen van de error zodat we de try-catch
        // behaviour kunnen testen.
        //
        // Als startNewScoreGame opgeroepen wordt op de Controller
        // (controller.startNewGame()), dan laten we de gemockte Controller
        // een error gooien (we moeten dus niet een hele setup doen om het
        // scenario waarin de controller effectief een exception zou gooien te
        // simuleren).
        //
        //  1. Alles BOVEN .when() is de return waarde van de gemockte class (interface)
        //  2. De .when() ZELF houdt bij op welke instance een method wordt aangeroepen
        //  3. NA .when() doe je .<methodDieWordtOpgeroepen>([argumenten])
        //
        // Hierbij kan argumenten bv. zijn
        //  -> ["NaamA","NaamB"]
        //  -> any(ArrayList.class) -> mag aangeroepen worden met eender welke ArrayList
        //
        doThrow(new IllegalArgumentException("invalid players")) // eerste keer throwen we een exception
                .doReturn(mockGame) // tweede keer (user geeft zogezegd valid input), returnen we een game (interface)
                .when(mockController) //
                .startNewScoreGame(any(ArrayList.class));

        // Act
        cli.callStartNewGame();

        // Assert

        // Verify dat
        //  - OP mockController
        //  - 2 keer
        //  - de method startNewScoreGame() wordt opgroepen
        //  - met eender welke ArrayList
        verify(mockController, times(2)).startNewScoreGame(any(ArrayList.class));

        // Verify user is informed about the failure and then successful registration.
        verify(mockInputOutput).writeLine(contains("Failed to start new game"));
        verify(mockInputOutput).writeLine("Players registered: \n\n- Alice\n- Bob\n- Charlie\n- Diana");
    }
}
