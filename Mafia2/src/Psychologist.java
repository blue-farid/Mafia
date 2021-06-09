public class Psychologist extends Citizen {

    public Psychologist(String name) {
        super(name);
    }

    public void mute(Player player) {
        player.setMute(true);
    }
}
