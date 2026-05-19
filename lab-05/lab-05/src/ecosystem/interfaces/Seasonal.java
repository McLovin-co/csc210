package ecosystem.interfaces;

/**
 * Implemented by plants that change behavior based on the season.
 * Seasonal plants may lose leaves, stop growing, or go dormant.
 */
public interface Seasonal {
    void onSeasonChange(String season);
    boolean isDormant();
}
