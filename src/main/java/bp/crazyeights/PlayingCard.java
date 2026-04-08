/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bp.crazyeights;

/**
 *
 * @author Cameron
 * @author Tamim
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
     * If a suit was declared after an 8, the declared suit overrides the top card's suit.
     * @param other the current top card
     * @param declaredSuit the suit declared by the player who played an 8 (-1 if no declaration active)
     * @return 
     */
    public boolean canPlayOn(PlayingCard other, int declaredSuit) {
        if (other == null) {
            return false;
        }
        // 8s are always wild (rank index 7 = "8" in RANKS array)
        if (this.rank == 7) {
            return true;
        }
        // If a suit was declared after an 8, must match the declared suit
        if (declaredSuit != -1) {
            return this.suit == declaredSuit;
        }
        // Otherwise match rank or suit of top card
        return this.rank == other.rank || this.suit == other.suit;
    }

    /**
     * Convenience overload with no declared suit (declaredSuit = -1).
     * @param other current top card
     * @return 
     */
    public boolean canPlayOn(PlayingCard other) {
        return canPlayOn(other, -1);
    }
    
        
    @Override
    public String toString() {
        return getRankName() + " of " + getSuitName();
    }
}
