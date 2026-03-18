package whistapp.scenarios;

import org.junit.jupiter.api.*;
import whistapp.application.Controller;
import whistapp.ui.PlayGameCLI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Scenario tests for Use Case 4.2: Play a virtual game.
 *
 * <p>Strategy: we use all-bot games (0 real players) so bidding and card-play
 * are fully autonomous. However, the bidding phase can still prompt the user
 * (even for all-bot games) if a bot is a lone proposer – the CLI asks
 * "Press enter when X is ready to choose to play alone or pass."
 * We provide a generous buffer of newline inputs so these prompts are satisfied.
 * The lone-proposer Yes/No answer is always "1\n" (No = pass the bid alone prompt).
 */
class PlayGameScenarioTest {

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

    // ── helpers ────────────────────────────────────────────────────────────────

    private String getOutput() {
        return outContent.toString();
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
            if (e.getCause() instanceof RuntimeException) throw (RuntimeException) e.getCause();
            if (e.getCause() instanceof Error) throw (Error) e.getCause();
            fail("Unexpected checked exception", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted");
        } finally {
            exec.shutdownNow();
        }
    }

    /**
     * Builds a PlayGameCLI with a simulated input stream injected via reflection.
     */
    private PlayGameCLI buildCLI(String simulatedInput, Controller controller) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        PlayGameCLI cli = new PlayGameCLI(controller);
        try {
            java.lang.reflect.Field field = whistapp.ui.CLI.class.getDeclaredField("scanner");
            field.setAccessible(true);
            field.set(cli, scanner);
        } catch (Exception e) {
            throw new RuntimeException("Could not inject scanner into CLI", e);
        }
        return cli;
    }

    /**
     * Build inputs for one full round of an all-bot game.
     *
     * <p>Even in an all-bot game, the bidding phase may fire
     * "Press enter when X is ready to choose to play alone or pass." + Yes/No prompt
     * whenever a bot ends up as the lone proposer.
     * We provide a very large buffer of alternating getInputString + getYesNo tokens so
     * any combination of proposer-prompt firings is handled.
     *
     * <p>Token order per cycle:
     *   \n   → satisfies getInputString("Press enter when X is ready…")
     *   1\n  → satisfies getYesNo("Do you want to play the bid alone?") → No
     *
     * After bidding:
     *   \n   → "Press enter to continue" after bidding summary
     *
     * Per trick:
     *   \n   → "Press enter to continue" after trick
     */
    private static String oneRoundInputs() {
        StringBuilder sb = new StringBuilder();
        // Provide a very large buffer (30 cycles) for the lone-proposer bidding prompts
        for (int i = 0; i < 30; i++) {
            sb.append("\n");  // getInputString for lone-proposer getReady
            sb.append("2\n"); // getYesNo "Do you want to play alone?" → Yes (2) – breaks loop
        }
        // "Press enter to continue" after bidding summary
        sb.append("\n");
        // 13 tricks
        for (int i = 0; i < 13; i++) {
            sb.append("\n");
        }
        return sb.toString();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MAIN SUCCESS SCENARIO – all-bot game, one round, then quit
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Main success scenario: play one full round with 4 bots")
    void testMainSuccessScenario() {

        StringBuilder sb = new StringBuilder();

        // Step 2: 0 real players → all bots
        sb.append("0\n");

        // Step 2 (bots): difficulty for each of the 4 bots (1=Low, 2=High)
        sb.append("1\n"); // Bot 1 → Low
        sb.append("2\n"); // Bot 2 → High
        sb.append("2\n"); // Bot 3 → High
        sb.append("2\n"); // Bot 4 → High

        // One round (bidding + tricks)
        sb.append(oneRoundInputs());

        // Another round? → No (1)
        sb.append("1\n");

        // Another game? → No (1)
        sb.append("1\n");

        Controller controller = new Controller();
        PlayGameCLI cli = buildCLI(sb.toString(), controller);

        runWithTimeout(() -> cli.show(), 30);

        String output = getOutput();

        // Step 2
        assertTrue(output.contains("How many real players will play in this game?"),
                "Step 2: system asks number of real players");
        assertTrue(output.contains("Choose the difficulty for Bot"),
                "Step 2: system asks bot difficulty");

        // Step 3: players registered
        assertTrue(output.contains("Players registered:"), "Step 3: players are listed");

        // Step 4: bidding phase outcome
        assertTrue(output.contains("THE BIDDING PHASE HAS ENDED."), "Step 4: bidding phase ends");
        assertTrue(output.contains("Winning bid:"), "Step 4: winning bid is shown");
        assertTrue(output.contains("Declarer(s):"), "Step 4: declarers are shown");

        // Step 5: trick-playing phase
        assertTrue(output.contains("Starting the trick playing phase of the round."),
                "Step 5: trick phase starts");
        assertTrue(output.contains("The trick is over."), "Step 5: at least one trick resolves");
        assertTrue(output.contains("Winner of this trick:"), "Step 5: trick winner shown");

        // Step 6: round points
        assertTrue(output.contains("Score earned/lost this round:"),
                "Step 6: round scores are shown");

        // Step 8: game results
        assertTrue(output.contains("points"), "Step 8: game result points shown");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // NEGATIVE SCENARIO 3a – invalid number of real players (step 2 validation)
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("3a: Invalid number of real players is rejected")
    void testInvalidNumberOfRealPlayers() {

        StringBuilder sb = new StringBuilder();

        // Step 2: invalid inputs first, then valid
        sb.append("5\n");   // Too many (only 4 players total) → rejected
        sb.append("-1\n");  // Negative → rejected
        sb.append("0\n");   // Valid: 0 real players

        // Bot difficulties
        sb.append("1\n").append("1\n").append("1\n").append("1\n");

        sb.append(oneRoundInputs());

        // Another round? No
        sb.append("1\n");
        // Another game? No
        sb.append("1\n");

        Controller controller = new Controller();
        PlayGameCLI cli = buildCLI(sb.toString(), controller);

        runWithTimeout(() -> cli.show(), 30);

        String output = getOutput();

        // Error shown
        assertTrue(output.contains("Invalid number of real players."),
                "3a: error shown for invalid real-player count");
        // Game still completed
        assertTrue(output.contains("Players registered:"),
                "3a: game proceeds after valid input");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // EXTENSION – play multiple rounds before quitting
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Extension: play two rounds then quit")
    void testPlayTwoRounds() {

        StringBuilder sb = new StringBuilder();

        // 0 real players – 4 Low bots
        sb.append("0\n");
        sb.append("1\n").append("1\n").append("1\n").append("1\n");

        // Round 1
        sb.append(oneRoundInputs());

        // Another round? → Yes (2)
        sb.append("2\n");

        // Round 2
        sb.append(oneRoundInputs());

        // Another round? → No (1)
        sb.append("1\n");

        // Another game? → No (1)
        sb.append("1\n");

        Controller controller = new Controller();
        PlayGameCLI cli = buildCLI(sb.toString(), controller);

        runWithTimeout(() -> cli.show(), 60);

        String output = getOutput();

        // The bidding-phase summary must appear at least twice (once per round)
        int count = 0;
        int idx = 0;
        while ((idx = output.indexOf("THE BIDDING PHASE HAS ENDED.", idx)) != -1) {
            count++;
            idx++;
        }
        assertTrue(count >= 2, "Extension: bidding phase occurred at least twice (two rounds played)");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // EXTENSION – restart game
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Extension: restart game and verify a second game is played")
    void testRestartGame() {

        StringBuilder sb = new StringBuilder();

        // Game 1 – 0 real players, 4 Low bots
        sb.append("0\n");
        sb.append("1\n").append("1\n").append("1\n").append("1\n");

        sb.append(oneRoundInputs());

        // Another round? No
        sb.append("1\n");

        // Another game? Yes (2)
        sb.append("2\n");

        // Game 2 – 0 real players, 4 Low bots
        sb.append("0\n");
        sb.append("1\n").append("1\n").append("1\n").append("1\n");

        sb.append(oneRoundInputs());

        // Another round? No
        sb.append("1\n");

        // Another game? No
        sb.append("1\n");

        Controller controller = new Controller();
        PlayGameCLI cli = buildCLI(sb.toString(), controller);

        runWithTimeout(() -> cli.show(), 60);

        String output = getOutput();

        // Both games must have registered players
        int registeredCount = 0;
        int idx = 0;
        while ((idx = output.indexOf("Players registered:", idx)) != -1) {
            registeredCount++;
            idx++;
        }
        assertTrue(registeredCount >= 2, "Extension: two separate games were started");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MIXED: one real human player + bots
    // Verifies that the human player name is registered and the getReady prompt fires.
    // We provide bidding tokens but intentionally let the game stall on trick-play
    // (since card positions are non-deterministic). The output is captured regardless.
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Human player name registration and getReady prompt are shown")
    void testOneRealPlayerWithBots() {

        StringBuilder sb = new StringBuilder();

        // Step 2: 1 real player, 3 bots
        sb.append("1\n");

        // Real player name
        sb.append("Vincent\n");

        // Bot difficulties for bots 2, 3, 4 (all Low)
        sb.append("1\n");
        sb.append("1\n");
        sb.append("1\n");

        // ── Bidding phase ─────────────────────────────────────────────────────
        // Vincent bids once; afterwards bots finish autonomously.
        // Buffer: Vincent getReady + Pass bid
        for (int i = 0; i < 3; i++) {
            sb.append("\n");  // getReady for Vincent's bid turn
            sb.append("1\n"); // Vincent bids Pass
        }
        // Lone-proposer buffer (Yes = play alone → breaks bidding loop)
        for (int i = 0; i < 5; i++) {
            sb.append("\n");  // lone-proposer getReady
            sb.append("2\n"); // play alone → Yes
        }
        // "Press enter to continue" after bidding summary
        sb.append("\n");

        Controller controller = new Controller();
        PlayGameCLI cli = buildCLI(sb.toString(), controller);

        // Allow up to 10 s; the game will stall when it needs a card choice,
        // but we only assert output that is produced BEFORE card play.
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<?> future = exec.submit(() -> cli.show());
        try {
            future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // Expected: game stalls when card selection is needed.
            // We cancel and verify output produced up to that point.
            future.cancel(true);
        } catch (Exception e) {
            // Input exhausted → same situation, output still captured
        } finally {
            exec.shutdownNow();
        }

        String output = getOutput();

        // Step 2: human name registered
        assertTrue(output.contains("Vincent"), "Vincent's name appears in the output");
        assertTrue(output.contains("Players registered:"),
                "Step 3: Human player was registered successfully");

        // Step 3: human-specific getReady prompt must appear in output
        assertTrue(output.contains("Press enter when Vincent is ready."),
                "Step 3: getReady prompt is shown for human player");

        // Step 4: bidding phase completes before card play
        assertTrue(output.contains("THE BIDDING PHASE HAS ENDED."),
                "Step 4: Bidding phase completed successfully");
    }
}

