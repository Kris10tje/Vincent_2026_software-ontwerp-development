package whistapp.application;

import java.util.ArrayList;
import java.util.HashMap;

import whistapp.application.Interfaces.IGameController;
import whistapp.domain.Interfaces.IGame;

public class GameController<TGame extends IGame>  implements IGameController {
    protected TGame game;
    
    public void startNewRound(){
        game.startNewRound();
    }

    public ArrayList<String> getPlayerNames(){
        return game.getPlayerNames();
    }

    public void updateScores(HashMap<String, Integer> tricksPerPlayer) {
        game.updateScores(tricksPerPlayer);
    }

    public HashMap<String, Integer> getScoresPerPlayer() {
        return game.getScoresPerPlayer();
    }
}
