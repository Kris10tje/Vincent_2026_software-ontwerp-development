package whistapp.application.Interfaces;

import java.util.ArrayList;
import java.util.HashMap;

public interface IGameController {
    void startNewRound();
    ArrayList<String> getPlayerNames();
    void updateScores(HashMap<String, Integer> tricksPerPlayer);
    HashMap<String, Integer> getScoresPerPlayer();
}
