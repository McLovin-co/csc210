package ecosystem.creatures;

import ecosystem.world.Tile;

import java.util.HashMap;
import java.util.Map;

/**
 * Bird — a Creature that flies, eats seeds/fruit, and needs moderate temperature.
 * Species are registered via Bird.addSpecies() just like Plant.
 *
 * Pre-registered: Robin, Hawk, Penguin
 */
public class Bird extends Creature {

    // ----------------------------------------------------------------
    // Static species registry
    // ----------------------------------------------------------------

    private static final Map<String, String> speciesRegistry = new HashMap<>();

    public static void addSpecies(String name, String definition) {
        if (speciesRegistry.containsKey(name)) {
            throw new IllegalArgumentException(
                "Bird species '" + name + "' already exists.");
        }
        speciesRegistry.put(name, definition);
    }

    static {
        addSpecies("Robin",   "diet=seeds,canFly=true,minTemp=5");
        addSpecies("Hawk",    "diet=meat,canFly=true,minTemp=0");
        addSpecies("Penguin", "diet=fish,canFly=false,minTemp=-20");
    }

    // ----------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------

    private boolean canFly;
    private String  diet;    // "seeds", "meat", "fish", "fruit"
    private int     minTemp; // dies if tile temp drops below this

    private Tile currentTile;

    // ------- Constructors -------

    public Bird(String name, String species) {
        super(name, species);
        applySpeciesDefaults(species);
    }

    public Bird(String name, String species, int startAge) {
        super(name, species, startAge);
        applySpeciesDefaults(species);
    }

    private void applySpeciesDefaults(String speciesName) {
        this.canFly  = true;
        this.diet    = "seeds";
        this.minTemp = 0;

        String def = speciesRegistry.get(speciesName);
        if (def == null) return;

        for (String token : def.split(",")) {
            String[] kv = token.trim().split("=");
            if (kv.length != 2) continue;
            switch (kv[0].trim().toLowerCase()) {
                case "canfly"  -> canFly  = kv[1].equalsIgnoreCase("true");
                case "diet"    -> diet    = kv[1].trim();
                case "mintemp" -> minTemp = Integer.parseInt(kv[1].trim());
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
            if (currentTile.getTemperature() < minTemp) {
                alive = false;
                System.out.println(name + " (" + species + ") froze — temp "
                    + currentTile.getTemperature() + " < " + minTemp);
                return;
            }
            // Birds that eat seeds/fruit benefit from plant presence
            long plantCount = currentTile.getCreatures().stream()
                .filter(c -> c instanceof ecosystem.plants.Plant)
                .count();
            if ((diet.equals("seeds") || diet.equals("fruit")) && plantCount == 0) {
                // No food this turn — birds can survive a few turns without food
                // (simplified: just print a warning)
                System.out.println(name + " found no food this turn.");
            }
        }
    }

    // ----------------------------------------------------------------
    // Getters & setters
    // ----------------------------------------------------------------

    public boolean canFly()   { return canFly; }
    public String  getDiet()  { return diet; }
    public int     getMinTemp(){ return minTemp; }

    public void setCurrentTile(Tile tile) { this.currentTile = tile; }

    @Override
    public String toString() {
        return super.toString() + String.format(
            " | Bird | fly=%b diet=%s minTemp=%d", canFly, diet, minTemp);
    }
}
