import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class RevealServer {

    // public static String secretWord = "JAVA";
    // public static StringBuilder currentDisplay = new StringBuilder("_ _ _ _");
    // public static int lives = 6;

    public static String secretWord = "PROGRAMMING";
    public static String hiddenWord = "___________"; // Matches length of secretWord
    public static int lives = 6;
    public static CopyOnWriteArrayList<Player> activePlayers = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        int port = 1728;

        try (ServerSocket listener = new ServerSocket(port)) {
            System.out.println("Waiting for players");

            while (true) {
                Socket connection = listener.accept();

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