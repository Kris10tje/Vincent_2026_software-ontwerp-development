package whistapp.domain.players;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Dedicated unit tests for Player names.
 */
class PlayerNamesTest {

    /* -------------------------------------------------------------------------- */
    /*                         playersValid – valid cases                         */
    /* -------------------------------------------------------------------------- */

    @Test
    @DisplayName("playersValid: four unique alpha-only names")
    void testPlayersValid_fourUniqueNames() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("Alice", "Bob", "Charlie", "Diana"));
        assertTrue(Player.playersValid(players), "Four unique alpha-only names should be valid");
    }

    @Test
    @DisplayName("playersValid: single player")
    void testPlayersValid_singlePlayer() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("Alice"));
        assertTrue(Player.playersValid(players), "A single alpha-only name should be valid");
    }

    @Test
    @DisplayName("playersValid: lowercase only names")
    void testPlayersValid_lowercaseOnly() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("alice", "bob", "charlie", "diana"));
        assertTrue(Player.playersValid(players), "All-lowercase names should be valid");
    }

    @Test
    @DisplayName("playersValid: uppercase only names")
    void testPlayersValid_uppercaseOnly() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("ALICE", "BOB", "CHARLIE", "DIANA"));
        assertTrue(Player.playersValid(players), "All-uppercase names should be valid");
    }

    @Test
    @DisplayName("playersValid: mixed case names")
    void testPlayersValid_mixedCase() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("AlIcE", "bOb"));
        assertTrue(Player.playersValid(players), "Mixed-case alpha names should be valid");
    }

    @Test
    @DisplayName("playersValid: single character names")
    void testPlayersValid_singleCharNames() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        assertTrue(Player.playersValid(players), "Single-character names should be valid");
    }

    /* -------------------------------------------------------------------------- */
    /*                       playersValid – duplicate cases                       */
    /* -------------------------------------------------------------------------- */

    @Test
    @DisplayName("playersValid: duplicate names returns false")
    void testPlayersValid_duplicateNames() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("Alice", "Bob", "Alice", "Diana"));
        assertFalse(Player.playersValid(players), "Duplicate names should be invalid");
    }

    @Test
    @DisplayName("playersValid: duplicate names are case-sensitive")
    void testPlayersValid_duplicatesCaseSensitive() {
        // "Alice" and "alice" are distinct strings, so HashSet.add will succeed for both
        ArrayList<String> players = new ArrayList<>(Arrays.asList("Alice", "alice"));
        assertTrue(Player.playersValid(players),
                "Names differing only in case should be treated as distinct (HashSet is case-sensitive)");
    }

    @Test
    @DisplayName("playersValid: all four names identical")
    void testPlayersValid_allIdentical() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("Bob", "Bob", "Bob", "Bob"));
        assertFalse(Player.playersValid(players), "All identical names should be invalid");
    }

    /* -------------------------------------------------------------------------- */
    /*                    playersValid – invalid character cases                  */
    /* -------------------------------------------------------------------------- */

    @Test
    @DisplayName("playersValid: name with digits returns false")
    void testPlayersValid_nameWithDigits() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("Alice", "Bob2", "Charlie", "Diana"));
        assertFalse(Player.playersValid(players), "Names containing digits should be invalid");
    }

    @Test
    @DisplayName("playersValid: name with spaces returns false")
    void testPlayersValid_nameWithSpaces() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("Alice", "Bob Smith", "Charlie", "Diana"));
        assertFalse(Player.playersValid(players), "Names containing spaces should be invalid");
    }

    @Test
    @DisplayName("playersValid: name with special characters returns false")
    void testPlayersValid_nameWithSpecialChars() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("Alice", "Bob!", "Charlie", "Diana"));
        assertFalse(Player.playersValid(players), "Names containing special characters should be invalid");
    }

    @Test
    @DisplayName("playersValid: name with hyphen returns false")
    void testPlayersValid_nameWithHyphen() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("Anne-Marie", "Bob", "Charlie", "Diana"));
        assertFalse(Player.playersValid(players), "Names containing hyphens should be invalid");
    }

    @Test
    @DisplayName("playersValid: name with underscore returns false")
    void testPlayersValid_nameWithUnderscore() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("player_one", "player_two", "player_three", "player_four"));
        assertFalse(Player.playersValid(players), "Names containing underscores should be invalid");
    }

    /* -------------------------------------------------------------------------- */
    /*                      playersValid – empty / edge cases                     */
    /* -------------------------------------------------------------------------- */

    @Test
    @DisplayName("playersValid: empty string name returns false")
    void testPlayersValid_emptyStringName() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("Alice", "", "Charlie", "Diana"));
        assertFalse(Player.playersValid(players), "Empty string names should be invalid");
    }

    @Test
    @DisplayName("playersValid: empty array returns true")
    void testPlayersValid_emptyArray() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList());
        assertTrue(Player.playersValid(players), "Empty player array should be valid (no invalid entries)");
    }

    @Test
    @DisplayName("playersValid: only-digits name returns false")
    void testPlayersValid_onlyDigits() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("123", "Bob", "Charlie", "Diana"));
        assertFalse(Player.playersValid(players), "A purely numeric name should be invalid");
    }

    @Test
    @DisplayName("playersValid: whitespace-only name returns false")
    void testPlayersValid_whitespaceOnly() {
        ArrayList<String> players = new ArrayList<>(Arrays.asList("   ", "Bob", "Charlie", "Diana"));
        assertFalse(Player.playersValid(players), "A whitespace-only name should be invalid");
    }
}
