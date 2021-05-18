public class CityDoc implements Doctor{
    @Override
    public void save(Player player) {
        if (player instanceof Citizen) {
            player.setOnShot(false);
        }
    }
}
