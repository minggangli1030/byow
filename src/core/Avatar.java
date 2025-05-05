package core;

import tileengine.TETile;
import tileengine.Tileset;

/**
 * Represents the player's avatar in the world.
 * Handles avatar position, movement, and interaction with the world.
 */
public class Avatar {
    private int x;
    private int y;
    private TETile avatarTile;
    private TETile originalTile;
    private World worldRef; // Reference to the world for coin collection

    /**
     * Creates a new avatar at the specified position with a world reference.
     * 
     * @param x The x-coordinate to place the avatar
     * @param y The y-coordinate to place the avatar
     * @param world The world grid to place the avatar in
     * @param worldRef Reference to the world object
     */
    public Avatar(int x, int y, TETile[][] world, World worldRef) {
        this.x = x;
        this.y = y;
        this.avatarTile = Tileset.AVATAR;
        this.originalTile = world[x][y];
        this.worldRef = worldRef;
        world[x][y] = avatarTile;  // Place avatar in world
    }

    /**
     * Attempts to move the avatar up one square.
     * 
     * @param world The world grid
     * @return true if the move was successful, false otherwise
     */
    public boolean moveUp(TETile[][] world) {
        if (isValidMove(x, y + 1, world)) {
            updatePosition(x, y + 1, world);
            return true;
        }
        return false;
    }

    /**
     * Attempts to move the avatar down one square.
     * 
     * @param world The world grid
     * @return true if the move was successful, false otherwise
     */
    public boolean moveDown(TETile[][] world) {
        if (isValidMove(x, y - 1, world)) {
            updatePosition(x, y - 1, world);
            return true;
        }
        return false;
    }

    /**
     * Attempts to move the avatar left one square.
     * 
     * @param world The world grid
     * @return true if the move was successful, false otherwise
     */
    public boolean moveLeft(TETile[][] world) {
        if (isValidMove(x - 1, y, world)) {
            updatePosition(x - 1, y, world);
            return true;
        }
        return false;
    }

    /**
     * Attempts to move the avatar right one square.
     * 
     * @param world The world grid
     * @return true if the move was successful, false otherwise
     */
    public boolean moveRight(TETile[][] world) {
        if (isValidMove(x + 1, y, world)) {
            updatePosition(x + 1, y, world);
            return true;
        }
        return false;
    }

    /**
     * Checks if a move to the specified coordinates is valid.
     * 
     * @param newX The x-coordinate to check
     * @param newY The y-coordinate to check
     * @param world The world grid
     * @return true if the move is valid, false otherwise
     */
    private boolean isValidMove(int newX, int newY, TETile[][] world) {
        // Check if within bounds
        if (newX < 0 || newX >= world.length || newY < 0 || newY >= world[0].length) {
            return false;
        }
        
        // Check if not a wall
        return world[newX][newY] != Tileset.WALL;
    }

    /**
     * Updates the avatar's position in the world.
     * 
     * @param newX The new x-coordinate
     * @param newY The new y-coordinate
     * @param world The world grid
     */
    private void updatePosition(int newX, int newY, TETile[][] world) {
        // Restore original tile at current position
        world[x][y] = originalTile;
        
        // Check if moving onto a coin
        boolean collectingCoin = world[newX][newY] == Tileset.COIN;
        
        // Report coin collection BEFORE replacing the coin
        if (collectingCoin && worldRef != null) {
            worldRef.collectCoin();
        }
        
        // Save what's at the new position
        originalTile = world[newX][newY];
        
        // If collecting a coin, change the tile to floor in the world
        if (collectingCoin) {
            world[newX][newY] = Tileset.FLOOR;
            originalTile = Tileset.FLOOR; // Update original tile to floor too
        }
        
        // Update position
        x = newX;
        y = newY;
        world[x][y] = avatarTile;
    }

    /**
     * Gets the current x-coordinate of the avatar.
     * 
     * @return The current x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the current y-coordinate of the avatar.
     * 
     * @return The current y-coordinate
     */
    public int getY() {
        return y;
    }
} 