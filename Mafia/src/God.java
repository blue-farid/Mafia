import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class God {
    private static God god;
    private static Socket connection;
    private static int numberOfPlayers;
    private static boolean allPlayersJoined = false;
    private boolean inqury = false;
    private LinkedList<Player> roles = new LinkedList<>();
    private LinkedList<Client> clients = new LinkedList<>();
    private LinkedList<Player> players = new LinkedList<>();
    private LinkedList<Mafia> mafias = new LinkedList<>();
    private LinkedList<Citizen> citizens = new LinkedList<>();
    private LinkedList<Player> deads = new LinkedList<>();

    private Night night = new Night();

    private God() {}

    public  static God getGod() {
        if (god == null) {
            god = new God();
        }
        return god;
    }

    public synchronized static void main(String[] args) {
        getGod().createRoles();
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
                connection = welcomingSocket.accept();
                counter++;
                if ((numberOfPlayers - counter) != 0) {
                    System.out.println("a player joined! just " +
                            (numberOfPlayers - counter) + " more player...");
                }
                Thread t = new Thread(new NewPlayerHandler(connection));
                pool.execute(t);
            }
            System.out.println("all of the players joined!");
            God.getGod().somePlayersAreNotReady();
            System.out.println("all of the players are ready now!");
            System.out.println("the game is on");
            allPlayersJoined = true;
            God.getGod().fillMafias();
            God.getGod().fillCitizens();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addClient(Client client) {
        clients.add(client);
        addPlayer(client.getPlayer());
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

    public void somePlayersAreNotReady() {
        if (God.getGod().getPlayers().size() < numberOfPlayers) {
            System.out.println("some players are not ready yet.");
            while (God.getGod().getPlayers().size() < numberOfPlayers) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Player typeToObj(Player player) {
        int index = players.indexOf(player);
        return players.get(index);
    }

    public LinkedList<Player> getDeads() {
        return deads;
    }


    public LinkedList<Mafia> getMafias() {
        return mafias;
    }

    public static boolean isAllPlayersJoined() {
        return allPlayersJoined;
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

    public LinkedList<Player> getPlayers() {
        return players;
    }

    public Night getNight() {
        return night;
    }

    public static Socket getConnection() {
        return connection;
    }
}

class NewPlayerHandler extends Thread {
    private Socket client;
    private InputStream inputStream;
    private OutputStream outputStream;
    public NewPlayerHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            inputStream = client.getInputStream();
            outputStream = client.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(God.getGod().randRole());
            ObjectInputStream in = new ObjectInputStream(inputStream);
            God.getGod().addClient((Client) in.readObject());
            while (!God.isAllPlayersJoined()) {
                Thread.sleep(5000);
            }
            out.writeObject("all of the players joined!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
