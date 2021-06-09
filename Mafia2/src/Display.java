/**
 * The Static class Display.
 */
public class Display {
    /**
     * Display mafias to the player.
     *
     * @param player the player
     */
    public static void displayMafias(Player player) {
        int i = 1;
        for (Mafia mafia: God.getGod().getMafias()) {
            String str = i + "- {\n" + mafia + "}";
            Network.sendToPlayer(str,player);
            i++;
        }
    }

    /**
     * Display citizens to the player.
     *
     * @param player the player
     */
    public static void displayCitizens(Player player) {
        int i = 1;
        for (Citizen citizen: God.getGod().getCitizens()) {
            Network.sendToPlayer(i + "- " + citizen.getName(),player);
            i++;
        }
    }

    /**
     * Display players to the player.
     *
     * @param player the player
     */
    public static void displayPlayers(Player player) {
        int i = 1;
        for (Player player1: God.getGod().getPlayers()) {
            Network.sendToPlayer(i + "- " + player1.getName(),player);
            i++;
        }
    }
}
