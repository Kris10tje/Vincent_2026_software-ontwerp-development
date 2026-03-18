package whistapp.application;

import whistapp.domain.Interfaces.IController;
import whistapp.domain.Interfaces.IPlayGame;
import whistapp.domain.Interfaces.IScoreGame;
import whistapp.domain.cards.Suit;
import whistapp.domain.game.ScoreGame;
import whistapp.domain.players.BotDifficulty;
import whistapp.domain.game.PlayGame;
import whistapp.domain.game.Game;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * The controller; acts as a thin communication layer between the UI layer and DOMAIN layer
 */
public class Controller implements IController{

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public Controller() {

    }

    /* -------------------------------------------------------------------------- */
    /*                                 Public Methods                             */
    /* -------------------------------------------------------------------------- */

    /**
     * Start a new game of ScoreWhist with the given player names.
     */
    public IScoreGame startNewScoreGame(ArrayList<String> playerNames) {
        return new ScoreGame(playerNames);
    }

    /**
     * Start a new game of PlayWhist with the given player names and bot difficulties.
     * The player map contains player name to BotDifficulty (null if human player).
     */
    public IPlayGame startNewPlayGame(LinkedHashMap<String, BotDifficulty> playerNamesAndBotDifficulties) {
        return new PlayGame(playerNamesAndBotDifficulties);
    }

    /**
     * Exit the application.
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * A simple getter returning all bids a player can make.
     *
     * @return An array of BidTypes, that can be printed as a string
     */
    public static String[] getBidTypes() {
        return Game.getBidTypes();
    }

    /**
     * A simple getter for the player count of a game
     *
     * @return The number of players (including bots) in a game.
     */
    public static int getPlayerCount() {
        return Game.getPlayerCount();
    }

    /**
     * Gets the possible bot difficulties.
     *
     * @return An array containing all possible difficulties.
     */
    public BotDifficulty[] getBotDifficultyOptions() {
        return BotDifficulty.values();
    }

    /**
     * A simple getter that returns all suits.
     *
     * @return An array containing all suits.
     */
    public Suit[] getSuits() {
        return Suit.values();
    }
}
