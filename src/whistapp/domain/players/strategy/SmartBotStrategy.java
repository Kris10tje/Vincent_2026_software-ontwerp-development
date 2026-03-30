package whistapp.domain.players.strategy;

import whistapp.domain.bids.BidType;
import whistapp.domain.bids.BidTypeWithTrump;
import whistapp.domain.cards.Hand;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.round.Context.BidContext;
import whistapp.domain.round.Context.RoundContext;

public class SmartBotStrategy implements PlayerStrategy {

    /*TODO zijn deze overrides nodig?? */
    //@Override
    public BidTypeWithTrump chooseBid(Hand hand, BidContext context) {
        return null;
    }

    //@Override
    public ICard chooseCard(Hand hand, RoundContext context) {
        return null;
    }

    //@Override
    public boolean isAutonomous() {
        return false;
    }
}
