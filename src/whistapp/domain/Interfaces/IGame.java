package whistapp.domain.Interfaces;

import java.util.ArrayList;
import java.util.HashMap;

public interface IGame {
    void startNewRound();
    ArrayList<String> getPlayerNames();
    void updateScores(HashMap<String, Integer> tricksPerPlayer);
    HashMap<String, Integer> getScoresPerPlayer();
}
