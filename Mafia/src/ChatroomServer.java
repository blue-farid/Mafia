import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatroomServer {

    private static ChatroomServer chatroomServer;

    private LinkedList<NewPlayerHandler> clients = new LinkedList<>();

    public void fillClients() {
        clients.clear();
        for (NewPlayerHandler newPlayerHandler: Network.newPlayerHandlers) {
            clients.add(newPlayerHandler);
        }
    }

    public void start() {
        ChatroomServer.getChatroomServer().fillClients();
        ExecutorService pool = Executors.newCachedThreadPool();
        for (NewPlayerHandler newPlayerHandler : clients) {
            pool.execute(new ClientHandler(newPlayerHandler));
        }
    }
    public static ChatroomServer getChatroomServer() {
        if (chatroomServer == null) {
            chatroomServer = new ChatroomServer();
        }
        return chatroomServer;
    }

    private class ClientHandler implements Runnable {
        private NewPlayerHandler newPlayerHandler;

        public  ClientHandler(NewPlayerHandler newPlayerHandler) {
            this.newPlayerHandler = newPlayerHandler;
        }

        @Override
        public synchronized void run() {
            ObjectOutputStream out = newPlayerHandler.getObjectOutputStream();
            ObjectInputStream in = newPlayerHandler.getObjectInputStream();
            while (true) {
                try {
                    String text = (String) in.readObject();
                    for (NewPlayerHandler newPlayerHandler: clients) {
                        newPlayerHandler.getObjectOutputStream().writeObject(text);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


