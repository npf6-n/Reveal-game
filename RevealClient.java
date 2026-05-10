import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RevealClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 1728)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner in = new Scanner(socket.getInputStream());
            Scanner user = new Scanner(System.in);

            if (in.nextLine().equals("enter")) {
                System.out.print("Username: ");
                out.println(user.nextLine());

                if (in.nextLine().equals("joined")) {
                    System.out.println("In Lobby. Type letters to guess.");
                    while (in.hasNextLine()) {
                        String fromServer = in.nextLine();
                        // This handles the GUI-ready status string
                        if (fromServer.startsWith("STATUS")) {
                            String[] parts = fromServer.split("\\|");
                            System.out.println("\n[PLAYERS]: " + parts[1]);
                            System.out.println("[LIVES]: " + parts[2]);
                            System.out.println("[WORD]: " + parts[3]);
                            System.out.print("Guess: ");
                        } else {
                            System.out.println("\nRESULT: " + fromServer);
                        }

                        if (user.hasNextLine())
                            out.println(user.nextLine());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Disconnected.");
        }
    }
}