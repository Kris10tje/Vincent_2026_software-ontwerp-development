package whistapp.ui;

import org.junit.jupiter.api.*;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import whistapp.application.*;
import whistapp.ui.TestClasses.TestScoreGameCLI;

public class ScoreGameCLITest {
    private InputOutputProvider mockInputOutput;
    private IController mockController;
    private TestScoreGameCLI cli;

    public ScoreGameCLITest()
    {
        this.mockInputOutput= mock(InputOutputProvider.class);
        this.mockController = mock(IController.class);
        this.cli = new TestScoreGameCLI(mockController, mockInputOutput);
    }


    @Test // Collecting 4 unique player names prints confirmation
    @DisplayName("Start new game - controller throws exception on first call")
    void testStartNewGame_ControllerThrowsException(){
        //Arrange
        when(mockInputOutput.readLine()).thenReturn(
            "Alice", "Bob", "Charlie", "Diana", "Alice", "Bob", "Charlie", "Diana");
        doThrow(new IllegalArgumentException("invalid players"))
            .doNothing()
            .when(mockController)
            .startNewScoreGame(any(ArrayList.class));
        

        //Act  
        cli.callStartNewGame();

        //Assert
        verify(mockController, times(2)).startNewScoreGame(any(ArrayList.class));
        verify(mockInputOutput).writeLine(contains("Failed to start new game"));
        verify(mockInputOutput).writeLine("Players registered: \n\n- Alice\n- Bob\n- Charlie\n- Diana");
    }
}
