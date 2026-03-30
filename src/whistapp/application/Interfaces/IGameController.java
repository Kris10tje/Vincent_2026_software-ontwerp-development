package whistapp.application.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import whistapp.domain.interfaces.IPlayer;

public interface IGameController {
    void startNewRound();
    ArrayList<IPlayer> getPlayers();
    ArrayList<String> getPlayerNames();
    void updateScores(HashMap<IPlayer, Integer> tricksPerPlayer);
    HashMap<IPlayer, Integer> getScoresPerPlayer();
}
