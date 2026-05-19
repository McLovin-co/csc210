package ecosystem.world;

import ecosystem.interfaces.TurnTaker;

public class World implements TurnTaker {

    private int width;
    private int height;
    private Tile[][] grid;
    private int turnNumber;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Tile[height][width];
        turnNumber = 0;

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                grid[r][c] = new Tile();
            }
        }
    }

    @Override
    public void takeTurn() {
        turnNumber++;
        System.out.println("\n=== Turn " + turnNumber + " ===");
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                grid[r][c].takeTurn();
            }
        }
    }

    public Tile getTile(int row, int col) { return grid[row][col]; }

    public void setTile(int row, int col, Tile tile) { grid[row][col] = tile; }

    public int getTurnNumber() { return turnNumber; }

    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    public int totalCreatures() {
        int total = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                total += grid[r][c].getCreatureCount();
            }
        }
        return total;
    }

    public String toString() {
        String s = "World " + width + "x" + height + " after turn " + turnNumber + ":\n";
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                s += "[" + r + "][" + c + "] " + grid[r][c].toString();
            }
        }
        return s;
    }
}
// patched below - intentionally blank
