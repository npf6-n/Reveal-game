import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class RevealServer {
    public static void main(String[] args) {
        int port = 1728;
        ArrayList<Player> activePlayers = new ArrayList<>();

        try (ServerSocket listener = new ServerSocket(port)) {
            System.out.println("Waiting for players...");

            while (true) {
                Socket connection = listener.accept();
                Scanner in = new Scanner(connection.getInputStream());
                PrintWriter out = new PrintWriter(connection.getOutputStream(), true);

                out.println("enter");

                if (in.hasNextLine()) {
                    String name = in.nextLine();

                    boolean isUnique = true;
                    for (Player p : activePlayers) {
                        if (p.name.equalsIgnoreCase(name)) {
                            isUnique = false;
                            break;
                        }
                    }

                    if (isUnique) {
                        activePlayers.add(new Player(connection, name));
                        System.out.println(name + " has joined the game!");
                        out.println("in");
                    } else {
                        out.println("Failed");
                        connection.close();
                    }
                }

                System.out.println("Current Roster:");
                for (Player p : activePlayers) {
                    System.out.println(p.name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// javac RevealServer.java && java RevealServer
// javac RevealClient.java && java RevealClient