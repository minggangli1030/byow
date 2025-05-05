package core;

import tileengine.TETile;
import tileengine.Tileset;

/**
 * Encapsulates line-of-sight and fog-of-war visibility logic.
 */
public class Vision {
    private final int WIDTH, HEIGHT;
    private final TETile[][] worldGrid;
    private final boolean[][] visibilityGrid;
    private final boolean[][] exploredGrid;
    private boolean lineOfSightEnabled = false;
    private Avatar avatar;

    public Vision(TETile[][] worldGrid, int width, int height) {
        this.worldGrid = worldGrid;
        this.WIDTH = width;
        this.HEIGHT = height;
        this.visibilityGrid = new boolean[width][height];
        this.exploredGrid = new boolean[width][height];
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public boolean isLineOfSightEnabled() {
        return lineOfSightEnabled;
    }

    public boolean[][] getVisibilityGrid() {
        return visibilityGrid;
    }

    public boolean[][] getExploredGrid() {
        return exploredGrid;
    }

    public void toggleLineOfSight() {
        lineOfSightEnabled = !lineOfSightEnabled;
        if (lineOfSightEnabled) {
            clearGrids();
            updateVisibility();
        } else {
            // reveal all
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    visibilityGrid[x][y] = true;
                    exploredGrid[x][y] = true;
                }
            }
        }
    }

    private void clearGrids() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                visibilityGrid[x][y] = false;
                exploredGrid[x][y] = false;
            }
        }
    }

    public void updateVisibility() {
        if (avatar == null || !lineOfSightEnabled) return;
        clearGrids();
        int ax = avatar.getX(), ay = avatar.getY();
        visibilityGrid[ax][ay] = true;
        exploredGrid[ax][ay] = true;
        int visionRadius = 8;
        for (int deg = 0; deg < 360; deg++) {
            castRay(ax, ay, Math.toRadians(deg), visionRadius);
        }
    }

    private void castRay(int startX, int startY, double angle, int maxDistance) {
        double dx = Math.cos(angle), dy = Math.sin(angle);
        double x0 = startX + 0.5, y0 = startY + 0.5;
        double step = 0.2;
        for (double dist = step; dist <= maxDistance; dist += step) {
            double x = x0 + dx * dist, y = y0 + dy * dist;
            int xi = (int) Math.floor(x), yi = (int) Math.floor(y);
            if (xi < 0 || xi >= WIDTH || yi < 0 || yi >= HEIGHT) break;
            visibilityGrid[xi][yi] = true;
            exploredGrid[xi][yi] = true;
            if (worldGrid[xi][yi] == Tileset.WALL) break;
        }
    }
}
