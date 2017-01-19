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
                view.showRoundInformationPanel();
                view.setRoundInformationMoneyLabel(serverMessageParts[2]);
                getServerMessage();
                break;
            case "NEWPLAYERCARD":
                view.addPlayerCard(serverMessageParts[2]);
                getServerMessage();
                break;
            case "ORIGINALHANDBET":
                view.setOriginalHandBetLabel("Bet: $" + serverMessageParts[2]);
                getServerMessage();
                break;
            case "BLACKJACK":
                switch (serverMessageParts[2]) {
                    case "PLAYERANDDEALER":
                        view.setRoundInformationBlackjackLabel("You and the dealer both have Blackjack!");
                        getServerMessage();
                        break;
                    case "PLAYER":
                        view.setRoundInformationBlackjackLabel("You have Blackjack!");
                        getServerMessage();
                        break;
                    case "DEALER":
                        view.setRoundInformationBlackjackLabel("The dealer has Blackjack!");
                        getServerMessage();
                        break;
                    case "DEALERNOBLACKJACK":
                        view.setRoundInformationBlackjackLabel("The dealer does not have Blackjack.");
                        getServerMessage();
                        break;
                }
                break;
            case "NEWDEALERCARD":
                view.addDealerCard(serverMessageParts[2]);
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
                        view.setRoundInformationInsuranceLabel("Insurance Bet: $" + serverMessageParts[3]);
                        view.setRoundInformationMoneyLabel(serverMessageParts[4]);
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
                view.setRoundInformationInsuranceLabel("You do not have enough money to place an insurance bet.");
                getServerMessage();
                break;
            case "INSURANCEBETWON":
                view.setRoundInformationInsuranceLabel("You won $" + serverMessageParts[2] + " from your insurance bet.");
                view.setRoundInformationMoneyLabel(serverMessageParts[3]);
                getServerMessage();
                break;
            case "INSURANCEBETLOST":
                view.setRoundInformationInsuranceLabel("You lost your insurance bet.");
                getServerMessage();
                break;
            case "INSURANCEBETDONE":
                view.setInsuranceBetWaiting(false);
                getServerMessage();
                break;
            case "TAKETURN":
                view.setBeforeTurnWaiting(false);
                view.showTurnPanel();
                view.setTurnMoneyLabel(serverMessageParts[2]);
                getServerMessage();
                break;
            case "NEWHAND":
                model.addBlackjackHandPanel(Integer.parseInt(serverMessageParts[2]), new BlackjackHandPanel(model, serverMessageParts[3], serverMessageParts[4]));
                view.addBlackjackHandPanel(model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])), Integer.parseInt(serverMessageParts[2]));
                getServerMessage();
                break;
            case "NEWEMPTYHAND":
                model.addBlackjackHandPanel(Integer.parseInt(serverMessageParts[2]), new BlackjackHandPanel(model));
                view.addBlackjackHandPanel(model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])), Integer.parseInt(serverMessageParts[2]));
                getServerMessage();
                break;
            case "REMOVEHAND":
                view.removeBlackjackHandPanel(model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])));
                model.removeBlackjackHandPanel(Integer.parseInt(serverMessageParts[2]));
                getServerMessage();
                break;
            case "HANDBET":
                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])).setHandBet("Bet: $" + serverMessageParts[3]);
                getServerMessage();
                break;
            case "HANDVALUE":
                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])).setHandValueLabel("Hand Value: " + serverMessageParts[3]);
                getServerMessage();
                break;
            case "TURNBLACKJACK":
                switch (serverMessageParts[2]) {
                    case "PLAYERANDDEALER":
                        view.setTurnBlackjackLabel("You and the dealer both have Blackjack!");
                        getServerMessage();
                        break;
                    case "PLAYER":
                        view.setTurnBlackjackLabel("You have Blackjack!");
                        getServerMessage();
                        break;
                    case "DEALER":
                        view.setTurnBlackjackLabel("The dealer has Blackjack!");
                        getServerMessage();
                        break;
                }
                break;
            case "NEWCARD":
                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])).addCard(serverMessageParts[3]);
                getServerMessage();
                break;
            case "GETHITSTAND":
                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])).enableHitStand();
                getServerMessage();
                break;
            case "HITSTANDRESPONSE":
                switch (serverMessageParts[2]) {
                    case "ERROR":
                        model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[3])).hitStandError();
                        getServerMessage();
                        break;
                }
                break;
            case "BUST":
                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])).bust();
                getServerMessage();
                break;
            case "GETSPLITPAIRS":
                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])).enableSplitPairs();
                getServerMessage();
                break;
            case "SPLITPAIRSRESPONSE":
                switch (serverMessageParts[2]) {
                    case "ERROR":
                        model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[3])).yesNoError();
                        getServerMessage();
                        break;
                    case "SUCCESS":
                        view.setTurnMoneyLabel(serverMessageParts[3]);
                        getServerMessage();
                        break;
                }
                break;
            case "CANNOTSPLITPAIRS":
                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])).setHandMessageLabel("You do not have enough money to split pairs.");
                getServerMessage();
                break;
            case "GETDOUBLEDOWN":
                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])).enableDoubleDown();
                getServerMessage();
                break;
            case "DOUBLEDOWNRESPONSE":
                switch (serverMessageParts[2]) {
                    case "ERROR":
                        model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[3])).yesNoError();
                        getServerMessage();
                        break;
                    case "SUCCESS":
                        model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[3])).doubleDownSuccess();
                        view.setTurnMoneyLabel(serverMessageParts[4]);
                        getServerMessage();
                        break;
                }
                break;
            case "CANNOTDOUBLEDOWN":
                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])).setHandMessageLabel("You do not have enough money to double down.");
                getServerMessage();
                break;
            case "SENDRESULT":
                view.setAfterTurnWaiting(false);
                getServerMessage();
                break;
            case "REMOVEDEALERCARD":
                view.removeDealerCard(Integer.parseInt(serverMessageParts[2]));
                getServerMessage();
                break;
            case "DEALERHANDVALUE":
                view.setDealerHandValueLabel("Dealer Hand Value: " + serverMessageParts[2]);
                getServerMessage();
                break;
            case "REMOVEPLAYERCARD":
                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])).removeCard(Integer.parseInt(serverMessageParts[3]));
                getServerMessage();
                break;
            case "REVEALDOUBLEDOWNCARD":
                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[2])).revealDoubleDownCard("Your face-down card is the " + serverMessageParts[3] + ".");
                getServerMessage();
                break;
            case "ROUNDRESULT":
                switch (serverMessageParts[2]) {
                    case "BUST":
                        switch (serverMessageParts[3]) {
                            case "TIE":
                                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("You and the dealer both busted. It's a tie!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "DEALER":
                                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("You busted. The dealer wins!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "PLAYER":
                                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("The dealer busted. You win!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                        }
                        break;
                    case "NORMAL":
                        switch (serverMessageParts[3]) {
                            case "TIE":
                                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("It's a tie!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "DEALER":
                                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("The dealer wins!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "PLAYER":
                                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("You win!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                        }
                        break;
                    case "BLACKJACK":
                        switch (serverMessageParts[3]) {
                            case "TIE":
                                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("You and the dealer both have Blackjack. It's a tie!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "DEALER":
                                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("The dealer has Blackjack. The dealer wins!");
                                view.setTurnMoneyLabel(serverMessageParts[5]);
                                getServerMessage();
                                break;
                            case "PLAYER":
                                model.getBlackjackHandPanel(Integer.parseInt(serverMessageParts[4])).setHandMessageLabel("You have Blackjack. You win!");
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
                    case "BEFORETURN":
                        view.setBeforeTurnWaiting(true);
                        getServerMessage();
                        break;
                    case "AFTERTURN":
                        view.setAfterTurnWaiting(true);
                        getServerMessage();
                        break;
                }
                break;
            default:
                System.err.println("UNKNOWN MESSAGE RECEIVED FROM SERVER: \"" + serverMessage + "\"");
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