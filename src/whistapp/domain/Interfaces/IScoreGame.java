package whistapp.domain.Interfaces;

import java.util.HashMap;

public interface IScoreGame extends IGame {
    void registerBids(HashMap<String, String> bids);
    void setReshuffledState(boolean reshuffled);
}
