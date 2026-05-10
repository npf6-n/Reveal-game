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

            out.println("enter"); // Handshake Start
            if (in.hasNextLine()) {
                this.playerName = in.nextLine();
                boolean unique = true;
                for (Player p : activePlayers) {
                    if (p.name.equalsIgnoreCase(playerName))
                        unique = false;
                }

                if (unique) {
                    activePlayers.add(new Player(socket, playerName));
                    out.println("joined");
                    broadcastStatus();
                } else {
                    out.println("notUnique");
                    return;
                }
            }

            while (in.hasNextLine()) {
                String input = in.nextLine().toUpperCase();
                if (input.equals("QUIT"))
                    break;

                if (input.length() == 1) {
                    char guess = input.charAt(0);
                    if (RevealServer.secretWord.indexOf(guess) >= 0) {
                        updateHidden(guess);
                        out.println("CORRECT"); // Tells current client they were right
                    } else {
                        RevealServer.lives--;
                        out.println("WRONG"); // Tells current client they were wrong
                    }
                    broadcastStatus(); // Updates EVERYONE on lives/names
                }
            }
        } catch (Exception e) {
        } finally {
            cleanup();
        }
    }

    private void updateHidden(char c) {
        StringBuilder sb = new StringBuilder(RevealServer.hiddenWord);
        for (int i = 0; i < RevealServer.secretWord.length(); i++) {
            if (RevealServer.secretWord.charAt(i) == c)
                sb.setCharAt(i, c);
        }
        RevealServer.hiddenWord = sb.toString();
    }

    private void broadcastStatus() {
        StringBuilder names = new StringBuilder();
        for (Player p : activePlayers)
            names.append(p.name).append(",");

        // Protocol: STATUS|Name1,Name2|Lives|CurrentWord
        String status = "STATUS|" + names.toString() + "|" + RevealServer.lives + "|" + RevealServer.hiddenWord;

        for (Player p : activePlayers) {
            try {
                new PrintWriter(p.socket.getOutputStream(), true).println(status);
            } catch (IOException e) {
            }
        }
    }

    private void cleanup() {
        activePlayers.removeIf(p -> p.name.equalsIgnoreCase(playerName));
        broadcastStatus();
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}