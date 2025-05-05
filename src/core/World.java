package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {

    private final int WIDTH;
    private final int HEIGHT;
    private final int MARGIN; // Min distance between room and world boarder

    private final TETile[][] worldGrid;
    private Random randomGenerator;

    private final int desiredRoomCount = 20;

    private final int minRoomHeight = 4;
    private final int maxRoomHeight = 12;
    private final int minRoomWidth = 3;
    private final int maxRoomWidth = 9;

    private final List<Room> rooms;

    // Default coin count
    private final int totalCoinCount = 10;


    // Coin related fields
    private final Coin coin;

    public World(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.MARGIN = 2;
        this.worldGrid = new TETile[this.WIDTH][this.HEIGHT];
        this.randomGenerator = null;
        this.rooms = new ArrayList<>();
        this.coin = new Coin(this.worldGrid, this.WIDTH, this.HEIGHT, totalCoinCount);
    }

    // Main method
    public void generateWorld(long SEED) {
        this.randomGenerator = new Random(SEED);
        initializeGrid();
        placeRooms();
        connectRooms();
        addWalls();

        // Place coins after the world is generated
        coin.placeCoins(randomGenerator);
    }

    // World construction:

    // Set up empty world grid
    public void initializeGrid() {
        for (int x = 0; x < this.WIDTH; x++) {
            for (int y = 0; y < this.HEIGHT; y++) {
                this.worldGrid[x][y] = Tileset.NOTHING;
            }
        }
    }

    // Place pseudorandom rooms in world grid
    public void placeRooms() {
        int roomCount = 0;
        while(roomCount < desiredRoomCount) {
            int roomWidth = RandomUtils.uniform(randomGenerator, minRoomWidth, maxRoomWidth);
            int roomHeight = RandomUtils.uniform(randomGenerator, minRoomHeight, maxRoomHeight);
            int startX = randomGenerator.nextInt(this.WIDTH - roomWidth - this.MARGIN);
            int startY = randomGenerator.nextInt(this.HEIGHT - roomHeight - this.MARGIN);
            Room newRoom = new Room(startX, startY, roomWidth, roomHeight);
            // Use isValidRoom method to check if the room can be placed.
            if (isValidRoom(newRoom)) {
                rooms.add(newRoom);
                markRoomTiles(newRoom);  // Update the grid with the room's tiles
                roomCount++;
            }
        }
    }

    // Connect rooms with hallways
    public void connectRooms() {
        if (rooms.isEmpty()) {
            return;
        }

        HallwayGenerator hallwayGenerator = new HallwayGenerator(worldGrid, rooms);
        hallwayGenerator.connectRoomsWithHallways();
    }

    // Add walls around rooms and hallways
    public void addWalls() {
        HallwayGenerator hallwayGenerator = new HallwayGenerator(worldGrid, rooms);
        hallwayGenerator.addWallsAroundHallways();
    }


    public void collectCoin() {
        coin.collectCoin();
    }

    public boolean allCoinsCollected() {
        return coin.allCoinsCollected();
    }

    public int getTotalCoins() {
        return coin.getTotalCoins();
    }

    public int getCollectedCoins() {
        return coin.getCollectedCoins();
    }

    // Helper Methods:

    // Check if newRoom is valid in the current world context
    public boolean isValidRoom(Room newRoom) {
        // First, check if the room is within the valid boundaries.
        if (newRoom.getStartX() < MARGIN ||
                newRoom.getStartY() < MARGIN ||
                newRoom.getStartX() + newRoom.getWidth() > WIDTH - MARGIN ||
                newRoom.getStartY() + newRoom.getHeight() > HEIGHT - MARGIN) {
            return false;
        }

        // Now, check against each already-placed room for overlap.
        for (Room existingRoom : rooms) {
            if (newRoom.overlapsWith(existingRoom)) {
                return false;
            }
        }
        return true;
    }

    // Place RoomTiles in valid newRoom
    private void markRoomTiles(Room newRoom) {
        for (int x = newRoom.getStartX(); x <= newRoom.getStartX() + newRoom.getWidth() - 1; x++) {
            for (int y = newRoom.getStartY(); y <= newRoom.getStartY() + newRoom.getHeight() - 1; y++) {
                worldGrid[x][y] = Tileset.FLOOR;
            }
        }
    }
    
    /**
     * Gets the world grid.
     * 
     * @return The 2D array representing the world
     */
    public TETile[][] getWorldGrid() {
        return worldGrid;
    }
}
