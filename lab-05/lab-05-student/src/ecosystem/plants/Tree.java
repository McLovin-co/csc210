package ecosystem.plants;

import ecosystem.interfaces.Seasonal;

public class Tree extends Plant implements Seasonal {

    static {
        Plant.addSpecies("Oak", "woody=true,perennial=true,growth=tall,reproduction=seed");
        Plant.addSpecies("Pine", "woody=true,perennial=true,pineneedles=true,growth=tall,reproduction=seed");
        Plant.addSpecies("Maple", "woody=true,perennial=true,growth=tall,reproduction=seed+clone");
    }

    private boolean dormant;
    private boolean hasLeaves;
    private String season;

    public Tree(String name, String species) {
        super(name, species);
        dormant = false;
        hasLeaves = true;
        season = "summer";
    }

    public Tree(String name, String species, boolean woody, boolean perennial) {
        super(name, species, woody, perennial);
        dormant = false;
        hasLeaves = true;
        season = "summer";
    }

    @Override
    public void onSeasonChange(String s) {
        season = s;
        if (s.equals("winter") && !hasPineNeedles()) {
            hasLeaves = false;
            dormant = true;
            System.out.println(name + " lost its leaves");
        } else if (s.equals("spring")) {
            hasLeaves = true;
            dormant = false;
            System.out.println(name + " is growing leaves again");
        }
    }

    @Override
    public boolean isDormant() { return dormant; }

    @Override
    public void takeTurn() {
        if (dormant) return;
        super.takeTurn();
    }

    public boolean hasLeaves() { return hasLeaves; }

    @Override
    public String toString() {
        return super.toString() + " | Tree | leaves: " + hasLeaves + " | dormant: " + dormant;
    }
}
