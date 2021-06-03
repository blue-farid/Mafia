import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client implements Runnable , Serializable {
    private Player player;
    private String name;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ExecutorService pool = Executors.newCachedThreadPool();
    @Override
    public void run() {
        String command = "";
        Scanner scanner = new Scanner(System.in);
        try {
            Socket client = new Socket("127.0.0.1" , 127);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            System.out.println("*****************");
            System.out.println("welcoming message.");
            System.out.println("*****************");
            System.out.println();
            System.out.println("please enter a username:");
            String username = scanner.nextLine();
            player = (Player) in.readObject();
            System.out.println("your role is: " + player.getClass().getName());
            name = username;
            player.setName(username);
            out.writeObject(player);
            System.out.println("you has been added to the game.");
            System.out.println("please wait for other players to join...");
            String wait = (String) in.readObject();
            System.out.println(wait);
            updateGod((God) in.readObject());
            System.out.println("the game is on!");
            // first night
            command = (String) in.readObject();
            commandLine(command);
            God.getGod().setFirstNight(false);
            command = (String) in.readObject();
            commandLine(command);
//            new ChatroomClient(this).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public ObjectInputStream getObjectInputStream() {
        return in;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return out;
    }
    public void updateGod(God god) {
        God.getGod().setGod(god);
    }

    public void commandLine(String command) throws IOException, ClassNotFoundException {
        if (command.equals("WakeUp")) {
            openEyes();
            if (!(player instanceof Mafia)) {
                out.writeObject("");
            }
        }
    }

    public void openEyes() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        if (God.getGod().isFirstNight()) {
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
        } else {
            System.out.println("Night!");
            System.out.println();
            if (player instanceof LecterDoc) {
                LecterDoc lecterDoc = (LecterDoc) player;
                if (!lecterDoc.isFirstWakeup()) {
                    lecterDoc.setFirstWakeup(true);
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
                } else {
                    pool.execute(new Reader(this));
                    lecterDoc.setFirstWakeup(false);
                    Mafia mafia = (Mafia) player;
                    System.out.println("choose a player to kill.");
                    Display.displayCitizens();
                    int choose = scanner.nextInt();
                    Player target = God.getGod().getCitizens().get(--choose);
                    if (!mafia.shot(target)) {
                        out.writeObject(mafia.getName() + " voted to " + target.getName());
                    } else {
                        out.writeObject(mafia.getName() + " (GodFather) " +
                                "shut to " + target.getName());
                    }
                }
            } else if (player instanceof Mafia) {
                pool.execute(new Reader(this));
                Mafia mafia = (Mafia) player;
                System.out.println("choose a player to kill.");
                Display.displayCitizens();
                int choose = scanner.nextInt();
                Player target = God.getGod().getCitizens().get(--choose);
                if (!mafia.shot(target)) {
                    out.writeObject(mafia.getName() + " voted to " + target.getName());
                } else {
                    out.writeObject(mafia.getName() + " (GodFather) " +
                            "shut to " + target.getName());
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
                    System.out.println("you muted " + target.getName());
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

    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(new Client());
    }
}
