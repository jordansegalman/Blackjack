package BlackjackClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    private ArrayList<BlackjackHandPanel> blackjackHandPanels = new ArrayList<>();

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

    public void addBlackjackHandPanel(int index, BlackjackHandPanel blackjackHandPanel) {
        blackjackHandPanels.add(index, blackjackHandPanel);
    }

    public BlackjackHandPanel getBlackjackHandPanel(int index) {
        return blackjackHandPanels.get(index);
    }

    public void removeBlackjackHandPanel(int index) {
        blackjackHandPanels.remove(index);
    }

    public void reset() {
        blackjackHandPanels.clear();
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