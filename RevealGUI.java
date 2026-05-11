import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RevealGUI {
    private static JLabel l1 = new JLabel("Word: ");
    private static JLabel l2 = new JLabel("Lives: ");
    private static JLabel l3 = new JLabel("Players: ");
    private static JLabel l4 = new JLabel("Connecting...");

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hangman Display");
        frame.setLayout(new GridLayout(4, 1));
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(l1);
        frame.add(l2);
        frame.add(l3);
        frame.add(l4);
        frame.setVisible(true);

        new Thread(() -> {
            try {
                Socket s = new Socket("127.0.0.1", 1728);
                Scanner in = new Scanner(s.getInputStream());
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                out.println("GUI_VIEWER");

                while (in.hasNextLine()) {
                    String msg = in.nextLine();
                    SwingUtilities.invokeLater(() -> {
                        if (msg.startsWith("STATUS")) {
                            String[] p = msg.split("\\|");
                            l3.setText("Players: " + p[1]);
                            l2.setText("Lives: " + p[2]);
                            l1.setText("Word: " + p[3]);
                            l4.setText(p[4]);
                        } else if (msg.startsWith("VICTORY")) {
                            l4.setText("You Win!");
                        } else if (msg.startsWith("GAMEOVER")) {
                            l4.setText("You Lost!");
                        }
                    });
                }
            } catch (Exception e) {
                l4.setText("Offline");
            }
        }).start();
    }
}