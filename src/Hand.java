import java.util.ArrayList;

/**
 * Hand objects represent a hand of cards.
 *
 * @author Jordan Segalman
 */

public class Hand {
    private ArrayList<Card> hand = new ArrayList<>();   // holds the cards in the hand

    /**
     * Adds a card to the hand.
     *
     * @param newCard Card to add to hand
     */

    public void addCard(Card newCard) {
        this.hand.add(newCard);
    }

    /**
     * Returns the value of the hand with aces counting as 1.
     *
     * @return the value of the hand
     */

    public int value() {
        int value = 0;  // value of the hand
        for (Card card : this.hand) {
            value += card.value();
        }
        return value;
    }

    /**
     * Returns the value of the hand with aces counting as
     * either 1 or 11 depending on whether or not the hand
     * is soft.
     *
     * @return the value of the hand
     */

    public int blackjackValue() {
        int value = 0;  // value of the hand
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
     * Returns the number of cards in the hand.
     *
     * @return the number of cards in the hand
     */

    public int size() {
        return this.hand.size();
    }

    /**
     * Returns the card at the given index.
     *
     * @param index Index of the card to return
     * @return the card at the given index
     */

    public Card getCard(int index) {
        return this.hand.get(index);
    }

    /**
     * Removes all of the cards from the hand.
     */

    public void clear() {
        this.hand.clear();
    }
}