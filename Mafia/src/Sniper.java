public class Sniper extends Citizen implements Shooter{
    private int bullets = 3; // mafias - 2
    @Override
    public void shot(Player player) {
        if (player instanceof Mafia) {
            bullets--;
            player.setOnShot(true);
            return;
        } else {
            this.setAlive(false);
        }
    }
}
