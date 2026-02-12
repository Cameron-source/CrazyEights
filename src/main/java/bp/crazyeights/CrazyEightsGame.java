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
import java.util.Scanner;


public class CrazyEightsGame extends Game {
    private static final int MAX_TURNS = 1000; // Safety limit to prevent infinite loops
    protected final Deck deck = new Deck();
    protected PlayingCard topCard;  // The current top card of the discard pile
    protected final ArrayList<PlayingCard> discardPile = new ArrayList<>();  // Cards that have been played

    public CrazyEightsGame() {
        super("Crazy Eights");
    }

    @Override
    public void play() {
        // Build and shuffle the deck
        deck.buildDeck();
        deck.shuffle();
        
        // Deal 5 cards to each player
        dealCards(5);
        
        // Show all hands
        System.out.println("\nStarting Hands:");
        for (Player p : getPlayers()) {
            p.play(); // This will print the hand
        }
        
        // Deal one card to the discard pile
        topCard = deck.dealOne();
        System.out.println("\nTop card: " + topCard);
        
        // Play the game
        playGame();
        
        // Declare winner
        declareWinner();
    }

    /**
     * Main game loop - players take turns playing cards or drawing from the deck.
     */
    protected void playGame() {
        System.out.println("\nGame Started\n");
        
        int currentPlayerIndex = 0;
        int turnNumber = 1;
        
        while (true) {
            CrazyEightsPlayer player = (CrazyEightsPlayer) getPlayers().get(currentPlayerIndex);
            
            System.out.println("Turn " + turnNumber + ": " + player.getName());
            System.out.println("Top card: " + topCard);
            
            // Try to play a card from hand
            boolean played = tryPlayCard(player, false);
            
            // If no card was played, draw from deck until a playable card is found
            if (!played) {
                int cardsDrawn = 0;
                PlayingCard playableCard = null;
                
                while (!played) {
                    PlayingCard drawnCard = drawCard();
                    if (drawnCard == null) {
                        System.out.println("Deck empty. Game ends.");
                        break;
                    }
                    
                    player.receive(drawnCard);
                    cardsDrawn++;
                    
                    // Check if the drawn card can be played
                    if (drawnCard.canPlayOn(topCard)) {
                        playableCard = drawnCard;
                        played = true;
                    }
                }
                
                // Announce the drawing and playing action
                if (cardsDrawn > 0) {
                    if (played && playableCard != null) {
                        System.out.println(player.getName() + " draws " + cardsDrawn + " card(s)");
                        // Remove and play the specific card we verified
                        if (player.removeCard(playableCard)) {
                            System.out.println(player.getName() + " plays: " + playableCard);
                            if (topCard != null) {
                                discardPile.add(topCard);
                            }
                            topCard = playableCard;
                        }
                    } else {
                        System.out.println(player.getName() + " draws " + cardsDrawn + " card(s) but cannot play");
                    }
                }
            }
            
            // Check if player won
            if (player.hasNoCards()) {
                System.out.println(player.getName() + " wins!");
                break;
            }
            
            System.out.println();
            
            // Move to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % getPlayers().size();
            turnNumber++;
            
            // Safety check to prevent infinite loops
            if (turnNumber > MAX_TURNS) {
                System.out.println("Game ended.");
                break;
            }
        }
    }

    /**
     * Attempts to play a card from the player's hand onto the top card.
     * @param player the player attempting to play
     * @param immediately whether the card was just drawn (for message formatting)
     * @return true if a card was played, false otherwise
     */
    private boolean tryPlayCard(CrazyEightsPlayer player, boolean immediately) {
        if (!player.hasPlayableCard(topCard)) {
            return false;
        }
        
        PlayingCard playedCard = player.playCard(topCard);
        if (playedCard != null) {
            System.out.println(player.getName() + " plays: " + playedCard);
            if (topCard != null) {
                discardPile.add(topCard);
            }
            topCard = playedCard;
            return true;
        }
        return false;
    }

    /**
     * Draws a card from the deck. If the deck is empty, reshuffles the discard pile.
     * @return the drawn card, or null if both deck and discard pile are empty
     */
    private PlayingCard drawCard() {
        PlayingCard card = deck.dealOne();
        if (card != null) {
            return card;
        }
        
        // Deck is empty - try to reshuffle discard pile
        if (discardPile.isEmpty()) {
            return null;
        }
        
        System.out.println("Deck empty! Reshuffling discard pile...");
        ArrayList<Card> cardsToShuffle = new ArrayList<>(discardPile);
        discardPile.clear();
        deck.setCards(cardsToShuffle);
        deck.shuffle();
        
        return deck.dealOne();
    }

    protected void dealCards(int numCards) {
        for (int i = 0; i < numCards; i++) {
            for (Player player : getPlayers()) {
                CrazyEightsPlayer cePlayer = (CrazyEightsPlayer) player;
                cePlayer.receive(deck.dealOne());
            }
        }
        System.out.println("Dealt " + numCards + " cards to each player");
    }

    @Override
    public void declareWinner() {
        System.out.println("\nGame Over");
        
        // Find the player with the fewest cards
        CrazyEightsPlayer winner = null;
        int minCards = Integer.MAX_VALUE;
        
        for (Player player : getPlayers()) {
            CrazyEightsPlayer cePlayer = (CrazyEightsPlayer) player;
            int handSize = cePlayer.getHandSize();
            
            if (handSize < minCards) {
                minCards = handSize;
                winner = cePlayer;
            }
        }
        
        System.out.println("Winner: " + winner.getName());
        
        // Show final hands
        System.out.println("\nFinal scores:");
        for (Player player : getPlayers()) {
            CrazyEightsPlayer cePlayer = (CrazyEightsPlayer) player;
            System.out.println(cePlayer.getName() + ": " + cePlayer.getHandSize() + " cards");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CrazyEightsGame game = new CrazyEightsGame();
        
        // Get number of players
        System.out.print("How many players? ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        // Get player names
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for player " + (i + 1) + ": ");
            String name = scanner.nextLine();
            players.add(new CrazyEightsPlayer(name));
        }
        
        game.setPlayers(players);
        game.play();
        scanner.close();
    }
}
