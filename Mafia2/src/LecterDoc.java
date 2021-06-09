/**
 * The type Lecter doc.
 */
public class LecterDoc extends Mafia implements Saver {

    private boolean selfSaveChance = true;
    private boolean firstWakeup = true;

    /**
     * Instantiates a new Lecter doc.
     *
     * @param name the name
     */
    public LecterDoc(String name) {
        super(name);
    }

    /**
     * save method.
     * @param player
     * @return true if saving was successful.
     *         false if saving was unsuccessful.
     */
    @Override
    public boolean save(Player player) {
        if (player instanceof Mafia) {
            if (player == this) {
                if (selfSaveChance){
                    selfSaveChance = false;
                    this.setAlive(true);
                    return true;
                } else
                    return false;
            }
            player.setAlive(true);
        }
        return true;
    }

    /**
     * Is first wakeup boolean.
     *
     * @return the boolean
     */
    public boolean isFirstWakeup() {
        return firstWakeup;
    }

    /**
     * Sets first wakeup.
     *
     * @param firstWakeup the first wakeup
     */
    public void setFirstWakeup(boolean firstWakeup) {
        this.firstWakeup = firstWakeup;
    }
}
