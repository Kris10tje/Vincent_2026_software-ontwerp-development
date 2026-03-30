package whistapp.domain.players;

import whistapp.domain.interfaces.IPlayerInputProvider;
import whistapp.domain.players.strategy.HighBotStrategy;
import whistapp.domain.players.strategy.HumanStrategy;
import whistapp.domain.players.strategy.LowBotStrategy;
import whistapp.domain.players.strategy.PlayerStrategy;

public class PlayerFactory {
    private final IPlayerInputProvider inputProvider;

    // Inject the CLI implementation here
    public PlayerFactory(IPlayerInputProvider inputProvider) {
        this.inputProvider = inputProvider;
    }

    public Player createPlayer(String name, PlayerType type) {
        PlayerStrategy strategy = switch (type) {
            case HUMAN -> new HumanStrategy(inputProvider);
            case LOW_BOT -> new LowBotStrategy();
            case HIGH_BOT -> new HighBotStrategy();
        };
        
        return new Player(name, strategy);
    }
}
