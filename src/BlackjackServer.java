import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server objects represent a Blackjack server that clients can connect to.
 *
 * @author Jordan Segalman
 */

public class BlackjackServer {
    private int serverPort;         // server port
    private int playersPerTable;    // number of players per table

    /**
     * Constructor for server object.
     *
     * @param serverPort Server port
     * @param playersPerTable Number of players per table
     */

    public BlackjackServer(int serverPort, int playersPerTable) {
        this.serverPort = serverPort;
        this.playersPerTable = playersPerTable;
    }

    /**
     * Starts the server.
     */

    public void start() {
        try {
            System.out.println("Creating server socket");
            ServerSocket serverSocket = new ServerSocket(this.serverPort);  // server socket on server port
            System.out.println("Listening on port: " + this.serverPort);
            while (true) {
                Table newTable = new Table();                               // new table players can join
                Thread newTableThread = new Thread(newTable);               // new thread for table
                for (int i = 0; i < this.playersPerTable; i++) {
                    Socket socket = serverSocket.accept();                  // new socket from server socket
                    System.out.println("Received request from port: " + socket.getPort());
                    Player newPlayer = new Player(socket, newTable);        // new player to join table
                    newTable.addPlayer(newPlayer);
                    Thread newPlayerThread = new Thread(newPlayer);         // new thread for player
                    newPlayerThread.start();
                }
                newTableThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method of the server that creates objects and executes other methods.
     *
     * @param args String array of arguments passed to the server
     */

    public static void main(String[] args) {
        BlackjackServer blackjackServer = new BlackjackServer(44444, 1);
        blackjackServer.start();
    }
}