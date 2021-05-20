public class Display {
    public static void displayMafias() {
        int i = 1;
        for (Mafia mafia: God.getGod().getMafias()) {
            System.out.println(i + "- {\n" + mafia);
            System.out.println("}");
            i++;
        }
    }
    public static void displayCitizens() {
        int i = 1;
        for (Citizen citizen: God.getGod().getCitizens()) {
            System.out.println(i + "- " + citizen.getName());
            i++;
        }
    }
    public static void displayPlayers() {
        int i = 1;
        for (Player player: God.getGod().getPlayers()) {
            System.out.println(i + "- " + player.getName());
            i++;
        }
    }
}
