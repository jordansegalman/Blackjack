package BlackjackServer;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Table objects represent a Blackjack table that players can join.
 *
 * @author Jordan Segalman
 */

public class Table implements Runnable {
    private ArrayList<Player> table = new ArrayList<>();    // holds the players at the table
    private Shoe shoe;                                      // shoe being used to deal cards
    private BlackjackHand dealerHand = new BlackjackHand(); // dealer hand to hold cards
    private boolean dealerHasBlackjack;                     // true if dealer has Blackjack, false if does not
    private CountDownLatch placedBetsLatch;                 // latch to wait for all players to place their bets
    private CountDownLatch placedInsuranceBetsLatch;        // latch to wait for all players to place their insurance bets
    private CountDownLatch turnLatch;                       // latch to wait for all players to be ready for their turns
    private CountDownLatch continuePlayingLatch;            // latch to wait for all players to determine if they will keep playing

    /**
     * Table thread run method.
     */

    @Override
    public void run() {
        this.shoe = new Shoe(6);
        this.shoe.shuffle();
        do {
            this.playBlackjack();
        } while (this.numPlayers() > 0);
    }

    /**
     * Plays Blackjack.
     */

    private void playBlackjack() {
        this.setup();
        for (Player player : this.table) {
            player.startLatchCountDown();
        }
        try {
            this.placedBetsLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Player player : this.table) {
            player.betLatchCountDown();
        }
        this.dealInitialCards();
        for (Player player : this.table) {
            player.dealLatchCountDown();
        }
        try {
            this.placedInsuranceBetsLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Player player : this.table) {
            player.insuranceBetLatchCountDown();
        }
        try {
            this.turnLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Player player : this.table) {
            player.takeTurn(player.originalPlayerHand());
        }
        this.dealerTurn();
        for (Player player : this.table) {
            player.dealerTurnLatchCountDown();
        }
        try {
            this.continuePlayingLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the table up for a new round of Blackjack.
     */

    private void setup() {
        if (this.shoe.remainingCards() < 78) {
            this.shoe = new Shoe(6);
            this.shoe.shuffle();
        }
        this.dealerHand.clear();
        this.dealerHasBlackjack = false;
        this.placedBetsLatch = new CountDownLatch(this.numPlayers());
        this.placedInsuranceBetsLatch = new CountDownLatch(this.numPlayers());
        this.turnLatch = new CountDownLatch(this.numPlayers());
        this.continuePlayingLatch = new CountDownLatch(this.numPlayers());
    }

    /**
     * Deals the first two cards to each player and the dealer.
     */

    private void dealInitialCards() {
        for (int i = 0; i < 2; i++) {
            this.dealerHand.addCard(this.shoe.dealCard());
            for (Player player : this.table) {
                player.originalPlayerHand().addCard(this.shoe.dealCard());
            }
        }
        if (this.dealerHand.blackjackValue() == 21) {
            this.dealerHasBlackjack = true;
        }
    }

    /**
     * Performs the dealer's turn.
     */

    private void dealerTurn() {
        while ((this.dealerHand.isSoft() && this.dealerHand.blackjackValue() == 17) || this.dealerHand.blackjackValue() < 17) {
            this.dealerHand.addCard(this.shoe.dealCard());
        }
    }

    /**
     * Adds a player to the table.
     *
     * @param player Player to add to table
     */

    public void addPlayer(Player player) {
        this.table.add(player);
    }

    /**
     * Removes a player from the table.
     *
     * @param player Player to remove from table
     */

    public void removePlayer(Player player) {
        this.table.remove(player);
    }

    /**
     * Returns the number of players at the table.
     *
     * @return the number of players at the table
     */

    public int numPlayers() {
        return this.table.size();
    }

    /**
     * Returns the minimum bet of the table.
     *
     * @return the minimum bet of the table
     */

    public double minimumBet() {
        return 500;
    }

    /**
     * Returns whether or not the dealer has Blackjack.
     *
     * @return true if the dealer has Blackjack, false if does not
     */

    public boolean dealerHasBlackjack() {
        return this.dealerHasBlackjack;
    }

    /**
     * Returns the card the dealer is showing.
     *
     * @return the card the dealer is showing
     */

    public Card dealerShownCard() {
        return this.dealerHand.getCard(0);
    }

    /**
     * Returns the dealer hand.
     *
     * @return the dealer hand
     */

    public BlackjackHand dealerHand() {
        return this.dealerHand;
    }

    /**
     * Returns a card dealt from the shoe.
     *
     * @return a card dealt from the shoe
     */

    public Card dealCard() {
        return this.shoe.dealCard();
    }

    /**
     * Decrements the placed bets latch.
     */

    public void placedBetsLatchCountDown() {
        this.placedBetsLatch.countDown();
    }

    /**
     * Decrements the placed insurance bets latch.
     */

    public void placedInsuranceBetsLatchCountDown() {
        this.placedInsuranceBetsLatch.countDown();
    }

    /**
     * Decrements the turn latch.
     */

    public void turnLatchCountDown() {
        this.turnLatch.countDown();
    }

    /**
     * Decrements the continue playing latch.
     */

    public void continuePlayingLatchCountDown() {
        this.continuePlayingLatch.countDown();
    }
}