package whistapp.domain.interfaces;

import whistapp.domain.bids.BidType;
import whistapp.domain.interfaces.IPlayer;

import java.util.HashMap;

public interface IScoreGame extends IGame {
    void registerBids(HashMap<IPlayer, BidType> bids);
    void setReshuffledState(boolean reshuffled);
}
