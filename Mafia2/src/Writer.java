import java.io.IOException;
import java.util.Scanner;

/**
 * The type Writer.
 */
public class Writer implements Runnable{
    private Client client;
    /**
     * Instantiates a new Writer.
     *
     * @param client the client
     */
    public Writer(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String text = scanner.nextLine();
            client.setResponse(true);
            try {
                client.getObjectOutputStream().writeObject(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
