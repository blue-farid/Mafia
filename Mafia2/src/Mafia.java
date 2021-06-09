import java.util.LinkedList;

/**
 * The type Mafia.
 */
public class Mafia extends Player implements Shooter{

    /**
     * Instantiates a new Mafia.
     *
     * @param name the name
     */
    public Mafia(String name) {
        super(name);
    }

    /**
     *
     * @param player
     * @return true if shot was successful.
     *         false if shot was unsuccessful.
     */
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
