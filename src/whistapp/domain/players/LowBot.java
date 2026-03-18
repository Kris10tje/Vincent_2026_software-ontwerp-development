package whistapp.domain.players;

import whistapp.domain.cards.Suit;

/**
 * Represents a bot player that plays low cards.
 */
public class LowBot extends BotPlayer {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public LowBot(String name) {
        super(name);
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public methods                               */
    /* -------------------------------------------------------------------------- */

    @Override
    public String findAutonomousCard(Suit leadSuit) {
        return hand.getOuterCard(leadSuit, false);
    }
}
