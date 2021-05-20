import java.util.Scanner;

public class GamePlay implements Runnable {
    private Player player;
    Night night = new Night();
    public GamePlay(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void run() {
        night.openEyes(player);
    }
}
