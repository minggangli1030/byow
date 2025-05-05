package core;

public class Main {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;

    public static void main(String[] args) {
        boolean gameCompleted = false;
        
        while (true) {
            GameMenu menu = new GameMenu(WIDTH, HEIGHT);
            Game game = menu.displayMainMenu(gameCompleted);
            gameCompleted = game.run();
        }
    }
}
