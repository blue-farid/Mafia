public class GodFather extends Mafia{
    public GodFather(String name) {
        super(name);
    }
    public void kill (Player player) {
        player.setAlive(false);
    }
}
