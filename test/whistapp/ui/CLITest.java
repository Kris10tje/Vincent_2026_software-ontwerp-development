package whistapp.ui;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

import whistapp.application.interfaces.IController;
import whistapp.ui.TestClasses.TestCLI;

/**
 * Unit tests for {@link CLI} behavior using Mockito test doubles.
 *
 * <p>Mock strategy in this class:
 * - {@code mockInputOutput} replaces terminal I/O so tests can fully script user input.
 * - {@code mockController} is injected because {@code CLI} requires a controller dependency,
 *   but controller behavior is not under test in this class.
 * - {@code when(...).thenReturn(...)} defines deterministic input sequences.
 * - {@code verify(...)} checks that expected output was written to the user.
 * 
 * <p>We extend the CLI class because the methods in that class could be protected/private
 * and otherwise inaccessible to the test code.
 * 
 * <p> Instead of using the actual System.in/System.out, 
 * we use the interface IInputOutputProvider (which is implemented
 * by ConsoleInputOutputProvider in the actual implementation) 
 * to mock user input and capture output.
 * This way, we don't rely on real console I/O
 * (standard in/out is really hard to test without this approach).
 */
public class CLITest {

    // Mocks for external dependencies used by the CLI. 
    //
    // Instead of using the actual System.in/System.out, 
    // we use the interface IInputOutputProvider (which is implemented
    // by ConsoleInputOutputProvider in the actual implementation) 
    // to mock user input and capture output.
    // 
    // This way, we don't rely on real console I/O
    // (standard in/out is really hard to test without this approach).
    private IInputOutputProvider mockInputOutput;
    private IController mockController;

    // This is the extended class from CLI which makes all methods public
    private TestCLI cli;

    public CLITest()
    {
        // @code{mock} creates an instance of the given interface
        // that has all methods stubbed to do nothing by default. 
        // 
        // We can then use when(...).thenReturn(...) to specify 
        // return values for specific method calls.
        this.mockInputOutput= mock(IInputOutputProvider.class);
        this.mockController = mock(IController.class);

        // TestCLI exposes protected CLI methods as public so they can be unit tested directly.
        this.cli = new TestCLI(mockController, mockInputOutput);
    }

    @Test
    @DisplayName("getInputString")
    void testGetInputString() {
        
        String mockedUserInput = "hello world\n";
        String mockedCLIOutput = "Enter text";

        // Mock an IO provider (like the ConsoleInputOutputProvider in our
        // actual implementation [i.e. the terminal of the user]),
        // so we can simulate user input and capture output.
        //
        //  -> when the CLI calls readLine(),
        //     return the predefined input string
        when(mockInputOutput.readLine()).thenReturn(mockedUserInput);

        // Call the method under test,
        // where mockedCLIOutput is the string that is shown to the user
        String result = cli.callGetInputString(mockedCLIOutput);

        // Check if the String that is returned by the CLI equals
        // the mocked user input.
        assertEquals(mockedUserInput, result, "Should return string entered by user");

        // Verify prompt was shown to the user.
        verify(mockInputOutput).writeLine(contains(mockedCLIOutput));
    }

    @Test
    @DisplayName("getInputInt")
    void testGetInputInt() {
        //Arrange
        String output = "Enter number";
        String input = "42\n";

        // Stub one valid integer input.
        when(mockInputOutput.readLine()).thenReturn(input);

        //Act
        int result = cli.callGetInputInt(output);

        // Assert parsed integer and prompt side-effect.
        assertEquals(42, result, "Should return number entered by user");
        verify(mockInputOutput).writeLine(contains(output));
    }

    @Test
    @DisplayName("getChoice")
    void testGetChoice_valid() {
        //Arrange
        String input = "2\n";
        String[] options = {"first", "second", "third"};

        // Stub choice "2" so CLI should return the second option.
        when(mockInputOutput.readLine()).thenReturn(input);

        //Act
        String choice = cli.callGetChoice("Pick one", options);

        //Assert
        assertEquals("second", choice, "Should return choice made by user");

        // Verify all options were printed before selection.
        verify(mockInputOutput).writeLine(contains("first"));
        verify(mockInputOutput).writeLine(contains("second"));
        verify(mockInputOutput).writeLine(contains("third"));
    }

    @Test
    @DisplayName("getChoice retry")
    void testGetChoice_invalidThenValid() {
        //Arrange
        // first enter an invalid choice (3), then a valid (1)

        // Multiple thenReturn values simulate repeated user attempts.
        when(mockInputOutput.readLine()).thenReturn("3", "1");

        String[] options = {"optA", "optB"};

        //Act
        String choice = cli.callGetChoice("Choose", options);
        
        //Assert
        assertEquals("optA", choice, "Should output the choice made by the user");

        // Verify that invalid input feedback was printed before retry succeeds.
        verify(mockInputOutput).writeLine(contains("Invalid choice"));
    }

    @Test
    @DisplayName("getNo")
    void testGetYesNo_expectedBehaviorGetNo() {
        //Arrange
        // Stub numeric choice mapped to "No".
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
        // Stub numeric choice mapped to "Yes".
        String input = "2\n";
        when(mockInputOutput.readLine()).thenReturn(input);

        //Act
        boolean response = cli.callGetYesNo("Confirm?");
       
        //Assert
        assertTrue(response, "Selecting option 2 (Yes) should return true");
    }

    @Test
    @DisplayName("getChoices")
    void testGetChoices_basic() {
        //Arrange
        // Toggle option 1 and 3, then finish with 0

        // More than one argument in thenReturn(.):
        //  -> first answer 1,
        //  -> second answer 3,
        //  -> third answer 0
        when(mockInputOutput.readLine()).thenReturn("1", "3", "0");
        String[] options = {"A", "B", "C"};

        //Act
        String[] result = cli.callGetChoices("Select items", options);

        //Assert
        // expected indices: 0 and 2
        assertArrayEquals(new String[]{"A", "C"}, result, "Should return all chosen values");

        // Verify the menu rendering across each interaction step.
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

        // Stub a full retry scenario including the extra Enter consumed by error handling.
        when(mockInputOutput.readLine()).thenReturn("5", "\n","1", "0");
        
        String[] options = {"Alpha", "Beta"};

        //Act
        String[] result = cli.callGetChoices("Choose", options);
        
        //Assert
        assertArrayEquals(new String[]{"Alpha"}, result, "Should return all chosen values");

        // Verify user-facing validation message for invalid menu choice.
        verify(mockInputOutput).writeLine(contains("Invalid choice"));
    }
}
