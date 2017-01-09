package BlackjackClient;

import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;

/**
 * BlackjackClient objects represent a Blackjack client that can connect to the Blackjack server.
 *
 * @author Jordan Segalman
 */

public class BlackjackClient {
    private BlackjackClientModel model;
    private BlackjackClientView view;

    public void playBlackjack() {
        this.model = new BlackjackClientModel("localhost", 44444);
        this.view = new BlackjackClientView(this.model);
        this.getServerMessage();
    }

    public void getServerMessage() {
        SwingWorker swingWorker = new SwingWorker<String, String>() {
            @Override
            public String doInBackground() throws Exception {
                return model.getServerMessage();
            }

            @Override
            public void done() {
                try {
                    changeView(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        swingWorker.execute();
    }

    public void changeView (String serverMessage) {
        String[] serverMessageParts = serverMessage.split("--");
        switch (serverMessageParts[1]) {
            case "WELCOME":
                this.view.addWelcomePanel();
                this.getServerMessage();
                break;
            case "GETBET":
                this.view.addBetPanel(serverMessageParts[2], serverMessageParts[3]);
                this.getServerMessage();
                break;
            case "BETRESPONSE":
                switch (serverMessageParts[2]) {
                    case "INVALID":
                        this.view.betError("Your bet must be a positive whole number.");
                        this.getServerMessage();
                        break;
                    case "TOOMUCH":
                        this.view.betError("You cannot bet more money than you have.");
                        this.getServerMessage();
                        break;
                    case "MINIMUM":
                        this.view.betError("You must bet at least the minimum amount.");
                        this.getServerMessage();
                        break;
                    case "SUCCESS":
                        this.view.addRoundInformationPanel(serverMessageParts[3]);
                        this.getServerMessage();
                        break;
                }
                break;
            default:
                System.out.println(serverMessage);
                break;
        }
    }

    /**
     * Main method of the client that creates objects and executes other methods.
     *
     * @param args String array of arguments passed to the client
     */

    public static void main(String[] args) {
        BlackjackClient controller = new BlackjackClient();
        controller.playBlackjack();
    }
}