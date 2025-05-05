package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

import java.awt.*;

public class HUD {
    private final int WIDTH;
    private final int HEIGHT;
    private final TETile[][] tiles;
    private final World world;
    private final Vision vision;

    
    public HUD(int width, int height, TETile[][] tiles, World world, Vision vision){
        this.WIDTH = width;
        this.HEIGHT = height;
        this.tiles = tiles;
        this.world = world;
        this.vision = vision;
    }

    public void draw() {
        StdDraw.setPenColor(Color.red);
        StdDraw.setFont(new Font("Monospaced", Font.PLAIN, 16));
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        
        // Display tile under cursor
        if (mouseX >= 0 && mouseX < WIDTH && mouseY >= 0 && mouseY < HEIGHT) {
            String desc = tiles[mouseX][mouseY].description();
            StdDraw.textLeft(1, this.HEIGHT - 1, "Current Tile: " + desc);
        }
        
        // Show line of sight status if world reference is available
        if (world != null) {
            String losStatus = vision.isLineOfSightEnabled() ? "ON" : "OFF";
            StdDraw.textRight(this.WIDTH - 1, this.HEIGHT - 1, "Line of Sight: " + losStatus + " (L to toggle)");
            
            // Display coin count
            StdDraw.textLeft(1, this.HEIGHT - 2, "Coins: " + world.getCollectedCoins() + "/" + world.getTotalCoins());
        }
        
        StdDraw.show();
    }
}
