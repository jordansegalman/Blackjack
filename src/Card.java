/**
 * Card objects represent a standard playing card with a rank and a suit.
 *
 * @author Jordan Segalman
 */

public class Card {
    private final Rank rank;    // rank of the card
    private final Suit suit;    // suit of the card

    /**
     * Ranks that cards can have.
     */

    public enum Rank {
        ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), JACK(10), QUEEN(10), KING(10);

        private int value;  // value of the rank

        /**
         * Constructor for rank object.
         *
         * @param value Value of the rank
         */

        Rank(int value) {
            this.value = value;
        }

        /**
         * Returns a string representation of the rank.
         *
         * @return the string representation of the rank
         */

        @Override
        public String toString() {
            return this.name().charAt(0) + this.name().substring(1, this.name().length()).toLowerCase();
        }
    }

    /**
     * Suits that cards can have.
     */

    public enum Suit {
        CLUBS, DIAMONDS, HEARTS, SPADES;

        /**
         * Returns a string representation of the suit.
         *
         * @return the string representation of the suit
         */

        @Override
        public String toString() {
            return this.name().charAt(0) + this.name().substring(1, this.name().length()).toLowerCase();
        }
    }

    /**
     * Constructor for card object.
     *
     * @param rank Rank of the card
     * @param suit Suit of the card
     */

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Returns the value of the card.
     *
     * @return the value of the card
     */

    public int value() {
        return this.rank.value;
    }

    /**
     * Returns a string representation of the card.
     *
     * @return the string representation of the card
     */

    @Override
    public String toString() {
        return this.rank + " of " + this.suit;
    }
}