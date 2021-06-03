import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class God implements Serializable {
    private static God god;
    private static Socket connection;
    private int numberOfPlayers;
    private boolean waiting = true;
    private boolean inqury = false;
    private boolean firstNight = true;
    private Integer numberOfMafiasWhoVotes = 0;
    private LinkedList<Player> roles = new LinkedList<>();
    private LinkedList<Player> players = new LinkedList<>();
    private LinkedList<Mafia> mafias = new LinkedList<>();
    private LinkedList<Citizen> citizens = new LinkedList<>();
    private LinkedList<Player> deads = new LinkedList<>();

    private God(){}

    public static God getGod() {
        if (god == null) {
            god = new God();
        }
        return god;
    }
    public void setGod(God god) {
        this.god = god;
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*****************");
        System.out.println("welcoming message.");
        System.out.println("*****************");
        System.out.println("\n" +
                "first enter the number of player to start the game: ");
        getGod().setNumberOfPlayers(scanner.nextInt());
        getGod().createRoles();
        System.out.println("OK. \n" +
                "you are the god of the game.\n" +
                "please wait for the players to join...");
        ExecutorService pool = Executors.newCachedThreadPool();
        int counter = 0;
        try {
            ServerSocket welcomingSocket = new ServerSocket(127);
            while (counter < getGod().getNumberOfPlayers()) {
                connection = welcomingSocket.accept();
                counter++;
                if ((getGod().getNumberOfPlayers() - counter) != 0) {
                    System.out.println("a player joined! just " +
                            (getGod().getNumberOfPlayers() - counter) + " more player...");
                }
                NewPlayerHandler newPlayerHandler = new NewPlayerHandler(connection);
                God.getGod().addNewPlayerHandler(newPlayerHandler);
                pool.execute(newPlayerHandler);
            }
//            System.out.println("all of the players joined!");
            God.getGod().somePlayersAreNotReady();
            System.out.println("all of the players are ready now!");
            System.out.println("the game is on");
            God.getGod().fillMafias();
            God.getGod().sortMafias();
            God.getGod().fillCitizens();
            getGod().waiting = false;
            Thread.sleep(5000);
            getGod().wakeUpCommands();
            getGod().setFirstNight(false);
            getGod().updateGod(); //kar nemikone.
            Thread.sleep(1000);
            for (NewPlayerHandler newPlayerHandler: Network.newPlayerHandlers) {
                pool.execute(new ServerReader(newPlayerHandler));
            }
            getGod().wakeUpCommands();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void addNewPlayerHandler(NewPlayerHandler newPlayerHandler) {
        Network.newPlayerHandlers.add(newPlayerHandler);
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
//            System.out.println("some players are not ready yet.");
            while (God.getGod().getPlayers().size() < numberOfPlayers) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public NewPlayerHandler playerToClient(Player player) {
        for (NewPlayerHandler newPlayerHandler: Network.newPlayerHandlers) {
            if (player.getName().equals(newPlayerHandler.getPlayer().getName())) {
                return newPlayerHandler;
            }
        }
        return null;
    }
    public void commandLine(Object command , Player player1) throws IOException, ClassNotFoundException {
        Player player = getGod().typeToObj(player1);
        ObjectOutputStream out = getGod().playerToClient(player).getObjectOutputStream();
        out.flush();
        out.writeObject(command);
        if (!getGod().isFirstNight()) {
            waitFor();
        }
    }
    public void wakeUpCommands() throws IOException, ClassNotFoundException {
        String command = "WakeUp";
        wakeUpMafias(command);
        getGod().commandLine(command , new CityDoc(""));
        getGod().commandLine(command , new Mayor(""));
        getGod().commandLine(command , new Sniper("", numberOfPlayers));
        if (!getGod().isFirstNight()) {
            getGod().commandLine(command, new LecterDoc(""));
        }
        getGod().commandLine(command, new Detective(""));
        getGod().commandLine(command , new Psychologist(""));
        getGod().commandLine(command , new DieHard(""));
        getGod().commandLine(command , new Citizen(""));
    }
    public Player typeToObj(Player player) {
        int index = players.indexOf(player);
        return players.get(index);
    }

    public void wakeUpMafias(String command) {
        for (Mafia mafia : getMafias()) {
            try {
                playerToClient(mafia).getObjectOutputStream().writeObject(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (! getGod().isFirstNight()) {
            waitFor();
        }

    }
    public void waitFor() {
        setWaiting(true);
        while (getGod().isWaiting()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void waitForMafia() {

    }
    public void updateGod() {
        for (NewPlayerHandler newPlayerHandler: Network.newPlayerHandlers) {
            try {
                newPlayerHandler.getObjectOutputStream().writeObject(God.getGod());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sortMafias() {
        GodFather godFather = null;
        LecterDoc lecterDoc = null;
        Mafia mafia = null;
        for (Mafia mafia1 : mafias) {
            if (mafia1 instanceof GodFather) {
                godFather = (GodFather) mafia1;
            } else if (mafia1 instanceof LecterDoc) {
                lecterDoc = (LecterDoc) mafia1;
            } else {
                mafia = mafia1;
            }
        }
        mafias.clear();
        if (mafia != null) {
            mafias.add(mafia);
        }
        if (lecterDoc != null) {
            mafias.add(lecterDoc);
        }
        if (godFather != null) {
            mafias.add(godFather);
        }
    }
    public LinkedList<Player> getDeads() {
        return deads;
    }


    public LinkedList<Mafia> getMafias() {
        return mafias;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public LinkedList<Citizen> getCitizens() {
        return citizens;
    }

    public void setInqury(boolean inqury) {
        this.inqury = inqury;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public LinkedList<Player> getPlayers() {
        return players;
    }

    public Integer getNumberOfMafiasWhoVotes() {
        return numberOfMafiasWhoVotes;
    }

    public void setNumberOfMafiasWhoVotes(Integer numberOfMafiasWhoVotes) {
        this.numberOfMafiasWhoVotes = numberOfMafiasWhoVotes;
    }

    public boolean isFirstNight() {
        return firstNight;
    }

    public void setFirstNight(boolean firstNight) {
        this.firstNight = firstNight;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(this.getClass()))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    public static void cls()
    {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {}
    }
}

class NewPlayerHandler extends Thread implements Serializable{
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
//    private InputStream inputStream;
//    private OutputStream outputStream;
    private Player player;
    private String name;
    public NewPlayerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            out = new ObjectOutputStream(outputStream);
            out.writeObject(God.getGod().randRole());
            InputStream inputStream = socket.getInputStream();
            in = new ObjectInputStream(inputStream);
            player = (Player) in.readObject();
            God.getGod().addPlayer(player);
            name = player.getName();
            while (God.getGod().isWaiting()) {
                Thread.sleep(2000);
            }
            God.getGod().somePlayersAreNotReady();
            out.writeObject("all of the players joined!");
            out.writeObject(God.getGod());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

    public Socket getSocket() {
        return socket;
    }
}
