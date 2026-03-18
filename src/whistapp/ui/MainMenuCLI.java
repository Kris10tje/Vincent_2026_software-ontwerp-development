package whistapp.ui;

import whistapp.application.Controller;

public class MainMenuCLI extends CLI {

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    public MainMenuCLI(Controller controller) {
        super(controller);
    }

    /* -------------------------------------------------------------------------- */
    /*                                 Public Methods                             */
    /* -------------------------------------------------------------------------- */

    public void show() {

        System.out.println("Welcome to the game of Whist!");

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
                    new ScoreGameCLI(controller).show();
                    break;
                case "Play a virtual game":
                    new PlayGameCLI(controller).show();
                    break;
                case "Exit":
                    controller.exit();
                    break;
                default:
                    invalidChoice = true;
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }

        }

    }

}
