public class Mafia extends Player implements Shooter{

    public Mafia(String name) {
        super(name);
    }

    @Override
    public boolean shot(Player player) {
        if (player instanceof Mafia) {
            setAlive(false);
            return true;
        } else
            return false;
    }
}
