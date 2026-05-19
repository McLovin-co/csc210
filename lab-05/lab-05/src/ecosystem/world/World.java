package ecosystem.world;

import ecosystem.interfaces.TurnTaker;

/**
 * World — a 2D grid of Tiles.
 *
 * takeTurn() iterates every tile and calls its takeTurn() method,
 * which cascades to every creature inside it.
 */
public class World implements TurnTaker {

    private final int width;
    private final int height;
    private final Tile[][] grid;
    private int turnNumber = 0;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------

    public World(int width, int height) {
        this.width  = width;
        this.height = height;
        this.grid   = new Tile[height][width];

        // Initialize every tile with defaults
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                grid[row][col] = new Tile();
            }
        }
    }

    // ----------------------------------------------------------------
    // TurnTaker
    // ----------------------------------------------------------------

    /**
     * Advances the entire simulation by one turn.
     * Iterates every tile in row-major order and calls its takeTurn().
     */
    @Override
    public void takeTurn() {
        turnNumber++;
        System.out.println("\n=== Turn " + turnNumber + " ===");

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                grid[row][col].takeTurn();
            }
        }
    }

    // ----------------------------------------------------------------
    // Tile access
    // ----------------------------------------------------------------

    public Tile getTile(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new IndexOutOfBoundsException(
                "Tile (" + row + "," + col + ") is out of bounds for "
                + height + "x" + width + " world.");
        }
        return grid[row][col];
    }

    public void setTile(int row, int col, Tile tile) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new IndexOutOfBoundsException("Tile position out of bounds.");
        }
        grid[row][col] = tile;
    }

    // ----------------------------------------------------------------
    // Summary
    // ----------------------------------------------------------------

    public int getTurnNumber() { return turnNumber; }
    public int getWidth()      { return width; }
    public int getHeight()     { return height; }

    /** Total creature count across all tiles. */
    public int totalCreatures() {
        int total = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                total += grid[row][col].getCreatureCount();
            }
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("World (").append(width).append("x").append(height)
          .append(") after turn ").append(turnNumber).append(":\n");
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                sb.append("  [").append(row).append(",").append(col).append("] ")
                  .append(grid[row][col]).append("\n");
            }
        }
        return sb.toString();
    }
}
