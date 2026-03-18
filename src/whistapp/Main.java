package whistapp;

import whistapp.application.Controller;
import whistapp.ui.MainMenuCLI;

public class Main {

    public static void main(String[] args) {

        MainMenuCLI menu = new MainMenuCLI(new Controller(), new whistapp.ui.ConsoleInputOutputProvider());
        menu.show();

    }
}