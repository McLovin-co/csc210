package ecosystem.plants;

import ecosystem.creatures.Creature;
import ecosystem.interfaces.ReproductionStrategy;

/**
 * Cloning (vegetative reproduction) requires high nutrients
 * but no particular water level — the plant copies itself directly.
 * Very reliable when conditions are right.
 */
public class CloneReproduction implements ReproductionStrategy {

    private static final int NUTRIENT_THRESHOLD = 50;

    @Override
    public Creature attempt(String parentSpecies, int nutrients, int water) {
        if (nutrients >= NUTRIENT_THRESHOLD) {
            // 60% chance — cloning is the most reliable when it's possible
            if (Math.random() < 0.60) {
                return new Plant("Clone-" + (int)(Math.random() * 1000),
                                 parentSpecies);
            }
        }
        return null;
    }

    @Override
    public String getName() { return "clone"; }

    @Override
    public String toString() { return "CloneReproduction"; }
}
