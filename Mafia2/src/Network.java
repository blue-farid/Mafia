import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

/**
 * The static class Network.
 */
public class Network {
    /**
     * The newPlayerHandlers.
     */
    public static LinkedList<NewPlayerHandler> newPlayerHandlers = new LinkedList<>();

    /**
     * Send an object to mafias.
     *
     * @param obj the obj
     */
    public static void sendToMafias(Object obj) {
        for (Mafia mafia: God.getGod().getMafias()) {
            try {
                God.getGod().playerToClient(mafia).getObjectOutputStream().writeObject(obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send an object to all.
     *
     * @param obj the obj
     */
    public static void sendToAll(Object obj) {
        for (NewPlayerHandler newPlayerHandler: newPlayerHandlers) {
            try {
                newPlayerHandler.getObjectOutputStream().writeObject(obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send an object to player.
     *
     * @param obj    the obj
     * @param player the player
     */
    public static void sendToPlayer(Object obj, Player player) {
        try {
            God.getGod().playerToClient(player).getObjectOutputStream().writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ready for reading the objects from each client.
     *
     * @param pool the pool.
     */
    public static void readyForReading(ExecutorService pool) {
        for (NewPlayerHandler newPlayerHandler: Network.newPlayerHandlers) {
            pool.execute(new ServerReader(newPlayerHandler));
        }
    }

    /**
     * do required tasks for a client exit.
     *
     * @param newPlayerHandler the new player handler
     * @throws IOException the io exception
     */
    public static void exit(NewPlayerHandler newPlayerHandler) throws IOException {
        God.getGod().removePlayer(newPlayerHandler.getPlayer());
        God.getGod().getPlayers().remove(newPlayerHandler.getPlayer());
        Network.newPlayerHandlers.remove(newPlayerHandler);
        newPlayerHandler.getSocket().close();
    }
}
