package whistapp.application;

import java.util.ArrayList;
import java.util.HashMap;

import whistapp.application.interfaces.IGameController;
import whistapp.domain.interfaces.IGame;
import whistapp.domain.interfaces.IPlayer;

public class GameController<TGame extends IGame>  implements IGameController {
    protected TGame game;
    
    public void startNewRound(){
        game.startNewRound();
    }

    public ArrayList<IPlayer> getPlayers(){
        return game.getPlayers();
    }

    public ArrayList<String> getPlayerNames(){
        return game.getPlayerNames();
    }

    public void updateScores(HashMap<IPlayer, Integer> tricksPerPlayer) {
        game.updateScores(tricksPerPlayer);
    }

    public HashMap<IPlayer, Integer> getScoresPerPlayer() {
        return game.getScoresPerPlayer();
    }

}
