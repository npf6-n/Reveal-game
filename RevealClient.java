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
                if (response.equals("in")) {
                    System.out.println("You are now in the game");
                } else {
                    System.out.println("Name taken. Please restart and try again.");
                }
            }

        } catch (Exception e) {
            System.out.println("Disconnected from server.");
        }
    }
}