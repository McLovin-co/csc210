package ecosystem.plants;

import ecosystem.world.Tile;

public class Moss extends Plant {

    static {
        Plant.addSpecies("PeatMoss", "woody=false,perennial=true,growth=tiny,reproduction=spore+clone");
        Plant.addSpecies("RockMoss", "woody=false,perennial=true,growth=tiny,reproduction=spore");
        Plant.addSpecies("SpagMoss", "woody=false,perennial=true,growth=tiny,reproduction=spore+clone");
    }

    private int coverage;
    private int tileWater;

    public Moss(String name, String species) {
        super(name, species);
        coverage = 1;
        tileWater = 50;
    }

    public Moss(String name, String species, boolean woody, boolean perennial) {
        super(name, species, woody, perennial);
        coverage = 1;
        tileWater = 50;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if (!isAlive()) return;
        if (tileWater > 50 && coverage < 10) {
            coverage++;
        }
    }

    @Override
    public void setCurrentTile(Tile tile) {
        super.setCurrentTile(tile);
        if (tile != null) tileWater = tile.getWater();
    }

    public int getCoverage() { return coverage; }

    @Override
    public String toString() {
        return super.toString() + " | Moss | coverage: " + coverage + "/10";
    }
}
