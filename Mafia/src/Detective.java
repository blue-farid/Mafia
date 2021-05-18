public class Detective extends Citizen {

    public Detective(String name) {
        super(name);
    }

    public boolean detection(Player player) {
        if (player instanceof Mafia) {
            if (!(player instanceof GodFather)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
