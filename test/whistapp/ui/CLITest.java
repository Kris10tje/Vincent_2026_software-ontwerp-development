package whistapp.ui;

import org.junit.jupiter.api.*;
import whistapp.application.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CLI class
 */
class CLITest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    private static class TestCLI extends CLI {
        public TestCLI(Controller controller) {
            super(controller);
        }

        // public wrappers so tests can call protected methods
        public String callGetInputString(String prompt) {
            return getInputString(prompt);
        }

        public int callGetInputInt(String prompt) {
            return getInputInt(prompt);
        }

        public <T> T callGetChoice(String question, T[] options) {
            return getChoice(question, options);
        }

        public boolean callGetYesNo(String question) {
            return getYesNo(question);
        }

        public <T> T[] callGetChoices(String question, T[] options) {
            return getChoices(question, options);
        }
    }

    private TestCLI buildCLI(String simulatedInput) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        TestCLI cli = new TestCLI(new Controller());
        cli.scanner = scanner;
        return cli;
    }

    private String getOutput() {
        return outContent.toString();
    }

    @Test
    @DisplayName("getInputString")
    void testGetInputString() {
        String input = "hello world\n";
        TestCLI cli = buildCLI(input);

        String result = cli.callGetInputString("Enter text");
        String output = getOutput();

        assertEquals("hello world", result, "Should return string entered by user");
        assertTrue(output.contains("Enter text: "), "Should prompt with provided prompt");
    }

    @Test
    @DisplayName("getInputInt")
    void testGetInputInt() {
        String input = "42\n";
        TestCLI cli = buildCLI(input);

        int result = cli.callGetInputInt("Enter number");
        String output = getOutput();

        assertEquals(42, result, "Should return number entered by user");
        assertTrue(output.contains("Enter number: "), "Should prompt with provided prompt");
    }

    @Test
    @DisplayName("getChoice")
    void testGetChoice_valid() {
        String input = "2\n";
        TestCLI cli = buildCLI(input);

        String[] options = {"first", "second", "third"};
        String choice = cli.callGetChoice("Pick one", options);
        String output = getOutput();

        assertEquals("second", choice, "Should return choice made by user");
        // ensure option texts are printed
        assertTrue(output.contains("first"), "Should print all options");
        assertTrue(output.contains("second"), "Should print all options");
        assertTrue(output.contains("third"), "Should print all options");
    }

    @Test
    @DisplayName("getChoice retry")
    void testGetChoice_invalidThenValid() {
        // first enter an invalid choice (3), then press enter (\n), then a valid (1)
        String input = "3\n\n1\n";
        TestCLI cli = buildCLI(input);

        String[] options = {"optA", "optB"};
        String choice = cli.callGetChoice("Choose", options);
        String output = getOutput();

        assertEquals("optA", choice, "Should output the choice made by the user");
        assertTrue(output.contains("Invalid choice"), "Should print invalid choice message on bad input");
    }

    @Test
    @DisplayName("getYesNo")
    void testGetYesNo_expectedBehavior() {
        // First test selecting "No" (0) -> expect false
        TestCLI cliNo = buildCLI("1\n");
        boolean no = cliNo.callGetYesNo("Confirm?");
        // Second test selecting "Yes" (1) -> expect true
        TestCLI cliYes = buildCLI("2\n");
        boolean yes = cliYes.callGetYesNo("Confirm?");

        assertFalse(no, "Selecting option 1 (No) should return false");
        assertTrue(yes, "Selecting option 2 (Yes) should return true");
    }

    @Test
    @DisplayName("getChoices")
    void testGetChoices_basic() {
        // Toggle option 1 and 3, then finish with 0
        String input = "1\n3\n0\n";
        TestCLI cli = buildCLI(input);

        String[] options = {"A", "B", "C"};
        String[] result = cli.callGetChoices("Select items", options);
        String output = getOutput();

        // expected indices: 0 and 2
        assertArrayEquals(new String[]{"A", "C"}, result, "Should return all chosen values");
        assertTrue(output.contains("Complete selection"), "Should show complete selection option");
        assertTrue(output.contains("A"), "Should show all options");
        assertTrue(output.contains("B"), "Should show all options");
        assertTrue(output.contains("C"), "Should show all options");
    }

    @Test
    @DisplayName("getChoices")
    void testGetChoices_invalidThenToggle() {
        // invalid "5" (outside range), then press enter (\n), then valid 1 then finish
        String input = "5\n\n1\n0\n";
        TestCLI cli = buildCLI(input);

        String[] options = {"Alpha", "Beta"};
        String[] result = cli.callGetChoices("Choose", options);
        String output = getOutput();

        assertArrayEquals(new String[]{"Alpha"}, result, "Should return all chosen values");
        assertTrue(output.contains("Invalid choice"), "Should print invalid choice on out-of-range selection");
    }

}