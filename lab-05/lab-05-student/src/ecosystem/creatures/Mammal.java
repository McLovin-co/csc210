package ecosystem.creatures;

import ecosystem.world.Tile;
import java.util.HashMap;

public class Mammal extends Creature {

    private static HashMap<String, String> speciesRegistry = new HashMap<String, String>();

    static {
        addSpecies("Deer", "diet=plants,minTemp=-10,size=large");
        addSpecies("Wolf", "diet=meat,minTemp=-20,size=large");
        addSpecies("Rabbit", "diet=plants,minTemp=-5,size=small");
    }

    private String diet;
    private int minTemp;
    private String size;
    private int hunger;
    private Tile currentTile;

    public Mammal(String name, String species) {
        super(name, species);
        diet = "plants";
        minTemp = -10;
        size = "medium";
        hunger = 0;
        loadFromRegistry(species);
    }

    public Mammal(String name, String species, int startAge) {
        super(name, species, startAge);
        diet = "plants";
        minTemp = -10;
        size = "medium";
        hunger = 0;
        loadFromRegistry(species);
    }

    private void loadFromRegistry(String sp) {
        String def = speciesRegistry.get(sp);
        if (def == null) return;
        for (String token : def.split(",")) {
            String[] kv = token.split("=");
            if (kv.length != 2) continue;
            if (kv[0].equals("diet")) diet = kv[1];
            else if (kv[0].equals("minTemp")) minTemp = Integer.parseInt(kv[1]);
            else if (kv[0].equals("size")) size = kv[1];
        }
    }

    public static void addSpecies(String name, String definition) {
        if (speciesRegistry.containsKey(name)) {
            throw new IllegalArgumentException("Mammal species " + name + " already exists");
        }
        speciesRegistry.put(name, definition);
    }

    @Override
    public void takeTurn() {
        if (!alive) return;
        ageOneYear();

        if (currentTile != null) {
            if (currentTile.getTemperature() < minTemp) {
                alive = false;
                System.out.println(name + " froze to death");
                return;
            }
            if (diet.equals("plants")) {
                boolean foundFood = false;
                for (Creature c : currentTile.getCreatures()) {
                    if (c instanceof ecosystem.plants.Plant && c.isAlive()) {
                        foundFood = true;
                        break;
                    }
                }
                if (foundFood) hunger = Math.max(0, hunger - 2);
                else hunger++;
            } else {
                hunger++;
            }

            if (hunger >= 10) {
                alive = false;
                System.out.println(name + " starved");
            }
        }
    }

    public void setCurrentTile(Tile tile) { this.currentTile = tile; }
    public String getDiet() { return diet; }
    public String getSize() { return size; }
    public int getHunger() { return hunger; }

    @Override
    public String toString() {
        return super.toString() + " | Mammal | diet: " + diet + " | size: " + size + " | hunger: " + hunger;
    }
}
