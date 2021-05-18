public class Sniper extends Citizen implements Shooter {
    private int bullets; // =  number of mafias - 2

    public Sniper(String name , int numberOfMafias) {
        super(name);
        bullets = numberOfMafias - 2;
    }
    @Override
    public void shot(Player player) {
        if (bullets <= 0) {
            return;
        }
        if (player instanceof Mafia) {
            bullets--;
            player.setAlive(false);
        } else {
            this.setAlive(false);
        }
    }
}
