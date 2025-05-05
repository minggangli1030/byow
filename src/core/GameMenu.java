package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GameMenu {
    private final int WIDTH;
    private final int HEIGHT;
    private final int SCALE = 16;

    // @source From https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html
    // I looked up Princeton StdDraw API 

    public GameMenu(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
        StdDraw.setCanvasSize(width * SCALE, height * SCALE);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.enableDoubleBuffering();
    }
    
    // Display main menu options with optional completion screen
    public Game displayMainMenu(boolean gameCompleted) {
        if (gameCompleted) {
            drawVictoryScreen();
            StdDraw.pause(3000); // Show victory screen for 3 seconds
        }
        
        drawMainMenuText();
        char choice = waitForMenuInput();

        if (choice == 'n') {
            // Go to seed input
            long seed = getSeedInput();
            return new Game(WIDTH, HEIGHT, seed);

        } else if (choice == 'l') {
            // Load previous game
            return loadSavedGame();

        } else {
            // Save and exit
            System.exit(0);
        }

        return null;
    }


    // Helper Methods

    public Game loadSavedGame() {
        try {
            Scanner sc = new Scanner(new File("save.txt"));
            long savedSeed = Long.parseLong(sc.nextLine());
            String actions = sc.nextLine();
            Game loadedGame = new Game(WIDTH, HEIGHT, savedSeed);
            loadedGame.loadInputHistory(actions);
            return loadedGame;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No saved game to load.");
        }
    }


    // Wait for player input on main menu
    private char waitForMenuInput() {
        while(true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                if (c == 'n' || c == 'l' || c == 'q') {
                    return c;
                }
            }
        }
    }


    // Draw game options in opening menu
    private void drawMainMenuText() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.yellow);
        StdDraw.setFont(new Font("Courier New", Font.BOLD, 60));
        StdDraw.text(this.WIDTH/2.0, this.HEIGHT/1.5, "CS61B: Coin Rush!");


        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Courier New", Font.BOLD, 30));
        StdDraw.text(this.WIDTH/2.0, this.HEIGHT/2.2, "(N) New Game");
        StdDraw.text(this.WIDTH/2.0, this.HEIGHT/2.8, "(L) Load Game");
        StdDraw.text(this.WIDTH/2.0, this.HEIGHT/3.8, "(Q) Quit Game");
        StdDraw.show();
    }
    
    // Draw victory screen when all coins are collected
    private void drawVictoryScreen() {
        StdDraw.clear(Color.black);
        
        // Draw congratulations message
        StdDraw.setPenColor(new Color(255, 215, 0)); // Gold color
        StdDraw.setFont(new Font("Courier New", Font.BOLD, 60));
        StdDraw.text(this.WIDTH/2.0, this.HEIGHT/1.5, "VICTORY!");
        
        StdDraw.setFont(new Font("Courier New", Font.BOLD, 30));
        StdDraw.text(this.WIDTH/2.0, this.HEIGHT/2.2, "You collected all the coins!");
        StdDraw.text(this.WIDTH/2.0, this.HEIGHT/2.8, "Congratulations!");
        
        // Draw coin symbols
        StdDraw.setFont(new Font("Courier New", Font.BOLD, 40));
        StdDraw.text(this.WIDTH/3.0, this.HEIGHT/3.8, "$");
        StdDraw.text(this.WIDTH/2.0, this.HEIGHT/3.8, "$");
        StdDraw.text(this.WIDTH/1.5, this.HEIGHT/3.8, "$");
        
        StdDraw.show();
    }

    // Turn player input into game seed for world generation
    private long getSeedInput() {
        String seedStr = "";
        while(true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                if (Character.isDigit(c)) {
                    seedStr += c;
                } else if (c == 's') {
                    break;
                }

                // Redraw screen with user input
                StdDraw.clear(Color.black);
                StdDraw.setPenColor(Color.white);
                StdDraw.text(this.WIDTH/2.0, this.HEIGHT/1.5, "Enter game seed followed by S...");
                StdDraw.setPenColor(Color.yellow);
                StdDraw.text(WIDTH / 2.0, HEIGHT/2.2, seedStr);
                StdDraw.show();
            }
        }

        return Long.parseLong(seedStr);
    }
}
