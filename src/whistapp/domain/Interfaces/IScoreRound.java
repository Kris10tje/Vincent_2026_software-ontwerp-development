package whistapp.domain.interfaces;

import java.util.LinkedHashMap;

import whistapp.domain.bids.BidType;
import whistapp.domain.players.Player;

public interface IScoreRound extends IRound {
    void registerBids(LinkedHashMap<Player, BidType> bids);
}
