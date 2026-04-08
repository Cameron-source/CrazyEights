/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bp.crazyeights;
import java.util.Scanner;

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
     * @return true if this card can be played
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
     * Convenience overload with no declared suit.
     * @param other the current top card
     * @return true if this card can be played
     */
    public boolean canPlayOn(PlayingCard other) {
        return canPlayOn(other, -1);
    }

    /**
     * Prompts the player to choose a new suit after playing an 8.
     * Returns a dummy PlayingCard with rank 7 and the chosen suit.
     * @param scanner the shared Scanner
     * @return a PlayingCard representing the declared suit
     */
    public PlayingCard chooseSuit(Scanner scanner) {
        if (this.getRank() != 7) {
            return this;
        }
        System.out.println("You have played an 8. Select the new suit.");
        System.out.println("0. Hearts  1. Diamonds  2. Clubs  3. Spades");
        System.out.print("Enter your choice: ");
        int newSuit = Integer.parseInt(scanner.nextLine());
        if (newSuit < 0 || newSuit > 3) {
            System.out.println("INVALID INPUT: Suit will remain unchanged.");
            return this;
        }
        PlayingCard newCard = new PlayingCard(this.rank, newSuit);
        System.out.println("Suit changed to: " + newCard.getSuitName());
        return newCard;
    }

    @Override
    public String toString() {
        return getRankName() + " of " + getSuitName();
    }
}
