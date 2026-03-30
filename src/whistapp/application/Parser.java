package whistapp.application;

import whistapp.domain.cards.Suit;
import whistapp.domain.cards.Value;
import whistapp.domain.interfaces.ICard;

public class Parser {

	public static String parseCard(ICard card){
		String parsedValue = parseCardValue(card.getValue());
		String parsedSuit = parseCardSuit(card.getSuit());
		return parsedValue + " of " + parsedSuit;
	}

	public static String parseCardValue(Value value){
		return switch (value) {
			case ACE -> "Ace";
			case KING -> "King";
			case QUEEN -> "Queen";
			case JACK -> "Jack";
			default -> value.toString(); // 2, 3, 4, 5, 6, 7, 8, 9
		};
	}

	public static String parseCardSuit(Suit suit){
		// Convert to lowercase and then capitalize first letter
		String lowercaseWord = suit.name().toLowerCase();
		String uppercaseFirstletter = String.valueOf(lowercaseWord.charAt(0)).toUpperCase();
		return uppercaseFirstletter + lowercaseWord.substring(1);
	}

}
