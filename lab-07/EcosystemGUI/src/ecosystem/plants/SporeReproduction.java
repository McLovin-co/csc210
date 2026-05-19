package ecosystem.plants;

import ecosystem.creatures.Creature;
import ecosystem.interfaces.ReproductionStrategy;

public class SporeReproduction implements ReproductionStrategy {

    public Creature attempt(String parentSpecies, int nutrients, int water) {
        if (nutrients >= 5 && water >= 10) {
            if (Math.random() < 0.25) {
                return new Plant("SporeGrowth-" + (int)(Math.random() * 1000), parentSpecies);
            }
        }
        return null;
    }

    public String getName() { return "spore"; }
}
