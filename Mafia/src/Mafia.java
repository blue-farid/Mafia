public class Mafia extends Player implements Shooter{

    public Mafia(String name) {
        super(name);
    }

    @Override
    public void shot(Player player) {
        player.setAlive(false);
    }
}
