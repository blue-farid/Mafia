import java.io.StreamCorruptedException;

/**
 * Reader Interface.
 */
public interface Reader {
    Object reader();
    void commandLine(Object command) throws StreamCorruptedException;
}