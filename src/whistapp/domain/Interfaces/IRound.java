package whistapp.domain.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.bids.Bid;
import whistapp.domain.bids.BidType;
import whistapp.domain.players.Player;

public interface IRound {
    void setFinalBid(BidType bidType, ArrayList<Player> declarers, boolean wasFirstTry);
    HashMap<Player, Integer> processRoundOutcome(HashMap<Player, Integer> tricksWon);
    void setWasFirstTry(boolean wasFirstTry);
    LinkedHashMap<Player, Integer> getTricksWon();
    Bid getFinalBid();
}
