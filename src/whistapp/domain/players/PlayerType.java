package whistapp.domain.players;

import whistapp.domain.players.strategy.HighBotStrategy;
import whistapp.domain.players.strategy.HumanStrategy;
import whistapp.domain.players.strategy.LowBotStrategy;
import whistapp.domain.players.strategy.PlayerStrategy;

import java.util.Arrays;

/**
 * An enumeration of the available player types in the game.
 */
public enum PlayerType {

    HUMAN("Human player"),
    LOW_BOT("Low Bot"),
    HIGH_BOT("High Bot");

    private final String label;

    PlayerType(String label) {
        this.label = label;
    }

    /**
     * A simple getter for the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * A getter for all player types that are bots.
     */
    public static PlayerType[] getBotTypes() {
        // We remove the human player from the list
        return Arrays.stream(values()).filter(playerType -> !playerType.equals(PlayerType.HUMAN)).toArray(PlayerType[]::new);
    }

    /**
     * A getter for the strategy of a player of this type.
     */
    /* moved to playerfactory */
    /* public PlayerStrategy getStrategy() {
        return switch (this) {
            case LOW_BOT -> new LowBotStrategy();
            case HIGH_BOT -> new HighBotStrategy();
            default -> new HumanStrategy();
        };
    }*/

    @Override
    public String toString() {
        return label;
    }
}
