public class Player {
    private String name;
    private Vote vote;
    private boolean alive;
    private boolean onShot;

    public Player() {
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
}
