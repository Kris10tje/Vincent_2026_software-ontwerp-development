package whistapp.ui;

import org.junit.jupiter.api.*;
import whistapp.application.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ScoreGameCLI user interaction flow.
 */
class ScoreGameCLITest {

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

    /**
     * Run a Runnable with a timeout so a stuck input loop does not hang the
     * whole test suite.
     */
    private void runWithTimeout(Runnable r, int seconds) {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<?> future = exec.submit(r);
        try {
            future.get(seconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            fail("Test timed out – likely stuck in an input loop.");
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException re) throw re;
            if (e.getCause() instanceof Error err) throw err;
            fail("Unexpected checked exception", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted");
        } finally {
            exec.shutdownNow();
        }
    }

    /* ---------------------------------------------------------------------- */
    /*  Helper: build a ScoreGameCLI with canned input                        */
    /* ---------------------------------------------------------------------- */

    private ScoreGameCLI buildCLI(String simulatedInput) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        ScoreGameCLI sgcli = new ScoreGameCLI(new Controller());
        sgcli.scanner = scanner;
        return sgcli;
    }

    private String getOutput() {
        return outContent.toString();
    }

    /* ---------------------------------------------------------------------- */
    /*  Tests for player name collection                                       */
    /* ---------------------------------------------------------------------- */

    @Test // Collecting 4 unique player names prints confirmation
    @DisplayName("4 unique")
    void testPlayerNames_fourUniquePlayers() {
        String input = String.join("\n", "Alice", "Bob", "Charlie", "Diana") + "\n";

        System.out.println(input);

        ScoreGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Alice"), "Output should contain player name Alice");
        assertTrue(output.contains("Bob"), "Output should contain player name Bob");
        assertTrue(output.contains("Charlie"), "Output should contain player name Charlie");
        assertTrue(output.contains("Diana"), "Output should contain player name Diana");
        assertTrue(output.contains("Players registered:"), "Should display registered players");
    }

    @Test // Collecting 4 player names with duplicate prints failure
    @DisplayName("duplicate fail")
    void testPlayerNames_duplicateFail() {
        String input = String.join("\n", "Alice", "Bob", "Bob", "Diana") + "\n";

        ScoreGameCLI cli = buildCLI(input);
        assertThrows(Exception.class, () -> cli.startNewGame());

        String output = getOutput();
        assertTrue(output.contains("Invalid player names"), "Should display failure message");
        assertFalse(output.contains("Alice"), "Output should contain player name Alice");
        assertFalse(output.contains("Bob"), "Output should contain player name Bob");
        assertFalse(output.contains("Diana"), "Output should contain player name Diana");
        assertFalse(output.contains("Players registered:"), "Should display registered players");
    }

    @Test // Collecting 4 player names with duplicate allows retry
    @DisplayName("duplicate retry")
    void testPlayerNames_duplicateRetry() {
        String input = String.join("\n", "Alice", "Bob", "Bob", "Diana") + "\n"
                + String.join("\n", "Alice", "Bob", "Charlie", "Diana") + "\n";

        ScoreGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Invalid player names"), "Should display failure message");
        assertTrue(output.contains("Alice"), "Output should contain player name Alice");
        assertTrue(output.contains("Bob"), "Output should contain player name Bob");
        assertTrue(output.contains("Charlie"), "Output should contain player name Charlie");
        assertTrue(output.contains("Diana"), "Output should contain player name Diana");
        assertTrue(output.contains("Players registered:"), "Should display registered players");
    }

    @Test // Collecting 4 player names with space prints failure
    @DisplayName("non alpha fail")
    void testPlayerNames_spaceFail() {
        String input = String.join("\n", "Alic e", "Bob", "Charlie", "Diana") + "\n";

        ScoreGameCLI cli = buildCLI(input);
        assertThrows(Exception.class, () -> cli.startNewGame());

        String output = getOutput();
        assertTrue(output.contains("Invalid player names"), "Should display failure message");
        assertFalse(output.contains("Alic e"), "Output should not contain player name Alice");
        assertFalse(output.contains("Bob"), "Output should not contain player name Bob");
        assertFalse(output.contains("Diana"), "Output should not contain player name Diana");
        assertFalse(output.contains("Players registered:"), "Should not display registered players");
    }

