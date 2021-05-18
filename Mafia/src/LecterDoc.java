public class LecterDoc extends Mafia implements Doctor {

    public LecterDoc(String name) {
        super(name);
    }
    @Override
    public void save(Player player) {
        if (player instanceof Mafia) {
            player.setAlive(true);
        }
    }
}
