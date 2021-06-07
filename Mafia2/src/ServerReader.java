import org.w3c.dom.Node;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ServerReader implements Reader , Runnable {
    NewPlayerHandler newPlayerHandler;


    public ServerReader(NewPlayerHandler newPlayerHandler) {
        this.newPlayerHandler = newPlayerHandler;
    }
    @Override
    public Object reader() {
        try {
            return newPlayerHandler.getObjectInputStream().readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
            Display.displayCitizens(mafia);
            int choose = Integer.parseInt((String) reader());
            Player target = God.getGod().getCitizens().get(--choose);
            if (!mafia.shot(target)) {
                Network.sendToMafias(mafia.getName() + " voted to " + target.getName());
            } else {
                Network.sendToMafias(mafia.getName() + " (GodFather) " +
                        "shut to " + target.getName());
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
            if (player instanceof CityDoc) {
                CityDoc cityDoc = (CityDoc) player;
                boolean state = true;
                while (state) {
                    String str = (String) reader();
                    int choose = Integer.parseInt(str);
                    Player target = God.getGod().getPlayers().get(--choose);
                    if (cityDoc.save(target)) {
                        Network.sendToPlayer("you saved " + target.getName(),cityDoc);
                        state = false;
                    } else {
                        Network.sendToPlayer("you can't save yourself anymore! choose another one.",cityDoc);
                    }
                }
            } else if (player instanceof Detective) {
                Detective detective = (Detective) player;
                int choose = Integer.parseInt((String) reader());
                Player target = God.getGod().getPlayers().get(--choose);
                if (detective.detection(target)) {
                    Network.sendToPlayer("Yes! " + target.getName() + " is mafia.",detective);
                } else {
                    Network.sendToPlayer("No!" + target.getName() + " is not mafia.",detective);
                }
            } else if(player instanceof Sniper) {
                Sniper sniper = (Sniper) player;
                int choose = Integer.parseInt((String) reader());
                Player target = God.getGod().getPlayers().get(--choose);
                if (!sniper.shot(target)) {
                    Network.sendToPlayer("you don't have any bullet to shoot!",player);
                } else {
                    Network.sendToPlayer("you shoot to " + target.getName(),player);
                }
            } else if (player instanceof Psychologist) {
                Psychologist psychologist = (Psychologist) player;
                int choose = Integer.parseInt((String) reader());
                Player target = God.getGod().getPlayers().get(--choose);
                psychologist.mute(target);
                Network.sendToPlayer("you muted " + target.getName(),player);
            }
            God.getGod().setWaiting(false);
        } else if(command.equals("sniper")) {
            Network.sendToPlayer("Do yo want to shoot? (yes or no)", newPlayerHandler.getPlayer());
            String answer = (String) reader();
            if (answer.equals("no")) {
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
                return;
            } else if (answer.equals("yes")) {
                Network.sendToPlayer("choose a player to mute!", newPlayerHandler.getPlayer());
                commandLine("Display.Players");
            }
        } else if(command.equals("dieHard")) {
            Network.sendToPlayer("Do yo want to get inqury? (yes or no)", newPlayerHandler.getPlayer());
            String answer = (String) reader();
            if (answer.equals("no")) {
                return;
            } else if (answer.equals("yes")) {
                DieHard dieHard = (DieHard) God.getGod().typeToObj(new DieHard(""));
                if (!dieHard.inqury()) {
                    Network.sendToPlayer("you do not have anymore chances!",dieHard);
                }
            }
            God.getGod().setWaiting(false);
        }
        else {
            Network.sendToPlayer("Invalid Input!",newPlayerHandler.getPlayer());
        }
    }

    @Override
    public void run() {
        while (true) {
            Object command = reader();
            if (command.equals("Day!")) {
                return;
            }
            commandLine(command);
        }
    }
}
