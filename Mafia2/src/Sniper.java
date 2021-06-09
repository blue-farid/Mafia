public class Sniper extends Citizen implements Shooter {
    private int bullets; // =  number of mafias - 2

    public Sniper(String name , int numberOfPlayers) {
        super(name);
        double numberOfMafias = Math.floor((double) numberOfPlayers / 3.0);
        this.bullets = (int) numberOfMafias - 1;
    }
    @Override
    public boolean shot(Player player) {
        if (bullets <= 0) {
            return false;
        }
        if (player instanceof Mafia) {
            bullets--;
            player.setAlive(false);
        } else {
            this.setAlive(false);
        }
        return true;
    }
}
