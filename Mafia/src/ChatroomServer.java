import java.io.*;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatroomServer {

    private static ChatroomServer chatroomServer;
    private Thread thread;
    private LinkedList<NewPlayerHandler> clients = new LinkedList<>();

    private ChatroomServer(){}
    public void fillClients() {
        clients.clear();
        for (NewPlayerHandler newPlayerHandler: Network.newPlayerHandlers) {
            clients.add(newPlayerHandler);
        }
    }

    public void start() throws IOException {
        ChatroomServer.getChatroomServer().fillClients();
        ExecutorService pool = Executors.newCachedThreadPool();
        for (NewPlayerHandler newPlayerHandler : clients) {
            pool.execute(new ClientHandler(newPlayerHandler));
        }
        int time = 5;
        thread = Thread.currentThread();
        for (int i = 0; i < time; i++) {
            Network.sendToAll((time - i) + " minute remaining...");
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                return;
            }
        }
        Network.sendToAll("Times Up!!");
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
        public void run() {
            boolean ready = true;
            ObjectOutputStream out = newPlayerHandler.getObjectOutputStream();
            ObjectInputStream in = newPlayerHandler.getObjectInputStream();
            while (God.getGod().getNumberOfPlayersWhoVotes() < God.getGod().getPlayers().size()) {
                try {
                    String text = (String) in.readObject();
                    if (text.equals("Ready")) {
                        if (ready) {
                            Network.sendToAll(newPlayerHandler.getPlayer().getName() + " is ready to vote!");
                            ready = false;
                            synchronized (God.getGod().getNumberOfPlayersWhoVotes()) {
                                int num = God.getGod().getNumberOfPlayersWhoVotes();
                                God.getGod().setNumberOfPlayersWhoVotes(++num);
                            }
                        } else {
                            System.out.println("don't repeat it! I know!");
                        }
                    }
                    else if (!newPlayerHandler.getPlayer().isMute() && newPlayerHandler.getPlayer().isAlive()) {
                        Network.sendToAll(newPlayerHandler.getPlayer().getName() + ": " + text);
                    } else {
                        out.writeObject("You do not allow to chat!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            thread.interrupt();
            God.getGod().setNumberOfPlayersWhoVotes(0);
            Network.sendToAll("All of the players are ready to vote.");
            return;
        }
    }
}


