import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class RevealServer {
    public static String secretWord = "BANANA";
    public static String hiddenWord = "______";
    public static int lives = 6;
    public static CopyOnWriteArrayList<Player> activePlayers = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1728)) {
            System.out.println("Server started...");
            while (true) {
                Socket s = serverSocket.accept();
                new Thread(new ClientHandler(s, activePlayers)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// javac RevealServer.java && java RevealServer
// javac RevealClient.java && java RevealClient
// javac RevealGUI.java && java RevealGUI