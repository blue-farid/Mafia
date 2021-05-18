public class LecterDoc implements Doctor {
    @Override
    public void save(Player player) {
        if (player instanceof Mafia) {
            player.setOnShot(false);
        }
    }
}
