package whistapp.domain.bids;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.players.Player;
import whistapp.domain.players.PlayerType;
import whistapp.domain.players.strategy.HumanStrategy;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ProposalAloneTest {

    private ProposalAlone proposalAlone;

    private Player p1;
    private Player p2;
    private Player p3;
    private Player p4;

    private HashMap<Player, Integer> tricksWon;
    private HashMap<Player, Integer> tricksWon2;
    private HashMap<Player, Integer> tricksWon3;
    private HashMap<Player, Integer> tricksWon4;
    private HashMap<Player, Integer> tricksWon5;

    private ArrayList<Player> declarers;
    private ArrayList<Player> declarers2;

    @BeforeEach
    void setUp() {
        p1 = new Player("p1", new HumanStrategy(null));
        p2 = new Player("p2", new HumanStrategy(null));
        p3 = new Player("p3", new HumanStrategy(null));
        p4 = new Player("p4", new HumanStrategy(null));

        declarers = new ArrayList<>();
        declarers.add(p1);

        declarers2 = new ArrayList<>();
        declarers2.add(p1);
        declarers2.add(p2);

        proposalAlone = new ProposalAlone(p1);

        tricksWon = new HashMap<>();
        tricksWon2 = new HashMap<>();
        tricksWon3 = new HashMap<>();
        tricksWon4 = new HashMap<>();
        tricksWon5 = new HashMap<>();

        tricksWon.put(p1, 10);
        tricksWon.put(p2, 0);
        tricksWon.put(p3, 2);
        tricksWon.put(p4, 1);
        tricksWon2.put(p1, 4);
        tricksWon2.put(p2, 1);
        tricksWon2.put(p3, 6);
        tricksWon2.put(p4, 2);
        tricksWon3.put(p1, 6);
        tricksWon3.put(p2, 2);
        tricksWon3.put(p3, 1);
        tricksWon3.put(p4, 4);
        tricksWon4.put(p1, 0);
        tricksWon4.put(p2, 0);
        tricksWon4.put(p3, 13);
        tricksWon4.put(p4, 0);
        tricksWon5.put(p1, 13);
        tricksWon5.put(p2, 0);
        tricksWon5.put(p3, 0);
        tricksWon5.put(p4, 0);
    }

    @Test
    void testCalculatePoints_legal() {
        assertEquals(6 + 3 * 5, proposalAlone.calculatePoints(tricksWon).get(p1));
        assertEquals(-(2 + 1 * 5), proposalAlone.calculatePoints(tricksWon).get(p2));
        assertEquals(-(2 + 1 * 5), proposalAlone.calculatePoints(tricksWon).get(p3));
        assertEquals(-(2 + 1 * 5), proposalAlone.calculatePoints(tricksWon).get(p4));

        assertEquals(-6 - 3, proposalAlone.calculatePoints(tricksWon2).get(p1));
        assertEquals(3, proposalAlone.calculatePoints(tricksWon2).get(p2));
        assertEquals(3, proposalAlone.calculatePoints(tricksWon2).get(p3));
        assertEquals(3, proposalAlone.calculatePoints(tricksWon2).get(p4));

        assertEquals(6 + 3, proposalAlone.calculatePoints(tricksWon3).get(p1));
        assertEquals(-3, proposalAlone.calculatePoints(tricksWon3).get(p2));
        assertEquals(-3, proposalAlone.calculatePoints(tricksWon3).get(p3));
        assertEquals(-3, proposalAlone.calculatePoints(tricksWon3).get(p4));

        assertEquals(-6 - 3 * 5, proposalAlone.calculatePoints(tricksWon4).get(p1));
        assertEquals(7, proposalAlone.calculatePoints(tricksWon4).get(p2));
        assertEquals(7, proposalAlone.calculatePoints(tricksWon4).get(p3));
        assertEquals(7, proposalAlone.calculatePoints(tricksWon4).get(p4));

        assertEquals(2 * (6 + 3 * 8), proposalAlone.calculatePoints(tricksWon5).get(p1));
        assertEquals(-20, proposalAlone.calculatePoints(tricksWon5).get(p2));
        assertEquals(-20, proposalAlone.calculatePoints(tricksWon5).get(p3));
        assertEquals(-20, proposalAlone.calculatePoints(tricksWon5).get(p4));
    }
}