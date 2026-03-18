package whistapp.domain.players;

import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Suit;

/**
 * Represents a bot player in the game of Whist.
 */
public abstract class BotPlayer extends Player {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public BotPlayer(String name) {
        super(name);
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public methods                               */
    /* -------------------------------------------------------------------------- */

    /**
     * A method for playing a card autonomously as a bot.
     */
    public abstract String findAutonomousCard(Suit leadSuit);

    /* -------------------------------------------------------------------------- */
    /*                                   Getters                                  */
    /* -------------------------------------------------------------------------- */

    @Override
    public boolean isAutonomous() {
        return true;
    }

    /**
     * A simple getter for the type of bid for a bot.
     */
    public BidType getAutonomousBid() {
        return BidType.PASS;
    }

}
