/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bp.crazyeights;

/**
 *
 * @author Cameron
 */
import java.util.ArrayList;
import java.util.List;


public class CrazyEightsPlayer extends Player {
    private final List<PlayingCard> hand = new ArrayList<>();

    public CrazyEightsPlayer(String name) {
        super(name);
    }

    public void receive(PlayingCard card) {
        if (card != null) {
            hand.add(card);
        }
    }

    public void printHand() {
        System.out.println(getName() + " has " + hand.size() + " cards:");
        for (PlayingCard card : hand) {
            System.out.println("  " + card);
        }
    }

    /**
     * Returns the number of cards in the player's hand.
     */
    public int getHandSize() {
        return hand.size();
    }

    /**
     * Checks if the player has no cards left.
     * @return true if the player has no cards in their hand, false otherwise
     */
    public boolean hasNoCards() {
        return hand.isEmpty();
    }

    /**
     * Checks if the player has any cards that can be played on the given card.
     */
    public boolean hasPlayableCard(PlayingCard topCard) {
        for (PlayingCard card : hand) {
            if (card.canPlayOn(topCard)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds and plays (removes from hand) the first card that can be played on the given card.
     * Returns null if no card can be played.
     */
    public PlayingCard playCard(PlayingCard topCard) {
        for (int i = 0; i < hand.size(); i++) {
            PlayingCard card = hand.get(i);
            if (card.canPlayOn(topCard)) {
                hand.remove(i);
                return card;
            }
        }
        return null;
    }

    /**
     * Removes a specific card from the player's hand.
     * @param cardToRemove the card to remove from the hand
     * @return true if the card was found and removed, false otherwise
     */
    public boolean removeCard(PlayingCard cardToRemove) {
        return hand.remove(cardToRemove);
    }

    @Override
    public void play() {
        // Simple implementation - just print hand
        printHand();
    }
}