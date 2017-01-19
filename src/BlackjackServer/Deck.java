package BlackjackServer;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Deck objects represent a standard deck of playing cards.
 *
 * @author Jordan Segalman
 */

public class Deck {
    private ArrayList<Card> deck = new ArrayList<>();   // holds the cards in the deck

    /**
     * Constructor for deck object.
     */

    public Deck() {
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                this.deck.add(new Card(rank, suit));
            }
        }
    }

    /**
     * Shuffles the deck.
     */

    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    /**
     * Returns the last card in the deck.
     *
     * @return the last card in the deck
     */

    public Card dealCard() {
        Card card = this.deck.get(this.deck.size() - 1);    // last card in the deck
        this.deck.remove(card);
        return card;
    }

    /**
     * Returns the number of cards in the deck.
     *
     * @return the number of cards in the deck
     */

    public int size() {
        return this.deck.size();
    }
}