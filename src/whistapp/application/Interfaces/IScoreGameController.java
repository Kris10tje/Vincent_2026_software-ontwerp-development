package whistapp.application.Interfaces;

import java.util.HashMap;

public interface IScoreGameController extends IGameController {
    void registerBids(HashMap<String, String> bids);
    void setReshuffledState(boolean reshuffled);
}
