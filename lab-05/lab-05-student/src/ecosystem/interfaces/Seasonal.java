package ecosystem.interfaces;

public interface Seasonal {
    void onSeasonChange(String season);
    boolean isDormant();
}
