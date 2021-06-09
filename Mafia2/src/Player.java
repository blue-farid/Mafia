import java.io.*;
import java.util.Objects;

public class Player implements Comparable , Serializable , Runnable {
    private String name;
    private int votes;
    private boolean alive;
    private boolean mute;

    public Player(String name) {
        this.name = name;
        alive = true;
        votes = 0;
        mute = false;
    }
    public Player() {
        alive = true;
        votes = 0;
        mute = false;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isMute() {
        return mute;
    }

    public boolean isAlive() {
        return alive;
    }

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

    public void addVote() {
        votes++;
    }

    public void resetVotes() {
        votes = 0;
    }

    public int getVotes() {
        return votes;
    }

    public String getName() {
        return name;
    }

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

    @Override
    public void run() {

    }
}
