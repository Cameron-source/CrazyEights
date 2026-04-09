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

import java.util.ArrayList;
import java.util.Scanner;

public class CrazyEightsGame extends Game {
    private static final int MAX_TURNS = 100; // Safety limit to prevent infinite loops
    private static final int MAX_DRAWS_PER_TURN = 3; // Maximum cards a player can draw per turn
    protected final Deck deck = new Deck();
    protected PlayingCard topCard;  // The current top card of the discard pile
    protected final ArrayList<PlayingCard> discardPile = new ArrayList<>();  // Cards that have been played
    protected int declaredSuit = -1; // Active suit declared after an 8 is played (-1 = none)

    private int turnNumber = 1; // global turn counter
    private final Scanner scanner;
    private final int deckSize;

    public CrazyEightsGame(Scanner scanner, int deckSize) {
        super("Crazy Eights");
        this.scanner = scanner;
        this.deckSize = deckSize;
    }

    @Override
    public void play() {
        // Build the deck (supports multiple decks for larger groups)
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

        // Deal one card to the discard pile (re-draw if it's an 8)
        do {
            topCard = dealOne();
        } while (topCard != null && topCard.getRank() == 7);
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
            DeckStatus();
            player.printHand();

            boolean turnDone = false;
            int drawsThisTurn = 0;
            while (!turnDone) {
                System.out.println("------------------");
                System.out.println("Choose an option:");
                System.out.println("1. Play a card");
                System.out.println("2. Draw a card (max:" + MAX_DRAWS_PER_TURN + ", current draws done:" + drawsThisTurn + ")");
                System.out.println("3. See your hand");
                System.out.print("Enter choice (1, 2, 3): ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1": {
                        if (!player.hasPlayableCard(topCard, declaredSuit)) {
                            System.out.println("You have no playable cards.");
                            break;
                        }
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
                            if (playedCard.getRank() == 7) {
                                topCard = playedCard.chooseSuit(scanner);
                                declaredSuit = topCard.getSuit();
                            } else {
                                declaredSuit = -1;
                            }
                            turnDone = true;
                        } else {
                            System.out.println("Invalid choice.");
                        }
                        break;
                    }
                    case "2": {
                        if (drawsThisTurn >= MAX_DRAWS_PER_TURN) {
                            System.out.println("You have reached the maximum draws for this turn.");
                            break;
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
                        if (drawnCard.canPlayOn(topCard, declaredSuit)) {
                            System.out.println("You must play the drawn card!");
                            player.removeCard(drawnCard);
                            System.out.println(player.getName() + " plays: " + drawnCard);
                            if (topCard != null) {
                                discardPile.add(topCard);
                            }
                            topCard = drawnCard;
                            if (drawnCard.getRank() == 7) {
                                topCard = drawnCard.chooseSuit(scanner);
                                declaredSuit = topCard.getSuit();
                            } else {
                                declaredSuit = -1;
                            }
                            turnDone = true;
                        } else if (drawsThisTurn >= MAX_DRAWS_PER_TURN) {
                            System.out.println("No playable card after " + MAX_DRAWS_PER_TURN + " draws. Turn ends.");
                            turnDone = true;
                        }
                        break;
                    }
                    case "3": {
                        System.out.println("Player " + player.getName() + " has " + player.getHandSize() + " cards.");
                        player.printHand();
                        break;
                    }
                    default: {
                        System.out.println("Invalid choice.");
                        break;
                    }
                }
            }

            if (player.hasNoCards()) {
                System.out.println(player.getName() + " wins!");
                break;
            }
            
            System.out.println();
            currentPlayerIndex = (currentPlayerIndex + 1) % getPlayers().size();
            turnNumber++;
            if (turnNumber > MAX_TURNS) {
                System.out.println("Turn limit: " + MAX_TURNS + " reached.");
                break;
            }
        }
    }

    private void DeckStatus() {
        System.out.println("Deck: " + deck.getCards().size() + " cards | "
            + "Discard pile: " + discardPile.size() + " cards");
    }

    private PlayingCard drawCard() {
        PlayingCard card = dealOne();
        if (card != null) {
            return card;
        }
        if (discardPile.isEmpty()) {
            System.out.println("Deck and Discard piles are both empty. Cannot draw.");
            return null;
        }
        System.out.println("Deck empty! Reshuffling discard pile...");
        deck.setCards(new ArrayList<>(discardPile));
        discardPile.clear();
        deck.shuffle();
        System.out.println("Reshuffled " + deck.getSize() + " cards back into deck.");
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
        System.out.println("Turns played: " + turnNumber);
        System.out.println("\nFinal Hands:");
        CrazyEightsPlayer winner = null;
        int minCards = Integer.MAX_VALUE;
        for (Player p : getPlayers()) {
            CrazyEightsPlayer cp = (CrazyEightsPlayer) p;
            System.out.println(cp.getName() + ": " + cp.getHandSize() + " cards");
            cp.printHand();
            System.out.println("----------");
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
