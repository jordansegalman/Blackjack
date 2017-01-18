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
    private String playAgain;                                           // choice to play again or not
    private boolean receivedPlayAgain = false;                          // true if choice to play again made, false if not
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
            this.in = new BufferedReader(isr);
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Player thread run method.
     */

    @Override
    public void run() {
//        this.out.println("INFOMESSAGE--Welcome to Blackjack!");
        this.out.println("SERVERMESSAGE--WELCOME");
        do {
            this.playBlackjack();
        } while (this.continuePlaying);
//        this.out.println("INFOMESSAGE--You leave with $" + String.format("%.2f", this.money) + ".");
//        this.out.println("GAMEOVERMESSAGE--Thanks for playing!");
        this.out.println("SERVERMESSAGE--GAMEOVER");
    }

    /**
     * Plays Blackjack.
     */

    private void playBlackjack() {
        this.setupPlayer();
        try {
            this.startLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.getBet();
        try {
            this.betLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            this.dealLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.sendRoundInformation();
        try {
            this.dealerTurnLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.sendDealerCards();
        for (BlackjackHand hand : this.playerHands) {
            this.sendResult(hand);
        }
        this.getContinuePlaying();
    }

    /**
     * Sets the player up for a new round of Blackjack.
     */

    private void setupPlayer() {
        this.playerHands.clear();
        this.originalPlayerHand = new BlackjackHand();
        this.playerHands.add(this.originalPlayerHand);
        this.receivedBet = false;
        this.hasBlackjack = false;
        this.receivedChoice = false;
        this.placedInsuranceBet = false;
        this.receivedPlayAgain = false;
        this.continuePlaying = false;
        this.startLatch = new CountDownLatch(1);
        this.betLatch = new CountDownLatch(1);
        this.insuranceBetLatch = new CountDownLatch(1);
        this.dealLatch = new CountDownLatch(1);
        this.dealerTurnLatch = new CountDownLatch(1);
//        this.out.println("INFOMESSAGE--Waiting for other players to join.");
        this.out.println("SERVERMESSAGE--WAITING--WELCOME");
    }

    /**
     * Gets the player's bet.
     */

    private void getBet() {
        do {
            boolean betNotNumeric = false;  // true if bet is not a positive integer, false if it is
//            this.out.println("INFOMESSAGE--You have $" + String.format("%.2f", this.money) + ".");
//            this.out.println("REPLYMESSAGE--The minimum bet is $" + String.format("%.2f", this.table.minimumBet()) + ". How much would you like to bet?");
            this.out.println("SERVERMESSAGE--GETBET--" + String.format("%.2f", this.money) + "--" + String.format("%.2f", this.table.minimumBet()));
            try {
                while (!this.receivedBet) {
                    if ((this.clientMessage = this.in.readLine()) != null) {
                        try {
                            int bet = Integer.parseInt(this.clientMessage);
                            this.originalPlayerHand.placeBet(bet);
                            this.receivedBet = true;
                        } catch (NumberFormatException e) {
                            betNotNumeric = true;
                            this.receivedBet = true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (betNotNumeric) {
//                this.out.println("INFOMESSAGE--Your bet must be a positive whole number.");
                this.out.println("SERVERMESSAGE--BETRESPONSE--INVALID");
                this.receivedBet = false;
            } else if (this.originalPlayerHand.bet() > this.money) {
//                this.out.println("INFOMESSAGE--You cannot bet more money than you have.");
                this.out.println("SERVERMESSAGE--BETRESPONSE--TOOMUCH");
                this.receivedBet = false;
            } else if (this.originalPlayerHand.bet() < this.table.minimumBet()) {
//                this.out.println("INFOMESSAGE--You must bet at least the minimum amount.");
                this.out.println("SERVERMESSAGE--BETRESPONSE--MINIMUM");

                this.receivedBet = false;
            }
        } while (!this.receivedBet);
        this.money -= this.originalPlayerHand.bet();
        this.table.placedBetsLatchCountDown();
        this.out.println("SERVERMESSAGE--BETRESPONSE--SUCCESS--" + String.format("%.2f", this.money));
        if (this.table.numPlayers() > 1) {
//            this.out.println("INFOMESSAGE--Waiting for other players to place their bets.");
            this.out.println("SERVERMESSAGE--WAITING--BET");
        }
    }

    /**
     * Sends initial round information to the player including the
     * player's first two cards, the card the dealer is showing,
     * and whether or not the player or dealer has Blackjack.
     */

    private void sendRoundInformation() {
        this.out.println("SERVERMESSAGE--NEWROUND--" + String.format("%.2f", this.money));
//        this.out.println("INFOMESSAGE--Your Cards:");
        for (int i = 0; i < this.originalPlayerHand.size(); i++) {
//            this.out.println("INFOMESSAGE--" + this.originalPlayerHand.getCard(i));
            this.out.println("SERVERMESSAGE--NEWPLAYERCARD--" + this.originalPlayerHand.getCard(i));
        }
        this.out.println("SERVERMESSAGE--ORIGINALHANDBET--" + String.format("%.2f", this.originalPlayerHand.bet()));
        if (this.originalPlayerHand.blackjackValue() == 21) {
//            this.out.println("INFOMESSAGE--You have Blackjack.");
            this.out.println("SERVERMESSAGE--BLACKJACK--PLAYER");
            this.hasBlackjack = true;
        }
//        this.out.println("INFOMESSAGE--The dealer is showing the " + this.table.dealerShownCard() + ".");
        this.out.println("SERVERMESSAGE--NEWDEALERCARD--" + this.table.dealerShownCard());
        this.out.println("SERVERMESSAGE--NEWDEALERCARD--FACE-DOWN CARD");
        if (this.table.dealerShownCard().rank() == Card.Rank.ACE) {
            this.getInsuranceBet();
        }
        this.table.placedInsuranceBetsLatchCountDown();
        try {
            this.insuranceBetLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.originalPlayerHand.blackjackValue() == 21 && this.table.dealerHand().blackjackValue() == 21) {
//            this.out.println("INFOMESSAGE--You and the dealer both have Blackjack.");
            this.out.println("SERVERMESSAGE--BLACKJACK--PLAYERANDDEALER");
            this.hasBlackjack = true;
            if (this.placedInsuranceBet) {
                this.money += (this.insuranceBet + (this.insuranceBet * 2));
//                this.out.println("INFOMESSAGE--You won $" + String.format("%.2f", this.insuranceBet * 2) + " from your insurance bet.");
                this.out.println("SERVERMESSAGE--INSURANCEBETWINNINGS--" + String.format("%.2f", this.insuranceBet * 2) + "--" + String.format("%.2f", this.money));
            }
        } else if (this.table.dealerHand().blackjackValue() == 21) {
//            this.out.println("INFOMESSAGE--The dealer has Blackjack.");
            this.out.println("SERVERMESSAGE--BLACKJACK--DEALER");
            if (this.placedInsuranceBet) {
                this.money += (this.insuranceBet + (this.insuranceBet * 2));
//                this.out.println("INFOMESSAGE--You won $" + String.format("%.2f", this.insuranceBet * 2) + " from your insurance bet.");
                this.out.println("SERVERMESSAGE--INSURANCEBETWON--" + String.format("%.2f", this.insuranceBet * 2) + "--" + String.format("%.2f", this.money));
            }
        } else if (this.table.dealerShownCard().rank() == Card.Rank.ACE && this.table.dealerHand().blackjackValue() != 21){
//            this.out.println("INFOMESSAGE--The dealer does not have Blackjack.");
            this.out.println("SERVERMESSAGE--BLACKJACK--DEALERNOBLACKJACK");
            if (this.placedInsuranceBet) {
                this.out.println("SERVERMESSAGE--INSURANCEBETLOST");
            }
        }
        if (this.table.dealerShownCard().rank() == Card.Rank.ACE) {
            this.out.println("SERVERMESSAGE--INSURANCEBETDONE");
        }
        this.table.turnLatchCountDown();
        if (this.table.numPlayers() > 1) {
//            this.out.println("INFOMESSAGE--Waiting for other players to take their turns.");
            this.out.println("SERVERMESSAGE--WAITING--BEFORETURN");
        }
    }

    /**
     * Asks the player if they want to place an insurance bet.
     */

    private void getInsuranceBet() {
        if (this.money >= this.originalPlayerHand.bet() / 2) {
            this.receivedChoice = false;
            do {
//                this.out.println("REPLYMESSAGE--Would you like to place an insurance bet? [Y/n]");
                this.out.println("SERVERMESSAGE--GETINSURANCEBET");
                this.getChoice();
                if (!this.choice.equals("Yes") && !this.choice.equals("No")) {
//                    this.out.println("INFOMESSAGE--Please enter either 'Yes' or 'No'.");
                    this.out.println("SERVERMESSAGE--INSURANCEBETRESPONSE--ERROR");
                    this.receivedChoice = false;
                }
            } while (!this.receivedChoice);
            if (this.choice.equals("Yes")) {
                this.insuranceBet = this.originalPlayerHand.bet() / 2;
                this.money -= this.insuranceBet;
                this.placedInsuranceBet = true;
//                this.out.println("INFOMESSAGE--You placed an insurance bet of $" + String.format("%.2f", this.insuranceBet) + ".");
                this.out.println("SERVERMESSAGE--INSURANCEBETRESPONSE--PLACED--" + String.format("%.2f", this.insuranceBet) + "--" + String.format("%.2f", this.money));
            } else if (this.choice.equals("No")) {
                this.out.println("SERVERMESSAGE--INSURANCEBETRESPONSE--NOTPLACED");
            }
        } else {
//            this.out.println("INFOMESSAGE--You do not have enough money to place an insurance bet.");
        }
        if (this.table.numPlayers() > 1) {
//            this.out.println("INFOMESSAGE--Waiting for other players to place their insurance bets.");
            this.out.println("SERVERMESSAGE--WAITING--INSURANCEBET");
        }
    }

    /**
     * Performs the player's turn on a given hand by asking if the
     * player wants to split pairs, double down, and hit or stand.
     *
     * @param hand Hand to play
     */

    void takeTurn(BlackjackHand hand) {
        if (hand == this.originalPlayerHand) {
            this.out.println("SERVERMESSAGE--TAKETURN--" + String.format("%.2f", this.money));
            this.out.println("SERVERMESSAGE--NEWHAND--0--" + hand.getCard(0) + "--" + hand.getCard(1));
            this.out.println("SERVERMESSAGE--HANDBET--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", hand.bet()));
        }
        if (this.hasBlackjack && this.table.dealerHasBlackjack()) {
            this.out.println("SERVERMESSAGE--TURNBLACKJACK--PLAYERANDDEALER");
        } else if (this.hasBlackjack && !this.table.dealerHasBlackjack()) {
            this.out.println("SERVERMESSAGE--TURNBLACKJACK--PLAYER");
        } else if (!this.hasBlackjack && this.table.dealerHasBlackjack()) {
            this.out.println("SERVERMESSAGE--TURNBLACKJACK--DEALER");
        }
        if (!this.hasBlackjack && !this.table.dealerHasBlackjack() && hand.getCard(0).rank() == hand.getCard(1).rank()) {
            if (this.money >= hand.bet()) {
                this.receivedChoice = false;
                do {
//                    this.out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
                    this.out.println("SERVERMESSAGE--HANDVALUE--" + this.playerHands.indexOf(hand) + "--" + hand.blackjackValue());
//                    this.out.println("REPLYMESSAGE--Would you like to split pairs? [Y/n]");
                    this.out.println("SERVERMESSAGE--GETSPLITPAIRS--" + this.playerHands.indexOf(hand));
                    this.getChoice();
                    if (!this.choice.equals("Yes") && !this.choice.equals("No")) {
//                        this.out.println("INFOMESSAGE--Please enter either 'Y' or 'N'.");
                        this.out.println("SERVERMESSAGE--SPLITPAIRSRESPONSE--ERROR--" + this.playerHands.indexOf(hand));
                        this.receivedChoice = false;
                    }
                } while (!this.receivedChoice);
                if (this.choice.equals("Yes")) {
                    this.splitPairs(hand);
                }
            } else {
//                this.out.println("INFOMESSAGE--You do not have enough money to split pairs.");
                this.out.println("SERVERMESSAGE--CANNOTSPLITPAIRS--" + this.playerHands.indexOf(hand));
            }
        }
        if (!this.hasBlackjack && !this.table.dealerHasBlackjack() && !hand.splitPairs() && ((hand.blackjackValue() >= 9 && hand.blackjackValue() <= 11) || (hand.isSoft() && hand.blackjackValue() >= 19 && hand.blackjackValue() <= 21))) {
            if (this.money >= hand.bet()) {
                this.receivedChoice = false;
                do {
//                    this.out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
                    this.out.println("SERVERMESSAGE--HANDVALUE--" + this.playerHands.indexOf(hand) + "--" + hand.blackjackValue());
//                    this.out.println("REPLYMESSAGE--Would you like to double down? [Y/n]");
                    this.out.println("SERVERMESSAGE--GETDOUBLEDOWN--" + this.playerHands.indexOf(hand));
                    this.getChoice();
                    if (!this.choice.equals("Yes") && !this.choice.equals("No")) {
//                        this.out.println("INFOMESSAGE--Please enter either 'Y' or 'N'.");
                        this.out.println("SERVERMESSAGE--DOUBLEDOWNRESPONSE--ERROR--" + this.playerHands.indexOf(hand));
                        this.receivedChoice = false;
                    }
                } while (!this.receivedChoice);
                if (this.choice.equals("Yes")) {
                    hand.setDoubleDown();
                    this.money -= hand.bet();
                    hand.placeBet(hand.bet() * 2);
                    Card newCard = this.table.dealCard();
                    hand.addDoubleDownCard(newCard);
//                    this.out.println("INFOMESSAGE--Your bet on this hand has been doubled. You were given a card face down.");
                    this.out.println("SERVERMESSAGE--HANDBET--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", hand.bet()));
                    this.out.println("SERVERMESSAGE--NEWCARD--" + this.playerHands.indexOf(hand) + "--FACE-DOWN CARD");
                    this.out.println("SERVERMESSAGE--DOUBLEDOWNRESPONSE--SUCCESS--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", this.money));
                }
            } else {
//                this.out.println("INFOMESSAGE--You do not have enough money to double down.");
                this.out.println("SERVERMESSAGE--CANNOTDOUBLEDOWN--" + this.playerHands.indexOf(hand));
            }
        }
        if (!this.hasBlackjack && !this.table.dealerHasBlackjack() && !hand.splitPairs() && !hand.doubleDown()) {
            do {
                this.receivedChoice = false;
                do {
//                    this.out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
                    this.out.println("SERVERMESSAGE--HANDVALUE--" + this.playerHands.indexOf(hand) + "--" + hand.blackjackValue());
//                    this.out.println("REPLYMESSAGE--Would you like to hit or stand? [H/s]");
                    this.out.println("SERVERMESSAGE--GETHITSTAND--" + this.playerHands.indexOf(hand));
                    this.getChoice();
                    if (!this.choice.equals("Hit") && !this.choice.equals("Stand")) {
//                        this.out.println("INFOMESSAGE--Please enter either 'H' or 'S'.");
                        this.out.println("SERVERMESSAGE--HITSTANDRESPONSE--ERROR--" + this.playerHands.indexOf(hand));
                        this.receivedChoice = false;
                    }
                } while (!this.receivedChoice);
                if (this.choice.equals("Hit")) {
                    Card newCard = this.table.dealCard();
                    hand.addCard(newCard);
//                    this.out.println("INFOMESSAGE--You got the " + newCard + ".");
                    this.out.println("SERVERMESSAGE--NEWCARD--" + this.playerHands.indexOf(hand) + "--" + newCard);
                }
            } while (this.choice.equals("Hit") && hand.blackjackValue() <= 21);
//            this.out.println("INFOMESSAGE--Final Hand Total: " + hand.blackjackValue());
            this.out.println("SERVERMESSAGE--HANDVALUE--" + this.playerHands.indexOf(hand) + "--" + hand.blackjackValue());
            if (hand.blackjackValue() > 21) {
//                this.out.println("INFOMESSAGE--You busted.");
                this.out.println("SERVERMESSAGE--BUST--" + this.playerHands.indexOf(hand));
            }
        }
        if (this.table.numPlayers() > 1 && !this.hasBlackjack && !this.table.dealerHasBlackjack() && hand == this.playerHands.get(this.playerHands.size() - 1)) {
//            this.out.println("INFOMESSAGE--Waiting for other players to take their turns.");
            this.out.println("SERVERMESSAGE--WAITING--AFTERTURN");
        }
    }

    /**
     * Splits a given hand.
     *
     * @param hand Hand to split
     */

    private void splitPairs(BlackjackHand hand) {
        hand.setSplitPairs();
        this.money -= hand.bet();
        this.out.println("SERVERMESSAGE--SPLITPAIRSRESPONSE--SUCCESS--" + String.format("%.2f", this.money));
        BlackjackHand firstHand = new BlackjackHand();
        BlackjackHand secondHand = new BlackjackHand();
        this.out.println("SERVERMESSAGE--NEWEMPTYHAND--" + this.playerHands.indexOf(hand));
        this.playerHands.add(this.playerHands.indexOf(hand), secondHand);
        this.out.println("SERVERMESSAGE--NEWEMPTYHAND--" + this.playerHands.indexOf(secondHand));
        this.playerHands.add(this.playerHands.indexOf(secondHand), firstHand);
        this.out.println("SERVERMESSAGE--REMOVEHAND--" + this.playerHands.indexOf(hand));
        this.playerHands.remove(hand);
        firstHand.addCard(hand.getCard(0));
        this.out.println("SERVERMESSAGE--NEWCARD--" + this.playerHands.indexOf(firstHand) + "--" + firstHand.getCard(0));
        secondHand.addCard(hand.getCard(1));
        this.out.println("SERVERMESSAGE--NEWCARD--" + this.playerHands.indexOf(secondHand) + "--" + secondHand.getCard(0));
        firstHand.placeBet(hand.bet());
        this.out.println("SERVERMESSAGE--HANDBET--" + this.playerHands.indexOf(firstHand) + "--" + String.format("%.2f", firstHand.bet()));
        secondHand.placeBet(hand.bet());
        this.out.println("SERVERMESSAGE--HANDBET--" + this.playerHands.indexOf(secondHand) + "--" + String.format("%.2f", secondHand.bet()));
        if (firstHand.getCard(0).rank() == Card.Rank.ACE && secondHand.getCard(0).rank() == Card.Rank.ACE) {
            Card newCard = this.table.dealCard();
            firstHand.addCard(newCard);
//            this.out.println("INFOMESSAGE--You got the " + newCard + " on the first hand.");
            this.out.println("SERVERMESSAGE--NEWCARD--" + this.playerHands.indexOf(firstHand) + "--" + newCard);
//            this.out.println("INFOMESSAGE--Final First Hand Total: " + firstHand.blackjackValue());
            this.out.println("SERVERMESSAGE--HANDVALUE--" + this.playerHands.indexOf(firstHand) + "--" + firstHand.blackjackValue());
            newCard = this.table.dealCard();
            secondHand.addCard(newCard);
//            this.out.println("INFOMESSAGE--You got the " + newCard + " on the second hand.");
            this.out.println("SERVERMESSAGE--NEWCARD--" + this.playerHands.indexOf(secondHand) + "--" + newCard);
//            this.out.println("INFOMESSAGE--Final Second Hand Total: " + secondHand.blackjackValue());
            this.out.println("SERVERMESSAGE--HANDVALUE--" + this.playerHands.indexOf(secondHand) + "--" + secondHand.blackjackValue());
            if (this.table.numPlayers() > 1 && secondHand == this.playerHands.get(this.playerHands.size() - 1)) {
//                this.out.println("INFOMESSAGE--Waiting for other players to take their turns.");
                this.out.println("SERVERMESSAGE--WAITING--AFTERTURN");
            }
        } else {
            Card newCard = this.table.dealCard();
            firstHand.addCard(newCard);
//            this.out.println("INFOMESSAGE--You got the " + newCard + " on the first hand.");
            this.out.println("SERVERMESSAGE--NEWCARD--" + this.playerHands.indexOf(firstHand) + "--" + newCard);
//            this.out.println("INFOMESSAGE--First Hand Total: " + firstHand.blackjackValue());
            this.out.println("SERVERMESSAGE--HANDVALUE--" + this.playerHands.indexOf(firstHand) + "--" + firstHand.blackjackValue());
            newCard = this.table.dealCard();
            secondHand.addCard(newCard);
//            this.out.println("INFOMESSAGE--You got the " + newCard + " on the second hand.");
            this.out.println("SERVERMESSAGE--NEWCARD--" + this.playerHands.indexOf(secondHand) + "--" + newCard);
//            this.out.println("INFOMESSAGE--Second Hand Total: " + secondHand.blackjackValue());
            this.out.println("SERVERMESSAGE--HANDVALUE--" + this.playerHands.indexOf(secondHand) + "--" + secondHand.blackjackValue());
            this.takeTurn(firstHand);
            this.takeTurn(secondHand);
        }
    }

    /**
     * Sends the dealer's cards to the player.
     */

    private void sendDealerCards() {
        this.out.println("SERVERMESSAGE--SENDRESULT");
//        this.out.println("INFOMESSAGE--Dealer's Cards:");
        this.out.println("SERVERMESSAGE--REMOVEDEALERCARD--1");
        for (int i = 1; i < this.table.dealerHand().size(); i++) {
//            this.out.println("INFOMESSAGE--" + this.table.dealerHand().getCard(i));
            this.out.println("SERVERMESSAGE--NEWDEALERCARD--" + this.table.dealerHand().getCard(i));
        }
        this.out.println("SERVERMESSAGE--DEALERHANDVALUE--" + this.table.dealerHand().blackjackValue());
    }

    /**
     * Sends the final results to the player for a given hand including
     * the player and dealer hand values, whether or not the player or
     * dealer busted, and who won.
     *
     * @param hand Hand to send results for
     */

    private void sendResult(BlackjackHand hand) {
//        this.out.println("INFOMESSAGE--Dealer's Total: " + this.table.dealerHand().blackjackValue());
        if (hand.doubleDown()) {
//            this.out.println("INFOMESSAGE--Your face down card is the " + hand.doubleDownCard() + ".");
            this.out.println("SERVERMESSAGE--REMOVEPLAYERCARD--" + this.playerHands.indexOf(hand) + "--2");
            this.out.println("SERVERMESSAGE--NEWCARD--" + this.playerHands.indexOf(hand) + "--" + hand.doubleDownCard());
            this.out.println("SERVERMESSAGE--REVEALDOUBLEDOWNCARD--" + this.playerHands.indexOf(hand) + "--" + hand.doubleDownCard());
        }
//        this.out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
        this.out.println("SERVERMESSAGE--HANDVALUE--" + this.playerHands.indexOf(hand) + "--" + hand.blackjackValue());
        if (!this.hasBlackjack && !this.table.dealerHasBlackjack()) {
            if (hand.blackjackValue() > 21 && this.table.dealerHand().blackjackValue() > 21) {
//                this.out.println("INFOMESSAGE--You and the dealer both busted. It's a tie!");
                this.money += hand.bet();
                this.out.println("SERVERMESSAGE--ROUNDRESULT--BUST--TIE--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", this.money));
            } else if (hand.blackjackValue() > 21) {
//                this.out.println("INFOMESSAGE--You busted. The dealer wins!");
                this.out.println("SERVERMESSAGE--ROUNDRESULT--BUST--DEALER--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", this.money));
            } else if (this.table.dealerHand().blackjackValue() > 21) {
//                this.out.println("INFOMESSAGE--The dealer busted. You win!");
                this.money += hand.bet() * 2;
//                this.out.println("INFOMESSAGE--You won $" + String.format("%.2f", hand.bet()) + ".");
                this.out.println("SERVERMESSAGE--ROUNDRESULT--BUST--PLAYER--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", this.money));
            } else {
                if (hand.blackjackValue() == this.table.dealerHand().blackjackValue()) {
//                    this.out.println("INFOMESSAGE--It's a tie!");
                    this.money += hand.bet();
                    this.out.println("SERVERMESSAGE--ROUNDRESULT--NORMAL--TIE--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", this.money));
                } else if (hand.blackjackValue() < this.table.dealerHand().blackjackValue()) {
//                    this.out.println("INFOMESSAGE--The dealer wins!");
                    this.out.println("SERVERMESSAGE--ROUNDRESULT--NORMAL--DEALER--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", this.money));
                } else if (hand.blackjackValue() > this.table.dealerHand().blackjackValue()){
//                    this.out.println("INFOMESSAGE--You win!");
                    this.money += hand.bet() * 2;
//                    this.out.println("INFOMESSAGE--You won $" + String.format("%.2f", hand.bet()) + ".");
                    this.out.println("SERVERMESSAGE--ROUNDRESULT--NORMAL--PLAYER--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", this.money));
                }
            }
        } else {
            if (this.hasBlackjack && this.table.dealerHasBlackjack()) {
//                this.out.println("INFOMESSAGE--You and the dealer both have Blackjack. It's a tie!");
                this.money += hand.bet();
                this.out.println("SERVERMESSAGE--ROUNDRESULT--BLACKJACK--TIE--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", this.money));
            } else if (!this.hasBlackjack && this.table.dealerHasBlackjack()) {
//                this.out.println("INFOMESSAGE--The dealer has Blackjack. The dealer wins!");
                this.out.println("SERVERMESSAGE--ROUNDRESULT--BLACKJACK--DEALER--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", this.money));
            } else if (this.hasBlackjack && !this.table.dealerHasBlackjack()) {
//                this.out.println("INFOMESSAGE--You have Blackjack. You win!");
                this.money += (hand.bet() + (hand.bet() * (3.0 / 2.0)));
//                this.out.println("INFOMESSAGE--You won $" + String.format("%.2f", hand.bet() * (3.0 / 2.0)) + ".");
                this.out.println("SERVERMESSAGE--ROUNDRESULT--BLACKJACK--PLAYER--" + this.playerHands.indexOf(hand) + "--" + String.format("%.2f", this.money));
            }
        }
    }

    /**
     * Determines whether or not the player wants to keep playing.
     */

    private void getContinuePlaying() {
        if (this.money >= this.table.minimumBet()) {
            do {
//                this.out.println("REPLYMESSAGE--Would you like to keep playing? [Y/n]");
                this.out.println("SERVERMESSAGE--GETCONTINUEPLAYING--" + String.format("%.2f", this.money));
                try {
                    while (!this.receivedPlayAgain) {
                        if ((this.clientMessage = this.in.readLine()) != null) {
                            this.playAgain = this.clientMessage;
                            this.receivedPlayAgain = true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!this.playAgain.equals("Yes") && !this.playAgain.equals("No")) {
//                    this.out.println("INFOMESSAGE--Please enter either 'Y' or 'N'.");
                    this.out.println("SERVERMESSAGE--CONTINUEPLAYINGRESPONSE--ERROR");
                    this.receivedPlayAgain = false;
                }
            } while (!this.receivedPlayAgain);
            if (this.playAgain.equals("Yes")) {
                this.continuePlaying = true;
            } else {
                this.table.removePlayer(this);
            }
        } else {
//            this.out.println("INFOMESSAGE--You do not have enough money to make the minimum bet.");
            this.out.println("SERVERMESSAGE--CANNOTCONTINUEPLAYING");
            this.table.removePlayer(this);
        }
        this.table.continuePlayingLatchCountDown();
    }

    /**
     * Gets the player's choice.
     */

    private void getChoice() {
        try {
            while (!this.receivedChoice) {
                if ((this.clientMessage = this.in.readLine()) != null) {
                    this.choice = this.clientMessage;
                    this.receivedChoice = true;
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
        return this.originalPlayerHand;
    }

    /**
     * Decrements the start latch.
     */

    public void startLatchCountDown() {
        this.startLatch.countDown();
    }

    /**
     * Decrements the bet latch.
     */

    public void betLatchCountDown() {
        this.betLatch.countDown();
    }

    /**
     * Decrements the insurance bet latch.
     */

    public void insuranceBetLatchCountDown() {
        this.insuranceBetLatch.countDown();
    }

    /**
     * Decrements the deal latch.
     */

    public void dealLatchCountDown() {
        this.dealLatch.countDown();
    }

    /**
     * Decrements the dealer turn latch.
     */

    public void dealerTurnLatchCountDown() {
        this.dealerTurnLatch.countDown();
    }
}