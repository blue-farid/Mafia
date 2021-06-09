/**
 * The type Vote.
 */
public class Vote {
    private Player voter;
    private Player target;

    /**
     * Instantiates a new Vote.
     *
     * @param voter  the voter
     * @param target the target
     */
    public Vote(Player voter, Player target) {
        this.voter = voter;
        this.target = target;
    }

    /**
     * Gets target.
     *
     * @return the target
     */
    public Player getTarget() {
        return target;
    }

    /**
     * Gets voter.
     *
     * @return the voter
     */
    public Player getVoter() {
        return voter;
    }
}
