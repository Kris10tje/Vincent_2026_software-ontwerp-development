package whistapp.ui.TestClasses;

import whistapp.application.interfaces.IController;
import whistapp.application.interfaces.IScoreGameController;
import whistapp.domain.interfaces.IScoreGame;
import whistapp.ui.IInputOutputProvider;
import whistapp.ui.ScoreGameCLI;

/**
 * Test-only adapter for {@link ScoreGameCLI}.
 *
 * <p>The production method {@code startNewGame()} is protected. This wrapper exposes a public
 * method so tests can invoke that flow directly while injecting Mockito mocks for dependencies.
 */
public class TestScoreGameCLI extends ScoreGameCLI {
    public TestScoreGameCLI(IController controller, IInputOutputProvider inputProvider) {
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

    // Allows tests from outside the whistapp.ui package to inject a mock IScoreGameController.
    public void setGame(IScoreGameController mockGame) {
        this.specificGameController = mockGame;
    }

}
