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
 * Tests for the PlayGameCLI user interaction flow.
 *
 * <p>Strategy:
 * <ul>
 *   <li><b>Unit tests</b> – cover {@code showIntro()} and {@code startNewGame()}
 *       directly (cheap, deterministic).</li>
 *   <li><b>End-to-end tests</b> – feed the full {@code show()} loop with canned
 *       input that creates a 4-bot game so we never have to manually "play" cards
 *       or bid, avoiding infinite-loop hangs.</li>
 * </ul>
 *
 * <p>All human-player tests that involve rounds use 0 real players (= 4 bots)
 * so that the bidding + trick phases proceed autonomously.
 */
class PlayGameCLITest {

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

    /* ---------------------------------------------------------------------- */
    /*  Helpers                                                                */
    /* ---------------------------------------------------------------------- */

    private PlayGameCLI buildCLI(String simulatedInput) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        PlayGameCLI pgcli = new PlayGameCLI(new Controller());
        pgcli.scanner = scanner;
        return pgcli;
    }

    private String getOutput() {
        return outContent.toString();
    }

    /**
     * Returns the input tokens needed for one full autonomous round.
     *
     * <p>Even with 0 real players the bidding phase can still prompt the user
     * when a bot is the lone proposer. We provide 30 cycles of:
     *   \n   – satisfies getInputString (lone-proposer getReady)
     *   2\n  – satisfies getYesNo "Do you want to play alone?" → Yes (breaks loop)
     * followed by 60 \n tokens for "Press enter to continue" after-bidding-summary
     * and all 13 trick completions.
     */
    private static String oneRoundInputs() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            sb.append("\n"); // lone-proposer getReady
            sb.append("2\n"); // Yes – play alone (breaks outer bidding loop)
        }
        // 60 newlines cover: bidding summary enter + 13 tricks × "press enter"
        for (int i = 0; i < 60; i++) {
            sb.append("\n");
        }
        return sb.toString();
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
            if (e.getCause() instanceof RuntimeException re)
                throw re;
            if (e.getCause() instanceof Error err)
                throw err;
            fail("Unexpected checked exception", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted");
        } finally {
            exec.shutdownNow();
        }
    }

    /* ====================================================================== */
    /*  UNIT TESTS – showIntro()                                               */
    /* ====================================================================== */

    @Test
    @DisplayName("showIntro prints virtual game message")
    void testShowIntro() {
        PlayGameCLI cli = buildCLI("\n");
        cli.showIntro();

        String output = getOutput();
        assertTrue(output.contains("Playing a virtual game of Whist."),
                "Should print the virtual game intro");
    }

    /* ====================================================================== */
    /*  UNIT TESTS – startNewGame() (player + bot registration)                */
    /* ====================================================================== */

    @Test
    @DisplayName("startNewGame – 2 humans + 2 low bots")
    void testStartNewGame_twoHumans_twoLowBots() {
        // 2 real players, names "Alice" and "Bob", then bot1=Low, bot2=Low
        String input = String.join("\n",
                "2", // nbOfRealPlayers
                "Alice", // player #1
                "Bob", // player #2
                "1", // Bot 1 -> Low Bot
                "1" // Bot 2 -> Low Bot
        ) + "\n";

        PlayGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Players registered:"), "Should show confirmation");
        assertTrue(output.contains("Alice"), "Should list Alice");
        assertTrue(output.contains("Bob"), "Should list Bob");
        assertTrue(output.contains("LowBotA"), "Should list LowBotA");
        assertTrue(output.contains("LowBotB"), "Should list LowBotB");
    }

    @Test
    @DisplayName("startNewGame – 2 humans + 2 high bots")
    void testStartNewGame_twoHumans_twoHighBots() {
        String input = String.join("\n",
                "2",
                "Alice",
                "Bob",
                "2", // Bot 1 -> High Bot
                "2" // Bot 2 -> High Bot
        ) + "\n";

        PlayGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Players registered:"));
        assertTrue(output.contains("Alice"));
        assertTrue(output.contains("Bob"));
        assertTrue(output.contains("HighBotA"));
        assertTrue(output.contains("HighBotB"));
    }

    @Test
    @DisplayName("startNewGame – 2 humans + 1 low + 1 high bot")
    void testStartNewGame_twoHumans_mixedBots() {
        String input = String.join("\n",
                "2",
                "Alice",
                "Bob",
                "1", // Bot 1 -> Low Bot
                "2" // Bot 2 -> High Bot
        ) + "\n";

        PlayGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Players registered:"));
        assertTrue(output.contains("LowBotA"));
        assertTrue(output.contains("HighBotA"));
    }

    @Test
    @DisplayName("startNewGame – 1 human + 3 bots")
    void testStartNewGame_oneHuman_threeBots() {
        String input = String.join("\n",
                "1",
                "Solo",
                "1", // Bot 1 -> Low
                "2", // Bot 2 -> High
                "1" // Bot 3 -> Low
        ) + "\n";

        PlayGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Players registered:"));
        assertTrue(output.contains("Solo"));
        assertTrue(output.contains("LowBotA"));
        assertTrue(output.contains("HighBotA"));
        assertTrue(output.contains("LowBotB"));
    }

    @Test
    @DisplayName("startNewGame – 4 humans, no bots")
    void testStartNewGame_fourHumans() {
        String input = String.join("\n",
                "4",
                "Alice",
                "Bob",
                "Charlie",
                "Diana") + "\n";

        PlayGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Players registered:"));
        assertTrue(output.contains("Alice"));
        assertTrue(output.contains("Bob"));
        assertTrue(output.contains("Charlie"));
        assertTrue(output.contains("Diana"));
    }

    @Test
    @DisplayName("startNewGame – invalid player count then valid")
    void testStartNewGame_invalidPlayerCount_thenValid() {
        String input = String.join("\n",
                "5", // invalid (> 4)
                "2", // valid
                "Alice",
                "Bob",
                "1", // Low Bot
                "1" // Low Bot
        ) + "\n";

        PlayGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Invalid number of real players."),
                "Should warn about invalid count");
        assertTrue(output.contains("Players registered:"),
                "Should eventually succeed");
    }

    @Test
    @DisplayName("startNewGame – negative player count then valid")
    void testStartNewGame_negativePlayerCount_thenValid() {
        String input = String.join("\n",
                "-1", // invalid (< 0)
                "2",
                "Alice",
                "Bob",
                "1",
                "2") + "\n";

        PlayGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Invalid number of real players."));
        assertTrue(output.contains("Players registered:"));
    }

    @Test
    @DisplayName("startNewGame – duplicate names triggers retry")
    void testStartNewGame_duplicateNames_retry() {
        // First attempt: "Alice", "Alice" → duplicate → error from controller
        // Then bot choices: 1, 1
        // Second attempt: "Alice", "Bob", bots: 1, 1
        String input = String.join("\n",
                "2",
                "Alice",
                "Alice",
                "1",
                "1",
                // retry
                "Alice",
                "Bob",
                "1",
                "1") + "\n";

        PlayGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        // The controller should throw on duplicate names, causing an error message
        assertTrue(output.contains("Error") || output.contains("Invalid"),
                "Should show error for duplicates");
        assertTrue(output.contains("Players registered:"),
                "Should succeed on retry");
    }

    @Test
    @DisplayName("startNewGame – non-alpha name triggers retry")
    void testStartNewGame_nonAlphaName_retry() {
        // "Alice1" is non-alpha → invalid → retry
        String input = String.join("\n",
                "2",
                "Alice1",
                "Bob",
                "1",
                "1",
                // retry
                "Alice",
                "Bob",
                "1",
                "1") + "\n";

        PlayGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Error") || output.contains("Invalid"),
                "Should show error for non-alpha name");
        assertTrue(output.contains("Players registered:"),
                "Should succeed on retry");
    }

    /* ====================================================================== */
    /*  END-TO-END TESTS – full show() with 0 real players (4 bots)           */
    /*                                                                         */
    /*  With 0 real players the entire bidding + trick phase is autonomous.    */
    /*  We only need to supply input for:                                       */
    /*    - "How many real players": 0                                         */
    /*    - Bot difficulty choices: 4 × "1" or "2"                             */
    /*    - Enough "\n" for "Press enter to continue" prompts                  */
    /*    - "Do you want to play another round?" → "1" (No)                   */
    /*    - "Do you want to play another game?" → "1" (No)                    */
    /* ====================================================================== */

    @Test
    @DisplayName("E2E - full game with 0 humans (all bots), 1 round, no replay")
    void testE2E_allBots_oneRound_noReplay() {
        // 0 real players ⇒ startNewGame() returns immediately (if-block skipped).
        // However, showAllRounds() still calls startNewGame → which is 0 real players.
        // The show() flow: showIntro → startNewGame → showAllRounds → showGameResults → play again?
        //
        // With 0 real players, startNewGame returns without prompting for bots (the if-block
        // around nbOfRealPlayers > 0 skips everything). This means controller.startNewPlayGame
        // is never called, so controller.nextRound() etc. will fail.
        //
        // Therefore, the minimum viable E2E test uses at least 1 real player + 3 bots,
        // where the real player bids "1" (Pass) and plays card "1" (always first allowed).
        // But the bidding loop requires at least one non-pass bid from some player, and bots
        // handle that.
        //
        // Actually, looking more carefully: with 0 real players, the startNewGame if-block
        // is entirely skipped, meaning the game is never created. So let's use 0 humans
        // but still call startNewPlayGame ourselves—wait, we can't in test.
        //
        // The cleanest E2E uses 1 human + 3 bots. The human needs to:
        //   - Bid: always "1" (Pass) – bots will pick something
        //   - Play cards: always "1" (first card offered)
        //   - Press enter at prompts
        //
        // 13 tricks × (1 human turn per trick) = 13 card plays.
        // Plus bidding (1 bid), plus ~15+ "press enter" prompts.
        // We provide LOTS of "1\n" and "\n" to cover everything.

        StringBuilder sb = new StringBuilder();
        // "How many real players": 1
        sb.append("0\n");
        // Bot difficulties: 3 bots
        sb.append("1\n"); // Bot 1 = Low
        sb.append("1\n"); // Bot 2 = Low
        sb.append("1\n"); // Bot 3 = Low
        sb.append("1\n"); // Bot 4 = Low

        sb.append(oneRoundInputs());
        sb.append("1\n"); // Another round? No
        sb.append("1\n"); // Another game? No

        PlayGameCLI cli = buildCLI(sb.toString());

        runWithTimeout(() -> cli.show(), 5);

        String output = getOutput();
        assertTrue(output.contains("Playing a virtual game of Whist."),
                "Should show intro");
        assertTrue(output.contains("Players registered:"),
                "Should register players");
        assertTrue(output.contains("LowBotA"),
                "Should list bot A");
        assertTrue(output.contains("Starting a new round!"),
                "Should start a round");
        assertTrue(output.contains("THE BIDDING PHASE HAS ENDED."),
                "Should complete bidding");
        assertTrue(output.contains("The trick is over."),
                "Should complete at least one trick");
        assertTrue(output.contains("Game results:"),
                "Should show game results");
    }

    @Test
    @DisplayName("E2E – full game with 4 high bots, 1 round")
    void testE2E_fourHighBots() {
        StringBuilder sb = new StringBuilder();
        sb.append("0\n"); // 0 real players
        sb.append("2\n"); // Bot 1 = High
        sb.append("2\n"); // Bot 2 = High
        sb.append("2\n"); // Bot 3 = High
        sb.append("2\n"); // Bot 4 = High

        sb.append(oneRoundInputs());
        sb.append("1\n"); // Another round? No
        sb.append("1\n"); // Another game? No

        System.out.println(sb.toString());
        System.out.println("test");
        PlayGameCLI cli = buildCLI(sb.toString());
        runWithTimeout(() -> cli.show(), 5);

        String output = getOutput();
        assertTrue(output.contains("HighBotA"));
        assertTrue(output.contains("HighBotB"));
        assertTrue(output.contains("HighBotC"));
        assertTrue(output.contains("Game results:"));
    }

    @Test
    @DisplayName("E2E – game results show points for each player")
    void testE2E_gameResults_showPoints() {
        StringBuilder sb = new StringBuilder();
        sb.append("0\n");
        sb.append("1\n").append("1\n").append("1\n").append("1\n"); // 4 low bots

        sb.append(oneRoundInputs());
        sb.append("1\n"); // Another round? No
        sb.append("1\n"); // Another game? No

        PlayGameCLI cli = buildCLI(sb.toString());
        runWithTimeout(() -> cli.show(), 5);

        String output = getOutput();
        assertTrue(output.contains("Game results:"));
        assertTrue(output.contains("points"), "Should display points in results");
    }

    @Test
    @DisplayName("E2E – current scores shown after round")
    void testE2E_roundScoresShown() {
        StringBuilder sb = new StringBuilder();
        sb.append("0\n");
        sb.append("1\n").append("1\n").append("1\n").append("1\n");

        sb.append(oneRoundInputs());
        sb.append("1\n"); // Another round? No
        sb.append("1\n"); // Another game? No

        PlayGameCLI cli = buildCLI(sb.toString());
        runWithTimeout(() -> cli.show(), 5);

        String output = getOutput();
        assertTrue(output.contains("Score earned/lost this round:"),
                "Should display round scores after the round");
    }

    @Test
    @DisplayName("E2E – dealer name is shown at round start")
    void testE2E_dealerNameShown() {
        StringBuilder sb = new StringBuilder();
        sb.append("0\n");
        sb.append("1\n").append("1\n").append("1\n").append("1\n");

        sb.append(oneRoundInputs());
        sb.append("1\n"); // Another round? No
        sb.append("1\n"); // Another game? No

        PlayGameCLI cli = buildCLI(sb.toString());
        runWithTimeout(() -> cli.show(), 5);

        String output = getOutput();
        assertTrue(output.contains("The dealer is:"),
                "Should display the dealer name");
    }

    @Test
    @DisplayName("E2E – bidding phase end is announced")
    void testE2E_biddingPhaseEndAnnounced() {
        StringBuilder sb = new StringBuilder();
        sb.append("0\n");
        sb.append("1\n").append("1\n").append("1\n").append("1\n");

        sb.append(oneRoundInputs());
        sb.append("1\n"); // Another round? No
        sb.append("1\n"); // Another game? No

        PlayGameCLI cli = buildCLI(sb.toString());
        runWithTimeout(() -> cli.show(), 5);

        String output = getOutput();
        assertTrue(output.contains("THE BIDDING PHASE HAS ENDED."));
        assertTrue(output.contains("Winning bid:"),
                "Should display the winning bid");
        assertTrue(output.contains("Declarer(s):"),
                "Should display the declarer(s)");
    }

    @Test
    @DisplayName("E2E – trick phase start is announced")
    void testE2E_trickPhaseStartAnnounced() {
        StringBuilder sb = new StringBuilder();
        sb.append("0\n");
        sb.append("1\n").append("1\n").append("1\n").append("1\n");

        sb.append(oneRoundInputs());
        sb.append("1\n"); // Another round? No
        sb.append("1\n"); // Another game? No

        PlayGameCLI cli = buildCLI(sb.toString());
        runWithTimeout(() -> cli.show(), 5);

        String output = getOutput();
        assertTrue(output.contains("Starting the trick playing phase of the round."));
    }

    @Test
    @DisplayName("E2E – trick winner is announced")
    void testE2E_trickWinnerAnnounced() {
        StringBuilder sb = new StringBuilder();
        sb.append("0\n");
        sb.append("1\n").append("1\n").append("1\n").append("1\n");

        sb.append(oneRoundInputs());
        sb.append("1\n"); // Another round? No
        sb.append("1\n"); // Another game? No

        PlayGameCLI cli = buildCLI(sb.toString());
        runWithTimeout(() -> cli.show(), 5);

        String output = getOutput();
        assertTrue(output.contains("Winner of this trick:"),
                "Should announce trick winner");
        assertTrue(output.contains("leads the next trick"),
                "Should indicate who leads next");
    }

    /* ====================================================================== */
    /*  UNIT TESTS – edge cases for startNewGame input validation              */
    /* ====================================================================== */

    @Test
    @DisplayName("startNewGame – non-integer input for player count retries")
    void testStartNewGame_nonIntegerPlayerCount() {
        // "abc" is not an integer → getInputInt will show "Invalid integer" and retry
        // Then provide valid "2" + names + bots
        String input = String.join("\n",
                "abc",
                "", // "Press enter to try again" from getInputInt
                "2",
                "Alice",
                "Bob",
                "1",
                "1") + "\n";

        PlayGameCLI cli = buildCLI(input);
        cli.startNewGame();

        String output = getOutput();
        assertTrue(output.contains("Invalid integer"),
                "Should warn about invalid integer");
        assertTrue(output.contains("Players registered:"),
                "Should eventually succeed");
    }
}