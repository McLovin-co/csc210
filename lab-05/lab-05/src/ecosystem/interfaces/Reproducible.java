package ecosystem.interfaces;

/**
 * Implemented by any creature that can reproduce.
 * The reproduce() method returns a new Creature instance (the offspring),
 * or null if conditions aren't right for reproduction this turn.
 */
public interface Reproducible {
    ecosystem.creatures.Creature reproduce();
    boolean canReproduce();
}
