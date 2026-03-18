package whistapp.application.Interfaces;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import whistapp.domain.cards.Suit;
import whistapp.domain.players.BotDifficulty;

public interface IController {
    void exit();
    IScoreGameController startNewScoreGame(ArrayList<String> playerNames);
    IPlayGameController startNewPlayGame(LinkedHashMap<String, BotDifficulty> playerNamesAndBotDifficulties);
    int getPlayerCount();
    String[] getBidTypes();
    BotDifficulty[] getBotDifficultyOptions();
    Suit[] getSuits();
}
