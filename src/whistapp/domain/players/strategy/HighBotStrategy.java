package whistapp.domain.players.strategy;

import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Hand;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.round.RoundContext;

public class HighBotStrategy implements PlayerStrategy {

    /*zijn deze overrides nodig?? */
    //@Override
    public BidType chooseBid(Hand hand, RoundContext context) {
        return BidType.PASS;
    }

    //@Override
    public ICard chooseCard(Hand hand, RoundContext context) {
        return hand.getOuterCard(context.getCurrentTrickSuit(), true);
    }

    //@Override
    public boolean isAutonomous() {
        return true;
    }
}
