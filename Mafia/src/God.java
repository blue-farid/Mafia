import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class God {
    private static God god;
    private LinkedList<Type> roles = new LinkedList<>();
    private LinkedList<Player> players = new LinkedList<>();
    private LinkedList<Player> deads = new LinkedList<>();
    private God() {}

    public  static God getGod() {
        if (god == null) {
            god = new God();
        }
        return god;
    }

    public static void main(String[] args) {
        God.getGod().createRoles();
        Scanner scanner = new Scanner(System.in);
        System.out.println("*****************");
        System.out.println("welcoming message.");
        System.out.println("*****************");
        System.out.println("\n" +
                "first enter the number of player to start the game: ");
        int numberOfPlayers = scanner.nextInt();
        System.out.println("OK. \n" +
                "you are the god of the game.\n" +
                "please wait for the players to join...");
        ExecutorService pool = Executors.newCachedThreadPool();
        int counter = 0;
        try (ServerSocket welcomingSocket = new ServerSocket(127)) {
            while (counter < numberOfPlayers) {
                Socket connection = welcomingSocket.accept();
                counter++;
                System.out.println("a player joined! just " +
                        (numberOfPlayers - counter) + "more player...");
                pool.execute(new NewPlayerHandler(connection) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addPlayer(Player player) {
        return players.add(player);
    }
    public boolean removePlayer(Player player) {
        deads.add(player);
        return players.remove(player);
    }
    public Type randRole() {
        Random random = new Random();
        return roles.get(random.nextInt(roles.size()));
    }
    public void createRoles() {
        // that for 10 players.
        roles.clear();
        roles.add(Citizen.class);
        roles.add(CityDoc.class);
        roles.add(Detective.class);
        roles.add(DieHard.class);
        roles.add(GodFather.class);
        roles.add(LecterDoc.class);
        roles.add(Mafia.class);
        roles.add(Mayor.class);
        roles.add(Psychologist.class);
        roles.add(Sniper.class);
    }
    public LinkedList<Player> getPlayers() {
        return players;
    }

    public LinkedList<Player> getDeads() {
        return deads;
    }

}

class NewPlayerHandler implements Runnable {
    private Socket connectionSocket;
    private Player player;

    public NewPlayerHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*****************");
        System.out.println("welcoming message.");
        System.out.println("*****************");
        System.out.println();
        System.out.println("please enter a username:");
        String username = scanner.nextLine();
        String role = God.getGod().randRole().getTypeName();
        /*
        do something with this and give a random role to player.
         */
        System.out.println("your role is: " + player.getClass().getName());
        God.getGod().addPlayer(player);
    }
}
