package ecosystem.plants;

import ecosystem.creatures.Creature;
import ecosystem.interfaces.ReproductionStrategy;

public class SeedReproduction implements ReproductionStrategy {

    public Creature attempt(String parentSpecies, int nutrients, int water) {
        if (nutrients >= 30 && water >= 25) {
            if (Math.random() < 0.40) {
                return new Plant("Seedling-" + (int)(Math.random() * 1000), parentSpecies);
            }
        }
        return null;
    }

    public String getName() { return "seed"; }
}
