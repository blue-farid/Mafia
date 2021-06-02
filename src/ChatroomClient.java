import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatroomClient {

    private Client client;

    public ChatroomClient(Client client) {
        this.client = client;
    }

    public void start() {
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(new Writer(client));
        pool.execute(new Reader(client));
    }
    private class Writer implements Runnable {
        private Client client;

        public Writer(Client client) {
            this.client = client;
        }

        @Override
        public synchronized void run() {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String text = scanner.nextLine();
                try {

                    client.getObjectOutputStream().writeObject(client.getPlayer().getName() + ": " + text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Reader implements Runnable {
        private Client client;

        public Reader(Client client) {
            this.client = client;
        }

        @Override
        public synchronized void run() {
            while (true) {
                try {
                    String text = (String) client.getObjectInputStream().readObject();
                    System.out.println(text);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
