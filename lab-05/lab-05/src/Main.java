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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Main — entry point for the ecosystem simulation.
 *
 * Reads world_config.json to set initial tile conditions and
 * creature placements, then runs the World's takeTurn() 100 times.
 */
public class Main {

    private static final int TURN_COUNT = 100;

    public static void main(String[] args) throws Exception {
        String configPath = args.length > 0 ? args[0] : "world_config.json";

        System.out.println("Loading world from: " + configPath);
        World world = loadWorldFromJson(configPath);

        System.out.println("Starting simulation: " + TURN_COUNT + " turns.");
        System.out.println("Initial creature count: " + world.totalCreatures());
        System.out.println("=".repeat(60));

        for (int i = 0; i < TURN_COUNT; i++) {
            world.takeTurn();
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Simulation complete after " + TURN_COUNT + " turns.");
        System.out.println("Surviving creatures: " + world.totalCreatures());
        System.out.println("\nFinal world state:\n" + world);
    }

    private static World loadWorldFromJson(String path) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(path)));
        SimpleJSON.JObject root = SimpleJSON.parseObject(content);

        SimpleJSON.JObject worldCfg = root.getObject("world");
        int width  = worldCfg.getInt("width");
        int height = worldCfg.getInt("height");
        World world = new World(width, height);

        SimpleJSON.JArray tiles = root.getArray("tiles");
        for (int i = 0; i < tiles.size(); i++) {
            SimpleJSON.JObject tileCfg = tiles.getObject(i);
            int row   = tileCfg.getInt("row");
            int col   = tileCfg.getInt("col");
            int water = tileCfg.getInt("water");
            int temp  = tileCfg.getInt("temperature");
            int nutr  = tileCfg.getInt("nutrients");

            Tile tile = new Tile(water, temp, nutr);

            SimpleJSON.JArray creatureArr = tileCfg.optArray("creatures");
            if (creatureArr != null) {
                for (int j = 0; j < creatureArr.size(); j++) {
                    SimpleJSON.JObject cCfg = creatureArr.getObject(j);
                    Creature c = buildCreature(cCfg);
                    if (c != null) tile.addCreature(c);
                }
            }

            world.setTile(row, col, tile);
        }

        return world;
    }

    private static Creature buildCreature(SimpleJSON.JObject cfg) {
        String type    = cfg.getString("type");
        String species = cfg.getString("species");
        String name    = cfg.getString("name");

        return switch (type) {
            case "Tree"   -> new Tree(name, species);
            case "Shrub"  -> new Shrub(name, species);
            case "Moss"   -> new Moss(name, species);
            case "Plant"  -> new Plant(name, species);
            case "Bird"   -> new Bird(name, species);
            case "Mammal" -> new Mammal(name, species);
            case "Fish"   -> new Fish(name, species);
            default -> {
                System.err.println("Unknown creature type: " + type);
                yield null;
            }
        };
    }
}
