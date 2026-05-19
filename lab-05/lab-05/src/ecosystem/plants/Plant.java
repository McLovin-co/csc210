package ecosystem.plants;

import ecosystem.creatures.Creature;
import ecosystem.interfaces.Reproducible;
import ecosystem.interfaces.ReproductionStrategy;
import ecosystem.world.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plant — extends Creature, models a wide variety of plant types.
 *
 * Design decisions (matching spec requirements):
 *
 *   STATIC SPECIES REGISTRY
 *     Plant.addSpecies(name, definition) registers a species globally.
 *     Duplicate names throw an exception. Each species string encodes
 *     all properties as comma-separated key=value pairs.
 *
 *   COMPOSITION (strategy pattern)
 *     reproductionStrategies — a List of ReproductionStrategy objects.
 *     A single plant can have multiple (e.g. seeds + cloning).
 *
 *   FLAGS (boolean fields)
 *     isWoody, isPerennial, hasPineNeedles, hasFruit
 *
 *   STRING ATTRIBUTES
 *     growthPattern — "ground", "climbing", "tall", "short", "tiny"
 *
 *   INTERFACE IMPLEMENTATIONS
 *     Reproducible — canReproduce() / reproduce()
 *
 *   OVERLOADED CONSTRUCTORS (two, both call super)
 *     Plant(name, species)                   — minimal
 *     Plant(name, species, woody, perennial) — full flag constructor
 *
 *   OVERLOADED METHOD
 *     addSpecies(name, definition)
 *     addSpecies(name, definition, overwrite) — allows forced overwrite
 *
 *   OVERRIDDEN METHOD
 *     toString() — adds plant-specific detail on top of Creature.toString()
 */
public class Plant extends Creature implements Reproducible {

    // ----------------------------------------------------------------
    // Static species registry — shared across ALL Plant instances
    // ----------------------------------------------------------------

    /** Maps species name -> definition string describing how to build it. */
    private static final Map<String, String> speciesRegistry = new HashMap<>();

    /**
     * Register a new plant species.
     * The definition string uses "key=value,key=value" format, e.g.:
     *   "woody=true,perennial=true,growth=tall,reproduction=seed"
     *
     * @throws IllegalArgumentException if the species name already exists.
     */
    public static void addSpecies(String name, String definition) {
        if (speciesRegistry.containsKey(name)) {
            throw new IllegalArgumentException(
                "Species '" + name + "' already exists in the registry. " +
                "Use addSpecies(name, definition, true) to overwrite.");
        }
        speciesRegistry.put(name, definition);
    }

    /**
     * Overloaded version: optionally allow overwriting an existing species.
     * Demonstrates method overloading as required by the spec.
     */
    public static void addSpecies(String name, String definition, boolean overwrite) {
        if (!overwrite && speciesRegistry.containsKey(name)) {
            throw new IllegalArgumentException(
                "Species '" + name + "' already exists.");
        }
        speciesRegistry.put(name, definition);
    }

    /** Returns the definition string for a registered species, or null. */
    public static String getSpeciesDefinition(String name) {
        return speciesRegistry.get(name);
    }

    /** Returns all registered species names. */
    public static Map<String, String> getAllSpecies() {
        return new HashMap<>(speciesRegistry); // defensive copy
    }

    // ----------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------

    // --- Flags ---
    private boolean isWoody;       // hard wooden stem vs soft green stem
    private boolean isPerennial;   // lives year-round vs seasonal annual
    private boolean hasPineNeedles;// pine needles (year-round) vs deciduous leaves
    private boolean hasFruit;      // currently bearing fruit

    // --- String attribute ---
    private String growthPattern;  // "ground","climbing","tall","short","tiny"

    // --- Composition: reproduction strategies (strategy pattern) ---
    private final List<ReproductionStrategy> reproductionStrategies = new ArrayList<>();

    // --- Tile reference (set by Tile when creature is placed) ---
    private Tile currentTile;

    // ----------------------------------------------------------------
    // Constructors — both call super(name, species)
    // ----------------------------------------------------------------

    /**
     * Minimal constructor: name + species.
     * Properties are inferred from the species registry if available;
     * otherwise default to non-woody annual with "short" growth.
     */
    public Plant(String name, String species) {
        super(name, species);   // calls Creature(name, species)
        applySpeciesDefaults(species);
    }

    /**
     * Full constructor: explicitly set the two most important flags.
     * Other properties still inferred from registry or defaulted.
     * Demonstrates the second overloaded constructor.
     */
    public Plant(String name, String species, boolean woody, boolean perennial) {
        super(name, species, 0);  // calls Creature(name, species, age)
        applySpeciesDefaults(species);
        this.isWoody     = woody;
        this.isPerennial = perennial;
    }

