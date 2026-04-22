import java.net.*;
import java.io.*;

public class RevealServer {

    public static void main(String[] args) {

        int port = 1728;
        ServerSocket listener;
        Socket connection;

        try {
            listener = new ServerSocket(port);

            System.out.println("Listening..");

            connection = listener.accept();

            System.out.println("Connected.");
        } catch (Exception e) {
            System.out.println("Error: failed to accept connection.");
            return;
        }

    }

}