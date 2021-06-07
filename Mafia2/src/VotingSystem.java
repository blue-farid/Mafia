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
    private Thread startThread;
    private Thread clientThread;
    private VotingSystem() {
        players = God.getGod().getPlayers();
        minimumOfVotes = (int) Math.floor((players.size() - 1) / 2);
    }
    public void addVote(Player player) {
        player.addVote();
    }
    public void resetVotes(Player player) {
        player.resetVotes();
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
    public void start() {
        ExecutorService pool = Executors.newCachedThreadPool();
        for (Player player: players) {
            pool.execute(new ClientHandler(God.getGod().playerToClient(player)));
        }
        int time = 3;
        startThread = Thread.currentThread();
        for (int i = 0; i < time; i++) {
            Network.sendToAll(((time - i) * 10) + " seconds remaining...");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return;
            }
        }
        Network.sendToAll("Times Up!!");
        clientThread.interrupt();
        return;
    }

    private class ClientHandler implements Runnable{
        private NewPlayerHandler newPlayerHandler;

        public ClientHandler(NewPlayerHandler newPlayerHandler) {
            this.newPlayerHandler = newPlayerHandler;
        }

        @Override
        public void run() {
            clientThread = Thread.currentThread();
            ObjectOutputStream out = newPlayerHandler.getObjectOutputStream();
            ObjectInputStream in = newPlayerHandler.getObjectInputStream();
            Player player = newPlayerHandler.getPlayer();

            Network.sendToPlayer("Vote:\n" + "choose a player",player);
            Display.displayPlayers(player);
            int choose = 0;
            while (true) {
                try {
                    choose = Integer.parseInt((String) in.readObject());
                    break;
                } catch (NumberFormatException e) {
                    Network.sendToPlayer("Invalid Input!", player);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            Player target = God.getGod().getPlayers().get(--choose);
            Vote vote = new Vote(player,target);
            VotingSystem.getVotingSystem().addVote(vote.getTarget());
            Network.sendToAll(vote.getVoter().getName() + " votes to " + vote.getTarget().getName());
            synchronized (God.getGod().getNumberOfPlayersWhoVotes()) {
                int a = God.getGod().getNumberOfPlayersWhoVotes();
                God.getGod().setNumberOfPlayersWhoVotes(++a);
            }
            Network.sendToPlayer("Wait for other players to vote...",player);
            while (God.getGod().getNumberOfPlayersWhoVotes() < God.getGod().getPlayers().size()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            startThread.interrupt();
            God.getGod().setNumberOfPlayersWhoVotes(0);
            Network.sendToAll("Voting Done!");
            return;
        }
    }
}
