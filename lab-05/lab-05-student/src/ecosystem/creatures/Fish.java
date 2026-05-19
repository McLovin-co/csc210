package ecosystem.creatures;

import ecosystem.world.Tile;
import java.util.HashMap;

public class Fish extends Creature {

    private static HashMap<String, String> speciesRegistry = new HashMap<String, String>();

    static {
        addSpecies("Trout", "minWater=60,minTemp=2,maxTemp=20");
        addSpecies("Salmon", "minWater=70,minTemp=0,maxTemp=18");
        addSpecies("Catfish", "minWater=40,minTemp=5,maxTemp=35");
    }

    private int minWater;
    private int minTemp;
    private int maxTemp;
    private Tile currentTile;

    public Fish(String name, String species) {
        super(name, species);
        minWater = 50;
        minTemp = 0;
        maxTemp = 25;
        loadFromRegistry(species);
    }

    public Fish(String name, String species, int startAge) {
        super(name, species, startAge);
        minWater = 50;
        minTemp = 0;
        maxTemp = 25;
        loadFromRegistry(species);
    }

    private void loadFromRegistry(String sp) {
        String def = speciesRegistry.get(sp);
        if (def == null) return;
        for (String token : def.split(",")) {
            String[] kv = token.split("=");
            if (kv.length != 2) continue;
            if (kv[0].equals("minWater")) minWater = Integer.parseInt(kv[1]);
            else if (kv[0].equals("minTemp")) minTemp = Integer.parseInt(kv[1]);
            else if (kv[0].equals("maxTemp")) maxTemp = Integer.parseInt(kv[1]);
        }
    }

    public static void addSpecies(String name, String definition) {
        if (speciesRegistry.containsKey(name)) {
            throw new IllegalArgumentException("Fish species " + name + " already exists");
        }
        speciesRegistry.put(name, definition);
    }

    @Override
    public void takeTurn() {
        if (!alive) return;
        ageOneYear();
        if (currentTile != null) {
            if (currentTile.getWater() < minWater) {
                alive = false;
                System.out.println(name + " died - not enough water");
                return;
            }
            if (currentTile.getTemperature() < minTemp || currentTile.getTemperature() > maxTemp) {
                alive = false;
                System.out.println(name + " died - bad temperature");
            }
        }
    }

    public void setCurrentTile(Tile tile) { this.currentTile = tile; }
    public int getMinWater() { return minWater; }

    @Override
    public String toString() {
        return super.toString() + " | Fish | minWater: " + minWater;
    }
}
