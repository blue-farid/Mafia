/**
 * The type Detective.
 */
public class Detective extends Citizen {

    /**
     * Instantiates a new Detective.
     *
     * @param name the name
     */
    public Detective(String name) {
        super(name);
    }

    /**
     * Detection boolean.
     *
     * @param player the player
     * @return true if detect the mafia.
     *         false if detect godFather or any other type.
     */
    public boolean detection(Player player) {
        if (player instanceof Mafia) {
            if (!(player instanceof GodFather)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
