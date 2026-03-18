package whistapp.domain.players;

/**
 * An enumeration of the available bot difficulty levels in the game.
 */
public enum BotDifficulty {

    LOW("Low Bot"),
    HIGH("High Bot");

    private final String label;

    BotDifficulty(String label) {
        this.label = label;
    }

    /**
     * A simple getter for the label.
     */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
