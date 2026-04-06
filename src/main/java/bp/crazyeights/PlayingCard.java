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
     */
    public boolean canPlayOn(PlayingCard other) {
        if (other == null) {
            return false;
        }
        // 8s are wild
        // 8's are on index 7
        if (this.rank == 7) {
            return true;
        }
        // Match rank or suit
        return this.rank == other.rank || this.suit == other.suit;
    }
    
    // logic for choosing a suit if an 8 (cards with rank 7) is played
    public PlayingCard chooseSuit(Scanner scanner) {
        if (this.getRank() != 7) {
            return this;
        }
        
        System.out.println("You have played an 8. Select the new suit.");
        System.out.println("0. Hearts  1. Diamonds  2. Clubs  3. Spades");
        System.out.print("Enter your choice: ");
        int newSuit = Integer.parseInt(scanner.nextLine());
        // catch invalid input
        if (newSuit < 0 || newSuit > 3) {
            System.out.println("Suit is unchanged.");
            return this;
        }
        // dummy card for choosing the new suit
        PlayingCard newCard = new PlayingCard(this.rank, newSuit);
        System.out.println("Suit changed to: " + newCard.getSuitName());
        return newCard;
    }
        
    @Override
    public String toString() {
        return getRankName() + " of " + getSuitName();
    }
}
