package ecosystem.interfaces;

import ecosystem.creatures.Creature;

/**
 * Strategy pattern: encapsulates how a plant reproduces.
 * Plants can hold multiple strategies (e.g. seeds AND cloning).
 * Each strategy decides independently if conditions are right.
 */
public interface ReproductionStrategy {
    /**
     * Attempt reproduction given current tile nutrient and water levels.
     * @param parentSpecies  species string to pass to the new plant
     * @param nutrients      current tile nutrient level
     * @param water          current tile water level
     * @return a new Creature offspring, or null if reproduction fails
     */
    Creature attempt(String parentSpecies, int nutrients, int water);

    /** Human-readable name for this strategy (e.g. "seed", "spore", "clone"). */
    String getName();
}
