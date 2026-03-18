package whistapp.domain.players;

import whistapp.domain.cards.Suit;

/**
 * Represents a bot player that plays high cards.
 */
public class HighBot extends BotPlayer {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public HighBot(String name) {
        super(name);
    }

    /* -------------------------------------------------------------------------- */
    /*                               Public methods                               */
    /* -------------------------------------------------------------------------- */

    @Override
    public String findAutonomousCard(Suit leadSuit) {
        return hand.getOuterCard(leadSuit, true);
    }

}
