package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class responsible for generating hallways between rooms.
 * Uses a minimum spanning tree algorithm (Kruskal's) to ensure
 * all rooms are connected with minimal total hallway length.
 */
public class HallwayGenerator {
    private final TETile[][] worldGrid;
    private final List<Room> rooms;

    /**
     * Constructor for the HallwayGenerator.
     * @param worldGrid The 2D grid representing the world
     * @param rooms List of rooms that need to be connected
     */
    public HallwayGenerator(TETile[][] worldGrid, List<Room> rooms) {
        this.worldGrid = worldGrid;
        this.rooms = rooms;
    }

    /**
     * Connects all rooms with hallways using Kruskal's minimum spanning tree algorithm.
     */
    public void connectRoomsWithHallways() {
        // 1. Create a disjoint set with one set per room
        DisjointSet ds = new DisjointSet(rooms.size());
        
        // 2. Create a list of all possible edges (connections between rooms)
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                Room roomA = rooms.get(i);
                Room roomB = rooms.get(j);
                int distance = calculateManhattanDistance(roomA, roomB);
                edges.add(new Edge(i, j, distance));
            }
        }
        
        // 3. Sort edges by distance (ascending)
        Collections.sort(edges, Comparator.comparingInt(e -> e.distance));
        
        // 4. Process edges in order of increasing distance
        for (Edge edge : edges) {
            int i = edge.from;
            int j = edge.to;
            
            // If the rooms are not already connected
            if (!ds.isConnected(i, j)) {
                // Connect rooms with a hallway
                createHallway(rooms.get(i), rooms.get(j));
                
                // Merge the sets
                ds.union(i, j);
                
                // Optional: If all rooms are connected, we can stop
                if (ds.isAllConnected()) {
                    break;
                }
            }
        }
    }

    /**
     * Calculates Manhattan distance between two rooms.
     * @param a First room
     * @param b Second room
     * @return Manhattan distance between room centers
     */
    private int calculateManhattanDistance(Room a, Room b) {
        // Calculate distance between room centers
        int centerAX = a.getStartX() + a.getWidth() / 2;
        int centerAY = a.getStartY() + a.getHeight() / 2;
        int centerBX = b.getStartX() + b.getWidth() / 2;
        int centerBY = b.getStartY() + b.getHeight() / 2;
        
        return Math.abs(centerAX - centerBX) + Math.abs(centerAY - centerBY);
    }

    /**
     * Creates a hallway between two rooms.
     * @param roomA First room
     * @param roomB Second room
     */
    private void createHallway(Room roomA, Room roomB) {
        // Find suitable connection points
        int[] pointA = findConnectionPoint(roomA, roomB);
        int[] pointB = findConnectionPoint(roomB, roomA);
        
        // Create L-shaped hallway
        createLShapedHallway(pointA[0], pointA[1], pointB[0], pointB[1]);
    }

    /**
     * Finds a suitable connection point on the perimeter of a room.
     * @param from The source room
     * @param to The destination room
     * @return [x, y] coordinates of the connection point
     */
    private int[] findConnectionPoint(Room from, Room to) {
        // Calculate centers of both rooms
        int fromCenterX = from.getStartX() + from.getWidth() / 2;
        int fromCenterY = from.getStartY() + from.getHeight() / 2;
        int toCenterX = to.getStartX() + to.getWidth() / 2;
        int toCenterY = to.getStartY() + to.getHeight() / 2;
        
        // Determine which side of the "from" room to connect from based on relative positions
        int x, y;
        
        // Determine if connection should be from horizontal or vertical wall
        boolean connectFromHorizontalWall = Math.abs(toCenterX - fromCenterX) > Math.abs(toCenterY - fromCenterY);
        
        if (connectFromHorizontalWall) {
            // Connect from left or right wall
            if (toCenterX < fromCenterX) {
                // Connect from left wall
                x = from.getStartX();
            } else {
                // Connect from right wall
                x = from.getStartX() + from.getWidth() - 1;
            }
            // Y-coordinate somewhere along the wall
            y = fromCenterY;
        } else {
            // Connect from top or bottom wall
            if (toCenterY < fromCenterY) {
                // Connect from bottom wall
                y = from.getStartY();
            } else {
                // Connect from top wall
                y = from.getStartY() + from.getHeight() - 1;
            }
            // X-coordinate somewhere along the wall
            x = fromCenterX;
        }
        
        return new int[]{x, y};
    }

    /**
     * Creates an L-shaped hallway between two points.
     * @param x1 X-coordinate of first point
     * @param y1 Y-coordinate of first point
     * @param x2 X-coordinate of second point
     * @param y2 Y-coordinate of second point
     */
    private void createLShapedHallway(int x1, int y1, int x2, int y2) {
        // Decide on the corner point (where the hallway turns)
        int cornerX = x1;
        int cornerY = y2;
        
        // Create first segment (from first point to corner)
        for (int x = Math.min(x1, cornerX); x <= Math.max(x1, cornerX); x++) {
            worldGrid[x][y1] = Tileset.FLOOR;
        }
        
        // Create second segment (from corner to second point)
        for (int y = Math.min(y1, cornerY); y <= Math.max(y1, cornerY); y++) {
            worldGrid[cornerX][y] = Tileset.FLOOR;
        }
        
        // Create third segment if needed (from corner to second point along X-axis)
        for (int x = Math.min(cornerX, x2); x <= Math.max(cornerX, x2); x++) {
            worldGrid[x][cornerY] = Tileset.FLOOR;
        }
    }

    /**
     * Adds walls around all hallways.
     */
    public void addWallsAroundHallways() {
        int width = worldGrid.length;
        int height = worldGrid[0].length;
        
        // Create a temporary copy of the world to avoid adding walls that would
        // be overwritten by floors
        TETile[][] tempWorld = new TETile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tempWorld[x][y] = worldGrid[x][y];
            }
        }
        
        // For each tile in the world
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // If it's a floor tile
                if (worldGrid[x][y] == Tileset.FLOOR) {
                    // Check all 8 surrounding tiles
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            // Skip the center tile
                            if (dx == 0 && dy == 0) {
                                continue;
                            }
                            
                            int nx = x + dx;
                            int ny = y + dy;
                            
                            // Ensure we're within bounds
                            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                                // If the tile is nothing (not a floor tile and not already a wall)
                                if (worldGrid[nx][ny] == Tileset.NOTHING) {
                                    tempWorld[nx][ny] = Tileset.WALL;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Update the world with the walls
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                worldGrid[x][y] = tempWorld[x][y];
            }
        }
    }

    /**
     * Helper class to represent a connection between two rooms.
     */
    private static class Edge {
        int from, to, distance;
        
        public Edge(int from, int to, int distance) {
            this.from = from;
            this.to = to;
            this.distance = distance;
        }
    }
} 