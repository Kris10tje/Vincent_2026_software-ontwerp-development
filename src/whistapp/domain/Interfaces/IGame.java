package whistapp.domain.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

public interface IGame {
    void startNewRound();
    ArrayList<IPlayer> getPlayers();
    ArrayList<String> getPlayerNames();
    void updateScores(HashMap<IPlayer, Integer> tricksPerPlayer);
    HashMap<IPlayer, Integer> getScoresPerPlayer();
}
