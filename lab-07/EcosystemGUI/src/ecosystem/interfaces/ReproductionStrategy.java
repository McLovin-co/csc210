package ecosystem.interfaces;

import ecosystem.creatures.Creature;

public interface ReproductionStrategy {
    Creature attempt(String species, int nutrients, int water);
    String getName();
}
