package ecosystem.world;

import ecosystem.creatures.Creature;
import ecosystem.creatures.Bird;
import ecosystem.creatures.Fish;
import ecosystem.creatures.Mammal;
import ecosystem.interfaces.TurnTaker;
import ecosystem.plants.Plant;

import java.util.ArrayList;

public class Tile implements TurnTaker {

    private int water;
    private int temperature;
    private int nutrients;
    private ArrayList<Creature> creatures;

    public Tile(int water, int temperature, int nutrients) {
        this.water = water;
        this.temperature = temperature;
        this.nutrients = nutrients;
        creatures = new ArrayList<Creature>();
    }

    public Tile() {
        this(50, 15, 50);
    }

    public void addCreature(Creature c) {
        // give creature a reference to this tile
        if (c instanceof Plant) ((Plant) c).setCurrentTile(this);
        if (c instanceof Bird) ((Bird) c).setCurrentTile(this);
        if (c instanceof Mammal) ((Mammal) c).setCurrentTile(this);
        if (c instanceof Fish) ((Fish) c).setCurrentTile(this);
        creatures.add(c);
    }

    @Override
    public void takeTurn() {
        ArrayList<Creature> snapshot = new ArrayList<Creature>(creatures);

        for (Creature c : snapshot) {
            if (c.isAlive()) {
                c.takeTurn();
            }
        }

        // wire up tile for any new offspring
        for (Creature c : creatures) {
            if (!snapshot.contains(c)) {
                addCreature(c);
                creatures.remove(c); // addCreature re-adds it, avoid duplicate
            }
        }

        // remove dead creatures
        creatures.removeIf(c -> !c.isAlive());
    }

    public int getWater() { return water; }
    public int getTemperature() { return temperature; }
    public int getNutrients() { return nutrients; }
    public void setWater(int w) { water = w; }
    public void setTemperature(int t) { temperature = t; }
    public void setNutrients(int n) { nutrients = n; }
    public ArrayList<Creature> getCreatures() { return creatures; }
    public int getCreatureCount() { return creatures.size(); }

    public String toString() {
        String s = "Tile[water=" + water + ", temp=" + temperature + ", nutrients=" + nutrients + "]\n";
        for (Creature c : creatures) {
            s += "  " + c.toString() + "\n";
        }
        return s;
    }
}
