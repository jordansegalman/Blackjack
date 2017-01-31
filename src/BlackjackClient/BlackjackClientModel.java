package BlackjackClient;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * BlackjackClientModel objects hold client information.
 *
 * @author Jordan Segalman
 */

public class BlackjackClientModel {
    private static final int MESSAGE_WAIT_TIME = 500;                           // time to wait between server messages
    private Socket socket;                                                      // socket on server address and port
    private BufferedReader in;                                                  // in to server
    private PrintWriter out;                                                    // out from server
    private ArrayList<BlackjackHandPanel> playerHandPanels = new ArrayList<>(); // holds player hand panels

    /**
     * Constructor for BlackjackClientModel object.
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

    /**
     * Adds a BlackjackHandPanel to playerHandPanels at the given index.
     *
     * @param index Index to add BlackjackHandPanel at
     * @param playerHandPanel BlackjackHandPanel to add to playerHandPanels
     */

    public void addPlayerHandPanel(int index, BlackjackHandPanel playerHandPanel) {
        playerHandPanels.add(index, playerHandPanel);
    }

    /**
     * Returns the BlackjackHandPanel in playerHandPanels at the given index.
     *
     * @param index Index of BlackjackHandPanel
     * @return the BlackjackHandPanel at the given index
     */

    public BlackjackHandPanel getPlayerHandPanel(int index) {
        return playerHandPanels.get(index);
    }

    /**
     * Removes the BlackjackHandPanel in playerHandPanels at the given index.
     *
     * @param index Index of BlackjackHandPanel to remove
     */

    public void removePlayerHandPanel(int index) {
        playerHandPanels.remove(index);
    }

    /**
     * Returns a JLabel containing an image of the card with the given name.
     *
     * @param cardName Name of card to add to JLabel
     * @return the JLabel containing an image of the card
     */

    public JLabel getCardImageLabel(String cardName) {
        JLabel cardLabel = null;    // label containing image of card
        try {
            cardLabel = new JLabel(new ImageIcon(ImageIO.read(new File("CardImages/" + cardName + ".png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardLabel;
    }

    /**
     * Resets the model.
     */

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