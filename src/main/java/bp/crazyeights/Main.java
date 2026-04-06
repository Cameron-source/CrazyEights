package bp.crazyeights;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point for the Crazy Eights game.
 *
 * @author Cameron - Apr 2026
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("How many players? ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine();
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for player " + i + ": ");
            String name = scanner.nextLine();
            players.add(new CrazyEightsPlayer(name));
        }
        scanner.close();

        CrazyEightsGame game = new CrazyEightsGame();
        game.setPlayers(players);
        game.play();
    }
}
