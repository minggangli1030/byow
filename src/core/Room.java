package core;

public class Room {
    private int startX;
    private int startY;
    private int width;
    private int height;

    public Room(int startX, int startY, int width, int height) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
    }


    // Check if two rooms are overlapped
    public boolean overlapsWith(Room other) {
        int padding = 2; // Min distance between each room
        return this.startX + this.width + padding > other.startX && other.startX + other.width + padding > this.startX &&
                this.startY + this.height + padding > other.startY && other.startY + other.height + padding > this.startY;
    }

    // Helper Methods

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }



}
