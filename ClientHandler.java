import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {
    private Socket socket;
    private CopyOnWriteArrayList<Player> activePlayers;
    private String playerName;

    public ClientHandler(Socket socket, CopyOnWriteArrayList<Player> activePlayers) {
        this.socket = socket;
        this.activePlayers = activePlayers;
    }

    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("enter");
            if (in.hasNextLine()) {
                this.playerName = in.nextLine();
                boolean unique = true;
                for (Player p : activePlayers) {
                    if (p.name.equalsIgnoreCase(playerName)) {
                        unique = false;
                    }
                }
                if (unique) {
                    activePlayers.add(new Player(socket, playerName));
                    System.out.println(playerName + " has joined the game!");
                    out.println("joined");
                } else {
                    out.println("notUnique");
                }
            }

            while (true) {
                if (in.hasNextLine()) {
                    String message = in.nextLine();
                    if (message.equalsIgnoreCase("quit"))
                        break;

                    out.println("Server heard: " + message);
                }
            }
        } catch (IOException e) {
            System.out.println("Connection lost with " + playerName);
        } finally {
            exit();
        }
    }

    private void exit() {
        try {
            activePlayers.removeIf(p -> p.name.equalsIgnoreCase(playerName));
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}