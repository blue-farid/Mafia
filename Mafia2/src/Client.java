import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Client.
 */
public class Client implements Runnable , Serializable {
    private Player player;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private boolean firstNight = true;
    private int toFire = -1;
    private boolean response = false;
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
            System.out.println("when you ready, enter your username to start:");
            String username = "";
            while (true) {
                username = scanner.nextLine();
                out.writeObject(username);
                boolean state = (Boolean) in.readObject();
                if (state) {
                    break;
                } else {
                    System.out.println("this username is not available. choose another!");
                }
            }
            player = (Player) in.readObject();
            System.out.println("your role is: " + player.getClass().getName());
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
        } catch (SocketException e) {
            System.exit(0);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Is response boolean.
     *
     * @return the boolean
     */
    public boolean isResponse() {
        return response;
    }

    /**
     * Sets response.
     *
     * @param response the response
     */
    public void setResponse(boolean response) {
        this.response = response;
    }

    /**
     * Sets to fire.
     *
     * @param toFire the to fire
     */
    public void setToFire(int toFire) {
        this.toFire = toFire;
    }

    /**
     * Gets to fire.
     *
     * @return the to fire
     */
    public int getToFire() {
        return toFire;
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
