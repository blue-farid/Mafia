/**
 * The type Die hard.
 */
public class DieHard  extends Citizen {
    private int chances = 2;
    private boolean extraLife = true;

    /**
     * Instantiates a new Die hard.
     *
     * @param name the name
     */
    public DieHard(String name) {
        super(name);
    }

    /**
     * Inqury boolean.
     *
     * @return true if has chances.
     *         false if doesn't have more chances.
     */
    public boolean inqury() {
        if (chances > 0) {
            chances--;
            God.getGod().setInqury(true);
            return true;
        } else {
            return false;
        }
    }

    public boolean isExtraLife() {
        return extraLife;
    }

    public void setExtraLife(boolean extraLife) {
        this.extraLife = extraLife;
    }
}
