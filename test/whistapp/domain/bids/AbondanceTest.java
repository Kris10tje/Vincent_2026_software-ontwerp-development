package whistapp.domain.bids;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.players.Player;
import whistapp.domain.players.PlayerType;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AbondanceTest {

    private Abondance abondance9;
    private Abondance abondance10;
    private Abondance abondance11;
    private Abondance abondance12;

    private Player p1;
    private Player p2;
    private Player p3;
    private Player p4;

    private HashMap<Player, Integer> tricksWon;
    private HashMap<Player, Integer> tricksWon2;
    private HashMap<Player, Integer> tricksWon3;

    private ArrayList<Player> declarers;
    private ArrayList<Player> declarers2;

    @BeforeEach
    void setUp() {
        p1 = new Player("p1", PlayerType.HUMAN);
        p2 = new Player("p2", PlayerType.HUMAN);
        p3 = new Player("p3", PlayerType.HUMAN);
        p4 = new Player("p4", PlayerType.HUMAN);

        declarers = new ArrayList<>();
        declarers.add(p1);

        declarers2 = new ArrayList<>();
        declarers2.add(p1);
        declarers2.add(p2);

        abondance9 = new Abondance(p1, 9, false);
        abondance10 = new Abondance(p1, 10, false);
        abondance11 = new Abondance(p1, 11, false);
        abondance12 = new Abondance(p1, 12, false);

        tricksWon = new HashMap<>();
        tricksWon2 = new HashMap<>();
        tricksWon3 = new HashMap<>();

        tricksWon.put(p1, 10);
        tricksWon.put(p2, 0);
        tricksWon.put(p3, 2);
        tricksWon.put(p4, 1);
        tricksWon2.put(p1, 12);
        tricksWon2.put(p2, 1);
        tricksWon2.put(p3, 0);
        tricksWon2.put(p4, 0);
        tricksWon3.put(p1, 6);
        tricksWon3.put(p2, 2);
        tricksWon3.put(p3, 1);
        tricksWon3.put(p4, 4);

    }

    @Test
    void testConstructor_tricks_illegal() {
        // Test the illegal values for the number of tricks
        assertThrows(IllegalArgumentException.class, () -> new Abondance(p1, 4, false));
        assertThrows(IllegalArgumentException.class, () -> new Abondance(p1, 15, false));
    }

    @Test
    void testCalculatePoints_abondance9_legal() {
        assertEquals(15, abondance9.calculatePoints(tricksWon).get(p1));
        assertEquals(-5, abondance9.calculatePoints(tricksWon).get(p2));
        assertEquals(-5, abondance9.calculatePoints(tricksWon).get(p3));
        assertEquals(-5, abondance9.calculatePoints(tricksWon).get(p4));

        assertEquals(-15, abondance9.calculatePoints(tricksWon3).get(p1));
        assertEquals(5, abondance9.calculatePoints(tricksWon3).get(p2));
        assertEquals(5, abondance9.calculatePoints(tricksWon3).get(p3));
        assertEquals(5, abondance9.calculatePoints(tricksWon3).get(p4));
    }

    @Test
    void testCalculatePoints_abondance10_legal() {
        assertEquals(18, abondance10.calculatePoints(tricksWon).get(p1));
        assertEquals(-6, abondance10.calculatePoints(tricksWon).get(p2));
        assertEquals(-6, abondance10.calculatePoints(tricksWon).get(p3));
        assertEquals(-6, abondance10.calculatePoints(tricksWon).get(p4));

        assertEquals(-18, abondance10.calculatePoints(tricksWon3).get(p1));
        assertEquals(6, abondance10.calculatePoints(tricksWon3).get(p2));
        assertEquals(6, abondance10.calculatePoints(tricksWon3).get(p3));
        assertEquals(6, abondance10.calculatePoints(tricksWon3).get(p4));
    }

    @Test
    void testCalculatePoints_abondance11_legal() {
        assertEquals(-24, abondance11.calculatePoints(tricksWon).get(p1));
        assertEquals(8, abondance11.calculatePoints(tricksWon).get(p2));
        assertEquals(8, abondance11.calculatePoints(tricksWon).get(p3));
        assertEquals(8, abondance11.calculatePoints(tricksWon).get(p4));

        assertEquals(24, abondance11.calculatePoints(tricksWon2).get(p1));
        assertEquals(-8, abondance11.calculatePoints(tricksWon2).get(p2));
        assertEquals(-8, abondance11.calculatePoints(tricksWon2).get(p3));
        assertEquals(-8, abondance11.calculatePoints(tricksWon2).get(p4));
    }

    @Test
    void testCalculatePoints_abondance12_legal() {
        assertEquals(-27, abondance12.calculatePoints(tricksWon).get(p1));
        assertEquals(9, abondance12.calculatePoints(tricksWon).get(p2));
        assertEquals(9, abondance12.calculatePoints(tricksWon).get(p3));
        assertEquals(9, abondance12.calculatePoints(tricksWon).get(p4));

        assertEquals(27, abondance12.calculatePoints(tricksWon2).get(p1));
        assertEquals(-9, abondance12.calculatePoints(tricksWon2).get(p2));
        assertEquals(-9, abondance12.calculatePoints(tricksWon2).get(p3));
        assertEquals(-9, abondance12.calculatePoints(tricksWon2).get(p4));
    }

}