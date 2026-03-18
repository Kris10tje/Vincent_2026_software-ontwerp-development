package whistapp.domain.Interfaces;

import java.util.LinkedHashMap;

import whistapp.domain.bids.BidType;

public interface IScoreRound extends IRound {
    void registerBids(LinkedHashMap<IPlayer, BidType> bids);
}
