import java.util.Objects;

public class Player implements Comparable {
    private final String name;
    private int votes = 0;
    private boolean alive;
    private boolean mute;

    public Player(String name) {
        this.name = name;
        alive = true;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    public void addVote() {
        votes++;
    }

    public void resetVotes() {
        votes = 0;
    }

    public int getVotes() {
        return votes;
    }


    @Override
    public int compareTo(Object o) {
        int compareVote = ((Player) o).getVotes();
        return this.votes-compareVote;
    }
}
