import java.io.IOException;

public class Reader implements Runnable {
    private Client client;

    public Reader(Client client) {
        this.client = client;
    }

    public Object reader() {
        try {
            return client.getObjectInputStream().readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public synchronized void run() {
        while (true) {
            System.out.println(reader());
        }
    }
}
