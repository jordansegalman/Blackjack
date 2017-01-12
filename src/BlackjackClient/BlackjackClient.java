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

    private void playBlackjack() {
        this.model = new BlackjackClientModel("localhost", 44444);
        this.view = new BlackjackClientView(this.model);
        this.getServerMessage();
    }

    private void getServerMessage() {
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

    private void changeView (String serverMessage) {
        String[] serverMessageParts = serverMessage.split("--");
        switch (serverMessageParts[1]) {
            case "WELCOME":
                this.view.showWelcomePanel();
                this.getServerMessage();
                break;
            case "GETBET":
                this.view.setWelcomeWaiting(false);
                this.view.showBetPanel();
                this.view.setBetMoneyLabel(serverMessageParts[2]);
                this.view.setMinimumBetLabel(serverMessageParts[3]);
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
                        this.view.betSuccess();
                        this.view.setBetMoneyLabel(serverMessageParts[3]);
                        this.getServerMessage();
                        break;
                }
                break;
            case "NEWROUND":
                this.view.setBetWaiting(false);
                this.view.showRoundInformationPanel();
                this.view.setRoundInformationMoneyLabel(serverMessageParts[2]);
                this.getServerMessage();
                break;
            case "NEWPLAYERCARD":
                this.view.addPlayerCard(serverMessageParts[2]);
                this.getServerMessage();
                break;
            case "BLACKJACK":
                switch (serverMessageParts[2]) {
                    case "PLAYER":
                        this.view.setRoundInformationMessage("You have Blackjack!");
                        this.getServerMessage();
                        break;
                    case "PLAYERANDDEALER":
                        this.view.setRoundInformationMessage("You and the dealer both have Blackjack!");
                        this.getServerMessage();
                        break;
                    case "DEALER":
                        this.view.setRoundInformationMessage("The dealer has Blackjack!");
                        this.getServerMessage();
                        break;
                    case "DEALERNOBLACKJACK":
                        this.view.setRoundInformationMessage("The dealer does not have Blackjack.");
                        this.getServerMessage();
                        break;
                }
                break;
            case "NEWDEALERCARD":
                this.view.addDealerCard(serverMessageParts[2]);
                this.getServerMessage();
                break;
            case "GETINSURANCEBET":
                this.view.enableInsuranceBet();
                this.getServerMessage();
                break;
            case "INSURANCEBETRESPONSE":
                switch (serverMessageParts[2]) {
                    case "ERROR":
                        this.view.insuranceBetError();
                        this.getServerMessage();
                        break;
                    case "PLACED":
                        this.view.insuranceBetSuccess();
                        this.view.setRoundInformationMoneyLabel(serverMessageParts[3]);
                        this.getServerMessage();
                        break;
                    case "NOTPLACED":
                        this.view.insuranceBetSuccess();
                        this.getServerMessage();
                        break;
                }
                break;
            case "INSURANCEBETWINNINGS":
                this.view.setInsuranceBetWaiting(false);
                this.view.setRoundInformationSecondMessage("You won $" + serverMessageParts[2] + " from your insurance bet.");
                this.view.setRoundInformationMoneyLabel(serverMessageParts[3]);
                this.getServerMessage();
                break;
            case "WAITING":
                switch (serverMessageParts[2]) {
                    case "WELCOME":
                        this.view.setWelcomeWaiting(true);
                        this.getServerMessage();
                        break;
                    case "BET":
                        this.view.setBetWaiting(true);
                        this.getServerMessage();
                        break;
                    case "INSURANCEBET":
                        this.view.setInsuranceBetWaiting(true);
                        this.getServerMessage();
                        break;
                    case "TURN":
                        this.view.setTurnWaiting(true);
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