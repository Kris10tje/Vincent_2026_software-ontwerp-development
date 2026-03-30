package whistapp.application.interfaces;

import whistapp.domain.bids.BidType;
import whistapp.domain.interfaces.IPlayer;

import java.util.HashMap;

public interface IScoreGameController extends IGameController {
    void registerBids(HashMap<IPlayer, BidType> bids);
    void setReshuffledState(boolean reshuffled);
}
