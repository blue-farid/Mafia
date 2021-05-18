import java.util.LinkedList;

public class God {
    private static God god;
    private LinkedList<Player> players = new LinkedList<>();
    private LinkedList<Player> deads = new LinkedList<>();
    private God() {}

    public  static God getGod() {
        if (god == null) {
            god = new God();
        }
        return god;
    }

    public boolean addPlayer(Player player) {
        return players.add(player);
    }
    public boolean removePlayer(Player player) {
        deads.add(player);
        return players.remove(player);
    }

    public LinkedList<Player> getPlayers() {
        return players;
    }

    public LinkedList<Player> getDeads() {
        return deads;
    }
}
