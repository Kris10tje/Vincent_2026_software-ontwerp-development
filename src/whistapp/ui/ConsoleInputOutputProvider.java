package whistapp.ui;

import java.util.Scanner;

public class ConsoleInputOutputProvider implements IInputOutputProvider{
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public void writeLine(String line) {
        System.out.println(line);
    }

    @Override
    public void flush() {
        System.out.flush();
    }
}
