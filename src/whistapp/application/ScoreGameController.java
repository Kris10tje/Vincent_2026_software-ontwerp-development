package whistapp.application;

import java.util.ArrayList;
import java.util.HashMap;

import whistapp.application.interfaces.IScoreGameController;
import whistapp.domain.interfaces.IScoreGame;
import whistapp.domain.bids.BidType;
import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.game.ScoreGame;

public class ScoreGameController extends GameController<IScoreGame> implements IScoreGameController {
    
    public ScoreGameController(ArrayList<String> players){
        game = new ScoreGame(players);
    }

    public void updateScores(HashMap<IPlayer, Integer> tricksPerPlayer) {
        game.updateScores(tricksPerPlayer);
    }

    public void registerBids(HashMap<IPlayer, BidType> bids) {
        game.registerBids(bids);
    }

    public void setReshuffledState(boolean reshuffled) {
        game.setReshuffledState(reshuffled);
    }

}
