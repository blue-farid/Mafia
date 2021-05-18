public class Player {
    private final String name;
    private Vote vote;
    private boolean alive;
    private boolean onShot;
    private boolean mute;

    public Player(String name) {
        this.name = name;
        alive = true;
        onShot = false;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setOnShot(boolean onShot) {
        this.onShot = onShot;
    }

    public boolean isOnShot() {
        return onShot;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }
}
