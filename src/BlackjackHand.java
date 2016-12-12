/**
 * BlackjackHand objects represent a hand of cards in Blackjack.
 *
 * @author Jordan Segalman
 */

public class BlackjackHand extends Hand {
    private double bet;                 // amount of money bet on hand
    private boolean splitPairs = false; // true if player decides to split pairs, false if does not
    private boolean doubleDown = false; // true if player decides to double down, false if does not
    private Card doubleDownCard;        // card added to hand face down after double down

    /**
     * Returns the value of the hand with aces counting as
     * either 1 or 11 depending on whether or not the hand
     * is soft.
     *
     * @return the value of the hand
     */

    public int blackjackValue() {
        int value = 0;  // value of the hand in Blackjack
        for (Card card : this.hand) {
            value += card.value();
        }
        if (this.isSoft()) {
            value += 10;
        }
        return value;
    }

    /**
     * Returns whether or not the hand contains an ace.
     *
     * @return true if the hand contains an ace, false if does not
     */

    public boolean hasAce() {
        for (Card card : this.hand) {
            if (card.value() == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether or not the hand is soft.
     *
     * @return true if the hand is soft, false if not
     */

    public boolean isSoft() {
        return this.hasAce() && this.value() < 12;
    }

    /**
     * Places a bet on the hand.
     *
     * @param bet Bet to place on the hand
     */

    public void placeBet(double bet) {
        this.bet = bet;
    }

    /**
     * Returns the bet placed on the hand.
     *
     * @return the bet placed on the hand
     */

    public double bet() {
        return this.bet;
    }

    /**
     * Sets the hand as split.
     */

    public void setSplitPairs() {
        this.splitPairs = true;
    }

    /**
     * Returns whether or not the hand has been split.
     *
     * @return true if the hand has been split, false if not
     */

    public boolean splitPairs() {
        return this.splitPairs;
    }

    /**
     * Sets the hand as doubled down.
     */

    public void setDoubleDown() {
        this.doubleDown = true;
    }

    /**
     * Returns whether or not the hand has been doubled down.
     *
     * @return true if the hand has been doubled down, false if not
     */

    public boolean doubleDown() {
        return this.doubleDown;
    }

    /**
     * Adds a single card to the hand after doubling down.
     *
     * @param card Card added to hand after doubling down
     */

    public void addDoubleDownCard(Card card) {
        this.doubleDownCard = card;
        this.addCard(card);
    }

    /**
     * Returns the card added to the hand after doubling down.
     *
     * @return the card added to the hand after doubling down
     */

    public Card doubleDownCard() {
        return this.doubleDownCard;
    }
}