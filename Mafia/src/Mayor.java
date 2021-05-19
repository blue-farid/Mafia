public class Mayor extends Citizen implements Saver {
    public Mayor(String name) {
        super(name);
    }

    @Override
    public boolean save(Player player) {
        player.setAlive(true);
        return true;
    }
}
