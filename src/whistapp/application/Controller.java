package whistapp.application;

import whistapp.application.Interfaces.IController;
import whistapp.application.Interfaces.IPlayGameController;
import whistapp.application.Interfaces.IScoreGameController;
import whistapp.domain.cards.Suit;
import whistapp.domain.players.BotDifficulty;
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
    public IScoreGameController startNewScoreGame(ArrayList<String> playerNames) {
        return new ScoreGameController(playerNames);
    }

    /**
     * Start a new game of PlayWhist with the given player names and bot difficulties.
     * The player map contains player name to BotDifficulty (null if human player).
     */
    public IPlayGameController startNewPlayGame(LinkedHashMap<String, BotDifficulty> playerNamesAndBotDifficulties) {
        return new PlayGameController(playerNamesAndBotDifficulties);
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
