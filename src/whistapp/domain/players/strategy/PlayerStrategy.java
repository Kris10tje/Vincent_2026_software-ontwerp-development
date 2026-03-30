package whistapp.domain.players.strategy;

import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Hand;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.round.RoundContext;

import java.util.ArrayList;

public interface PlayerStrategy {



    /**
     * Choose which bid to make.
     * @param hand The current hand of the player.
     * @param context Read-only view of the current round.
     * @return The chosen bid type.
     */
    BidType chooseBid(Hand hand, RoundContext context);

    /**
     * Choose which card to play.
     * @param hand The current hand of the player.
     * @param context Read-only view of the current round.
     * @return The chosen card.
     */
    ICard chooseCard(Hand hand, RoundContext context);

    /**
     * Whether this strategy is autonomous (no human input needed).
     */
    boolean isAutonomous();

}
