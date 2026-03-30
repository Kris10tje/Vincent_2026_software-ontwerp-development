package whistapp.ui.TestClasses;

import whistapp.application.interfaces.IController;
import whistapp.application.interfaces.IPlayGameController;
import whistapp.ui.IInputOutputProvider;
import whistapp.ui.PlayGameCLI;

/**
 * Test-only adapter for {@link PlayGameCLI}.
 *
 * <p>The production methods {@code startNewGame()} and {@code showRound()} are protected.
 * This wrapper exposes them as public methods so unit tests can invoke them directly
 * while injecting Mockito mocks for dependencies.
 */
public class TestPlayGameCLI extends PlayGameCLI {
    public TestPlayGameCLI(IController controller, IInputOutputProvider inputProvider) {
        super(controller, inputProvider);
    }

    // Public entry point used by tests to execute the protected startNewGame flow.
    public void callStartNewGame() {
        startNewGame();
    }

    // Public entry point used by tests to execute the protected showRound flow.
    public void callShowRound() {
        showRound();
    }

    // Allows tests from outside the whistapp.ui package to inject a mock IPlayGameController.
    public void setGame(IPlayGameController mockGame) {
        this.specificGameController = mockGame;
    }

}
