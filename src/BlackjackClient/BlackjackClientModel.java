package BlackjackClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * BlackjackClientModel objects represent a Blackjack client model that holds client information.
 *
 * @author Jordan Segalman
 */

public class BlackjackClientModel {
    private static final int MESSAGE_WAIT_TIME = 500;
    private Socket socket;          // socket on server address and port
    private BufferedReader in;      // in to server
    private PrintWriter out;        // out from server
    private ArrayList<BlackjackHandPanel> playerHandPanels = new ArrayList<>();

    /**
     * Constructor for Blackjack client model object.
     *
     * @param serverAddress Server address
     * @param serverPort Server port
     */

    public BlackjackClientModel(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());    // input stream reader from socket
            in = new BufferedReader(isr);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a message sent by the server.
     *
     * @return message sent by the server
     */

    public String getServerMessage() {
        String serverMessage = null;
        try {
            Thread.sleep(MESSAGE_WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (serverMessage == null) {
            try {
                serverMessage = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return serverMessage;
    }

    /**
     * Sends a message to the server.
     *
     * @param clientMessage Message to send to server
     */

    public void sendClientMessage(String clientMessage) {
        out.println(clientMessage);
    }

    public void addPlayerHandPanel(int index, BlackjackHandPanel playerHandPanel) {
        playerHandPanels.add(index, playerHandPanel);
    }

    public BlackjackHandPanel getPlayerHandPanel(int index) {
        return playerHandPanels.get(index);
    }

    public void removePlayerHandPanel(int index) {
        playerHandPanels.remove(index);
    }

    public JLabel getCardImageLabel(String cardName) {
        JLabel cardLabel = null;
        try {
            cardLabel = new JLabel(new ImageIcon(ImageIO.read(new File("CardImages/" + cardName + ".png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardLabel;
    }

    public void reset() {
        playerHandPanels.clear();
    }

    /**
     * Sends a message to the server to quit the game and closes the socket.
     */

    public void quitGame() {
        sendClientMessage("CLIENTMESSAGE--QUITGAME");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}