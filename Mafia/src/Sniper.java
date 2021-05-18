public class Sniper extends Citizen implements Shooter {
    private int bullets; // =  number of mafias - 2

    public Sniper(int numberOfMafias) {
        bullets = numberOfMafias - 2;
    }
    @Override
    public void shot(Player player) {
        if (bullets <= 0) {
            return;
        }
        if (player instanceof Mafia) {
            bullets--;
            player.setOnShot(true);
        } else {
            this.setAlive(false);
        }
    }
}
