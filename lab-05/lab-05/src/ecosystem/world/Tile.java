package ecosystem.world;

import ecosystem.creatures.Creature;
import ecosystem.creatures.Bird;
import ecosystem.creatures.Fish;
import ecosystem.creatures.Mammal;
import ecosystem.interfaces.TurnTaker;
import ecosystem.plants.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tile — a single cell in the World grid.
 *
 * Holds an ArrayList of Creatures and three environmental attributes:
 *   water       — how much water is present (0-100)
 *   temperature — degrees Celsius
 *   nutrients   — soil nutrient level (0-100)
 *
 * Each turn, the Tile tells all its living Creatures to take their turn.
 * It also cleans up dead creatures and handles tile-feeding each creature's
 * tile reference so they can react to local conditions.
 */
public class Tile implements TurnTaker {

    private int water;
    private int temperature;
    private int nutrients;

    private final ArrayList<Creature> creatures = new ArrayList<>();

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------

    public Tile(int water, int temperature, int nutrients) {
        this.water       = water;
        this.temperature = temperature;
        this.nutrients   = nutrients;
    }

    public Tile() {
        this(50, 15, 50); // sensible defaults
    }

    // ----------------------------------------------------------------
    // Adding creatures — wire up tile reference
    // ----------------------------------------------------------------

    public void addCreature(Creature c) {
        setTileReference(c);
        creatures.add(c);
    }

    /** Give the creature a reference to this tile so it can check conditions. */
    private void setTileReference(Creature c) {
        // Each concrete class has its own setCurrentTile; we dispatch here.
        // (A cleaner design would put setCurrentTile on Creature, but that
        //  would couple the base class to Tile — composition avoids that.)
        if (c instanceof Plant p)   p.setCurrentTile(this);
        if (c instanceof Bird b)    b.setCurrentTile(this);
        if (c instanceof Mammal m)  m.setCurrentTile(this);
        if (c instanceof Fish f)    f.setCurrentTile(this);
    }

    // ----------------------------------------------------------------
    // TurnTaker
    // ----------------------------------------------------------------

    /**
     * Runs one simulation turn for this tile:
     *   1. All living creatures take their turn (they may reproduce or die).
     *   2. Dead creatures are removed from the list.
     *   3. Any offspring added during the turn get their tile reference wired up.
     */
    @Override
    public void takeTurn() {
        // Snapshot the list so newly-added offspring don't take a turn this round
        List<Creature> snapshot = new ArrayList<>(creatures);

        for (Creature c : snapshot) {
            if (c.isAlive()) {
                c.takeTurn();
            }
        }

        // Wire up tile reference for any offspring added this turn
        for (Creature c : creatures) {
            if (!snapshot.contains(c)) {
                setTileReference(c);
            }
        }

        // Prune dead creatures
        creatures.removeIf(c -> !c.isAlive());
    }

    // ----------------------------------------------------------------
    // Getters & setters
    // ----------------------------------------------------------------

    public int getWater()           { return water; }
    public int getTemperature()     { return temperature; }
    public int getNutrients()       { return nutrients; }

    public void setWater(int w)           { this.water       = Math.max(0, w); }
    public void setTemperature(int t)     { this.temperature = t; }
    public void setNutrients(int n)       { this.nutrients   = Math.max(0, n); }

    public List<Creature> getCreatures()  { return new ArrayList<>(creatures); }
    public int getCreatureCount()         { return creatures.size(); }

    @Override
    public String toString() {
        String creatureList = creatures.stream()
            .map(c -> "    " + c.toString())
            .collect(Collectors.joining("\n"));
        return String.format(
            "Tile[water=%d, temp=%d, nutrients=%d, creatures=%d]\n%s",
            water, temperature, nutrients, creatures.size(),
            creatureList.isEmpty() ? "    (empty)" : creatureList);
    }
}
