/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bp.crazyeights;

/**
 *
 * @author Cameron - Apr 2026
 */

import java.util.ArrayList;
import java.util.Scanner;
public class CrazyEightsGame extends Game {
    private static final int MAX_TURNS = 1000; // Safety limit to prevent infinite loops
    private static final int MAX_DRAWS_PER_TURN = 3; // Maximum cards a player can draw per turn
    protected final Deck deck = new Deck();
    protected PlayingCard topCard;  // The current top card of the discard pile
    protected final ArrayList<PlayingCard> discardPile = new ArrayList<>();  // Cards that have been played
    protected int declaredSuit = -1; // Active suit declared after an 8 is played (-1 = none)

    public CrazyEightsGame() {
        super("Crazy Eights");
    }

    @Override
    public void play() {
        Scanner scanner = new Scanner(System.in);

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

        // Deal one card to the discard pile (re-draw if it's an 8)
        do {
            topCard = dealOne();
        } while (topCard != null && topCard.getRank() == 7);
        System.out.println("\nTop card: " + topCard);

        // Play the game (winner is declared inside playGame)
        playGame(scanner);
        scanner.close();
    }

    /**
     * Main game loop - players take turns playing cards or drawing from the deck.
     * @param scanner the shared Scanner reading from System.in
     */
    protected void playGame(Scanner scanner) {
        System.out.println("\nGame Started\n");
        int currentPlayerIndex = 0;
        int turnNumber = 1;

        while (true) {
            CrazyEightsPlayer player = (CrazyEightsPlayer) getPlayers().get(currentPlayerIndex);
            System.out.println("Turn " + turnNumber + ": " + player.getName());
            if (declaredSuit != -1) {
                System.out.println("Active declared suit: " + getSuitName(declaredSuit));
            }
            System.out.println("Top card: " + topCard);
            player.printHand();

            boolean turnDone = false;
            int drawsThisTurn = 0;
            while (!turnDone) {
                if (player.hasPlayableCard(topCard, declaredSuit)) {
                    System.out.println("You have a playable card. Choose an option:");
                    System.out.println("1. Play a card");
                    System.out.println("2. Draw a card (max " + MAX_DRAWS_PER_TURN + ")");
                    System.out.print("Enter choice (1 or 2): ");
                    String choice = scanner.nextLine();
                    if (choice.equals("1")) {
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
                            // If an 8 was played, ask the player to declare a suit
                            if (playedCard.getRank() == 7) {
                                declaredSuit = chooseSuit(scanner, player.getName());
                            } else {
                                declaredSuit = -1;
                            }
                            turnDone = true;
                        } else {
                            System.out.println("Invalid choice.");
                        }
                    } else if (choice.equals("2")) {
                        if (drawsThisTurn >= MAX_DRAWS_PER_TURN) {
                            System.out.println("You have reached the maximum draws for this turn.");
                            continue;
                        }
                        PlayingCard drawnCard = drawCard();
                        drawsThisTurn++;
                        if (drawnCard == null) {
                            System.out.println("Deck empty. Game ends.");
                            turnDone = true;
                            break;
                        }
                        player.receive(drawnCard);
                        System.out.println("You drew: " + drawnCard);
                        if (drawsThisTurn >= MAX_DRAWS_PER_TURN) {
                            System.out.println("No playable card after " + MAX_DRAWS_PER_TURN + " draws. Turn ends.");
                            turnDone = true;
                        }
                    } else {
                        System.out.println("Invalid choice.");
                    }
                } else {
                    // No playable cards, must draw up to MAX_DRAWS_PER_TURN
                    while (drawsThisTurn < MAX_DRAWS_PER_TURN && !turnDone) {
                        System.out.println("No playable cards. Drawing...");
                        PlayingCard drawnCard = drawCard();
                        drawsThisTurn++;
                        if (drawnCard == null) {
                            System.out.println("Deck empty. Game ends.");
                            turnDone = true;
                            break;
                        }
                        player.receive(drawnCard);
                        System.out.println("You drew: " + drawnCard);
                        if (drawnCard.canPlayOn(topCard, declaredSuit)) {
                            turnDone = true; // player now has a playable card, loop back for their choice
                        }
                    }
                    if (!turnDone) {
                        System.out.println("No playable card after " + MAX_DRAWS_PER_TURN + " draws. Turn ends.");
                        turnDone = true;
                    }
                }
            }

            if (player.hasNoCards()) {
                declareWinner();
                break;
            }
            System.out.println();
            currentPlayerIndex = (currentPlayerIndex + 1) % getPlayers().size();
            turnNumber++;
            if (turnNumber > MAX_TURNS) {
                System.out.println("Game ended after " + MAX_TURNS + " turns.");
                declareWinner();
                break;
            }
        }
    }

    /**
     * Prompts the active player to choose a suit after playing an 8.
     * @param scanner the shared Scanner
     * @param playerName the name of the player choosing
     * @return the chosen suit index (0=Hearts, 1=Diamonds, 2=Clubs, 3=Spades)
     */
    private int chooseSuit(Scanner scanner, String playerName) {
        System.out.println(playerName + " played an 8! Choose a suit:");
        System.out.println("1. Hearts");
        System.out.println("2. Diamonds");
        System.out.println("3. Clubs");
        System.out.println("4. Spades");
        while (true) {
            System.out.print("Enter choice (1-4): ");
            String input = scanner.nextLine();
            switch (input) {
                case "1": System.out.println("Suit declared: Hearts");   return 0;
                case "2": System.out.println("Suit declared: Diamonds"); return 1;
                case "3": System.out.println("Suit declared: Clubs");    return 2;
                case "4": System.out.println("Suit declared: Spades");   return 3;
                default:  System.out.println("Invalid choice. Try again.");
            }
        }
    }

    /**
     * Returns the suit name for the given suit index.
     */
    private String getSuitName(int suit) {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        return (suit >= 0 && suit < suits.length) ? suits[suit] : "Unknown";
    }

    private PlayingCard drawCard() {
        PlayingCard card = dealOne();
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
        deck.setCards(cardsToShuffle); // Deck.setCards() replaces the card list
        deck.shuffle();
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
        // Find the player with the fewest cards
        CrazyEightsPlayer winner = null;
        int minCards = Integer.MAX_VALUE;
        for (Player p : getPlayers()) {
            CrazyEightsPlayer cp = (CrazyEightsPlayer) p;
            int handSize = cp.getHandSize();
            if (handSize < minCards) {
                minCards = handSize;
                winner = cp;
            }
        }
        if (winner != null) {
            System.out.println("Winner: " + winner.getName() + " with " + minCards + " cards left.");
        } else {
            System.out.println("No winner.");
        }
    }

    
}
