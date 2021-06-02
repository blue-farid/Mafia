import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;
public class Night implements Serializable {
    private boolean firstNight;

    public boolean isFirstNight() {
        return firstNight;
    }

    public void openEyes(Player player) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        if (God.getGod().getNight().isFirstNight()) {
            System.out.println();
            System.out.println("First Night!");
            System.out.println();
            if (player instanceof Mafia) {
                Display.displayMafias();
            } else if (player instanceof Mayor) {
                //find doc.
                int index = God.getGod().getPlayers().indexOf(new CityDoc(""));
                System.out.println(God.getGod().getPlayers().get(index));
                System.out.println(player);
            } else if (player instanceof CityDoc) {
                //find mayor.
                int index = God.getGod().getPlayers().indexOf(new Mayor(""));
                System.out.println(God.getGod().getPlayers().get(index));
                System.out.println(player);
            } else {
                System.out.println(player);
            }
//            firstNight = false;
        } else {
            System.out.println();
            System.out.println("Night!");
            System.out.println();
            if (player instanceof LecterDoc) {
                LecterDoc lecterDoc = (LecterDoc) player;
                System.out.println("choose a Mafia to save.");
                Display.displayMafias();
                int choose = scanner.nextInt();
                Mafia target = God.getGod().getMafias().get(--choose);
                boolean state = true;
                while (state) {
                    if (lecterDoc.save(target)) {
                        System.out.println("you saved " + target.getName());
                        state = false;
                    } else {
                        System.out.println("you can't save yourself anymore! choose another one.");
                    }
                }
            } else if (player instanceof Mafia) {
                Mafia mafia = (Mafia) player;
                System.out.println("choose a player to kill.");
                Display.displayCitizens();
                int choose = scanner.nextInt();
                Player target = God.getGod().getCitizens().get(--choose);
                if (!mafia.shot(target)) {
//                    System.out.println(mafia.getName() + " voted to " + target.getName());
//                    Client.getOutputStream().writeObject(mafia.getName() + " voted to " + target.getName());
                } else {
//                    System.out.println(mafia.getName() + " (GodFather) " +
//                            "shut to " + target.getName());
//                    Client.getOutputStream().writeObject(mafia.getName() + " (GodFather) " +
//                            "shut to " + target.getName());
                }
                for (int i = 0; i < God.getGod().getMafias().size(); i++) {
//                    System.out.println((String) Client.getInputStream().readObject());
                }
            }  else if (player instanceof CityDoc) {
                CityDoc cityDoc = (CityDoc) player;
                System.out.println("choose a Player to save.");
                Display.displayPlayers();
                int choose = scanner.nextInt();
                Player target = God.getGod().getPlayers().get(--choose);
                boolean state = true;
                while (state) {
                    if (cityDoc.save(target)) {
                        System.out.println("you saved " + target.getName());
                        state = false;
                    } else {
                        System.out.println("you can't save yourself anymore! choose another one.");
                    }
                }
            } else if (player instanceof Detective) {
                Detective detective = (Detective) player;
                System.out.println("Choose a player to detect.");
                Display.displayPlayers();
                int choose = scanner.nextInt();
                Player target = God.getGod().getPlayers().get(--choose);
                if (detective.detection(target)) {
                    System.out.println("Yes! " + target.getName() + " is mafia.");
                } else {
                    System.out.println("No!" + target.getName() + " is not mafia.");
                }
            } else if (player instanceof Sniper) {
                System.out.println("Do yo want to shoot? (yes or no)");
                String answer = scanner.nextLine();
                if (answer.equals("no")) {
                    return;
                } else if (answer.equals("yes")) {
                    Sniper sniper = (Sniper) player;
                    System.out.println("OK! choose a player to kill!");
                    Display.displayPlayers();
                    int choose = scanner.nextInt();
                    Player target = God.getGod().getPlayers().get(--choose);
                    if (!sniper.shot(target)) {
                        System.out.println("you don't have any bullet to shoot!");
                    } else {
                        System.out.println("you shoot to " + target.getName());
                    }
                }
            } else if (player instanceof Psychologist) {
                System.out.println("Do yo want to therapy? (yes or no)");
                String answer = scanner.nextLine();
                if (answer.equals("no")) {
                    return;
                } else if (answer.equals("yes")) {
                    Psychologist psychologist = (Psychologist) player;
                    System.out.println("OK! choose a player to therapy!");
                    Display.displayPlayers();
                    int choose = scanner.nextInt();
                    Player target = God.getGod().getPlayers().get(--choose);
                    psychologist.mute(target);
                    System.out.println("you muted" + target.getName());
                }
            } else if (player instanceof DieHard) {
                System.out.println("Do yo want to get inqury? (yes or no)");
                String answer = scanner.nextLine();
                if (answer.equals("no")) {
                    return;
                } else if (answer.equals("yes")) {
                    DieHard dieHard = (DieHard) player;
                    if (!dieHard.inqury()) {
                        System.out.println("sorry, you don't have any more chance.");
                    }
                }
            } else {
                System.out.println(player);
            }
        }
    }

    public void setFirstNight(boolean firstNight) {
        this.firstNight = firstNight;
    }
}
