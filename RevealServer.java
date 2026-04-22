import java.net.*;
import java.io.*;

public class RevealServer {

    public static int clientNum = 1;

    public static void main(String[] args) {

        int port = 1728;
        ServerSocket listener;
        Socket connection;

        try {
            listener = new ServerSocket(port);
            System.out.println("Listening...");

            while (true) {

                connection = listener.accept();
                System.out.println("Client " + clientNum + " just Connected!");
                clientNum += 1;
            }
        } catch (Exception e) {
            System.out.println("Error: connection failed.");
            return;
        }

    }

}
// javac RevealServer.java && java RevealServer
// javac RevealClient.java && java RevealClient
