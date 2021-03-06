import java.io.*;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatroomServer {

    private static ChatroomServer chatroomServer;
    private LinkedList<NewPlayerHandler> clients = new LinkedList<>();
    private FileUtils fileUtils = new FileUtils();
    private boolean firstRun = true;
    private boolean timesUp = false;
    private int numberOfPlayersWhoReady = 0;

    private ChatroomServer() {
    }

    public void fillClients() {
        clients.clear();
        for (NewPlayerHandler newPlayerHandler : Network.newPlayerHandlers) {
            clients.add(newPlayerHandler);
        }
    }

    public void start() throws IOException {
        ChatroomServer.getChatroomServer().fillClients();
        timesUp = false;
        ChatroomServer.getChatroomServer().setNumberOfPlayersWhoReady(0);
        ExecutorService pool = Executors.newCachedThreadPool();
        for (NewPlayerHandler newPlayerHandler : clients) {
            pool.execute(new ClientHandler(newPlayerHandler));
        }
        Thread tiktokThread = new Thread(new TikTok(3));
        tiktokThread.start();
        while (!timesUp && ChatroomServer.getChatroomServer().getNumberOfPlayersWhoReady() < God.getGod().getPlayers().size()) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        tiktokThread.interrupt();
        Network.sendToAll("Chatroom is closing...");
        return;
    }

    public static ChatroomServer getChatroomServer() {
        if (chatroomServer == null) {
            chatroomServer = new ChatroomServer();
        }
        return chatroomServer;
    }


    public void setNumberOfPlayersWhoReady(int numberOfPlayersWhoReady) {
        this.numberOfPlayersWhoReady = numberOfPlayersWhoReady;
    }

    public Integer getNumberOfPlayersWhoReady() {
        return numberOfPlayersWhoReady;
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
            while (true) {
                try {
                    String text = (String) in.readObject();
                    Player player = newPlayerHandler.getPlayer();
                    if (text.equals("ready") && player.isAlive()) {
                        if (ready) {
                            Network.sendToAll(player.getName() + " is ready to vote!");
                            ready = false;
                            synchronized (ChatroomServer.getChatroomServer().getNumberOfPlayersWhoReady()) {
                                int num = ChatroomServer.getChatroomServer().getNumberOfPlayersWhoReady();
                                ChatroomServer.getChatroomServer().setNumberOfPlayersWhoReady(++num);
                            }
                        } else {
                            Network.sendToPlayer("don't repeat it! I know you are ready!",player);
                        }
                    } else if(text.equals("Chatroom is closing...")) {
                        return;
                    } else if (text.equals("exit")) {
                        Network.exit(newPlayerHandler);
                    }
                    else if (text.equals("history")) {
                        if (firstRun) {
                            Network.sendToPlayer("Nothing to show!",player);
                        }
                        else {
                            String messages = fileUtils.loadMessages("messages.txt");
                            Network.sendToPlayer(messages, player);
                        }
                    }
                    else if (text.equals("BreakTheBlock"));
                    else if (!player.isMute() && player.isAlive()) {
                        String res = player.getName() + ": " + text;
                        Network.sendToAll(res);
                        File file = new File("messages.txt");
                        if (firstRun && file.exists()) {
                            file.delete();
                            firstRun = false;
                        }
                        fileUtils.saveMessage(res,file);
                    }
                    else {
                        out.writeObject("You do not allow to chat!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class TikTok implements Runnable {
        private int time;

        public TikTok(int time) {
            this.time = time;
        }

        @Override
        public void run() {
            tiktok(time);
        }
        public void tiktok(int time) {
            for (int i = 0; i < time; i++) {
                Network.sendToAll(((time - i) + " minute remaining..."));
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    return;
                }
            }
            Network.sendToAll("TimesUp!");
            timesUp = true;
        }
    }
}


