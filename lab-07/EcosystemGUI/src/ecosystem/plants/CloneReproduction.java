package ecosystem.plants;

import ecosystem.creatures.Creature;
import ecosystem.interfaces.ReproductionStrategy;

public class CloneReproduction implements ReproductionStrategy {

    public Creature attempt(String parentSpecies, int nutrients, int water) {
        if (nutrients >= 50) {
            if (Math.random() < 0.60) {
                return new Plant("Clone-" + (int)(Math.random() * 1000), parentSpecies);
            }
        }
        return null;
    }

    public String getName() { return "clone"; }
}
