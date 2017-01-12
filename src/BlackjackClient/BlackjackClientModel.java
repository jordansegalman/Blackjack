package BlackjackClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * BlackjackClientModel objects represent a Blackjack client model that holds client information.
 *
 * @author Jordan Segalman
 */

public class BlackjackClientModel {
    private Socket socket;          // socket on server address and port
    private BufferedReader in;      // in to server
    private PrintWriter out;        // out from server

    /**
     * Constructor for Blackjack client model object.
     *
     * @param serverAddress Server address
     * @param serverPort Server port
     */

    public BlackjackClientModel(String serverAddress, int serverPort) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            InputStreamReader isr = new InputStreamReader(this.socket.getInputStream());    // input stream reader from socket
            this.in = new BufferedReader(isr);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
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
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (serverMessage == null) {
            try {
                serverMessage = this.in.readLine();
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
        this.out.println(clientMessage);
    }

    /**
     * Sends a message to the server to quit the game and closes the socket.
     */

    public void quitGame() {
        this.sendClientMessage("CLIENTMESSAGE--QUITGAME");
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}