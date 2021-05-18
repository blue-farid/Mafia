public class Mayor extends Citizen {
    public Mayor(String name) {
        super(name);
    }

    public void cancelVotes(Player player) {
        player.setAlive(true);
    }
}
