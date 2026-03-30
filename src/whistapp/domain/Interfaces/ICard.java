package whistapp.domain.interfaces;

import whistapp.domain.cards.Suit;
import whistapp.domain.cards.Value;

public interface ICard {
	Value getValue();
	Suit getSuit();
	boolean isSameCard(ICard card);
}
