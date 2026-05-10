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

    @Override
    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 1. HANDSHAKE
            out.println("enter");
            if (in.hasNextLine()) {
                this.playerName = in.nextLine().trim();
                boolean unique = true;
                for (Player p : activePlayers) {
                    if (p.name.equalsIgnoreCase(playerName)) {
                        unique = false;
                        break;
                    }
                }

                if (unique && !playerName.isEmpty()) {
                    activePlayers.add(new Player(socket, playerName));
                    out.println("joined");
                    broadcastStatus();
                } else {
                    out.println("notUnique");
                    return;
                }
            }

            // 2. GAME LOOP
            while (in.hasNextLine()) {
                String input = in.nextLine().trim().toUpperCase();

                if (input.equals("QUIT"))
                    break;

                // VALIDATION: Only 1 letter allowed
                if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
                    out.println("Invalid input: please enter only one letter.");
                    continue;
                }

                char guess = input.charAt(0);

                // LOGIC: Check guess
                if (RevealServer.secretWord.indexOf(guess) >= 0) {
                    updateHidden(guess);
                    out.println("CORRECT");
                } else {
                    RevealServer.lives--;
                    out.println("WRONG");
                }

                // 3. WIN/LOSS CHECK
                if (RevealServer.hiddenWord.equals(RevealServer.secretWord)) {
                    broadcast("VICTORY|The word was: " + RevealServer.secretWord);
                } else if (RevealServer.lives <= 0) {
                    broadcast("GAMEOVER|The word was: " + RevealServer.secretWord);
                } else {
                    broadcastStatus();
                }
            }
        } catch (Exception e) {
            System.out.println("Connection error with " + playerName);
        } finally {
            cleanup();
        }
    }

    private void updateHidden(char c) {
        StringBuilder sb = new StringBuilder(RevealServer.hiddenWord);
        for (int i = 0; i < RevealServer.secretWord.length(); i++) {
            if (RevealServer.secretWord.charAt(i) == c) {
                sb.setCharAt(i, c);
            }
        }
        RevealServer.hiddenWord = sb.toString();
    }

    private void broadcastStatus() {
        StringBuilder names = new StringBuilder();
        for (Player p : activePlayers) {
            names.append(p.name).append(",");
        }
        // Protocol: STATUS|Names|Lives|Word
        String status = "STATUS|" + names.toString() + "|" + RevealServer.lives + "|" + RevealServer.hiddenWord;
        broadcast(status);
    }

    private void broadcast(String message) {
        for (Player p : activePlayers) {
            try {
                PrintWriter writer = new PrintWriter(p.socket.getOutputStream(), true);
                writer.println(message);
            } catch (IOException e) {
                // Connection lost; cleanup will handle removal
            }
        }
    }

    private void cleanup() {
        if (playerName != null) {
            activePlayers.removeIf(p -> p.name.equalsIgnoreCase(playerName));
            System.out.println(playerName + " has left.");
            broadcastStatus();
        }
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}