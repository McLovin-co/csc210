package ecosystem.plants;

import ecosystem.interfaces.Flowering;

public class Shrub extends Plant implements Flowering {

    static {
        Plant.addSpecies("RoseHip", "woody=true,perennial=true,growth=short,reproduction=seed+clone");
        Plant.addSpecies("Blackberry", "woody=false,perennial=true,growth=climbing,reproduction=seed+clone");
        Plant.addSpecies("HoneySuckle", "woody=true,perennial=true,growth=climbing,reproduction=seed");
    }

    private FlowerPhase phase;
    private int timer;

    public Shrub(String name, String species) {
        super(name, species);
        phase = FlowerPhase.NO_FLOWER;
        timer = 0;
    }

    public Shrub(String name, String species, boolean woody, boolean perennial) {
        super(name, species, woody, perennial);
        phase = FlowerPhase.NO_FLOWER;
        timer = 0;
    }

    @Override
    public FlowerPhase getFlowerPhase() { return phase; }

    @Override
    public void advanceFlowerPhase() {
        if (phase == FlowerPhase.NO_FLOWER) phase = FlowerPhase.FLOWERING;
        else if (phase == FlowerPhase.FLOWERING) phase = FlowerPhase.FRUITING;
        else phase = FlowerPhase.NO_FLOWER;

        setHasFruit(phase == FlowerPhase.FRUITING);
        System.out.println(name + " flower phase -> " + phase);
    }

    @Override
    public boolean hasFruit() { return phase == FlowerPhase.FRUITING; }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (!isAlive()) return;
        timer++;
        if (age >= 3 && timer >= 5) {
            advanceFlowerPhase();
            timer = 0;
        }
    }

    @Override
    public String toString() {
        return super.toString() + " | Shrub | phase: " + phase;
    }
}
