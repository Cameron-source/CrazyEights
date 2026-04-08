/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bp.crazyeights;

/**
 *
 * @author Cameron
 * @author Tamim
 * 
 */

import java.util.ArrayList;
import java.util.Scanner;

public class CrazyEightsGame extends Game {
    private static final int MAX_TURNS = 100; // Safety limit to prevent infinite loops
    private static final int MAX_DRAWS_PER_TURN = 3; // Maximum cards a player can draw per turn
    protected final Deck deck = new Deck();
    protected PlayingCard topCard;  // The current top card of the discard pile
    protected final ArrayList<PlayingCard> discardPile = new ArrayList<>();  // Cards that have been played
     protected int declaredSuit = -1; // Active suit declared after an 8 is played (-1 = none)
     
    private int turnNumber = 1; // moved turn number above for global tracking

    private final Scanner scanner; // stores scanner for game
    private final int deckSize; // determines size of deck based on check in Main
    
    // constructor
    public CrazyEightsGame(Scanner scanner, int deckSize) {
        super("Crazy Eights");
        this.scanner = scanner;
        this.deckSize = deckSize;
    }

    @Override
    public void play() {
        // Build the deck
        ArrayList<Card> cards = new ArrayList<>();
        int decks = deckSize / 52;
        for (int d = 0; d < decks; d++) {
            for (int suit = 0; suit < 4; suit++) {
                for (int rank = 0; rank < 13; rank++) {
                    cards.add(new PlayingCard(rank, suit));
                }
            }
        }
        deck.setSize(deckSize);
        deck.setCards(cards);
        
        deck.shuffle();

        // Deal 5 cards to each player
        dealCards(5);

        // Show all hands
        System.out.println("\nStarting Hands:");
        for (Player p : getPlayers()) {
            if (p instanceof CrazyEightsPlayer) {
                ((CrazyEightsPlayer)p).printHand();
            }
        }

        // Deal one card to the discard pile
        topCard = dealOne();
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
        
        while (true) {
            CrazyEightsPlayer player = (CrazyEightsPlayer) getPlayers().get(currentPlayerIndex);
            System.out.println("Turn " + turnNumber + ": " + player.getName());
            System.out.println("Top card: " + topCard);
            player.printHand();

            boolean turnDone = false;
            int drawsThisTurn = 0;
            while (!turnDone) {
                if (player.hasPlayableCard(topCard, declaredSuit)) {
                    System.out.println("Top card: " + topCard);
                    if (declaredSuit != -1) {
                        System.out.println("Active declared suit: " + getSuitName(declaredSuit));
                    }
                    System.out.println("------------------");
                    System.out.println("You have a playable card. Choose an option:");
                    System.out.println("0. Skip Turn");
                    System.out.println("1. Play a card");
                    System.out.println("2. Draw a card (max:" + MAX_DRAWS_PER_TURN + ", current draws done:" + drawsThisTurn + ")");
                    System.out.println("3. See hand and deck status");
                    System.out.print("Enter choice (0, 1, 2, 3): ");
                    String choice = scanner.nextLine();
                    
                    switch(choice) {
                        // skip turn
                        case "0": {
                            System.out.println(player.getName() + " skips their turn.");
                            turnDone = true;
                            break;
                        }
                        
                        // play card
                        case "1": {
                            // check for playable cards in hand
                            if (!player.hasPlayableCard(topCard, declaredSuit)) {
                                System.out.println("You have no playable cards.");
                                continue;
                            }
                            
                            // display playable cards in hand
                            ArrayList<PlayingCard> playable = new ArrayList<>();
                            int idx = 1;
                            for (PlayingCard card : player.getHand()) {
                                if (card.canPlayOn(topCard, declaredSuit)) {
                                    System.out.println(idx + ". " + card);
                                    playable.add(card);
                                    idx++;
                                }
                            }
                            
                            System.out.print("Select card to play (number): ");
                            int cardChoice = Integer.parseInt(scanner.nextLine());
                            
                            if (cardChoice >= 1 && cardChoice <= playable.size()) {
                                PlayingCard playedCard = playable.get(cardChoice - 1);
                                player.removeCard(playedCard);
                                
                                System.out.println(player.getName() + " plays: " + playedCard);
                                
                                if (topCard != null) {
                                    discardPile.add(topCard);
                                }
                                topCard = playedCard;
                            
                                // logic for choosing a suit if an 8 (cards with rank 7) is played
                                if (playedCard.getRank() == 7) {
                                    declaredSuit = chooseSuit(scanner, player.getName()); // logic stored in playing card
                                }
                                else {
                                    declaredSuit = -1;
                                }
                                turnDone = true;
                            }
                            else {
                                System.out.println("Invalid choice.");
                            }
                            System.out.println("--------------------");
                            break;
                        }
                        
                        // draw cards
                        case "2": {
                            if (drawsThisTurn >= MAX_DRAWS_PER_TURN) {
                                System.out.println("You have reached the maximum draws for this turn.");
                                continue;
                            }
                            
                            PlayingCard drawnCard = drawCard();
                            drawsThisTurn++;
            
                            // catch decks being empty in case
                            if (drawnCard == null) {
                                System.out.println("Both deck and discard pile are empty. Ending game.");
                                System.out.println("--------------------");
                                declareWinner();
                                return;
                            }
                            
                            // put cards in hand
                            player.receive(drawnCard);
                            System.out.println("You drew: " + drawnCard);
                        
                            // dont force players to play card, end turns after max draws reached even if player has cards
                            if (player.hasPlayableCard(topCard, declaredSuit)) {
                                System.out.println("You have a playable card in your hand.");
                            } 

                            System.out.println("--------------------");
                            break;
                        }
                        
                        // reprint hand
                        case "3": {
                            System.out.println("Player " + player.getName() + " has " + player.getHandSize() + " cards.");
                            player.printHand();
                            System.out.println("--------------------");
                            DeckStatus();
                            break;
                        }
                        
                        // invalid input
                        default: {
                            System.out.println("Invalid choice.");
                            break;
                        }
                    
                    } 
                    
                }
                
                // if player has no playable cards
                else {
                    System.out.println("Top card: " + topCard);
                    System.out.println(player.getName() + " has no playable cards in hand, select an option.");
                    System.out.println("0. Skip Turn");
                    System.out.println("1. Draw a card (max:" + MAX_DRAWS_PER_TURN + ", current draws done:" + drawsThisTurn + ")");
                    System.out.println("2. See hand and deck status ");
                    System.out.print("Enter choice (0, 1, 2): ");
                    String choice = scanner.nextLine();     
                    // choices
                    switch (choice) {
                        // skip turn
                        case "0": {
                            System.out.println(player.getName() + " skips their turn.");
                            System.out.println("--------------------");
                            turnDone = true;
                            break;
                        }
                        
                        // draw card
                        case "1": {
                            PlayingCard drawnCard = drawCard();
                            drawsThisTurn++;
            
                            // catch decks being empty in case
                            if (drawnCard == null) {
                                System.out.println("Both deck and discard pile are empty. Ending game.");
                                declareWinner();
                                return;
                            }
                            
                            // put cards in hand
                            player.receive(drawnCard);
                            System.out.println("You drew: " + drawnCard);
                            
                            // message to show if card is playable or not
                            if (player.hasPlayableCard(topCard, declaredSuit)) {
                                System.out.println("You now have a playable card.");
                            }
                            else if (drawsThisTurn >= MAX_DRAWS_PER_TURN) {
                                System.out.println("You have reached the maximum draws for this turn and have no playable cards. Skipping.");
                                turnDone = true;
                                break;
                            }
                            else {
                                System.out.println("No playable cards in hand, draw again.");
                            }
                                                        
                            System.out.println("--------------------");
                            break;
                        }  
                        
                        // reprint hand
                        case "2": {
                            System.out.println("Player " + player.getName() + " has " + player.getHandSize() + " cards.");
                            player.printHand();
                            System.out.println("--------------------");
                            DeckStatus();
                            break;
                        }
                        
                        // invalid input
                        default: {
                            System.out.println("Invalid choice.");
                            break;
                        }     
                    }  
                }
            }
            
            // line to break up turn text
            System.out.println("====================\n");
            
            
            // ends game once player hand is 0
            if (player.hasNoCards()) {
                declareWinner();
                break;
            }
            
            // check for player turn
            System.out.println();
            currentPlayerIndex = (currentPlayerIndex + 1) % getPlayers().size();
            turnNumber++;
            if (turnNumber > MAX_TURNS) {
                System.out.println("Turn limit: " + MAX_TURNS + " reached.");
                break;
            }
        }
    }
    
    
    //Returns the suit name for the given suit index.
    private String getSuitName(int suit) {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        return (suit >= 0 && suit < suits.length) ? suits[suit] : "Unknown";
    }
    
