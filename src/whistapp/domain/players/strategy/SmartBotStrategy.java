package whistapp.domain.players.strategy;

import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Hand;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.round.RoundContext;

public class SmartBotStrategy implements PlayerStrategy {

    @Override
    public BidType chooseBid(Hand hand, RoundContext context) {
        return null;
    }

    @Override
    public ICard chooseCard(Hand hand, RoundContext context) {
        return null;
    }

    @Override
    public boolean isAutonomous() {
        return false;
    }
}
