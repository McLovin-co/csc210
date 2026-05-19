package ecosystem.creatures;

import ecosystem.world.Tile;
import java.util.HashMap;

public class Bird extends Creature {

    private static HashMap<String, String> speciesRegistry = new HashMap<String, String>();

    static {
        addSpecies("Robin", "diet=seeds,canFly=true,minTemp=5");
        addSpecies("Hawk", "diet=meat,canFly=true,minTemp=0");
        addSpecies("Penguin", "diet=fish,canFly=false,minTemp=-20");
    }

    private boolean canFly;
    private String diet;
    private int minTemp;
    private Tile currentTile;

    public Bird(String name, String species) {
        super(name, species);
        canFly = true;
        diet = "seeds";
        minTemp = 0;
        loadFromRegistry(species);
    }

    public Bird(String name, String species, int startAge) {
        super(name, species, startAge);
        canFly = true;
        diet = "seeds";
        minTemp = 0;
        loadFromRegistry(species);
    }

    private void loadFromRegistry(String sp) {
        String def = speciesRegistry.get(sp);
        if (def == null) return;
        for (String token : def.split(",")) {
            String[] kv = token.split("=");
            if (kv.length != 2) continue;
            if (kv[0].equals("canFly")) canFly = kv[1].equals("true");
            else if (kv[0].equals("diet")) diet = kv[1];
            else if (kv[0].equals("minTemp")) minTemp = Integer.parseInt(kv[1]);
        }
    }

    public static void addSpecies(String name, String definition) {
        if (speciesRegistry.containsKey(name)) {
            throw new IllegalArgumentException("Bird species " + name + " already exists");
        }
        speciesRegistry.put(name, definition);
    }

    @Override
    public void takeTurn() {
        if (!alive) return;
        ageOneYear();
        if (currentTile != null && currentTile.getTemperature() < minTemp) {
            alive = false;
            System.out.println(name + " froze to death");
        }
    }

    public void setCurrentTile(Tile tile) { this.currentTile = tile; }
    public boolean canFly() { return canFly; }
    public String getDiet() { return diet; }

    @Override
    public String toString() {
        return super.toString() + " | Bird | canFly: " + canFly + " | diet: " + diet;
    }
}
