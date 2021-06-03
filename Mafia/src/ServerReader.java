import java.io.IOException;
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

    @Override
    public void commandLine(Object command) {
        if (command.equals("MafiaWakeUp")) {
            Network.sendToMafias(reader());
            synchronized (God.getGod().getNumberOfMafiasWhoVotes()) {
                Integer a = God.getGod().getNumberOfMafiasWhoVotes();
                God.getGod().setNumberOfMafiasWhoVotes(++a);
                if (God.getGod().getNumberOfMafiasWhoVotes() >= God.getGod().getMafias().size()) {
                    God.getGod().setNumberOfMafiasWhoVotes(0);
                    God.getGod().setWaiting(false);
                }
            }
        } else if (command.equals("NotWait")) {
            God.getGod().setWaiting(false);
        } else if (command.equals("Wait")) {
            God.getGod().setWaiting(true);
        }
    }

    @Override
    public void run() {
        while (true) {
            commandLine(reader());
        }
    }
}
