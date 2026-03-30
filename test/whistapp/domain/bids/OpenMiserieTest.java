package whistapp.domain.bids;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.cards.Deck;
import whistapp.domain.players.Player;
import whistapp.domain.players.PlayerType;
import whistapp.domain.players.strategy.HumanStrategy;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class OpenMiserieTest {

    private OpenMiserie openMiserie;
    private OpenMiserie openMiserie2;
    private OpenMiserie openMiserie3;


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
        p1 = new Player("p1", new HumanStrategy(null));
        p2 = new Player("p2", new HumanStrategy(null));
        p3 = new Player("p3", new HumanStrategy(null));
        p4 = new Player("p4", new HumanStrategy(null));

        declarers = new ArrayList<>();
        declarers.add(p1);

        declarers2 = new ArrayList<>();
        declarers2.add(p2);
        declarers2.add(p4);
        declarers3 = new ArrayList<>();
        declarers3.add(p3);
        declarers3.add(p2);
        declarers3.add(p4);

        openMiserie = new OpenMiserie(declarers);
        openMiserie2 = new OpenMiserie(declarers2);
        openMiserie3 = new OpenMiserie(declarers3);


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
    void testCalculatePoints_legal() {
        assertEquals(42, openMiserie.calculatePoints(tricksWon).get(p1));
        assertEquals(-14, openMiserie.calculatePoints(tricksWon).get(p2));
        assertEquals(-14, openMiserie.calculatePoints(tricksWon).get(p3));
        assertEquals(-14, openMiserie.calculatePoints(tricksWon).get(p4));

        assertEquals(-42, openMiserie.calculatePoints(tricksWon3).get(p1));
        assertEquals(14, openMiserie.calculatePoints(tricksWon3).get(p2));
        assertEquals(14, openMiserie.calculatePoints(tricksWon3).get(p3));
        assertEquals(14, openMiserie.calculatePoints(tricksWon3).get(p4));
    }

    @Test
    void testCalculatePoints_legal2() {
        assertEquals(0, openMiserie2.calculatePoints(tricksWon).get(p1));
        assertEquals(56, openMiserie2.calculatePoints(tricksWon).get(p2));
        assertEquals(0, openMiserie2.calculatePoints(tricksWon).get(p3));
        assertEquals(-56, openMiserie2.calculatePoints(tricksWon).get(p4));

        assertEquals(-28, openMiserie2.calculatePoints(tricksWon2).get(p1));
        assertEquals(28, openMiserie2.calculatePoints(tricksWon2).get(p2));
        assertEquals(-28, openMiserie2.calculatePoints(tricksWon2).get(p3));
        assertEquals(28, openMiserie2.calculatePoints(tricksWon2).get(p4));

        assertEquals(28, openMiserie2.calculatePoints(tricksWon3).get(p1));
        assertEquals(-28, openMiserie2.calculatePoints(tricksWon3).get(p2));
        assertEquals(28, openMiserie2.calculatePoints(tricksWon3).get(p3));
        assertEquals(-28, openMiserie2.calculatePoints(tricksWon3).get(p4));
    }

    @Test
    void testCalculatePoints_legal3() {
        assertEquals(14, openMiserie3.calculatePoints(tricksWon).get(p1));
        assertEquals(70, openMiserie3.calculatePoints(tricksWon).get(p2));
        assertEquals(-42, openMiserie3.calculatePoints(tricksWon).get(p3));
        assertEquals(-42, openMiserie3.calculatePoints(tricksWon).get(p4));

        assertEquals(-14, openMiserie3.calculatePoints(tricksWon2).get(p1));
        assertEquals(42, openMiserie3.calculatePoints(tricksWon2).get(p2));
        assertEquals(-70, openMiserie3.calculatePoints(tricksWon2).get(p3));
        assertEquals(42, openMiserie3.calculatePoints(tricksWon2).get(p4));

        assertEquals(42, openMiserie3.calculatePoints(tricksWon3).get(p1));
        assertEquals(-14, openMiserie3.calculatePoints(tricksWon3).get(p2));
        assertEquals(-14, openMiserie3.calculatePoints(tricksWon3).get(p3));
        assertEquals(-14, openMiserie3.calculatePoints(tricksWon3).get(p4));
    }

    @Test
    void testGetOpenMiserieHands() {
        Deck deck = new Deck();
        deck.shuffle();
        p1.giveHand(deck.dealHand(13));
        p2.giveHand(deck.dealHand(13));
        p3.giveHand(deck.dealHand(13));
        p4.giveHand(deck.dealHand(13));
        // The declarers are not going to have cards,
        // but it's the fact that they are added to the output that is important
        HashMap<Player, ArrayList<ICard>> hands = openMiserie3.getOpenMiserieHands(p1);
        assertEquals(3, hands.size());
        assertEquals(hands.get(p2).size(), 13);
        assertEquals(p2.getHandCards().get(0), hands.get(p2).get(0));
        assertFalse(hands.containsKey(p1));
        hands = openMiserie3.getOpenMiserieHands(p2);
        assertEquals(2, hands.size());
        assertFalse(hands.containsKey(p2));
        assertEquals(13, hands.get(p3).size());
        assertEquals(p3.getHandCards().get(0), hands.get(p3).get(0));
        hands = openMiserie3.getOpenMiserieHands(p3);
        assertEquals(2, hands.size());
        assertFalse(hands.containsKey(p3));
        assertEquals(13, hands.get(p4).size());
        assertEquals(p4.getHandCards().get(0), hands.get(p4).get(0));
        hands = openMiserie3.getOpenMiserieHands(p4);
        assertEquals(2, hands.size());
        assertFalse(hands.containsKey(p4));
        assertEquals(13, hands.get(p2).size());
        assertEquals(p2.getHandCards().get(0), hands.get(p2).get(0));
    }

}