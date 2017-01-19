package BlackjackServer;

import java.util.ArrayList;
import java.util.Collections;

public class Shoe {
    private ArrayList<Card> shoe = new ArrayList<>();   // holds the cards in the shoe

    /**
     * Constructor for shoe object.
     *
     * @param numDecks Number of decks in the shoe
     */

    public Shoe(int numDecks) {
        for (int i = 0; i < numDecks; i++) {
            this.addDeck(new Deck());
        }
    }

    /**
     * Adds a deck to the shoe.
     *
     * @param deck Deck to add to the shoe
     */

    public void addDeck(Deck deck) {
        for (int i = 0; i < deck.size(); i++) {
            this.shoe.add(deck.dealCard());
        }
    }

    /**
     * Shuffles the shoe.
     */

    public void shuffle() {
        Collections.shuffle(this.shoe);
    }

    /**
     * Returns the last card in the shoe.
     *
     * @return the last card in the shoe
     */

    public Card dealCard() {
        Card card = this.shoe.get(this.shoe.size() - 1);    // last card in the shoe
        this.shoe.remove(card);
        return card;
    }

    /**
     * Returns the number of cards in the shoe.
     *
     * @return the number of cards in the shoe
     */

    public int remainingCards() {
        return this.shoe.size();
    }
}