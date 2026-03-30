package whistapp.domain.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.players.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private ScoreGame game;

    private String player1;
    private String player2;
    private String player3;
    private String player4;

    private ArrayList<String> players;
    @BeforeEach
    void setUp() {
        player1 = "Gary";
        player2 = "Jack";
        player3 = "Alice";
        player4 = "Frank";
        players = new ArrayList<>(Arrays.asList(player1, player2, player3, player4));

        game = new ScoreGame(players);
    }

    @Test
    void initializeHumanPlayers() {
        ScoreGame gameEmpty = new ScoreGame(players);

        // The players instantiated should have the correct names
        assertEquals(4, gameEmpty.getPlayers().size());
        assertEquals(player1, gameEmpty.getPlayers().getFirst().getName());
        assertEquals(player2, gameEmpty.getPlayers().get(1).getName());
        assertEquals(player3, gameEmpty.getPlayers().get(2).getName());
        assertEquals(player4, gameEmpty.getPlayers().get(3).getName());
    }

    @Test
    void getPlayerNames() {
        ArrayList<String> names = game.getPlayerNames();
        assertEquals(player1, names.get(0));
        assertEquals(player2, names.get(1));
        assertEquals(player3, names.get(2));
        assertEquals(player4, names.get(3));
    }

    @Test
    void getPlayerByName_valid() {
        assertEquals(player1, game.getPlayerByName(player1).getName());
        assertEquals(player1, game.getPlayerByName("gary").getName());
        assertEquals(player1, game.getPlayerByName("gaRY").getName());
        assertEquals(player2, game.getPlayerByName(player2).getName());
        assertEquals(player2, game.getPlayerByName("jack").getName());
        assertEquals(player2, game.getPlayerByName("jaCk").getName());
        assertEquals(player3, game.getPlayerByName(player3).getName());
        assertEquals(player4, game.getPlayerByName(player4).getName());
    }

    @Test
    void getPlayerByName_invalid() {
        assertNull(game.getPlayerByName("garyl"));
        assertNull(game.getPlayerByName("alice "));
    }

    @Test
    void getPlayersByName_valid() {
        ArrayList<Player> players1 = game.getPlayersByName(players);

        assertEquals(player1, players1.get(0).getName());
        assertEquals(player2, players1.get(1).getName());
        assertEquals(player3, players1.get(2).getName());
        assertEquals(player4, players1.get(3).getName());
    }

    @Test
    void getPlayersByName_invalid() {
        players.add("Jay");
        ArrayList<Player> players1 = game.getPlayersByName(players);

        assertEquals(player1, players1.get(0).getName());
        assertEquals(player2, players1.get(1).getName());
        assertEquals(player3, players1.get(2).getName());
        assertEquals(player4, players1.get(3).getName());
        assertNull(players1.get(4));
    }

    @Test
    void getScoresPerPlayer_start() {
        HashMap<IPlayer, Integer> scores = game.getScoresPerPlayer();
        assertEquals(4, scores.size());
        assertEquals(0, scores.get(game.getPlayersByName(players).get(0)));
        assertEquals(0, scores.get(game.getPlayersByName(players).get(1)));
        assertEquals(0, scores.get(game.getPlayersByName(players).get(2)));
        assertEquals(0, scores.get(game.getPlayersByName(players).get(3)));
    }

    @Test
    void setAllScores() {
        game.setAllScores(10);
        HashMap<IPlayer, Integer> scores = game.getScoresPerPlayer();
        assertEquals(4, scores.size());
        assertEquals(10, scores.get(game.getPlayersByName(players).get(0)));
        assertEquals(10, scores.get(game.getPlayersByName(players).get(1)));
        assertEquals(10, scores.get(game.getPlayersByName(players).get(2)));
        assertEquals(10, scores.get(game.getPlayersByName(players).get(3)));
    }
}