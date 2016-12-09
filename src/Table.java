import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Table objects represent a Blackjack table that players can join.
 *
 * @author Jordan Segalman
 */

public class Table implements Runnable {
    private ArrayList<Player> table = new ArrayList<>();    // holds the players at the table
    private Deck deck;                                      // deck being used
    private Hand dealerHand = new Hand();                   // dealer hand to hold cards
    private boolean dealerHasBlackjack;                     // true if dealer has Blackjack, false if does not
    private CountDownLatch placedBetsLatch;                 // latch to wait for all players to place their bets
    private CountDownLatch turnLatch;                       // latch to wait for all players to be ready for their turns
    private CountDownLatch continuePlayingLatch;            // latch to wait for all players to determine if they will keep playing

    /**
     * Table thread run method.
     */

    @Override
    public void run() {
        do {
            this.playBlackjack();
        } while (this.getNumPlayers() > 0);
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
            this.turnLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Player player : this.table) {
            player.takeTurn();
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
        this.deck = new Deck();
        this.deck.shuffle();
        this.dealerHand.clear();
        this.dealerHasBlackjack = false;
        this.placedBetsLatch = new CountDownLatch(this.getNumPlayers());
        this.turnLatch = new CountDownLatch(this.getNumPlayers());
        this.continuePlayingLatch = new CountDownLatch(this.getNumPlayers());
    }

    /**
     * Deals the first two cards to each player and the dealer.
     */

    private void dealInitialCards() {
        for (int i = 0; i < 2; i++) {
            this.dealerHand.addCard(this.deck.dealCard());
            for (Player player : this.table) {
                player.dealCard(this.deck.dealCard());
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
            this.dealerHand.addCard(this.deck.dealCard());
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
        this.table.remove(this.table.indexOf(player));
    }

    /**
     * Returns the number of players at the table.
     *
     * @return the number of players at the table
     */

    public int getNumPlayers() {
        return this.table.size();
    }

    /**
     * Returns the minimum bet of the table.
     *
     * @return the minimum bet of the table
     */

    public double getMinimumBet() {
        return 500;
    }

    /**
     * Returns whether or not the dealer has Blackjack.
     *
     * @return true if the dealer has Blackjack, false if does not
     */

    public boolean getDealerHasBlackjack() {
        return this.dealerHasBlackjack;
    }

    /**
     * Returns the dealer hand.
     *
     * @return the dealer hand
     */

    public Hand getDealerHand() {
        return this.dealerHand;
    }

    /**
     * Returns a card dealt from the deck.
     *
     * @return a card dealt from the deck
     */

    public Card dealCard() {
        return this.deck.dealCard();
    }

    /**
     * Decrements the placed bets latch.
     */

    public void placedBetsLatchCountDown() {
        this.placedBetsLatch.countDown();
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