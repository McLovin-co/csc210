package ecosystem.interfaces;

/**
 * Implemented by plants that go through flowering and fruiting phases.
 * The plant cycles: NO_FLOWER -> FLOWERING -> FRUITING -> NO_FLOWER
 */
public interface Flowering {
    enum FlowerPhase { NO_FLOWER, FLOWERING, FRUITING }

    FlowerPhase getFlowerPhase();
    void advanceFlowerPhase();
    boolean hasFruit();
}
