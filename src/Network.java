import java.io.IOException;
import java.util.LinkedList;

public class Network {
    public static LinkedList<NewPlayerHandler> newPlayerHandlers = new LinkedList<>();

    public static void sendToMafias(Object obj) {
        for (Mafia mafia: God.getGod().getMafias()) {
            try {
                God.getGod().playerToClient(mafia).getObjectOutputStream().writeObject(obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
