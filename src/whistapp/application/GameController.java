package whistapp.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.application.interfaces.IGameController;
import whistapp.domain.interfaces.IGame;
import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.interfaces.IPlayerInputProvider;
import whistapp.domain.players.Player;
import whistapp.domain.players.PlayerFactory;
import whistapp.domain.players.PlayerType;

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

    protected ArrayList<Player> createPlayerList(LinkedHashMap<String, PlayerType> playerNamesAndBotDifficulties, IPlayerInputProvider playerInputProvider){
        var playerFactory = new PlayerFactory(playerInputProvider);
        ArrayList<Player> players = new ArrayList<>();
        for (HashMap.Entry<String, PlayerType> entry : playerNamesAndBotDifficulties.entrySet()) {    
            String name = entry.getKey();
            PlayerType type = entry.getValue();
            players.add(playerFactory.createPlayer(name, type));
        }
        return players;
    }

}
