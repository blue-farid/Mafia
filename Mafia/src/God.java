import java.util.LinkedList;

public class God {
    private static God god;
    private LinkedList<Player> players = new LinkedList<>();

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
        return players.remove(player);
    }
}
