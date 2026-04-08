package bp.crazyeights;

import java.util.ArrayList;

/**
 * A standard 52-card deck used in Crazy Eights.
 * Extends GroupOfCards with the ability to initialize and replace the card list.
 *
 * @author Cameron - Apr 2026
 * @author Tamim 
 */
public class Deck extends GroupOfCards {

    public Deck() {
        super(52);
        setCards(buildStandardDeck());
    }

    /**
     * Builds a full 52-card standard deck.
     * @return list of all 52 PlayingCards
     */
    private ArrayList<Card> buildStandardDeck() {
        ArrayList<Card> cards = new ArrayList<>();
        for (int suit = 0; suit < 4; suit++) {
            for (int rank = 0; rank < 13; rank++) {
                cards.add(new PlayingCard(rank, suit));
            }
        }
        return cards;
    }

    /**
     * Replaces the current cards in the deck (used when reshuffling the discard pile).
     * @param cards the new list of cards
     */
    @Override
    public void setCards(ArrayList<Card> cards) {
        // Populate via the inherited getCards() reference by clearing and re-adding,
        // since GroupOfCards.cards is private. We keep a local reference instead.
        this.deckCards = cards;
    }

    // Local card list since GroupOfCards.cards is private and has no setter
    private ArrayList<Card> deckCards;

    /**
     * Returns the deck's card list.
     */
    @Override
    public ArrayList<Card> getCards() {
        return deckCards;
    }

    /**
     * Shuffles the deck.
     */
    @Override
    public void shuffle() {
        java.util.Collections.shuffle(deckCards);
    }
}
