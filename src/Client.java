import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client objects represent a client that can connect to the Blackjack server.
 *
 * @author Jordan Segalman
 */

// TODO: GUI

public class Client {

    /**
     * Main method of the client that creates objects and executes other methods.
     *
     * @param args String array of arguments passed to the client
     */

    public static void main(String[] args) {
        String serverAddress = "localhost";         // server address
        int serverPort = 44444;                     // server port
        String serverMessage;                       // message received from server
        Scanner scanner = new Scanner(System.in);   // scanner to get input from the user
        boolean gameOver = false;                   // true if the game is over, false if not

        try {
            Socket socket = new Socket(serverAddress, serverPort);                      // socket on server address and port
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());     // input stream reader from socket
            BufferedReader in = new BufferedReader(isr);                                // in to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);          // out from server

            while(!gameOver) {
                if ((serverMessage = in.readLine()) != null) {
                    if (serverMessage.substring(0, 13).equals("INFOMESSAGE--")) {
                        System.out.println(serverMessage.substring(13, serverMessage.length()));
                    } else if (serverMessage.substring(0, 14).equals("REPLYMESSAGE--")) {
                        System.out.println(serverMessage.substring(14, serverMessage.length()));
                        out.println(scanner.nextLine());
                    } else if (serverMessage.substring(0, 17).equals("GAMEOVERMESSAGE--")) {
                        System.out.println(serverMessage.substring(17, serverMessage.length()));
                        gameOver = true;
                    }
                    else {
                        System.err.println("Unrecognized Message: \"" + serverMessage + "\"");
                        gameOver = true;
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}