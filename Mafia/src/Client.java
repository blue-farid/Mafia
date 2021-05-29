import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client implements Runnable , Serializable {
    private String name;
    private Player player;

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        try (Socket client = new Socket("127.0.0.1" , 127)) {
            InputStream inputStream = client.getInputStream();
            OutputStream outputStream = client.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            System.out.println("*****************");
            System.out.println("welcoming message.");
            System.out.println("*****************");
            System.out.println();
            System.out.println("please enter a username:");
            String username = scanner.nextLine();
            ObjectInputStream in = new ObjectInputStream(inputStream);
            player = (Player) in.readObject();
            System.out.println("your role is: " + player.getClass().getName());
            name = username;
            player.setName(username);
            out.writeObject(this);
            System.out.println("you has been added to the game.");
            System.out.println("please wait for other players to join...");
            String wait = (String) in.readObject();
            System.out.println(wait);
            System.out.println("the game is on!");
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

    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(new Client());
    }
}
