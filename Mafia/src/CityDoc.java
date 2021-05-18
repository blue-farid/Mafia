public class CityDoc extends Citizen implements Doctor{

    public CityDoc(String name) {
        super(name);
    }

    @Override
    public void save(Player player) {
        if (player instanceof Citizen) {
            player.setAlive(true);
        }
    }
}
