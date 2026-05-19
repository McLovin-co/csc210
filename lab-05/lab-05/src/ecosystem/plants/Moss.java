package ecosystem.plants;

/**
 * Moss — very tiny, non-woody, grows along the ground.
 * Reproduces via spores, thrives in high-water low-nutrient environments.
 * One of the hardiest plant types in the simulation.
 *
 * Pre-registered species: PeatMoss, RockMoss, SpagMoss
 */
public class Moss extends Plant {

    static {
        Plant.addSpecies("PeatMoss",
            "woody=false,perennial=true,growth=tiny,reproduction=spore+clone");
        Plant.addSpecies("RockMoss",
            "woody=false,perennial=true,growth=tiny,reproduction=spore");
        Plant.addSpecies("SpagMoss",
            "woody=false,perennial=true,growth=tiny,reproduction=spore+clone");
    }

    // Moss tracks how much surface area it has colonized (purely aesthetic)
    private int coverage = 1; // in arbitrary units

    // ------- Constructors -------

    public Moss(String name, String species) {
        super(name, species);
    }

    public Moss(String name, String species, boolean woody, boolean perennial) {
        super(name, species, woody, perennial);
    }

    // ------- Override takeTurn -------

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (!isAlive()) return;

        // Moss slowly spreads in high-water conditions
        if (getCurrentTileWater() > 50 && coverage < 10) {
            coverage++;
        }
    }

    /**
     * Moss is tiny — override toString to reflect its unique character.
     */
    @Override
    public String toString() {
        return super.toString() + String.format(
            " | Moss | coverage=%d/10", coverage);
    }

    // Moss needs tile water but doesn't store the tile itself —
    // we expose a package-private hook so Plant.currentTile can be read.
    // Instead, we track it separately here for simplicity.
    private int cachedWater = 50; // default; updated by setCurrentTile override

    /** We override setCurrentTile to cache water for the tiny helper above. */
    @Override
    public void setCurrentTile(ecosystem.world.Tile tile) {
        super.setCurrentTile(tile);
        if (tile != null) cachedWater = tile.getWater();
    }

    private int getCurrentTileWater() { return cachedWater; }

    public int getCoverage() { return coverage; }
}
