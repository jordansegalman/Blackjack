import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;

/**
 * BlackjackClient objects connect to the Blackjack server and coordinate between the model and view.
 *
 * @author Jordan Segalman
 */

public class BlackjackClient {
    private static final String DEFAULT_SERVER_ADDRESS = "localhost";   // default server address
    private static final int DEFAULT_SERVER_PORT = 44444;               // default server port
    private BlackjackClientModel model;                                 // client GUI model
    private BlackjackClientView view;                                   // client GUI view

    /**
     * Sets up the client GUI and gets the first message from the server.
     */

    public void start() {
        model = new BlackjackClientModel(DEFAULT_SERVER_ADDRESS, DEFAULT_SERVER_PORT);
        view = new BlackjackClientView(this);
        getServerMessage();
    }

    /**
     * Gets a message from the server and calls the changeView method with the message.
     */

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

    /**
     * Changes the client view based on which message was received from the server.
     *
     * @param serverMessage Message received from server
     */

    private void changeView (String serverMessage) {
        String[] serverMessageComponents = serverMessage.split("--");   // array containing the components of the server message
        switch (serverMessageComponents[1]) {
            case "WELCOME":
                view.showWelcomePanel();
                getServerMessage();
                break;
            case "GETBET":
                view.setWelcomeWaiting(false);
                view.setContinuePlayingWaiting(false);
                view.showBetPanel();
                view.setBetMoneyLabel(serverMessageComponents[2]);
                view.setMinimumBetLabel(serverMessageComponents[3]);
                getServerMessage();
                break;
            case "BETRESPONSE":
                switch (serverMessageComponents[2]) {
                    case "INVALID":
                        view.betError("Your bet must be a positive whole number.");
                        getServerMessage();
                        break;
                    case "TOOMUCH":
                        view.betError("You cannot bet more money than you have.");
                        getServerMessage();
                        break;
                    case "MINIMUM":
                        view.betError("You must bet at least the minimum amount.");
                        getServerMessage();
                        break;
                    case "SUCCESS":
                        view.betSuccess();
                        view.setBetMoneyLabel(serverMessageComponents[3]);
                        getServerMessage();
                        break;
                }
                break;
            case "NEWROUND":
                view.setBetWaiting(false);
                view.showTurnPanel();
                view.setTurnMoneyLabel(serverMessageComponents[2]);
                getServerMessage();
                break;
            case "BLACKJACK":
                switch (serverMessageComponents[2]) {
                    case "PLAYERANDDEALER":
                        view.setBlackjackLabel("You and the dealer both have Blackjack!");
                        getServerMessage();
                        break;
                    case "PLAYER":
                        view.setBlackjackLabel("You have Blackjack!");
                        getServerMessage();
                        break;
                    case "DEALER":
                        view.setBlackjackLabel("The dealer has Blackjack!");
                        getServerMessage();
                        break;
                    case "DEALERNOBLACKJACK":
                        view.setBlackjackLabel("The dealer does not have Blackjack.");
                        getServerMessage();
                        break;
                }
                break;
            case "NEWDEALERCARD":
                view.addDealerCard(model.getCardImageLabel(serverMessageComponents[2]));
                getServerMessage();
                break;
            case "GETINSURANCEBET":
                view.enableInsuranceBet();
                getServerMessage();
                break;
            case "INSURANCEBETRESPONSE":
                switch (serverMessageComponents[2]) {
                    case "ERROR":
                        view.insuranceBetError();
                        getServerMessage();
                        break;
                    case "PLACED":
                        view.insuranceBetSuccess();
                        view.setMessageLabel("Insurance Bet: $" + serverMessageComponents[3]);
                        view.setTurnMoneyLabel(serverMessageComponents[4]);
                        getServerMessage();
                        break;
                    case "NOTPLACED":
                        view.insuranceBetSuccess();
                        view.removeInsuranceBetInfo();
                        getServerMessage();
                        break;
                }
                break;
            case "CANNOTINSURANCEBET":
                view.setMessageLabel("You do not have enough money to place an insurance bet.");
                getServerMessage();
                break;
            case "INSURANCEBETWON":
                view.setMessageLabel("You won $" + serverMessageComponents[2] + " from your insurance bet.");
                view.setTurnMoneyLabel(serverMessageComponents[3]);
                getServerMessage();
                break;
            case "INSURANCEBETLOST":
                view.setMessageLabel("You lost your insurance bet.");
                getServerMessage();
                break;
            case "INSURANCEBETDONE":
                view.setInsuranceBetWaiting(false);
                getServerMessage();
                break;
            case "TAKETURN":
                view.setTurnWaiting(false);
                view.removeInsuranceBetInfo();
                getServerMessage();
                break;
            case "NEWHAND":
                model.addPlayerHandPanel(Integer.parseInt(serverMessageComponents[2]), new BlackjackHandPanel(this));
                view.addPlayerHandPanel(model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[2])), Integer.parseInt(serverMessageComponents[2]));
                getServerMessage();
                break;
            case "REMOVEHAND":
                view.removePlayerHandPanel(model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[2])));
                model.removePlayerHandPanel(Integer.parseInt(serverMessageComponents[2]));
                getServerMessage();
                break;
            case "HANDBET":
                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[2])).setHandBet(serverMessageComponents[3]);
                getServerMessage();
                break;
            case "HANDVALUE":
                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[2])).setHandValueLabel(serverMessageComponents[3]);
                getServerMessage();
                break;
            case "TURNBLACKJACK":
                switch (serverMessageComponents[2]) {
                    case "PLAYERANDDEALER":
                        view.setBlackjackLabel("You and the dealer both have Blackjack!");
                        getServerMessage();
                        break;
                    case "PLAYER":
                        view.setBlackjackLabel("You have Blackjack!");
                        getServerMessage();
                        break;
                    case "DEALER":
                        view.setBlackjackLabel("The dealer has Blackjack!");
                        getServerMessage();
                        break;
                }
                break;
            case "NEWPLAYERCARD":
                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[2])).addCard(model.getCardImageLabel(serverMessageComponents[3]));
                getServerMessage();
                break;
            case "TURNOPTION":
                switch (serverMessageComponents[2]) {
                    case "BOTH":
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[3])).enableSplitPairs();
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[3])).enableDoubleDown();
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[3])).enableHitStand();
                        getServerMessage();
                        break;
                    case "SPLITPAIRS":
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[3])).enableSplitPairs();
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[3])).enableHitStand();
                        getServerMessage();
                        break;
                    case "DOUBLEDOWN":
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[3])).enableDoubleDown();
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[3])).enableHitStand();
                        getServerMessage();
                        break;
                    case "NEITHER":
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[3])).enableHitStand();
                        getServerMessage();
                        break;
                }
                break;
            case "TURNOPTIONERROR":
                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[2])).turnError();
                getServerMessage();
                break;
            case "BUST":
                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[2])).bust();
                getServerMessage();
                break;
            case "SPLITPAIRSRESPONSE":
                switch (serverMessageComponents[2]) {
                    case "SUCCESS":
                        view.setTurnMoneyLabel(serverMessageComponents[3]);
                        getServerMessage();
                        break;
                }
                break;
            case "DOUBLEDOWNRESPONSE":
                switch (serverMessageComponents[2]) {
                    case "SUCCESS":
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[3])).doubleDownSuccess();
                        view.setTurnMoneyLabel(serverMessageComponents[4]);
                        getServerMessage();
                        break;
                }
                break;
            case "SENDRESULT":
                view.setTurnWaiting(false);
                getServerMessage();
                break;
            case "REMOVEDEALERFACEDOWNCARD":
                view.removeDealerFaceDownCard();
                getServerMessage();
                break;
            case "DEALERHANDVALUE":
                view.setDealerHandValueLabel(serverMessageComponents[2]);
                getServerMessage();
                break;
            case "REMOVEDOUBLEDOWNFACEDOWNCARD":
                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[2])).removeDoubleDownFaceDownCard();
                getServerMessage();
                break;
            case "ROUNDRESULT":
                switch (serverMessageComponents[2]) {
                    case "BUST":
                        switch (serverMessageComponents[3]) {
                            case "TIE":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[4])).setHandMessageLabel("You and the dealer both busted. It's a tie!");
                                view.setTurnMoneyLabel(serverMessageComponents[5]);
                                getServerMessage();
                                break;
                            case "DEALER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[4])).setHandMessageLabel("You busted. The dealer wins!");
                                view.setTurnMoneyLabel(serverMessageComponents[5]);
                                getServerMessage();
                                break;
                            case "PLAYER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[4])).setHandMessageLabel("The dealer busted. You win!");
                                view.setTurnMoneyLabel(serverMessageComponents[5]);
                                getServerMessage();
                                break;
                        }
                        break;
                    case "NORMAL":
                        switch (serverMessageComponents[3]) {
                            case "TIE":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[4])).setHandMessageLabel("It's a tie!");
                                view.setTurnMoneyLabel(serverMessageComponents[5]);
                                getServerMessage();
                                break;
                            case "DEALER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[4])).setHandMessageLabel("The dealer wins!");
                                view.setTurnMoneyLabel(serverMessageComponents[5]);
                                getServerMessage();
                                break;
                            case "PLAYER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[4])).setHandMessageLabel("You win!");
                                view.setTurnMoneyLabel(serverMessageComponents[5]);
                                getServerMessage();
                                break;
                        }
                        break;
                    case "BLACKJACK":
                        switch (serverMessageComponents[3]) {
                            case "TIE":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[4])).setHandMessageLabel("You and the dealer both have Blackjack. It's a tie!");
                                view.setTurnMoneyLabel(serverMessageComponents[5]);
                                getServerMessage();
                                break;
                            case "DEALER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[4])).setHandMessageLabel("The dealer has Blackjack. The dealer wins!");
                                view.setTurnMoneyLabel(serverMessageComponents[5]);
                                getServerMessage();
                                break;
                            case "PLAYER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageComponents[4])).setHandMessageLabel("You have Blackjack. You win!");
                                view.setTurnMoneyLabel(serverMessageComponents[5]);
                                getServerMessage();
                                break;
                        }
                        break;
                }
                break;
            case "GETCONTINUEPLAYING":
                view.enableContinuePlaying();
                getServerMessage();
                break;
            case "CONTINUEPLAYINGRESPONSE":
                switch (serverMessageComponents[2]) {
                    case "ERROR":
                        view.continuePlayingError();
                        getServerMessage();
                        break;
                    case "CONTINUE":
                        view.reset();
                        model.reset();
                        view.showContinuePlayingPanel();
                        getServerMessage();
                        break;
                }
                break;
            case "GAMEOVER":
                view.showContinuePlayingPanel();
                view.setContinuePlayingMoneyLabel(serverMessageComponents[2]);
                view.gameOver();
                getServerMessage();
                break;
            case "WAITING":
                switch (serverMessageComponents[2]) {
                    case "WELCOME":
                        view.setWelcomeWaiting(true);
                        view.setContinuePlayingWaiting(true);
                        getServerMessage();
                        break;
                    case "BET":
                        view.setBetWaiting(true);
                        getServerMessage();
                        break;
                    case "INSURANCEBET":
                        view.setInsuranceBetWaiting(true);
                        getServerMessage();
                        break;
                    case "TURN":
                        view.setTurnWaiting(true);
                        getServerMessage();
                        break;
                }
                break;
            default:
                System.err.println("Unknown message received from server: \"" + serverMessage + "\"");
                break;
        }
    }

    /**
     * Calls the model sendClientMessage method with the given clientMessage.
     *
     * @param clientMessage Message to send to server
     */

    public void sendClientMessage(String clientMessage) {
        model.sendClientMessage(clientMessage);
    }

    /**
     * Calls the model quitGame method.
     */

    public void quitGame() {
        model.quitGame();
    }

    /**
     * Main method of the client that creates objects and executes other methods.
     *
     * @param args String array of arguments passed to the client
     */

    public static void main(String[] args) {
        BlackjackClient controller = new BlackjackClient();
        controller.start();
    }
}