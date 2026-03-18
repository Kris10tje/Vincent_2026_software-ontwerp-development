package whistapp.ui.TestClasses;

import whistapp.application.IController;
import whistapp.ui.InputOutputProvider;
import whistapp.ui.ScoreGameCLI;

public class TestScoreGameCLI extends ScoreGameCLI {
    public TestScoreGameCLI(IController controller, InputOutputProvider inputProvider) {
        super(controller, inputProvider);
    }

    public void callStartNewGame() {
        startNewGame();
    }

}
