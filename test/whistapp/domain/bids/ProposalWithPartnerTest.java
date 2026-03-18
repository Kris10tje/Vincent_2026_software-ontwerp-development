package whistapp.domain.bids;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.players.Player;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ProposalWithPartnerTest {

    private ProposalWithPartner proposalWithPartner;

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
        p1 = new Player("p1");
        p2 = new Player("p2");
        p3 = new Player("p3");
        p4 = new Player("p4");

        declarers = new ArrayList<>();
        declarers.add(p1);

        declarers2 = new ArrayList<>();
        declarers2.add(p1);
        declarers2.add(p2);

        proposalWithPartner = new ProposalWithPartner(declarers2);

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
    void testConstructor_illegal() {
        assertThrows(IllegalArgumentException.class, () -> new ProposalWithPartner(declarers));
        declarers2.add(p3);
        assertThrows(IllegalArgumentException.class, () -> new ProposalWithPartner(declarers2));
    }

    @Test
    void testCalculatePoints_legal() {
        assertEquals(2 + 2, proposalWithPartner.calculatePoints(tricksWon).get(p1));
        assertEquals(4, proposalWithPartner.calculatePoints(tricksWon).get(p2));
        assertEquals(-4, proposalWithPartner.calculatePoints(tricksWon).get(p3));
        assertEquals(-4, proposalWithPartner.calculatePoints(tricksWon).get(p4));

        assertEquals(-2 - 3, proposalWithPartner.calculatePoints(tricksWon2).get(p1));
        assertEquals(-5, proposalWithPartner.calculatePoints(tricksWon2).get(p2));
        assertEquals(5, proposalWithPartner.calculatePoints(tricksWon2).get(p3));
        assertEquals(5, proposalWithPartner.calculatePoints(tricksWon2).get(p4));

        assertEquals(2, proposalWithPartner.calculatePoints(tricksWon3).get(p1));
        assertEquals(2, proposalWithPartner.calculatePoints(tricksWon3).get(p2));
        assertEquals(-2, proposalWithPartner.calculatePoints(tricksWon3).get(p3));
        assertEquals(-2, proposalWithPartner.calculatePoints(tricksWon3).get(p4));

        assertEquals(-2 - 1 * 8, proposalWithPartner.calculatePoints(tricksWon4).get(p1));
        assertEquals(-10, proposalWithPartner.calculatePoints(tricksWon4).get(p2));
        assertEquals(10, proposalWithPartner.calculatePoints(tricksWon4).get(p3));
        assertEquals(10, proposalWithPartner.calculatePoints(tricksWon4).get(p4));

        assertEquals(2 * (2 + 5 * 1), proposalWithPartner.calculatePoints(tricksWon5).get(p1));
        assertEquals(14, proposalWithPartner.calculatePoints(tricksWon5).get(p2));
        assertEquals(-14, proposalWithPartner.calculatePoints(tricksWon5).get(p3));
        assertEquals(-14, proposalWithPartner.calculatePoints(tricksWon5).get(p4));
    }

}