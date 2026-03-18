package whistapp.domain.bids;

import whistapp.domain.players.Player;

import java.util.HashMap;

public class SoloSlim extends Solo {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public SoloSlim(Player declarer) {
        super(declarer);
    }

    /* -------------------------------------------------------------------------- */
    /*                             Protected methods                              */
    /* -------------------------------------------------------------------------- */

    /**
     * SoloSlim simply changes the win score compared to Solo.
     * The score calculation logic is handled by the parent class.
     *
     * @return The win score for the bid.
     */
    @Override
    protected int getWinScore() {
        return 90;
    }

    /**
     * SoloSlim simply changes the loss score compared to Solo.
     * The score calculation logic is handled by the parent class.
     *
     * @return The loss score for the bid.
     */
    @Override
    protected int getLossScore() {
        return -30;
    }

    @Override
    public String toString() {
        return "Solo Slim";
    }

}
