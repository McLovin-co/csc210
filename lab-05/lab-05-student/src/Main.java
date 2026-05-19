import ecosystem.creatures.Bird;
import ecosystem.creatures.Creature;
import ecosystem.creatures.Fish;
import ecosystem.creatures.Mammal;
import ecosystem.plants.Moss;
import ecosystem.plants.Plant;
import ecosystem.plants.Shrub;
import ecosystem.plants.Tree;
import ecosystem.world.Tile;
import ecosystem.world.World;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        String configPath = "world_config.json";
        if (args.length > 0) configPath = args[0];

        String content = new String(Files.readAllBytes(Paths.get(configPath)));
        SimpleJSON.JObject root = SimpleJSON.parseObject(content);

        SimpleJSON.JObject worldCfg = root.getObject("world");
        int width = worldCfg.getInt("width");
        int height = worldCfg.getInt("height");
        World world = new World(width, height);

        SimpleJSON.JArray tiles = root.getArray("tiles");
        for (int i = 0; i < tiles.size(); i++) {
            SimpleJSON.JObject tileCfg = tiles.getObject(i);
            int row = tileCfg.getInt("row");
            int col = tileCfg.getInt("col");
            Tile tile = new Tile(tileCfg.getInt("water"), tileCfg.getInt("temperature"), tileCfg.getInt("nutrients"));

            SimpleJSON.JArray creatureArr = tileCfg.optArray("creatures");
            if (creatureArr != null) {
                for (int j = 0; j < creatureArr.size(); j++) {
                    SimpleJSON.JObject cCfg = creatureArr.getObject(j);
                    String type = cCfg.getString("type");
                    String species = cCfg.getString("species");
                    String name = cCfg.getString("name");

                    Creature c = null;
                    if (type.equals("Tree")) c = new Tree(name, species);
                    else if (type.equals("Shrub")) c = new Shrub(name, species);
                    else if (type.equals("Moss")) c = new Moss(name, species);
                    else if (type.equals("Plant")) c = new Plant(name, species);
                    else if (type.equals("Bird")) c = new Bird(name, species);
                    else if (type.equals("Mammal")) c = new Mammal(name, species);
                    else if (type.equals("Fish")) c = new Fish(name, species);

                    if (c != null) tile.addCreature(c);
                }
            }
            world.setTile(row, col, tile);
        }

        System.out.println("Starting simulation...");
        System.out.println("Creatures at start: " + world.totalCreatures());

        for (int i = 0; i < 100; i++) {
            world.takeTurn();
        }

        System.out.println("\nDone! Creatures after 100 turns: " + world.totalCreatures());
        System.out.println(world);
    }
}
