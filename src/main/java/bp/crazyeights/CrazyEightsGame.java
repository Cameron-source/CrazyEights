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
    private static final int MAX_DRAWS_PER_TURN = 3; // Maximum cards a player can draw per turn
    protected final GroupOfCards deck = new GroupOfCards(52);
    protected PlayingCard topCard;  // The current top card of the discard pile
    protected final ArrayList<PlayingCard> discardPile = new ArrayList<>();  // Cards that have been played

    public CrazyEightsGame() {
        super("Crazy Eights");
    }

    @Override
    public void play() {
        // Build the deck
        ArrayList<Card> cards = new ArrayList<>();
        for (int suit = 0; suit < 4; suit++) {
            for (int rank = 0; rank < 13; rank++) {
                cards.add(new PlayingCard(rank, suit));
            }
        }
        // Assign cards to deck
        try {
            java.lang.reflect.Field f = GroupOfCards.class.getDeclaredField("cards");
            f.setAccessible(true);
            f.set(deck, cards);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set deck cards", e);
        }
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
        int turnNumber = 1;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            CrazyEightsPlayer player = (CrazyEightsPlayer) getPlayers().get(currentPlayerIndex);
            System.out.println("Turn " + turnNumber + ": " + player.getName());
            System.out.println("Top card: " + topCard);
            player.printHand();

            boolean turnDone = false;
            int drawsThisTurn = 0;
            while (!turnDone) {
                if (player.hasPlayableCard(topCard)) {
                    System.out.println("You have a playable card. Choose an option:");
                    System.out.println("1. Play a card");
                    System.out.println("2. Draw a card (max " + MAX_DRAWS_PER_TURN + ")");
                    System.out.print("Enter choice (1 or 2): ");
                    String choice = scanner.nextLine();
                    if (choice.equals("1")) {
                        ArrayList<PlayingCard> playable = new ArrayList<>();
                        int idx = 1;
                        for (PlayingCard card : player.getHand()) {
                            if (card.canPlayOn(topCard)) {
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
                        if (drawnCard.canPlayOn(topCard)) {
                            System.out.println("You must play the drawn card!");
                            player.removeCard(drawnCard);
                            System.out.println(player.getName() + " plays: " + drawnCard);
                            if (topCard != null) {
                                discardPile.add(topCard);
                            }
                            topCard = drawnCard;
                            turnDone = true;
                        } else if (drawsThisTurn >= MAX_DRAWS_PER_TURN) {
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
                        if (drawnCard.canPlayOn(topCard)) {
                            System.out.println("You must play the drawn card!");
                            player.removeCard(drawnCard);
                            System.out.println(player.getName() + " plays: " + drawnCard);
                            if (topCard != null) {
                                discardPile.add(topCard);
                            }
                            topCard = drawnCard;
                            turnDone = true;
                        }
                    }
                    if (!turnDone) {
                        System.out.println("No playable card after " + MAX_DRAWS_PER_TURN + " draws. Turn ends.");
                        turnDone = true;
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
                System.out.println("Game ended.");
                break;
            }
        }
        scanner.close();
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
        try {
            java.lang.reflect.Field f = GroupOfCards.class.getDeclaredField("cards");
            f.setAccessible(true);
            f.set(deck, cardsToShuffle);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to reshuffle deck", e);
        }
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("How many players? ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine(); // consume newline
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for player " + i + ": ");
            String name = scanner.nextLine();
            players.add(new CrazyEightsPlayer(name));
        }
        CrazyEightsGame game = new CrazyEightsGame();
        game.setPlayers(players);
        game.play();
        scanner.close();
    }
}
