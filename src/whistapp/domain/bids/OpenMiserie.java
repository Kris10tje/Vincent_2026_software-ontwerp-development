package whistapp.domain.bids;

import whistapp.domain.Interfaces.IPlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents an Open Miserie bid (0 cards, but with the possibility of showing cards)
 */
public class OpenMiserie extends Miserie {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public OpenMiserie(ArrayList<IPlayer> declarers) throws IllegalArgumentException {
        super(declarers);
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Getters                                    */
    /* -------------------------------------------------------------------------- */

    @Override
    public HashMap<IPlayer, String[]> getOpenMiserieHands(IPlayer currentPlayer) {
        HashMap<IPlayer, String[]> openMiserieHands = new HashMap<>();
        for (IPlayer declarer : declarers) {
            if (!declarer.equals(currentPlayer)) {
                openMiserieHands.put(declarer, declarer.getHandCards());
            }
        }
        return openMiserieHands;
    }

    @Override
    public String toString() {
        return "Open Miserie";
    }

    /* -------------------------------------------------------------------------- */
    /*                             Protected methods                              */
    /* -------------------------------------------------------------------------- */

    /**
     * Returns the base value for the open miserie. Base value is 14 (so total is 42).
     *
     * @return The base value of the open miserie bid.
     */
    @Override
    protected int getBaseValue() {
        return 14;
    }

}
