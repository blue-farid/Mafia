import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Client reader.
 */
public class ClientReader implements Runnable , Reader {
    private Client client;

    /**
     * Instantiates a new Client reader.
     *
     * @param client the client
     */
    public ClientReader(Client client) {
        this.client = client;
    }

    public Object reader() {
        try {
            return client.getObjectInputStream().readObject();
        } catch (EOFException | SocketException | OptionalDataException e) {
            System.exit(0);
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void commandLine(Object command) throws NullPointerException {
        ExecutorService pool = Executors.newCachedThreadPool();
        if (command.equals("WakeUp")) {
            pool.execute(new OpenEyes(client));
        } else if(command.equals("Chatroom is closing...")) {
            try {
                client.getObjectOutputStream().writeObject(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(command);
        } else if (command.equals("Day!")) {
            try {
                client.getObjectOutputStream().writeObject(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(command);
        } else if (command.equals("BreakTheBlock")) {
            try {
                client.getObjectOutputStream().writeObject("BreakTheBlock");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (command.equals("CheckResponse")) {
            if (!client.isResponse()) {
                int a = client.getToFire();
                client.setToFire(++a);
            } else {
                client.setToFire(0);
            }
            if (client.getToFire() > 2) {
                try {
                    System.out.println("you fired of the game because of the no response law.");
                    client.getObjectOutputStream().writeObject("exit");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println(command);
        }
    }


    @Override
    public void run() {
        while (true) {
            commandLine(reader());
        }
    }

    private class OpenEyes implements Runnable {
        private Client client;

        /**
         * Instantiates a new Open eyes.
         *
         * @param client the client
         */
        public OpenEyes(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                openEyes();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void openEyes() throws IOException, ClassNotFoundException {
            Player player = client.getPlayer();
            ObjectOutputStream out = client.getObjectOutputStream();
            System.out.println();
            if (client.isFirstNight()) {
                client.setFirstNight(false);
                System.out.println("First Night!");
                System.out.println();
                if (player instanceof Mafia) {
                    for (Mafia mafia: God.getGod().getMafias()) {
                        System.out.println(mafia);
                    }
                } else if (player instanceof Mayor) {
                    //find doc.
                    CityDoc cityDoc = (CityDoc) God.getGod().typeToObj(new CityDoc(""));
                    if (cityDoc != null) {
                        System.out.println(cityDoc);
                    }
                    System.out.println(player);
                } else if (player instanceof CityDoc) {
                    Mayor mayor = (Mayor) God.getGod().typeToObj(new Mayor(""));
                    if (mayor != null) {
                        System.out.println(mayor);
                    }
                    System.out.println(player);
                } else {
                    System.out.println(player);
                }
            } else {
                try {
//                    System.out.println("Night!");
                    System.out.println();
                    if (player instanceof LecterDoc) {
                        LecterDoc lecterDoc = (LecterDoc) player;
                        if (!lecterDoc.isFirstWakeup()) {
                            System.out.println("choose a Mafia to save.");
                            out.writeObject("Display.LecterDoc");
                            Thread.sleep(1000);
                        } else {
                            out.writeObject("Display.Citizens");
                        }
                    } else if (player instanceof Mafia) {
                        out.writeObject("Display.Citizens");
                    } else if (player instanceof CityDoc) {
                        System.out.println("choose a Player to save.");
                        out.writeObject("Display.Players");
                    } else if (player instanceof Detective) {
                        System.out.println("Choose a player to detect.");
                        out.writeObject("Display.Players");
                    } else if (player instanceof Sniper) {
                        out.writeObject("sniper");
                    } else if (player instanceof Psychologist) {
                        out.writeObject("psychologist");
                    } else if (player instanceof DieHard) {
                        out.writeObject("dieHard");
                    } else {
                        System.out.println(player);
                        out.writeObject("NotWait");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                    }
                }
            }
        }
    }
}
