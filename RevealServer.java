import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class RevealServer {
    public static void main(String[] args) {
        int port = 1728;
        CopyOnWriteArrayList<Player> activePlayers = new CopyOnWriteArrayList<>();

        try (ServerSocket listener = new ServerSocket(port)) {
            System.out.println("Waiting for players");

            while (true) {
                Socket connection = listener.accept();
                System.out.println("New connection accepted.");

                ClientHandler handler = new ClientHandler(connection, activePlayers);

                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (Exception e) {
            System.out.println("Error. Connection failed.");
        }
    }
}
// javac RevealServer.java && java RevealServer
// javac RevealClient.java && java RevealClient