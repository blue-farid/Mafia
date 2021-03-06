/**
 * The type City doc.
 */
public class CityDoc extends Citizen implements Saver {

    private boolean selfSaveChance = true;

    /**
     * Instantiates a new City doc.
     *
     * @param name the name
     */
    public CityDoc(String name) {
        super(name);
    }

    @Override
    public boolean save(Player player) {
        if (player instanceof Citizen) {
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
}
