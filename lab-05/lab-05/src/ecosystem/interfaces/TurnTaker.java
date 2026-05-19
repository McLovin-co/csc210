package ecosystem.interfaces;

/**
 * Every participant in the simulation must implement TurnTaker.
 * Each turn, the simulation calls takeTurn() on the World, which
 * cascades down to every Tile and every Creature inside it.
 */
public interface TurnTaker {
    void takeTurn();
}
