package ecosystem.plants;

import ecosystem.creatures.Creature;
import ecosystem.interfaces.ReproductionStrategy;

/**
 * Seeds need decent nutrients AND water to germinate.
 * Lower threshold than spores; higher than cloning.
 */
public class SeedReproduction implements ReproductionStrategy {

    private static final int NUTRIENT_THRESHOLD = 30;
    private static final int WATER_THRESHOLD    = 25;

    @Override
    public Creature attempt(String parentSpecies, int nutrients, int water) {
        if (nutrients >= NUTRIENT_THRESHOLD && water >= WATER_THRESHOLD) {
            // 40% chance per turn when conditions are met
            if (Math.random() < 0.40) {
                return new Plant("Seedling-" + (int)(Math.random() * 1000),
                                 parentSpecies);
            }
        }
        return null;
    }

    @Override
    public String getName() { return "seed"; }

    @Override
    public String toString() { return "SeedReproduction"; }
}
