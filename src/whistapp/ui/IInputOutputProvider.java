package whistapp.ui;

/**
 * 
 */
public interface IInputOutputProvider {
    String readLine();
    void writeLine(String line);
    void flush();
}
