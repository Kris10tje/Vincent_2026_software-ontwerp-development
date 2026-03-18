package whistapp.domain.Interfaces;

import java.util.LinkedHashMap;

public interface ITrick {
    LinkedHashMap<IPlayer, String> getCardsAsStrings();
}
