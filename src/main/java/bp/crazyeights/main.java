/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bp.crazyeights;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Cameron
 * @author Tamim
 */
public class Main {
    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in); // new scanner, scanner here instead of game
        System.out.print("How many players? (Min: 2, Max: 12)");
        int numPlayers = scanner.nextInt();
        scanner.nextLine(); 
        // condition: player count must be between 2 or 12
        while (numPlayers < 2 || numPlayers > 12) {
            System.out.println("There must be a minimum of 2 players per game and a maximum of 12.");
            System.out.print("How many players? (Min: 2, Max: 12)");
            numPlayers = scanner.nextInt();
            scanner.nextLine(); 
        }
        
        // set deckSize here, decksize changes based on the amount of players added
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
        
        CrazyEightsGame game = new CrazyEightsGame(scanner, deckSize); // send scanner to CrazyEightsGame
        
        game.setPlayers(players);
        game.play();
        
        scanner.close();
        
    }
}
