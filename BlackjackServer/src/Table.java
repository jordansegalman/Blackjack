import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Table objects represent a Blackjack table that players can join.
 *
 * @author Jordan Segalman
 */

public class Table implements Runnable {
    private static final int MAXIMUM_SCORE = 21;                            // maximum score before bust
    private static final int DEALER_HIT_THRESHOLD = 17;                     // score that dealer stands on
    private ArrayList<Player> table = new ArrayList<>();                    // holds the players at the table
    private int minimumBet;                                                 // minimum player bet
    private int numberOfDecks;                                              // number of decks in shoe
    private int minimumCardsBeforeShuffle;                                  // minimum number of cards remaining before shuffling the shoe
    private Shoe shoe;                                                      // shoe being used to deal cards
    private BlackjackHand dealerHand = new BlackjackHand();                 // dealer hand to hold cards
    private boolean dealerHasBlackjack;                                     // true if dealer has Blackjack, false if does not
    private CountDownLatch placedBetsLatch;                                 // latch to wait for all players to place their bets
    private CountDownLatch placedInsuranceBetsLatch;                        // latch to wait for all players to place their insurance bets
    private CountDownLatch turnLatch;                                       // latch to wait for all players to be ready for their turns
    private CountDownLatch continuePlayingLatch;                            // latch to wait for all players to determine if they will keep playing

    /**
     * Constructor for Table object.
     *
     * @param minimumBet Minimum player bet
     */

    public Table(int minimumBet, int numberOfDecks, int minimumCardsBeforeShuffle) {
        this.minimumBet = minimumBet;
        this.numberOfDecks = numberOfDecks;
        this.minimumCardsBeforeShuffle = minimumCardsBeforeShuffle;
    }

    /**
     * Table thread run method.
     */

    @Override
    public void run() {
        shoe = new Shoe(numberOfDecks);
        shoe.shuffle();
        do {
            playBlackjack();
        } while (numPlayers() > 0);
    }

    /**
     * Plays Blackjack.
     */

    private void playBlackjack() {
        setup();
        for (Player player : table) {
            player.startLatchCountDown();
        }
        try {
            placedBetsLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Player player : table) {
            player.betLatchCountDown();
        }
        dealInitialCards();
        for (Player player : table) {
            player.dealLatchCountDown();
        }
        try {
            placedInsuranceBetsLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Player player : table) {
            player.insuranceBetLatchCountDown();
        }
        try {
            turnLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Player player : table) {
            player.takeTurn(player.originalPlayerHand());
        }
        dealerTurn();
        for (Player player : table) {
            player.dealerTurnLatchCountDown();
        }
        try {
            continuePlayingLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the table up for a new round of Blackjack.
     */

    private void setup() {
        if (shoe.remainingCards() <= minimumCardsBeforeShuffle) {
            shoe = new Shoe(numberOfDecks);
            shoe.shuffle();
        }
        dealerHand.clear();
        dealerHasBlackjack = false;
        placedBetsLatch = new CountDownLatch(numPlayers());
        placedInsuranceBetsLatch = new CountDownLatch(numPlayers());
        turnLatch = new CountDownLatch(numPlayers());
        continuePlayingLatch = new CountDownLatch(numPlayers());
    }

    /**
     * Deals the first two cards to each player and the dealer.
     */

    private void dealInitialCards() {
        for (int i = 0; i < 2; i++) {
            dealerHand.addCard(dealCard());
            for (Player player : table) {
                player.originalPlayerHand().addCard(dealCard());
            }
        }
        if (dealerHand.blackjackValue() == MAXIMUM_SCORE) {
            dealerHasBlackjack = true;
        }
    }

    /**
     * Performs the dealer's turn.
     */

    private void dealerTurn() {
        while ((dealerHand.isSoft() && dealerHand.blackjackValue() == DEALER_HIT_THRESHOLD) || dealerHand.blackjackValue() < DEALER_HIT_THRESHOLD) {
            dealerHand.addCard(dealCard());
        }
    }

    /**
     * Adds a player to the table.
     *
     * @param player Player to add to table
     */

    public void addPlayer(Player player) {
        table.add(player);
    }

    /**
     * Removes a player from the table.
     *
     * @param player Player to remove from table
     */

    public void removePlayer(Player player) {
        table.remove(player);
    }

    /**
     * Returns the number of players at the table.
     *
     * @return the number of players at the table
     */

    public int numPlayers() {
        return table.size();
    }

    /**
     * Returns the minimum bet of the table.
     *
     * @return the minimum bet of the table
     */

    public double minimumBet() {
        return minimumBet;
    }

    /**
     * Returns whether or not the dealer has Blackjack.
     *
     * @return true if the dealer has Blackjack, false if does not
     */

    public boolean dealerHasBlackjack() {
        return dealerHasBlackjack;
    }

    /**
     * Returns the card the dealer is showing.
     *
     * @return the card the dealer is showing
     */

    public Card dealerShownCard() {
        return dealerHand.getCard(0);
    }

    /**
     * Returns the dealer hand.
     *
     * @return the dealer hand
     */

    public BlackjackHand dealerHand() {
        return dealerHand;
    }

    /**
     * Returns a card dealt from the shoe.
     *
     * @return a card dealt from the shoe
     */

    public Card dealCard() {
        if (shoe.remainingCards() == 0) {
            shoe = new Shoe(numberOfDecks);
            shoe.shuffle();
        }
        return shoe.dealCard();
    }

    /**
     * Decrements the placed bets latch.
     */

    public void placedBetsLatchCountDown() {
        placedBetsLatch.countDown();
    }

    /**
     * Decrements the placed insurance bets latch.
     */

    public void placedInsuranceBetsLatchCountDown() {
        placedInsuranceBetsLatch.countDown();
    }

    /**
     * Decrements the turn latch.
     */

    public void turnLatchCountDown() {
        turnLatch.countDown();
    }

    /**
     * Decrements the continue playing latch.
     */

    public void continuePlayingLatchCountDown() {
        continuePlayingLatch.countDown();
    }
}