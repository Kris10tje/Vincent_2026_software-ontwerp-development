package whistapp.domain.Interfaces;

import whistapp.domain.cards.Suit;
import whistapp.domain.cards.Value;

public interface ICard {
    Suit getSuit();
    Value getValue();
    boolean isSameCard(String card);
}
