public class LecterDoc extends Mafia implements Saver {

    private boolean selfSaveChance = true;
    public LecterDoc(String name) {
        super(name);
    }
    @Override
    public boolean save(Player player) {
        if (player instanceof Mafia) {
            if (player == this) {
                if (selfSaveChance){
                    selfSaveChance = false;
                    this.setAlive(true);
                    return true;
                } else
                    return false;
            }
            player.setAlive(true);
        }
        return true;
    }
}
