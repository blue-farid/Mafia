import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class God implements Serializable {
    private static God god;
    private static Socket connection;
    private int numberOfPlayers;
    private boolean waiting = true;
    private boolean inqury = false;
    private boolean firstNight = true;
    private boolean gameOver = false;
    private int numberOfMafiasWhoVotes = 0;
    private LinkedList<Player> roles = new LinkedList<>();
    private LinkedList<Player> players = new LinkedList<>();
    private LinkedList<Mafia> mafias = new LinkedList<>();
    private LinkedList<Citizen> citizens = new LinkedList<>();
    private LinkedList<Player> deads = new LinkedList<>();
    private LinkedList<Player> newDeads = new LinkedList<>();
    private LinkedList<Player> mutes = new LinkedList<>();

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
            ExecutorService serverReader = Executors.newCachedThreadPool();
            ExecutorCompletionService completionService = new ExecutorCompletionService<>(serverReader);
            while (true) {
                Network.readyForReading(serverReader);
                if (God.getGod().isFirstNight()) {
                    getGod().wakeUpCommands();
                    Thread.sleep(5000);
                } else {
                    Network.sendToAll("Night!");
                    getGod().wakeUpCommands();
                    getGod().sendNightEventsToAll();
                    getGod().removeDeads();
                    getGod().sendInquryToAll();
                    getGod().sendFinalMessageToNewDeads();
                    getGod().checkForGameOver();
                }
                Network.sendToAll("Day!");
                Future future;
                do {
                    future = completionService.poll();
                } while (future != null);
                ChatroomServer.getChatroomServer().start();
                Network.sendToAll("Voting!");
                VotingSystem.getVotingSystem().start();
                Network.sendToAll("BreakTheBlock");
                if (getGod().mutes.size() > 0) {
                    getGod().typeToObj(getGod().mutes.getFirst()).setMute(false);
                    getGod().mutes.clear();
                }
                getGod().removeDeads();
                getGod().sendFinalMessageToNewDeads();
                getGod().checkForGameOver();
            }
        } catch (IOException e) {
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
    public void removePlayer(Player player) {
        if (!deads.contains(player))
            deads.add(player);
        mafias.remove(player);
        citizens.remove(player);
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
    public void commandLine(Object command , Player player1) throws IOException {
        if (God.getGod().getPlayers().contains(player1)) {
            Player player = getGod().typeToObj(player1);
            ObjectOutputStream out = getGod().playerToClient(player).getObjectOutputStream();
            out.flush();
            out.writeObject(command);
            if (!getGod().isFirstNight()) {
                waitFor();
            }
        } else
            return;
    }
    public void wakeUpCommands() throws IOException {
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
        if (God.getGod().isFirstNight()) {
            God.getGod().setFirstNight(false);
        }
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

    public void removeDeads() {
        Iterator itr = players.iterator();
        while (itr.hasNext()) {
            Player player = (Player) itr.next();
            if (!player.isAlive()) {
                removePlayer(player);
                itr.remove();
            }
        }
    }
    public void sendInquryToAll() {
        if (God.getGod().isInqury()) {
            Network.sendToAll("Die-Hard Inqury:");
            God.getGod().setInqury(false);
            for (Player dead: deads) {
                Network.sendToAll(dead.getClass().getName());
            }
        }
    }
    public void sendFinalMessageToNewDeads() {
        for (Player dead: newDeads) {
            Network.sendToPlayer("You Dead!\nYou can leave the game by type 'exit' " +
                    "command whenever you want.",dead);
        }
        newDeads.clear();
    }
    public void sendNightEventsToAll() {
        boolean noOne = true;
        Network.sendToAll("In Last Night we lost: ");
        for (Player player: players) {
            if (!player.isAlive()) {
                newDeads.add(player);
                noOne = false;
                Network.sendToAll(player.getName());
            }
        }
        if (noOne) {
            Network.sendToAll("No One!");
        }
        for (Player player: players) {
            if (player.isMute()) {
                mutes.add(player);
                Network.sendToAll(player.getName() + " has been muted!");
            }
        }
    }
    public void checkForGameOver() {
        if (God.getGod().getMafias().size() >= God.getGod().getCitizens().size()) {
            Network.sendToAll("Mafia Wins!\nGame Over!");
            God.getGod().setGameOver(true);
        } else if (God.getGod().getMafias().size() == 0) {
            Network.sendToAll("Citizens Wins!\nGame Over!");
            God.getGod().setGameOver(true);
        }
        if (God.getGod().isGameOver()) {
            System.exit(0);
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

    public LinkedList<Player> getNewDeads() {
        return newDeads;
    }

    public void setInqury(boolean inqury) {
        this.inqury = inqury;
    }

    public boolean isInqury() {
        return inqury;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
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
