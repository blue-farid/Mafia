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
//        new Writer(client);
//        pool.execute(new ClientReader(client));
    }
}
