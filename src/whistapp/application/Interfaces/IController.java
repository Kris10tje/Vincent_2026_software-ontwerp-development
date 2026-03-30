package whistapp.application.interfaces;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import whistapp.domain.cards.Suit;
import whistapp.domain.players.PlayerType;

public interface IController {
    void exit();
    IScoreGameController startNewScoreGame(ArrayList<String> playerNames);
    IPlayGameController startNewPlayGame(LinkedHashMap<String, PlayerType> playerNamesAndBotDifficulties);
    int getPlayerCount();
    String[] getBidTypes();
    PlayerType[] getBotTypes();
    Suit[] getSuits();
}
