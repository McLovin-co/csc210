package ecosystem.plants;

import ecosystem.interfaces.Flowering;

/**
 * Shrub — woody but shorter than trees, often flowering.
 * Implements Flowering to cycle through flower phases.
 * Can climb walls or spread along the ground.
 *
 * Pre-registered species: RoseHip, Blackberry, HoneySuckle
 */
public class Shrub extends Plant implements Flowering {

    static {
        Plant.addSpecies("RoseHip",
            "woody=true,perennial=true,growth=short,reproduction=seed+clone");
        Plant.addSpecies("Blackberry",
            "woody=false,perennial=true,growth=climbing,reproduction=seed+clone");
        Plant.addSpecies("HoneySuckle",
            "woody=true,perennial=true,growth=climbing,reproduction=seed");
    }

    private FlowerPhase flowerPhase = FlowerPhase.NO_FLOWER;
    private int phaseTimer = 0;
    private static final int PHASE_DURATION = 5; // turns per phase

    // ------- Constructors -------

    public Shrub(String name, String species) {
        super(name, species);
    }

    public Shrub(String name, String species, boolean woody, boolean perennial) {
        super(name, species, woody, perennial);
    }

    // ------- Flowering interface -------

    @Override
    public FlowerPhase getFlowerPhase() { return flowerPhase; }

    @Override
    public void advanceFlowerPhase() {
        flowerPhase = switch (flowerPhase) {
            case NO_FLOWER -> FlowerPhase.FLOWERING;
            case FLOWERING -> FlowerPhase.FRUITING;
            case FRUITING  -> FlowerPhase.NO_FLOWER;
        };
        setHasFruit(flowerPhase == FlowerPhase.FRUITING);
        System.out.println(getName() + " flower phase → " + flowerPhase);
    }

    @Override
    public boolean hasFruit() {
        return flowerPhase == FlowerPhase.FRUITING;
    }

    // ------- Override takeTurn -------

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (!isAlive()) return;

        // Advance flowering cycle every PHASE_DURATION turns
        phaseTimer++;
        if (getAge() >= 3 && phaseTimer >= PHASE_DURATION) {
            advanceFlowerPhase();
            phaseTimer = 0;
        }
    }

    @Override
    public String toString() {
        return super.toString() + String.format(
            " | Shrub | phase=%s", flowerPhase);
    }
}
