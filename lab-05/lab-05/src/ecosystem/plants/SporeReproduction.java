package ecosystem.plants;

import ecosystem.creatures.Creature;
import ecosystem.interfaces.ReproductionStrategy;

/**
 * Spores are hardy — they need very little water or nutrients,
 * making them ideal for mosses and ferns in harsh conditions.
 */
public class SporeReproduction implements ReproductionStrategy {

    private static final int NUTRIENT_THRESHOLD = 5;
    private static final int WATER_THRESHOLD    = 10;

    @Override
    public Creature attempt(String parentSpecies, int nutrients, int water) {
        if (nutrients >= NUTRIENT_THRESHOLD && water >= WATER_THRESHOLD) {
            // 25% chance — spores spread widely but germinate less reliably
            if (Math.random() < 0.25) {
                return new Plant("SporeGrowth-" + (int)(Math.random() * 1000),
                                 parentSpecies);
            }
        }
        return null;
    }

    @Override
    public String getName() { return "spore"; }

    @Override
    public String toString() { return "SporeReproduction"; }
}
