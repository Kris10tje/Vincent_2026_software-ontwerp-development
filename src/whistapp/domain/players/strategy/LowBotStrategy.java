package whistapp.domain.players.strategy;

import whistapp.domain.bids.BidType;
import whistapp.domain.bids.BidTypeWithTrump;
import whistapp.domain.cards.Hand;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.round.Context.BidContext;
import whistapp.domain.round.Context.RoundContext;

public class LowBotStrategy implements PlayerStrategy{
    /*zijn deze overrides nodig?? */
    //@Override
    public BidTypeWithTrump chooseBid(Hand hand, BidContext context) {
        return new BidTypeWithTrump(BidType.PASS, null);
    }

    //@Override
    public ICard chooseCard(Hand hand, RoundContext context) {
        return hand.getOuterCard(context.getCurrentTrickSuit(), false);
    }

    //@Override
    public boolean isAutonomous() {
        return true;
    }
}
