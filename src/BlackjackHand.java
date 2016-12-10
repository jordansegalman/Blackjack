/**
 * BlackjackHand objects represent a hand of cards in Blackjack.
 *
 * @author Jordan Segalman
 */

public class BlackjackHand extends Hand {

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
}