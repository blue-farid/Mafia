import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Client.
 */
public class Client implements Runnable , Serializable {
    private Player player;
    private String name;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private boolean firstNight = true;
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        try {
            /*System.out.println("Enter the IP address:");
            String ip = scanner.nextLine();
            System.out.println("Enter th port:");
            int port = scanner.nextInt();*/
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
            God.getGod().setGod((God) in.readObject());
            System.out.println("the game is on!");
            // get ready for reading
            pool.execute(new ClientReader(this));
            pool.execute(new Writer(this));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets player.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets object input stream.
     *
     * @return the object input stream
     */
    public ObjectInputStream getObjectInputStream() {
        return in;
    }

    /**
     * Gets object output stream.
     *
     * @return the object output stream
     */
    public ObjectOutputStream getObjectOutputStream() {
        return out;
    }

    /**
     * Is first night boolean.
     *
     * @return the boolean
     */
    public boolean isFirstNight() {
        return firstNight;
    }

    /**
     * Sets first night.
     *
     * @param firstNight the first night
     */
    public void setFirstNight(boolean firstNight) {
        this.firstNight = firstNight;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new Client().run();
    }
}
