package ecosystem.plants;

import ecosystem.creatures.Creature;
import ecosystem.interfaces.Reproducible;
import ecosystem.interfaces.ReproductionStrategy;
import ecosystem.world.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plant extends Creature implements Reproducible {

    private static HashMap<String, String> speciesRegistry = new HashMap<String, String>();

    // flags
    private boolean isWoody;
    private boolean isPerennial;
    private boolean hasPineNeedles;
    private boolean hasFruit;

    private String growthPattern;

    // composition - reproduction strategies
    private List<ReproductionStrategy> reproStrategies = new ArrayList<ReproductionStrategy>();

    private Tile currentTile;

    // constructor 1
    public Plant(String name, String species) {
        super(name, species);
        isWoody = false;
        isPerennial = false;
        hasPineNeedles = false;
        hasFruit = false;
        growthPattern = "short";
        loadFromRegistry(species);
    }

    // constructor 2
    public Plant(String name, String species, boolean woody, boolean perennial) {
        super(name, species, 0);
        isWoody = woody;
        isPerennial = perennial;
        hasPineNeedles = false;
        hasFruit = false;
        growthPattern = "short";
        loadFromRegistry(species);
        // override what registry says for woody/perennial
        this.isWoody = woody;
        this.isPerennial = perennial;
    }

    private void loadFromRegistry(String speciesName) {
        String def = speciesRegistry.get(speciesName);
        if (def == null) return;

        String[] parts = def.split(",");
        for (String part : parts) {
            String[] kv = part.trim().split("=");
            if (kv.length != 2) continue;
            String key = kv[0].trim();
            String val = kv[1].trim();

            if (key.equals("woody")) isWoody = val.equals("true");
            else if (key.equals("perennial")) isPerennial = val.equals("true");
            else if (key.equals("pineneedles")) hasPineNeedles = val.equals("true");
            else if (key.equals("growth")) growthPattern = val;
            else if (key.equals("reproduction")) {
                String[] strats = val.split("\\+");
                for (String s : strats) {
                    if (s.equals("seed")) reproStrategies.add(new SeedReproduction());
                    else if (s.equals("spore")) reproStrategies.add(new SporeReproduction());
                    else if (s.equals("clone")) reproStrategies.add(new CloneReproduction());
                }
            }
        }
    }

    public static void addSpecies(String name, String definition) {
        if (speciesRegistry.containsKey(name)) {
            throw new IllegalArgumentException("Species " + name + " already exists!");
        }
        speciesRegistry.put(name, definition);
    }

    // overloaded version of addSpecies
    public static void addSpecies(String name, String definition, boolean overwrite) {
        if (!overwrite && speciesRegistry.containsKey(name)) {
            throw new IllegalArgumentException("Species " + name + " already exists!");
        }
        speciesRegistry.put(name, definition);
    }

    public static Map<String, String> getAllSpecies() {
        return speciesRegistry;
    }

    @Override
    public void takeTurn() {
        if (!alive) return;
        ageOneYear();

        if (currentTile != null) {
            if (currentTile.getWater() <= 0) {
                alive = false;
                System.out.println(name + " died - no water");
                return;
            }
            if (!isPerennial && currentTile.getNutrients() <= 0) {
                alive = false;
                System.out.println(name + " died - no nutrients");
                return;
            }
        }

        if (canReproduce() && currentTile != null && currentTile.getCreatureCount() < 20) {
            Creature baby = reproduce();
            if (baby != null) {
                currentTile.addCreature(baby);
                System.out.println(name + " reproduced -> " + baby.getName());
            }
        }
    }

    @Override
    public boolean canReproduce() {
        return alive && age >= 2 && reproStrategies.size() > 0;
    }

    @Override
    public Creature reproduce() {
        if (currentTile == null) return null;
        for (ReproductionStrategy s : reproStrategies) {
            Creature baby = s.attempt(species, currentTile.getNutrients(), currentTile.getWater());
            if (baby != null) return baby;
        }
        return null;
    }

    @Override
    public String toString() {
        String strats = "";
        for (ReproductionStrategy s : reproStrategies) {
            strats += s.getName() + " ";
        }
        return super.toString() + " | woody: " + isWoody + " | perennial: " + isPerennial
                + " | growth: " + growthPattern + " | reproduction: " + strats.trim();
    }

    public boolean isWoody() { return isWoody; }
    public boolean isPerennial() { return isPerennial; }
    public boolean hasPineNeedles() { return hasPineNeedles; }
    public boolean hasFruit() { return hasFruit; }
    public String getGrowthPattern() { return growthPattern; }
    public void setCurrentTile(Tile tile) { this.currentTile = tile; }
    public void setHasFruit(boolean val) { hasFruit = val; }
    public void addReproductionStrategy(ReproductionStrategy s) { reproStrategies.add(s); }
}
