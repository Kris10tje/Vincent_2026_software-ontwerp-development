package whistapp.domain.bids;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.players.Player;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BidTest {

    private Player p1;
    private Player p2;
    private Player p3;
    private Player p4;
    private Player p5;

    private ArrayList<Player> declarers;
    private ArrayList<Player> declarers2;
    private ArrayList<Player> declarers3;
    private ArrayList<Player> declarers4;

    private HashMap<Player, Integer> tricksWon;
    private HashMap<Player, Integer> tricksWon2;
    private HashMap<Player, Integer> tricksWon3;

    private Bid bid;
    private Bid bid2;

    @BeforeEach
    void setUp() {
        p1 = new Player("p1");
        p2 = new Player("p2");
        p3 = new Player("p3");
        p4 = new Player("p4");
        p5 = new Player("p5");

        tricksWon = new HashMap<>();
        tricksWon2 = new HashMap<>();
        tricksWon3 = new HashMap<>();

        tricksWon.put(p1, 1);
        tricksWon.put(p2, 5);
        tricksWon.put(p3, 1);
        tricksWon.put(p4, 3);

        tricksWon2.put(p1, 1);
        tricksWon2.put(p2, 10);
        tricksWon2.put(p3, 2);

        tricksWon3.put(p1, -2);
        tricksWon3.put(p2, 10);
        tricksWon3.put(p3, 3);
        tricksWon3.put(p4, 4);

        declarers = new ArrayList<>();
        declarers.add(p1);
        declarers2 = new ArrayList<>();
        declarers2.add(p2);
        declarers2.add(p2);
        declarers3 = new ArrayList<>();
        declarers3.add(p3);
        declarers3.add(p4);
        declarers3.add(p1);
        declarers3.add(p2);
        declarers3.add(p5);
        declarers4 = new ArrayList<>();
        declarers4.add(p3);
        declarers4.add(p4);
        declarers4.add(p1);

        bid = new Miserie(declarers4);
        bid2 = new Abondance(p1, 10, false);

    }

    @Test
    void testConstructor_legal() {
        Bid bid = new Solo(p1);
        assertEquals(bid.getBidders(), declarers);
    }

    // @Test
    // void testCalculatePoints_illegal() {
    //     assertThrows(IllegalArgumentException.class, () -> bid.calculatePoints(tricksWon));
    //     assertThrows(IllegalArgumentException.class, () -> bid.calculatePoints(tricksWon2));
    //     assertThrows(IllegalArgumentException.class, () -> bid.calculatePoints(tricksWon3));

    //     assertThrows(IllegalArgumentException.class, () -> bid2.calculatePoints(tricksWon));
    //     assertThrows(IllegalArgumentException.class, () -> bid2.calculatePoints(tricksWon2));
    //     assertThrows(IllegalArgumentException.class, () -> bid2.calculatePoints(tricksWon3));
    // }
}