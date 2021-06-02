import java.util.Scanner;

public class GamePlay implements Runnable {
    private Player player;
    public GamePlay(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void run() {

    }
}
