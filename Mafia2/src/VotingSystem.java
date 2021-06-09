import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VotingSystem {
    private static VotingSystem votingSystem;
    private LinkedList<Player> players;
    private LinkedList<Player> pleaders;
    private int minimumOfVotes;
    private int numberOfPlayersWhoVote = 0;
    boolean timesUp = false;
    private VotingSystem() {
        players = new LinkedList<>();
        pleaders = new LinkedList<>();
        minimumOfVotes = (int) Math.floor((players.size() - 1) / 2);
    }
    public void addVote(Player player) {
        player.addVote();
    }
    public static VotingSystem getVotingSystem() {
        if (votingSystem == null) {
            votingSystem = new VotingSystem();
        }
        return votingSystem;
    }
    public void sortPlayers() {
        Collections.sort(players);
        Collections.reverse(players);
    }

    public Player killPLeader() {
        sortPlayers();
        findPleaders();
        if (pleaders.size() == 1) {
            God.getGod().typeToObj(pleaders.getFirst()).setAlive(false);
            return God.getGod().typeToObj(pleaders.getFirst());
        }
        return null;
    }
    public void findPleaders() {
        pleaders.clear();
        int maxOfVotes = players.get(0).getVotes();
        if (maxOfVotes < minimumOfVotes) {
            return;
        }
        int i = 0;
        while (maxOfVotes == players.get(0).getVotes() && i < players.size()) {
            Player pleader = players.get(i);
            pleaders.add(pleader);
            i++;
            if (i < players.size()) {
                maxOfVotes = players.get(i).getVotes();
            }
        }
    }

    public void resetVotes() {
        for (Player player: players) {
            player.resetVotes();
        }
    }
    public void setNumberOfPlayersWhoVote(int numberOfPlayersWhoVote) {
        this.numberOfPlayersWhoVote = numberOfPlayersWhoVote;
    }

    public Integer getNumberOfPlayersWhoVote() {
        return numberOfPlayersWhoVote;
    }

    public void start() {
        fillPlayers();
        resetVotes();
        ExecutorService pool = Executors.newCachedThreadPool();
        for (Player player: players) {
            pool.execute(new ClientHandler(God.getGod().playerToClient(player)));
        }
        Thread tiktokThread = new Thread(new TikTok(5));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tiktokThread.start();
        while (!timesUp && VotingSystem.getVotingSystem().getNumberOfPlayersWhoVote() < God.getGod().getPlayers().size()) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        timesUp = false;
        VotingSystem.getVotingSystem().setNumberOfPlayersWhoVote(0);
        tiktokThread.interrupt();
        Network.sendToAll("Voting Done!");
        Player pleader = killPLeader();
        if (pleader != null) {
            if (players.contains(new Mayor(""))) {
                Network.sendToAll("waiting for the mayor...");
                Mayor mayor = null;
                for (Player player: players) {
                    if (player instanceof Mayor) {
                        mayor = (Mayor) player;
                    }
                }
                ObjectOutputStream out = God.getGod().playerToClient(mayor).getObjectOutputStream();
                ObjectInputStream in = God.getGod().playerToClient(mayor).getObjectInputStream();
                Network.sendToPlayer("Do you want to cancel this voting? (yes or no)",mayor);
                boolean state = false;
                do {
                    try {
                        String answer = (String) in.readObject();
                        System.out.println(answer);
                        if (answer.equals("yes")) {
                            mayor.save(pleader);
                        } else if (answer.equals("no")) {
                        } else {
                            state = true;
                            Network.sendToPlayer("Invalid Input!" , mayor);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } while (state);
                if (pleader.isAlive()) {
                    Network.sendToAll("The Mayor Canceled The Vote!");
                } else {
                    Network.sendToAll(pleader.getName() + " has been executed!");
                    God.getGod().getNewDeads().add(pleader);
                }
            } else {
                Network.sendToAll(pleader.getName() + " has been executed!");
                God.getGod().getNewDeads().add(pleader);
            }
        }
    }

    public void fillPlayers() {
        players = God.getGod().getPlayers();
    }
    private class ClientHandler implements Runnable{
        private NewPlayerHandler newPlayerHandler;

        public ClientHandler(NewPlayerHandler newPlayerHandler) {
            this.newPlayerHandler = newPlayerHandler;
        }

        @Override
        public void run() {
            ObjectOutputStream out = newPlayerHandler.getObjectOutputStream();
            ObjectInputStream in = newPlayerHandler.getObjectInputStream();
            Player player = newPlayerHandler.getPlayer();

            Network.sendToPlayer("choose a player:\n" +
                    "0- No one",player);
            Display.displayPlayers(player);
            int choose = 0;
            String str = "";
            while (true) {
                try {
                    str = (String) in.readObject();
                    choose = Integer.parseInt(str);
                    break;
                } catch (NumberFormatException e) {
                    if (str.equals("BreakTheBlock")) {
                        break;
                    }
                    else if (str.equals("exit")) {
                        try {
                            Network.exit(newPlayerHandler);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                    else {
                        Network.sendToPlayer("Invalid Input!", player);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (choose != 0) {
                Player target = players.get(--choose);
                Vote vote = new Vote(player, target);
                VotingSystem.getVotingSystem().addVote(vote.getTarget());
                Network.sendToAll(vote.getVoter().getName() + " votes to " + vote.getTarget().getName());
            }
            synchronized (VotingSystem.getVotingSystem().getNumberOfPlayersWhoVote()) {
                int a = VotingSystem.getVotingSystem().getNumberOfPlayersWhoVote();
                VotingSystem.getVotingSystem().setNumberOfPlayersWhoVote(++a);
            }
            Network.sendToPlayer("Wait for other players to vote...",player);
        }
    }
    private class TikTok implements Runnable{
        private int time;

        public TikTok(int time) {
            this.time = time;
        }

        @Override
        public void run() {
            tiktok(time);
        }
        public void tiktok(int time) {
            for (int i = 0; i < time; i++) {
                Network.sendToAll((time - i) + " minutes remaining...");
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    return;
                }
            }
            Network.sendToAll("TimesUp!");
            timesUp = true;
        }
    }
}
