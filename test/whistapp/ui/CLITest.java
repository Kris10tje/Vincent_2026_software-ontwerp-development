package whistapp.ui;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

import whistapp.application.Interfaces.IController;
import whistapp.ui.TestClasses.TestCLI;

public class CLITest {
    private InputOutputProvider mockInputOutput;
    private IController mockController;
    private TestCLI cli;

    public CLITest()
    {
        this.mockInputOutput= mock(InputOutputProvider.class);
        this.mockController = mock(IController.class);
        this.cli = new TestCLI(mockController, mockInputOutput);
    }

    @Test
    @DisplayName("getInputString")
    void testGetInputString() {
        //Arrange
        String input = "hello world\n";
        String output = "Enter text";

        when(mockInputOutput.readLine()).thenReturn(input);

        //Act
        String result = cli.callGetInputString(output);

        //Assert
        assertEquals(input, result, "Should return string entered by user");
        verify(mockInputOutput).writeLine(contains(output));
    }

    @Test
    @DisplayName("getInputInt")
    void testGetInputInt() {
        //Arrange
        String output = "Enter number";
        String input = "42\n";
        when(mockInputOutput.readLine()).thenReturn(input);

        //Act
        int result = cli.callGetInputInt(output);

        assertEquals(42, result, "Should return number entered by user");
        verify(mockInputOutput).writeLine(contains(output));
    }

    @Test
    @DisplayName("getChoice")
    void testGetChoice_valid() {
        //Arrange
        String input = "2\n";
        String[] options = {"first", "second", "third"};
        when(mockInputOutput.readLine()).thenReturn(input);

        //Act
        String choice = cli.callGetChoice("Pick one", options);

        //Assert
        assertEquals("second", choice, "Should return choice made by user");

        verify(mockInputOutput).writeLine(contains("first"));
        verify(mockInputOutput).writeLine(contains("second"));
        verify(mockInputOutput).writeLine(contains("third"));
    }

    @Test
    @DisplayName("getChoice retry")
    void testGetChoice_invalidThenValid() {
        //Arrange
        // first enter an invalid choice (3), then a valid (1)
        when(mockInputOutput.readLine()).thenReturn("3", "1");

        String[] options = {"optA", "optB"};

        //Act
        String choice = cli.callGetChoice("Choose", options);
        
        //Assert
        assertEquals("optA", choice, "Should output the choice made by the user");
        verify(mockInputOutput).writeLine(contains("Invalid choice"));
    }

    @Test
    @DisplayName("getNo")
    void testGetYesNo_expectedBehaviorGetNo() {
        //Arrange
        // First test selecting "No" (0) -> expect false
        String input = "1\n";
        when(mockInputOutput.readLine()).thenReturn(input);

        //Act
        boolean response = cli.callGetYesNo("Confirm?");
       
        //Assert
        assertFalse(response, "Selecting option 1 (No) should return false");
    }

    @Test
    @DisplayName("getYes")
    void testGetYesNo_expectedBehaviorGetYes() {
        //Arrange
        // First test selecting "Yes" (1) -> expect true
        String input = "2\n";
        when(mockInputOutput.readLine()).thenReturn(input);

        //Act
        boolean response = cli.callGetYesNo("Confirm?");
       
        //Assert
        assertTrue(response, "Selecting option 1 (No) should return false");
    }

    @Test
    @DisplayName("getChoices")
    void testGetChoices_basic() {
        //Arrange
        // Toggle option 1 and 3, then finish with 0
        when(mockInputOutput.readLine()).thenReturn("1", "3", "0");
        String[] options = {"A", "B", "C"};

        //Act
        String[] result = cli.callGetChoices("Select items", options);

        //Assert
        // expected indices: 0 and 2
        assertArrayEquals(new String[]{"A", "C"}, result, "Should return all chosen values");
        verify(mockInputOutput, times(3)).writeLine(contains("0. Complete selection"));
        verify(mockInputOutput, times(1)).writeLine(contains("1. [ ] A"));
        verify(mockInputOutput, times(2)).writeLine(contains("1. [x] A"));
        verify(mockInputOutput, times(3)).writeLine(contains("2. [ ] B"));
        verify(mockInputOutput, times(2)).writeLine(contains("3. [ ] C"));
        verify(mockInputOutput, times(1)).writeLine(contains("3. [x] C"));
    }

    @Test
    @DisplayName("getChoices")
    void testGetChoices_invalidThenToggle() {
        //Arrange
        // invalid "5" (outside range), then press enter (\n), then valid 1 then finish
        when(mockInputOutput.readLine()).thenReturn("5", "\n","1", "0");
        
        String[] options = {"Alpha", "Beta"};

        //Act
        String[] result = cli.callGetChoices("Choose", options);
        
        //Assert
        assertArrayEquals(new String[]{"Alpha"}, result, "Should return all chosen values");
        verify(mockInputOutput).writeLine(contains("Invalid choice"));
    }
}
