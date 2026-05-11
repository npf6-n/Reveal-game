import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RevealClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 1728);
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner userInput = new Scanner(System.in)) {

            if (in.hasNextLine() && in.nextLine().equals("enter")) {
                System.out.print("Enter your player name: ");
                String name = userInput.nextLine();
                out.println(name);
            }

            new Thread(() -> {
                while (in.hasNextLine()) {
                    System.out.println(in.nextLine());
                    System.out.print("Enter guess: ");
                }
            }).start();

            while (userInput.hasNextLine()) {
                String guess = userInput.nextLine();
                out.println(guess);
                if (guess.equalsIgnoreCase("QUIT"))
                    break;
            }

        } catch (IOException e) {
            System.out.println("Disconnected from server.");
        }
    }
}