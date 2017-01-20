package BlackjackServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Player objects represent a player in the Blackjack game.
 *
 * @author Jordan Segalman
 */

public class Player implements Runnable {
    private static final int MAXIMUM_SCORE = 21;
    private static final int MINIMUM_DOUBLE_DOWN_SOFT_VALUE = 9;
    private static final int MAXIMUM_DOUBLE_DOWN_SOFT_VALUE = 11;
    private static final int MINIMUM_DOUBLE_DOWN_VALUE = 19;
    private static final int MAXIMUM_DOUBLE_DOWN_VALUE = 21;
    private static final double BLACKJACK_PAYOUT_MULTIPLIER = 3.0 / 2.0;
    private Table table;                                                // table to join
    private BufferedReader in;                                          // in to client
    private PrintWriter out;                                            // out from client
    private ArrayList<BlackjackHand> playerHands = new ArrayList<>();   // holds player hands
    private BlackjackHand originalPlayerHand;                           // player hand to hold cards
    private double money = 2500;                                        // money available to bet
    private String clientMessage;                                       // message received from client
    private boolean receivedBet = false;                                // true if bet made, false if not
    private boolean hasBlackjack = false;                               // true if player has Blackjack, false if does not
    private String choice;                                              // choice player made
    private boolean receivedChoice = false;                             // true if player made a choice, false if did not
    private double insuranceBet;                                        // amount of insurance bet
    private boolean placedInsuranceBet = false;                         // true if insurance bet made, false if not
    private CountDownLatch startLatch;                                  // latch to wait for all players to join game
    private CountDownLatch betLatch;                                    // latch to wait for all players to place their bets
    private CountDownLatch insuranceBetLatch;                           // latch to wait for all players to place their insurance bets
    private CountDownLatch dealLatch;                                   // latch to wait for all players to be dealt cards
    private CountDownLatch dealerTurnLatch;                             // latch to wait for dealer to finish turn
    private boolean continuePlaying = false;                            // true if player wants to keep playing, false if does not

    /**
     * Constructor for player object.
     *
     * @param socket Socket from server socket
     * @param table Table the player joined
     */

