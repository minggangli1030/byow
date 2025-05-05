package core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import tileengine.TETile;
import tileengine.Tileset;

/**
 * Manages coin placement and tracking in the world.
 */
public class Coin {
    private int totalCoins = 0;
    private int collectedCoins = 0;
    private final int totalCoinCount;
    private final TETile[][] worldGrid;
    private final int width;
    private final int height;

    public Coin(TETile[][] worldGrid, int width, int height, int totalCoinCount) {
        this.worldGrid = worldGrid;
        this.width = width;
        this.height = height;
        this.totalCoinCount = totalCoinCount;
    }

    public void placeCoins(Random rand) {
        List<Point> floorTiles = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (worldGrid[x][y] == Tileset.FLOOR) {
                    floorTiles.add(new Point(x, y));
                }
            }
        }
        if (floorTiles.isEmpty()) {
            return;
        }
        Collections.shuffle(floorTiles, rand);
        int placed = 0;
        int centerX = width / 2;
        int centerY = height / 2;
        for (Point p : floorTiles) {
            if (placed >= totalCoinCount) break;
            if (Math.abs(p.x - centerX) < 5 && Math.abs(p.y - centerY) < 5) {
                continue;
            }
            worldGrid[p.x][p.y] = Tileset.COIN;
            placed++;
        }
        totalCoins = placed;
    }

    public void collectCoin() {
        collectedCoins++;
    }

    public boolean allCoinsCollected() {
        return collectedCoins >= totalCoins && totalCoins > 0;
    }

    public int getTotalCoins() {
        return totalCoins;
    }

    public int getCollectedCoins() {
        return collectedCoins;
    }
}
