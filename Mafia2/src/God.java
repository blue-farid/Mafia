import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

/**
 * The type God.
 */
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

    /**
     * Gets god.
     *
     * @return the god
     */
    public static God getGod() {
        if (god == null) {
            god = new God();
        }
        return god;
    }

    /**
     * Sets god.
     *
     * @param god the god
     */
    public void setGod(God god) {
        this.god = god;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
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

    /**
     * Add new player handler.
     *
     * @param newPlayerHandler the new player handler
     */
    public void addNewPlayerHandler(NewPlayerHandler newPlayerHandler) {
        Network.newPlayerHandlers.add(newPlayerHandler);
    }

    /**
     * Add player boolean.
     *
     * @param player the player
     * @return the boolean
     */
    public boolean addPlayer(Player player) {
        return players.add(player);
    }

    /**
     * Remove player.
     *
     * @param player the player
     */
    public void removePlayer(Player player) {
        if (!deads.contains(player))
            deads.add(player);
        mafias.remove(player);
        citizens.remove(player);
    }

    /**
     * Rand role player.
     *
     * @return the player
     */
    public Player randRole() {
        Random random = new Random();
        int randIndex = random.nextInt(roles.size());
        Player role = roles.get(randIndex);
        roles.remove(randIndex);
        return role;
    }

    /**
     * Create roles.
     */
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

    /**
     * Fill mafias.
     */
    public void fillMafias() {
        for (Player player: players) {
            if (player instanceof Mafia) {
                Mafia mafia = (Mafia) player;
                mafias.add(mafia);
            }
        }
    }

    /**
     * Fill citizens.
     */
    public void fillCitizens() {
        for (Player player: players) {
            if (player instanceof Citizen) {
                Citizen citizen = (Citizen) player;
                citizens.add(citizen);
            }
        }
    }

    /**
     * Some players are not ready.
     */
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

    /**
     * Player to client new player handler.
     *
     * @param player the player
     * @return the new player handler
     */
    public NewPlayerHandler playerToClient(Player player) {
        for (NewPlayerHandler newPlayerHandler: Network.newPlayerHandlers) {
            if (player.getName().equals(newPlayerHandler.getPlayer().getName())) {
                return newPlayerHandler;
            }
        }
        return null;
    }

    /**
     * Command line.
     *
     * @param command the command
     * @param player1 the player 1
     * @throws IOException the io exception
     */
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

    /**
     * Wake up commands.
     *
     * @throws IOException the io exception
     */
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

    /**
     * Type to obj player.
     *
     * @param player the player
     * @return the player
     */
    public Player typeToObj(Player player) {
        int index = players.indexOf(player);
        return players.get(index);
    }

    /**
     * Wake up mafias.
     *
     * @param command the command
     */
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

    /**
     * Wait for.
     */
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

    /**
     * Remove deads.
     */
    public void removeDeads() {
        Iterator itr = players.iterator();
        while (itr.hasNext()) {
            Player player = (Player) itr.next();
            if (!player.isAlive()) {
                if (player instanceof DieHard) {
                    DieHard dieHard = (DieHard) player;
                    if (dieHard.isExtraLife()) {
                        dieHard.setExtraLife(false);
                        dieHard.setAlive(true);
                        continue;
                    }
                }
                removePlayer(player);
                itr.remove();
            }
        }
    }

    /**
     * Send inqury to all.
     */
    public void sendInquryToAll() {
        if (God.getGod().isInqury()) {
            Network.sendToAll("Die-Hard Inqury:");
            God.getGod().setInqury(false);
            for (Player dead: deads) {
                Network.sendToAll(dead.getClass().getName());
            }
        }
    }

    /**
     * Send final message to new deads.
     */
    public void sendFinalMessageToNewDeads() {
        for (Player dead: newDeads) {
            Network.sendToPlayer("You Dead!\nYou can leave the game by type 'exit' " +
                    "command whenever you want.",dead);
        }
        newDeads.clear();
    }

    /**
     * Send night events to all.
     */
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

    /**
     * Check for game over.
     */
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

    /**
     * Sort mafias.
     */
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

    /**
     * Gets deads.
     *
     * @return the deads
     */
    public LinkedList<Player> getDeads() {
        return deads;
    }


    /**
     * Gets mafias.
     *
     * @return the mafias
     */
    public LinkedList<Mafia> getMafias() {
        return mafias;
    }

    /**
     * Is waiting boolean.
     *
     * @return the boolean
     */
    public boolean isWaiting() {
        return waiting;
    }

    /**
     * Sets waiting.
     *
     * @param waiting the waiting
     */
    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    /**
     * Gets citizens.
     *
     * @return the citizens
     */
    public LinkedList<Citizen> getCitizens() {
        return citizens;
    }

    /**
     * Gets new deads.
     *
     * @return the new deads
     */
    public LinkedList<Player> getNewDeads() {
        return newDeads;
    }

    /**
     * Sets inqury.
     *
     * @param inqury the inqury
     */
    public void setInqury(boolean inqury) {
        this.inqury = inqury;
    }

    /**
     * Is inqury boolean.
     *
     * @return the boolean
     */
    public boolean isInqury() {
        return inqury;
    }

    /**
     * Gets number of players.
     *
     * @return the number of players
     */
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * Is game over boolean.
     *
     * @return the boolean
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Sets game over.
     *
     * @param gameOver the game over
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Sets number of players.
     *
     * @param numberOfPlayers the number of players
     */
    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    /**
     * Gets players.
     *
     * @return the players
     */
    public LinkedList<Player> getPlayers() {
        return players;
    }

    /**
     * Gets number of mafias who votes.
     *
     * @return the number of mafias who votes
     */
    public Integer getNumberOfMafiasWhoVotes() {
        return numberOfMafiasWhoVotes;
    }

    /**
     * Sets number of mafias who votes.
     *
     * @param numberOfMafiasWhoVotes the number of mafias who votes
     */
    public void setNumberOfMafiasWhoVotes(Integer numberOfMafiasWhoVotes) {
        this.numberOfMafiasWhoVotes = numberOfMafiasWhoVotes;
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

    /**
     * Clear The Screen.
     */
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

/**
 * The type New player handler.
 */
class NewPlayerHandler extends Thread implements Serializable{
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
//    private InputStream inputStream;
//    private OutputStream outputStream;
    private Player player;
    private String name;

    /**
     * Instantiates a new New player handler.
     *
     * @param socket the socket
     */
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
     * Gets socket.
     *
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }
}
