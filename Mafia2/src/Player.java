import java.io.*;
import java.util.Objects;

/**
 * The type Player.
 */
public class Player implements Comparable , Serializable {
    private String name;
    private int votes;
    private boolean alive;
    private boolean mute;

    /**
     * Instantiates a new Player.
     *
     * @param name the name
     */
    public Player(String name) {
        this.name = name;
        alive = true;
        votes = 0;
        mute = false;
    }

    /**
     * Instantiates a new Player.
     */
    public Player() {
        alive = true;
        votes = 0;
        mute = false;
    }

    /**
     * Sets alive.
     *
     * @param alive the alive
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Is mute boolean.
     *
     * @return the boolean
     */
    public boolean isMute() {
        return mute;
    }

    /**
     * Is alive boolean.
     *
     * @return the boolean
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Sets mute.
     *
     * @param mute the mute
     */
    public void setMute(boolean mute) {
        this.mute = mute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(this.getClass(), player.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Add vote.
     */
    public void addVote() {
        votes++;
    }

    /**
     * Reset votes.
     */
    public void resetVotes() {
        votes = 0;
    }

    /**
     * Gets votes.
     *
     * @return the votes
     */
    public int getVotes() {
        return votes;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        int compareVote = ((Player) o).getVotes();
        return this.votes - compareVote;
    }

    @Override
    public String toString() {
        return "Username: " + name + "\n" +
                "Role: " + getClass().getName() + "\n";
    }
}
