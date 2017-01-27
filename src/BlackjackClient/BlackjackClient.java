package BlackjackClient;

import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;

/**
 * BlackjackClient objects represent a Blackjack client that can connect to the Blackjack server.
 *
 * @author Jordan Segalman
 */

public class BlackjackClient {
    private static final String DEFAULT_SERVER = "localhost";
    private static final int DEFAULT_PORT = 44444;
    private BlackjackClientModel model;
    private BlackjackClientView view;

    private void playBlackjack() {
        model = new BlackjackClientModel(DEFAULT_SERVER, DEFAULT_PORT);
        view = new BlackjackClientView(model);
        getServerMessage();
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
                view.showWelcomePanel();
                getServerMessage();
                break;
            case "GETBET":
                view.setWelcomeWaiting(false);
                view.setContinuePlayingWaiting(false);
                view.showBetPanel();
                view.setBetMoneyLabel(serverMessageParts[2]);
                view.setMinimumBetLabel(serverMessageParts[3]);
                getServerMessage();
                break;
            case "BETRESPONSE":
                switch (serverMessageParts[2]) {
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
                        view.setBetMoneyLabel(serverMessageParts[3]);
                        getServerMessage();
                        break;
                }
                break;
            case "NEWROUND":
                view.setBetWaiting(false);
                view.showTurnPanel();
                view.setTurnMoneyLabel(serverMessageParts[2]);
                getServerMessage();
                break;
            case "BLACKJACK":
                switch (serverMessageParts[2]) {
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
                view.addDealerCard(model.getCardImageLabel(serverMessageParts[2]));
                getServerMessage();
                break;
            case "GETINSURANCEBET":
                view.enableInsuranceBet();
                getServerMessage();
                break;
            case "INSURANCEBETRESPONSE":
                switch (serverMessageParts[2]) {
                    case "ERROR":
                        view.insuranceBetError();
                        getServerMessage();
                        break;
                    case "PLACED":
                        view.insuranceBetSuccess();
                        view.setInsuranceBetLabel("Insurance Bet: $" + serverMessageParts[3]);
                        view.setTurnMoneyLabel(serverMessageParts[4]);
                        getServerMessage();
                        break;
                    case "NOTPLACED":
                        view.insuranceBetSuccess();
                        view.insuranceBetNotPlaced();
                        getServerMessage();
                        break;
                }
                break;
            case "CANNOTINSURANCEBET":
                view.setInsuranceBetLabel("You do not have enough money to place an insurance bet.");
                getServerMessage();
                break;
            case "INSURANCEBETWON":
                view.setInsuranceBetLabel("You won $" + serverMessageParts[2] + " from your insurance bet.");
                view.setTurnMoneyLabel(serverMessageParts[3]);
                getServerMessage();
                break;
            case "INSURANCEBETLOST":
                view.setInsuranceBetLabel("You lost your insurance bet.");
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
                model.addPlayerHandPanel(Integer.parseInt(serverMessageParts[2]), new BlackjackHandPanel(model));
                view.addPlayerHandPanel(model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])), Integer.parseInt(serverMessageParts[2]));
                getServerMessage();
                break;
            case "REMOVEHAND":
                view.removePlayerHandPanel(model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])));
                model.removePlayerHandPanel(Integer.parseInt(serverMessageParts[2]));
                getServerMessage();
                break;
            case "HANDBET":
                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).setHandBet(serverMessageParts[3]);
                getServerMessage();
                break;
            case "HANDVALUE":
                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).setHandValueLabel(serverMessageParts[3]);
                getServerMessage();
                break;
            case "TURNBLACKJACK":
                switch (serverMessageParts[2]) {
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
                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).addCard(model.getCardImageLabel(serverMessageParts[3]));
                getServerMessage();
                break;
            case "TURNOPTION":
                switch (serverMessageParts[2]) {
                    case "BOTH":
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).enableSplitPairs();
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).enableDoubleDown();
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).enableHitStand();
                        getServerMessage();
                        break;
                    case "SPLITPAIRS":
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).enableSplitPairs();
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).enableHitStand();
                        getServerMessage();
                        break;
                    case "DOUBLEDOWN":
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).enableDoubleDown();
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).enableHitStand();
                        getServerMessage();
                        break;
                    case "NEITHER":
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).enableHitStand();
                        getServerMessage();
                        break;
                }
                break;
            case "TURNOPTIONERROR":
                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).turnError();
                getServerMessage();
                break;
            case "BUST":
                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).bust();
                getServerMessage();
                break;
            case "SPLITPAIRSRESPONSE":
                switch (serverMessageParts[2]) {
                    case "SUCCESS":
                        view.setTurnMoneyLabel(serverMessageParts[3]);
                        getServerMessage();
                        break;
                }
                break;
            case "DOUBLEDOWNRESPONSE":
                switch (serverMessageParts[2]) {
                    case "SUCCESS":
                        model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[3])).doubleDownSuccess();
                        view.setTurnMoneyLabel(serverMessageParts[4]);
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
                view.setDealerHandValueLabel(serverMessageParts[2]);
                getServerMessage();
                break;
            case "REMOVEDOUBLEDOWNFACEDOWNCARD":
                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[2])).removeDoubleDownFaceDownCard();
                getServerMessage();
                break;
            case "ROUNDRESULT":
                switch (serverMessageParts[2]) {
                    case "BUST":
                        switch (serverMessageParts[3]) {
                            case "TIE":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("You and the dealer both busted. It's a tie!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "DEALER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("You busted. The dealer wins!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "PLAYER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("The dealer busted. You win!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                        }
                        break;
                    case "NORMAL":
                        switch (serverMessageParts[3]) {
                            case "TIE":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("It's a tie!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "DEALER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("The dealer wins!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "PLAYER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("You win!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                        }
                        break;
                    case "BLACKJACK":
                        switch (serverMessageParts[3]) {
                            case "TIE":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("You and the dealer both have Blackjack. It's a tie!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "DEALER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("The dealer has Blackjack. The dealer wins!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "PLAYER":
                                model.getPlayerHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("You have Blackjack. You win!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                        }
                        break;
                }
                break;
            case "GETCONTINUEPLAYING":
                view.showContinuePlayingPanel();
                view.enableContinuePlaying();
                view.setContinuePlayingMessageLabel("Would you like to keep playing?");
                view.setContinuePlayingMoneyLabel(serverMessageParts[2]);
                getServerMessage();
                break;
            case "CONTINUEPLAYINGRESPONSE":
                switch (serverMessageParts[2]) {
                    case "ERROR":
                        view.continuePlayingError();
                        getServerMessage();
                        break;
                    case "CONTINUE":
                        view.reset();
                        model.reset();
                        getServerMessage();
                        break;
                }
                break;
            case "CANNOTCONTINUEPLAYING":
                view.showContinuePlayingPanel();
                view.setContinuePlayingMessageLabel("You do not have enough money to make the minimum bet.");
                view.setContinuePlayingMoneyLabel(serverMessageParts[2]);
                getServerMessage();
                break;
            case "GAMEOVER":
                view.gameOver();
                getServerMessage();
                break;
            case "WAITING":
                switch (serverMessageParts[2]) {
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
     * Main method of the client that creates objects and executes other methods.
     *
     * @param args String array of arguments passed to the client
     */

    public static void main(String[] args) {
        BlackjackClient controller = new BlackjackClient();
        controller.playBlackjack();
    }
}