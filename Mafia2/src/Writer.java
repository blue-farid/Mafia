import java.io.IOException;
import java.util.Scanner;

public class Writer implements Runnable{
    private Client client;

    public Writer(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String text = scanner.nextLine();
            try {
                client.getObjectOutputStream().writeObject(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
