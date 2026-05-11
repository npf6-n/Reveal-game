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

            // 1. HANDSHAKE & UNIQUE NAME CHECK
            out.println("enter");
            if (in.hasNextLine()) {
                String requestedName = in.nextLine().trim();

                // Check if the name already exists in the activePlayers list
                boolean isTaken = false;
                for (Player p : activePlayers) {
                    if (p.name.equalsIgnoreCase(requestedName)) {
                        isTaken = true;
                        break;
                    }
                }

                if (isTaken) {
                    out.println("Username already taken. Connection closing.");
                    socket.close();
                    return; // Exit the thread
                }

                this.playerName = requestedName;
                activePlayers.add(new Player(socket, playerName));
                out.println("joined");
                broadcastStatus("Game in progress");
            }

            // 2. GAME LOOP
            while (in.hasNextLine()) {
                String input = in.nextLine().trim().toUpperCase();
                if (input.equals("QUIT"))
                    break;

                if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
                    char guess = input.charAt(0);
                    String feedback;

                    if (RevealServer.secretWord.indexOf(guess) >= 0) {
                        updateHidden(guess);
                        feedback = "Correct! Letter '" + guess + "' revealed.";
                    } else {
                        RevealServer.lives--;
                        feedback = "Incorrect! '" + guess + "' is not there. -1 life.";
                    }

                    // Check Win/Loss
                    if (RevealServer.hiddenWord.equals(RevealServer.secretWord)) {
                        broadcast("VICTORY|You Win! The word was " + RevealServer.secretWord);
                    } else if (RevealServer.lives <= 0) {
                        broadcast("GAMEOVER|You Lost! The word was " + RevealServer.secretWord);
                    } else {
                        broadcastStatus(feedback);
                    }
                } else {
                    out.println("Invalid input: enter one letter.");
                }
            }
        } catch (Exception e) {
            // Error handling
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

    private void broadcastStatus(String feedback) {
        StringBuilder names = new StringBuilder();
        // Don't show the GUI_VIEWER in the player list on screen
        for (Player p : activePlayers) {
            if (!p.name.equals("GUI_VIEWER")) {
                names.append(p.name).append(", ");
            }
        }

        String guiData = "STATUS|" + names.toString() + "|" + RevealServer.lives + "|" + RevealServer.hiddenWord + "|"
                + feedback;

        // Smart Broadcasting: GUI gets technical data, Players get plain text
        for (Player p : activePlayers) {
            try {
                PrintWriter writer = new PrintWriter(p.socket.getOutputStream(), true);
                if (p.name.equals("GUI_VIEWER")) {
                    writer.println(guiData);
                } else {
                    writer.println(feedback);
                }
            } catch (IOException e) {
            }
        }
    }

    private void broadcast(String message) {
        for (Player p : activePlayers) {
            try {
                PrintWriter writer = new PrintWriter(p.socket.getOutputStream(), true);
                if (p.name.equals("GUI_VIEWER")) {
                    writer.println(message);
                } else {
                    // Send only the text after the "|" to the terminal clients
                    String clean = message.contains("|") ? message.split("\\|")[1] : message;
                    writer.println(clean);
                }
            } catch (IOException e) {
            }
        }
    }

    private void cleanup() {
        if (playerName != null) {
            activePlayers.removeIf(p -> p.name.equalsIgnoreCase(playerName));
            broadcastStatus(playerName + " left.");
        }
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}