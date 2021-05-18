import java.lang.reflect.Type;

public class DieHard  extends Citizen {
    private int chances = 2;
    private boolean extraLife = true;

    public DieHard(String name) {
        super(name);
    }

    public Type recognition() {
        if (chances > 0) {
            chances--;
            Player lastDead = God.getGod().getDeads().getLast();
            return lastDead.getClass();
        }
        return this.getClass();
    }
}
