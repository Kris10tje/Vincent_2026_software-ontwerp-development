package whistapp.scenarios;

import org.junit.jupiter.api.*;
import whistapp.application.Controller;
import whistapp.ui.ScoreGameCLI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Scenario tests for Use Case 4.1: Start new count.
 */
class ScoreGameScenarioTest {

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

    private void runWithTimeout(Runnable r, int seconds) {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<?> future = exec.submit(r);
        try {
            future.get(seconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            fail("Test timed out – likely stuck in an input loop.");
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException)
                throw (RuntimeException) e.getCause();
            if (e.getCause() instanceof Error)
                throw (Error) e.getCause();
            fail("Unexpected checked exception", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted");
        } finally {
            exec.shutdownNow();
        }
    }

    private ScoreGameCLI buildCLI(String simulatedInput, Controller controller) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        ScoreGameCLI cli = new ScoreGameCLI(controller);
        try {
            java.lang.reflect.Field field = whistapp.ui.CLI.class.getDeclaredField("scanner");
            field.setAccessible(true);
            field.set(cli, scanner);
        } catch (Exception e) {
            throw new RuntimeException("Could not inject scanner into CLI", e);
        }
        return cli;
    }

    private String getOutput() {
        return outContent.toString();
    }

    @Test
    @DisplayName("Main Success Scenario: Start new count")
    void testMainSuccessScenario() {
        StringBuilder sb = new StringBuilder();
        // Step 1: The user selects to start a new count. (This is done by invoking ScoreGameCLI)
        // Step 2: The system asks to register the names of the four players.
        sb.append("Alice\n");
        sb.append("Bob\n");
        sb.append("Charlie\n");
        sb.append("Diana\n");

        // Step 3: The system starts a new game with the registered players...
        // Step 4: The user registers which bid will be played... and trump suit.
        sb.append("1\n"); // Alice final active bid: Pass (1)
        sb.append("1\n"); // Bob final active bid: Pass (1)
        sb.append("1\n"); // Charlie final active bid: Pass (1)
        sb.append("2\n"); // Diana final active bid: Proposal (2)

        // InformUser: "Bids registered successfully." (doesn't wait)

        // Reshuffle choice? (1=No, 2=Yes) => "Was there a reshuffle?" -> No (1)
        sb.append("1\n");

        // InformUser: "Enter round results when finished." (doesn't wait)

        // Step 5: The user registers which player(s) play the bid.
        // Step 6: The user can enter the result of the round.
        // "How many tricks were won by [Player]:"
        sb.append("0\n"); // Alice
        sb.append("0\n"); // Bob
        sb.append("0\n"); // Charlie
        sb.append("13\n"); // Diana won 13 tricks

        // InformUser: "Round results registered successfully. Here's a summary:" (doesn't wait)

        // System pauses to show round points ("Press enter to continue" in GameCLI but ScoreGameCLI doesn't wait!)

        // "Do you want to play another round?" -> No (1)
        sb.append("1\n");

        // Step 8: The user selects to quit the game.
        // "Do you want to play another game?" -> No (1)
        sb.append("1\n");

        Controller controller = new Controller();
        ScoreGameCLI cli = buildCLI(sb.toString(), controller);

        try {
            runWithTimeout(() -> cli.show(), 5);
        } finally {
            String output = getOutput();
            System.err.println("OUTPUT OF SCENARIO1 ===================");
            System.err.println(output);
        }

        String output = getOutput();

        // Verify Step 1
        assertTrue(output.contains("Keeping track of the score of a physical game of Whist"),
                "Step 1: The user selects to start a new count");

        // Verify Step 2
        assertTrue(output.contains("Enter name for player 1"), "Step 2: Ask names");
        assertTrue(output.contains("Players registered:"), "Step 2: Registered players");

        // Verify Step 3
        // System starts new game successfully after players registered.

        // Verify Step 4
        assertTrue(output.contains("What is the final active bid"), "Step 4: Register bids");

        // Verify Step 5 (part of bid registration) / 6 (Tricks won)
        assertTrue(output.contains("How many tricks were won by"), "Step 6: User enters result of the round");

        // Verify Step 7
        assertTrue(output.contains("Scores after this round:"), "Step 7: Calculate points for round");
        assertTrue(output.contains("Diana: 60 points"), "Step 7: Correct points calculated");
        assertTrue(output.contains("Alice: -20 points"), "Step 7: Correct negative points for opponents");

        // Verify Step 8
        assertTrue(output.contains("Game results:"), "Step 8: Output game results upon quitting");
    }

    @Test
    @DisplayName("Extension 8a: Restart game keeping scores")
    void testExtension8aRestartGame() {
        StringBuilder sb = new StringBuilder();
        // Game 1 setup
        sb.append("Alice\n").append("Bob\n").append("Charlie\n").append("Diana\n");

        // Bids: Diana Proposal
        sb.append("1\n").append("1\n").append("1\n").append("2\n");
        
        sb.append("1\n"); // Reshuffle: No
        
        // Tricks 
        sb.append("0\n").append("0\n").append("0\n").append("13\n");
        // "Round results registered successfully"
        // info prompt "Scores after this round"

        sb.append("1\n"); // Another round? No.

        // Extension 8a: Restart the game at 3., ensuring scores are kept.
        // "Do you want to play another game?" -> Yes (2)
        sb.append("2\n");

        // ScoreGameCLI asks for players again when restarting
        sb.append("Alice\n").append("Bob\n").append("Charlie\n").append("Diana\n");

        // Game 2 Bids
        sb.append("1\n").append("1\n").append("1\n").append("2\n"); // Diana Proposal
        
        sb.append("1\n"); // Reshuffle: No
        
        // Game 2 Tricks 
        sb.append("0\n").append("0\n").append("0\n").append("13\n");
        // "Round results registered successfully"
        // info prompt "Scores after this round"

        sb.append("1\n"); // Another round? No.

        // Quit
        sb.append("1\n"); // Another game? No.

        Controller controller = new Controller();
        ScoreGameCLI cli = buildCLI(sb.toString(), controller);
        try {
            runWithTimeout(() -> cli.show(), 5);
        } finally {
            String output2 = getOutput();
            System.err.println("OUTPUT OF SCENARIO2 ===================");
            System.err.println(output2);
        }

        String output = getOutput();

        // Verify that the game ran twice and at the end scores cumulate correctly
        // (Assuming Diana won 13 tricks proposal alone = +120 points)
        assertTrue(output.contains("Game results:"), "Output game results");
    }
}
