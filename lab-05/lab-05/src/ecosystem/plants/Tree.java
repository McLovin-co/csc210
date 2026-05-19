package ecosystem.plants;

import ecosystem.interfaces.Seasonal;

/**
 * Tree — tall, woody, perennial. Many are seasonal (deciduous).
 * Implements Seasonal to handle leaf-drop in winter.
 *
 * Pre-registered species: Oak, Pine, Maple
 */
public class Tree extends Plant implements Seasonal {

    static {
        Plant.addSpecies("Oak",
            "woody=true,perennial=true,growth=tall,reproduction=seed");
        Plant.addSpecies("Pine",
            "woody=true,perennial=true,pineneedles=true,growth=tall,reproduction=seed");
        Plant.addSpecies("Maple",
            "woody=true,perennial=true,growth=tall,reproduction=seed+clone");
    }

    private boolean dormant = false;
    private boolean hasLeaves = true;
    private String currentSeason = "summer";

    // ------- Constructors -------

    public Tree(String name, String species) {
        super(name, species);
    }

    public Tree(String name, String species, boolean woody, boolean perennial) {
        super(name, species, woody, perennial);
    }

    // ------- Seasonal interface -------

    @Override
    public void onSeasonChange(String season) {
        this.currentSeason = season.toLowerCase();
        switch (currentSeason) {
            case "winter" -> {
                if (!hasPineNeedles()) {
                    hasLeaves = false;
                    dormant   = true;
                    System.out.println(getName() + " lost its leaves for winter.");
                }
            }
            case "spring" -> {
                hasLeaves = true;
                dormant   = false;
                System.out.println(getName() + " is leafing out in spring.");
            }
            case "autumn" -> System.out.println(getName() + " leaves are changing color.");
            default -> dormant = false;
        }
    }

    @Override
    public boolean isDormant() { return dormant; }

    // ------- Override takeTurn -------

    @Override
    public void takeTurn() {
        if (dormant) {
            // Trees don't do much in winter — just survive
            return;
        }
        super.takeTurn();
    }

    // ------- Getters -------

    public boolean hasLeaves()      { return hasLeaves; }
    public String getCurrentSeason(){ return currentSeason; }

    @Override
    public String toString() {
        return super.toString() + String.format(
            " | Tree | leaves=%b dormant=%b season=%s",
            hasLeaves, dormant, currentSeason);
    }
}