    @Test // Collecting 4 player names with number allows retry
    @DisplayName("non alpha retry")
    void testPlayerNames_numberRetry() {
        String input = String.join("\n", "Alice1", "Bob", "Charlie", "Diana") + "\n"
                + String.join("\n", "Alice", "Bob", "Charlie", "Diana") + "\n";

        ScoreGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Invalid player names"), "Should display failure message");
        assertFalse(output.contains("Alice1"), "Output should not contain player name Alice1");
        assertTrue(output.contains("Alice"), "Output should contain player name Alice");
        assertTrue(output.contains("Bob"), "Output should contain player name Bob");
        assertTrue(output.contains("Charlie"), "Output should contain player name Charlie");
        assertTrue(output.contains("Diana"), "Output should contain player name Diana");
        assertTrue(output.contains("Players registered:"), "Should display registered players");
    }

    @Test
    @DisplayName("E2E – score game")
    void testE2E_play_score_game_one_round() {
        StringBuilder sb = new StringBuilder();
        sb.append("PlayerA\n").append("PlayerB\n").append("PlayerC\n").append("PlayerD\n");

        // All pass, one proposal alone
        sb.append("1\n").append("1\n").append("1\n").append("2\n");

        sb.append("2\n"); // there was a reshuffle

        // Player D won all tricks
        sb.append("0\n").append("0\n").append("0\n").append("13\n");

        // No new round, no new game
        sb.append("1\n").append("1\n");

        ScoreGameCLI cli = buildCLI(sb.toString());
        runWithTimeout(() -> cli.show(), 5);

        String output = getOutput();
        assertTrue(output.contains("Enter name for player 1"), "Should ask for every players name");
        assertTrue(output.contains("Enter name for player 2"), "Should ask for every players name");
        assertTrue(output.contains("Enter name for player 3"), "Should ask for every players name");
        assertTrue(output.contains("Enter name for player 4"), "Should ask for every players name");
        assertTrue(output.contains("Players registered"), "Should show confirmation message");
        assertTrue(output.contains("What is the final active bid for PlayerA"), "Should ask for every players bid");
        assertTrue(output.contains("What is the final active bid for PlayerB"), "Should ask for every players bid");
        assertTrue(output.contains("What is the final active bid for PlayerC"), "Should ask for every players bid");
        assertTrue(output.contains("What is the final active bid for PlayerD"), "Should ask for every players bid");
        assertTrue(output.contains("Was there a reshuffle"), "Should ask for the occurence of a reshuffle");
        assertTrue(output.contains("How many tricks were won by PlayerA"), "Should ask for the amount of tricks for every player");
        assertTrue(output.contains("How many tricks were won by PlayerB"), "Should ask for the amount of tricks for every player");
        assertTrue(output.contains("How many tricks were won by PlayerC"), "Should ask for the amount of tricks for every player");
        assertTrue(output.contains("How many tricks were won by PlayerD"), "Should ask for the amount of tricks for every player");
        assertTrue(output.contains("Scores after this round"), "Should display intermediate scores");
        assertTrue(output.contains("Do you want to play another round?"), "Should ask for another round");
        assertTrue(output.contains("Game results"), "Should display game results");
        assertTrue(output.contains("PlayerA: -40 points"), "Should display correct game results");
        assertTrue(output.contains("PlayerB: -40 points"), "Should display correct game results");
        assertTrue(output.contains("PlayerC: -40 points"), "Should display correct game results");
        assertTrue(output.contains("PlayerD: 120 points"), "Should display correct game results");
    }
}
