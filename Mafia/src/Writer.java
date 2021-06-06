import java.io.IOException;
import java.util.Scanner;

public class Writer implements Runnable{
    private Client client;
    private Scanner scanner;
    public Writer(Client client , Scanner scanner) {
        this.client = client;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (!Thread.interrupted()) {
            String text = scanner.nextLine();
            try {
                client.getObjectOutputStream().writeObject(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
