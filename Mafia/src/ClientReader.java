import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.GenericSignatureFormatError;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientReader implements Runnable , Reader {
    private Client client;
    private Thread writer;
    private Thread vote;
    private Scanner writerScanner;
    public ClientReader(Client client) {
        this.client = client;
    }

    public Object reader() {
        try {
            return client.getObjectInputStream().readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void commandLine(Object command) {
        ExecutorService pool = Executors.newCachedThreadPool();
        if (command.equals("WakeUp")) {
            pool.execute(new OpenEyes(client));
        } else if (command.equals(God.getGod())) {
            updateGod((God) command);
        } else if (command.equals("Day")) {
            System.out.println("Day!");
            try {
                client.getObjectOutputStream().writeObject("Day");
            } catch (IOException e) {
                e.printStackTrace();
            }
            writerScanner = new Scanner(System.in);
            writer = new Thread(new Writer(client,writerScanner));
            writer.start();
        } else if (command.equals("CloseWriter")) {
            writer.interrupt();
            writerScanner.close();
        } else if (command.equals("Vote")) {
            vote = new Thread(new Voting(client));
            vote.start();
        } else if (command.equals("CloseVote")) {
            vote.interrupt();
        }
        else {
            System.out.println(command);
        }
    }

    public void updateGod(God god) {
        God.getGod().setGod(god);
        System.out.println(God.getGod().getPlayers().size());
    }

    @Override
    public void run() {
        while (true) {
            commandLine(reader());
        }
    }

    private class OpenEyes implements Runnable {
        private Client client;

        public OpenEyes(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                openEyes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void openEyes() throws IOException {
            Player player = client.getPlayer();
            ObjectOutputStream out = client.getObjectOutputStream();
            Scanner scanner = new Scanner(System.in);
            String mafiaWakeUp = "MafiaWakeUp";
            System.out.println();
            if (God.getGod().isFirstNight()) {
                God.getGod().setFirstNight(false);
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
                try {
                    System.out.println("Night!");
                    System.out.println();
                    if (player instanceof LecterDoc) {
                        LecterDoc lecterDoc = (LecterDoc) player;
                        if (!lecterDoc.isFirstWakeup()) {
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
                            Mafia mafia = (Mafia) player;
                            out.writeObject(mafiaWakeUp);
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
                        Mafia mafia = (Mafia) player;
                        out.writeObject(mafiaWakeUp);
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
                    } else if (player instanceof CityDoc) {
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

                } finally {
                    if (!(player.equals(new Mafia("")) || player.equals(new GodFather("")))) {
                        if (player.equals(new LecterDoc(""))) {
                            LecterDoc lecterDoc = (LecterDoc) player;
                            if (lecterDoc.isFirstWakeup()) {
                                lecterDoc.setFirstWakeup(false);
                                return;
                            } else {
                                lecterDoc.setFirstWakeup(true);
                            }
                        }
                        out.writeObject("NotWait");
                    }
                }
            }
        }
    }
    private class Voting implements Runnable {

        private Client client;

        public Voting(Client client) {
            this.client = client;
        }
        @Override
        public void run() {
            vote();
        }

        public void vote() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Vote: ");
            Display.displayPlayers();
            int choose = scanner.nextInt();
            Player target = God.getGod().getPlayers().get(--choose);
            Vote vote = new Vote(client.getPlayer(),target);
            try {
                client.getObjectOutputStream().writeObject(vote);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Wait for other players to vote");
        }
    }
}
