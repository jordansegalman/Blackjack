import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * Player objects represent a player in the Blackjack game.
 *
 * @author Jordan Segalman
 */

// TODO: Splitting Pairs
// TODO: Doubling Down
// TODO: Insurance

public class Player implements Runnable {
    private final Table table;                  // table to join
    private BufferedReader in;                  // in to client
    private PrintWriter out;                    // out from client
    private Hand playerHand = new Hand();       // player hand to hold cards
    private double money = 2500;                // money available to bet
    private String clientMessage;               // message received from client
    private double bet;                         // amount of money bet
    private boolean receivedBet = false;        // true if bet made, false if not
    private boolean hasBlackjack = false;       // true if player has Blackjack, false if does not
    private String choice;                      // choice to hit or stand
    private boolean receivedChoice = false;     // true if choice to hit or stand made, false if not
    private CountDownLatch startLatch;          // latch to wait for all players to join game
    private CountDownLatch betLatch;            // latch to wait for all players to bet
    private CountDownLatch dealLatch;           // latch to wait for all players to be dealt cards
    private CountDownLatch dealerTurnLatch;     // latch to wait for dealer to finish turn
    private String playAgain;                   // choice to play again or not
    private boolean receivedPlayAgain = false;  // true if choice to play again made, false if not
    private boolean continuePlaying = false;    // true if player wants to keep playing, false if does not

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
        out.println("INFOMESSAGE--Welcome to Blackjack!");
    }

    /**
     * Player thread run method.
     */

    @Override
    public void run() {
        do {
            this.playBlackjack();
        } while (this.continuePlaying);
        out.println("INFOMESSAGE--You leave with $" + String.format("%.2f", this.money) + ".");
        out.println("GAMEOVERMESSAGE--Thanks for playing!");
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
        this.sendResult();
        this.getContinuePlaying();
    }

    /**
     * Sets the player up for a new round of Blackjack.
     */

    private void setupPlayer() {
        this.playerHand.clear();
        this.receivedBet = false;
        this.hasBlackjack = false;
        this.receivedChoice = false;
        this.receivedPlayAgain = false;
        this.continuePlaying = false;
        this.startLatch = new CountDownLatch(1);
        this.betLatch = new CountDownLatch(1);
        this.dealLatch = new CountDownLatch(1);
        this.dealerTurnLatch = new CountDownLatch(1);
        out.println("INFOMESSAGE--Waiting for other players to join.");
    }

    /**
     * Gets the player's bet.
     */

    private void getBet() {
        do {
            boolean betNotNumeric = false;  // true if bet is not a positive integer, false if it is
            out.println("INFOMESSAGE--You have $" + String.format("%.2f", this.money) + ".");
            out.println("REPLYMESSAGE--The minimum bet is $" + String.format("%.2f", this.table.getMinimumBet()) + ". How much would you like to bet?");
            try {
                while (!this.receivedBet) {
                    if ((this.clientMessage = in.readLine()) != null) {
                        try {
                            this.bet = Integer.parseInt(this.clientMessage);
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
                out.println("INFOMESSAGE--Your bet must be a positive whole number.");
                this.receivedBet = false;
            } else if (this.bet > this.money) {
                out.println("INFOMESSAGE--You cannot bet more money than you have.");
                this.receivedBet = false;
            } else if (this.bet < this.table.getMinimumBet()) {
                out.println("INFOMESSAGE--You must bet at least the minimum amount.");
                this.receivedBet = false;
            }
        } while (!this.receivedBet);
        this.table.placedBetsLatchCountDown();
        if (this.table.getNumPlayers() > 1) {
            out.println("INFOMESSAGE--Waiting for other players to place their bets.");
        }
    }

    /**
     * Sends initial round information to the player including the
     * player's first two cards, the card the dealer is showing,
     * and whether or not the player or dealer has Blackjack.
     */

    private void sendRoundInformation() {
        out.println("INFOMESSAGE--Your Cards:");
        for (int i = 0; i < this.playerHand.size(); i++) {
            out.println("INFOMESSAGE--" + this.playerHand.getCard(i));
        }
        out.println("INFOMESSAGE--Your Total: " + this.playerHand.blackjackValue());
        out.println("INFOMESSAGE--The dealer is showing the " + table.getDealerHand().getCard(0) + ".");
        if (this.playerHand.blackjackValue() == 21 && this.table.getDealerHand().blackjackValue() == 21) {
            out.println("INFOMESSAGE--You and the dealer both have Blackjack.");
            this.hasBlackjack = true;
        } else if (this.playerHand.blackjackValue() == 21) {
            out.println("INFOMESSAGE--You have Blackjack.");
            this.hasBlackjack = true;
        } else if (this.table.getDealerHand().blackjackValue() == 21) {
            out.println("INFOMESSAGE--The dealer has Blackjack.");
        }
        this.table.turnLatchCountDown();
        if (this.table.getNumPlayers() > 1) {
            out.println("INFOMESSAGE--Waiting for other players to take their turns.");
        }
    }

    /**
     * Performs the player's turn by asking if the player wants to
     * hit or stand.
     */

    void takeTurn() {
        if (!this.hasBlackjack && !this.table.getDealerHasBlackjack()) {
            do {
                this.receivedChoice = false;
                do {
                    out.println("REPLYMESSAGE--Would you like to hit or stand? [H/s]");
                    try {
                        while (!this.receivedChoice) {
                            if ((this.clientMessage = in.readLine()) != null) {
                                this.choice = this.clientMessage;
                                this.receivedChoice = true;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!this.choice.equals("H") && !this.choice.equals("h") && !this.choice.equals("S") && !this.choice.equals("s")) {
                        out.println("INFOMESSAGE--Please enter either 'H' or 'S'.");
                        out.println("INFOMESSAGE--Your Total: " + this.playerHand.blackjackValue());
                        this.receivedChoice = false;
                    }
                } while (!this.receivedChoice);
                if (this.choice.equals("H") || this.choice.equals("h")) {
                    Card newCard = this.table.dealCard();
                    this.dealCard(newCard);
                    out.println("INFOMESSAGE--You got the " + newCard + ".");
                    out.println("INFOMESSAGE--Your Total: " + this.playerHand.blackjackValue());
                }
            } while ((this.choice.equals("H") || this.choice.equals("h")) && this.playerHand.blackjackValue() <= 21);
            if (this.table.getNumPlayers() > 1) {
                out.println("INFOMESSAGE--Waiting for other players to take their turns.");
            }
        }
    }

    /**
     * Sends the final results to the player including the player
     * and dealer hand values, whether or not the player or dealer
     * busted, and who won.
     */

    private void sendResult() {
        out.println("INFOMESSAGE--Dealer's Cards:");
        for (int i = 0; i < this.table.getDealerHand().size(); i++) {
            out.println("INFOMESSAGE--" + this.table.getDealerHand().getCard(i));
        }
        out.println("INFOMESSAGE--Dealer's Total: " + this.table.getDealerHand().blackjackValue());
        out.println("INFOMESSAGE--Your Total: " + this.playerHand.blackjackValue());
        if (!this.hasBlackjack && !this.table.getDealerHasBlackjack()) {
            if (this.playerHand.blackjackValue() > 21 && this.table.getDealerHand().blackjackValue() > 21) {
                out.println("INFOMESSAGE--You and the dealer both busted. It's a tie!");
            } else if (this.playerHand.blackjackValue() > 21) {
                out.println("INFOMESSAGE--You busted. The dealer wins!");
                this.money -= this.bet;
            } else if (this.table.getDealerHand().blackjackValue() > 21) {
                out.println("INFOMESSAGE--The dealer busted. You win!");
                this.money += this.bet;
            } else {
                if (this.playerHand.blackjackValue() == this.table.getDealerHand().blackjackValue()) {
                    out.println("INFOMESSAGE--It's a tie!");
                } else if (this.playerHand.blackjackValue() < this.table.getDealerHand().blackjackValue()) {
                    out.println("INFOMESSAGE--The dealer wins!");
                    this.money -= this.bet;
                } else if (this.playerHand.blackjackValue() > this.table.getDealerHand().blackjackValue()){
                    out.println("INFOMESSAGE--You win!");
                    this.money += this.bet;
                }
            }
        } else {
            if (this.hasBlackjack && this.table.getDealerHasBlackjack()) {
                out.println("INFOMESSAGE--You and the dealer both have Blackjack. It's a tie!");
            } else if (!this.hasBlackjack && this.table.getDealerHasBlackjack()) {
                out.println("INFOMESSAGE--The dealer has Blackjack. The dealer wins!");
                this.money -= this.bet;
            } else if (this.hasBlackjack && !this.table.getDealerHasBlackjack()) {
                out.println("INFOMESSAGE--You have Blackjack. You win!");
                this.money += this.bet * (3 / 2);
            }
        }
    }

    /**
     * Determines whether or not the player wants to keep playing.
     */

    private void getContinuePlaying() {
        if (this.money >= this.table.getMinimumBet()) {
            do {
                out.println("REPLYMESSAGE--Would you like to keep playing? [Y/n]");
                try {
                    while (!this.receivedPlayAgain) {
                        if ((this.clientMessage = in.readLine()) != null) {
                            this.playAgain = this.clientMessage;
                            this.receivedPlayAgain = true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!this.playAgain.equals("Y") && !this.playAgain.equals("y") && !this.playAgain.equals("N") && !this.playAgain.equals("n")) {
                    out.println("INFOMESSAGE--Please enter either 'Y' or 'N'.");
                    this.receivedPlayAgain = false;
                }
            } while (!this.receivedPlayAgain);
            if (this.playAgain.equals("Y") || this.playAgain.equals("y")) {
                this.continuePlaying = true;
            } else {
                this.table.removePlayer(this);
            }
        } else {
            out.println("INFOMESSAGE--You do not have enough money to make the minimum bet.");
            this.table.removePlayer(this);
        }
        this.table.continuePlayingLatchCountDown();
    }

    /**
     * Deals a card to the player.
     *
     * @param card Card dealt to player
     */

    public void dealCard(Card card) {
        this.playerHand.addCard(card);
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