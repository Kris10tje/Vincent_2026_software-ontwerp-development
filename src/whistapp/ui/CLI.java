/**
 * `ui/` is the PRESENTATION layer.
 */

package whistapp.ui;

import java.util.Arrays;
import java.util.Objects;

import whistapp.application.Interfaces.IController;

/**
 * Base class for Command Line Interfaces used in the Whist application.
 *
 * <p>Provides common input utilities for reading user input,
 * selecting options, and clearing the terminal screen.
 */
public abstract class CLI {

    protected IController controller;
    protected InputOutputProvider ioProvider;

    /* -------------------------------------------------------------------------- */
    /*                                Constructors                                */
    /* -------------------------------------------------------------------------- */

    /**
     * Create a CLI bound to the given controller.
     *
     * @param controller the application controller used by the CLI
     */
    public CLI(IController controller, InputOutputProvider ioProvider) {
        this.controller = controller;
        this.ioProvider = ioProvider;
    }

    /* -------------------------------------------------------------------------- */
    /*                               Protected Methods                            */
    /* -------------------------------------------------------------------------- */

    /**
     * Prompt the user for a string input.
     *
     * @param prompt the message shown to the user (: is added automatically)
     * @return the string entered by the user
     */
    protected String getInputString(String prompt) {
        ioProvider.writeLine(prompt + ": ");
        return ioProvider.readLine();
    }

    /**
     * Prompt the user for an integer input.
     *
     * <p>If invalid input is provided the user is asked again.
     *
     * @param prompt the message shown to the user
     * @return the integer entered by the user
     */
    protected int getInputInt(String prompt) {
        while (true) {
            try {
                ioProvider.writeLine(prompt + ": ");
                return Integer.parseInt(ioProvider.readLine().trim());
            } catch (Exception e) {
                clearScreen();
                ioProvider.writeLine("Invalid integer.\nPress enter to try again.");
                ioProvider.readLine();
            }
        }

    }

    /**
     * Prompt the user to choose one option from a list.
     *
     * @param question the question shown to the user
     * @param options  the available options
     * @return the selected option
     */
    protected <T> T getChoice(String question, T[] options) {

        ioProvider.writeLine(question);

        // Print all options
        for (int i = 0; i < options.length; i++) {
            ioProvider.writeLine("\t" + (i + 1) + ". " + options[i]);
        }

        // Print the question
        ioProvider.writeLine("--> ");

        // Validate the integer provided
        int choice;
        try {
            choice = Integer.parseInt(ioProvider.readLine().trim());
        } catch (NumberFormatException e) {
            ioProvider.writeLine("Invalid number. Press enter to try again.");
            ioProvider.readLine();
            return getChoice(question, options);
        }

        if (choice <= 0 || choice > options.length) {
            ioProvider.writeLine("Invalid choice. Press enter to try again.");
            ioProvider.readLine();
            return getChoice(question, options);
        }

        // Return the selected option
        return options[choice - 1];
    }

    /**
     * Prompt the user for a yes/no decision.
     *
     * @param question the question shown to the user
     * @return {@code true} if the user selects "Yes", otherwise {@code false}
     */
    protected boolean getYesNo(String question) {

        // Get user selection
        String response = getChoice(question, new String[]{"No", "Yes"});

        // Return true if the user selects "Yes"
        return Objects.equals(response, "Yes");

    }

    /**
     * Prompt the user to select <i>multiple</i> options from a list.
     *
     * <p>The user can toggle options on or off until the selection
     * is completed.
     *
     * @param question the question shown to the user
     * @param options  the available options
     * @return an array containing the selected options
     */
    protected <T> T[] getChoices(String question, T[] options) {
        boolean[] active = new boolean[options.length];

        while (true) {
            clearScreen();

            // Print the question
            ioProvider.writeLine(question);

            // Print all options
            for (int i = 0; i < options.length; i++) {
                ioProvider.writeLine("\t" + (i + 1) + ". [" + (active[i] ? "x" : " ") + "] " + options[i]);
            }

            // Print the complete selection option
            ioProvider.writeLine("\t0. Complete selection");
            ioProvider.writeLine("Enter option number to toggle (0 to finish): ");

            // Validate the integer provided
            int choice;
            try {
                choice = Integer.parseInt(ioProvider.readLine().trim());
            } catch (NumberFormatException e) {
                ioProvider.writeLine("Invalid number. Press enter to try again.");
                ioProvider.readLine();
                continue;
            }

            // If the user wants to finish
            if (choice == 0) {
                // Count the number of active options
                int count = 0;
                for (boolean b : active)
                    if (b)
                        count++;

                // Create an array of the selected options
                T[] result = Arrays.copyOf(options, count);

                // Copy the selected options to the result array
                int idx = 0;
                for (int i = 0; i < active.length; i++) {
                    if (active[i])
                        result[idx++] = options[i];
                }

                return result;
            } else if (choice >= 1 && choice <= options.length) {
                // Toggle the selected option
                active[choice - 1] = !active[choice - 1];
            } else {
                // Invalid choice
                ioProvider.writeLine("Invalid choice. Press enter to try again.");
                ioProvider.readLine();
            }
        }
    }

    /**
     * Clear the terminal screen.
     *
     * <p>This method uses ANSI escape codes and only works in terminals
     * that support them.
     *
     * <p>It may not work in Windows CMD or some IDE terminals,
     * but works in most Linux terminals and Windows PowerShell.
     */
    protected void clearScreen() {
        // Fallback newlines to push content down if ANSI isn't fully supported
        // for (int i = 0; i < 50; i++) System.out.println();

        ioProvider.writeLine("\033[H\033[2J"); // ANSI: clear screen + home
        ioProvider.flush();
    }

    /**
     * Prints a decorative separator line for better readability in the terminal.
     */
    protected void printSeparator() {
        ioProvider.writeLine("------------------------------------------------------------");
    }

    /**
     * A helper method for showing messages to the user in a consistent format.
     *
     * @param message the message to show
     */
    protected void informUser(String message) {
        ioProvider.writeLine("");
        printSeparator();
        ioProvider.writeLine(message);
        printSeparator();
        ioProvider.writeLine("");
    }

}
