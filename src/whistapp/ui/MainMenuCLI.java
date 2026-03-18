package whistapp.ui;

import whistapp.application.Interfaces.IController;

public class MainMenuCLI extends CLI {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public MainMenuCLI(IController controller, InputOutputProvider ioProvider) {
        super(controller, ioProvider);
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Public Methods                             */
    /* -------------------------------------------------------------------------- */

    public void show() {

        ioProvider.writeLine("Welcome to the game of Whist!");

        boolean invalidChoice = true;

        // Loop until the user chooses to exit
        while (invalidChoice) {

            invalidChoice = false;

            String choice = getChoice(
                    "What would you like to do?",
                    new String[]{
                            "Start a new point counter for a physical game",
                            "Play a virtual game",
                            "Exit"
                    });

            switch (choice) {
                case "Start a new point counter for a physical game":
                    new ScoreGameCLI(controller, ioProvider).show();
                    break;
                case "Play a virtual game":
                    new PlayGameCLI(controller, ioProvider).show();
                    break;
                case "Exit":
                    controller.exit();
                    break;
                default:
                    invalidChoice = true;
                    ioProvider.writeLine("Invalid choice. Please try again.");
                    break;
            }

        }

    }

}
