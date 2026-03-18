package whistapp.domain.Interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.bids.Bid;
import whistapp.domain.bids.BidType;

public interface IRound {
    void setFinalBid(BidType bidType, ArrayList<IPlayer> declarers, boolean wasFirstTry);
    HashMap<IPlayer, Integer> processRoundOutcome(HashMap<IPlayer, Integer> tricksWon);
    void setWasFirstTry(boolean wasFirstTry);
    LinkedHashMap<IPlayer, Integer> getTricksWon();
    Bid getFinalBid();
}
