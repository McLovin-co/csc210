package ecosystem.creatures;

import ecosystem.interfaces.TurnTaker;

/**
 * Abstract base for every living thing in the simulation.
 * Subclasses must implement takeTurn() to define per-turn behavior.
 *
 * Key design notes:
 *   - Fields are protected so subclasses can read/write without getters.
 *   - MAX_AGE is final static — a shared, immutable constant.
 *   - The class is abstract: you cannot do `new Creature(...)`.
 */
public abstract class Creature implements TurnTaker {

    public static final int MAX_AGE = 200;

    // Protected so subclasses access directly without boilerplate getters.
    protected String name;
    protected String species;
    protected int age;
    protected boolean alive;

    // ------- Constructors -------

    /**
     * Minimal constructor: name + species. Age starts at 0.
     */
    public Creature(String name, String species) {
        this.name    = name;
        this.species = species;
        this.age     = 0;
        this.alive   = true;
    }

    /**
     * Full constructor: include a starting age (useful when loading from JSON).
     */
    public Creature(String name, String species, int age) {
        this(name, species);
        this.age = age;
    }

    // ------- Abstract behavior -------

    /**
     * Each subclass defines what it does each simulation turn.
     * Typical pattern: age++, react to tile conditions, reproduce, maybe die.
     */
    @Override
    public abstract void takeTurn();

    // ------- Shared helpers -------

    /** Advance age by one turn; mark dead if MAX_AGE exceeded. */
    protected void ageOneYear() {
        age++;
        if (age > MAX_AGE) {
            alive = false;
        }
    }

    // ------- Getters -------

    public String getName()    { return name; }
    public String getSpecies() { return species; }
    public int    getAge()     { return age; }
    public boolean isAlive()   { return alive; }

    public void setAlive(boolean alive) { this.alive = alive; }

    @Override
    public String toString() {
        return String.format("[%s] %s (age %d, %s)",
                species, name, age, alive ? "alive" : "dead");
    }
}
