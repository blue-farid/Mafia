public class Vote {
    private Player voter; // is this usable?
    private Player target;

    public Vote(Player voter, Player target) {
        this.voter = voter;
        this.target = target;
    }

    public Player getTarget() {
        return target;
    }

    public Player getVoter() {
        return voter;
    }
}
