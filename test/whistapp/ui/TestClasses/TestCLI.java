package whistapp.ui.TestClasses;

import whistapp.domain.Interfaces.IController;
import whistapp.ui.CLI;
import whistapp.ui.InputOutputProvider;

public class TestCLI extends CLI {
        public TestCLI(IController controller, InputOutputProvider inputProvider) {
            super(controller, inputProvider);
        }

        public String callGetInputString(String prompt) {
            return getInputString(prompt);
        }

        public int callGetInputInt(String prompt) {
            return getInputInt(prompt);
        }

        public <T> T callGetChoice(String question, T[] options) {
            return getChoice(question, options);
        }
        public boolean callGetYesNo(String question) {
            return getYesNo(question);
        }
        public <T> T[] callGetChoices(String question, T[] options) {
            return getChoices(question, options);
        }
    }

    
