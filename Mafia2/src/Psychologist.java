/**
 * The type Psychologist.
 */
public class Psychologist extends Citizen {

    /**
     * Instantiates a new Psychologist.
     *
     * @param name the name
     */
    public Psychologist(String name) {
        super(name);
    }

    /**
     * Mute.
     *
     * @param player the player
     */
    public void mute(Player player) {
        player.setMute(true);
    }
}
