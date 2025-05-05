package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Color;
import java.awt.Font;


/**
 * Main game class that handles the game loop, input processing, and rendering.
 */
public class Game {
    private World world;
    private Avatar avatar;
    private HUD hud;
    private TERenderer ter;
    private boolean gameRunning;
    private boolean gameCompleted;
    private final int WIDTH;
    private final int HEIGHT;
    private StringBuilder inputHistory = new StringBuilder();
    private boolean colonPressed = false;
    private long SEED;
    private final Vision vision;

    /**
     * Creates a new game with the specified dimensions and seed.
     *
     * @param width The width of the game world
     * @param height The height of the game world
     * @param seed The seed for world generation
     */
    public Game(int width, int height, long seed) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.world = new World(width, height);
        this.vision = new Vision(world.getWorldGrid(), width, height);
        this.ter = new TERenderer();
        this.SEED = seed;
        this.gameCompleted = false;

        // Initialize the world with the provided seed
        world.generateWorld(seed);

        // Place the avatar at the center-most floor tile
        this.avatar = placeAvatarAtCenterMost(world.getWorldGrid(), world);

        // Set avatar reference in world for line of sight updates
        vision.setAvatar(this.avatar);

        // Initialize visibility for first frame
        vision.updateVisibility();

        // Initialize HUD with world reference for line of sight display
        this.hud = new HUD(width, height, world.getWorldGrid(), world, vision);

        // Initialize the renderer
        ter.initialize(WIDTH, HEIGHT);
    }

    /**
     * Starts the game loop.
     * @return true if the game was completed (all coins collected), false otherwise
     */
    public boolean run() {
        gameRunning = true;

        while (gameRunning) {
            // Process user input
            if (StdDraw.hasNextKeyTyped()) {
                processKeypress(StdDraw.nextKeyTyped());
            }

            // Refresh visibility each frame when LOS is on
            if (vision.isLineOfSightEnabled()) {
                vision.updateVisibility();
            }

            // Render the world with line of sight if enabled
            if (vision.isLineOfSightEnabled()) {
                ter.renderFrameWithFog(world.getWorldGrid(),
                                      vision.getVisibilityGrid(),
                                      vision.getExploredGrid(),
                                      true);
            } else {
                ter.renderFrame(world.getWorldGrid());
            }

            // Display HUD
            hud.draw();

            // Check for game completion
            if (world.allCoinsCollected() && !gameCompleted) {
                gameCompleted = true;
                drawCompletionMessage();
                StdDraw.pause(2000); // Show message for 2 seconds
                return true;
            }

            // Small pause to avoid excessive CPU usage
            StdDraw.pause(10);
        }

        return false; // Game was not completed
    }

    /**
     * Draws a completion message when all coins are collected.
     */
    private void drawCompletionMessage() {
        // Save the current font and color
        Font currentFont = StdDraw.getFont();
        Color currentColor = StdDraw.getPenColor();

        // Draw completion message
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.yellow);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(WIDTH/2.0, HEIGHT/2.0 + 5, "Congratulations!");
        StdDraw.text(WIDTH/2.0, HEIGHT/2.0, "All coins collected!");
        StdDraw.text(WIDTH/2.0, HEIGHT/2.0 - 5, "Returning to menu...");
        StdDraw.show();

        // Restore the original font and color
        StdDraw.setFont(currentFont);
        StdDraw.setPenColor(currentColor);
    }

    // Helper Methods

    public void loadInputHistory(String actions) {
        for (char c : actions.toCharArray()) {
            // Skip ":Q" if it exists at the end
            if (c == ':' || c == 'Q' || c == 'q') {
                continue;
            }
            processKeypress(c);
        }
    }

    /**
     * Processes a keypress and updates the game state accordingly.
     *
     * @param key The key that was pressed
     */
    private void processKeypress(char key) {

        if (colonPressed && (key == 'q' || key == 'Q')) {
            inputHistory.append(":Q");
            saveToFile(this.SEED, inputHistory.toString());
            System.exit(0);
        }

        if (key == ':') {
            colonPressed = true;
            return;
        } else {
            colonPressed = false;
            inputHistory.append(key);
        }

        boolean moved = false;

        switch (key) {
            case 'w':
            case 'W':
                moved = avatar.moveUp(world.getWorldGrid());
                break;
            case 's':
            case 'S':
                moved = avatar.moveDown(world.getWorldGrid());
                break;
            case 'a':
            case 'A':
                moved = avatar.moveLeft(world.getWorldGrid());
                break;
            case 'd':
            case 'D':
                moved = avatar.moveRight(world.getWorldGrid());
                break;
            case 'l':
            case 'L':
                vision.toggleLineOfSight();
                break;
        }

        // If the avatar moved and line of sight is enabled, update visibility
        if (moved && vision.isLineOfSightEnabled()) {
            vision.updateVisibility();
        }
    }

    private void saveToFile(long worldSeed, String input) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("save.txt"))) {
            writer.write(worldSeed + "\n" + input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Places the avatar at the floor tile closest to the center of the world.
     * If there's a tie, it chooses the one in the bottom right.
     * 
     * @param worldGrid The world grid
     * @param world Reference to the world object for coin collection
     * @return A new Avatar positioned at the center-most floor tile
     */
    private Avatar placeAvatarAtCenterMost(TETile[][] worldGrid, World world) {
        int centerX = worldGrid.length / 2;
        int centerY = worldGrid[0].length / 2;
        
        int closestDistance = Integer.MAX_VALUE;
        int bestX = -1;
        int bestY = -1;
        
        // Find the floor tile closest to the center
        for (int x = 0; x < worldGrid.length; x++) {
            for (int y = 0; y < worldGrid[0].length; y++) {
                if (worldGrid[x][y] == Tileset.FLOOR) {
                    int distance = Math.abs(x - centerX) + Math.abs(y - centerY);
                    
                    // If this tile is closer to center than our current best
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        bestX = x;
                        bestY = y;
                    } 
                    // If this tile is equally close to center but is more bottom-right
                    else if (distance == closestDistance && (x > bestX || (x == bestX && y < bestY))) {
                        bestX = x;
                        bestY = y;
                    }
                }
            }
        }
        
        if (bestX == -1 || bestY == -1) {
            throw new RuntimeException("No floor tiles found to place avatar");
        }
        
        return new Avatar(bestX, bestY, worldGrid, world);
    }

}