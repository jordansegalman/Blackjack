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
    private String choice;                                              // choice to hit or stand
    private boolean receivedChoice = false;                             // true if choice to hit or stand made, false if not
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
        this.out.println("INFOMESSAGE--Welcome to Blackjack!");
        do {
            this.playBlackjack();
        } while (this.continuePlaying);
        this.out.println("INFOMESSAGE--You leave with $" + String.format("%.2f", this.money) + ".");
        this.out.println("GAMEOVERMESSAGE--Thanks for playing!");
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
        this.out.println("INFOMESSAGE--Waiting for other players to join.");
    }

    /**
     * Gets the player's bet.
     */

    private void getBet() {
        do {
            boolean betNotNumeric = false;  // true if bet is not a positive integer, false if it is
            this.out.println("INFOMESSAGE--You have $" + String.format("%.2f", this.money) + ".");
            this.out.println("REPLYMESSAGE--The minimum bet is $" + String.format("%.2f", this.table.minimumBet()) + ". How much would you like to bet?");
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
                this.out.println("INFOMESSAGE--Your bet must be a positive whole number.");
                this.receivedBet = false;
            } else if (this.originalPlayerHand.bet() > this.money) {
                this.out.println("INFOMESSAGE--You cannot bet more money than you have.");
                this.receivedBet = false;
            } else if (this.originalPlayerHand.bet() < this.table.minimumBet()) {
                this.out.println("INFOMESSAGE--You must bet at least the minimum amount.");
                this.receivedBet = false;
            }
        } while (!this.receivedBet);
        this.money -= this.originalPlayerHand.bet();
        this.table.placedBetsLatchCountDown();
        if (this.table.numPlayers() > 1) {
            this.out.println("INFOMESSAGE--Waiting for other players to place their bets.");
        }
    }

    /**
     * Sends initial round information to the player including the
     * player's first two cards, the card the dealer is showing,
     * and whether or not the player or dealer has Blackjack.
     */

    private void sendRoundInformation() {
        this.out.println("INFOMESSAGE--Your Cards:");
        for (int i = 0; i < this.originalPlayerHand.size(); i++) {
            this.out.println("INFOMESSAGE--" + this.originalPlayerHand.getCard(i));
        }
        if (this.originalPlayerHand.blackjackValue() == 21) {
            this.out.println("INFOMESSAGE--You have Blackjack.");
            this.hasBlackjack = true;
        }
        this.out.println("INFOMESSAGE--The dealer is showing the " + this.table.dealerShownCard() + ".");
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
            this.out.println("INFOMESSAGE--You and the dealer both have Blackjack.");
            this.hasBlackjack = true;
            if (this.placedInsuranceBet) {
                this.money += (this.insuranceBet + (this.insuranceBet * 2));
                this.out.println("INFOMESSAGE--You won $" + String.format("%.2f", this.insuranceBet * 2) + " from your insurance bet.");
            }
        } else if (this.table.dealerHand().blackjackValue() == 21) {
            this.out.println("INFOMESSAGE--The dealer has Blackjack.");
            if (this.placedInsuranceBet) {
                this.money += (this.insuranceBet + (this.insuranceBet * 2));
                this.out.println("INFOMESSAGE--You won $" + String.format("%.2f", this.insuranceBet * 2) + " from your insurance bet.");
            }
        } else {
            this.out.println("INFOMESSAGE--The dealer does not have Blackjack.");
        }
        this.table.turnLatchCountDown();
        if (this.table.numPlayers() > 1) {
            this.out.println("INFOMESSAGE--Waiting for other players to take their turns.");
        }
    }

    /**
     * Asks the player if they want to place an insurance bet.
     */

    private void getInsuranceBet() {
        if (this.money >= this.originalPlayerHand.bet() / 2) {
            this.receivedChoice = false;
            do {
                this.out.println("REPLYMESSAGE--Would you like to place an insurance bet? [Y/n]");
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
                if (!this.choice.equals("Y") && !this.choice.equals("y") && !this.choice.equals("N") && !this.choice.equals("n")) {
                    this.out.println("INFOMESSAGE--Please enter either 'Y' or 'N'.");
                    this.receivedChoice = false;
                }
            } while (!this.receivedChoice);
            if (this.choice.equals("Y") || this.choice.equals("y")) {
                this.insuranceBet = this.originalPlayerHand.bet() / 2;
                this.money -= this.insuranceBet;
                this.placedInsuranceBet = true;
                this.out.println("INFOMESSAGE--You placed an insurance bet of $" + String.format("%.2f", this.insuranceBet) + ".");
            }
        } else {
            this.out.println("INFOMESSAGE--You do not have enough money to place an insurance bet.");
        }
        if (this.table.numPlayers() > 1) {
            this.out.println("INFOMESSAGE--Waiting for other players to place their insurance bets.");
        }
    }

    /**
     * Performs the player's turn on a given hand by asking if the
     * player wants to split pairs, double down, and hit or stand.
     *
     * @param hand Hand to play
     */

    void takeTurn(BlackjackHand hand) {
        if (!this.hasBlackjack && !this.table.dealerHasBlackjack() && hand.getCard(0).rank() == hand.getCard(1).rank()) {
            if (this.money >= hand.bet()) {
                this.receivedChoice = false;
                do {
                    this.out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
                    this.out.println("REPLYMESSAGE--Would you like to split pairs? [Y/n]");
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
                    if (!this.choice.equals("Y") && !this.choice.equals("y") && !this.choice.equals("N") && !this.choice.equals("n")) {
                        this.out.println("INFOMESSAGE--Please enter either 'Y' or 'N'.");
                        this.receivedChoice = false;
                    }
                } while (!this.receivedChoice);
                if (this.choice.equals("Y") || this.choice.equals("y")) {
                    this.splitPairs(hand);
                }
            } else {
                this.out.println("INFOMESSAGE--You do not have enough money to split pairs.");
            }
        }
        if (!this.hasBlackjack && !this.table.dealerHasBlackjack() && !hand.splitPairs() && ((hand.blackjackValue() >= 9 && hand.blackjackValue() <= 11) || (hand.isSoft() && hand.blackjackValue() >= 19 && hand.blackjackValue() <= 21))) {
            if (this.money >= hand.bet()) {
                this.receivedChoice = false;
                do {
                    this.out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
                    this.out.println("REPLYMESSAGE--Would you like to double down? [Y/n]");
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
                    if (!this.choice.equals("Y") && !this.choice.equals("y") && !this.choice.equals("N") && !this.choice.equals("n")) {
                        this.out.println("INFOMESSAGE--Please enter either 'Y' or 'N'.");
                        this.receivedChoice = false;
                    }
                } while (!this.receivedChoice);
                if (this.choice.equals("Y") || this.choice.equals("y")) {
                    hand.setDoubleDown();
                    this.money -= hand.bet();
                    hand.placeBet(hand.bet() * 2);
                    Card newCard = this.table.dealCard();
                    hand.addDoubleDownCard(newCard);
                    this.out.println("INFOMESSAGE--Your bet on this hand has been doubled. You were given a card face down.");
                }
            } else {
                this.out.println("INFOMESSAGE--You do not have enough money to double down.");
            }
        }
        if (!this.hasBlackjack && !this.table.dealerHasBlackjack() && !hand.splitPairs() && !hand.doubleDown()) {
            do {
                this.receivedChoice = false;
                do {
                    this.out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
                    this.out.println("REPLYMESSAGE--Would you like to hit or stand? [H/s]");
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
                    if (!this.choice.equals("H") && !this.choice.equals("h") && !this.choice.equals("S") && !this.choice.equals("s")) {
                        this.out.println("INFOMESSAGE--Please enter either 'H' or 'S'.");
                        this.receivedChoice = false;
                    }
                } while (!this.receivedChoice);
                if (this.choice.equals("H") || this.choice.equals("h")) {
                    Card newCard = this.table.dealCard();
                    hand.addCard(newCard);
                    this.out.println("INFOMESSAGE--You got the " + newCard + ".");
                }
            } while ((this.choice.equals("H") || this.choice.equals("h")) && hand.blackjackValue() <= 21);
            this.out.println("INFOMESSAGE--Final Hand Total: " + hand.blackjackValue());
            if (hand.blackjackValue() > 21) {
                this.out.println("INFOMESSAGE--You busted.");
            }
        }
        if (this.table.numPlayers() > 1 && !this.hasBlackjack && !this.table.dealerHasBlackjack() && hand == this.playerHands.get(this.playerHands.size() - 1)) {
            this.out.println("INFOMESSAGE--Waiting for other players to take their turns.");
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
        BlackjackHand firstHand = new BlackjackHand();
        BlackjackHand secondHand = new BlackjackHand();
        this.playerHands.add(this.playerHands.indexOf(hand), secondHand);
        this.playerHands.add(this.playerHands.indexOf(secondHand), firstHand);
        this.playerHands.remove(hand);
        firstHand.addCard(hand.getCard(0));
        secondHand.addCard(hand.getCard(1));
        firstHand.placeBet(hand.bet());
        secondHand.placeBet(hand.bet());
        if (firstHand.getCard(0).rank() == Card.Rank.ACE && secondHand.getCard(0).rank() == Card.Rank.ACE) {
            Card newCard = this.table.dealCard();
            firstHand.addCard(newCard);
            this.out.println("INFOMESSAGE--You got the " + newCard + " on the first hand.");
            this.out.println("INFOMESSAGE--Final First Hand Total: " + firstHand.blackjackValue());
            newCard = this.table.dealCard();
            secondHand.addCard(newCard);
            this.out.println("INFOMESSAGE--You got the " + newCard + " on the second hand.");
            this.out.println("INFOMESSAGE--Final Second Hand Total: " + secondHand.blackjackValue());
            if (this.table.numPlayers() > 1 && secondHand == this.playerHands.get(this.playerHands.size() - 1)) {
                this.out.println("INFOMESSAGE--Waiting for other players to take their turns.");
            }
        } else {
            Card newCard = this.table.dealCard();
            firstHand.addCard(newCard);
            this.out.println("INFOMESSAGE--You got the " + newCard + " on the first hand.");
            this.out.println("INFOMESSAGE--First Hand Total: " + firstHand.blackjackValue());
            newCard = this.table.dealCard();
            secondHand.addCard(newCard);
            this.out.println("INFOMESSAGE--You got the " + newCard + " on the second hand.");
            this.out.println("INFOMESSAGE--Second Hand Total: " + secondHand.blackjackValue());
            this.takeTurn(firstHand);
            this.takeTurn(secondHand);
        }
    }

    /**
     * Sends the dealer's cards to the player.
     */

    private void sendDealerCards() {
        this.out.println("INFOMESSAGE--Dealer's Cards:");
        for (int i = 0; i < this.table.dealerHand().size(); i++) {
            this.out.println("INFOMESSAGE--" + this.table.dealerHand().getCard(i));
        }
    }

    /**
     * Sends the final results to the player for a given hand including
     * the player and dealer hand values, whether or not the player or
     * dealer busted, and who won.
     *
     * @param hand Hand to send results for
     */

    private void sendResult(BlackjackHand hand) {
        this.out.println("INFOMESSAGE--Dealer's Total: " + this.table.dealerHand().blackjackValue());
        if (hand.doubleDown()) {
            this.out.println("INFOMESSAGE--Your face down card is the " + hand.doubleDownCard() + ".");
        }
        this.out.println("INFOMESSAGE--Hand Total: " + hand.blackjackValue());
        if (!this.hasBlackjack && !this.table.dealerHasBlackjack()) {
            if (hand.blackjackValue() > 21 && this.table.dealerHand().blackjackValue() > 21) {
                this.out.println("INFOMESSAGE--You and the dealer both busted. It's a tie!");
                this.money += hand.bet();
            } else if (hand.blackjackValue() > 21) {
                this.out.println("INFOMESSAGE--You busted. The dealer wins!");
            } else if (this.table.dealerHand().blackjackValue() > 21) {
                this.out.println("INFOMESSAGE--The dealer busted. You win!");
                this.money += hand.bet() * 2;
                this.out.println("INFOMESSAGE--You won $" + String.format("%.2f", hand.bet()) + ".");
            } else {
                if (hand.blackjackValue() == this.table.dealerHand().blackjackValue()) {
                    this.out.println("INFOMESSAGE--It's a tie!");
                    this.money += hand.bet();
                } else if (hand.blackjackValue() < this.table.dealerHand().blackjackValue()) {
                    this.out.println("INFOMESSAGE--The dealer wins!");
                } else if (hand.blackjackValue() > this.table.dealerHand().blackjackValue()){
                    this.out.println("INFOMESSAGE--You win!");
                    this.money += hand.bet() * 2;
                    this.out.println("INFOMESSAGE--You won $" + String.format("%.2f", hand.bet()) + ".");
                }
            }
        } else {
            if (this.hasBlackjack && this.table.dealerHasBlackjack()) {
                this.out.println("INFOMESSAGE--You and the dealer both have Blackjack. It's a tie!");
                this.money += hand.bet();
            } else if (!this.hasBlackjack && this.table.dealerHasBlackjack()) {
                this.out.println("INFOMESSAGE--The dealer has Blackjack. The dealer wins!");
            } else if (this.hasBlackjack && !this.table.dealerHasBlackjack()) {
                this.out.println("INFOMESSAGE--You have Blackjack. You win!");
                this.money += (hand.bet() + (hand.bet() * (3.0 / 2.0)));
                this.out.println("INFOMESSAGE--You won $" + String.format("%.2f", hand.bet() * (3.0 / 2.0)) + ".");
            }
        }
    }

    /**
     * Determines whether or not the player wants to keep playing.
     */

    private void getContinuePlaying() {
        if (this.money >= this.table.minimumBet()) {
            do {
                this.out.println("REPLYMESSAGE--Would you like to keep playing? [Y/n]");
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
                if (!this.playAgain.equals("Y") && !this.playAgain.equals("y") && !this.playAgain.equals("N") && !this.playAgain.equals("n")) {
                    this.out.println("INFOMESSAGE--Please enter either 'Y' or 'N'.");
                    this.receivedPlayAgain = false;
                }
            } while (!this.receivedPlayAgain);
            if (this.playAgain.equals("Y") || this.playAgain.equals("y")) {
                this.continuePlaying = true;
            } else {
                this.table.removePlayer(this);
            }
        } else {
            this.out.println("INFOMESSAGE--You do not have enough money to make the minimum bet.");
            this.table.removePlayer(this);
        }
        this.table.continuePlayingLatchCountDown();
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