    /**
     * Parse the species registry entry (if present) and set fields accordingly.
     * Format: "woody=true,perennial=false,growth=tall,reproduction=seed+clone"
     */
    private void applySpeciesDefaults(String speciesName) {
        // Sensible defaults
        this.isWoody        = false;
        this.isPerennial    = false;
        this.hasPineNeedles = false;
        this.hasFruit       = false;
        this.growthPattern  = "short";

        String def = speciesRegistry.get(speciesName);
        if (def == null) return;

        for (String token : def.split(",")) {
            String[] kv = token.trim().split("=");
            if (kv.length != 2) continue;
            String key = kv[0].trim().toLowerCase();
            String val = kv[1].trim().toLowerCase();

            switch (key) {
                case "woody"        -> isWoody        = val.equals("true");
                case "perennial"    -> isPerennial    = val.equals("true");
                case "pineneedles"  -> hasPineNeedles = val.equals("true");
                case "growth"       -> growthPattern  = val;
                case "reproduction" -> {
                    // supports multiple strategies: "seed+spore" or just "clone"
                    for (String strat : val.split("\\+")) {
                        switch (strat.trim()) {
                            case "seed"  -> reproductionStrategies.add(new SeedReproduction());
                            case "spore" -> reproductionStrategies.add(new SporeReproduction());
                            case "clone" -> reproductionStrategies.add(new CloneReproduction());
                        }
                    }
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // TurnTaker — overrides abstract method from Creature
    // ----------------------------------------------------------------

    /**
     * Each turn: age, check survival, attempt reproduction.
     * Plants die if the tile has zero water or zero nutrients.
     */
    @Override
    public void takeTurn() {
        if (!alive) return;

        ageOneYear();

        if (currentTile != null) {
            // Plants wilt without water
            if (currentTile.getWater() <= 0) {
                alive = false;
                System.out.println(name + " (" + species + ") wilted — no water.");
                return;
            }
            // Perennials survive low nutrients; annuals don't
            if (!isPerennial && currentTile.getNutrients() <= 0) {
                alive = false;
                System.out.println(name + " (" + species + ") starved — no nutrients.");
                return;
            }
        }

        // Attempt reproduction via each strategy (cap tile at 20 creatures)
        if (canReproduce() && currentTile != null
                && currentTile.getCreatureCount() < 20) {
            Creature offspring = reproduce();
            if (offspring != null) {
                currentTile.addCreature(offspring);
                System.out.println(name + " reproduced → " + offspring.getName());
            }
        }
    }

    // ----------------------------------------------------------------
    // Reproducible interface
    // ----------------------------------------------------------------

    @Override
    public boolean canReproduce() {
        return alive && age >= 2 && !reproductionStrategies.isEmpty();
    }

    /**
     * Tries each reproduction strategy in order.
     * Returns the first successful offspring, or null.
     */
    @Override
    public Creature reproduce() {
        if (currentTile == null) return null;
        for (ReproductionStrategy strategy : reproductionStrategies) {
            Creature offspring = strategy.attempt(
                species,
                currentTile.getNutrients(),
                currentTile.getWater()
            );
            if (offspring != null) return offspring;
        }
        return null;
    }

    // ----------------------------------------------------------------
    // Override toString() — required by spec
    // ----------------------------------------------------------------

    @Override
    public String toString() {
        return super.toString() + String.format(
            " | woody=%b perennial=%b growth=%s strategies=%s",
            isWoody, isPerennial, growthPattern,
            reproductionStrategies.stream()
                .map(ReproductionStrategy::getName)
                .reduce((a, b) -> a + "+" + b)
                .orElse("none")
        );
    }

    // ----------------------------------------------------------------
    // Getters & setters
    // ----------------------------------------------------------------

    public boolean isWoody()         { return isWoody; }
    public boolean isPerennial()     { return isPerennial; }
    public boolean hasPineNeedles()  { return hasPineNeedles; }
    public boolean hasFruit()        { return hasFruit; }
    public String  getGrowthPattern(){ return growthPattern; }
    public List<ReproductionStrategy> getReproductionStrategies() {
        return new ArrayList<>(reproductionStrategies);
    }

    public void setCurrentTile(Tile tile) { this.currentTile = tile; }
    public void setHasFruit(boolean hasFruit) { this.hasFruit = hasFruit; }

    /** Convenience: add a strategy at runtime (e.g. after mutation). */
    public void addReproductionStrategy(ReproductionStrategy strategy) {
        reproductionStrategies.add(strategy);
    }
}