    // get status of deck
    private void DeckStatus() {
        System.out.println("Deck: " + deck.getCards().size() + " cards | "
        + "Discard pile: " + discardPile.size() + " cards");
    }
    
    /**
     * Prompts the active player to choose a suit after playing an 8.
     * @param scanner the shared Scanner
     * @param playerName the name of the player choosing
     * @return the chosen suit index (0=Hearts, 1=Diamonds, 2=Clubs, 3=Spades)
     */
    public int chooseSuit(Scanner scanner, String playerName) {
        System.out.println(playerName + " played an 8! Choose a suit:");
        System.out.println("0. Hearts");
        System.out.println("1. Diamonds");
        System.out.println("2. Clubs");
        System.out.println("3. Spades");
        System.out.print("Enter choice (0, 1, 2, 3): ");
        
        while(true) {
            int newSuit = Integer.parseInt(scanner.nextLine());
            switch (newSuit) {
                case 0: System.out.println("Suit declared: Hearts");   return 0;
                case 1: System.out.println("Suit declared: Diamonds"); return 1;
                case 2: System.out.println("Suit declared: Clubs");    return 2;
                case 3: System.out.println("Suit declared: Spades");   return 3;
                default:  System.out.println("Invalid choice. Try again.");
            }
            
        }
    }
    
