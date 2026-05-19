package ecosystem.interfaces;

public interface Flowering {
    enum FlowerPhase { NO_FLOWER, FLOWERING, FRUITING }
    FlowerPhase getFlowerPhase();
    void advanceFlowerPhase();
    boolean hasFruit();
}
