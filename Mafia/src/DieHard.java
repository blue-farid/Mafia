import java.lang.reflect.Type;

public class DieHard  extends Citizen {
    private int chances = 2;
    private boolean extraLife = true;

    public DieHard(String name) {
        super(name);
    }

    public boolean inqury() {
        if (chances > 0) {
            chances--;
            God.getGod().setInqury(true);
            return true;
        } else {
            return false;
        }
    }
}
