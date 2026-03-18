package whistapp.application;

import java.util.ArrayList;
import java.util.HashMap;

import whistapp.application.Interfaces.IScoreGameController;
import whistapp.domain.Interfaces.IScoreGame;
import whistapp.domain.game.ScoreGame;

public class ScoreGameController extends GameController<IScoreGame> implements IScoreGameController {
    
    public ScoreGameController(ArrayList<String> players){
        game = new ScoreGame(players);
    }

    public void updateScores(HashMap<String, Integer> tricksPerPlayer) {
        game.updateScores(tricksPerPlayer);
    }

    public void registerBids(HashMap<String, String> bids) {
        game.registerBids(bids);
    }

    public void setReshuffledState(boolean reshuffled) {
        game.setReshuffledState(reshuffled);
    }
}
