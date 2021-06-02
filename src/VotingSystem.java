import java.util.Collections;
import java.util.LinkedList;

public class VotingSystem {
    private static VotingSystem votingSystem;
    private LinkedList<Player> players;
    private LinkedList<Player> pleaders;
    private int minimumOfVotes = (int) Math.floor((players.size() - 1) / 2);
    private VotingSystem() {
        players = God.getGod().getPlayers();
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
}
