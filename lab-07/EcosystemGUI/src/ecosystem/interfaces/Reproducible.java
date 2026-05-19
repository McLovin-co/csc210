package ecosystem.interfaces;

import ecosystem.creatures.Creature;

public interface Reproducible {
    Creature reproduce();
    boolean canReproduce();
}
