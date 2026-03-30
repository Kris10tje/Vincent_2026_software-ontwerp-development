package whistapp.ui;

import whistapp.domain.cards.Suit;
import whistapp.domain.cards.Value;
import whistapp.domain.interfaces.ICard;

import java.util.Locale;

/**
 * Utility class responsible for formatting domain Card objects into human-readable strings.
 * 
 * <p>This class embodies the GRASP Information Expert principle: the UI layer (where presentation
 * concerns live) is responsible for formatting cards for display. By keeping formatting logic in the
 * UI package, we maintain proper Separation of Concerns and avoid violating layer boundaries.
 * 
 * <p>The Application layer (controllers) returns domain objects (ICard), and the UI layer uses
 * this formatter to present them to users. This keeps the Application layer decoupled from
 * presentation logic.
 */
public class CardFormatter {

    /**
     * Format a Card for display.
     * 
     * @param card the card to format
     * @return a user-friendly string representation (e.g., "Ace of Hearts")
     */
    public static String formatCard(ICard card) {
        String formattedValue = formatCardValue(card.getValue());
        String formattedSuit = formatCardSuit(card.getSuit());
        return formattedValue + " of " + formattedSuit;
    }

    /**
     * Format a Card's value for display.
     * 
     * @param value the card value to format
     * @return the formatted value (e.g., "King", "Queen", "10")
     */
    public static String formatCardValue(Value value) {
        return switch (value) {
            case ACE -> "Ace";
            case KING -> "King";
            case QUEEN -> "Queen";
            case JACK -> "Jack";
            default -> value.toString().charAt(0) + value.toString().toLowerCase().substring(1);
        };
    }

    /**
     * Format a Card's suit for display.
     * 
     * @param suit the card suit to format
     * @return the formatted suit with proper capitalization (e.g., "Hearts")
     */
    public static String formatCardSuit(Suit suit) {
        // Convert to lowercase and then capitalize first letter
        String lowercaseWord = suit.name().toLowerCase();
        String uppercaseFirstLetter = String.valueOf(lowercaseWord.charAt(0)).toUpperCase();
        return uppercaseFirstLetter + lowercaseWord.substring(1);
    }
}
