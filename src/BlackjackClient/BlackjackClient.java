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
            case "ORIGINALHANDBET":
                this.view.setOriginalHandBetLabel("Bet: $" + serverMessageParts[2]);
                this.getServerMessage();
                break;
            case "BLACKJACK":
                switch (serverMessageParts[2]) {
                    case "PLAYERANDDEALER":
                        this.view.setRoundInformationBlackjackLabel("You and the dealer both have Blackjack!");
                        this.getServerMessage();
                        break;
                    case "PLAYER":
                        this.view.setRoundInformationBlackjackLabel("You have Blackjack!");
                        this.getServerMessage();
                        break;
                    case "DEALER":
                        this.view.setRoundInformationBlackjackLabel("The dealer has Blackjack!");
                        this.getServerMessage();
                        break;
                    case "DEALERNOBLACKJACK":
                        this.view.setRoundInformationBlackjackLabel("The dealer does not have Blackjack.");
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
                        this.view.setRoundInformationInsuranceLabel("Insurance Bet: $" + serverMessageParts[3]);
                        this.view.setRoundInformationMoneyLabel(serverMessageParts[4]);
                        this.getServerMessage();
                        break;
                    case "NOTPLACED":
                        this.view.insuranceBetSuccess();
                        this.view.insuranceBetNotPlaced();
                        this.getServerMessage();
                        break;
                }
                break;
            case "INSURANCEBETWON":
                this.view.setRoundInformationInsuranceLabel("You won $" + serverMessageParts[2] + " from your insurance bet.");
                this.view.setRoundInformationMoneyLabel(serverMessageParts[3]);
                this.getServerMessage();
                break;
            case "INSURANCEBETLOST":
                this.view.setRoundInformationInsuranceLabel("You lost your insurance bet.");
                this.getServerMessage();
                break;
            case "INSURANCEBETDONE":
                this.view.setInsuranceBetWaiting(false);
                this.getServerMessage();
                break;
            case "TAKETURN":
                this.view.setBeforeTurnWaiting(false);
                this.view.showTurnPanel();
                this.getServerMessage();
                break;
            case "NEWHAND":
                this.model.addPlayerHandPanel(Integer.parseInt(serverMessageParts[2]), new PlayerHandPanel(this.model, serverMessageParts[3], serverMessageParts[4]));
                this.view.addPlayerHandPanel(this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])), Integer.parseInt(serverMessageParts[2]));
                this.getServerMessage();
                break;
            case "NEWEMPTYHAND":
                this.model.addPlayerHandPanel(Integer.parseInt(serverMessageParts[2]), new PlayerHandPanel(this.model));
                this.view.addPlayerHandPanel(this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])), Integer.parseInt(serverMessageParts[2]));
                this.getServerMessage();
                break;
            case "REMOVEHAND":
                this.view.removePlayerHandPanel(this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])));
                this.model.removePlayerHandPanel(Integer.parseInt(serverMessageParts[2]));
                this.getServerMessage();
                break;
            case "HANDBET":
                this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).setHandBet("Bet: $" + serverMessageParts[3]);
                this.getServerMessage();
                break;
            case "HANDVALUE":
                this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).setHandValueLabel("Hand Value: " + serverMessageParts[3]);
                this.getServerMessage();
                break;
            case "TURNBLACKJACK":
                switch (serverMessageParts[2]) {
                    case "PLAYERANDDEALER":
                        this.view.setTurnBlackjackLabel("You and the dealer both have Blackjack!");
                        this.getServerMessage();
                        break;
                    case "PLAYER":
                        this.view.setTurnBlackjackLabel("You have Blackjack!");
                        this.getServerMessage();
                        break;
                    case "DEALER":
                        this.view.setTurnBlackjackLabel("The dealer has Blackjack!");
                        this.getServerMessage();
                        break;
                }
                break;
            case "NEWCARD":
                this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).addCard(serverMessageParts[3]);
                this.getServerMessage();
                break;
            case "GETHITSTAND":
                this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).enableHitStand();
                this.getServerMessage();
                break;
            case "HITSTANDRESPONSE":
                switch (serverMessageParts[2]) {
                    case "ERROR":
                        this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).hitStandError();
                        this.getServerMessage();
                        break;
                }
                break;
            case "BUST":
                this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).bust();
                this.getServerMessage();
                break;
            case "GETDOUBLEDOWN":
                this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).enableDoubleDown();
                this.getServerMessage();
                break;
            case "DOUBLEDOWNRESPONSE":
                switch (serverMessageParts[2]) {
                    case "ERROR":
                        this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).yesNoError();
                        this.getServerMessage();
                        break;
                    case "SUCCESS":
                        this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).doubleDownSuccess();
                        this.getServerMessage();
                        break;
                }
                break;
            case "CANNOTDOUBLEDOWN":
                this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).setHandMessageLabel("You do not have enough money to double down.");
                this.getServerMessage();
                break;
            case "GETSPLITPAIRS":
                this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).enableSplitPairs();
                this.getServerMessage();
                break;
            case "SPLITPAIRSRESPONSE":
                switch (serverMessageParts[2]) {
                    case "ERROR":
                        this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).yesNoError();
                        this.getServerMessage();
                        break;
                }
                break;
            case "CANNOTSPLITPAIRS":
                this.model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).setHandMessageLabel("You do not have enough money to split pairs.");
                this.getServerMessage();
                break;
            case "REMOVEDEALERCARD":
                this.view.removeDealerCard(Integer.parseInt(serverMessageParts[2]));
                this.getServerMessage();
                break;
            case "DEALERHANDVALUE":
                this.view.setDealerHandValueLabel("Dealer Hand Value: " + serverMessageParts[2]);
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
                    case "BEFORETURN":
                        this.view.setBeforeTurnWaiting(true);
                        this.getServerMessage();
                        break;
                    case "AFTERTURN":
                        this.view.setAfterTurnWaiting(true);
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