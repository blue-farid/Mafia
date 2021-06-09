/**
 * The type Mayor.
 */
public class Mayor extends Citizen implements Saver {
    /**
     * Instantiates a new Mayor.
     *
     * @param name the name
     */
    public Mayor(String name) {
        super(name);
    }

    @Override
    public boolean save(Player player) {
        player.setAlive(true);
        return true;
    }
}
