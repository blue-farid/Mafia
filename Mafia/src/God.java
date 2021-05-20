import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class God {
    private static God god;
    private static int numberOfPlayers;
    private boolean inqury = false;
    private LinkedList<Player> roles = new LinkedList<>();
    private LinkedList<Player> players = new LinkedList<>();
    private LinkedList<Mafia> mafias = new LinkedList<>();
    private LinkedList<Citizen> citizens = new LinkedList<>();
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
        numberOfPlayers = scanner.nextInt();
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
            pool.shutdown();
            System.out.println("all of the players joined!");
            God.getGod().fillMafias();
            God.getGod().fillCitizens();
            God.class.notifyAll();
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
    public Player randRole() {
        Random random = new Random();
        int randIndex = random.nextInt(roles.size());
        Player role = roles.get(randIndex);
        roles.remove(randIndex);
        return role;
    }
    public void createRoles() {
        // that for 10 players.
        roles.clear();
        roles.add(new Citizen(""));
        roles.add(new CityDoc(""));
        roles.add(new Detective(""));
        roles.add(new DieHard(""));
        roles.add(new GodFather(""));
        roles.add(new LecterDoc(""));
        roles.add(new Mafia(""));
        roles.add(new Mayor(""));
        roles.add(new Psychologist(""));
        roles.add(new Sniper("" , numberOfPlayers));
    }
    public void fillMafias() {
        for (Player player: players) {
            if (player instanceof Mafia) {
                Mafia mafia = (Mafia) player;
                mafias.add(mafia);
            }
        }
    }
    public void fillCitizens() {
        for (Player player: players) {
            if (player instanceof Citizen) {
                Citizen citizen = (Citizen) player;
                citizens.add(citizen);
            }
        }
    }

    public Player typeToObj(Player player) {
        int index = players.indexOf(player);
        return players.get(index);
    }
    public LinkedList<Player> getPlayers() {
        return players;
    }

    public LinkedList<Player> getDeads() {
        return deads;
    }

    public LinkedList<Mafia> getMafias() {
        return mafias;
    }

    public LinkedList<Citizen> getCitizens() {
        return citizens;
    }

    public void setInqury(boolean inqury) {
        this.inqury = inqury;
    }

    public static int getNumberOfPlayers() {
        return numberOfPlayers;
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
        player = God.getGod().randRole();
        System.out.println("your role is: " + player.getClass().getName());
        player.setName(username);
        God.getGod().addPlayer(player);
        System.out.println("you has been added to the game.");
        System.out.println("please wait for other players to join...");
        try {
            wait();
        } catch (InterruptedException e) {
            System.out.println("the game is on!");
        }
    }
}
