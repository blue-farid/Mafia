import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public static void sendToAll(Object obj) {
        for (NewPlayerHandler newPlayerHandler: newPlayerHandlers) {
            try {
                newPlayerHandler.getObjectOutputStream().writeObject(obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendToPlayer(Object obj, Player player) {
        try {
            God.getGod().playerToClient(player).getObjectOutputStream().writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void readyForReading(ExecutorService pool) {
        for (NewPlayerHandler newPlayerHandler: Network.newPlayerHandlers) {
            pool.execute(new ServerReader(newPlayerHandler));
        }
    }
    public static void exit(NewPlayerHandler newPlayerHandler) throws IOException {
        God.getGod().removePlayer(newPlayerHandler.getPlayer());
        God.getGod().getPlayers().remove(newPlayerHandler.getPlayer());
        Network.newPlayerHandlers.remove(newPlayerHandler);
        newPlayerHandler.getSocket().close();
    }
}
