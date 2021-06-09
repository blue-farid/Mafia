import java.io.IOException;

/**
 * The type Server reader.
 */
public class ServerReader implements Reader , Runnable {
    /**
     * The New player handler.
     */
    private NewPlayerHandler newPlayerHandler;


    /**
     * Instantiates a new Server reader.
     *
     * @param newPlayerHandler the new player handler
     */
    public ServerReader(NewPlayerHandler newPlayerHandler) {
        this.newPlayerHandler = newPlayerHandler;
    }
    @Override
    public Object reader() {
        try {
            return newPlayerHandler.getObjectInputStream().readObject();
        } catch (IOException e) {
            System.out.println(newPlayerHandler.getPlayer().getName() + " left the game!");
            Network.sendToAll(newPlayerHandler.getPlayer().getName() + " left the game!");
            Thread.currentThread().stop();
            return new Object();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets num.
     *
     * @return the num
     */
    public int getNum() {
        while (true) {
            try {
                int choose = Integer.parseInt((String) reader());
                return choose;
            } catch (NumberFormatException e) {
                commandLine("inv");
            }
        }
    }
    public void commandLine(Object command) {
        if (command.equals("NotWait")) {
            God.getGod().setWaiting(false);
        } else if (command.equals("Wait")) {
            God.getGod().setWaiting(true);
        } else if(command.equals("Display.Mafias")) {
            Display.displayMafias(newPlayerHandler.getPlayer());
        } else if(command.equals("Display.Mayor")) {
            Network.sendToPlayer(newPlayerHandler.getPlayer(),
                    God.getGod().typeToObj(new CityDoc("")));
        } else if(command.equals("Display.Doc")) {
            Network.sendToPlayer(newPlayerHandler.getPlayer(),
                    God.getGod().typeToObj(new Mayor("")));
        } else if(command.equals("Display.LecterDoc")) {
            Display.displayMafias(newPlayerHandler.getPlayer());
            boolean state = true;
            while (state) {
                int choose = Integer.parseInt((String) reader());
                LecterDoc lecterDoc = (LecterDoc) newPlayerHandler.getPlayer();
                Player target = God.getGod().getMafias().get(--choose);
                if (lecterDoc.save(target)) {
                    Network.sendToPlayer("you saved " + target.getName(), lecterDoc);
                    state = false;
                } else {
                    Network.sendToPlayer("you can't save yourself anymore! choose another one.", lecterDoc);
                }
            }
            God.getGod().setWaiting(false);
        } else if (command.equals("Display.Citizens")) {
            Mafia mafia = (Mafia) newPlayerHandler.getPlayer();
            Display.displayMafias(mafia);
            Network.sendToPlayer("choose a player to kill.",mafia);
            Display.displayCitizens(mafia);
            int choose = getNum();
            Player target = God.getGod().getCitizens().get(--choose);
            if (!mafia.shot(target)) {
                Network.sendToMafias(mafia.getName() + " voted to " + target.getName());
            } else {
                Network.sendToMafias(mafia.getName() +
                        " shut to " + target.getName());
            }
            synchronized (God.getGod().getNumberOfMafiasWhoVotes()) {
                Integer a = God.getGod().getNumberOfMafiasWhoVotes();
                God.getGod().setNumberOfMafiasWhoVotes(++a);
                if (God.getGod().getNumberOfMafiasWhoVotes() >= God.getGod().getMafias().size()) {
                    God.getGod().setNumberOfMafiasWhoVotes(0);
                    God.getGod().setWaiting(false);
                }
            }
        } else if (command.equals("Display.Players")) {
            Player player = newPlayerHandler.getPlayer();
            Display.displayPlayers(player);
            boolean state2 = false;
            int choose = getNum();
            if (player instanceof CityDoc) {
                CityDoc cityDoc = (CityDoc) player;
                boolean state = true;
                while (state) {
                    if (state2) {
                        choose = getNum();
                    }
                    Player target = God.getGod().getPlayers().get(--choose);
                    if (cityDoc.save(target)) {
                        Network.sendToPlayer("you saved " + target.getName(),cityDoc);
                        state = false;
                    } else {
                        state2 = true;
                        Network.sendToPlayer("you can't save yourself anymore! choose another one.",cityDoc);
                    }
                }
            } else if (player instanceof Detective) {
                Detective detective = (Detective) player;
                Player target = God.getGod().getPlayers().get(--choose);
                if (detective.detection(target)) {
                    Network.sendToPlayer("Yes! " + target.getName() + " is mafia.",detective);
                } else {
                    Network.sendToPlayer("No! " + target.getName() + " is not mafia.",detective);
                }
            } else if(player instanceof Sniper) {
                Sniper sniper = (Sniper) player;
                Player target = God.getGod().getPlayers().get(--choose);
                if (!sniper.shot(target)) {
                    Network.sendToPlayer("you don't have any bullet to shoot!",player);
                } else {
                    Network.sendToPlayer("you shoot to " + target.getName(),player);
                }
            } else if (player instanceof Psychologist) {
                Psychologist psychologist = (Psychologist) player;
                Player target = God.getGod().getPlayers().get(--choose);
                psychologist.mute(target);
                Network.sendToPlayer("you muted " + target.getName(),player);
            }
            God.getGod().setWaiting(false);
        } else if(command.equals("sniper")) {
            Network.sendToPlayer("Do yo want to shoot? (yes or no)", newPlayerHandler.getPlayer());
            String answer = (String) reader();
            if (answer.equals("no")) {
                God.getGod().setWaiting(false);
                return;
            } else if (answer.equals("yes")) {
                Network.sendToPlayer("choose a player to shoot!", newPlayerHandler.getPlayer());
                commandLine("Display.Players");
                return;
            }
        } else if(command.equals("psychologist")) {
            Network.sendToPlayer("Do yo want to therapy? (yes or no)", newPlayerHandler.getPlayer());
            String answer = (String) reader();
            if (answer.equals("no")) {
                God.getGod().setWaiting(false);
                return;
            } else if (answer.equals("yes")) {
                Network.sendToPlayer("choose a player to mute!", newPlayerHandler.getPlayer());
                commandLine("Display.Players");
            }
        } else if(command.equals("dieHard")) {
            Network.sendToPlayer("Do yo want to get inqury? (yes or no)", newPlayerHandler.getPlayer());
            String answer = (String) reader();
            if (answer.equals("no")) {
                God.getGod().setWaiting(false);
                return;
            } else if (answer.equals("yes")) {
                DieHard dieHard = (DieHard) God.getGod().typeToObj(new DieHard(""));
                if (!dieHard.inqury()) {
                    Network.sendToPlayer("you do not have anymore chances!",dieHard);
                }
            }
            God.getGod().setWaiting(false);
        } else if (command.equals("exit")) {
            try {
                Network.exit(newPlayerHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (command.equals("BreakTheBlock"));
        else {
            Network.sendToPlayer("Invalid Input!",newPlayerHandler.getPlayer());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object command = reader();
                if (command.equals("Day!")) {
                    return;
                }
                commandLine(command);
            } catch (NullPointerException e) {

            }
        }
    }
}
