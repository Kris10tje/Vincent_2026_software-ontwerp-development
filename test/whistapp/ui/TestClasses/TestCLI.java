package whistapp.ui.TestClasses;

import whistapp.application.interfaces.IController;
import whistapp.ui.CLI;
import whistapp.ui.IInputOutputProvider;

/**
 * Test-only adapter for {@link CLI}.
 *
 * <p>{@code CLI} exposes core input helpers as protected methods. This wrapper re-exposes
 * them as public methods so unit tests can call them directly while still injecting Mockito
 * mocks for controller and I/O dependencies.
 * 
 * <p>We extend the CLI class because the methods in that class could be protected/private
 * and otherwise inaccessible to the test code.
 */
public class TestCLI extends CLI {
        public TestCLI(IController controller, IInputOutputProvider inputProvider) {
            super(controller, inputProvider);
        }

        // Pass-through method used by tests to exercise CLI.getInputString.
        public String callGetInputString(String prompt) {
            return getInputString(prompt);
        }

        // Pass-through method used by tests to exercise CLI.getInputInt.
        public int callGetInputInt(String prompt) {
            return getInputInt(prompt);
        }

        // Pass-through method used by tests to exercise CLI.getChoice.
        public <T> T callGetChoice(String question, T[] options) {
            return getChoice(question, options);
        }

        // Pass-through method used by tests to exercise CLI.getYesNo.
        public boolean callGetYesNo(String question) {
            return getYesNo(question);
        }

        // Pass-through method used by tests to exercise CLI.getChoices.
        public <T> T[] callGetChoices(String question, T[] options) {
            return getChoices(question, options);
        }
    }

    
