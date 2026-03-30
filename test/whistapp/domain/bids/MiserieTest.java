package whistapp.domain.bids;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.players.Player;
import whistapp.domain.players.PlayerType;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MiserieTest {

    private Miserie miserie;
    private Miserie miserie2;
    private Miserie miserie3;

    private Player p1;
    private Player p2;
    private Player p3;
    private Player p4;

    private ArrayList<Player> declarers;
    private ArrayList<Player> declarers2;
    private ArrayList<Player> declarers3;

    private HashMap<Player, Integer> tricksWon;
    private HashMap<Player, Integer> tricksWon2;
    private HashMap<Player, Integer> tricksWon3;


    @BeforeEach
    void setUp() {
        p1 = new Player("p1", PlayerType.HUMAN);
        p2 = new Player("p2", PlayerType.HUMAN);
        p3 = new Player("p3", PlayerType.HUMAN);
        p4 = new Player("p4", PlayerType.HUMAN);

        declarers = new ArrayList<>();
        declarers.add(p1);

        declarers2 = new ArrayList<>();
        declarers2.add(p2);
        declarers2.add(p4);
        declarers3 = new ArrayList<>();
        declarers3.add(p3);
        declarers3.add(p2);
        declarers3.add(p4);

        miserie = new Miserie(declarers);
        miserie2 = new Miserie(declarers2);
        miserie3 = new Miserie(declarers3);

        tricksWon = new HashMap<>();
        tricksWon2 = new HashMap<>();
        tricksWon3 = new HashMap<>();

        tricksWon.put(p1, 0);
        tricksWon.put(p2, 0);
        tricksWon.put(p3, 10);
        tricksWon.put(p4, 3);
        tricksWon2.put(p1, 12);
        tricksWon2.put(p2, 0);
        tricksWon2.put(p3, 1);
        tricksWon2.put(p4, 0);
        tricksWon3.put(p1, 6);
        tricksWon3.put(p2, 2);
        tricksWon3.put(p3, 1);
        tricksWon3.put(p4, 4);
    }

    @Test
    void testCalculatePoints_miserie1_legal() {
        assertEquals(21, miserie.calculatePoints(tricksWon).get(p1));
        assertEquals(-7, miserie.calculatePoints(tricksWon).get(p2));
        assertEquals(-7, miserie.calculatePoints(tricksWon).get(p3));
        assertEquals(-7, miserie.calculatePoints(tricksWon).get(p4));

        assertEquals(-21, miserie.calculatePoints(tricksWon3).get(p1));
        assertEquals(7, miserie.calculatePoints(tricksWon3).get(p2));
        assertEquals(7, miserie.calculatePoints(tricksWon3).get(p3));
        assertEquals(7, miserie.calculatePoints(tricksWon3).get(p4));
    }

    @Test
    void testCalculatePoints_miserie2_legal() {
        assertEquals(0, miserie2.calculatePoints(tricksWon).get(p1));
        assertEquals(28, miserie2.calculatePoints(tricksWon).get(p2));
        assertEquals(0, miserie2.calculatePoints(tricksWon).get(p3));
        assertEquals(-28, miserie2.calculatePoints(tricksWon).get(p4));

        assertEquals(-14, miserie2.calculatePoints(tricksWon2).get(p1));
        assertEquals(14, miserie2.calculatePoints(tricksWon2).get(p2));
        assertEquals(-14, miserie2.calculatePoints(tricksWon2).get(p3));
        assertEquals(14, miserie2.calculatePoints(tricksWon2).get(p4));

        assertEquals(14, miserie2.calculatePoints(tricksWon3).get(p1));
        assertEquals(-14, miserie2.calculatePoints(tricksWon3).get(p2));
        assertEquals(14, miserie2.calculatePoints(tricksWon3).get(p3));
        assertEquals(-14, miserie2.calculatePoints(tricksWon3).get(p4));
    }

    @Test
    void testCalculatePoints_miserie3_legal() {
        assertEquals(7, miserie3.calculatePoints(tricksWon).get(p1));
        assertEquals(35, miserie3.calculatePoints(tricksWon).get(p2));
        assertEquals(-21, miserie3.calculatePoints(tricksWon).get(p3));
        assertEquals(-21, miserie3.calculatePoints(tricksWon).get(p4));

        assertEquals(-7, miserie3.calculatePoints(tricksWon2).get(p1));
        assertEquals(21, miserie3.calculatePoints(tricksWon2).get(p2));
        assertEquals(-35, miserie3.calculatePoints(tricksWon2).get(p3));
        assertEquals(21, miserie3.calculatePoints(tricksWon2).get(p4));

        assertEquals(21, miserie3.calculatePoints(tricksWon3).get(p1));
        assertEquals(-7, miserie3.calculatePoints(tricksWon3).get(p2));
        assertEquals(-7, miserie3.calculatePoints(tricksWon3).get(p3));
        assertEquals(-7, miserie3.calculatePoints(tricksWon3).get(p4));
    }
}