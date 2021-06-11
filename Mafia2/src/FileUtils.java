import java.io.*;
import java.util.LinkedList;

public class FileUtils {
    private boolean firstRun = true;
    public void serverSaveGame() {
        File saveFile = new File("SaveGame.bin");
        try {
            saveFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(saveFile);
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            ServerPack serverPack = new ServerPack(God.getGod(),Network.newPlayerHandlers);
            out.writeObject(serverPack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void serverLoadGame() {
        File loadFile = new File("SaveGame.bin");
        try {
            FileInputStream inputStream = new FileInputStream(loadFile);
            ObjectInputStream in = new ObjectInputStream(inputStream);
            ServerPack serverPack = (ServerPack) in.readObject();
            God.getGod().setGod(serverPack.getGod());
            Network.newPlayerHandlers = serverPack.newPlayerHandlers;
        } catch (FileNotFoundException e) {
            System.out.println("there is no saveGame to load!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void saveMessage(String message , File save) {
        try {
            save.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(save,true));
            out.writeUTF(message);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String loadMessages(String path) {
        File load = new File(path);
        String res = "";
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(load));
            while (in.available() > 0) {
                res += in.readUTF() + "\n";
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File Not Found!");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
    private class ServerPack {
        private God god;
        private LinkedList<NewPlayerHandler> newPlayerHandlers = new LinkedList<>();

        public ServerPack(God god, LinkedList<NewPlayerHandler> newPlayerHandlers) {
            this.god = god;
            this.newPlayerHandlers = newPlayerHandlers;
        }

        public God getGod() {
            return god;
        }
        public LinkedList<NewPlayerHandler> getNewPlayerHandlers() {
            return newPlayerHandlers;
        }
    }
}
