import java.util.LinkedList;

public class Mafia extends Player implements Shooter{

    public Mafia(String name) {
        super(name);
    }

    @Override
    public boolean shot(Player player) {
        LinkedList<Player> players = God.getGod().getPlayers();
        if (players.contains(new GodFather(""))) {
            if (this instanceof GodFather) {
                player.setAlive(false);
                return true;
            } else
                return false;
        } else if (players.contains(new LecterDoc(""))) {
            if (this instanceof LecterDoc) {
                player.setAlive(false);
                return true;
            } else
                return false;
        } else {
            player.setAlive(false);
            return true;
        }
    }
}
