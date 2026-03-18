package whistapp.ui;

public interface InputOutputProvider {
    String readLine();
    void writeLine(String line);
    void flush();
}
