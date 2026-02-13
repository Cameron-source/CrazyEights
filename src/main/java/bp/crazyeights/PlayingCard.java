/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bp.crazyeights;

/**
 *
 * @author Cameron
 */
public class PlayingCard extends Card {
    private final int rank;
    private final int suit;
    private static final String[] RANKS = {
         "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"
    };
    private static final String[] SUITS = {
        "Hearts", "Diamonds", "Clubs", "Spades"
    };

    public PlayingCard(int rank, int suit) {
        
        this.rank = rank;
        this.suit = suit;
    }

    public String getRankName() {
        return RANKS[rank];
    }

    public String getSuitName() {
        return SUITS[suit];
    }

    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }

    /**
     * Checks if this card can be played on top of another card.
     * A card can be played if it matches the rank or suit, or if this card is an 8 (wild).
     */
    public boolean canPlayOn(PlayingCard other) {
        if (other == null) {
            return false;
        }
        // 8s are wild
        if (this.rank == 8) {
            return true;
        }
        // Match rank or suit
        return this.rank == other.rank || this.suit == other.suit;
    }

    @Override
    public String toString() {
        return getRankName() + " of " + getSuitName();
    }
}
