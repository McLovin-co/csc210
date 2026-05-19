package ecosystem.creatures;

import ecosystem.world.Tile;

import java.util.HashMap;
import java.util.Map;

/**
 * Fish — aquatic Creature. Dies quickly if tile water drops too low.
 * Also sensitive to extreme temperatures.
 *
 * Pre-registered: Trout, Salmon, Catfish
 */
public class Fish extends Creature {

    // ----------------------------------------------------------------
    // Static species registry
    // ----------------------------------------------------------------

    private static final Map<String, String> speciesRegistry = new HashMap<>();

    public static void addSpecies(String name, String definition) {
        if (speciesRegistry.containsKey(name)) {
            throw new IllegalArgumentException(
                "Fish species '" + name + "' already exists.");
        }
        speciesRegistry.put(name, definition);
    }

    static {
        addSpecies("Trout",   "minWater=60,minTemp=2,maxTemp=20");
        addSpecies("Salmon",  "minWater=70,minTemp=0,maxTemp=18");
        addSpecies("Catfish", "minWater=40,minTemp=5,maxTemp=35");
    }

    // ----------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------

    private int minWater;  // needs at least this much water in tile
    private int minTemp;   // cold-water limit
    private int maxTemp;   // heat limit

    private Tile currentTile;

    // ------- Constructors -------

    public Fish(String name, String species) {
        super(name, species);
        applySpeciesDefaults(species);
    }

    public Fish(String name, String species, int startAge) {
        super(name, species, startAge);
        applySpeciesDefaults(species);
    }

    private void applySpeciesDefaults(String speciesName) {
        this.minWater = 50;
        this.minTemp  = 0;
        this.maxTemp  = 25;

        String def = speciesRegistry.get(speciesName);
        if (def == null) return;

        for (String token : def.split(",")) {
            String[] kv = token.trim().split("=");
            if (kv.length != 2) continue;
            switch (kv[0].trim().toLowerCase()) {
                case "minwater" -> minWater = Integer.parseInt(kv[1].trim());
                case "mintemp"  -> minTemp  = Integer.parseInt(kv[1].trim());
                case "maxtemp"  -> maxTemp  = Integer.parseInt(kv[1].trim());
            }
        }
    }

    // ----------------------------------------------------------------
    // TurnTaker
    // ----------------------------------------------------------------

    @Override
    public void takeTurn() {
        if (!alive) return;
        ageOneYear();

        if (currentTile != null) {
            int water = currentTile.getWater();
            int temp  = currentTile.getTemperature();

            if (water < minWater) {
                alive = false;
                System.out.println(name + " (" + species + ") suffocated — water "
                    + water + " < " + minWater);
                return;
            }
            if (temp < minTemp || temp > maxTemp) {
                alive = false;
                System.out.println(name + " (" + species + ") died — temp "
                    + temp + " out of range [" + minTemp + ".." + maxTemp + "]");
            }
        }
    }

    // ----------------------------------------------------------------
    // Getters & setters
    // ----------------------------------------------------------------

    public int getMinWater() { return minWater; }
    public int getMinTemp()  { return minTemp; }
    public int getMaxTemp()  { return maxTemp; }

    public void setCurrentTile(Tile tile) { this.currentTile = tile; }

    @Override
    public String toString() {
        return super.toString() + String.format(
            " | Fish | minWater=%d temp=[%d..%d]", minWater, minTemp, maxTemp);
    }
}
