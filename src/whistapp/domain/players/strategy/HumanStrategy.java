package whistapp.domain.players.strategy;

import whistapp.domain.bids.BidType;
import whistapp.domain.cards.Hand;
import whistapp.domain.interfaces.ICard;
import whistapp.domain.round.RoundContext;

import java.util.ArrayList;

public class HumanStrategy implements PlayerStrategy {

    @Override
    public BidType chooseBid(Hand hand, RoundContext context) {
        throw new UnsupportedOperationException("Humans choose bids through the CLI.");
    }

    @Override
    public ICard chooseCard(Hand hand, RoundContext context) {
        throw new UnsupportedOperationException("Humans choose cards through the CLI.");
    }

    @Override
    public boolean isAutonomous() {
        return false;
    }
}
