package ecosystem.creatures;

import ecosystem.world.Tile;

import java.util.HashMap;
import java.util.Map;

/**
 * Mammal — warm-blooded Creature. Needs food (plants or other creatures)
 * and moderate temperature. Species registered statically.
 *
 * Pre-registered: Deer, Wolf, Rabbit
 */
public class Mammal extends Creature {

    // ----------------------------------------------------------------
    // Static species registry
    // ----------------------------------------------------------------

    private static final Map<String, String> speciesRegistry = new HashMap<>();

    public static void addSpecies(String name, String definition) {
        if (speciesRegistry.containsKey(name)) {
            throw new IllegalArgumentException(
                "Mammal species '" + name + "' already exists.");
        }
        speciesRegistry.put(name, definition);
    }

    static {
        addSpecies("Deer",   "diet=plants,minTemp=-10,size=large");
        addSpecies("Wolf",   "diet=meat,minTemp=-20,size=large");
        addSpecies("Rabbit", "diet=plants,minTemp=-5,size=small");
    }

    // ----------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------

    private String diet;    // "plants" or "meat"
    private int    minTemp;
    private String size;    // "small", "large"
    private int    hunger;  // 0=full, 10=starving

    private Tile currentTile;

    // ------- Constructors -------

    public Mammal(String name, String species) {
        super(name, species);
        applySpeciesDefaults(species);
        this.hunger = 0;
    }

    public Mammal(String name, String species, int startAge) {
        super(name, species, startAge);
        applySpeciesDefaults(species);
        this.hunger = 0;
    }

    private void applySpeciesDefaults(String speciesName) {
        this.diet    = "plants";
        this.minTemp = -10;
        this.size    = "medium";

        String def = speciesRegistry.get(speciesName);
        if (def == null) return;

        for (String token : def.split(",")) {
            String[] kv = token.trim().split("=");
            if (kv.length != 2) continue;
            switch (kv[0].trim().toLowerCase()) {
                case "diet"    -> diet    = kv[1].trim();
                case "mintemp" -> minTemp = Integer.parseInt(kv[1].trim());
                case "size"    -> size    = kv[1].trim();
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
            // Freeze check
            if (currentTile.getTemperature() < minTemp) {
                alive = false;
                System.out.println(name + " (" + species + ") froze to death.");
                return;
            }

            // Hunger: herbivores need plants in the tile
            if (diet.equals("plants")) {
                boolean foundFood = currentTile.getCreatures().stream()
                    .anyMatch(c -> c instanceof ecosystem.plants.Plant && c.isAlive());
                if (foundFood) {
                    hunger = Math.max(0, hunger - 2);
                } else {
                    hunger++;
                }
            } else {
                // Carnivores: just increment hunger (prey-hunting simplified)
                hunger++;
            }

            if (hunger >= 10) {
                alive = false;
                System.out.println(name + " (" + species + ") starved.");
            }
        }
    }

    // ----------------------------------------------------------------
    // Getters & setters
    // ----------------------------------------------------------------

    public String getDiet()    { return diet; }
    public int    getMinTemp() { return minTemp; }
    public String getSize()    { return size; }
    public int    getHunger()  { return hunger; }

    public void setCurrentTile(Tile tile) { this.currentTile = tile; }

    @Override
    public String toString() {
        return super.toString() + String.format(
            " | Mammal | diet=%s size=%s hunger=%d", diet, size, hunger);
    }
}
