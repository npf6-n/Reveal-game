import java.net.*;
import java.util.Scanner;
import java.io.*;

public class RevealClient {

    public static void main(String[] args) {

        String ipAddress;
        int port = 1728;

        Socket connection;

        Scanner userInput = new Scanner(System.in);
        System.out.println("Enter IP address");
        ipAddress = userInput.nextLine();

        try {
            System.out.println("Connecting to " + ipAddress + " on port " + port);

            connection = new Socket(ipAddress, port);

            System.out.println("Connected.");

        } catch (Exception e) {
            System.out.println("Connection failed or invalid IP.");
            userInput.close();
            return;
        }

    }

}
