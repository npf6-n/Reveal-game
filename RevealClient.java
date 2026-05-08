import java.net.*;
import java.util.Scanner;
import java.io.*;

public class RevealClient {
    public static void main(String[] args) {
        String ipAddress = "127.0.0.1";
        int port = 1728;

        try (Socket connection = new Socket(ipAddress, port)) {
            PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
            Scanner in = new Scanner(connection.getInputStream());
            Scanner userInput = new Scanner(System.in);

            String serverMessage = in.nextLine();

            if (serverMessage.equals("enter")) {
                System.out.print("Enter your unique username: ");
                String name = userInput.nextLine();
                out.println(name);

                String response = in.nextLine();
                if (response.equals("joined")) {
                    System.out.println("Connected to the game! Type 'quit' to leave.");

                    while (true) {
                        System.out.print("Guess a letter: ");
                        String command = userInput.nextLine();
                        out.println(command);

                        if (command.equalsIgnoreCase("quit")) {
                            break;
                        }

                        if (in.hasNextLine()) {
                            System.out.println("Server: " + in.nextLine());
                        }
                    }
                } else {
                    System.out.println("This name already exists. Reconnect and try again.");
                }
            }

        } catch (Exception e) {
            System.out.println("Connection closed or lost.");
        }
    }
}