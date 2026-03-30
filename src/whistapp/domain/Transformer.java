package whistapp.domain;

import java.util.HashMap;
import java.util.LinkedHashMap;

import whistapp.domain.interfaces.IPlayer;
import whistapp.domain.players.Player;

public class Transformer {
    /**
     * A helper method for transforming incoming maps with Player as a key
     * into maps with IPlayer as a key to preserve abstraction boundaries.
     */
    public static <T> LinkedHashMap<IPlayer, T> transformPlayerMapToIPlayerMap(LinkedHashMap<Player, T> map) {
        LinkedHashMap<IPlayer, T> result = new LinkedHashMap<>();
        for (HashMap.Entry<Player, T> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * A helper method for transforming incoming maps with Player as a key
     * into maps with IPlayer as a key to preserve abstraction boundaries.
     */
    public static <T> HashMap<IPlayer, T> transformPlayerHashMapToIPlayerMap(HashMap<Player, T> map) {
        HashMap<IPlayer, T> result = new HashMap<>();
        for (HashMap.Entry<Player, T> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
