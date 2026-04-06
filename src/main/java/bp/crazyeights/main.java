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
public class main {
    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in); // new scanner, scanner here instead of game
        
        System.out.print("How many players? ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine(); 
        
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for player " + i + ": ");
            String name = scanner.nextLine();
            players.add(new CrazyEightsPlayer(name));
        }
        
        CrazyEightsGame game = new CrazyEightsGame(scanner); // send scanner to CrazyEightsGame
        
        game.setPlayers(players);
        game.play();
        
        scanner.close();
        
    }
    
    
}