    public Player(Socket socket, Table table) {
        this.table = table;
        try {
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());     // input stream reader from socket
            in = new BufferedReader(isr);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Player thread run method.
     */

    @Override
    public void run() {
//        out.println("INFOMESSAGE--Welcome to Blackjack!");
        out.println("SERVERMESSAGE--WELCOME");
        do {
            playBlackjack();
        } while (continuePlaying);
//        out.println("INFOMESSAGE--You leave with $" + String.format("%.2f", money) + ".");
//        out.println("GAMEOVERMESSAGE--Thanks for playing!");
        out.println("SERVERMESSAGE--GAMEOVER");
    }

    /**
     * Plays Blackjack.
     */

    private void playBlackjack() {
        setupPlayer();
        try {
            startLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getBet();
        try {
            betLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            dealLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendRoundInformation();
        try {
            dealerTurnLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendDealerCards();
        for (BlackjackHand hand : playerHands) {
            sendResult(hand);
        }
        getContinuePlaying();
    }

    /**
     * Sets the player up for a new round of Blackjack.
     */

    private void setupPlayer() {
        playerHands.clear();
        originalPlayerHand = new BlackjackHand();
        playerHands.add(originalPlayerHand);
        receivedBet = false;
        hasBlackjack = false;
        receivedChoice = false;
        placedInsuranceBet = false;
        continuePlaying = false;
        startLatch = new CountDownLatch(1);
        betLatch = new CountDownLatch(1);
        insuranceBetLatch = new CountDownLatch(1);
        dealLatch = new CountDownLatch(1);
        dealerTurnLatch = new CountDownLatch(1);
//        out.println("INFOMESSAGE--Waiting for other players to join.");
        out.println("SERVERMESSAGE--WAITING--WELCOME");
    }

    /**
     * Gets the player's bet.
     */

    private void getBet() {
        do {
            boolean betNotNumeric = false;  // true if bet is not a positive integer, false if it is
//            out.println("INFOMESSAGE--You have $" + String.format("%.2f", money) + ".");
//            out.println("REPLYMESSAGE--The minimum bet is $" + String.format("%.2f", table.minimumBet()) + ". How much would you like to bet?");
            out.println("SERVERMESSAGE--GETBET--" + String.format("%.2f", money) + "--" + String.format("%.2f", table.minimumBet()));
            try {
                while (!receivedBet) {
                    if ((clientMessage = in.readLine()) != null) {
                        try {
                            int bet = Integer.parseInt(clientMessage);
                            originalPlayerHand.placeBet(bet);
                            receivedBet = true;
                        } catch (NumberFormatException e) {
                            betNotNumeric = true;
                            receivedBet = true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (betNotNumeric) {
//                out.println("INFOMESSAGE--Your bet must be a positive whole number.");
                out.println("SERVERMESSAGE--BETRESPONSE--INVALID");
                receivedBet = false;
            } else if (originalPlayerHand.bet() > money) {
//                out.println("INFOMESSAGE--You cannot bet more money than you have.");
                out.println("SERVERMESSAGE--BETRESPONSE--TOOMUCH");
                receivedBet = false;
            } else if (originalPlayerHand.bet() < table.minimumBet()) {
//                out.println("INFOMESSAGE--You must bet at least the minimum amount.");
                out.println("SERVERMESSAGE--BETRESPONSE--MINIMUM");

                receivedBet = false;
            }
        } while (!receivedBet);
        money -= originalPlayerHand.bet();
        table.placedBetsLatchCountDown();
        out.println("SERVERMESSAGE--BETRESPONSE--SUCCESS--" + String.format("%.2f", money));
        if (table.numPlayers() > 1) {
//            out.println("INFOMESSAGE--Waiting for other players to place their bets.");
            out.println("SERVERMESSAGE--WAITING--BET");
        }
    }

    /**
     * Sends initial round information to the player including the
     * player's first two cards, the card the dealer is showing,
     * and whether or not the player or dealer has Blackjack.
     */

    private void sendRoundInformation() {
        out.println("SERVERMESSAGE--NEWROUND--" + String.format("%.2f", money));
//        out.println("INFOMESSAGE--Your Cards:");
        for (int i = 0; i < originalPlayerHand.size(); i++) {
//            out.println("INFOMESSAGE--" + originalPlayerHand.getCard(i));
            out.println("SERVERMESSAGE--NEWPLAYERCARD--" + originalPlayerHand.getCard(i));
        }
        out.println("SERVERMESSAGE--ORIGINALHANDBET--" + String.format("%.2f", originalPlayerHand.bet()));
        if (originalPlayerHand.blackjackValue() == MAXIMUM_SCORE) {
//            out.println("INFOMESSAGE--You have Blackjack.");
            out.println("SERVERMESSAGE--BLACKJACK--PLAYER");
            hasBlackjack = true;
        }
//        out.println("INFOMESSAGE--The dealer is showing the " + table.dealerShownCard() + ".");
        out.println("SERVERMESSAGE--NEWDEALERCARD--" + table.dealerShownCard());
        out.println("SERVERMESSAGE--NEWDEALERCARD--back");
        if (table.dealerShownCard().rank() == Card.Rank.ACE) {
            getInsuranceBet();
        }
        table.placedInsuranceBetsLatchCountDown();
        try {
            insuranceBetLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (originalPlayerHand.blackjackValue() == MAXIMUM_SCORE && table.dealerHand().blackjackValue() == MAXIMUM_SCORE) {
//            out.println("INFOMESSAGE--You and the dealer both have Blackjack.");
            out.println("SERVERMESSAGE--BLACKJACK--PLAYERANDDEALER");
            hasBlackjack = true;
            if (placedInsuranceBet) {
                money += (insuranceBet + (insuranceBet * 2));
//                out.println("INFOMESSAGE--You won $" + String.format("%.2f", insuranceBet * 2) + " from your insurance bet.");
                out.println("SERVERMESSAGE--INSURANCEBETWINNINGS--" + String.format("%.2f", insuranceBet * 2) + "--" + String.format("%.2f", money));
            }
        } else if (table.dealerHand().blackjackValue() == MAXIMUM_SCORE) {
//            out.println("INFOMESSAGE--The dealer has Blackjack.");
            out.println("SERVERMESSAGE--BLACKJACK--DEALER");
            if (placedInsuranceBet) {
                money += (insuranceBet + (insuranceBet * 2));
//                out.println("INFOMESSAGE--You won $" + String.format("%.2f", insuranceBet * 2) + " from your insurance bet.");
                out.println("SERVERMESSAGE--INSURANCEBETWON--" + String.format("%.2f", insuranceBet * 2) + "--" + String.format("%.2f", money));
            }
        } else if (table.dealerShownCard().rank() == Card.Rank.ACE && table.dealerHand().blackjackValue() != MAXIMUM_SCORE){
//            out.println("INFOMESSAGE--The dealer does not have Blackjack.");
            out.println("SERVERMESSAGE--BLACKJACK--DEALERNOBLACKJACK");
            if (placedInsuranceBet) {
                out.println("SERVERMESSAGE--INSURANCEBETLOST");
            }
        }
        if (table.dealerShownCard().rank() == Card.Rank.ACE) {
            out.println("SERVERMESSAGE--INSURANCEBETDONE");
        }
        table.turnLatchCountDown();
        if (table.numPlayers() > 1) {
//            out.println("INFOMESSAGE--Waiting for other players to take their turns.");
            out.println("SERVERMESSAGE--WAITING--BEFORETURN");
        }
    }

    /**
     * Asks the player if they want to place an insurance bet.
     */

    private void getInsuranceBet() {
        if (money >= originalPlayerHand.bet() / 2) {
            receivedChoice = false;
            do {
//                out.println("REPLYMESSAGE--Would you like to place an insurance bet? [Y/n]");
                out.println("SERVERMESSAGE--GETINSURANCEBET");
                getChoice();
                if (!choice.equals("Yes") && !choice.equals("No")) {
//                    out.println("INFOMESSAGE--Please enter either 'Yes' or 'No'.");
                    out.println("SERVERMESSAGE--INSURANCEBETRESPONSE--ERROR");
                    receivedChoice = false;
                }
            } while (!receivedChoice);
            if (choice.equals("Yes")) {
                insuranceBet = originalPlayerHand.bet() / 2;
                money -= insuranceBet;
                placedInsuranceBet = true;
//                out.println("INFOMESSAGE--You placed an insurance bet of $" + String.format("%.2f", insuranceBet) + ".");
                out.println("SERVERMESSAGE--INSURANCEBETRESPONSE--PLACED--" + String.format("%.2f", insuranceBet) + "--" + String.format("%.2f", money));
            } else if (choice.equals("No")) {
                out.println("SERVERMESSAGE--INSURANCEBETRESPONSE--NOTPLACED");
            }
        } else {
//            out.println("INFOMESSAGE--You do not have enough money to place an insurance bet.");
            out.println("SERVERMESSAGE--CANNOTINSURANCEBET");
        }
        if (table.numPlayers() > 1) {
//            out.println("INFOMESSAGE--Waiting for other players to place their insurance bets.");
            out.println("SERVERMESSAGE--WAITING--INSURANCEBET");
        }
    }

    /**
     * Performs the player's turn on a given hand by asking if the
     * player wants to split pairs, double down, and hit or stand.
     *
     * @param hand Hand to play
     */

    void takeTurn(BlackjackHand hand) {
        if (hand == originalPlayerHand) {
            out.println("SERVERMESSAGE--TAKETURN--" + String.format("%.2f", money));
            out.println("SERVERMESSAGE--NEWHAND--" + playerHands.indexOf(hand));
            out.println("SERVERMESSAGE--NEWTURNCARD--" + playerHands.indexOf(hand) + "--" + hand.getCard(0));
            out.println("SERVERMESSAGE--NEWTURNCARD--" + playerHands.indexOf(hand) + "--" + hand.getCard(1));
            out.println("SERVERMESSAGE--HANDBET--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", hand.bet()));
            out.println("SERVERMESSAGE--NEWTURNDEALERCARD--" + table.dealerShownCard());
            out.println("SERVERMESSAGE--NEWTURNDEALERCARD--back");
        }
        if (hasBlackjack && table.dealerHasBlackjack()) {
            out.println("SERVERMESSAGE--TURNBLACKJACK--PLAYERANDDEALER");
        } else if (hasBlackjack && !table.dealerHasBlackjack()) {
            out.println("SERVERMESSAGE--TURNBLACKJACK--PLAYER");
        } else if (!hasBlackjack && table.dealerHasBlackjack()) {
            out.println("SERVERMESSAGE--TURNBLACKJACK--DEALER");
        }
        if (!hasBlackjack && !table.dealerHasBlackjack() && hand.getCard(0).rank() == hand.getCard(1).rank()) {
            if (money >= hand.bet()) {
                receivedChoice = false;
                do {
//                    out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
                    out.println("SERVERMESSAGE--HANDVALUE--" + playerHands.indexOf(hand) + "--" + hand.blackjackValue());
//                    out.println("REPLYMESSAGE--Would you like to split pairs? [Y/n]");
                    out.println("SERVERMESSAGE--GETSPLITPAIRS--" + playerHands.indexOf(hand));
                    getChoice();
                    if (!choice.equals("Yes") && !choice.equals("No")) {
//                        out.println("INFOMESSAGE--Please enter either 'Y' or 'N'.");
                        out.println("SERVERMESSAGE--SPLITPAIRSRESPONSE--ERROR--" + playerHands.indexOf(hand));
                        receivedChoice = false;
                    }
                } while (!receivedChoice);
                if (choice.equals("Yes")) {
                    splitPairs(hand);
                }
            } else {
//                out.println("INFOMESSAGE--You do not have enough money to split pairs.");
                out.println("SERVERMESSAGE--CANNOTSPLITPAIRS--" + playerHands.indexOf(hand));
            }
        }
        if (!hasBlackjack && !table.dealerHasBlackjack() && !hand.splitPairs() && ((hand.blackjackValue() >= MINIMUM_DOUBLE_DOWN_SOFT_VALUE && hand.blackjackValue() <= MAXIMUM_DOUBLE_DOWN_SOFT_VALUE) || (hand.isSoft() && hand.blackjackValue() >= MINIMUM_DOUBLE_DOWN_VALUE && hand.blackjackValue() <= MAXIMUM_DOUBLE_DOWN_VALUE))) {
            if (money >= hand.bet()) {
                receivedChoice = false;
                do {
//                    out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
                    out.println("SERVERMESSAGE--HANDVALUE--" + playerHands.indexOf(hand) + "--" + hand.blackjackValue());
//                    out.println("REPLYMESSAGE--Would you like to double down? [Y/n]");
                    out.println("SERVERMESSAGE--GETDOUBLEDOWN--" + playerHands.indexOf(hand));
                    getChoice();
                    if (!choice.equals("Yes") && !choice.equals("No")) {
//                        out.println("INFOMESSAGE--Please enter either 'Y' or 'N'.");
                        out.println("SERVERMESSAGE--DOUBLEDOWNRESPONSE--ERROR--" + playerHands.indexOf(hand));
                        receivedChoice = false;
                    }
                } while (!receivedChoice);
                if (choice.equals("Yes")) {
                    hand.setDoubleDown();
                    money -= hand.bet();
                    hand.placeBet(hand.bet() * 2);
                    Card newCard = table.dealCard();
                    hand.addDoubleDownCard(newCard);
//                    out.println("INFOMESSAGE--Your bet on this hand has been doubled. You were given a card face down.");
                    out.println("SERVERMESSAGE--HANDBET--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", hand.bet()));
                    out.println("SERVERMESSAGE--NEWTURNCARD--" + playerHands.indexOf(hand) + "--back");
                    out.println("SERVERMESSAGE--DOUBLEDOWNRESPONSE--SUCCESS--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", money));
                }
            } else {
//                out.println("INFOMESSAGE--You do not have enough money to double down.");
                out.println("SERVERMESSAGE--CANNOTDOUBLEDOWN--" + playerHands.indexOf(hand));
            }
        }
        if (!hasBlackjack && !table.dealerHasBlackjack() && !hand.splitPairs() && !hand.doubleDown()) {
            do {
                receivedChoice = false;
                do {
//                    out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
                    out.println("SERVERMESSAGE--HANDVALUE--" + playerHands.indexOf(hand) + "--" + hand.blackjackValue());
//                    out.println("REPLYMESSAGE--Would you like to hit or stand? [H/s]");
                    out.println("SERVERMESSAGE--GETHITSTAND--" + playerHands.indexOf(hand));
                    getChoice();
                    if (!choice.equals("Hit") && !choice.equals("Stand")) {
//                        out.println("INFOMESSAGE--Please enter either 'H' or 'S'.");
                        out.println("SERVERMESSAGE--HITSTANDRESPONSE--ERROR--" + playerHands.indexOf(hand));
                        receivedChoice = false;
                    }
                } while (!receivedChoice);
                if (choice.equals("Hit")) {
                    Card newCard = table.dealCard();
                    hand.addCard(newCard);
//                    out.println("INFOMESSAGE--You got the " + newCard + ".");
                    out.println("SERVERMESSAGE--NEWTURNCARD--" + playerHands.indexOf(hand) + "--" + newCard);
                }
            } while (choice.equals("Hit") && hand.blackjackValue() <= MAXIMUM_SCORE);
//            out.println("INFOMESSAGE--Final Hand Total: " + hand.blackjackValue());
            out.println("SERVERMESSAGE--HANDVALUE--" + playerHands.indexOf(hand) + "--" + hand.blackjackValue());
            if (hand.blackjackValue() > MAXIMUM_SCORE) {
//                out.println("INFOMESSAGE--You busted.");
                out.println("SERVERMESSAGE--BUST--" + playerHands.indexOf(hand));
            }
        }
        if (table.numPlayers() > 1 && !hasBlackjack && !table.dealerHasBlackjack() && hand == playerHands.get(playerHands.size() - 1)) {
//            out.println("INFOMESSAGE--Waiting for other players to take their turns.");
            out.println("SERVERMESSAGE--WAITING--AFTERTURN");
        }
    }

    /**
     * Splits a given hand.
     *
     * @param hand Hand to split
     */

    private void splitPairs(BlackjackHand hand) {
        hand.setSplitPairs();
        money -= hand.bet();
        out.println("SERVERMESSAGE--SPLITPAIRSRESPONSE--SUCCESS--" + String.format("%.2f", money));
        BlackjackHand firstHand = new BlackjackHand();
        BlackjackHand secondHand = new BlackjackHand();
        out.println("SERVERMESSAGE--NEWHAND--" + playerHands.indexOf(hand));
        playerHands.add(playerHands.indexOf(hand), secondHand);
        out.println("SERVERMESSAGE--NEWHAND--" + playerHands.indexOf(secondHand));
        playerHands.add(playerHands.indexOf(secondHand), firstHand);
        out.println("SERVERMESSAGE--REMOVEHAND--" + playerHands.indexOf(hand));
        playerHands.remove(hand);
        firstHand.addCard(hand.getCard(0));
        out.println("SERVERMESSAGE--NEWTURNCARD--" + playerHands.indexOf(firstHand) + "--" + firstHand.getCard(0));
        secondHand.addCard(hand.getCard(1));
        out.println("SERVERMESSAGE--NEWTURNCARD--" + playerHands.indexOf(secondHand) + "--" + secondHand.getCard(0));
        firstHand.placeBet(hand.bet());
        out.println("SERVERMESSAGE--HANDBET--" + playerHands.indexOf(firstHand) + "--" + String.format("%.2f", firstHand.bet()));
        secondHand.placeBet(hand.bet());
        out.println("SERVERMESSAGE--HANDBET--" + playerHands.indexOf(secondHand) + "--" + String.format("%.2f", secondHand.bet()));
        if (firstHand.getCard(0).rank() == Card.Rank.ACE && secondHand.getCard(0).rank() == Card.Rank.ACE) {
            Card newCard = table.dealCard();
            firstHand.addCard(newCard);
//            out.println("INFOMESSAGE--You got the " + newCard + " on the first hand.");
            out.println("SERVERMESSAGE--NEWTURNCARD--" + playerHands.indexOf(firstHand) + "--" + newCard);
//            out.println("INFOMESSAGE--Final First Hand Total: " + firstHand.blackjackValue());
            out.println("SERVERMESSAGE--HANDVALUE--" + playerHands.indexOf(firstHand) + "--" + firstHand.blackjackValue());
            newCard = table.dealCard();
            secondHand.addCard(newCard);
//            out.println("INFOMESSAGE--You got the " + newCard + " on the second hand.");
            out.println("SERVERMESSAGE--NEWTURNCARD--" + playerHands.indexOf(secondHand) + "--" + newCard);
//            out.println("INFOMESSAGE--Final Second Hand Total: " + secondHand.blackjackValue());
            out.println("SERVERMESSAGE--HANDVALUE--" + playerHands.indexOf(secondHand) + "--" + secondHand.blackjackValue());
            if (table.numPlayers() > 1 && secondHand == playerHands.get(playerHands.size() - 1)) {
//                out.println("INFOMESSAGE--Waiting for other players to take their turns.");
                out.println("SERVERMESSAGE--WAITING--AFTERTURN");
            }
        } else {
            Card newCard = table.dealCard();
            firstHand.addCard(newCard);
//            out.println("INFOMESSAGE--You got the " + newCard + " on the first hand.");
            out.println("SERVERMESSAGE--NEWTURNCARD--" + playerHands.indexOf(firstHand) + "--" + newCard);
//            out.println("INFOMESSAGE--First Hand Total: " + firstHand.blackjackValue());
            out.println("SERVERMESSAGE--HANDVALUE--" + playerHands.indexOf(firstHand) + "--" + firstHand.blackjackValue());
            newCard = table.dealCard();
            secondHand.addCard(newCard);
//            out.println("INFOMESSAGE--You got the " + newCard + " on the second hand.");
            out.println("SERVERMESSAGE--NEWTURNCARD--" + playerHands.indexOf(secondHand) + "--" + newCard);
//            out.println("INFOMESSAGE--Second Hand Total: " + secondHand.blackjackValue());
            out.println("SERVERMESSAGE--HANDVALUE--" + playerHands.indexOf(secondHand) + "--" + secondHand.blackjackValue());
            takeTurn(firstHand);
            takeTurn(secondHand);
        }
    }

    /**
     * Sends the dealer's cards to the player.
     */

    private void sendDealerCards() {
        out.println("SERVERMESSAGE--SENDRESULT");
//        out.println("INFOMESSAGE--Dealer's Cards:");
        out.println("SERVERMESSAGE--REMOVEDEALERFACEDOWNCARD");
        for (int i = 1; i < table.dealerHand().size(); i++) {
//            out.println("INFOMESSAGE--" + table.dealerHand().getCard(i));
            out.println("SERVERMESSAGE--NEWTURNDEALERCARD--" + table.dealerHand().getCard(i));
        }
        out.println("SERVERMESSAGE--DEALERHANDVALUE--" + table.dealerHand().blackjackValue());
    }

    /**
     * Sends the final results to the player for a given hand including
     * the player and dealer hand values, whether or not the player or
     * dealer busted, and who won.
     *
     * @param hand Hand to send results for
     */

    private void sendResult(BlackjackHand hand) {
//        out.println("INFOMESSAGE--Dealer's Total: " + table.dealerHand().blackjackValue());
        if (hand.doubleDown()) {
//            out.println("INFOMESSAGE--Your face down card is the " + hand.doubleDownCard() + ".");
            out.println("SERVERMESSAGE--REMOVEDOUBLEDOWNFACEDOWNCARD--" + playerHands.indexOf(hand));
            out.println("SERVERMESSAGE--NEWTURNCARD--" + playerHands.indexOf(hand) + "--" + hand.doubleDownCard());
            out.println("SERVERMESSAGE--REVEALDOUBLEDOWNCARD--" + playerHands.indexOf(hand) + "--" + hand.doubleDownCard());
        }
//        out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
        out.println("SERVERMESSAGE--HANDVALUE--" + playerHands.indexOf(hand) + "--" + hand.blackjackValue());
        if (!hasBlackjack && !table.dealerHasBlackjack()) {
            if (hand.blackjackValue() > MAXIMUM_SCORE && table.dealerHand().blackjackValue() > MAXIMUM_SCORE) {
//                out.println("INFOMESSAGE--You and the dealer both busted. It's a tie!");
                money += hand.bet();
                out.println("SERVERMESSAGE--ROUNDRESULT--BUST--TIE--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", money));
            } else if (hand.blackjackValue() > MAXIMUM_SCORE) {
//                out.println("INFOMESSAGE--You busted. The dealer wins!");
                out.println("SERVERMESSAGE--ROUNDRESULT--BUST--DEALER--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", money));
            } else if (table.dealerHand().blackjackValue() > MAXIMUM_SCORE) {
//                out.println("INFOMESSAGE--The dealer busted. You win!");
                money += hand.bet() * 2;
//                out.println("INFOMESSAGE--You won $" + String.format("%.2f", hand.bet()) + ".");
                out.println("SERVERMESSAGE--ROUNDRESULT--BUST--PLAYER--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", money));
            } else {
                if (hand.blackjackValue() == table.dealerHand().blackjackValue()) {
//                    out.println("INFOMESSAGE--It's a tie!");
                    money += hand.bet();
                    out.println("SERVERMESSAGE--ROUNDRESULT--NORMAL--TIE--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", money));
                } else if (hand.blackjackValue() < table.dealerHand().blackjackValue()) {
//                    out.println("INFOMESSAGE--The dealer wins!");
                    out.println("SERVERMESSAGE--ROUNDRESULT--NORMAL--DEALER--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", money));
                } else if (hand.blackjackValue() > table.dealerHand().blackjackValue()){
//                    out.println("INFOMESSAGE--You win!");
                    money += hand.bet() * 2;
//                    out.println("INFOMESSAGE--You won $" + String.format("%.2f", hand.bet()) + ".");
                    out.println("SERVERMESSAGE--ROUNDRESULT--NORMAL--PLAYER--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", money));
                }
            }
        } else {
            if (hasBlackjack && table.dealerHasBlackjack()) {
//                out.println("INFOMESSAGE--You and the dealer both have Blackjack. It's a tie!");
                money += hand.bet();
                out.println("SERVERMESSAGE--ROUNDRESULT--BLACKJACK--TIE--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", money));
            } else if (!hasBlackjack && table.dealerHasBlackjack()) {
//                out.println("INFOMESSAGE--The dealer has Blackjack. The dealer wins!");
                out.println("SERVERMESSAGE--ROUNDRESULT--BLACKJACK--DEALER--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", money));
            } else if (hasBlackjack && !table.dealerHasBlackjack()) {
//                out.println("INFOMESSAGE--You have Blackjack. You win!");
                money += (hand.bet() + (hand.bet() * (BLACKJACK_PAYOUT_MULTIPLIER)));
//                out.println("INFOMESSAGE--You won $" + String.format("%.2f", hand.bet() * (3.0 / 2.0)) + ".");
                out.println("SERVERMESSAGE--ROUNDRESULT--BLACKJACK--PLAYER--" + playerHands.indexOf(hand) + "--" + String.format("%.2f", money));
            }
        }
    }

    /**
     * Determines whether or not the player wants to keep playing.
     */

    private void getContinuePlaying() {
        if (money >= table.minimumBet()) {
            receivedChoice = false;
            do {
//                out.println("REPLYMESSAGE--Would you like to keep playing? [Y/n]");
                out.println("SERVERMESSAGE--GETCONTINUEPLAYING--" + String.format("%.2f", money));
                getChoice();
                if (!choice.equals("Yes") && !choice.equals("No")) {
//                    out.println("INFOMESSAGE--Please enter either 'Y' or 'N'.");
                    out.println("SERVERMESSAGE--CONTINUEPLAYINGRESPONSE--ERROR");
                    receivedChoice = false;
                }
            } while (!receivedChoice);
            if (choice.equals("Yes")) {
                continuePlaying = true;
                out.println("SERVERMESSAGE--CONTINUEPLAYINGRESPONSE--CONTINUE");
            } else {
                table.removePlayer(this);
            }
        } else {
//            out.println("INFOMESSAGE--You do not have enough money to make the minimum bet.");
            out.println("SERVERMESSAGE--CANNOTCONTINUEPLAYING--" + String.format("%.2f", money));
            table.removePlayer(this);
        }
        table.continuePlayingLatchCountDown();
    }

    /**
     * Gets the player's choice.
     */

    private void getChoice() {
        try {
            while (!receivedChoice) {
                if ((clientMessage = in.readLine()) != null) {
                    choice = clientMessage;
                    receivedChoice = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the player hand.
     *
     * @return the player hand
     */

    public BlackjackHand originalPlayerHand() {
        return originalPlayerHand;
    }

    /**
     * Decrements the start latch.
     */

    public void startLatchCountDown() {
        startLatch.countDown();
    }

    /**
     * Decrements the bet latch.
     */

    public void betLatchCountDown() {
        betLatch.countDown();
    }

    /**
     * Decrements the insurance bet latch.
     */

    public void insuranceBetLatchCountDown() {
        insuranceBetLatch.countDown();
    }

    /**
     * Decrements the deal latch.
     */

    public void dealLatchCountDown() {
        dealLatch.countDown();
    }

    /**
     * Decrements the dealer turn latch.
     */

    public void dealerTurnLatchCountDown() {
        dealerTurnLatch.countDown();
    }
}