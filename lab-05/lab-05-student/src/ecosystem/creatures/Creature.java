package ecosystem.creatures;

import ecosystem.interfaces.TurnTaker;

public abstract class Creature implements TurnTaker {

    public static final int MAX_AGE = 200;

    protected String name;
    protected String species;
    protected int age;
    protected boolean alive;

    public Creature(String name, String species) {
        this.name = name;
        this.species = species;
        this.age = 0;
        this.alive = true;
    }

    public Creature(String name, String species, int age) {
        this.name = name;
        this.species = species;
        this.age = age;
        this.alive = true;
    }

    public abstract void takeTurn();

    protected void ageOneYear() {
        age++;
        if (age > MAX_AGE) {
            alive = false;
        }
    }

    public String getName() { return name; }
    public String getSpecies() { return species; }
    public int getAge() { return age; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }

    public String toString() {
        return name + " | species: " + species + " | age: " + age + " | alive: " + alive;
    }
}
