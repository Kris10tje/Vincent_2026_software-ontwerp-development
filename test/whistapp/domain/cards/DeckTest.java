package whistapp.domain.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    private Deck deck;

    @BeforeEach
    void setUp() {
        // This runs before every test, ensuring a fresh deck
        deck = new Deck();
    }

    @Test
    void testDeckInitialization() {
        // A standard deck should have exactly 52 cards
        assertEquals(52, deck.cardsRemaining(), "A new deck should have 52 cards.");
    }

    @Test
    void testDealCardDecreasesRemaining() {
        deck.dealCard();
        assertEquals(51, deck.cardsRemaining(), "Dealing one card should leave 51 cards.");
    }

    @Test
    void testDealHand() {
        List<Card> hand = deck.dealHand(13);

        assertEquals(13, hand.size(), "Dealt hand should contain exactly 13 cards.");
        assertEquals(39, deck.cardsRemaining(), "Deck should have 39 cards left after dealing 13.");
    }

    @Test
    void testShuffleChangesOrder() {
        Deck unshuffledDeck = new Deck();
        deck.shuffle();

        // Draw the top 5 cards of both decks and compare
        boolean isDifferent = false;
        for (int i = 0; i < 5; i++) {
            Card card1 = unshuffledDeck.dealCard();
            Card card2 = deck.dealCard();
            // Assuming Card class has properly implemented equals()
            if (!card1.equals(card2)) {
                isDifferent = true;
                break;
            }
        }

        assertTrue(isDifferent, "Shuffling should change the order of the cards.");
    }

    @Test
    void testDeckContainsNoDuplicates() {
        // Verify that a standard deck doesn't accidentally create two Ace of Spades
        Set<Card> uniqueCards = new HashSet<>();
        while (deck.cardsRemaining() > 0) {
            uniqueCards.add(deck.dealCard());
        }

        // A Set automatically removes duplicates. If it's 52, there were no duplicates.
        assertEquals(52, uniqueCards.size(), "Deck should contain exactly 52 unique cards.");
    }

    @Test
    void testExceptionWhenDealingFromEmptyDeck() {
        // Deal all 52 cards
        deck.dealHand(52);

        // The 53rd card should throw an exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            deck.dealCard();
        });

        assertEquals("Cannot deal from an empty deck.", exception.getMessage());
    }
}