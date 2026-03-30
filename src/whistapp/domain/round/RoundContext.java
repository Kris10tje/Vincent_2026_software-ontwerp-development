package whistapp.domain.round;

import whistapp.domain.cards.Suit;

public class RoundContext {

    private Suit trumpSuit;

    private Suit currentTrickSuit;

    RoundContext(Suit trumpSuit, Suit currentTrickSuit) {
        this.trumpSuit = trumpSuit;
        this.currentTrickSuit = currentTrickSuit;
    }

    /**
     * A getter for the trump suit of the round context.
     */
    public Suit getTrumpSuit() {
        return trumpSuit;
    }

    /**
     * A getter for the leading suit of the current trick.
     */
    public Suit getCurrentTrickSuit() {
        return currentTrickSuit;
    }
}
