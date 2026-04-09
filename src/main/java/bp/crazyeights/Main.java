package bp.crazyeights;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point for the Crazy Eights game.
 *
 * @author Cameron
 * @author Tamim
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("How many players? (Min: 2, Max: 12) ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine();
        // condition: player count must be between 2 and 4
        while (numPlayers < 2 || numPlayers > 12) {
            System.out.println("There must be a minimum of 2 players per game and a maximum of 12.");
            System.out.print("How many players? (");
            numPlayers = scanner.nextInt();
            scanner.nextLine();
        }

        // deckSize doubles for more than 6 players
        int deckSize = 52;
        if (numPlayers > 6) {
            deckSize = 104;
        }
        
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for player " + i + ": ");
            String name = scanner.nextLine();
            players.add(new CrazyEightsPlayer(name));
        }

        CrazyEightsGame game = new CrazyEightsGame(scanner, deckSize);
        game.setPlayers(players);
        game.play();

        scanner.close();
    }
}
