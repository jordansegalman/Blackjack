import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BlackjackServer objects allow clients to connect to play Blackjack as a new player.
 *
 * @author Jordan Segalman
 */

public class BlackjackServer {
    private static final int DEFAULT_PORT = 44444;                      // default server port
    private static final int DEFAULT_PLAYERS_PER_TABLE = 1;             // default number of players per table
    private static final int DEFAULT_STARTING_MONEY = 2500;             // default amount of money players start with
    private static final int DEFAULT_MINIMUM_BET = 500;                 // default minimum player bet
    private static final int DEFAULT_NUMBER_OF_DECKS = 6;               // default number of decks in shoe
    private static final int DEFAULT_MINIMUM_CARDS_BEFORE_SHUFFLE = 78; // default minimum number of cards remaining before shuffling the shoe
    private int serverPort;                                             // server port
    private int playersPerTable;                                        // number of players per table
    private int startingMoney;                                          // amount of money players start with
    private int minimumBet;                                             // minimum player bet
    private int numberOfDecks;                                          // number of decks in shoe
    private int minimumCardsBeforeShuffle;                              // minimum number of cards remaining before shuffling the shoe

    /**
     * Constructor for BlackjackServer object.
     *
     * @param serverPort Server port
     * @param playersPerTable Number of players per table
     * @param startingMoney Amount of money players start with
     * @param minimumBet Minimum player bet
     * @param numberOfDecks Number of decks in shoe
     * @param minimumCardsBeforeShuffle Minimum number of cards remaining before shuffling the shoe
     */

    public BlackjackServer(int serverPort, int playersPerTable, int startingMoney, int minimumBet, int numberOfDecks, int minimumCardsBeforeShuffle) {
        this.serverPort = serverPort;
        this.playersPerTable = playersPerTable;
        this.startingMoney = startingMoney;
        this.minimumBet = minimumBet;
        this.numberOfDecks = numberOfDecks;
        this.minimumCardsBeforeShuffle = minimumCardsBeforeShuffle;
    }

    /**
     * Starts the server and adds connected clients to new tables as new players.
     */

    public void start() {
        System.out.println("Starting Blackjack server\nServer port: " + serverPort + "\nPlayers per table: " + playersPerTable + "\nStarting money: " + startingMoney + "\nMinimum bet: " + minimumBet + "\nNumber of decks: " + numberOfDecks + "\nMinimum cards before shuffle: " + minimumCardsBeforeShuffle);
        ServerSocket serverSocket = null;
        try {
            System.out.println("Creating server socket");
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            System.err.println("Could not start Blackjack server on port " + serverPort);
            System.exit(1);
        }
        try {
            System.out.println("Listening on port " + serverPort);
            while (true) {
                Table newTable = new Table(minimumBet, numberOfDecks, minimumCardsBeforeShuffle);
                Thread newTableThread = new Thread(newTable);
                for (int i = 0; i < playersPerTable; i++) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Received request from port " + socket.getPort());
                    Player newPlayer = new Player(socket, newTable, startingMoney);
                    newTable.addPlayer(newPlayer);
                    Thread newPlayerThread = new Thread(newPlayer);
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
        int serverPort = DEFAULT_PORT;
        int playersPerTable = DEFAULT_PLAYERS_PER_TABLE;
        int startingMoney = DEFAULT_STARTING_MONEY;
        int minimumBet = DEFAULT_MINIMUM_BET;
        int numberOfDecks = DEFAULT_NUMBER_OF_DECKS;
        int minimumCardsBeforeShuffle = DEFAULT_MINIMUM_CARDS_BEFORE_SHUFFLE;
        for (int i = 0; i < args.length; i += 2) {
            String option = args[i];
            String argument = null;
            try {
                argument = args[i + 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Options: [-p serverPort] [-t playersPerTable] [-m startingMoney] [-b minimumBet] [-d numberOfDecks] [-c minimumCardsBeforeShuffle]");
                System.exit(1);
            }
            switch (option) {
                case "-p":
                    try {
                        serverPort = Integer.parseInt(argument);
                    } catch (NumberFormatException e) {
                        System.err.println("Server port must be an integer");
                        System.exit(1);
                    }
                    break;
                case "-t":
                    try {
                        playersPerTable = Integer.parseInt(argument);
                    } catch (NumberFormatException e) {
                        System.err.println("Number of players per table must be an integer");
                        System.exit(1);
                    }
                    break;
                case "-m":
                    try {
                        startingMoney = Integer.parseInt(argument);
                    } catch (NumberFormatException e) {
                        System.err.println("Amount of starting money must be an integer");
                        System.exit(1);
                    }
                    break;
                case "-b":
                    try {
                        minimumBet = Integer.parseInt(argument);
                    } catch (NumberFormatException e) {
                        System.err.println("Minimum bet amount must be an integer");
                        System.exit(1);
                    }
                    break;
                case "-d":
                    try {
                        numberOfDecks = Integer.parseInt(argument);
                    } catch (NumberFormatException e) {
                        System.err.println("Number of decks must be an integer");
                        System.exit(1);
                    }
                    break;
                case "-c":
                    try {
                        minimumCardsBeforeShuffle = Integer.parseInt(argument);
                    } catch (NumberFormatException e) {
                        System.err.println("Minimum cards before shuffle must be an integer");
                        System.exit(1);
                    }
                    break;
                default:
                    System.err.println("Options: [-p serverPort] [-t playersPerTable] [-m startingMoney] [-b minimumBet] [-d numberOfDecks] [-c minimumCardsBeforeShuffle]");
                    System.exit(1);
                    break;
            }
        }
        if (playersPerTable < 1) {
            System.err.println("Number of players per table must be at least 1");
            System.exit(1);
        } else if (startingMoney < minimumBet) {
            System.err.println("Amount of starting money cannot be less than minimum bet");
            System.exit(1);
        } else if (numberOfDecks < 1) {
            System.err.println("Number of decks must be at least 1");
            System.exit(1);
        } else if (minimumCardsBeforeShuffle < 0) {
            System.err.println("Minimum cards before shuffle cannot be less than 0");
            System.exit(1);
        }
        BlackjackServer blackjackServer = new BlackjackServer(serverPort, playersPerTable, startingMoney, minimumBet, numberOfDecks, minimumCardsBeforeShuffle);
        blackjackServer.start();
    }
}