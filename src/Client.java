import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client implements Runnable , Serializable {
    private Player player;
    private String name;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    @Override
    public void run() {
        String command = "";
        Scanner scanner = new Scanner(System.in);
        try {
            Socket client = new Socket("127.0.0.1" , 127);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            System.out.println("*****************");
            System.out.println("welcoming message.");
            System.out.println("*****************");
            System.out.println();
            System.out.println("please enter a username:");
            String username = scanner.nextLine();
            player = (Player) in.readObject();
            System.out.println("your role is: " + player.getClass().getName());
            name = username;
            player.setName(username);
            out.writeObject(player);
            System.out.println("you has been added to the game.");
            System.out.println("please wait for other players to join...");
            String wait = (String) in.readObject();
            System.out.println(wait);
            updateGod((God) in.readObject());
            System.out.println("the game is on!");
            // first night
            command = (String) in.readObject();
            commandLine(command);
            God.getGod().getNight().setFirstNight(false);
            command = (String) in.readObject();
            commandLine(command);
            new ChatroomClient(this).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public ObjectInputStream getObjectInputStream() {
        return in;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return out;
    }
    public void updateGod(God god) {
        God.getGod().setGod(god);
    }

    public void commandLine(String command) throws IOException, ClassNotFoundException {
        if (command.equals("WakeUp")) {
            God.getGod().getNight().openEyes(player);
            if (player instanceof Mafia && ! God.getGod().getNight().isFirstNight()) {
                for (int i = 0; i < God.getGod().getMafias().size(); i++) {
                    System.out.println(in.readObject());
                }
            }
            out.writeObject("");
        }
    }
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(new Client());
    }
}
