package whistapp.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import whistapp.application.interfaces.IScoreGameController;
import whistapp.domain.interfaces.IScoreGame;
import whistapp.domain.players.PlayerType;
import whistapp.domain.bids.BidType;
import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.game.ScoreGame;

public class ScoreGameController extends GameController<IScoreGame> implements IScoreGameController {
    
    public ScoreGameController(ArrayList<String> playerList){
        LinkedHashMap<String, PlayerType> playerMap = playerList.stream()
            .collect(Collectors.toMap(
                player -> player,               // The Key: the string from the list
                player -> PlayerType.HUMAN,     // The Value: your constant enum
                (existing, replacement) -> existing, // Merge function (handles duplicate keys)
                LinkedHashMap::new              // Supplier: ensures we get a LinkedHashMap
            ));
        game = new ScoreGame(createPlayerList(playerMap, null));
    }

    public void updateScores(HashMap<IPlayer, Integer> tricksPerPlayer) {
        game.updateScores(tricksPerPlayer);
    }

    public void registerBids(HashMap<IPlayer, BidType> bids) {
        game.registerBids(bids);
    }

    public void setReshuffledState(boolean reshuffled) {
        game.setReshuffledState(reshuffled);
    }

}
