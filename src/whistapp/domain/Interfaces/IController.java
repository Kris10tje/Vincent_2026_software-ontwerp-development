package whistapp.domain.Interfaces;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import whistapp.domain.cards.Suit;
import whistapp.domain.players.BotDifficulty;

public interface IController {
    void exit();
    IScoreGame startNewScoreGame(ArrayList<String> playerNames);
    IPlayGame startNewPlayGame(LinkedHashMap<String, BotDifficulty> playerNamesAndBotDifficulties);
    BotDifficulty[] getBotDifficultyOptions();
    Suit[] getSuits();
}