    private PlayingCard drawCard() {
        PlayingCard card = dealOne();
        if (card != null) {
            return card;
        }
        // Deck is empty - try to reshuffle discard pile
        if (discardPile.isEmpty()) {
            System.out.println("Deck and Discard piles are both empty. Cannot draw.");
            return null;
        }
        System.out.println("Deck empty! Reshuffling discard pile...");
        deck.setCards(new ArrayList<Card>(discardPile));
        discardPile.clear();
        deck.shuffle();
        System.out.println("Reshuffled " + deck.getCards().size() + " back into deck.");
        return dealOne();
    }

    private PlayingCard dealOne() {
        ArrayList<Card> cards = deck.getCards();
        if (cards == null || cards.isEmpty()) {
            return null;
        }
        Card card = cards.remove(cards.size() - 1);
        if (card instanceof PlayingCard pc) {
            return pc;
        }
        return null;
    }

    protected void dealCards(int numCards) {
        for (int i = 0; i < numCards; i++) {
            for (Player p : getPlayers()) {
                ((CrazyEightsPlayer) p).receive(dealOne());
            }
        }
        System.out.println("Dealt " + numCards + " cards to each player");
    }

    @Override
    public void declareWinner() {
        System.out.println("\nGame Over");
        System.out.println("Turns played: " + turnNumber); // displayed turn
        System.out.println("\nFinal Hands:");
        // Find the player with the fewest cards
        CrazyEightsPlayer winner = null;
        int minCards = Integer.MAX_VALUE;
        for (Player p : getPlayers()) {
            CrazyEightsPlayer cp = (CrazyEightsPlayer) p;
            // print out card and hand info for all players before declaring a winner
            System.out.println(cp.getName() + ": " + cp.getHandSize());
            cp.printHand();
            System.out.println("====================");
            
            int handSize = cp.getHandSize();
            if (handSize < minCards) {
                minCards = handSize;
                winner = cp;
            }
        }
        if (winner != null) {
            System.out.println("Winner: " + winner.getName() + " with " + minCards + " cards left.");
        } 
        else {
            System.out.println("No winner.");
        }
    }
}
